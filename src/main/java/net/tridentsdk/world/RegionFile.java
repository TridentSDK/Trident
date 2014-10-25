/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.world;

import com.google.common.math.IntMath;
import net.tridentsdk.api.nbt.*;
import net.tridentsdk.api.world.Chunk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

/**
 * Represents a Region File (in region/ directory) in memory
 */

// TODO stop using locks on native methods
public class RegionFile {
    //The path to the region file
    private final Path path;
    //The class in charge of sector allocation
    private final SectorStorage sectors;
    //The object to lock on to stop reading/writing simultaneously
    private final Object readWriteLock = new Object();

    public RegionFile(Path path)
            throws IOException {
        this.path = path;

        synchronized (this.readWriteLock) {
            RandomAccessFile access;
            //Checks whether or not the file exists
            if (!Files.isRegularFile(path)) {
                //Delete if it is mistakenly a directory
                Files.deleteIfExists(path);
                //Creates a new empty file
                Files.createFile(path);
                access = new RandomAccessFile(path.toFile(), "rw");
                this.createNew(access);
            } else {
                access = new RandomAccessFile(path.toFile(), "rw");
            }

            // Packing to default size of 8192 if it isn't already that size
            // (this should really never happen, but I'll take my changes)
            if (access.length() < 8192L) {
                access.seek(access.length());

                long diff = 8192L - access.length();
                for (long l = 0L; l < diff; l++) {
                    access.write(0);
                }
            }

            this.packFile(access);

            //Jump to beginning of file
            access.seek(0L);

            //Get the offsets of each chunk and cache
            int[] offsets = new int[1024];
            for (int i = 0; i < offsets.length; i++) {
                offsets[i] = access.readInt();
            }
            this.sectors = new SectorStorage(offsets);

            access.close();
        }
    }

    /**
     * Packs the file with empty bytes in order to fit the specifications The idea behind the packing is for speed (file
     * systems work better with 4KiB chunks apparently)
     */
    private void packFile(RandomAccessFile access) throws IOException {
        // Packing if length is not a multiple of 4096
        if (access.length() % 4096L != 0) {
            long paddingNeeded = access.length() % 4096L;

            //Jump to the end of the file
            access.seek(access.length());
            byte[] padding = new byte[(int) paddingNeeded];
            access.write(padding);
        }

        access.close();
    }

    private void createNew(RandomAccessFile access) {
        /*TODO: Generate a new Region File
         * - Start off with just blank files
         * - Move on to world generation
         */

    }

    /**
     * Pass in a chunk to load its data from file
     */
    public TridentChunk loadChunkData(TridentWorld owner) throws NBTException, IOException, DataFormatException {
        TridentChunk chunk = new TridentChunk(owner, null);
        short compression;
        byte[] compressedData;

        synchronized (this.readWriteLock) {
            RandomAccessFile access = new RandomAccessFile(this.path.toFile(), "rw");

            //Jump to timestamp location
            access.seek((long) this.sectors.getTimeStampLocation(chunk));

            // Read Timestamp
            int lastUpdate = access.readInt();

            // Check to see whether the chunk needs the data loaded
            // Not sure why it would ever not need
            if (chunk.getLastFileAccess() > lastUpdate) {
                chunk.setLastFileAccess((int) (System.currentTimeMillis() / 1000L));

                access.close();
                return chunk;
            } else {
                chunk.setLastFileAccess((int) (System.currentTimeMillis() / 1000L));
            }

            //Jump to location of actual chunk data
            access.seek((long) this.sectors.getDataLocation(chunk));

            // Read the length, and the compression type
            int length = access.readInt();
            compression = (short) access.readByte();
            compressedData = new byte[length - 1];

            // Read the compressed data
            access.readFully(compressedData);

            //Close the stream as fast as possible: allows other chunks to read as soon as possible
            access.close();
        }

        // Decompress the data using rather the GZIP or Zlib
        byte[] chunkData;

        switch (compression) {
            case 0:

            case 1:
                GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(compressedData));
                chunkData = new byte[in.available()];
                in.read(chunkData);
                in.close();
                break;

            case 2:
                Inflater inflater = new Inflater();

                inflater.setInput(compressedData);
                chunkData = new byte[inflater.getRemaining()];

                inflater.inflate(chunkData);
                inflater.end();
                break;

            default:
                throw new IllegalStateException("Compression type provided is invalid!");
        }

