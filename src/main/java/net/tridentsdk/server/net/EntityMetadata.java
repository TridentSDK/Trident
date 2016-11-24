/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.tridentsdk.base.BlockDirection;
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
        return this.items.get(x);
    }

    public void add(EntityMetadataItem item) {
        this.items.add(item);
    }

    public void add(int index, EntityMetadataType type, Object value) {
        this.items.add(new EntityMetadataItem(index, type, type.cast(value)));
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
                    Vector vector = new Vector();
                    NetData.rvec(buf, vector);
                    value = vector;
                    break;
                }
                case OPTPOSITION: {
                    if (buf.readBoolean()) {
                        Vector vector = new Vector();
                        NetData.rvec(buf, vector);
                        value = vector;
                    } else {
                        value = null;
                    }
                    break;
                }
                case DIRECTION:
                    value = BlockDirection.fromMinecraftDirection(NetData.rvint(buf));
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
                    NetData.wvec(buf, pv);
                    break;
                case OPTPOSITION:
                    Vector opv = (Vector) item.value;
                    if (opv != null) {
                        NetData.wvec(buf, opv);
                    }
                    break;
                case DIRECTION:
                    NetData.wvint(buf, ((BlockDirection) item.value).getMinecraftDirection());
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
            this.value = this.type.cast(value);
        }

        public byte asByte() {
            return (byte) this.value;
        }

        public int asInt() {
            return (int) this.value;
        }

        public boolean asBit(int x) {
            return (this.asByte() & (1 << x)) != 0;
        }

        public void setBit(int x, boolean value) {
            if (this.asBit(x) == value)
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
            return (float) this.value;
        }

        public String asString() {
            return (String) this.value;
        }

        public ChatComponent asChatComponent() {
            return (ChatComponent) this.value;
        }

        public boolean asBoolean() {
            return (boolean) this.value;
        }

        public Vector asRotation() {
            return (Vector) this.value;
        }

        public Vector asPosition() {
            return (Vector) this.value;
        }

        public BlockDirection asDirection() {
            return (BlockDirection) this.value;
        }

        public UUID asUUID() {
            return (UUID) this.value;
        }

        public int[] asBlockId() {
            return (int[]) this.value;
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
                return object;
            }
        },
        POSITION(8) {
            @Override
            public Object cast(Object object) {
                return object;
            }
        },
        OPTPOSITION(9) {
            @Override
            public Object cast(Object object) {
                return object;
            }
        },
        DIRECTION(10) {
            @Override
            public Object cast(Object object) {
                return object;
            }
        },
        OPTUUID(11) {
            @Override
            public Object cast(Object object) {
                return object;
            }
        },
        BLOCKID(12) {
            @Override
            public Object cast(Object object) {
                return object;
            }
        };

        private final int id;

        EntityMetadataType(int id) {
            this.id = id;
        }

        public abstract Object cast(Object object);

    }

}
