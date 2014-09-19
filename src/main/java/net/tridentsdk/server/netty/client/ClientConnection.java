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
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.tridentsdk.server.encryption.RSA;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.protocol.Protocol;

import java.net.InetSocketAddress;
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

    protected InetSocketAddress address;
    protected Channel channel;

    protected volatile PublicKey publicKey;
    protected volatile Protocol.ClientStage stage;
    protected volatile boolean encryptionEnabled;
    protected volatile PrivateKey privateKey;
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
        AtomicReference<ClientConnection> reference = ClientConnection.clientData.get(address);
        if (reference == null)
            return null;
        return reference.get();
    }

    public static ClientConnection registerConnection(ChannelHandlerContext channelContext) {
        ClientConnection newConnection = new ClientConnection(channelContext);

        ClientConnection.clientData.put(newConnection.getAddress(), new AtomicReference<>(newConnection));
        return newConnection;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Sends protocol data through the client stream
     *
     * @param packet    the packet to send, encoded and written to the stream
     * @param encrypted if you wish for packet to be encrypted
     */
    public void sendPacket(Packet packet, boolean encrypted) {
        // Create new ByteBuf
        ByteBuf buffer = this.channel.alloc().buffer();

        if (encrypted && !this.encryptionEnabled)
            throw new IllegalArgumentException("You can not use encryption if encryption is not enabled!");

        System.out.println("Sending packet: " + packet.getClass().getSimpleName());

        try {
            if (encrypted) {
                //TODO: Write as VarInt
                buffer.writeBytes(RSA.encrypt((byte) packet.getId(), this.publicKey));

                packet.encode(buffer);
                buffer.writeBytes(this.encrypt(buffer.array()));
            } else {
                System.out.println("UNENCRYPTED");
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
        return RSA.encrypt(data, this.publicKey);
    }

    public byte[] decrypt(byte... data) throws Exception {
        return RSA.decrypt(data, this.privateKey);
    }

    public void generateToken() {
        verificationToken = new byte[4];
        SR.nextBytes(verificationToken);
    }

    public void enableEncryption(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.encryptionEnabled = true;
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

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * Removes the client's server side client handler
     */
    public void logout() {
        ClientConnection.clientData.remove(this.address);

        this.channel.close();
    }
}
