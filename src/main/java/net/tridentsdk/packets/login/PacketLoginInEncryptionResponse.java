/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.packets.login;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.player.TridentPlayer;
import net.tridentsdk.server.encryption.RSA;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.*;
import net.tridentsdk.server.netty.protocol.Protocol;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Pattern;

public class PacketLoginInEncryptionResponse extends InPacket {
    /**
     * Gson instance
     */
    protected static final Gson GSON = new Gson();
    /**
     * Pattern used to format the UUID
     */
    protected static final Pattern idDash;

    static {
        idDash = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    }

    /**
     * Length of the secret key
     */
    protected short secretLength;
    /**
     * Length of the token
     */
    protected short tokenLength;

    /**
     * Secret token used as an AES encryption key (encrypted using login keypair)
     */
    protected byte[] encryptedSecret;
    /**
     * Login token (encrypted using the login keypair)
     */
    protected byte[] encryptedToken;

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.secretLength = (short) Codec.readVarInt32(buf);
        this.encryptedSecret = new byte[(int) this.secretLength];

        buf.readBytes(this.encryptedSecret);

        this.tokenLength = (short) Codec.readVarInt32(buf);
        this.encryptedToken = new byte[(int) this.tokenLength];

        buf.readBytes(this.encryptedToken);

        return this;
    }

    @Override
    public PacketType getType() {
        return PacketType.IN;
    }

    /**
     * Gets the length of the secret
     *
     * @return the secret length
     */
    public short getSecretLength() {
        return this.secretLength;
    }

    /**
     * Gets the length of the client token
     *
     * @return the token client length
     */
    public short getTokenLength() {
        return this.tokenLength;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // Decrypt and store the shared secret and token
        byte[] sharedSecret = null;
        byte[] token = null;

        try {
            sharedSecret = RSA.decrypt(this.encryptedSecret, connection.getLoginKeyPair().getPrivate());
            token = RSA.decrypt(this.encryptedToken, connection.getLoginKeyPair().getPrivate());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Check that we got the same verification token;
        if (!Arrays.equals(connection.getVerificationToken(), token)) {
            System.out.println("Client with IP " + connection.getAddress().getHostName() +
                               " has sent an invalid token!");

            connection.logout();
            return;
        }

        // Enable encryption from hereon out
        connection.enableEncryption(sharedSecret);

        String name = LoginManager.getInstance().getName(connection.getAddress());
        StringBuilder sb = new StringBuilder();

        try {
            // Contact Mojang's session servers, to finalize creating the session as well as get the client's UUID
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" +
                              URLEncoder.encode(name, "UTF-8") + "&serverId=" +
                              new BigInteger(HashGenerator.getHash(connection, sharedSecret)).toString(16));
            HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
            int code = c.getResponseCode();

            // If the code isn't 200 OK, logout and inform the client of so
            if (code != 200) {
                //TODO: No encryption
                connection.sendPacket(new PacketLoginOutDisconnect().setJsonMessage("Unable to create session"));

                connection.logout();
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }

            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();

            connection.logout();
            return;
        }

        // Read the JSON response
        SessionResponse response = PacketLoginInEncryptionResponse.GSON.fromJson(sb.toString(), SessionResponse.class);
        PacketLoginOutSuccess packet = new PacketLoginOutSuccess();

        //Replaces the '-' less UUID from session server, with the required '-' filled UUID
        packet.set("uuid", PacketLoginInEncryptionResponse.idDash.matcher(response.id).replaceAll("$1-$2-$3-$4-$5"));
        packet.set("username", response.name);

        // Send the client PacketLoginOutSuccess and set the new stage to PLAY
        connection.sendPacket(packet);
        connection.setStage(Protocol.ClientStage.PLAY);

        // Store the UUID to be used when spawning the player
        UUID id = UUID.fromString(packet.getUuid());

        // Remove stored information in LoginManager and spawn the player
        LoginManager.getInstance().finish(connection.getAddress());
        TridentPlayer.spawnPlayer(connection, id);
    }

    protected static final class HashGenerator {

        private HashGenerator() {}

        /**
         * Used to generate the hash for the serverId
         *
         * @param connection Connection of the client
         * @param secret     Scared secret
         * @return Generated Hash
         */
        static byte[] getHash(ClientConnection connection, byte... secret) throws Exception {
            byte[][] b = { secret, connection.getLoginKeyPair().getPublic().getEncoded() };
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            for (byte[] bytes : b) {
                digest.update(bytes);
            }

            return digest.digest();
        }
    }

    /**
     * Response received from the session server
     */
    public static class SessionResponse {
        /**
         * Id of the player
         */
        String id;
        /**
         * Name of the player
         */
        String name;
        /**
         * List or JsonArray of properties
         */
        List<Properties> properties;
        boolean legacy;

        public static class Properties {
            String name;
            String value;
            String signature;
        }
    }
}
