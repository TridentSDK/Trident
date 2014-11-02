/*
 *     Trident - A Multithreaded Server Alternative
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
package net.tridentsdk.packets.login;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.player.TridentPlayer;
import net.tridentsdk.server.encryption.RSA;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.protocol.Protocol;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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

        // Enable encryption from hereon out
        connection.enableEncryption(sharedSecret);

        // Read the JSON response
        SessionResponse response = GSON.fromJson(sb.toString(), SessionResponse.class);
        PacketLoginOutSuccess packet = new PacketLoginOutSuccess();

        //Replaces the '-' less UUID from session server, with the required '-' filled UUID
        packet.set("uuid", idDash.matcher(response.id).replaceAll("$1-$2-$3-$4-$5"));
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

        private HashGenerator() {
        }

        /**
         * Used to generate the hash for the serverId
         *
         * @param connection Connection of the client
         * @param secret     Scared secret
         * @return Generated Hash
         */
        static byte[] getHash(ClientConnection connection, byte... secret) throws Exception {
            byte[][] b = {secret, connection.getLoginKeyPair().getPublic().getEncoded()};
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
