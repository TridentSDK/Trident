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

import com.google.gson.JsonObject;
import net.tridentsdk.api.ChatColor;

// TODO: JavaDoc
public final class Message {

    final JsonObject message;

    public Message() {
        this.message = new JsonObject();
    }

    public Message text(String input) {
        this.message.addProperty("text", input);
        return this;
    }

    public Message color(ChatColor color) {
        this.message.addProperty("color", color.toString());

        return this;
    }

    public Message clickEvent(ClickEvent event) {
        JsonObject obj = new JsonObject();

        obj.addProperty("action", event.action.toString());
        obj.addProperty("value", event.value);

        this.message.add("clickEvent", obj);
        return this;
    }

    public Message hoverEvent(HoverEvent event) {
        JsonObject obj = new JsonObject();

        obj.addProperty("action", event.action.toString());
        obj.addProperty("value", event.value);

        this.message.add("hoverEvent", obj);
        return this;
    }

    public String toJson() {
        return MessageBuilder.GSON.toJson(this.message);
    }
}