        // Get the NBT tag
        CompoundTag nbtData = new NBTDecoder(new DataInputStream(new ByteArrayInputStream(chunkData))).decode();
        chunk.load(nbtData);

        return chunk;
    }

    /**
     * Pass in a chunk to save its data to the file
     */
    public void saveChunkData(TridentChunk chunk) throws IOException, NBTException {
        /* Gets the ChunkData in a byte array form */
        ByteArrayOutputStream nbtStream = new ByteArrayOutputStream();
        new NBTEncoder(new DataOutputStream(new ByteArrayOutputStream())).encode(chunk.toNbt());
        byte[] uncompressed = nbtStream.toByteArray();
        
        /* Gonna only use Zlib compression by default */
        Deflater deflater = new Deflater();
        deflater.setInput(uncompressed);
        byte[] compressed = new byte[(int) deflater.getBytesRead()];
        deflater.deflate(compressed);
        
        /* Compare and sector lengths*/
        //The extra byte is for compression type (always saved as 1 for now)
        int actualLength = compressed.length + 1;
        //Sector length is rounded up to the nearest sector
        int sectorLength = IntMath.divide(actualLength, SectorStorage.SECTOR_LENGTH, RoundingMode.CEILING);
        //Checks if offsets need to change
        int oldSectorLength = this.sectors.getDataSectors(chunk);

        //If the length is smaller, we can free up a sector
        if (sectorLength < oldSectorLength) {
            this.sectors.setDataSectors(chunk, sectorLength);
            //Clears up all the now-free sectors
            this.sectors.freeSectors(this.sectors.getSectorOffset(chunk) + sectorLength - 1,
                    oldSectorLength - sectorLength);
        }
        //If the length is bigger, we need to find a new location!
        else if (sectorLength > oldSectorLength) {
            this.sectors.setDataSectors(chunk, sectorLength);

            //Clears up all the space previously used by this chunk (we need to find a new space!
            this.sectors.freeSectors(this.sectors.getSectorOffset(chunk), oldSectorLength);

            //Finds a new free location
            this.sectors.setSectorOffset(chunk, this.sectors.findFreeSectors(sectorLength));
        }

        //Update what sectors are being used
        this.sectors.addSectors(this.sectors.getSectorOffset(chunk), this.sectors.getDataSectors(chunk));

        synchronized (this.readWriteLock) {
            /* Write the actual chunk data */
            //Initialize access to the file
            RandomAccessFile access = new RandomAccessFile(this.path.toFile(), "rw");
            access.seek((long) this.sectors.getDataLocation(chunk));
            access.write(actualLength);

            //We only use compression type 1 (zlib)
            access.write((int) (byte) 1);
            access.write(compressed);

            //Now we pad to the end of the sector... just in-case
            int paddingNeeded = actualLength % SectorStorage.SECTOR_LENGTH;

            if (paddingNeeded != 0) {
                byte[] padding = new byte[paddingNeeded];
                access.write(padding);
            }

            //Write the new offset data to the header
            access.seek((long) (4 * this.sectors.getOffsetLoc(chunk)));
            access.write(this.sectors.getRawOffset(chunk));

            //Pack the file as in the specifications
            this.packFile(access);

            //Finished writing the chunk
            access.close();
        }
    }

    /**
     * Class that manages the free space/sectors of the file
     */
    private static class SectorStorage {
        //The length of a Sector in bytes
        private static final int SECTOR_LENGTH = 4096;

        //The mapping of which sectors are free/not free (true is occupied)
        private final BitSet sectorMapping;
        //A cache of the locationOffsets
        private final int[] offsets;

        private SectorStorage(int... offsets) {
            this.offsets = offsets;

            //Random starting capacity for now
            this.sectorMapping = new BitSet(1024);
            //The first two sectors are reserved for the header
            this.sectorMapping.set(0);
            this.sectorMapping.set(1);
            //Set what sectors are initially taken up

            for (int offset : offsets) {
                int loc = offset >> 8;
                int length = offset & 0xFF;

                for (int j = loc; j < loc + length; j++) {
                    this.sectorMapping.set(j);
                }
            }
        }

        /**
         * Finds a section of the file with enough free-space to accomodate 'length' sectors of data
         *
         * @param length the amount of sectors of data
         * @return offset the location of the free space (in sectors)
         */
        int findFreeSectors(int length) {
            //Start searching after the header
            int counter = 2;
            int consecutive = 0;

            while (true) {
                if (!this.sectorMapping.get(counter)) {
                    consecutive++;

                    if (consecutive >= length) {
                        break;
                    }
                } else {
                    consecutive = 0;
                }
            }

            return counter;
        }

        /**
         * Free up a certain length of sectors, from a starting point
         *
         * @param start  the sector to start from (inclusive)
         * @param length the amount of sectors to free up
         */
        void freeSectors(int start, int length) {
            for (int i = start; i < start + length; i++) {
                this.sectorMapping.set(i, false);
            }
        }

        /**
         * Occupies up a certain length of sectors, from a starting point
         *
         * @param start  the sector to start from (inclusive)
         * @param length the amount of sectors to occupy
         */
        void addSectors(int start, int length) {
            for (int i = start; i < start + length; i++) {
                this.sectorMapping.set(i, true);
            }
        }

        /**
         * Gets the location of a chunk's timestamp
         *
         * @param c chunk
         * @return location in bytes
         */
        int getTimeStampLocation(Chunk c) {
            return 4 * this.getOffsetLoc(c) + SECTOR_LENGTH;
        }

        /**
         * Gets the location of a chunk's data
         *
         * @param c chunk
         * @return location in bytes
         */
        int getDataLocation(Chunk c) {
            return this.getSectorOffset(c) * SECTOR_LENGTH;
        }

        /**
         * Gets the amount of sectors a chunk occupies in the file
         *
         * @param c chunk
         * @return amount the amount of sectors
         */
        int getDataSectors(Chunk c) {
            return this.offsets[this.getOffsetLoc(c)] & 0xFF;
        }

        /**
         * Sets the amount of sectors a chunk occupies
         *
         * @param c     chunk
         * @param toSet the amount to set
         */
        void setDataSectors(Chunk c, int toSet) {
            int old = this.offsets[this.getOffsetLoc(c)];
            this.offsets[this.getOffsetLoc(c)] = old & 0xFFFFFF00 | toSet;
        }

        /**
         * Gets the offset of the chunk data in Sectors
         *
         * @param c chunk
         * @return amount the amount of sectors
         */
        int getSectorOffset(Chunk c) {
            return this.offsets[this.getOffsetLoc(c)] >> 8;
        }

        /**
         * Sets the offset of the chunk data in Sectors
         *
         * @param c     chunk
         * @param toSet the amount to set
         */
        void setSectorOffset(Chunk c, int toSet) {
            int old = this.offsets[this.getOffsetLoc(c)];
            this.offsets[this.getOffsetLoc(c)] = old & 0x000000FF | toSet << 8;
        }

        /**
         * Gets the raw header/offset of the chunk
         *
         * @param c chunk
         * @return offset
         */
        private int getRawOffset(Chunk c) {
            return this.offsets[this.getOffsetLoc(c)];
        }

        /**
         * Gets the location of the raw offset of the chunk
         *
         * @param c chunk
         * @return offsetLoc in bytes
         */
        private int getOffsetLoc(Chunk c) {
            return IntMath.mod(c.getX(), 32) + IntMath.mod(c.getX(), 32) * 32;
        }
    }
}
