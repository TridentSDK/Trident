package net.tridentsdk.server.world.gen;

import net.tridentsdk.factory.GenFactory;
import net.tridentsdk.server.world.ChunkSection;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.WorldUtils;
import net.tridentsdk.util.NibbleArray;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.gen.ChunkTile;

/**
 * Implementation of generation factory, used to produce effects of world generation implementation side
 *
 * @author The TridentSDK Team
 */
public class TridentGenFactory implements GenFactory {
    @Override
    public void putBlock(ChunkTile tile, Chunk chunk) {
        int tileX = (int) tile.coordinates().x();
        int tileY = (int) tile.coordinates().y();
        int tileZ = (int) tile.coordinates().z();

        int index = WorldUtils.blockArrayIndex(tileX % 16, tileY, tileZ % 16);
        int sectionId = WorldUtils.section(tileY);

        TridentChunk tChunk = (TridentChunk) chunk;
        if (tChunk.sections == null) tChunk.sections = new ChunkSection[16];

        for (int i = 0; i < tChunk.sections.length; i++) {
            ChunkSection sect = null;
            if (tChunk.sections[i] == null)
                sect = tChunk.sections[i] = new ChunkSection();

            // Already set
            if (sect == null) continue;
            sect.rawTypes = new byte[4096];
            sect.types = new char[4096];
            sect.blockLight = new byte[2048];
            sect.skyLight = new byte[2048];

            sect.add = new byte[2048];
            sect.data = new byte[2048];
        }

        ChunkSection section = tChunk.sections[sectionId];

        byte b = section.rawTypes[index] = (byte) tile.substance().id();
        NibbleArray.set(section.add, index, (byte) 0);
        NibbleArray.set(section.data, index, tile.meta());

        section.types[index] = (char) (((b & 0xff) << 4) | tile.meta());
        section.blockLight[index] = 16;
        section.skyLight[index] = 16;
    }
}
