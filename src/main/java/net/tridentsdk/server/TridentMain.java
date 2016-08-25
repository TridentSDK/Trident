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
import net.tridentsdk.server.command.InfoLogger;
import net.tridentsdk.server.command.PipelinedLogger;
import net.tridentsdk.server.concurrent.ServerTick;
import net.tridentsdk.server.config.ConfigIo;
import net.tridentsdk.server.config.ServerConfig;
import net.tridentsdk.server.net.NetServer;

import javax.annotation.concurrent.Immutable;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.List;

/**
 * Trident server startup class
 */
@Immutable
public class TridentMain {
    /**
     * Verbose commandline option (print debug or not)
     */
    private static final String VERBOSE = "-v";

    public static void main(String[] args) {
        try {
            start(args);
        } catch (Exception e) {
            String url = "https://tridentsdk.atlassian.net/secure/CreateIssue!default.jspa";

            try {
                StackTraceElement element = e.getStackTrace()[0];
                int pos = 0;
                while(element.getClassName().startsWith("java")){
                    element = e.getStackTrace()[pos++];
                }

                String errorMessage = e.getMessage() == null ? "java.lang.NullPointerException" : e.getMessage();
                String summary = errorMessage + " in " + element.getClassName() + " at line " + element.getLineNumber();

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);

                String description = "{code:title=StackTrace}" + sw.toString() + "{code}";

                String environment = "Trident Version: " + "0.5-alpha\n" + // TODO Get actual TridentSDK version
                        "Operating System: " + System.getProperty("os.name") + " (" + System.getProperty("os.version") + ")\n" +
                        "System Architecture: " + System.getProperty("os.arch") + "\n" +
                        "Java Version: " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")";

                String longUrl = "https://tridentsdk.atlassian.net/secure/CreateIssueDetails!init.jspa?pid=10200&issuetype=1&priority=4&summary=" +
                        URLEncoder.encode(summary, "UTF-8") +
                        "&description=" +
                        URLEncoder.encode(description, "UTF-8") +
                        "&environment=" +
                        URLEncoder.encode(environment, "UTF-8");

                URL shortened = new URL("http://tsdk.xyz/api/v2/action/shorten?url=" + URLEncoder.encode(longUrl, "UTF-8"));
                BufferedReader in = new BufferedReader(new InputStreamReader(shortened.openStream()));
                url = in.readLine();
            }catch (Exception ignored){
            }


            PrintStream o = System.err;
            o.println();
            o.println();
            o.println("Unhandled exception occurred while starting the server.");
            o.println("This was not intended to happen.");
            o.println("Please report this on " + url);
            o.println();
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
        PipelinedLogger internal = PipelinedLogger.init(verbose);
        Logger logger = InfoLogger.get(internal, "Server");
        // -------------------------------------------------

        logger.log("Server software by TridentSDK - https://tridentsdk.net");

        // Setup the config file ---------------------------
        logger.log("Checking for server files: server.json");
        if (!Files.exists(ServerConfig.PATH)) {
            logger.warn("File \"server.json\" not present");
            logger.log("Creating one for you... ");
            ConfigIo.exportResource(ServerConfig.PATH, "/server.json");
            logger.success("Done.");
        }

        logger.log("Reading server.json... ");
        ServerConfig config = ServerConfig.init();
        logger.success("Done.");
        // -------------------------------------------------

        // Pass net args to the server handler -------------
        NetServer server = NetServer.init(config);
        // -------------------------------------------------

        logger.log("Setting up the server... ");
        TridentServer tridentServer = TridentServer.init(config, logger, server);
        logger.success("Done.");

        // Setup ticking/heartbeat -------------------------
        ServerTick ticker = new ServerTick(tridentServer);
        ticker.start();
        // -------------------------------------------------

        // Setup API implementations -----------------------
        logger.log("Setting up API implementation providers... ");
        ImplementationProvider impl = new ImplementationProvider(internal);
        Impl.setImpl(impl);
        logger.success("Done.");
        // -------------------------------------------------

        // Load worlds -------------------------------------
        impl.wrlds().loadAll();
        // -------------------------------------------------

        // Setup netty and other network crap --------------
        String address = config.ip();
        int port = config.port();
        logger.log(String.format("Server will be opened on %s:%s", address, port));
        server.setup();
        // -------------------------------------------------
    }
}