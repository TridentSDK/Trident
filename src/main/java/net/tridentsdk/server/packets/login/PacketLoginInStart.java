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

package net.tridentsdk.server.packets.login;

import com.google.gson.JsonArray;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketDirection;
import net.tridentsdk.server.netty.packet.RSA;
import net.tridentsdk.server.netty.protocol.Protocol;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.TridentLogger;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @author The TridentSDK Team
 */
public class PacketLoginInStart extends InPacket {
    private static final boolean ONLINE_MODE = TridentServer.instance().config().getBoolean("online-mode", true);

    /**
     * Username of the client to be verified
     */
    protected String name;

    @Override
    public int id() {
        return 0x00;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.name = Codec.readString(buf);

        return this;
    }

    @Override
    public PacketDirection direction() {
        return PacketDirection.IN;
    }

    /**
     * Gets the client name
     *
     * @return the client name
     */
    public String name() {
        return this.name;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        boolean allow = LoginHandler.getInstance().initLogin(connection.address(), this.name());
        if (!allow) {
            connection.sendPacket(new PacketLoginOutDisconnect().setJsonMessage("Server is full"));
            return;
        }

        /*
         * If the client is the local machine, skip the encryption process and proceed to the PLAY stage
         */
        if (connection.address().getHostString().equals("127.0.0.1") || !ONLINE_MODE) {
            UUID id;

            try {
                URL url = new URL("https://api.mojang.com/profiles/minecraft");
                HttpsURLConnection c = (HttpsURLConnection) url.openConnection();

                // add request header
                c.setRequestMethod("POST");
                c.setRequestProperty("User-Agent", "Mozilla/5.0");
                c.setRequestProperty("Content-Type", "application/json");
                c.setDoOutput(true);
                c.setDoInput(true);

                // write the payload
                c.getOutputStream().write(String.format("[ \"%s\" ]", name()).getBytes());
                c.getOutputStream().close();

                int responseCode = c.getResponseCode();

                // if the response isn't 200 OK, logout and inform the client of so
                if (responseCode != 200) {
                    connection.sendPacket(new PacketLoginOutDisconnect().setJsonMessage("Unable retrieve UUID"));
                    return;
                }

                /*
                 * Read the response
                 */
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }

                reader.close();

                // parse the response and set the ID
                JsonArray array = PacketLoginInEncryptionResponse.GSON.fromJson(sb.toString(), JsonArray.class);
                JsonArray jsonArray = array.getAsJsonArray();
                if (jsonArray.size() == 0) {
                    id = UUID.randomUUID();
                } else {
                    id = UUID.fromString(PacketLoginInEncryptionResponse.idDash.matcher(
                            jsonArray.get(0).getAsJsonObject().get("id").getAsString())
                            .replaceAll("$1-$2-$3-$4-$5"));
                }

                if (TridentPlayer.getPlayer(id) != null) {
                    connection.sendPacket(new PacketLoginOutDisconnect().setJsonMessage(
                            "Player has already logged in"));
                    return;
                }
            } catch (Exception e) {
                TridentLogger.get().error(e);
                return;
            }

            if (TridentServer.WORLD == null) {
                connection.sendPacket(new PacketLoginOutDisconnect()
                        .setJsonMessage("{\"text\":\"Disconnected: no world on server\"}"));
                TridentLogger.get().error("Rejected a client due to not having a map!");
                return;
            }

            LoginHandler.getInstance().finish(connection.address());
            PacketLoginOutSuccess success = new PacketLoginOutSuccess();

            // set values in the packet
            success.uuid = id.toString();
            success.username = name();
            success.connection = connection;

            // send the success packet and spawn the player
            connection.enableCompression();
            connection.sendPacket(success);
            connection.setStage(Protocol.ClientStage.PLAY);

            connection.setUuid(id);
            TridentPlayer.spawnPlayer(connection, id, name());
            return;
        }

        PacketLoginOutEncryptionRequest p = new PacketLoginOutEncryptionRequest();

        // Generate the 4 byte token and update the packet
        connection.generateToken();
        p.set("verifyToken", connection.verificationToken());

        try {
            /* Generate the 1024-bit encryption key specified for the client, only used during the LOGIN stage.
             * Note: A notchian Minecraft server will have one KeyPair for all clients during the LOGIN stage,
             * this is flawed as it won't be hard to decrypt the secret which is used as the key for all encryption
             * after LOGIN therefore generating a keypair for each client is much more secure
             */
            KeyPair pair = RSA.generate(1024);

            // Update the packet with the new key
            p.set("publicKey", pair.getPublic().getEncoded());
            connection.setLoginKeyPair(pair);
        } catch (NoSuchAlgorithmException ignored) {
        }

        // Send the packet to the client
        connection.sendPacket(p);
    }
}
