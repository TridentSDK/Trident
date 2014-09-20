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

package net.tridentsdk.server.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.tridentsdk.server.encryption.RSA;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.protocol.Protocol;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles the connection of a client upon joining
 *
 * @author The TridentSDK Team
 */
public class ClientConnection {
    protected static final Map<InetSocketAddress, AtomicReference<ClientConnection>> clientData =
            new ConcurrentHashMap<>();
    protected static final SecureRandom SR = new SecureRandom();
    protected static Cipher cipher;
    

    static {
        try {
            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* Network fields */
    protected InetSocketAddress address;
    protected Channel channel;

    /* Encryption and client data fields */
    protected volatile KeyPair loginKeyPair;
    protected volatile Protocol.ClientStage stage;
    protected volatile boolean encryptionEnabled;
    protected volatile SecretKey sharedSecret;
    private IvParameterSpec ivSpec;
    protected volatile byte[] verificationToken;

    /**
     * Creates a new connection handler for the joining channel stream
     *
     * @param channelContext the channel of the client joining
     */
    protected ClientConnection(ChannelHandlerContext channelContext) {
        this.address = (InetSocketAddress) channelContext.channel().remoteAddress();
        this.channel = channelContext.channel();
        this.encryptionEnabled = false;
        this.stage = Protocol.ClientStage.HANDSHAKE;
    }

    protected ClientConnection() {}


    /**
     * Checks if an IP address is logged into the server
     *
     * @param address the address to check if online
     * @return {@code true} if the IP is on the server, {@code false} if not
     */
    public static boolean isLoggedIn(InetSocketAddress address) {
        return ClientConnection.clientData.containsKey(address);
    }

    /**
     * Gets the connection by the IP address
     *
     * @param address the IP to lookup the connection handler
     * @return the instance of the client handler associated with the IP
     */
    public static ClientConnection getConnection(InetSocketAddress address) {
        // Get the connection reference
        AtomicReference<ClientConnection> reference = ClientConnection.clientData.get(address);

        // return null if connection is not found
        if (reference == null)
            return null;

        // return found connection
        return reference.get();
    }

    public static ClientConnection registerConnection(ChannelHandlerContext channelContext) {
        // Make a new instance of ClientConnection
        ClientConnection newConnection = new ClientConnection(channelContext);

        // Register data and return the new instance
        ClientConnection.clientData.put(newConnection.getAddress(), new AtomicReference<>(newConnection));
        return newConnection;
    }

    public void setLoginKeyPair(KeyPair keyPair) {
        this.loginKeyPair = keyPair;
    }

    /**
     * Sends protocol data through the client stream
     *
     * @param packet    the packet to send, encoded and written to the stream
     * @param encrypted if you wish for packet to be encrypted
     */
    public void sendPacket(Packet packet, boolean encrypted) {
        System.out.println("Sending Packet: " + packet.getClass().getSimpleName() + " Encrypted: " + encrypted);
        
        // Create new ByteBuf
        ByteBuf buffer = this.channel.alloc().buffer();

        // throw an IllegalArgumentException if encryption hasn't been enabled yet
        if (encrypted && !this.encryptionEnabled)
            throw new IllegalArgumentException("You can not use encryption if encryption is not enabled!");

        // Write the packet into the bytebuf
        try {
            if (encrypted) {
                ByteBuf decrypted = Unpooled.buffer();
                Codec.writeVarInt32(decrypted, packet.getId());
                packet.encode(decrypted);

                buffer.writeBytes(encrypt(Codec.toArray(decrypted)));
            } else {
                Codec.writeVarInt32(buffer, packet.getId());
                packet.encode(buffer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // Write the packet and flush it
        this.channel.write(buffer);
        this.channel.flush();
    }

    public void sendPacket(Packet packet) {
        this.sendPacket(packet, encryptionEnabled);
    }

    public byte[] encrypt(byte... data) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, sharedSecret, ivSpec);

        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte... data) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, sharedSecret, ivSpec);

        return cipher.doFinal(data);
    }

    public void generateToken() {
        verificationToken = new byte[4];
        SR.nextBytes(verificationToken);
    }

    public void enableEncryption(byte[] secret) {
        //Makes sure the secret is only set once
        if (!encryptionEnabled) {
            this.sharedSecret = new SecretKeySpec(secret, "AES");
            this.ivSpec = new IvParameterSpec(sharedSecret.getEncoded(), 0, 16);
            this.encryptionEnabled = true;
        }    
    }

    /**
     * Gets the channel context for the connection stream
     *
     * @return the netty channel wrapped by the handler
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * The IP address of the client handled by this connection wrapper
     *
     * @return the handled IP address
     */
    public InetSocketAddress getAddress() {
        return this.address;
    }

    /**
     * Gets the current state of the connection
     *
     * @return the current state of the protocol for the client
     */
    public Protocol.ClientStage getStage() {
        return this.stage;
    }

    public byte[] getVerificationToken() {
        return verificationToken;
    }

    /**
     * Sets the client state, should only be used by the ClientConnectionHandler
     *
     * @param stage the state to set the client to
     */
    public void setStage(Protocol.ClientStage stage) {
        this.stage = stage;
    }

    public boolean isEncryptionEnabled() {
        return this.encryptionEnabled;
    }

    public KeyPair getLoginKeyPair() {
        return this.loginKeyPair;
    }
    
    public SecretKey getSharedSecret() {
        return this.sharedSecret;
    }

    /**
     * Removes the client's server side client handler
     */
    public void logout() {
        ClientConnection.clientData.remove(this.address);

        this.channel.close();
    }
}
