/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server.netty.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The channel handler that is placed into the netty connection bootstrap to process outbound and inbound messages
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class ClientConnectionHandler extends ChannelHandlerAdapter {
    // TODO: Store client object

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        // TODO: Handle data
    }
}
