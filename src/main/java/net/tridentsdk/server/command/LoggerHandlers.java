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
 * This class contains the handlers that plugins may use to
 * change the output of the logger or their loggers.
 */
public class LoggerHandlers implements Logger {
    // TODO implement
    /**
     * The next logger in the pipeline
     */
    private final Logger next;

    /**
     * Creates a new logger handler interceptor with the
     * given logger next in the pipeline.
     *
     * @param next the next logger
     */
    public LoggerHandlers(Logger next) {
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