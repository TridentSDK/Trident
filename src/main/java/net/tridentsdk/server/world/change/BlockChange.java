package net.tridentsdk.server.world.change;

import com.google.code.tempusfugit.concurrency.annotations.ThreadSafe;

/**
 * For Internal Use only
 * <p/>
 * <p>Representation of the changes to be made, queued in a MassChange</p>
 * <p/>
 * <p>Thread safe by immutability</p>
 */
@ThreadSafe
public class BlockChange {

    private final int x;
    private final int y;
    private final int z;

    private final byte id;
    private final byte data;

    protected BlockChange(int x, int y, int z, byte id, byte data) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.id = id;
        this.data = data;
    }

    public byte data() {
        return data;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    public byte id() {
        return id;
    }

}
