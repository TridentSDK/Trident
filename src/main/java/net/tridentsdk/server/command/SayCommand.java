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
package net.tridentsdk.server.command;

import net.tridentsdk.command.*;
import net.tridentsdk.command.params.ParamsAnnotations;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.ui.chat.ChatComponent;

import net.tridentsdk.ui.chat.ClickAction;
import net.tridentsdk.ui.chat.ClickEvent;

public class SayCommand implements CommandListener {
    @Command(name = "say", help = "/say <message>", desc = "Broadcasts a message to all players")
    @ParamsAnnotations.PermissionRequired("minecraft.say")
    public void say(CommandSource source, String[] args, @ParamsAnnotations.MinCount(1) String... message) {
        StringBuilder builder = new StringBuilder();
        for (String arg : message) {
            builder.append(' ').append(arg);
        }

        if (source.getCmdType() == CommandSourceType.PLAYER) {
            String name = ((TridentPlayer) source).getName();
            String msg = '[' + name + "]";
            ChatComponent cc = ChatComponent.create()
                    .setText(msg)
                    .setClickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/tell " + name + " "))
                    .addExtra(builder.toString());
            for (TridentPlayer player : TridentPlayer.getPlayers().values()) {
                player.sendMessage(cc);
            }
            TridentServer.getInstance().getLogger().log(msg + builder);
        } else {
            String msg = "[Server]" + builder;
            for (TridentPlayer player : TridentPlayer.getPlayers().values()) {
                player.sendMessage(ChatComponent.create().setText(msg));
            }
            TridentServer.getInstance().getLogger().log(msg);
        }
    }
}
