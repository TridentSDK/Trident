/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
