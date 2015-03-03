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

package net.tridentsdk.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.tridentsdk.concurrent.ConcurrentCache;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.protocol.Protocol;
import net.tridentsdk.server.packets.login.PacketLoginOutSetCompression;
import net.tridentsdk.server.packets.play.out.PacketPlayOutDisconnect;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.util.TridentLogger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.concurrent.Callable;

/**
 * Handles the connection of a client upon joining
 *
 * @author The TridentSDK Team
 */
public class ClientConnection {
    /**
     * Map of client connections registered
     */
    protected static final ConcurrentCache<InetSocketAddress, ClientConnection> clientData = ConcurrentCache.create();

    /**
     * Random for generating the verification token
     */
    protected static final SecureRandom SR = new SecureRandom();
    /**
     * The RSA cipher used to encrypt client data
     */
    protected static final Cipher cipher = getCipher();

    /* Network fields */
    private static final Callable<ClientConnection> NULL_CALLABLE = new Callable<ClientConnection>() {
        @Override
        public ClientConnection call() throws Exception {
            return null;
        }
    };
    private final Object BARRIER;

    /* Encryption and client data fields */
    /**
     * The client's connection address
     */
    protected InetSocketAddress address;
    /**
     * The data channel
     */
    protected Channel channel;
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
     * Whether or not encryption is enabled
     */
    protected volatile boolean compressionEnabled = false;
    private IvParameterSpec ivSpec;

    /**
     * Creates a new connection handler for the joining channel stream
     */
    protected ClientConnection(Channel channel) {
        BARRIER = new Object();
        this.address = (InetSocketAddress) channel.remoteAddress();
        this.channel = channel;
        this.encryptionEnabled = false;
        this.stage = Protocol.ClientStage.HANDSHAKE;
    }

    protected ClientConnection() {
        BARRIER = new Object();
    }

    private static Cipher getCipher() {
        try {
            return Cipher.getInstance("AES/CFB8/NoPadding");
        } catch (Exception ex) {
            TridentLogger.error(ex);
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
        return clientData.keys().contains(address);
    }

    /**
     * Gets the connection by the IP address
     *
     * @param address the IP to lookup the connection handler
     * @return the instance of the client handler associated with the IP, or {@code null} if not registered
     */
    public static ClientConnection getConnection(InetSocketAddress address) {
        return clientData.retrieve(address, NULL_CALLABLE);
    }

    /**
     * Gets the connection of a channel handler context
     *
     * @param chx the context of which to find the client from
     * @return the client connection given the handler context, or {@code null} if not registered
     */
    public static ClientConnection connection(ChannelHandlerContext chx) {
        return getConnection((InetSocketAddress) chx.channel().remoteAddress());
    }

    /**
     * Registers the client channel with a protocol connection wrapper
     *
     * @param channel the channel of which the player is connected by
     * @return the client connection that was registered
     */
    public static ClientConnection registerConnection(final Channel channel) {
        return clientData.retrieve((InetSocketAddress) channel.remoteAddress(), new Callable<ClientConnection>() {
            @Override
            public ClientConnection call() throws Exception {
                return new ClientConnection(channel);
            }
        });
    }

    /**
     * Sends protocol data through the client stream
     *
     * @param packet the packet to send, encoded and written to the stream
     */
    public void sendPacket(Packet packet) {
        // Create new ByteBuf
        ByteBuf buffer = this.channel.alloc().buffer();

        TridentLogger.log("Sent " + packet.getClass().getSimpleName());

        Codec.writeVarInt32(buffer, packet.id());
        packet.encode(buffer);

        // Write the packet and flush it
        this.channel.write(buffer);
        this.channel.flush();

        if (packet instanceof PacketPlayOutDisconnect) {
            logout();
        }
    }

    /**
     * Encrypts the given {@code byte} data
     *
     * @param data the data to encrypt
     * @return the encrypted data
     * @throws Exception if something wrong occurs
     */
    public byte[] encrypt(byte... data) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, this.sharedSecret, this.ivSpec);

        return cipher.doFinal(data);
    }

    /**
     * Decrypts the given {@code byte} encryption data
     *
     * @param data the data to decrypt
     * @return the decrypted data
     * @throws Exception if something wrong occurs
     */
    public byte[] decrypt(byte... data) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, this.sharedSecret, this.ivSpec);

        return cipher.doFinal(data);
    }

    /**
     * Generates the client token and stores it in the {@link #verificationToken}
     */
    public void generateToken() {
        byte[] localToken = new byte[4];
        SR.nextBytes(localToken);
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
     * Allows compression on the server and client
     */
    public void enableCompression() {
        if (compressionEnabled) {
            TridentLogger.error(new UnsupportedOperationException("Compression is already enabled!"));
        }

        if (stage != Protocol.ClientStage.LOGIN) {
            TridentLogger.error(new UnsupportedOperationException());
        }

        sendPacket(new PacketLoginOutSetCompression());
        compressionEnabled = true;
    }

    /**
     * Gets the channel context for the connection stream
     *
     * @return the netty channel wrapped by the handler
     */
    public Channel channel() {
        return this.channel;
    }

    /**
     * The IP address of the client handled by this connection wrapper
     *
     * @return the handled IP address
     */
    public InetSocketAddress address() {
        return this.address;
    }

    /**
     * Gets the current state of the connection
     *
     * @return the current state of the protocol for the client
     */
    public Protocol.ClientStage stage() {
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
    public byte[] verificationToken() {
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

    public boolean isCompressionEnabled() {
        return this.compressionEnabled;
    }

    /**
     * Gets the key pair for client login
     *
     * @return the {@link java.security.KeyPair} for the client
     */
    public KeyPair loginKeyPair() {
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
    public SecretKey sharedSecret() {
        return this.sharedSecret;
    }

    /**
     * Removes the client's server side client handler
     */
    public void logout() {
        if (this instanceof PlayerConnection) {
            TridentPlayer player = ((PlayerConnection) this).player();
            ThreadsHandler.remove(player);
            player.remove();
        }


        clientData.remove(this.address);
        this.channel.close();
    }
}
