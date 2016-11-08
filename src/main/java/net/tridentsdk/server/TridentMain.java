/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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

import com.google.common.collect.Lists;
import io.netty.util.ResourceLeakDetector;
import net.tridentsdk.Impl;
import net.tridentsdk.Server;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.doc.Debug;
import net.tridentsdk.server.command.InfoLogger;
import net.tridentsdk.server.command.PipelinedLogger;
import net.tridentsdk.server.config.ConfigIo;
import net.tridentsdk.server.config.ServerConfig;
import net.tridentsdk.server.net.NetServer;
import net.tridentsdk.server.packet.status.StatusOutResponse;
import net.tridentsdk.server.util.JiraExceptionCatcher;
import net.tridentsdk.server.world.TridentWorldLoader;

import javax.annotation.concurrent.Immutable;
import java.nio.file.Files;
import java.util.List;

/**
 * Trident server startup class
 */
@Immutable
public final class TridentMain {
    /**
     * Verbose commandline option (print debug or not)
     */
    private static final String VERBOSE = "-v";

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
    @Debug("Leak detector")
    private static void start(String[] args) throws Exception {
        // Parse args --------------------------------------
        List<String> argList = Lists.newArrayList(args);
        boolean verbose = argList.contains(VERBOSE);
        // -------------------------------------------------

        // Setup logging facilities ------------------------
        PipelinedLogger internal = PipelinedLogger.init(verbose);
        Logger logger = InfoLogger.get(internal, "Server");
        // -------------------------------------------------

        logger.log("Server software by TridentSDK - https://tridentsdk.net");

        logger.log("Server implements API version " + Server.VERSION);
        logger.log("Server implements Minecraft protocol for " + StatusOutResponse.MC_VERSION);

        // Setup the config file ---------------------------
        logger.log("Checking for server files: server.json");
        if (!Files.exists(ServerConfig.PATH)) {
            logger.warn("File \"server.json\" not present");
            logger.log("Creating one for you... ");
            ConfigIo.exportResource(ServerConfig.PATH, "/server.json");
            logger.success("Done.");
        }

        logger.log("Reading server.json...");
        ServerConfig config = ServerConfig.init();
        logger.success("Done.");
        // -------------------------------------------------

        // Pass net args to the server handler -------------
        NetServer server = NetServer.init(config);
        // -------------------------------------------------

        // Setup API implementations -----------------------
        logger.log("Setting up API implementation providers...");
        ImplementationProvider impl = new ImplementationProvider(internal);
        Impl.setImpl(impl);
        logger.success("Done.");
        // -------------------------------------------------

        // Init thread pools -------------------------------

        // -------------------------------------------------

        // Load worlds -------------------------------------
        logger.log("Loading worlds...");
        TridentWorldLoader.getInstance().loadAll();
        logger.log("Done.");
        // -------------------------------------------------

        // Setup server ------------------------------------
        logger.log("Setting up the server...");
        TridentServer.init(config, logger, server);
        logger.success("Done.");
        // -------------------------------------------------

        // Setup netty and other network crap --------------
        String address = config.ip();
        int port = config.port();
        logger.log(String.format("Server will be opened on %s:%s", address, port));
        server.setup();
        // -------------------------------------------------

        if(config.isNettyLeakDetectorEnabled()) {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        }
    }
}