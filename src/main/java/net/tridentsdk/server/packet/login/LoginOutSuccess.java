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
package net.tridentsdk.server.packet.login;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketOut;

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
    private final String textures;

    public LoginOutSuccess(NetClient client) {
        super(LoginOutSuccess.class);
        this.client = client;
        this.name = client.name();

        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(this.name));
        String tempUuid;
        try {
            tempUuid = Mojang.<String>req("https://api.mojang.com/profiles/minecraft")
                    .callback((JsonElement element) -> element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString())
                    .post(array).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (tempUuid == null) {
            this.uuid = UUID.randomUUID();
            this.textures = "";
        } else {
            this.uuid = Login.convert(this.name, tempUuid);
            try {
                this.textures = Mojang.<String>req("https://sessionserver.mojang.com/session/minecraft/profile/%s", tempUuid)
                        .callback((JsonElement element) -> element.getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString())
                        .get().get();
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
        this.textures = "";
        client.enableCompression();
    }

    @Override
    public void write(ByteBuf buf) {
        wstr(buf, this.uuid.toString());
        wstr(buf, this.name);
        this.client.setState(NetClient.NetState.PLAY);
    }
}