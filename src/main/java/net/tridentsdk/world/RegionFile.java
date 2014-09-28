/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.tridentsdk.world;

import com.google.common.math.IntMath;
import net.tridentsdk.api.nbt.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

/**
 * Represents a Region File (in region/ directory) in memory
 */
public class RegionFile {
    private final static int SECTOR_LENGTH = 4096;

    //The path to the region file
    private final Path path;
    //A cache of the locationOffsets
    private final int[] offsets = new int[1024];

    public RegionFile(Path path) throws IOException, DataFormatException, NBTException {
        this.path = path;

        RandomAccessFile access;
        //Checks whether or not the file exists
        if (!Files.isRegularFile(path)) {
            //Delete if it is mistakenly a directory
            Files.deleteIfExists(path);
            //Creates a new empty file
            Files.createFile(path);
            access = new RandomAccessFile(path.toFile(), "rw");
            createNew(access);
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

        packFile(access);

        //Jump to beginning of file
        access.seek(0L);

        //Cache the offsets of each chunk
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] = access.readInt();
        }

        access.close();

    }

    /**
     * Packs the file with empty bytes in order to fit the specifications The idea behind the packing is for speed (file
     * systems work better with 4KiB chunks apparently)
     *
     * @throws IOException
     */
    private void packFile(RandomAccessFile access) throws IOException {
        // Packing if length is not a multiple of 4096
        if ((access.length() & 4095L) != 0) {
            long diff = access.length() - (access.length() & 4095L);

            for (long l = 0L; l < diff; l++) {
                access.write(0);
            }
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
     *
     * @param chunk
     * @throws NBTException
     * @throws IOException
     * @throws DataFormatException
     */
    public void loadChunkData(TridentChunk chunk) throws NBTException, IOException, DataFormatException {
        RandomAccessFile access = new RandomAccessFile(path.toFile(), "rw");

        //The location of the offset in the cache of offsets
        int offsetLoc = (IntMath.mod(chunk.getX(), 32) + IntMath.mod(chunk.getX(), 32) * 32);

        // Read the offset of the chunk data in the file
        int offset = offsets[offsetLoc];
        // Measured in sectors
        int offsetSectors = offset >> 8;
        int lengthSectors = offset & 0xFF;

        //Jump to timestamp location
        access.seek(4 * offsetLoc + SECTOR_LENGTH);

        // Read Timestamp
        int lastUpdate = access.readInt();


        // Check to see whether the chunk needs the data loaded
        // Not sure why it would ever not need
        if (chunk.getLastFileAccess() > lastUpdate) {
            chunk.setLastFileAccess((int) (System.currentTimeMillis() / 1000));
            access.close();
            return;
        } else {
            chunk.setLastFileAccess((int) (System.currentTimeMillis() / 1000));
        }

        //Jump to location of actual chunk data
        access.seek(offsetSectors * SECTOR_LENGTH);

        // Read the length, and the compression type
        int length = access.readInt();
        short compression = (short) access.readByte();
        byte[] compressedData = new byte[length - 1];

        // Read the compressed data
        access.readFully(compressedData);

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
        chunk.setData(nbtData);

        //Close the stream --> stop any leaks
        access.close();
    }
}