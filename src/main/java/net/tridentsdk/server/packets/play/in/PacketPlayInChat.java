/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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

package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.Handler;
import net.tridentsdk.event.player.PlayerChatEvent;
import net.tridentsdk.meta.MessageBuilder;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.packets.play.out.PacketPlayOutChat;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;

public class PacketPlayInChat extends InPacket {

    /**
     * Message sent by the client, represented in JSON  TODO: provide example
     */
    protected String message;

    @Override
    public int id() {
        return 0x01;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.message = Codec.readString(buf);

        return this;
    }

    public String message() {
        return this.message;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        PlayerConnection pc = (PlayerConnection) connection;
        TridentPlayer player = pc.player();

        if(message.startsWith("/")) {
            Handler.forCommands().handleCommand(message.substring(1), player);
            return;
        } else {
            PlayerChatEvent event = new PlayerChatEvent(player, message);

            Handler.forEvents().fire(event);

            if(event.isIgnored()) {
                return;
            }
        }

        PacketPlayOutChat packet = new PacketPlayOutChat();

        String identifier = Handler
                .forChat()
                .format(player.name() + "> ", player)
                .replaceAll("%p", "")
                .replaceAll("%n", player.name())
                .replaceAll("%s", "")
                .replaceAll("%d", "> ");

        packet.set("jsonMessage", new MessageBuilder(identifier + message).build().asJson());
        packet.set("position", PacketPlayOutChat.ChatPosition.CHAT);

        TridentPlayer.sendAll(packet);
    }
}
