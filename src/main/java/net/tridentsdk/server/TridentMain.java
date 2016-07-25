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
import net.tridentsdk.Impl;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.server.command.DefaultLogger;
import net.tridentsdk.server.command.InfoLogger;
import net.tridentsdk.server.config.ConfigIo;
import net.tridentsdk.server.config.ServerConfig;
import net.tridentsdk.server.net.NetServer;

import java.io.PrintStream;
import java.nio.file.Files;
import java.util.List;

/**
 * Trident server startup class
 */
public class TridentMain {
    /**
     * Verbose commandline option (print debug or not)
     */
    private static final String VERBOSE = "-v";

    public static void main(String[] args) {
        try {
            start(args);
        } catch (Exception e) {
            PrintStream o = System.out;
            o.println("Unhandled exception occurred while starting the server.");
            o.println("This was not intended to happen.");
            o.println("Please report this on https://tridentsdk.atlassian.net/secure/CreateIssue!default.jspa");
            e.printStackTrace();

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
        List<String> argList = Lists.newArrayList(args);
        boolean verbose = argList.contains(VERBOSE);
        // -------------------------------------------------

        // Setup logging facilities ------------------------
        Logger internal = DefaultLogger.init(verbose);
        Logger logger = new InfoLogger(internal, "Server");
        // -------------------------------------------------

        logger.log("Server software by TridentSDK - https://tridentsdk.net");

        // Setup the config file ---------------------------
        logger.log("Checking for server files: server.json");
        if (!Files.exists(ServerConfig.PATH)) {
            logger.warn("File \"server.json\" not present");
            logger.logp("Creating one for you... ");
            ConfigIo.exportResource(ServerConfig.PATH, "/server.json");
            logger.success("Done.");
        }

        logger.logp("Reading server.json... ");
        new InfoLogger(internal, "Interrupt").log("KEK");
        ServerConfig config = ServerConfig.init();
        logger.success("Done.");
        // -------------------------------------------------

        // Pass net args to the server handler -------------
        NetServer server = NetServer.init(config);
        // -------------------------------------------------

        logger.logp("Setting up the server... ");
        TridentServer.init(config, logger, server);
        logger.success("Done.");

        // Setup API implementations -----------------------
        logger.logp("Setting up API implementation providers... ");
        Impl.setImpl(new ImplementationProvider());
        logger.success("Done.");
        // -------------------------------------------------

        // Setup netty and other network crap --------------
        String address = config.ip();
        int port = config.port();
        logger.log(String.format("Server will be opened on %s:%s", address, port));
        server.setup();
        // -------------------------------------------------
    }
}