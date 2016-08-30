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
package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.ui.tablist.TabListElement;
import net.tridentsdk.world.opt.GameMode;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.tridentsdk.server.net.NetData.wstr;
import static net.tridentsdk.server.net.NetData.wvint;

@Immutable
public abstract class PlayOutTabListItem extends PacketOut {

    private final PlayOutTabListItemActionType action;

    private PlayOutTabListItem(PlayOutTabListItemActionType action) {
        super(PlayOutTabListItem.class);
        this.action = action;
    }

    @Override
    public void write(ByteBuf buf) {
        wvint(buf, action.ordinal());
        wvint(buf, getActionCount());
    }

    protected abstract int getActionCount();

    public static PlayOutTabListItemAddPlayer addPlayerPacket() {
        return new PlayOutTabListItemAddPlayer();
    }

    public static PlayOutTabListItemRemovePlayer removePlayerPacket() {
        return new PlayOutTabListItemRemovePlayer();
    }

    public static PlayOutTabListItemUpdateDisplayName updatePlayerPacket() {
        return new PlayOutTabListItemUpdateDisplayName();
    }

    public static class PlayOutTabListItemAddPlayer extends PlayOutTabListItem {

        private List<PlayerData> additions = new ArrayList<>();

        public PlayOutTabListItemAddPlayer() {
            super(PlayOutTabListItemActionType.ADD_PLAYER);
        }

        public void addPlayer(UUID uuid, String name, GameMode gameMode, int ping, ChatComponent displayName) {
            PlayerData playerData = new PlayerData();
            playerData.uuid = uuid;
            playerData.name = name;
            playerData.gameMode = gameMode;
            playerData.ping = ping;
            playerData.displayName = displayName;
            additions.add(playerData);
        }

        public void addPlayer(TabListElement element) {
            PlayerData playerData = new PlayerData();
            playerData.uuid = element.getUuid();
            playerData.name = element.getName();
            playerData.gameMode = element.getGameMode();
            playerData.ping = element.getPing();
            playerData.displayName = element.getDisplayName();
            playerData.properties = element.getProperties();
            additions.add(playerData);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            additions.forEach(data -> {
                buf.writeLong(data.uuid.getMostSignificantBits());
                buf.writeLong(data.uuid.getLeastSignificantBits());
                wstr(buf, data.name);
                wvint(buf, data.properties != null ? data.properties.size() : 0);
                if(data.properties != null){
                    data.properties.forEach(playerProperty -> {
                        wstr(buf, playerProperty.getName());
                        wstr(buf, playerProperty.getValue());
                        buf.writeBoolean(playerProperty.getSignature() != null);
                        if(playerProperty.getSignature() != null){
                            wstr(buf, playerProperty.getSignature());
                        }
                    });
                }
                wvint(buf, data.gameMode.asInt());
                wvint(buf, data.ping);
                buf.writeBoolean(data.displayName != null);
                if(data.displayName != null){
                    wstr(buf, data.displayName.toString());
                }
            });
        }

        @Override
        protected int getActionCount() {
            return additions.size();
        }

        private class PlayerData {
            private UUID uuid;
            private String name;
            private GameMode gameMode;
            private int ping;
            private ChatComponent displayName;
            public List<TabListElement.PlayerProperty> properties;
        }

    }

    public static class PlayOutTabListItemRemovePlayer extends PlayOutTabListItem {

        private List<UUID> removals = new ArrayList<>();

        public PlayOutTabListItemRemovePlayer() {
            super(PlayOutTabListItemActionType.REMOVE_PLAYER);
        }

        public void removePlayer(UUID uuid) {
            removals.add(uuid);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            removals.forEach(uuid -> {
                buf.writeLong(uuid.getMostSignificantBits());
                buf.writeLong(uuid.getLeastSignificantBits());
            });
        }

        @Override
        protected int getActionCount() {
            return removals.size();
        }

    }

    public static class PlayOutTabListItemUpdateDisplayName extends PlayOutTabListItem {

        private List<PlayerData> updates = new ArrayList<>();

        public PlayOutTabListItemUpdateDisplayName() {
            super(PlayOutTabListItemActionType.UPDATE_DISPLAY_NAME);
        }

        public void update(UUID uuid, ChatComponent displayName) {
            PlayerData data = new PlayerData();
            data.uuid = uuid;
            data.displayName = displayName;
            updates.add(data);
        }

        @Override
        public void write(ByteBuf buf) {
            super.write(buf);

            updates.forEach(data -> {
                buf.writeLong(data.uuid.getMostSignificantBits());
                buf.writeLong(data.uuid.getLeastSignificantBits());
                buf.writeBoolean(data.displayName != null);
                if(data.displayName != null){
                    wstr(buf, data.displayName.toString());
                }
            });
        }

        @Override
        protected int getActionCount() {
            return updates.size();
        }

        private class PlayerData {
            private UUID uuid;
            private ChatComponent displayName;
        }

    }

    public enum PlayOutTabListItemActionType {
        ADD_PLAYER, UPDATE_GAMEMODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, REMOVE_PLAYER
    }

}