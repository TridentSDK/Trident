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
package net.tridentsdk.server.command;

import net.tridentsdk.command.logger.Logger;
import org.fusesource.jansi.AnsiConsole;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This console is normally regarded as the underlying
 * console as it is designed to be the final console in the
 * pipeline; it does not pass the logger messages any
 * further and logs its output directly to the output.
 *
 * <p>Thus such, this class contains the init code required
 * to setup the loggers.</p>
 */
public class DefaultConsole implements Logger {
    // TODO use system.out
    // also system.err
    // also add info such as [Classname] and time

    /**
     * The underlying stream that to which output is passed
     */
    private final PrintStream stream = System.out;

    public static Logger init(boolean verbose) {
        AnsiConsole.systemInstall();

        // bottom of pipeline
        Logger underlying = new DefaultConsole();
        Logger colorizer = new ColorizerConsole(underlying);
        Logger debugger = verbose ? DebugConsole.verbose(colorizer) : DebugConsole.noop(colorizer);
        ConsoleHandlers handler = new ConsoleHandlers(debugger);
        return new LogFileConsole(handler); // top of pipeline
    }

    @Override
    public void log(String s) {
        stream.println(s);
    }

    @Override
    public void logp(String s) {
        stream.print(s);
    }

    @Override
    public void success(String s) {
        stream.println(s);
    }

    @Override
    public void successp(String s) {
        stream.print(s);
    }

    @Override
    public void warn(String s) {
        stream.println(s);
    }

    @Override
    public void warnp(String s) {
        stream.print(s);
    }

    @Override
    public void error(String s) {
        stream.println(s);
    }

    @Override
    public void errorp(String s) {
        stream.print(s);
    }

    @Override
    public void debug(String s) {
        stream.println(s);
    }

    @Override
    public OutputStream out() {
        return stream;
    }
}