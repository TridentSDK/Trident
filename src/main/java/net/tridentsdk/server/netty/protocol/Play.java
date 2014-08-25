package net.tridentsdk.server.netty.protocol;

class Play implements ProtocolType {
    private In playIn = new In();
    private Out playOut = new Out();

    public class In extends PacketManager {
        // TODO
    }

    public class Out extends PacketManager {
        // TODO
    }

    public PacketManager getIn() {
        return playIn;
    }

    public PacketManager getOut() {
        return playOut;
    }
}
