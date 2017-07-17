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
package net.tridentsdk.server.packet;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.handshake.HandshakeIn;
import net.tridentsdk.server.packet.handshake.LegacyHandshakeIn;
import net.tridentsdk.server.packet.login.*;
import net.tridentsdk.server.packet.play.*;
import net.tridentsdk.server.packet.status.StatusInPing;
import net.tridentsdk.server.packet.status.StatusInRequest;
import net.tridentsdk.server.packet.status.StatusOutPong;
import net.tridentsdk.server.packet.status.StatusOutResponse;
import net.tridentsdk.server.util.Reference2IntOpenHashMap;
import net.tridentsdk.util.Int2ReferenceOpenHashMap;

import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds packets registered by their identifying
 * packet ID as specified in the Minecraft protocol.
 */
@Immutable
public final class PacketRegistry {
    /**
     * The constructors used to instantiate the packets
     */
    private static final Map<Class<? extends Packet>, ConstructorAccess<? extends Packet>> CTORS = new HashMap<>();

    // Even though we save on autobox overhead, I was unable
    // to figure out how to flatten the packet registry,
    // even though only 2 collections to hold in reality
    // almost 8 sets of data is good enough by any standard

    /**
     * Packet registry
     */
    private static final Reference2IntOpenHashMap<Class<? extends Packet>> PACKET_IDS =
            new Reference2IntOpenHashMap<>();
    /**
     * Inverse packet registry
     */
    private static final Int2ReferenceOpenHashMap<Class<? extends Packet>> PACKETS =
            new Int2ReferenceOpenHashMap<>();

    // Initialization done in static initializer performed
    // whilst under lock during class initialization, thus
    // is threadsafe to design the registry this way

    static {
        put(HandshakeIn.class, NetClient.NetState.HANDSHAKE, Packet.Bound.SERVER, 0x00);
        put(LegacyHandshakeIn.class, NetClient.NetState.HANDSHAKE, Packet.Bound.SERVER, 0xFE);

        put(StatusInRequest.class, NetClient.NetState.STATUS, Packet.Bound.SERVER, 0x00);
        put(StatusOutResponse.class, NetClient.NetState.STATUS, Packet.Bound.CLIENT, 0x00);
        put(StatusInPing.class, NetClient.NetState.STATUS, Packet.Bound.SERVER, 0x01);
        put(StatusOutPong.class, NetClient.NetState.STATUS, Packet.Bound.CLIENT, 0x01);

        put(LoginInStart.class, NetClient.NetState.LOGIN, Packet.Bound.SERVER, 0x00);
        put(LoginOutDisconnect.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 0x00);
        put(LoginOutEncryptionRequest.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 0x01);
        put(LoginInEncryptionResponse.class, NetClient.NetState.LOGIN, Packet.Bound.SERVER, 0x01);
        put(LoginOutSuccess.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 0x02);
        put(LoginOutCompression.class, NetClient.NetState.LOGIN, Packet.Bound.CLIENT, 0x03);

        put(PlayOutLightning.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x02);
        put(PlayOutSpawnPlayer.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x05);
        put(PlayOutAnimation.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x06);
        put(PlayOutBossBar.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x0C);
        put(PlayOutDifficulty.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x0D);
        put(PlayOutChat.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x0F);
        put(PlayOutWindowItems.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x14);
        put(PlayOutSlot.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x16);
        put(PlayOutPluginMsg.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x18);
        put(PlayOutDisconnect.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x1A);
        put(PlayOutUnloadChunk.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x1D);
        put(PlayOutGameState.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x1E);
        put(PlayOutKeepAlive.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x1F);
        put(PlayOutChunk.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x20);
        put(PlayOutJoinGame.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x23);
        put(PlayOutEntityRelativeMove.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x26);
        put(PlayOutEntityLookAndRelativeMove.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x27);
        put(PlayOutEntityLook.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x28);
        put(PlayOutPlayerAbilities.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x2B);
        put(PlayOutTabListItem.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x2D);
        put(PlayOutPosLook.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x2E);
        put(PlayOutDestroyEntities.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x31);
        put(PlayOutEntityHeadLook.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x35);
        put(PlayOutEntityMetadata.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x3B);
        put(PlayOutEquipment.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x3E);
        put(PlayOutSpawnPos.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x45);
        put(PlayOutTitle.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x47);
        put(PlayOutPlayerListHeaderAndFooter.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x49);
        put(PlayOutTeleport.class, NetClient.NetState.PLAY, Packet.Bound.CLIENT, 0x4B);

        put(PlayInTeleportConfirm.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x00);
        put(PlayInChat.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x03);
        put(PlayInClientStatus.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x04);
        put(PlayInClientSettings.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x05);
        put(PlayInCloseWindow.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x09);
        put(PlayInPluginMsg.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0A);
        put(PlayInUseEntity.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0B);
        put(PlayInKeepAlive.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0C);
        put(PlayInPlayer.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0D);
        put(PlayInPos.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0E);
        put(PlayInPosLook.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x0F);
        put(PlayInLook.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x10);
        put(PlayInPlayerAbilities.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x13);
        put(PlayInPlayerDig.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x14);
        put(PlayInEntityAction.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x15);
        put(PlayInSetSlot.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x1A);
        put(PlayInCreativeInventoryAction.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x1B);
        put(PlayInAnimation.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x1D);
        put(PlayInBlockPlace.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x1F);
        put(PlayInUseItem.class, NetClient.NetState.PLAY, Packet.Bound.SERVER, 0x20);

        PACKETS.trim();
        PACKET_IDS.trim();
    }

