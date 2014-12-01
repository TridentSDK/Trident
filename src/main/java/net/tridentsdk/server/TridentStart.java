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
package net.tridentsdk.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.tridentsdk.api.Defaults;
import net.tridentsdk.api.config.JsonConfig;
import net.tridentsdk.api.factory.CollectFactory;
import net.tridentsdk.api.factory.ConfigFactory;
import net.tridentsdk.api.factory.Factories;
import net.tridentsdk.server.netty.ClientChannelInitializer;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import net.tridentsdk.server.threads.ThreadsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.collect.Lists.newArrayList;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(TridentServer.class);

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

        LOGGER.info("Open source software by TridentSDK - https://github.com/TridentSDK");
        LOGGER.info("Starting Trident server");

        LOGGER.info("Creating handlers...");
        OptionParser parser = new OptionParser();
        parser.acceptsAll(newArrayList("h", "help"), "Show this help dialog.").forHelp();
        OptionSpec<Boolean> append =
                parser.acceptsAll(newArrayList("log-append"), "Whether to append to the log file")
                        .withRequiredArg()
                        .ofType(Boolean.class)
                        .defaultsTo(true)
                        .describedAs("Log append");
        LOGGER.info("Parsing server properties, using server.json...");
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

        LOGGER.info("Looking for server properties...");
        File f;
        if (!(f = properties.value(options)).exists()) {
            LOGGER.info("Server properties not found, creating one for you...");
            InputStream link = TridentServer.class.getResourceAsStream("/server.json");
            Files.copy(link, f.getAbsoluteFile().toPath());
        }

        LOGGER.info("Starting server process...");
        init(new JsonConfig(f));
    }

    /**
     * Initializes the server with the configuration file
     *
     * @param config the configuration to use for option lookup
     */
    private static void init(JsonConfig config) {
        LOGGER.info("Initializing the API implementations");

        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
        Factories.init(new ThreadsManager());
        Factories.init(new TridentScheduler());

        //Server should read all settings from the loaded config
        final ConcurrentTaskExecutor<?> taskExecutor = new ConcurrentTaskExecutor<>(1);
        final JsonConfig innerConfig = config;

        Factories.init(new ConfigFactory() {
            @Override
            public JsonConfig serverConfig() {
                return innerConfig;
            }
        });

        LOGGER.info("Creating server task thread...");
        taskExecutor.scaledThread().addTask(new Runnable() {
            @Override
            public void run() {
                TridentServer.createServer(innerConfig, taskExecutor, LOGGER);
            }
        });

        try {
            LOGGER.info("Creating server connections...");
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ClientChannelInitializer())
                    .option(ChannelOption.TCP_NODELAY, true);

            // Bind and start to accept incoming connections.
            int port = config.getInt("port", 25565);
            LOGGER.info("Binding socket to server address, using port: " + port);
            ChannelFuture f = b.bind(
                    new InetSocketAddress(config.getString("address", Defaults.ADDRESS),
                            config.getInt("port", Defaults.PORT)))
                    .sync();

            // Wait until the server socket is closed, to gracefully shut down your server.
            LOGGER.info("Server started!");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //This exception is caught if server is closed.
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
     * Shuts down the server by closing the backed event loops
     */
    public static void close() {
        //Correct way to close the socket and shut down the server
        workerGroup.shutdownGracefully().awaitUninterruptibly();
        bossGroup.shutdownGracefully().awaitUninterruptibly();
    }
}
