package net.tridentsdk.server.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.protocol.Protocol;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class ClientConnection {

    // TODO: Find a more efficient way to do this
    private volatile static ConcurrentHashMap<InetSocketAddress, ClientConnection> clientData =
            new ConcurrentHashMap<>();

    private InetSocketAddress    address;
    private Channel              channel;
    private Protocol.ClientStage stage;

    public ClientConnection(ChannelHandlerContext channelContext) {
        address = (InetSocketAddress) channelContext.channel().remoteAddress();
        this.channel = channelContext.channel();

        clientData.put(address, this);
    }

    public void sendPacket(Packet packet) {
        // TODO: Larger procedure is most probably required
        channel.writeAndFlush(packet);

        // In case Channel state changes lets update the HashMap
        clientData.put(address, this);
    }

    public void setStage(Protocol.ClientStage stage) {
        this.stage = stage;

        clientData.put(address, this);
    }

    public Channel getChannel() {
        return channel;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public Protocol.ClientStage getStage() {
        return stage;
    }

    public void logout() {
        // TODO
        clientData.remove(address);
        channel.close();
    }

    public static boolean isLoggedIn(InetSocketAddress address) {
        return clientData.containsKey(address);
    }

    public static ClientConnection getConnection(InetSocketAddress address) {
        return clientData.get(address);
    }
}
