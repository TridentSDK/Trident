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
package net.tridentsdk.server.util;

import net.tridentsdk.Server;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Catches exceptions in the server process that are
 * unrecoverable and pastes a link for creating an issue
 * with details pulled from the environment.
 */
public final class JiraExceptionCatcher {
    // Prevent instantiation
    private JiraExceptionCatcher() {
    }

    /**
     * Catches the given exception and reformats it,
     * pulling
     * in the stacktrace information and providing a post
     * link to the TridentSDK JIRA.
     *
     * @param e the exception to catch
     */
    public static void serverException(Exception e) {
        String url = "https://tridentsdk.atlassian.net/secure/CreateIssue!default.jspa";

        try {
            StackTraceElement element = e.getStackTrace()[0];
            int pos = 0;
            while (element.getClassName().startsWith("java")) {
                element = e.getStackTrace()[pos++];
            }

            String errorMessage = e.getMessage() == null ? "java.lang.NullPointerException" : e.getMessage();
            String summary = errorMessage + " in " + element.getClassName() + " at line " + element.getLineNumber();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            String description = "{code:title=StackTrace}" + sw.toString() + "{code}";

            String environment = "Trident Version: " + Server.VERSION + "\n" +
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
        } catch (Exception ignored) {
        }


        PrintStream o = System.err;
        o.println();
        o.println();
        o.println("Unhandled serverException occurred while starting the server.");
        o.println("This was not intended to happen.");
        o.println("Please report this on " + url);
        o.println();
        e.printStackTrace();
    }
}