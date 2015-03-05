package net.tridentsdk.server.data;

public enum MetadataType {
    BYTE(0),
    SHORT(1),
    INT(2),
    FLOAT(3),
    STRING(4),
    SLOT(5),
    XYZ(6), // expecting a vector to represent
    /*
     * Essentially representing pitch, yaw, and roll. Expecting a vector to represent
     */
    PYR(7);

    private int id;

    MetadataType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}