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
