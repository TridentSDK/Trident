package org.projectblueshift.server;

import io.netty.bootstrap.ServerBootstrap;
import org.projectblueshift.api.Server;

import java.net.InetSocketAddress;

public class BlueServer implements Server {

    private InetSocketAddress address;
    private ServerBootstrap bootstrap;

    public BlueServer(InetSocketAddress address) {
        this.address = address;
    }

    public InetSocketAddress getSocketAddress() {
        return address;
    }

    public short getPort() {
        return (short) address.getPort();
    }

    public void startup() {

    }

    public void shutdown() {

    }

}
