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

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.concurrent.Immutable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static net.tridentsdk.server.net.NetData.arr;
import static net.tridentsdk.server.net.NetData.rvint;

/**
 * Response to {@link LoginOutEncryptionRequest} sent by
 * the client.
 */
@Immutable
public final class LoginInEncryptionResponse extends PacketIn {
    /**
     * Hex values
     */
    private static final String HEX = "0123456789abcdef";
    /**
     * The URL to confirm player joins
     */
    private static final String MOJANG_SERVER =
            "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s";

    public LoginInEncryptionResponse() {
        super(LoginInEncryptionResponse.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int secretLen = rvint(buf);
        byte[] encryptedSecret = arr(buf, secretLen);
        int tokenLen = rvint(buf);
        byte[] encryptedToken = arr(buf, tokenLen);

        byte[] sharedSecret;
        if ((sharedSecret = client.cryptoModule().begin(encryptedSecret, encryptedToken)) == null) {
            client.disconnect("Crypto error");
            return;
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        md.update(sharedSecret);
        md.update(client.cryptoModule().kp().getPublic().getEncoded());

        String hash = toHexStringTwosComplement(md.digest());
        Mojang.req(MOJANG_SERVER, client.name(), hash).callback((resp) -> {
            if (resp == null) {
                client.disconnect("Auth error");
                return;
            }

            JsonObject obj = resp.getAsJsonObject();
            String id = obj.get("id").getAsString();
            String name = obj.get("name").getAsString();
            String textures = obj
                    .get("properties").getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("value").getAsString();

            UUID uuid = Login.convert(name, id);
            LoginOutSuccess success = new LoginOutSuccess(client, uuid, name);
            client.sendPacket(success).addListener(future -> {
                TridentPlayer.spawn(client, name, uuid);
                Login.finish();
            });
        }).get();
    }

    /**
     * Obtains a signed hex value using two's complement.
     *
     * @param bys the bytes to convert
     * @return the hex string
     */
    // https://gist.github.com/unascribed/70e830d471d6a3272e3f#file-methodtwo-java
    private static String toHexStringTwosComplement(byte[] bys) {
        boolean negative = ((bys[0] & 0x80) != 0);
        if (negative) {
            boolean carry = true;
            for (int i = bys.length - 1; i >= 0; i--) {
                bys[i] = (byte) ((~bys[i]) & 0xFF);
                if (carry) {
                    carry = ((bys[i] & 0xFF) == 0xFF);
                    bys[i]++;
                }
            }
        }
        StringBuilder sb = new StringBuilder(bys.length * 2);
        if (negative) {
            sb.append("-");
        }
        boolean skipZeroes = true;
        for (byte by : bys) {
            if (by == 0) {
                if (!skipZeroes) {
                    sb.append("00");
                }
            } else if ((by & 0xF0) == 0) {
                if (!skipZeroes) {
                    sb.append("0");
                }
                sb.append(HEX.charAt(by & 0x0F));
                skipZeroes = false;
            } else {
                sb.append(HEX.charAt((by & 0xF0) >> 4));
                sb.append(HEX.charAt(by & 0x0F));
                skipZeroes = false;
            }
        }
        return sb.toString();
    }
}