package net.tridentsdk.server.data;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.util.Vector;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.LinkedList;
import java.util.List;

@ThreadSafe
public class ProtocolMetadata implements Writable {
    @GuardedBy("metadata")
    private final List<MetadataValue> metadata = new LinkedList<>();

    public int addMeta(MetadataType type, Object value) {
        synchronized (metadata) {
            metadata.add(new MetadataValue(metadata.size(), value, type));
            return metadata.size() - 1;
        }
    }

    public void setMeta(int index, MetadataValue value) {
        synchronized (metadata) {
            metadata.add(index, value);
        }
    }

    public MetadataValue get(int index) {
        synchronized (metadata) {
            return metadata.get(index);
        }
    }

    public void remove(int index) {
        synchronized (metadata) {
            metadata.remove(index);
        }
    }

    @Override
    public void write(ByteBuf buf) {
        List<MetadataValue> localMeta;
        synchronized (metadata) {
            localMeta = metadata;
        }

        for(MetadataValue value : localMeta) {
            buf.writeByte((value.type().id() << 5 | value.index & 0x1F) & 0xFF);

            switch(value.type) {
                case BYTE:
                    buf.writeByte((Byte) value.value);
                    break;

                case SHORT:
                    buf.writeShort((Short) value.value);
                    break;

                case INT:
                    buf.writeInt((Integer) value.value);
                    break;

                case FLOAT:
                    buf.writeFloat((Float) value.value);
                    break;

                case STRING:
                    Codec.writeString(buf, (String) value.value);
                    break;

                case SLOT:
                    ((Slot) value.value).write(buf);
                    break;

                case XYZ:
                    Vector vector = (Vector) value.value;

                    buf.writeInt((int) vector.x());
                    buf.writeInt((int) vector.y());
                    buf.writeInt((int) vector.z());
                    break;

                case PYR:
                    Vector v = (Vector) value.value;

                    buf.writeFloat((float) v.x());
                    buf.writeFloat((float) v.y());
                    buf.writeFloat((float) v.z());

                    break;
            }

            buf.writeByte(0x7F); // terminate byte
        }
    }

    public static class MetadataValue {
        private final int index;
        private final Object value;
        private final MetadataType type;

        private MetadataValue(int index, Object value, MetadataType type) {
            this.index = index;
            this.value = value;
            this.type = type;
        }

        public int index() {
            return index;
        }

        public Object value() {
            return value;
        }

        public MetadataType type() {
            return type;
        }
    }

    public static enum MetadataType {
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
}
