package net.tridentsdk.server.netty.protocol;

interface ProtocolType {
    public PacketManager getIn();

    public PacketManager getOut();
}
