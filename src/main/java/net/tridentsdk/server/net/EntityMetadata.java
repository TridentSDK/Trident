package net.tridentsdk.server.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.tridentsdk.base.Direction;
import net.tridentsdk.base.Vector;
import net.tridentsdk.chat.ChatComponent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class EntityMetadata {

    private List<EntityMetadataItem> items = Collections.synchronizedList(new LinkedList<>());

    public EntityMetadataItem get(int x) {
        return items.get(x);
    }

    public void add(EntityMetadataItem item) {
        items.add(item);
    }

    public void add(int index, EntityMetadataType type, Object value) {
        items.add(new EntityMetadataItem(index, type, type.cast(value)));
    }

    public void read(ByteBuf buf) {
        List<EntityMetadataItem> items = new LinkedList<>();
        short id;
        while ((id = buf.readUnsignedByte()) != 0xFF) {
            EntityMetadataType type = EntityMetadataType.values()[id];
            Object value = null;
            switch (type) {
                case BYTE:
                    value = buf.readByte();
                    break;
                case VARINT:
                    value = NetData.rvint(buf);
                    break;
                case FLOAT:
                    value = buf.readFloat();
                    break;
                case STRING:
                    value = NetData.rstr(buf);
                    break;
                case CHAT:
                    value = ChatComponent.fromJson(new Gson().fromJson(NetData.rstr(buf), JsonObject.class));
                    break;
                case SLOT:
                    // TODO -  slots
                    break;
                case BOOLEAN:
                    value = buf.readBoolean();
                    break;
                case ROTATION:
                    float[] rd = new float[3];
                    for (int i = 0; i < 3; i++) {
                        rd[i] = buf.readFloat();
                    }
                    value = new Vector(rd[0], rd[1], rd[2]);
                    break;
                case POSITION: {
                    long val = buf.readLong();
                    int x = (int) (val >> 38);
                    int y = (int) ((val >> 26) & 0xFFF);
                    int z = (int) (val << 38 >> 38);
                    value = new Vector(x, y, z);
                    break;
                }
                case OPTPOSITION: {
                    if (buf.readBoolean()) {
                        long val = buf.readLong();
                        int x = (int) (val >> 38);
                        int y = (int) ((val >> 26) & 0xFFF);
                        int z = (int) (val << 38 >> 38);
                        value = new Vector(x, y, z);
                    } else {
                        value = null;
                    }
                    break;
                }
                case DIRECTION:
                    value = Direction.values()[NetData.rvint(buf)];
                    break;
                case OPTUUID:
                    if (buf.readBoolean()) {
                        value = new UUID(buf.readLong(), buf.readLong());
                    } else {
                        value = null;
                    }
                    break;
                case BLOCKID:
                    int bid = NetData.rvint(buf);
                    value = new int[]{ (bid >> 4) & 0xF, bid & 0xF };
                    break;
            }
            if (value != null) {
                items.add(new EntityMetadataItem(id, type, value));
            }
        }
        this.items.clear();
        this.items.addAll(items);
    }

    public void write(ByteBuf buf) {
        List<EntityMetadataItem> items = this.items;

        for (EntityMetadataItem item : items) {
            buf.writeByte(item.index);
            buf.writeByte(item.type.id);

            switch (item.type) {
                case BYTE:
                    buf.writeByte((byte) item.value);
                    break;
                case VARINT:
                    NetData.wvint(buf, (int) item.value);
                    break;
                case FLOAT:
                    buf.writeFloat((float) item.value);
                    break;
                case STRING:
                    NetData.wstr(buf, item.value.toString());
                    break;
                case SLOT:
                    // TODO - slots
                    break;
                case BOOLEAN:
                    buf.writeBoolean((Boolean) item.value);
                    break;
                case ROTATION:
                    Vector rv = (Vector) item.value;
                    buf.writeFloat((float) rv.x());
                    buf.writeFloat((float) rv.y());
                    buf.writeFloat((float) rv.z());
                    break;
                case POSITION:
                    Vector pv = (Vector) item.value;
                    int[] posData = new int[]{ pv.intX(), pv.intY(), pv.intZ() };
                    long posWrite = ((posData[0] & 0x3FFFFFF) << 38) | ((posData[1] & 0xFFF) << 26) | (posData[2] & 0x3FFFFFF);
                    buf.writeLong(posWrite);
                    break;
                case OPTPOSITION:
                    Vector opv = (Vector) item.value;
                    if (opv != null) {
                        int[] optPosData = new int[]{ opv.intX(), opv.intY(), opv.intZ() };
                        long optPosWrite = ((optPosData[0] & 0x3FFFFFF) << 38) | ((optPosData[1] & 0xFFF) << 26) | (optPosData[2] & 0x3FFFFFF);
                        buf.writeLong(optPosWrite);
                    }
                    break;
                case DIRECTION:
                    NetData.wvint(buf, ((Direction) item.value).getData());
                    break;
                case OPTUUID:
                    UUID uuid = (UUID) item.value;
                    if (uuid != null) {
                        buf.writeLong(uuid.getMostSignificantBits());
                        buf.writeLong(uuid.getLeastSignificantBits());
                    }
                    break;
                case BLOCKID:
                    int[] blockIdData = (int[]) item.value;
                    NetData.wvint(buf, blockIdData[0] << 4 | blockIdData[1]);
                    break;
            }
        }
        buf.writeByte(0xFF);
    }

    @Getter
    @AllArgsConstructor
    public static final class EntityMetadataItem {

        private int index;
        private EntityMetadataType type;
        private Object value;

        public void set(Object value) {
            this.value = type.cast(value);
        }

        public byte asByte() {
            return (byte) value;
        }

        public int asInt() {
            return (int) value;
        }

        public boolean asBit(int x) {
            return (asByte() & (1 << x)) != 0;
        }

        public void setBit(int x, boolean value) {
            if (asBit(x) == value)
                return;
            byte val = (byte) this.value;
            if (value) {
                val |= 1 << x;
            } else {
                val &= ~(1 << x);
            }
            this.value = val;
        }

        public float asFloat() {
            return (float) value;
        }

        public String asString() {
            return (String) value;
        }

        public ChatComponent asChatComponent() {
            return (ChatComponent) value;
        }

        public boolean asBoolean() {
            return (boolean) value;
        }

        public Vector asRotation() {
            return (Vector) value;
        }

        public Vector asPosition() {
            return (Vector) value;
        }

        public Direction asDirection() {
            return (Direction) value;
        }

        public UUID asUUID() {
            return (UUID) value;
        }

        public int[] asBlockId() {
            return (int[]) value;
        }

    }

    public enum EntityMetadataType {

        BYTE(0) {
            @Override
            public Object cast(Object object) {
                return ((Number) object).byteValue();
            }
        },
        VARINT(1) {
            @Override
            public Object cast(Object object) {
                return ((Number) object).intValue();
            }
        },
        FLOAT(2) {
            @Override
            public Object cast(Object object) {
                return ((Number) object).floatValue();
            }
        },
        STRING(3) {
            @Override
            public Object cast(Object object) {
                return object.toString();
            }
        },
        CHAT(4) {
            @Override
            public Object cast(Object object) {
                return object.toString();
            }
        },
        SLOT(5) {
            @Override
            public Object cast(Object object) {
                return null;
            }
        },
        BOOLEAN(6) {
            @Override
            public Object cast(Object object) {
                return object instanceof Boolean ? (boolean) object : Boolean.parseBoolean(object.toString());
            }
        },
        ROTATION(7) {
            @Override
            public Object cast(Object object) {
                return (Vector) object;
            }
        },
        POSITION(8) {
            @Override
            public Object cast(Object object) {
                return (Vector) object;
            }
        },
        OPTPOSITION(9) {
            @Override
            public Object cast(Object object) {
                return (Vector) object;
            }
        },
        DIRECTION(10) {
            @Override
            public Object cast(Object object) {
                return (Direction) object;
            }
        },
        OPTUUID(11) {
            @Override
            public Object cast(Object object) {
                return (UUID) object;
            }
        },
        BLOCKID(12) {
            @Override
            public Object cast(Object object) {
                return (int[]) object;
            }
        };

        private final int id;

        private EntityMetadataType(int id) {
            this.id = id;
        }

        public abstract Object cast(Object object);

    }

}
