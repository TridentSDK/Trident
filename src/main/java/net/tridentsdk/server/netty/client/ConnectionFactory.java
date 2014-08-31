package net.tridentsdk.server.netty.client;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicReference;

public final class ConnectionFactory {

    public static ClientConnection registerConnection(ChannelHandlerContext channelContext) {
        ClientConnection newConnection = new ClientConnection(channelContext);

        ClientConnection.clientData.put(newConnection.getAddress(), new AtomicReference<>(newConnection));
        return newConnection;
    }
}
