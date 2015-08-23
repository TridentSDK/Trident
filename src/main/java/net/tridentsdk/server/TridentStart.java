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
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.tridentsdk.Defaults;
import net.tridentsdk.Trident;
import net.tridentsdk.config.Config;
import net.tridentsdk.docs.Volatile;
import net.tridentsdk.plugin.Plugins;
import net.tridentsdk.registry.Implementation;
import net.tridentsdk.registry.Registered;
import net.tridentsdk.server.command.ServerCommandRegistrar;
import net.tridentsdk.server.concurrent.TickSync;
import net.tridentsdk.server.netty.ClientChannelInitializer;
import net.tridentsdk.server.service.Statuses;
import net.tridentsdk.server.service.TridentImpl;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.server.world.TridentWorldLoader;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.World;
import org.apache.log4j.Level;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Server class that starts the connection listener.
 * <p/>
 * <p>Despite the fact that this class is under protected access,
 * it is documented anyways because of its significance in the server</p>
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class TridentStart {
    private static volatile EventLoopGroup bossGroup;
    private static volatile EventLoopGroup workerGroup;

    private TridentStart() {
    } // Do not initialize

    /**
     * Starts the server up when the jarfile is run
     *
     * @param args the command line arguments
     */
    public static void main(String... args) throws Exception {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(newArrayList("h", "help"), "Show this help dialog.").forHelp();
        parser.accepts("d", "Prints debug level logging output");
        parser.acceptsAll(newArrayList("log-append"), "Whether to append to the log file")
                .withRequiredArg()
                .ofType(Boolean.class)
                .defaultsTo(true)
                .describedAs("Log append");
        OptionSpec<File> properties = parser.acceptsAll(newArrayList("properties"),
                "The location for the properties file")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("server.json"))
                .describedAs("Properties file");

        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException ex) {
            TridentLogger.get().error(ex);
            return;
        }

        boolean d = options.has("d");
        TridentLogger.init(d ? Level.DEBUG : Level.INFO);
        TickSync.DEBUG = d;

        TridentLogger.get().log("Open source software by TridentSDK - https://github.com/TridentSDK");
        TridentLogger.get().log("Starting Trident server");

        TridentLogger.get().log("Looking for server files...");
        File f = properties.value(options);
        if (!f.exists()) {
            TridentLogger.get().warn("Server properties not found, creating one for you...");
            InputStream link = TridentServer.class.getResourceAsStream("/server.json");
            Files.copy(link, f.getAbsoluteFile().toPath());
        }

        TridentLogger.get().log("Initializing the API implementations");
        Implementation implementation = new TridentImpl();
        Registered.setProvider(implementation);
        TridentLogger.get().success("Loaded API implementations.");

        ((Statuses) Registered.statuses()).loadAll();
        TridentLogger.get().success("Loaded the server files");

        TridentLogger.get().log("Starting server process...");
        init(new Config(f));
    }

    /**
     * Initializes the server with the configuration file
     *
     * @param config the configuration to use for option lookup
     */
    @Volatile(policy = "Do not throw exceptions before",
            reason = "Init begins here",
            fix = "Just don't do it")
    private static void init(final Config config) throws InterruptedException {
        bossGroup = new NioEventLoopGroup(4, Defaults.ERROR_HANDLED);
        workerGroup = new NioEventLoopGroup(4, Defaults.ERROR_HANDLED);

        try {
            TridentLogger.get().log("Creating server...");
            TridentServer.createServer(config);
            TridentLogger.get().success("Server created.");

            // Required before loading worlds to find all class files in case the plugin has a world generator
            TridentLogger.get().log("Loading plugins...");
            File fi = new File(System.getProperty("user.dir") + File.separator + "plugins");
            if (!fi.exists())
                fi.mkdir();

            for (File file : new File(System.getProperty("user.dir") + File.separator + "plugins")
                    .listFiles((dir, name) -> name.endsWith(".jar")))
                Registered.plugins().load(file);
            TridentLogger.get().success("Loaded plugins.");

            TridentWorldLoader.loadAll();
            TridentServer.WORLD = (TridentWorld) Registered.worlds().get("world");

            if (TridentServer.WORLD == null) {
                World world = TridentServer.instance().rootWorldLoader.createWorld("world");
                TridentServer.WORLD = (TridentWorld) world;
            }

            TridentLogger.get().log("Setting server commands...");
            ServerCommandRegistrar.registerAll();
            TridentLogger.get().success("Server commands set.");

            TridentLogger.get().log("Enabling plugins...");
            Plugins handler = Registered.plugins();
            handler.forEach(handler::enable);
            TridentLogger.get().success("Enabled plugins.");

            ////////////////////////////////// NETTY SETUP //////////////////////////////////////////

            TridentLogger.get().log("Creating server connections...");
            String ip = config.getString("address", Defaults.ADDRESS);
            int port = config.getInt("port", Defaults.PORT);

            TridentLogger.get().log("Binding socket to server address, using address:port " + ip + ":" + port);

            new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ClientChannelInitializer())
                    .option(ChannelOption.TCP_NODELAY, true)
                    .bind(new InetSocketAddress(ip, port))
                    .sync();

            TridentLogger.get().success("Server started.");

            /////////////////////////// Console command handling ////////////////////////////////////
            Thread thread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    String command = scanner.nextLine();
                    System.out.print("$ ");

                    Trident.console().invokeCommand(command);
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (InterruptedException e) {
            // This exception is caught if server is closed.
        } catch (NoSuchElementException e) {
            // For some reason, this is thrown when the server is quit
        } catch (Exception e) {
            TridentLogger.get().error("Server closed, error occurred");
            TridentLogger.get().error(e);
            Trident.shutdown();
        }
    }

    /**
     * Shuts down the backed event loops
     */
    public static void close() {
        workerGroup.shutdownGracefully().awaitUninterruptibly();
        bossGroup.shutdownGracefully().awaitUninterruptibly();
    }
}