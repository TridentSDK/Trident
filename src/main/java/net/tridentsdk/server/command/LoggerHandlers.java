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

import com.google.common.collect.Sets;
import net.tridentsdk.command.logger.LogHandler;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;

/**
 * This class contains the handlers that plugins may use to
 * change the output of the logger or their loggers.
 */
@ThreadSafe
public class LoggerHandlers extends PipelinedLogger {
    /**
     * The set of handlers that intercept all output
     */
    private final Set<LogHandler> handlers = Sets.newConcurrentHashSet();

    /**
     * Creates a new handler class for the all messages log
     * interceptors.
     *
     * @param next the next logger in the pipeline
     */
    public LoggerHandlers(PipelinedLogger next) {
        super(next);
    }

    @Override
    public LogMessageImpl handle(LogMessageImpl msg) {
        boolean doLog = true;
        for (LogHandler handler : handlers) {
            if (!handler.handle(msg)) {
                doLog = false;
            }
        }
        return doLog ? msg : null;
    }

    @Override
    public LogMessageImpl handlep(LogMessageImpl msg) {
        return handle(msg);
    }

    /**
     * Obtains the all the handlers that are attached to
     * the
     * output.
     *
     * @return the logger handlers
     */
    public Set<LogHandler> handlers() {
        return handlers;
    }
}