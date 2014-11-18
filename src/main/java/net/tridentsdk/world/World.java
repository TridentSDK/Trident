/*
 *     TridentSDK - A Minecraft Server API
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

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Difficulty;
import net.tridentsdk.api.GameMode;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.world.*;

public interface World {
    
    /**
     * Gets the name of the world
     * 
     * @return the name of the world
     */
    String getName();
    
    /**
     * Gets the chunk on the given location, and generates the chunk if it does not exist.
     * 
     * @return The chunk on the given location
     */
    net.tridentsdk.api.world.Chunk getChunkAt(ChunkLocation loc, boolean generateIfNotFound);
    
    /**
     * Gets the chunk on the given x and z , and generates the chunk if it does not exist
     * 
     * @return The chunk on the given location
     */
    net.tridentsdk.api.world.Chunk getChunkAt(int x, int z, boolean generateIfNotFound);
    
    /**
     * Generates the chunk on the given location
     * 
     * @return The generated chunk
     */
    net.tridentsdk.api.world.Chunk generateChunk(int x, int z);
    
    /**
     * Generates the chunk on the given location
     * 
     * @return The generated chunk
     */
    net.tridentsdk.api.world.Chunk generateChunk(ChunkLocation location);
    
    /**
     * Gets the block on the given location
     * 
     * @return The block on the given location
     */
    Block getBlockAt(Location location);
    
    /**
     * Gets the ChunkSnapshot
     * 
     * @return The ChunkSnapshot
     */
    ChunkSnapshot getChunkSnapshot();
    
    /**
     * Gets the dimension of a world
     * 
     * @return The dimension of a world
     */
    Dimension getDimension();
    
    /**
     * Gets the difficulty set in a world
     * 
     * @return The difficulty set in a world
     */
    Difficulty getDifficulty();
    
    /**
     * Gets the default gamemode in a given chunk
     * 
     * @return The default gamemode in a given chunk
     */
    GameMode getDefaultGamemode();
    
    /**
     * Gets the type of a world
     * 
     * @return The type of a world
     */
    LevelType getLevelType();

    /**
     * Gets the spawn location of a world
     *
     * @return The spawn location of a world
     */
    Location getSpawnLocation();
    
    /**
     * Gets the set boolean for the given gamerule
     * 
     * @return The set boolean for the given gamerule
     */
    boolean getGamerule(String rule);
    
    /**
     * Gets the time in a world
     * 
     * @return The time in a world
     */
    long getTime();
    
    /**
     * Gets the spawn location of a world
     * 
     * @return The spawn location in a world
     */
    Location getSpawn();
    
    /**
     * Checks if it is raining in a world
     * 
     * @return True if it is raining in a world
     */
    boolean isRaining();
    
    /**
     * Gets the number of ticks before raining is toggled
     * 
     * @return The number of ticks before raining is toggled
     */
    int getRainTime();
    
    /**
     * Checks if it is thundering in a world
     * 
     * @return True if it is thundering in a world
     */
    boolean isThundering();
    
    /**
     * Gets the number of ticks before thundering is toggled
     * 
     * @return The number of ticks before thundering is toggled
     */
    int getThunderTime();
    
    /**
     * Checks if structures are generated in a world (Stronghold, villages, dungeons)
     * 
     * @return True if structures are generated in a world (Stronghold, villages, dungeons)
     */
    boolean canGenerateStructures();
    
    /**
     * Gets the size of the worldborder
     * 
     * @return The size of the worldborder
     */
    int getBorderSize();
    
    /**
     * Gets the location where the worldborder is centered
     * 
     * @return The location where the worldborder is centered
     */
    Location getBorderCenter();
    
    /**
     * Gets to what size a border is contracting, 60000000 by default
     * 
     * @return To what size a border is contracting, 60000000 by default
     */
    int getBorderSizeContraction();
    
    /**
     * Gets the time the border has to contract to the contraction target
     * 
     * @return The time the border has to contract to the contraction target
     */
    int getBorderSizeContractionTime();
}
