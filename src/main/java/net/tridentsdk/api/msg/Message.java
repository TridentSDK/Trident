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
