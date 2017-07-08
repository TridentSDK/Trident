/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.world;

import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Represents a region file stored on file which maps out
 * the data in a chunk to be saved.
 *
 * We didn't write this file.
 * A few fields were removed to reduce memory footprint of
 * having this class.
 */
public class Region {
    private static final Map<Path, Region> CACHE = new ConcurrentHashMap<>();

    private static final int VERSION_GZIP = 1;
    private static final int VERSION_DEFLATE = 2;

    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = SECTOR_BYTES / 4;

    private static final int CHUNK_HEADER_SIZE = 5;
    private static final byte[] emptySector = new byte[4096];

    @Getter
    private final int regionX;
    @Getter
    private final int regionZ;

    private final Path path;
    private final RandomAccessFile file;
    private final int[] offsets;
    private final ArrayList<Boolean> sectorFree;

    private Region(Path path) {
        this.path = path;
        this.offsets = new int[SECTOR_INTS];

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            this.file = new RandomAccessFile(path.toFile(), "rw");

            if (this.file.length() < SECTOR_BYTES) {
                /* we need to write the chunk offset table */
                for (int i = 0; i < SECTOR_INTS; i++) {
                    this.file.writeInt(0);
                }
                // write another sector for the timestamp info
                for (int i = 0; i < SECTOR_INTS; i++) {
                    this.file.writeInt(0);
                }
            }

            if ((this.file.length() & 0xfff) != 0) {
                /* the file size is not a multiple of 4KB, grow it */
                for (int i = 0; i < (this.file.length() & 0xfff); ++i) {
                    this.file.write((byte) 0);
                }
            }

            /* set up the available sector map */
            int nSectors = (int) this.file.length() / SECTOR_BYTES;
            this.sectorFree = new ArrayList<>(nSectors);

            for (int i = 0; i < nSectors; i++) {
                this.sectorFree.add(true);
            }

            this.sectorFree.set(0, false); // chunk offset table
            this.sectorFree.set(1, false); // for the last modified info

            this.file.seek(0);
            for (int i = 0; i < SECTOR_INTS; i++) {
                int offset = this.file.readInt();
                this.offsets[i] = offset;
                if (offset != 0 && (offset >> 8) + (offset & 0xFF) <= this.sectorFree.size()) {
                    for (int sectorNum = 0; sectorNum < (offset & 0xFF); ++sectorNum) {
                        this.sectorFree.set((offset >> 8) + sectorNum, false);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] split = path.getFileName().toString().split(Pattern.quote("."));
        this.regionX = Integer.parseInt(split[1]);
        this.regionZ = Integer.parseInt(split[2]);
    }

    /**
     * Obtains the chunk file for the given chunk, creating
     * if it doesn't exist and if specified.
     *
     * @param chunk the chunk to obtain the region file
     * @param create {@code true} to create if it doesn't
     * exist
     * @return the region file, or {@code null} if it
     * doesn't exist and don't create it
     */
    public static Region getFile(TridentChunk chunk, boolean create) {
        Path path = chunk.getWorld().getDirectory().resolve("region").
                resolve("r." + (chunk.getX() >> 5) + '.' + (chunk.getZ() >> 5) + ".mca");
        if (!Files.exists(path) && !create) {
            return null;
        }

        return CACHE.computeIfAbsent(path, Region::new);
    }

    /*
     * gets an (uncompressed) stream representing the chunk data returns null if
     * the chunk is not found or an error occurs
     */
    public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
        if (this.outOfBounds(x, z)) {
            return null;
        }

        try {
            int offset = this.getOffset(x, z);
            if (offset == 0) {
                return null;
            }

            int sectorNumber = offset >> 8;
            int numSectors = offset & 0xFF;

            if (sectorNumber + numSectors > this.sectorFree.size()) {
                return null;
            }

            this.file.seek(sectorNumber * SECTOR_BYTES);
            int length = this.file.readInt();

            if (length > SECTOR_BYTES * numSectors) {
                return null;
            }

            byte version = this.file.readByte();
            if (version == VERSION_GZIP) {
                byte[] data = new byte[length - 1];
                this.file.read(data);
                return new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data)));
            } else if (version == VERSION_DEFLATE) {
                byte[] data = new byte[length - 1];
                this.file.read(data);
                return new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data)));
            }

            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DataOutputStream getChunkDataOutputStream(int x, int z) {
        if (this.outOfBounds(x, z)) {
            return null;
        }

        return new DataOutputStream(new DeflaterOutputStream(new ChunkBuffer(x, z)));
    }

    /*
     * lets chunk writing be multithreaded by not locking the whole file as a
     * chunk is serializing -- only writes when serialization is over
     */
    class ChunkBuffer extends ByteArrayOutputStream {
        private final int x;
        private final int z;

        public ChunkBuffer(int x, int z) {
            super(8096); // initialize to 8KB
            this.x = x;
            this.z = z;
        }

        @Override
        public void close() {
            Region.this.write(this.x, this.z, this.buf, this.count);
        }
    }

    /* write a chunk at (x,z) with length bytes of data to disk */
    public synchronized void write(int x, int z, byte[] data, int length) {
        try {
            int offset = this.getOffset(x, z);
            int sectorNumber = offset >> 8;
            int sectorsAllocated = offset & 0xFF;
            int sectorsNeeded = (length + CHUNK_HEADER_SIZE) / SECTOR_BYTES + 1;

            // maximum chunk size is 1MB
            if (sectorsNeeded >= 256) {
                return;
            }

            if (sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
                /* we can simply overwrite the old sectors */
                this.write(sectorNumber, data, length);
            } else {
                /* we need to allocate new sectors */

                /* mark the sectors previously used for this chunk as free */
                for (int i = 0; i < sectorsAllocated; ++i) {
                    this.sectorFree.set(sectorNumber + i, true);
                }

                /* scan for a free space large enough to store this chunk */
                int runStart = this.sectorFree.indexOf(true);
                int runLength = 0;
                if (runStart != -1) {
                    for (int i = runStart; i < this.sectorFree.size(); ++i) {
                        if (runLength != 0) {
                            if (this.sectorFree.get(i)) runLength++;
                            else runLength = 0;
                        } else if (this.sectorFree.get(i)) {
                            runStart = i;
                            runLength = 1;
                        }
                        if (runLength >= sectorsNeeded) {
                            break;
                        }
                    }
                }

                if (runLength >= sectorsNeeded) {
                    /* we found a free space large enough */
                    sectorNumber = runStart;
                    this.setOffset(x, z, sectorNumber << 8 | sectorsNeeded);
                    for (int i = 0; i < sectorsNeeded; ++i) {
                        this.sectorFree.set(sectorNumber + i, false);
                    }
                    this.write(sectorNumber, data, length);
                } else {
                    /*
                     * no free space large enough found -- we need to grow the
                     * file
                     */
                    this.file.seek(this.file.length());
                    sectorNumber = this.sectorFree.size();
                    for (int i = 0; i < sectorsNeeded; ++i) {
                        this.file.write(emptySector);
                        this.sectorFree.add(false);
                    }

                    this.write(sectorNumber, data, length);
                    this.setOffset(x, z, sectorNumber << 8 | sectorsNeeded);
                }
            }
            this.setTimestamp(x, z, (int) (System.currentTimeMillis() / 1000L));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* write a chunk data to the region file at specified sector number */
    private void write(int sectorNumber, byte[] data, int length) throws IOException {
        this.file.seek(sectorNumber << 12);
        this.file.writeInt(length + 1); // chunk length
        this.file.writeByte(VERSION_DEFLATE); // chunk version number
        this.file.write(data, 0, length); // chunk data
    }

    /* is this an invalid chunk coordinate? */
    private boolean outOfBounds(int x, int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }

    private int getOffset(int x, int z) {
        return this.offsets[x + (z << 5)];
    }

    public boolean hasChunk(int x, int z) {
        return this.getOffset(x, z) != 0;
    }

    private void setOffset(int x, int z, int offset) throws IOException {
        this.offsets[x + (z << 5)] = offset;
        this.file.seek(x + (z << 5) << 2);
        this.file.writeInt(offset);
    }

    private void setTimestamp(int x, int z, int value) throws IOException {
        this.file.seek(SECTOR_BYTES + (x + (z << 5) << 2));
        this.file.writeInt(value);
    }

    public void close() throws IOException {
        CACHE.remove(this.path);
        this.file.close();
    }
}