/*
 * THIS CLASS IS NOT PROPERTY OF TRIDENTSDK
 *
 * Source: http://pastebin.java-gaming.org/0361c1c8f7b
 */

package net.tridentsdk.server.world.gen;

import java.util.Random;

public class PerlinNoise {
    // Just a Random class object so I can fill my noise map with random directions.
    public static final Random random = new Random();

    // Width and Height of the map.
    public int width, height;

    // Random directions of length 1.
    private Vec2D[] values;

    /**
     * Creates a noise map with specified dimensions.
     *
     * @param width  of the noise map.
     * @param height of the noise map.
     */
    public PerlinNoise(int width, int height) {
        this.width = width;
        this.height = height;

        values = new Vec2D[(width + 1) * (height + 1)]; // Create an array to store random directions.

        for (int y = 0; y < height + 1; y++) {
            for (int x = 0; x < width + 1; x++) {
                int rot = random.nextInt(359); // Random direction.

                // Store random direction of length 1 to our directions array.
                values[x + y * width] = point(new Vec2D(0, 0), new Vec2D(0, -1), rot);
            }
        }
        // If you're wondering why "width + 1" "height + 1", it is because map looks blurry
        // at right and bottom edges of the image without it. Try removing it, you will see.
    }

    public float noise(float x, float y) {
        // Grid cell coordinates in integer values.
        int gx0 = (int) (Math.floor(x)); // Top-Left
        int gy0 = (int) (Math.floor(y)); // Top-Left
        int gx1 = gx0 + 1; // Down-Right
        int gy1 = gy0 + 1; // Down-Right

        // Random directions.
        Vec2D g00 = random(gx0, gy0); // Top-Left
        Vec2D g10 = random(gx1, gy0); // Top-Right
        Vec2D g11 = random(gx1, gy1); // Down-Right
        Vec2D g01 = random(gx0, gy1); // Down-Left

        // Subtract grid cells values from the point specified.
        Vec2D delta00 = new Vec2D(x - gx0, y - gy0); // Top-Left
        Vec2D delta10 = new Vec2D(x - gx1, y - gy0); // Top-Right
        Vec2D delta11 = new Vec2D(x - gx1, y - gy1); // Down-Right
        Vec2D delta01 = new Vec2D(x - gx0, y - gy1); // Down-Left

        // Compute a dot product between random directions and corresponding delta values.
        float s = dotProduct(g00, new Vec2D(delta00.getX(), delta00.getY())); // Top-Left
        float t = dotProduct(g10, new Vec2D(delta10.getX(), delta10.getY())); // Top-Right
        float u = dotProduct(g11, new Vec2D(delta11.getX(), delta11.getY())); // Down-Right
        float v = dotProduct(g01, new Vec2D(delta01.getX(), delta01.getY())); // Down-Left

        // Compute the weights for x and y axis.
        float sx = weigh(delta00.getX());
        float sy = weigh(delta00.getY());

        // Interpolate between values.
        float a = interpolate(sy, s, v); // Interpolate Top-Left(s) and Down-Left(v). We can also call this LEFT
        float b = interpolate(sy, t, u); // Interpolate Top-Right(t) and Down-Right(u) We can also call this RIGHT
        float h = interpolate(sx, a, b); // Interpolate LEFT(a) and RIGHT(b). We can call this height(h)

        h *= 4; // Multiply here so adjust contrast.

        // Make sure it is -1 to 1. If you don't change contrast, you don't have to do this.
        if (h > 1)
            h = 1;
        if (h < -1)
            h = -1;

        return h;
    }

    /**
     * Computes a weight using S-curve function f(x) = 3 * (x * x) - 2 * (x * x * x).
     *
     * @param x NOT as in axis, but as a variable.
     */
    private float weigh(float x) {
        return 3 * (x * x) - 2 * (x * x * x);
    }

    /**
     * Interpolate between 2 values, using weight.
     */
    private float interpolate(float weight, float a, float b) {
        return a + weight * (b - a);
    }

    /**
     * Compute a dot product. Example: dot product between (a, b) and (c, d) is: a * c + b * d
     */
    private float dotProduct(Vec2D v0, Vec2D v1) {
        return (v0.getX() * v1.getX()) + (v0.getY() * v1.getY());
    }

    /**
     * Get the random direction.
     */
    private Vec2D random(int x, int y) {
        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        if (x >= width)
            x = width;
        if (y >= height)
            y = height;
        return values[x + y * width];
    }

    /**
     * Rotates specified point around pivot.
     *
     * @param pivot    to rotate around.
     * @param point    to rotate around pivot.
     * @param rotation - how many degrees to rotate.
     * @return a new point, which was created by rotating given point around pivot by some degrees.
     */
    public Vec2D point(Vec2D pivot, Vec2D point, float rotation) {
        float rot = (float) (1f / 180 * rotation * Math.PI);

        float x = point.getX() - pivot.getX();
        float y = point.getY() - pivot.getY();

        float newx = (float) (x * Math.cos(rot) - y * Math.sin(rot));
        float newy = (float) (x * Math.sin(rot) + y * Math.cos(rot));

        newx += pivot.getX();
        newy += pivot.getY();

        return new Vec2D(newx, newy);
    }

    public static class Vec2D {
        private float x;
        private float y;

        public Vec2D(float x, float y) {
            this.setX(x);
            this.setY(y);
        }

        public int getX() {
            return (int) x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public int getY() {
            return (int) y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}