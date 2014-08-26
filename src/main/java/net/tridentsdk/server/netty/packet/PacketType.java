package net.tridentsdk.server.netty.packet;

public enum PacketType {
    /**
     * For packets which are received from the client
     */
    IN,

    /**
     * For packets which are sent from the server
     */
    OUT
}
