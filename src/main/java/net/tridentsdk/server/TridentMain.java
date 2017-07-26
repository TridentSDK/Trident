/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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

import net.tridentsdk.Impl;
import net.tridentsdk.Server;
import net.tridentsdk.command.CommandHandler;
import net.tridentsdk.logger.Logger;
import net.tridentsdk.plugin.Plugin;
import net.tridentsdk.server.command.*;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.config.ConfigIo;
import net.tridentsdk.server.config.OpsList;
import net.tridentsdk.server.config.ServerConfig;
import net.tridentsdk.server.logger.InfoLogger;
import net.tridentsdk.server.logger.PipelinedLogger;
import net.tridentsdk.server.net.NetServer;
import net.tridentsdk.server.packet.status.StatusOutResponse;
import net.tridentsdk.server.util.Debug;
import net.tridentsdk.server.util.JiraExceptionCatcher;
import net.tridentsdk.server.world.TridentWorldLoader;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

import javax.annotation.concurrent.Immutable;
import java.nio.file.Files;

import static net.tridentsdk.server.command.TridentDummyCommandPlugin.MINECRAFT_INST;
import static net.tridentsdk.server.command.TridentDummyCommandPlugin.TRIDENT_INST;

/**
 * Trident server startup class
 */
@Immutable
public final class TridentMain {
    /**
     * Verbose commandline option (print debug or not)
     */
    private static final String VERBOSE = "-v";
    /**
     * If passed, native epoll is not used
     */
    private static final String NO_EPOLL = "-noepoll";
    /**
     * Enables certain debugging functions in the server
     */
    private static final String DEBUG = "-d";

    // Prevent instantiation
    private TridentMain() {
    }

    public static void main(String[] args) {
        try {
            start(args);
        } catch (Exception e) {
            JiraExceptionCatcher.serverException(e);
            System.exit(1);
        }
    }

    /**
     * Move out the method into here because we don't want
     * everything shifted to the right as a result of the
     * error handling
     */
    private static void start(String[] args) throws Exception {
        // Parse args --------------------------------------
        boolean verbose = false;
        boolean noEpoll = false;
        for (String s : args) {
            if (s.equals(VERBOSE)) {
                verbose = true;
                continue;
            }

            if (s.equals(NO_EPOLL)) {
                noEpoll = true;
                continue;
            }

            if (s.equals(DEBUG)) {
                Debug.IS_DEBUGGING = true;
                continue;
            }

            System.out.println("Unrecognized option: " + s + ", ignoring.");
        }
        // -------------------------------------------------

        // Setup logging facilities ------------------------
        PipelinedLogger internal = PipelinedLogger.init(verbose);
        Logger logger = InfoLogger.get(internal, "Server");
        // -------------------------------------------------

        logger.log("Server software by TridentSDK - https://tsdk.xyz");

        logger.log("Server implements API version " + Server.VERSION);
        logger.log("Server implements Minecraft protocol for " + StatusOutResponse.MC_VERSION);

        // Setup the files ---------------------------
        logger.log("Checking for server files: server.json");
        if (!Files.exists(ServerConfig.PATH)) {
            logger.warn("File \"server.json\" not present");
            logger.log("Creating one for you... ");
            ConfigIo.exportResource(ServerConfig.PATH, "/server.hjson");
        }

        logger.log("Checking for server files: plugins folder");
        if (!Files.exists(Plugin.PLUGIN_DIR)) {
            logger.warn("File \"plugins\" not present");
            logger.log("Creating one for you... ");
            Files.createDirectory(Plugin.PLUGIN_DIR);
        }

        boolean initOpsList = false;
        logger.log("Checking for server files: ops list");
        if (!Files.exists(OpsList.PATH)) {
            logger.warn("File \"ops.json\" not present");
            logger.log("Creating one for you... ");
            Files.createFile(OpsList.PATH);
            initOpsList = true;
        }

        logger.log("Reading server.json...");
        ServerConfig config = ServerConfig.init();

        logger.log("Reading ops.json...");
        OpsList opsList = OpsList.init(initOpsList);
        // -------------------------------------------------

        // Pass net args to the server handler -------------
        String address = config.ip();
        int port = config.port();
        
        NetServer server = NetServer.init(address, port, config.useNative() && !noEpoll);
        // -------------------------------------------------

        // Setup API implementations -----------------------
        logger.log("Setting up API implementation providers...");
        ImplementationProvider impl = new ImplementationProvider(internal);
        Impl.setImpl(impl);
        logger.success("Done.");
        // -------------------------------------------------

        // Init thread pools -------------------------------
        ServerThreadPool.init();
        // -------------------------------------------------

        // Setup server ------------------------------------
        logger.log("Setting up the server...");
        TridentServer trident = TridentServer.init(config, logger, server, opsList);
        logger.success("Done.");
        // -------------------------------------------------

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).submit(() -> {
            // Register commands ---------------------------
            logger.log("Registering server commands...");
            CommandHandler h = trident.getCommandHandler();
            h.register(MINECRAFT_INST, new StopCommand());
            h.register(MINECRAFT_INST, new KickCommand());
            h.register(MINECRAFT_INST, new TeleportCommand());
            h.register(MINECRAFT_INST, new SayCommand());
            h.register(MINECRAFT_INST, new HelpCommand());
            h.register(MINECRAFT_INST, new OpCommand());
            h.register(MINECRAFT_INST, new DeopCommand());
            h.register(TRIDENT_INST, new DebugCommand());
            logger.log("Done.");
            // ---------------------------------------------

            // Load plugins --------------------------------
            logger.log("Loading plugins...");
            trident.getPluginLoader().loadAll();
            logger.log("Done.");
            // ---------------------------------------------
        }).get();

        // Load worlds -------------------------------------
        logger.log("Loading worlds...");
        TridentWorldLoader.getInstance().loadAll();
        logger.log("Done.");
        // -------------------------------------------------

        // Setup plugins -----------------------------------
        logger.log("Enabling plugins...");
        ServerThreadPool.forSpec(PoolSpec.PLUGINS).submit(() -> {
            for (Plugin plugin : trident.getPluginLoader().getLoaded().values()) {
                plugin.setup();
            }
        }).get();
        logger.log("Done.");
        // -------------------------------------------------

        // Setup netty and other network crap --------------
        logger.log(String.format("Server will be opened on %s:%s", address, port));
        server.setup();
        // -------------------------------------------------

        // Load server icon ----------------- --------------
        logger.log("Loading server icon");
        StatusOutResponse.init();
        // -------------------------------------------------

        // JLine -------------------------------------------
        LineReader reader = LineReaderBuilder.
                builder().
                appName("Trident").
                terminal(TerminalBuilder.
                        builder().
                        dumb(true).
                        jansi(true).
                        build()).
                build();
        // -------------------------------------------------

        // Command handler ---------------------------------
        while (true) {
            // TODO this sucks
            String line = reader.readLine("$ ");
            if (line.isEmpty()) {
                continue;
            }

            trident.runCommand(line);

            if (trident.isShutdownState()) {
                return;
            }
        }
        // -------------------------------------------------
    }
}
