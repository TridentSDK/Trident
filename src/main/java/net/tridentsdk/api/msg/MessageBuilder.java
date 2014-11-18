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
