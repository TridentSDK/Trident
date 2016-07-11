package net.tridentsdk.server.util;

import io.netty.buffer.ByteBuf;

public class BufferUtils {

    public static String debugBuffer(ByteBuf buf){
        return debugBuffer(buf, false);
    }

    public static String debugBuffer(ByteBuf buf, boolean decimal){
        int index = buf.readerIndex();
        int readableBytes = buf.readableBytes();
        String response = buf.getClass().getSimpleName() + "(" + readableBytes + "): [";

        for (int i = 0; i < readableBytes; i++) {
            if(i > 0){
                response += ", ";
            }

            byte b = buf.readByte();
            response +=  String.format("%02x", b);

            if(decimal){
                response += "(" + b + ")";
            }
        }

        buf.readerIndex(index);
        return response + "]";
    }

}
