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

package net.tridentsdk.server;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class CTXProper implements ChannelHandlerContext {
    @Override public Channel channel() {
        return new Channel() {
            @Override public ChannelId id() {
                return null;
            }

            @Override public EventLoop eventLoop() {
                return null;
            }

            @Override public Channel parent() {
                return null;
            }

            @Override public ChannelConfig config() {
                return null;
            }

            @Override public boolean isOpen() {
                return false;
            }

            @Override public boolean isRegistered() {
                return false;
            }

            @Override public boolean isActive() {
                return false;
            }

            @Override public ChannelMetadata metadata() {
                return null;
            }

            @Override public SocketAddress localAddress() {
                return null;
            }

            @Override public SocketAddress remoteAddress() {
                return new InetSocketAddress(69);
            }

            @Override public ChannelFuture closeFuture() {
                return null;
            }

            @Override public boolean isWritable() {
                return false;
            }

            @Override public Unsafe unsafe() {
                return null;
            }

            @Override public ChannelPipeline pipeline() {
                return null;
            }

            @Override public ByteBufAllocator alloc() {
                return null;
            }

            @Override public ChannelPromise newPromise() {
                return null;
            }

            @Override public ChannelProgressivePromise newProgressivePromise() {
                return null;
            }

            @Override public ChannelFuture newSucceededFuture() {
                return null;
            }

            @Override public ChannelFuture newFailedFuture(Throwable throwable) {
                return null;
            }

            @Override public ChannelPromise voidPromise() {
                return null;
            }

            @Override public ChannelFuture bind(SocketAddress socketAddress) {
                return null;
            }

            @Override public ChannelFuture connect(SocketAddress socketAddress) {
                return null;
            }

            @Override public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress2) {
                return null;
            }

            @Override public ChannelFuture disconnect() {
                return null;
            }

            @Override public ChannelFuture close() {
                return null;
            }

            @Override public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise channelPromise) {
                return null;
            }

            @Override public ChannelFuture connect(SocketAddress socketAddress, ChannelPromise channelPromise) {
                return null;
            }

            @Override
            public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress2,
                                         ChannelPromise channelPromise) {
                return null;
            }

            @Override public ChannelFuture disconnect(ChannelPromise channelPromise) {
                return null;
            }

            @Override public ChannelFuture close(ChannelPromise channelPromise) {
                return null;
            }

            @Override public Channel read() {
                return null;
            }

            @Override public ChannelFuture write(Object o) {
                return null;
            }

            @Override public ChannelFuture write(Object o, ChannelPromise channelPromise) {
                return null;
            }

            @Override public Channel flush() {
                return null;
            }

            @Override public ChannelFuture writeAndFlush(Object o, ChannelPromise channelPromise) {
                return null;
            }

            @Override public ChannelFuture writeAndFlush(Object o) {
                return null;
            }

            @Override public <T> Attribute<T> attr(AttributeKey<T> tAttributeKey) {
                return null;
            }

            @Override public int compareTo(Channel channel) {
                return 0;
            }
        };
    }

    @Override public EventExecutor executor() {
        return null;
    }

    @Override public ChannelHandlerInvoker invoker() {
        return null;
    }

    @Override public String name() {
        return null;
    }

    @Override public ChannelHandler handler() {
        return null;
    }

    @Override public boolean isRemoved() {
        return false;
    }

    @Override public ChannelHandlerContext fireChannelRegistered() {
        return null;
    }

    @Override public ChannelHandlerContext fireChannelActive() {
        return null;
    }

    @Override public ChannelHandlerContext fireChannelInactive() {
        return null;
    }

    @Override public ChannelHandlerContext fireExceptionCaught(Throwable throwable) {
        return null;
    }

    @Override public ChannelHandlerContext fireUserEventTriggered(Object o) {
        return null;
    }

    @Override public ChannelHandlerContext fireChannelRead(Object o) {
        return null;
    }

    @Override public ChannelHandlerContext fireChannelReadComplete() {
        return null;
    }

    @Override public ChannelHandlerContext fireChannelWritabilityChanged() {
        return null;
    }

    @Override public ChannelFuture bind(SocketAddress socketAddress) {
        return null;
    }

    @Override public ChannelFuture connect(SocketAddress socketAddress) {
        return null;
    }

    @Override public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress2) {
        return null;
    }

    @Override public ChannelFuture disconnect() {
        return null;
    }

    @Override public ChannelFuture close() {
        return null;
    }

    @Override public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise channelPromise) {
        return null;
    }

    @Override public ChannelFuture connect(SocketAddress socketAddress, ChannelPromise channelPromise) {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress2,
                                 ChannelPromise channelPromise) {
        return null;
    }

    @Override public ChannelFuture disconnect(ChannelPromise channelPromise) {
        return null;
    }

    @Override public ChannelFuture close(ChannelPromise channelPromise) {
        return null;
    }

    @Override public ChannelHandlerContext read() {
        return null;
    }

    @Override public ChannelFuture write(Object o) {
        return null;
    }

    @Override public ChannelFuture write(Object o, ChannelPromise channelPromise) {
        return null;
    }

    @Override public ChannelHandlerContext flush() {
        return null;
    }

    @Override public ChannelFuture writeAndFlush(Object o, ChannelPromise channelPromise) {
        return null;
    }

    @Override public ChannelFuture writeAndFlush(Object o) {
        return null;
    }

    @Override public ChannelPipeline pipeline() {
        return null;
    }

    @Override public ByteBufAllocator alloc() {
        return null;
    }

    @Override public ChannelPromise newPromise() {
        return null;
    }

    @Override public ChannelProgressivePromise newProgressivePromise() {
        return null;
    }

    @Override public ChannelFuture newSucceededFuture() {
        return null;
    }

    @Override public ChannelFuture newFailedFuture(Throwable throwable) {
        return null;
    }

    @Override public ChannelPromise voidPromise() {
        return null;
    }

    @Override public <T> Attribute<T> attr(AttributeKey<T> tAttributeKey) {
        return null;
    }
}
