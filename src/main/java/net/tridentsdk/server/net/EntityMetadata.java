/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.tridentsdk.base.BlockDirection;
import net.tridentsdk.base.Vector;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
@ThreadSafe
public class EntityMetadata {
    private static final Gson GSON = new Gson();

    private final List<EntityMetadata.EntityMetadataItem> items = Collections.synchronizedList(new ArrayList<>());

    public EntityMetadata.EntityMetadataItem get(int x) {
        return this.items.get(x);
    }

    public void add(EntityMetadata.EntityMetadataItem item) {
        this.items.add(item);
    }

    public void add(int index, EntityMetadata.EntityMetadataType type, Object value) {
        this.items.add(new EntityMetadata.EntityMetadataItem(index, type, new AtomicReference<>(type.cast(value))));
    }

    public void read(ByteBuf buf) {
        List<EntityMetadata.EntityMetadataItem> items = new ArrayList<>();
        short id;
        while ((id = buf.readUnsignedByte()) != 0xFF) {
            EntityMetadata.EntityMetadataType type = EntityMetadata.EntityMetadataType.values()[id];
            Object value;
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
                    value = ChatComponent.fromJson(GSON.fromJson(NetData.rstr(buf), JsonObject.class));
                    break;
                case SLOT:
                    value = Slot.read(buf);
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
                case OPTPOSITION:
                    if (buf.readBoolean()) {
                        Vector vector = new Vector();
                        NetData.rvec(buf, vector);
                        value = vector;
                    } else {
                        value = null;
                    }
                    break;
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
                    value = new int[]{ bid >> 4 & 0xF, bid & 0xF };
                    break;
                default:
                    continue;
            }
            items.add(new EntityMetadata.EntityMetadataItem(id, type, new AtomicReference<>(value)));
        }
        this.items.clear();
        this.items.addAll(items);
    }

    public void write(ByteBuf buf) {
        List<EntityMetadata.EntityMetadataItem> items = this.items;

        for (EntityMetadata.EntityMetadataItem item : items) {
            buf.writeByte(item.index);
            buf.writeByte(item.type.id);

            switch (item.type) {
                case BYTE:
                    buf.writeByte(item.asByte());
                    break;
                case VARINT:
                    NetData.wvint(buf, item.asInt());
                    break;
                case FLOAT:
                    buf.writeFloat(item.asFloat());
                    break;
                case STRING:
                    String str = Objects.requireNonNull(item.asString(), "string required but was null (idx: " + item.index + ")");
                    NetData.wstr(buf, str);
                    break;
                case CHAT:
                    ChatComponent cc = Objects.requireNonNull(item.asChatComponent(), "chat component required but was null (idx: " + item.index + ")");
                    NetData.wstr(buf, cc.toString());
                    break;
                case SLOT:
                    Slot slot = Objects.requireNonNull(item.asSlot(), "slot required but was null (idx: " + item.index + ")");
                    slot.write(buf);
                    break;
                case BOOLEAN:
                    buf.writeBoolean(item.asBoolean());
                    break;
                case ROTATION:
                    Vector rv = Objects.requireNonNull(item.asRotation(), "rotation required but was null (idx: " + item.index + ")");
                    buf.writeFloat((float) rv.getX());
                    buf.writeFloat((float) rv.getY());
                    buf.writeFloat((float) rv.getZ());
                    break;
                case POSITION:
                    Vector pv = Objects.requireNonNull(item.asPosition(), "position required but was null (idx: " + item.index + ")");
                    NetData.wvec(buf, pv);
                    break;
                case OPTPOSITION:
                    Vector opv = item.asPosition();
                    buf.writeBoolean(opv != null);
                    if (opv != null) {
                        NetData.wvec(buf, opv);
                    }
                    break;
                case DIRECTION:
                    BlockDirection direction = Objects.requireNonNull(item.asDirection(), "direction required but was null (idx: " + item.index + ")");
                    NetData.wvint(buf, direction.getMinecraftDirection());
                    break;
                case OPTUUID:
                    UUID uuid = item.asUUID();
                    buf.writeBoolean(uuid != null);
                    if (uuid != null) {
                        buf.writeLong(uuid.getMostSignificantBits());
                        buf.writeLong(uuid.getLeastSignificantBits());
                    }
                    break;
                case BLOCKID:
                    int[] blockIdData = item.asBlockId();
                    NetData.wvint(buf, blockIdData[0] << 4 | blockIdData[1]);
                    break;
            }
        }
        buf.writeByte(0xFF);
    }

    @Getter
    @AllArgsConstructor
    public static class EntityMetadataItem {
        private final int index;
        private final EntityMetadata.EntityMetadataType type;
        private final AtomicReference<Object> value;

        public void set(Object value) {
            this.value.set(this.type.cast(value));
        }

        public byte asByte() {
            Object object = this.value.get();
            return object instanceof Number ? ((Number) object).byteValue() : 0;
        }

        public int asInt() {
            Object object = this.value.get();
            return object instanceof Number ? ((Number) object).intValue() : 0;
        }

        public float asFloat() {
            Object object = this.value.get();
            return object instanceof Number ? ((Number) object).floatValue() : 0;
        }

        public boolean asBit(int x) {
            return (this.asByte() & 1 << x) != 0;
        }

        public void setBit(int x, boolean value) {
            byte val;
            byte newVal;
            do {
                val = newVal = this.asByte();
                if ((val & 1 << x) != 0 == value)
                    return;
                if (value) {
                    newVal |= 1 << x;
                } else {
                    newVal &= ~(1 << x);
                }
            } while (!this.value.compareAndSet(val, newVal));
        }

        public String asString() {
            Object object = this.value.get();
            return String.valueOf(object);
        }

        public ChatComponent asChatComponent() {
            return (ChatComponent) this.value.get();
        }

        public boolean asBoolean() {
            Object object = this.value.get();
            return object instanceof Boolean ? (Boolean) object : Boolean.valueOf(String.valueOf(object));
        }

        public Slot asSlot() {
            return (Slot) this.value.get();
        }

        public Vector asRotation() {
            return (Vector) this.value.get();
        }

        public Vector asPosition() {
            return (Vector) this.value.get();
        }

        public BlockDirection asDirection() {
            return (BlockDirection) this.value.get();
        }

        public UUID asUUID() {
            return (UUID) this.value.get();
        }

        public int[] asBlockId() {
            return (int[]) this.value.get();
        }
    }

    public enum EntityMetadataType {
        BYTE(0) {
            @Override
            public Object cast(Object object) {
                return object == null ? 0 : ((Number) object).byteValue();
            }
        },
        VARINT(1) {
            @Override
            public Object cast(Object object) {
                return object == null ? 0 : ((Number) object).intValue();
            }
        },
        FLOAT(2) {
            @Override
            public Object cast(Object object) {
                return object == null ? 0 : ((Number) object).floatValue();
            }
        },
        STRING(3) {
            @Override
            public Object cast(Object object) {
                return object == null ? "" : object.toString();
            }
        },
        CHAT(4) {
            @Override
            public Object cast(Object object) {
                return object == null ? new ChatComponent() : object.toString();
            }
        },
        SLOT(5) {
            @Override
            public Object cast(Object object) {
                // TODO handle slot casting from NBT and JSON where applicable
                return object;
            }
        },
        BOOLEAN(6) {
            @Override
            public Object cast(Object object) {
                return object instanceof Boolean ? (boolean) object : Boolean.parseBoolean(String.valueOf(object));
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
