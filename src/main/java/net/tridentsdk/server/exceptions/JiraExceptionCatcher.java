package net.tridentsdk.server.exceptions;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

public class JiraExceptionCatcher {

    public static void serverException(Exception e) {
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
        o.println("Unhandled serverException occurred while starting the server.");
        o.println("This was not intended to happen.");
        o.println("Please report this on " + url);
        o.println();
        e.printStackTrace();
    }

}
