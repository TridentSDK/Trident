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
import net.tridentsdk.server.command.DefaultConsole;
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
        Logger console = DefaultConsole.init(verbose);
        // -------------------------------------------------

        console.log("Server software by TridentSDK - https://tridentsdk.net");

        // Setup the config file ---------------------------
        console.log("Checking for server files: server.json");
        if (!Files.exists(ServerConfig.PATH)) {
            console.warn("File \"server.json\" not present");
            console.warnp("Creating one for you... ");
            ConfigIo.exportResource(ServerConfig.PATH, "/server.json");
            console.success("Done.");
        }

        console.logp("Reading server.json... ");
        ServerConfig config = ServerConfig.init();
        console.success("Done.");
        // -------------------------------------------------

        console.logp("Setting up the server... ");
        TridentServer.init(config, console);
        console.success("Done.");

        // Setup netty and other network crap --------------
        String address = config.address();
        int port = config.port();
        console.log(String.format("Server will be opened on %s:%s", address, port));
        NetServer server = NetServer.init(address, port);

        // -------------------------------------------------

        // Setup API implementations -----------------------
        console.logp("Setting up API implementation providers... ");
        Impl.setImpl(new ImplementationProvider());
        console.success("Done.");
        // -------------------------------------------------
    }
}