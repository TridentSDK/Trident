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
package net.tridentsdk.server.packet.login;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.ui.tablist.TabListElement;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import javax.annotation.concurrent.Immutable;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static net.tridentsdk.server.net.NetData.wstr;

/**
 * This packet is sent by the server to indicate to the
 * Minecraft client that login has sucessfully completed.
 */
@Immutable
public final class LoginOutSuccess extends PacketOut {
    /**
     * The client that is successfully logging in
     */
    private final NetClient client;
    /**
     * The UUID of the player to be joined
     */
    @Getter
    private final UUID uuid;
    /**
     * The name of the player
     */
    private final String name;
    /**
     * The player skin/cape
     */
    @Getter
    private final TabListElement.PlayerProperty textures;

    public LoginOutSuccess(NetClient client) {
        super(LoginOutSuccess.class);
        this.client = client;
        this.name = client.getName();

        String tempUuid;
        try {
            tempUuid = Mojang.<String>req("https://api.mojang.com/users/profiles/minecraft/%s", this.name)
                    .callback((JsonValue element) -> element.asObject().get("id").asString())
                    .onException(s -> null)
                    .get().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (tempUuid == null) {
            this.uuid = UUID.randomUUID();
            this.textures = null;
        } else {
            this.uuid = Login.convert(this.name, tempUuid);
            try {
                JsonObject tex = Mojang.<JsonObject>req("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", tempUuid)
                        .callback((JsonValue e) -> e.asObject().get("properties").asArray().get(0).asObject())
                        .onException(s -> {
                            TridentServer.getInstance().getLogger().error("Login cannot be completed due to HTTPS error");
                            return null;
                        })
                        .get()
                        .get();
                if (tex == null) {
                    this.textures = new TabListElement.PlayerProperty("", "", "");
                } else {
                    JsonValue signature = tex.get("signature");
                    this.textures = new TabListElement.PlayerProperty(tex.get("name").asString(),
                            tex.get("value").asString(), signature != null ? signature.asString() : null);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        client.enableCompression();
    }

    public LoginOutSuccess(NetClient client, UUID uuid, String name) {
        super(LoginOutSuccess.class);
        this.client = client;
        this.uuid = uuid;
        this.name = name;
        this.textures = null;
        client.enableCompression();
    }

    @Override
    public void write(ByteBuf buf) {
        wstr(buf, this.uuid.toString());
        wstr(buf, this.name);
        this.client.setState(NetClient.NetState.PLAY);
    }
}
