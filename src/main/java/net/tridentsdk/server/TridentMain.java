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
import net.tridentsdk.Server;
import net.tridentsdk.command.Console;
import net.tridentsdk.config.Config;
import net.tridentsdk.server.command.ConsoleHandlers;
import net.tridentsdk.server.command.DebugConsole;
import net.tridentsdk.server.command.DefaultConsole;
import net.tridentsdk.server.command.LogFileConsole;
import net.tridentsdk.server.config.ConfigIo;
import net.tridentsdk.server.config.ServerConfig;
import net.tridentsdk.server.config.TridentConfig;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
        Console underlying = new DefaultConsole();
        Console debugger = verbose ? DebugConsole.verbose(underlying) : DebugConsole.noop(underlying);
        LogFileConsole console = new LogFileConsole(new ConsoleHandlers(debugger));
        // -------------------------------------------------

        console.log("Server software by TridentSDK - https://tridentsdk.net");

        // Setup the config file ---------------------------
        console.log("Checking for server files: server.json");
        if (!Files.exists(ServerConfig.PATH)) {
            console.warn("server.json not present");
            console.warn("Creating one for you...");
            ConfigIo.exportResource(ServerConfig.PATH, "/server.json");
            console.success("server.json created!");
        }

        console.log("Reading server.json...");
        ServerConfig config = new ServerConfig();
        // -------------------------------------------------

        console.log("Setting up the server...");
        TridentServer server = new TridentServer(config, console);

        // Setup netty and other network crap --------------
        String address = config.address();
        int port = config.port();
        console.log(String.format("Server will be opened on %s:%s", address, port));
        // NetServer server = NetsServer.setup(address, port);
        // -------------------------------------------------

        // Setup API implementations -----------------------
        // TODO put this somewhere less retarded
        console.log("Setting up API implementation providers");
        Impl.setImpl(new Impl.ImplementationProvider() {
            @Override
            public Server svr() {
                return server;
            }

            @Override
            public Config newCfg(Path p) {
                return new TridentConfig(p);
            }
        });
        // -------------------------------------------------
    }
}