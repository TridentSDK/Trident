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
 * Logger filter which prevents debug messages from being
 * passed on.
 */
class NoDebugLogger extends DebugLogger {
    public NoDebugLogger(Logger underlying) {
        super(underlying);
    }

    @Override
    public void debug(String s) {
        // No op
    }
}

/**
 * A debug filter logger which allows debug messages to be
 * passed along the pipeline, useful for verbose mode.
 */
public class DebugLogger implements Logger {
    /**
     * The next logger which logs the messages given
     * by the (no)debug logger to the shell
     */
    private final Logger next;

    /**
     * Create a new logger which logs to the next
     * system specific logger.
     *
     * @param underlying the next logger in the pipeline
     */
    protected DebugLogger(Logger underlying) {
        this.next = underlying;
    }

    /**
     * Creates a verbose logger filter
     *
     * @param underlying the next logger to which
     *                   the filter will pass messages
     * @return a new instance of the logger filter
     */
    public static Logger verbose(Logger underlying) {
        return new DebugLogger(underlying);
    }

    /**
     * Creates a non-verbose logger filter which removes
     * debug messages from the pipeline
     *
     * @param underlying the next logger to which
     *                   the filter will pass messages
     * @return a new instance of the logger filter
     */
    public static Logger noop(Logger underlying) {
        return new NoDebugLogger(underlying);
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