/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.api.msg;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.tridentsdk.api.ChatColor;
import net.tridentsdk.api.entity.living.Player;

// TODO: JavaDoc
public final class MessageBuilder {
    static final Gson GSON = new Gson();

    private final JsonObject obj;
    private final JsonArray extra;
    private Message buildingObject;

    public MessageBuilder(String message) {
        this.obj = new JsonObject();
        this.extra = new JsonArray();

        // setup required properties
        this.obj.addProperty("text", "");
        this.buildingObject = new Message().text(message);
    }

    public MessageBuilder color(ChatColor color) {
        this.buildingObject.color(color);
        return this;
    }

    public MessageBuilder link(String url) {
        this.buildingObject.clickEvent(new ClickEvent()
                .action(ClickEvent.ClickAction.OPEN_URL)
                .value(url));

        return this;
    }

    public MessageBuilder file(String file) {
        this.buildingObject.clickEvent(new ClickEvent()
                .action(ClickEvent.ClickAction.OPEN_FILE)
                .value(file));

        return this;
    }

    public MessageBuilder hover(String message) {
        this.buildingObject.hoverEvent(new HoverEvent()
                .action(HoverEvent.HoverAction.SHOW_TEXT)
                .value(message));

        return this;
    }

    public MessageBuilder then(String message) {
        this.extra.add(this.buildingObject.message);
        this.buildingObject = new Message().text(message);

        return this;
    }

    public MessageBuilder then(Message message) {
        this.extra.add(this.buildingObject.message);
        this.buildingObject = message;

        return this;
    }

    /**
     * Completes the building of the message, after this call no change should be made. If any change were to be made,
     * an NPE will be thrown
     */
    public MessageBuilder build() {
        this.obj.add("extra", this.extra);
        this.buildingObject = null;

        return this;
    }

    @Override
    public String toString() {
        return GSON.toJson(this.obj);
    }

    public String toJson() {
        return this.toString();
    }

    public MessageBuilder sendTo(Player... players) {
        for (Player p : players) {
            // TODO: send message
        }

        return this;
    }
}
