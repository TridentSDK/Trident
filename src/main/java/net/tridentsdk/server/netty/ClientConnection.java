/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package net.tridentsdk.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.protocol.Protocol;
import net.tridentsdk.server.threads.PlayerThreads;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.security.KeyPair;
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
    /**
     * Map of client connections registered
     */
    protected static final Map<InetSocketAddress, AtomicReference<ClientConnection>> clientData =
            new ConcurrentHashMap<>();

    /**
     * Random for generating the verification token
     */
    protected static final SecureRandom SR = new SecureRandom();
    /**
     * The RSA cipher used to encrypt client data
     */
    protected static final Cipher cipher = ClientConnection.getCipher();

    /* Network fields */
    /**
     * The client's connection address
     */
    protected InetSocketAddress address;
    /**
     * The data channel
     */
    protected Channel channel;

    /* Encryption and client data fields */
    /**
     * The login key pair
     */
    protected volatile KeyPair loginKeyPair;
    /**
     * The client stage during login
     */
    protected volatile Protocol.ClientStage stage;
    /**
     * Whether or not encryption is enabled for the client
     */
    protected volatile boolean encryptionEnabled;
    /**
     * The secret key shared between the client and server
     */
    protected volatile SecretKey sharedSecret;
    /**
     * The verification token
     */
    protected volatile byte[] verificationToken; // DO NOT WRITE INDIVIDUAL ELEMENTS TO IT. Consult AgentTroll
    /**
     * Encryption IV specification
     */
    private IvParameterSpec ivSpec;

    /**
     * Creates a new connection handler for the joining channel stream
     */
    protected ClientConnection(Channel channel) {
        this.address = (InetSocketAddress) channel.remoteAddress();
        this.channel = channel;
        this.encryptionEnabled = false;
        this.stage = Protocol.ClientStage.HANDSHAKE;
    }

    protected ClientConnection() {
    }

    private static Cipher getCipher() {
        try {
            return Cipher.getInstance("AES/CFB8/NoPadding");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

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
     * @return the instance of the client handler associated with the IP, or {@code null} if not registered
     */
    public static ClientConnection getConnection(InetSocketAddress address) {
        // Get the connection reference
        AtomicReference<ClientConnection> reference = ClientConnection.clientData.get(address);

        // return null if connection is not found
        if (reference == null) {
            return null;
        }

        // return found connection
        return reference.get();
    }

    /**
     * Gets the connection of a channel handler context
     *
     * @param chx the context of which to find the client from
     * @return the client connection given the handler context, or {@code null} if not registered
     */
    public static ClientConnection getConnection(ChannelHandlerContext chx) {
        return ClientConnection.getConnection((InetSocketAddress) chx.channel().remoteAddress());
    }

    /**
     * Registers the client channel with a protocol connection wrapper
     *
     * @param channel the channel of which the player is connected by
     * @return the client connection that was registered
     */
    public static ClientConnection registerConnection(Channel channel) {
        // Make a new instance of ClientConnection
        ClientConnection newConnection = new ClientConnection(channel);

        // Register data and return the new instance
        ClientConnection.clientData.put(newConnection.getAddress(), new AtomicReference<>(newConnection));
        return newConnection;
    }

    /**
     * Sends protocol data through the client stream
     *
     * @param packet the packet to send, encoded and written to the stream
     */
    public void sendPacket(Packet packet) {
        System.out.println("Sending Packet: " + packet.getClass().getSimpleName());

        // Create new ByteBuf
        ByteBuf buffer = this.channel.alloc().buffer();

        Codec.writeVarInt32(buffer, packet.getId());
        packet.encode(buffer);

        // Write the packet and flush it
        this.channel.write(buffer);
        this.channel.flush();
    }

    /**
     * Encrypts the given {@code byte} data
     *
     * @param data the data to encrypt
     * @return the encrypted data
     * @throws Exception if something wrong occurs
     */
    public byte[] encrypt(byte... data) throws Exception {
        ClientConnection.cipher.init(Cipher.ENCRYPT_MODE, this.sharedSecret, this.ivSpec);

        return ClientConnection.cipher.doFinal(data);
    }

    /**
     * Decrypts the given {@code byte} encryption data
     *
     * @param data the data to decrypt
     * @return the decrypted data
     * @throws Exception if something wrong occurs
     */
    public byte[] decrypt(byte... data) throws Exception {
        ClientConnection.cipher.init(Cipher.DECRYPT_MODE, this.sharedSecret, this.ivSpec);

        return ClientConnection.cipher.doFinal(data);
    }

    /**
     * Generates the client token and stores it in the {@link #verificationToken}
     */
    public void generateToken() {
        byte[] localToken = new byte[4];
        ClientConnection.SR.nextBytes(localToken);
        this.verificationToken = localToken;
    }

    /**
     * Enables client data encryption
     *
     * @param secret the client secret to encrypt data with
     */
    public void enableEncryption(byte... secret) {
        //Makes sure the secret is only set once
        if (!this.encryptionEnabled) {
            this.sharedSecret = new SecretKeySpec(secret, "AES");
            this.ivSpec = new IvParameterSpec(this.sharedSecret.getEncoded());
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

    /**
     * Sets the client state, should only be used by the ClientConnectionHandler
     *
     * @param stage the state to set the client to
     */
    public void setStage(Protocol.ClientStage stage) {
        this.stage = stage;
    }

    /**
     * Gets the client verification token
     *
     * @return the token of which to verify the client
     */
    public byte[] getVerificationToken() {
        return this.verificationToken;
    }

    /**
     * Whether or not encryption is enabled
     *
     * @return {@code true} if encryption is enabled, {@code false} if it is not
     */
    public boolean isEncryptionEnabled() {
        return this.encryptionEnabled;
    }

    /**
     * Gets the key pair for client login
     *
     * @return the {@link java.security.KeyPair} for the client
     */
    public KeyPair getLoginKeyPair() {
        return this.loginKeyPair;
    }

    /**
     * Sets the client login key pair
     *
     * @param keyPair the key pair used by the client
     */
    public void setLoginKeyPair(KeyPair keyPair) {
        this.loginKeyPair = keyPair;
    }

    /**
     * The protocol login encryption secret
     *
     * @return the {@link javax.crypto.SecretKey} shared between the server and client
     */
    public SecretKey getSharedSecret() {
        return this.sharedSecret;
    }

    /**
     * Removes the client's server side client handler
     */
    public void logout() {
        ClientConnection.clientData.remove(this.address);
        PlayerThreads.remove(this);
        this.channel.close();
    }
}
