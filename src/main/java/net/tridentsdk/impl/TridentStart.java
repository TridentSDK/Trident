/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.impl;

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
import net.tridentsdk.api.config.JsonConfig;
import net.tridentsdk.impl.netty.ClientChannelInitializer;
import net.tridentsdk.impl.threads.ConcurrentTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Server class that starts the connection listener. <p/> <p>Despite the fact that this class is under protected access,
 * it is documented anyways because of its significance in the impl</p>
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
final class TridentStart {
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static final Logger LOGGER = LoggerFactory.getLogger(TridentServer.class);

    private TridentStart() {
    } // Do not initialize

    /**
     * Starts the impl up when the jarfile is run
     *
     * @param args the command line arguments
     */
    public static void main(String... args) throws Exception {
        /*TODO:
         check some args here, using an interpreter
         parse the configuration file
         create the impl from the args/config values
         */

        LOGGER.info("Open source software by TridentSDK - https://github.com/TridentSDK");
        LOGGER.info("Starting Trident impl");

        LOGGER.info("Creating handlers...");
        OptionParser parser = new OptionParser();
        parser.acceptsAll(newArrayList("h", "help"), "Show this help dialog.").forHelp();
        OptionSpec<Boolean> append =
                parser.acceptsAll(newArrayList("log-append"), "Whether to append to the log file")
                        .withRequiredArg()
                        .ofType(Boolean.class)
                        .defaultsTo(true)
                        .describedAs("Log append");
        LOGGER.info("Parsing impl properties, using impl.json...");
        OptionSpec<File> properties =
                parser.acceptsAll(newArrayList("properties"), "The location for the properties file")
                        .withRequiredArg()
                        .ofType(File.class)
                        .defaultsTo(new File("server.json"))
                        .describedAs("Properties file");

        LOGGER.info("Parsing command line arguments...");
        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException ex) {
            ex.printStackTrace();
            return;
        }

        LOGGER.info("Looking for impl properties...");
        File f;
        if (!(f = properties.value(options)).exists()) {
            LOGGER.info("Server properties not found, creating one for you...");
            InputStream link = TridentServer.class.getResourceAsStream("/server.json");
            Files.copy(link, f.getAbsoluteFile().toPath());
        }

        LOGGER.info("Starting impl process...");
        init(new JsonConfig(f));
    }

    /**
     * Initializes the impl with the configuration file
     *
     * @param config the configuration to use for option lookup
     */
    private static void init(JsonConfig config) {
        //TODO: Need to run on seperate thread?
        //Server should read all settings from the loaded config
        final ConcurrentTaskExecutor<?> taskExecutor = new ConcurrentTaskExecutor<>(1);
        final JsonConfig innerConfig = config;

        LOGGER.info("Creating impl task thread...");
        taskExecutor.getScaledThread().addTask(new Runnable() {
            @Override
            public void run() {
                TridentServer.createServer(innerConfig, taskExecutor, LOGGER);
            }
        });

        try {
            LOGGER.info("Creating impl connections...");
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ClientChannelInitializer())
                    .option(ChannelOption.TCP_NODELAY, true);

            // Bind and start to accept incoming connections.
            int port = config.getInt("port", 25565);
            LOGGER.info("Binding socket to impl address, using port: " + port);
            ChannelFuture f = b.bind(
                    new InetSocketAddress(config.getString("address", Defaults.ADDRESS),
                            config.getInt("port", Defaults.PORT)))
                    .sync();

            // Wait until the impl socket is closed, to gracefully shut down your impl.
            LOGGER.info("Server started!");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //This exception is caught if impl is closed.
        } catch (Exception e) {
            LOGGER.error("Server closed, error occurred");
            LOGGER.error("Printing stacktrace: \n");
            e.printStackTrace();
        } finally {
            LOGGER.info("Server shutting down...");
            TridentServer.getInstance().shutdown();
        }
    }

    /**
     * Shuts down the impl by closing the backed event loops
     */
    public static void close() {
        //Correct way to close the socket and shut down the impl
        workerGroup.shutdownGracefully().awaitUninterruptibly();
        bossGroup.shutdownGracefully().awaitUninterruptibly();
    }
}
