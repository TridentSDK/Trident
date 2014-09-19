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
import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.encryption.RSA;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.*;
import net.tridentsdk.server.netty.protocol.Protocol;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class PacketLoginInEncryptionResponse extends InPacket {
    private static final Gson GSON = new Gson();

    private short secretLength;
    private short tokenLength;

    private byte[] encryptedSecret;
    private byte[] encryptedToken;

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
        byte[] sharedSecret = null;
        byte[] token = null;
        try {
            sharedSecret = RSA.decrypt(encryptedSecret, connection.getLoginKeyPair().getPrivate());
            token = RSA.decrypt(encryptedToken, connection.getLoginKeyPair().getPrivate());
        } catch (Exception e) {
         // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //Check that we got the same verification token;
        if(!(Arrays.equals(connection.getVerificationToken(), token))) {
            System.out.println("Client with IP " + connection.getAddress().getHostName() +
                    " has sent an invalid token!");

            connection.logout();
            return;
        }
        
        connection.enableEncryption(sharedSecret);

        try{
            KeyFactory factory = KeyFactory.getInstance("RSA");
            EncodedKeySpec spec = new X509EncodedKeySpec(sharedSecret);

        }catch(NoSuchAlgorithmException ignored) {}

        String name = LoginManager.getInstance().getName(connection.getAddress());
        
        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" +
                    URLEncoder.encode(name, "UTF-8") + "&serverId=" +
                    new BigInteger(HashGenerator.getHash(connection, sharedSecret)).toString(16));
            HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
            
            int code = c.getResponseCode();
            if (code != 200) {
                //TODO: If session servers are down... or?
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
            
            String line;
            while((line = reader.readLine()) != null) {
                System.out.println("LINE: " + line);
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
        }catch(Exception ex) {
            ex.printStackTrace();

            connection.logout();
            return;
        }
        
        SessionResponse response = GSON.fromJson(sb.toString(), SessionResponse.class);
        //TODO: Generate the PlayerConnection object
        
        //TODO:
        PacketLoginOutSuccess packet = new PacketLoginOutSuccess();
        packet.set("uuid", response.id);
        packet.set("username", response.name);
        

        connection.sendPacket(packet);
        connection.setStage(Protocol.ClientStage.PLAY);
        LoginManager.getInstance().finish(connection.getAddress());
    }

    private static class HashGenerator {

        private static char[] hexArray = "0123456789ABCDEF".toCharArray();

        static byte[] getHash(ClientConnection connection, byte[] secret) throws Exception {
            /*byte[][] b = {getHex(name).getBytes("ISO_8859_1"), secret,
                    connection.getLoginKeyPair().getPublic().getEncoded()};*/
            byte[][] b = {secret, connection.getLoginKeyPair().getPublic().getEncoded()};
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            for(byte[] bytes : b) {
                digest.update(bytes);
            }

            return digest.digest();
        }
        
        //Currently unneeded
        /*private static String getHex(String data) {
            MessageDigest digest = null;

            try {
                digest = MessageDigest.getInstance("SHA-1");
                digest.reset();
                digest.update(data.getBytes("UTF-8"));
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            byte[] hash = digest.digest();
            boolean negative = (hash[0] & 0x80) == 0x80;

            if (negative)
                hash = twosCompliment(hash);

            String digests = getHexString(hash);

            if (digests.startsWith("0")) {
                digests = digests.replaceFirst("0", digests);
            }

            if (negative) {
                digests = "-" + digests;
            }

            digests = digests.toLowerCase();
            return digests;
        }

        private static String getHexString(byte[] bytes) {
            char[] hexChars = new char[bytes.length * 2];
            int v;

            for (int j = 0; j < bytes.length; j++ ) {
                v = bytes[j] & 0xFF;

                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }

            return new String(hexChars);
        }

        private static byte[] twosCompliment(byte[] p) {
            int i;
            boolean carry = true;

            for (i = p.length - 1; i >= 0; i--) {
                p[i] = (byte)~p[i];

                if (carry) {
                    carry = p[i] == 0xFF;
                    p[i]++;
                }
            }

            return p;
        }*/
    }
    
    public static class SessionResponse {
        public static class Properties {
            String name;
            String value;
        }
        //The UUID of the player
        String id;
        String name;
        Properties properties;
    }
}
