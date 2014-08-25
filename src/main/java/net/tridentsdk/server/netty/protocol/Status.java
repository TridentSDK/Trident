package net.tridentsdk.server.netty.protocol;

class Status implements ProtocolType {
    private In statusIn = new In();
    private Out statusOut  = new Out();

    public class In extends PacketManager {
        // TODO
    }

    public class Out extends PacketManager {
        // TODO
    }

    public PacketManager getIn() {
        return statusIn;
    }

    public PacketManager getOut() {
        return statusOut;
    }
}