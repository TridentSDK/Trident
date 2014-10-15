/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server;

import static com.google.common.collect.Lists.newArrayList;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.tridentsdk.Defaults;
import net.tridentsdk.api.config.JsonConfig;
import net.tridentsdk.server.netty.ClientChannelInitializer;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(TridentStart.class);

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

        TridentStart.LOGGER.info("Open source software by TridentSDK - https://github.com/TridentSDK");
        TridentStart.LOGGER.info("Starting Trident server");

        TridentStart.LOGGER.info("Creating handlers...");
        OptionParser parser = new OptionParser();
        parser.acceptsAll(newArrayList("h", "help"), "Show this help dialog.").forHelp();
        OptionSpec<Boolean> append =
                parser.acceptsAll(newArrayList("log-append"), "Whether to append to the log file")
                        .withRequiredArg()
                        .ofType(Boolean.class)
                        .defaultsTo(true)
                        .describedAs("Log append");
        TridentStart.LOGGER.info("Parsing server properties, using server.json...");
        OptionSpec<File> properties =
                parser.acceptsAll(newArrayList("properties"), "The location for the properties file")
                        .withRequiredArg()
                        .ofType(File.class)
                        .defaultsTo(new File("server.json"))
                        .describedAs("Properties file");

        TridentStart.LOGGER.info("Parsing command line arguments...");
        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException ex) {
            ex.printStackTrace();
            return;
        }

        TridentStart.LOGGER.info("Looking for server properties...");
        File f;
        if (!(f = properties.value(options)).exists()) {
            TridentStart.LOGGER.info("Server properties not found, creating one for you...");
            InputStream link = TridentServer.class.getResourceAsStream("/server.json");
            Files.copy(link, f.getAbsoluteFile().toPath());
        }

        TridentStart.LOGGER.info("Starting server process...");
        TridentStart.init(new JsonConfig(f));
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

        TridentStart.LOGGER.info("Creating server task thread...");
        taskExecutor.getScaledThread().addTask(new Runnable() {
            @Override
            public void run() {
                TridentServer.createServer(innerConfig, taskExecutor, TridentStart.LOGGER);
            }
        });

        try {
            TridentStart.LOGGER.info("Creating server connections...");
            ServerBootstrap b = new ServerBootstrap();
            b.group(TridentStart.bossGroup, TridentStart.workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ClientChannelInitializer())
                    .option(ChannelOption.TCP_NODELAY, true);

            // Bind and start to accept incoming connections.
            int port = config.getInt("port", 25565);
            TridentStart.LOGGER.info("Binding socket to server address, using port: " + port);
            ChannelFuture f = b.bind(
                    new InetSocketAddress(config.getString("address", Defaults.ADDRESS),
                            config.getInt("port", Defaults.PORT)))
                    .sync();

            // Wait until the server socket is closed, to gracefully shut down your server.
            TridentStart.LOGGER.info("Server started!");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //This exception is caught if server is closed.
        } catch (Exception e) {
            TridentStart.LOGGER.error("Server closed, error occurred");
            TridentStart.LOGGER.error("Printing stacktrace: \n");
            e.printStackTrace();
        } finally {
            TridentStart.LOGGER.info("Server shutting down...");
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
