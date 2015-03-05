/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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
package net.tridentsdk.server.world.gen;

import java.util.Random;

/**
 * 
 */
public class SimplexOctaveGenerator {
    private final SimplexNoiseGenerator[] generators;
    private final double[] amplitudes;
    private final double[] frequencies;
    
    /**
     * Creates a new SimplexOctave generator with parameters
     * 
     * <p>Given the same seed, output will be consistent from one runtime to another.</p>
     *  
     * @param octaves number of passes of the generator to use, each one at double the resolution
     * @param persistence the diminishing value of each successive octave, must be in the range 0 < persistence <= 0.5
     *                    to avoid producing values that exceed 1                    
     * @param seed the seed to use for this generator, usually the world seed
     */
    public SimplexOctaveGenerator(int octaves, double persistence, int seed) {
        Random rand = new Random(seed);
        
        generators = new SimplexNoiseGenerator[octaves];
        frequencies = new double[octaves];
        amplitudes = new double[octaves];
        
        for (int i = 0; i < octaves; i++) {
            generators[i] = new SimplexNoiseGenerator(rand.nextInt());
            // each freqency will be 2^i, each amplitude is peristence to the octaves - ith power
            frequencies[i] = Math.pow(2,i);
            amplitudes[i] = Math.pow(persistence,octaves - i);
        }
    }
    
    public double noise(int x, int y) {
        double retVal = 0;
        for(int i = 0; i < generators.length; i ++) {
            retVal += generators[i].noise(x / frequencies[i], y / frequencies[i]) * amplitudes[i];
        }
        return retVal;
    }
    
    public double noise(int x, int y, int z) {
        double retVal = 0;
        for(int i = 0; i < generators.length; i ++) {
            retVal += generators[i].noise(x / frequencies[i], y / frequencies[i], z / frequencies[i]) * amplitudes[i];
        }
        return retVal;
    }
}
