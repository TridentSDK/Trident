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

import net.tridentsdk.api.nbt.*;

import java.io.*;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

/**
 * Represents a Region File (in region/ directory) in memory
 */
public class RegionFile {

    private final RandomAccessFile file;
    private final int[] locations = new int[1024];
    private final int[] timestamps = new int[1024];
    private final CompoundTag nbtTag;

    public RegionFile(File path) throws IOException, DataFormatException, NBTException {
        this.file = new RandomAccessFile(path, "rw");

        // Packing to default size of 8192 if it isn't already that size
        // (this should really never happen, but I'll take my changes)
        if (this.file.length() < 8192L) {
            this.file.seek(this.file.length());

            long diff = 8192L - this.file.length();
            for (long l = 0L; l < diff; l++) {
                this.file.write(0);
            }
        }

        // Packing if length is not a multiple of 4096

        if ((this.file.length() & 4095L) != 0) {
            long diff = this.file.length() - (this.file.length() & 4095L);

            for (long l = 0L; l < diff; l++) {
                this.file.write(0);
            }
        }
        // Jump to beginning
        this.file.seek(0L);

        // Read Locations
        for (int i = 0; i < 1024; i++) {
            this.locations[i] = this.file.readInt();
        }

        // Read Timestamps
        for (int i = 0; i < 1024; i++) {
            this.timestamps[i] = this.file.readInt();
        }

        // Read the length, and the compression type
        int length = this.file.readInt();
        short compression = (short) this.file.readByte();
        byte[] compressedData = new byte[length - 1];

        // Read the compressed data
        this.file.readFully(compressedData);

        // Decompress the data using rather the GZIP or Zlib
        byte[] chunkData;
        switch (compression) {
            case 1:
                GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(compressedData));
                chunkData = new byte[in.available()];

                this.file.readFully(chunkData);
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
        this.nbtTag = new NBTDecoder(new DataInputStream(new ByteArrayInputStream(chunkData))).decode();

        // Read and store all NBT data
        IntTag chunkX = (IntTag) this.nbtTag.getTag("xPos");
        IntTag chunkZ = (IntTag) this.nbtTag.getTag("zPos");
        LongTag lastUpdate = (LongTag) this.nbtTag.getTag("LastUpdate");

        ByteTag lightPopulated = (ByteTag) this.nbtTag.getTag("LightPopulated"); // unknown usage
        ByteTag terrainPopulated = (ByteTag) this.nbtTag.getTag("TerrainPopulated");

        LongTag inhabitedTag = (LongTag) this.nbtTag.getTag("InhabitedTime");
        ByteArrayTag biomes = (ByteArrayTag) this.nbtTag.getTag("Biomes");
        IntArrayTag heightMap = (IntArrayTag) this.nbtTag.getTag("HeightMap");

        ListTag sections = (ListTag) this.nbtTag.getTag("Sections");
        ListTag entities = (ListTag) this.nbtTag.getTag("Entities");
        ListTag tileEntities = (ListTag) this.nbtTag.getTag("TileEntities");
        ListTag tileTicks = (ListTag) this.nbtTag.getTag("TileTicks");
    }
}
