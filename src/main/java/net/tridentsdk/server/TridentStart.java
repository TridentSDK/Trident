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
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.tridentsdk.Defaults;
import net.tridentsdk.Trident;
import net.tridentsdk.config.JsonConfig;
import net.tridentsdk.docs.Volatile;
import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.command.ServerCommandRegistrar;
import net.tridentsdk.server.netty.ClientChannelInitializer;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.util.TridentLogger;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Server class that starts the connection listener.
 *
 * <p>Despite the fact that this class is under protected access,
 * it is documented anyways because of its significance in the server</p>
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class TridentStart {
    static {
        TridentLogger.init();
    }

    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(4, Defaults.ERROR_HANDLED);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4, Defaults.ERROR_HANDLED);

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

        TridentLogger.log("Open source software by TridentSDK - https://github.com/TridentSDK");
        TridentLogger.log("Starting Trident server");

        TridentLogger.log("Creating handlers...");
        OptionParser parser = new OptionParser();
        parser.acceptsAll(newArrayList("h", "help"), "Show this help dialog.").forHelp();
        parser.acceptsAll(newArrayList("log-append"), "Whether to append to the log file")
                .withRequiredArg()
                .ofType(Boolean.class)
                .defaultsTo(true)
                .describedAs("Log append");
        TridentLogger.log("Parsing server properties, using server.json...");
        OptionSpec<File> properties = parser.acceptsAll(newArrayList("properties"),
                "The location for the properties file")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("server.json"))
                .describedAs("Properties file");

        TridentLogger.log("Parsing command line arguments...");
        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException ex) {
            TridentLogger.error(ex);
            return;
        }
        TridentLogger.success("Parsed arguments.");

        TridentLogger.log("Looking for server properties...");
        File f = properties.value(options);
        if (!f.exists()) {
            TridentLogger.warn("Server properties not found, creating one for you...");
            InputStream link = TridentServer.class.getResourceAsStream("/server.json");
            Files.copy(link, f.getAbsoluteFile().toPath());
        }
        TridentLogger.success("Created the server JSON");

        TridentLogger.log("Starting server process...");
        init(new JsonConfig(f));
    }

    /**
     * Initializes the server with the configuration file
     *
     * @param config the configuration to use for option lookup
     */
    @Volatile(policy = "Do not throw exceptions before",
            reason = "Init begins here",
            fix = "Just don't do it")
    private static void init(final JsonConfig config) throws InterruptedException {
        try {
            TridentLogger.log("Initializing the API implementations");
            Factories.init(new CollectFactory() {
                @Override
                public <K, V> ConcurrentMap<K, V> createMap() {
                    return new ConcurrentHashMapV8<>();
                }
            });
            Factories.init(ThreadsHandler.create());
            Factories.init(TridentScheduler.create());
            TridentLogger.success("Loaded API implementations.");

            TridentLogger.log("Creating server...");
            TridentServer.createServer(config);
            TridentLogger.success("Server created.");

            TridentLogger.log("Setting server commands...");
            ServerCommandRegistrar.registerAll();
            TridentLogger.success("Server commands set.");

            TridentLogger.log("Loading plugins...");
            File fi = new File(System.getProperty("user.dir") + File.separator + "plugins");
            if (!fi.exists())
                fi.mkdir();

            for (File file : new File(System.getProperty("user.dir") + File.separator + "plugins").listFiles())
                Trident.pluginHandler().load(file);
            TridentLogger.success("Loaded plugins.");

            ////////////////////////////////// NETTY SETUP //////////////////////////////////////////

            TridentLogger.log("Creating server connections...");
            String ip = config.getString("address", Defaults.ADDRESS);
            int port = config.getInt("port", Defaults.PORT);

            TridentLogger.log("Binding socket to server address, using address:port " + ip + ":" + port);

            new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ClientChannelInitializer())
                    .option(ChannelOption.TCP_NODELAY, true)
                    .bind(new InetSocketAddress(ip, port))
                    .sync();

            TridentLogger.success("Server started.");

            /////////////////////////// Console command handling ////////////////////////////////////
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("$ \r");
                String command = scanner.nextLine();
                System.out.println(command);

                Trident.console().invokeCommand(command);

                if (command.equalsIgnoreCase("shutdown"))
                    break;
            }
        } catch (InterruptedException e) {
            // This exception is caught if server is closed.
        } catch (Exception e) {
            TridentLogger.error("Server closed, error occurred");
            TridentLogger.error(e);
            Trident.shutdown();
        }
    }

    /**
     * Shuts down the backed event loops
     */
    public static void close() {
        // Correct way to close the socket and shut down the server
        workerGroup.shutdownGracefully().awaitUninterruptibly();
        bossGroup.shutdownGracefully().awaitUninterruptibly();
    }
}
