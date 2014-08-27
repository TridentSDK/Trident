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
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.threads.PlayerThreads;
import net.tridentsdk.server.threads.ThreadsManager;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/*
# Run progress: 0.00% complete, ETA 00:00:30
# Warmup: 10 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 10 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.TestPlayerThreads.autoBox
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7537 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile
.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 7.162 ns/op
# Warmup Iteration   2: 7.341 ns/op
# Warmup Iteration   3: 7.063 ns/op
# Warmup Iteration   4: 7.082 ns/op
# Warmup Iteration   5: 7.098 ns/op
# Warmup Iteration   6: 7.140 ns/op
# Warmup Iteration   7: 7.110 ns/op
# Warmup Iteration   8: 7.044 ns/op
# Warmup Iteration   9: 7.182 ns/op
# Warmup Iteration  10: 7.129 ns/op
Iteration   1: 6.986 ns/op
Iteration   2: 7.131 ns/op
Iteration   3: 7.120 ns/op
Iteration   4: 7.107 ns/op
Iteration   5: 7.221 ns/op

Result: 7.113 ±(99.9%) 0.323 ns/op [Average]
  Statistics: (min, avg, max) = (6.986, 7.113, 7.221), stdev = 0.084
  Confidence interval (99.9%): [6.790, 7.436]


# Run progress: 50.00% complete, ETA 00:00:18
# Warmup: 10 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 10 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.TestPlayerThreads.explicitBox
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7537 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile
.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 7.340 ns/op
# Warmup Iteration   2: 7.310 ns/op
# Warmup Iteration   3: 7.067 ns/op
# Warmup Iteration   4: 7.191 ns/op
# Warmup Iteration   5: 7.152 ns/op
# Warmup Iteration   6: 7.125 ns/op
# Warmup Iteration   7: 7.173 ns/op
# Warmup Iteration   8: 7.327 ns/op
# Warmup Iteration   9: 7.021 ns/op
# Warmup Iteration  10: 7.139 ns/op
Iteration   1: 7.196 ns/op
Iteration   2: 7.005 ns/op
Iteration   3: 7.087 ns/op
Iteration   4: 7.153 ns/op
Iteration   5: 7.044 ns/op

Result: 7.097 ±(99.9%) 0.301 ns/op [Average]
  Statistics: (min, avg, max) = (7.005, 7.097, 7.196), stdev = 0.078
  Confidence interval (99.9%): [6.796, 7.398]


# Run complete. Total time: 00:00:36

Benchmark                               Mode   Samples        Score  Score error    Units
n.t.s.TestPlayerThreads.autoBox         avgt         5        7.113        0.323    ns/op
n.t.s.TestPlayerThreads.explicitBox     avgt         5        7.097        0.301    ns/op
 */
public class TestPlayerThreads {
    public static final ClientConnection CLIENT_CONNECTION = new ClientConnection(new ChannelHandlerContext() {
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
    });

    /* @Benchmark public void explicitBox(Blackhole blackhole) {
        blackhole.consume(Integer.valueOf(69));
    }

    @Benchmark public void autoBox(Blackhole blackhole) {
        blackhole.consume((Object) 69);
    } */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + TestPlayerThreads.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(10)
                .measurementIterations(5)
                .forks(1)
                .threads(10)
                .build();

        new org.openjdk.jmh.runner.Runner(opt).run();
        ThreadsManager.stopAll();
    }

    @Benchmark public void put(Blackhole blackhole) {
        blackhole.consume(PlayerThreads.clientThreadHandle(TestPlayerThreads.CLIENT_CONNECTION));
    }

    @Benchmark public void remove(Blackhole blackhole) {
        PlayerThreads.remove(TestPlayerThreads.CLIENT_CONNECTION);
    }
}
