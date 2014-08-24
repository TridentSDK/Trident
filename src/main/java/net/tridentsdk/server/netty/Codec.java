package net.tridentsdk.server.netty;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;

public class Codec {
	
	public static String readString(ByteBuf buf) {
		//Reads the length of the string
		int length = readVarInt32(buf);
		byte[] bytes = new byte[length];
		buf.readBytes(bytes);

		//Current charset used by strings is UFT_8
		return new String(bytes, Charsets.UTF_8);
		
	}
	
	public static int readVarInt32(ByteBuf buf) {
		//The result we will return
		int result = 0;

		//How much to indent the current bytes
		int indent = 0;
		int b = buf.readByte();

		//If below, it means there are more bytes
		while ((b & 0b10000000) == 0b10000000) {
			Preconditions.checkArgument(indent < 21, "Too many bytes for a VarInt32.");
			
			//Adds the byte in the appropriate position (first byte goes last, etc.)
			result += (b & 0b01111111) << indent;
			indent +=7;
			
			//Reads the next byte
			b = buf.readByte();
			
		}

		return result += (b & 0b01111111) << indent;
	}
	
	public static long readVarInt64(ByteBuf buf) {
		//The result we will return
		long result = 0;

		//How much to indent the current bytes
		int indent = 0;
		long b = buf.readByte();

		//If below, it means there are more bytes
		while ((b & 0b10000000) == 0b10000000) {
			Preconditions.checkArgument(indent < 49, "Too many bytes for a VarInt64.");
			
			//Adds the byte in the apprioriate position (first byte goes last, etc.)
			result += (b & 0b01111111) << indent;
			indent +=7;
			
			//Reads the next byte
			b = buf.readByte();
			
		}

		return result += (b & 0b01111111) << indent;
	}
}
