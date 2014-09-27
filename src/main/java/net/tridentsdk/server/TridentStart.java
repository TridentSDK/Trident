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

import com.google.common.collect.Lists;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import joptsimple.*;
import net.tridentsdk.api.config.JsonConfig;
import net.tridentsdk.api.util.TridentLogger;
import net.tridentsdk.server.netty.ClientChannelInitializer;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Server class that starts the connection listener. <p/> <p>Despite the fact that this class is under protected access,
 * it is documented anyways because of its significance in the server</p>
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
final class TridentStart {
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static final Logger LOGGER = new TridentLogger();

    private TridentStart() {
    } // Do not initialize

    /**
     * Starts the server up when the jarfile is run
     *
     * @param args the command line arguments
     */
    public static void main(String... args) throws Exception {
        /*TODO:
         check some args here, using an interpreter
         parse the configuration file
         create the server from the args/config values
         */

        OptionParser parser = new OptionParser();
        parser.acceptsAll(TridentStart.asList("h", "help"), "Show this help dialog.").forHelp();
        OptionSpec<Boolean> append =
                parser.acceptsAll(TridentStart.asList("log-append"), "Whether to append to the log file")
                      .withRequiredArg()
                      .ofType(Boolean.class)
                      .defaultsTo(true)
                      .describedAs("Log append");
        OptionSpec<File> properties =
                parser.acceptsAll(TridentStart.asList("properties"), "The location for the properties file")
                      .withRequiredArg()
                      .ofType(File.class)
                      .defaultsTo(new File("server.json"))
                      .describedAs("Properties file");
        OptionSet options;

        try {
            options = parser.parse(args);
        } catch (OptionException ex) {
            ex.printStackTrace();
            return;
        }

        File f;
        if (!(f = properties.value(options)).exists()) {
            InputStream link = TridentServer.class.getResourceAsStream("/server.json");
            Files.copy(link, f.getAbsoluteFile().toPath());
        }

        TridentStart.init(new JsonConfig(f));
    }

    private static Collection<String> asList(String... params) {
        return Lists.newArrayList(params);
    }

    /**
     * Initializes the server with the configuration file
     *
     * @param config the configuration to use for option lookup
     */
    private static void init(JsonConfig config) {
        //TODO: Need to run on seperate thread?
        //Server should read all settings from the loaded config
        final ConcurrentTaskExecutor<?> taskExecutor = new ConcurrentTaskExecutor<>(1);
        final JsonConfig innerConfig = config;
        taskExecutor.getScaledThread().addTask(new Runnable() {
            @Override public void run() {
                TridentServer.createServer(innerConfig, taskExecutor);
            }
        });

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(TridentStart.bossGroup, TridentStart.workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ClientChannelInitializer())
             .option(ChannelOption.TCP_NODELAY, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(
                    new InetSocketAddress(config.getString("address", "127.0.0.1"),
                                          config.getInt("port", 25565)))
                               .sync();

            // Wait until the server socket is closed, to gracefully shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //This exception is caught if server is closed.
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TridentServer.getInstance().shutdown();
        }
    }

    /**
     * Shuts down the server by closing the backed event loops
     */
    public static void close() {
        //Correct way to close the socket and shut down the server
        TridentStart.workerGroup.shutdownGracefully().awaitUninterruptibly();
        TridentStart.bossGroup.shutdownGracefully().awaitUninterruptibly();
    }
}
