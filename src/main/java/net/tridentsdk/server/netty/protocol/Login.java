package net.tridentsdk.server.netty.protocol;

class Login implements ProtocolType {
    private In loginIn;
    private Out loginOut;

    public class In extends PacketManager {
        // TODO
    }

    public class Out extends PacketManager {
        // TODO
    }

    public PacketManager getIn() {
        return loginIn;
    }

    public PacketManager getOut() {
        return loginOut;
    }
}