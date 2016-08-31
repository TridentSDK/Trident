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
package net.tridentsdk.server.packet;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.tridentsdk.server.packet.handshake.HandshakeIn;
import net.tridentsdk.server.packet.login.*;
import net.tridentsdk.server.packet.play.*;
import net.tridentsdk.server.packet.status.StatusInPing;
import net.tridentsdk.server.packet.status.StatusInRequest;
import net.tridentsdk.server.packet.status.StatusOutPong;
import net.tridentsdk.server.packet.status.StatusOutResponse;

import javax.annotation.concurrent.Immutable;
import java.util.Map;

import static net.tridentsdk.server.net.NetClient.NetState;
import static net.tridentsdk.server.packet.Packet.Bound;

/**
 * This class holds packets registered by their identifying
 * packet ID as specified in the Minecraft protocol.
 */
@Immutable
public final class PacketRegistry {
    /**
     * The constructors used to instantiate the packets
     */
    private static final Map<Class<? extends Packet>, ConstructorAccess<? extends Packet>> CTORS =
            Maps.newHashMap();

    // Even though we save on autobox overhead, I was unable
    // to figure out how to flatten the packet registry,
    // even though only 2 collections to hold in reality
    // almost 8 sets of data is good enough by any standard

    /**
     * Packet registry
     */
    private static final Object2IntOpenHashMap<Class<? extends Packet>> PACKET_IDS =
            new Object2IntOpenHashMap<>();
    /**
     * Inverse packet registry
     */
    private static final Int2ObjectOpenHashMap<Class<? extends Packet>> PACKETS =
            new Int2ObjectOpenHashMap<>();

    // Initialization done in static initializer performed
    // whilst under lock during class initialization, thus
    // is threadsafe to design the registry this way

    static {
        put(HandshakeIn.class, NetState.HANDSHAKE, Bound.SERVER, 0x00);

        put(StatusInRequest.class, NetState.STATUS, Bound.SERVER, 0x00);
        put(StatusOutResponse.class, NetState.STATUS, Bound.CLIENT, 0x00);
        put(StatusInPing.class, NetState.STATUS, Bound.SERVER, 0x01);
        put(StatusOutPong.class, NetState.STATUS, Bound.CLIENT, 0x01);

        put(LoginInStart.class, NetState.LOGIN, Bound.SERVER, 0x00);
        put(LoginOutDisconnect.class, NetState.LOGIN, Bound.CLIENT, 0x00);
        put(LoginOutEncryptionRequest.class, NetState.LOGIN, Bound.CLIENT, 0x01);
        put(LoginInEncryptionResponse.class, NetState.LOGIN, Bound.SERVER, 0x01);
        put(LoginOutSuccess.class, NetState.LOGIN, Bound.CLIENT, 0x02);
        put(LoginOutCompression.class, NetState.LOGIN, Bound.CLIENT, 0x03);

        put(PlayInChat.class, NetState.PLAY, Bound.SERVER, 0x02);
        put(PlayOutChat.class, NetState.PLAY, Bound.CLIENT, 0x0F);
        put(PlayOutJoinGame.class, NetState.PLAY, Bound.CLIENT, 0x23);
        put(PlayOutPluginMsg.class, NetState.PLAY, Bound.CLIENT, 0x18);
        put(PlayOutDifficulty.class, NetState.PLAY, Bound.CLIENT, 0x0D);
        put(PlayOutSpawnPos.class, NetState.PLAY, Bound.CLIENT, 0x43);
        put(PlayOutAbilities.class, NetState.PLAY, Bound.CLIENT, 0x2B);
        put(PlayInPluginMsg.class, NetState.PLAY, Bound.SERVER, 0x09);
        put(PlayInSettings.class, NetState.PLAY, Bound.SERVER, 0x04);
        put(PlayOutPosLook.class, NetState.PLAY, Bound.CLIENT, 0x2E);
        put(PlayInTeleportConfirm.class, NetState.PLAY, Bound.SERVER, 0x00);
        put(PlayInPosLook.class, NetState.PLAY, Bound.SERVER, 0x0D);
        put(PlayInClientStatus.class, NetState.PLAY, Bound.SERVER, 0x03);
        put(PlayOutKeepAlive.class, NetState.PLAY, Bound.CLIENT, 0x1F);
        put(PlayInKeepAlive.class, NetState.PLAY, Bound.SERVER, 0x0B);
        put(PlayInPos.class, NetState.PLAY, Bound.SERVER, 0x0C);
        put(PlayInPlayer.class, NetState.PLAY, Bound.SERVER, 0x0F);
        put(PlayInLook.class, NetState.PLAY, Bound.SERVER, 0x0E);
        put(PlayOutChunk.class, NetState.PLAY, Bound.CLIENT, 0x20);
        put(PlayOutDisconnect.class, NetState.PLAY, Bound.CLIENT, 0x1A);
        put(PlayOutTabListItem.class, NetState.PLAY, Bound.CLIENT, 0x2D);
        put(PlayOutSpawnPlayer.class, NetState.PLAY, Bound.CLIENT, 0x05);
        put(PlayOutEntityRelativeMove.class, NetState.PLAY, Bound.CLIENT, 0x25);
        put(PlayOutEntityLookAndRelativeMove.class, NetState.PLAY, Bound.CLIENT, 0x26);
        put(PlayOutEntityLook.class, NetState.PLAY, Bound.CLIENT, 0x27);
        put(PlayOutEntityHeadLook.class, NetState.PLAY, Bound.CLIENT, 0x34);
        put(PlayOutPlayerListHeaderAndFooter.class, NetState.PLAY, Bound.CLIENT, 0x47);
        put(PlayOutDestroyEntities.class, NetState.PLAY, Bound.CLIENT, 0x30);

        put(PlayOutEntityMetadata.class, NetState.PLAY, Bound.CLIENT, 0x39);
        put(PlayInEntityAction.class, NetState.PLAY, Bound.SERVER, 0x14);

        PACKETS.trim();
        PACKET_IDS.trim();
        PACKET_IDS.defaultReturnValue(-1);
    }