    /**
     * Combines the data into a single value which is used
     * to locate the a packet inside of the register.
     *
     * @param bound the bound of the packet
     * @param state the packet getState
     * @param id the packet ID
     * @return the compressed packet represented as an
     * integer
     */
    // Bit shifting magic 101
    // Using compressed ID value helps flatten the map
    // hierarchy with minimal impact to performance
    // this helps save memory and autoboxing tremendously
    // top bit is bound switch
    // after that 4 bits for net getState ordinal
    // then comes the rest of the ID
    private static int shift(NetClient.NetState state, Packet.Bound bound, int id) {
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
                            NetClient.NetState state, Packet.Bound bound, int id) {
        int identifier = shift(state, bound, id);
        PACKET_IDS.put(cls, identifier);

        // Only in packets will need reflection inst
        if (bound == Packet.Bound.SERVER) {
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
     * ID, bound, and the given getState.
     *
     * @param state the packet's network getState
     * @param bound the packet bound
     * @param id the packet ID
     * @return the packet class
     */
    public static Class<? extends Packet> byId(NetClient.NetState state, Packet.Bound bound, int id) {
        int identifier = shift(state, bound, id);
        Class<? extends Packet> packet = PACKETS.get(identifier);
        if (packet != null) {
            return packet;
        }

        String paddedHex = String.format("%2s", Integer.toHexString(id).toUpperCase()).replace(' ', '0');
        throw new IllegalArgumentException(state + " => " + bound + ", " + id + " (0x" + paddedHex + ") is not registered");
    }

    /**
     * Obtains the net getState which the packet is registered
     * to be present in.
     *
     * @param cls the packet class
     * @return the getState of the packet
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
     * Obtains the getState of the packet with the given info.
     *
     * @param info the info
     * @return the packet getState
     */
    public static NetClient.NetState stateOf(int info) {
        int ordinal = info >> 27 & 0xf;
        return NetClient.NetState.values()[ordinal];
    }

    /**
     * Obtains the bound of the packet with the given info.
     *
     * @param info the info
     * @return the packet bound
     */
    public static Packet.Bound boundOf(int info) {
        int ordinal = info >> 31 & 0x1;
        return Packet.Bound.values()[ordinal];
    }
}
