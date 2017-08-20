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
package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.ui.tablist.TabListElement;
import net.tridentsdk.ui.chat.ChatComponent;
import net.tridentsdk.world.opt.GameMode;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

import static net.tridentsdk.server.net.NetData.wstr;
import static net.tridentsdk.server.net.NetData.wvint;

/**
 * A tab list item update packet, which can perform any of
 * the operations that are listed in
 * {@link ActionType}.
 */
@Immutable
public abstract class PlayOutTabListItem extends PacketOut {
    /**
     * The action type that is occuring
     */
    private final ActionType action;

    private PlayOutTabListItem(ActionType action) {
        super(PlayOutTabListItem.class);
        this.action = action;
    }

    @Override
    public void write(ByteBuf buf) {
        wvint(buf, this.action.ordinal());
        wvint(buf, this.getActionCount());
    }

    public abstract int getActionCount();

    public static AddPlayer addPlayerPacket() {
        return new AddPlayer();
    }

    public static RemovePlayer removePlayerPacket() {
        return new RemovePlayer();
    }

    public static UpdateDisplayName updatePlayerPacket() {
        return new UpdateDisplayName();
    }

    public static UpdateGameMode updateGamemodePacket() {
        return new UpdateGameMode();
    }

    public static UpdateLatency updateLatencyPacket() {
        return new UpdateLatency();
    }

    @NotThreadSafe
    public static class AddPlayer extends PlayOutTabListItem {
        private final Collection<PlayerData> additions = new HashSet<>();

        public AddPlayer() {
            super(ActionType.ADD_PLAYER);
        }

        public void addPlayer(UUID uuid, String name, GameMode gameMode, int ping, ChatComponent displayName) {
            this.addPlayer(uuid, name, gameMode, ping, displayName, null);
        }

        public void addPlayer(UUID uuid, String name, GameMode gameMode, int ping, ChatComponent displayName, List<TabListElement.PlayerProperty> properties) {
            PlayerData playerData = new PlayerData(uuid, name, gameMode, ping, displayName, properties);
            this.additions.add(playerData);
        }

        public void addPlayer(TabListElement element) {
            PlayerData playerData = new PlayerData(element.getUuid(),
                    element.getName(),
                    element.getGameMode(),
                    element.getPing(),
                    element.getDisplayName(),
                    element.getProperties());
            this.additions.add(playerData);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            this.additions.forEach(data -> {
                buf.writeLong(data.uuid.getMostSignificantBits());
                buf.writeLong(data.uuid.getLeastSignificantBits());
                wstr(buf, data.name);
                wvint(buf, data.properties != null ? data.properties.size() : 0);
                if (data.properties != null) {
                    data.properties.forEach(playerProperty -> {
                        wstr(buf, playerProperty.getName());
                        wstr(buf, playerProperty.getValue());
                        buf.writeBoolean(playerProperty.getSignature() != null);
                        if (playerProperty.getSignature() != null) {
                            wstr(buf, playerProperty.getSignature());
                        }
                    });
                }
                wvint(buf, data.gameMode.asInt());
                wvint(buf, data.ping);
                buf.writeBoolean(data.displayName != null);
                if (data.displayName != null) {
                    wstr(buf, data.displayName.toString());
                }
            });
        }

        @Override
        public int getActionCount() {
            return this.additions.size();
        }

        @Immutable
        @EqualsAndHashCode(of = "uuid")
        @RequiredArgsConstructor
        private class PlayerData {
            private final UUID uuid;
            private final String name;
            private final GameMode gameMode;
            private final int ping;
            private final ChatComponent displayName;
            private final List<TabListElement.PlayerProperty> properties;
        }
    }

    @NotThreadSafe
    public static class RemovePlayer extends PlayOutTabListItem {
        private final Collection<UUID> removals = new HashSet<>();

        public RemovePlayer() {
            super(ActionType.REMOVE_PLAYER);
        }

        public void removePlayer(UUID uuid) {
            this.removals.add(uuid);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            this.removals.forEach(uuid -> {
                buf.writeLong(uuid.getMostSignificantBits());
                buf.writeLong(uuid.getLeastSignificantBits());
            });
        }

        @Override
        public int getActionCount() {
            return this.removals.size();
        }
    }

    @NotThreadSafe
    public static class UpdateGameMode extends PlayOutTabListItem {
        private final Map<UUID, GameMode> updates = new HashMap<>();

        public UpdateGameMode() {
            super(ActionType.UPDATE_GAMEMODE);
        }

        public void update(UUID uuid, GameMode gameMode) {
            this.updates.put(uuid, gameMode);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            this.updates.forEach((uuid, gameMode) -> {
                buf.writeLong(uuid.getMostSignificantBits());
                buf.writeLong(uuid.getLeastSignificantBits());
                wvint(buf, gameMode.asInt());
            });
        }

        @Override
        public int getActionCount() {
            return this.updates.size();
        }
    }

    @NotThreadSafe
    public static class UpdateLatency extends PlayOutTabListItem {
        private final Map<UUID, Integer> updates = new HashMap<>();

        public UpdateLatency() {
            super(ActionType.UPDATE_LATENCY);
        }

        public void update(UUID uuid, int latency) {
            this.updates.put(uuid, latency);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            this.updates.forEach((uuid, latency) -> {
                buf.writeLong(uuid.getMostSignificantBits());
                buf.writeLong(uuid.getLeastSignificantBits());
                wvint(buf, latency);
            });
        }

        @Override
        public int getActionCount() {
            return this.updates.size();
        }
    }

    @NotThreadSafe
    public static class UpdateDisplayName extends PlayOutTabListItem {
        private final Collection<UpdateDisplayName.PlayerData> updates = new HashSet<>();

        public UpdateDisplayName() {
            super(ActionType.UPDATE_DISPLAY_NAME);
        }

        public void update(UUID uuid, ChatComponent displayName) {
            UpdateDisplayName.PlayerData data = new UpdateDisplayName.PlayerData(uuid, displayName);
            this.updates.add(data);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            this.updates.forEach(data -> {
                buf.writeLong(data.uuid.getMostSignificantBits());
                buf.writeLong(data.uuid.getLeastSignificantBits());
                buf.writeBoolean(data.displayName != null);
                if (data.displayName != null) {
                    wstr(buf, data.displayName.toString());
                }
            });
        }

        @Override
        public int getActionCount() {
            return this.updates.size();
        }

        @Immutable
        @EqualsAndHashCode(of = "uuid")
        @RequiredArgsConstructor
        private final class PlayerData {
            private final UUID uuid;
            private final ChatComponent displayName;
        }
    }

    @Immutable
    public enum ActionType {
        ADD_PLAYER, UPDATE_GAMEMODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, REMOVE_PLAYER
    }
}