    /**
     * Combines the data into a single value which is used
     * to locate the a packet inside of the register.
     *
     * @param bound the bound of the packet
     * @param state the packet state
     * @param id the packet ID
     * @return the compressed packet represented as an
     * integer
     */
    // Bit shifting magic 101
    // Using compressed ID value helps flatten the map
    // hierarchy with minimal impact to performance
    // this helps save memory and autoboxing tremendously
    // top bit is bound switch
    // after that 4 bits for net state ordinal
    // then comes the rest of the ID
    private static int shift(NetState state, Bound bound, int id) {
        int identifier = id;
        identifier |= state.ordinal() << 27;
        identifier |= bound.ordinal() << 31;
        return identifier;
    }

    /**
     * Puts the given packet class into the map with the
     * given ID, and also inserts the constructor into the
     * CTOR cache.
     *
     * @param cls the class
     * @param id the ID
     */
    private static void put(Class<? extends Packet> cls,
                            NetState state, Bound bound, int id) {
        int identifier = shift(state, bound, id);
        PACKET_IDS.put(cls, identifier);

        // Only in packets will need reflection inst
        if (bound == Bound.SERVER) {
            PACKETS.put(identifier, cls);
            CTORS.put(cls, ConstructorAccess.get(cls));
        }
    }

    // Prevent instantiation
    private PacketRegistry() {
    }

    /**
     * Creates a new instance of the given packet class.
     *
     * @param cls the packet class to instantiate
     * @return the instantiated packet
     */
    public static <T extends Packet> T make(Class<? extends Packet> cls) {
        return (T) CTORS.get(cls).newInstance();
    }

    // When switching over enums always put PLAY in front
    // because the majority of packets can return quickly
    // as there are only a few packets that are not PLAY net
    // status and thus the majority of lookups can return
    // much quicker

    /**
     * Obtains the class of the packet containing the given
     * ID, bound, and the given state.
     *
     * @param state the packet's network state
     * @param bound the packet bound
     * @param id the packet ID
     * @return the packet class
     */
    public static Class<? extends Packet> byId(NetState state, Bound bound, int id) {
        int identifier = shift(state, bound, id);
        Class<? extends Packet> packet = PACKETS.get(identifier);
        if (packet != null) {
            return packet;
        }

        String paddedHex = String.format("%2s", Integer.toHexString(id).toUpperCase()).replace(' ', '0');
        throw new IllegalArgumentException(state + ", " + bound + ", " + id + " (0x" + paddedHex + ") is not registered");
    }

    /**
     * Obtains the net state which the packet is registered
     * to be present in.
     *
     * @param cls the packet class
     * @return the state of the packet
     */
    public static int packetInfo(Class<? extends Packet> cls) {
        int identifier = PACKET_IDS.getInt(cls);
        if (identifier != -1) {
            return identifier;
        }

        throw new IllegalArgumentException(cls.getSimpleName() + " is not registered");
    }

    /**
     * Obtains the ID of the packet with the given info.
     *
     * @param info the info
     * @return the packet ID
     */
    public static int idOf(int info) {
        return info & 0x7ffffff;
    }

    /**
     * Obtains the state of the packet with the given info.
     *
     * @param info the info
     * @return the packet state
     */
    public static NetState stateOf(int info) {
        int ordinal = info >> 27 & 0xf;
        return NetState.values()[ordinal];
    }

    /**
     * Obtains the bound of the packet with the given info.
     *
     * @param info the info
     * @return the packet bound
     */
    public static Bound boundOf(int info) {
        int ordinal = info >> 31 & 0x1;
        return Bound.values()[ordinal];
    }
}
