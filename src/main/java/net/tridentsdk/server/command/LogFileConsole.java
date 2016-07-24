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

import java.io.OutputStream;

/**
 * This class represents the file logger which writes the
 * messages sent by loggers to the log file.
 *
 * <p>In addition, this class also manages the log files,
 * moving them to appropriate directories when they fill to
 * the text editor limits.</p>
 *
 * <p>This class is also the first logger in the server
 * logger pipeline, because plugins must call to this logger
 * in order for it to log plugin messages to the file as
 * well.</p>
 *
 * <p>The server pipeline usually looks something like this:
 * <pre>{@code
 *              [Plugin Loggers]
 *                     ||
 *                     \/
 *               LogFileConsole
 *                     ||
 *                     \/
 *             [Logger handlers]
 *                     ||
 *                    /  \
 *      NoDebugConsole ?? DebugConsole
 *                    \  /
 *                     ||
 *                     \/
 *               ColorizerConsole
 *                     ||
 *                     \/
 *                DefaultConsole
 * }</pre></p>
 */
public class LogFileConsole implements Logger {
    // TODO implement file handling logic

    /**
     * The next console in the pipeline
     */
    private final Logger next;

    /**
     * Creates a new log file console, which logs items to
     * a file before being sent to the underlying console.
     *
     * @param next the next console in the pipeline
     */
    public LogFileConsole(Logger next) {
        this.next = next;
    }

    @Override
    public void log(String s) {
        next.log(s);
    }

    @Override
    public void logp(String s) {
        next.logp(s);
    }

    @Override
    public void success(String s) {
        next.success(s);
    }

    @Override
    public void successp(String s) {
        next.successp(s);
    }

    @Override
    public void warn(String s) {
        next.warn(s);
    }

    @Override
    public void warnp(String s) {
        next.warnp(s);
    }

    @Override
    public void error(String s) {
        next.error(s);
    }

    @Override
    public void errorp(String s) {
        next.errorp(s);
    }

    @Override
    public void debug(String s) {
        next.debug(s);
    }

    @Override
    public OutputStream out() {
        return next.out();
    }
}