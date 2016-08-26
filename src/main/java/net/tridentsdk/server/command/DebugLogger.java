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

import javax.annotation.concurrent.Immutable;

/**
 * Logger filter which prevents debug messages from being
 * passed on.
 */
@Immutable
class NoDebugLogger extends PipelinedLogger {
    /**
     * Creates a new logger that handles debug messages
     * without printing it.
     *
     * @param next the next logger in the pipeline
     */
    public NoDebugLogger(PipelinedLogger next) {
        super(next);
    }

    @Override
    public LogMessageImpl handle(LogMessageImpl msg) {
        return msg;
    }

    @Override
    public void debug(LogMessageImpl msg) {
        // No-op
    }
}

/**
 * A debug filter logger which allows debug messages to be
 * passed along the pipeline, useful for verbose mode.
 */
@Immutable
public class DebugLogger extends PipelinedLogger {
    /**
     * Create a new logger which logs to the next
     * system specific logger.
     *
     * @param next the next logger in the pipeline
     */
    protected DebugLogger(PipelinedLogger next) {
        super(next);
    }

    /**
     * Creates a verbose logger filter
     *
     * @param next the next logger to which
     * the filter will pass messages
     * @return a new instance of the logger filter
     */
    public static PipelinedLogger verbose(PipelinedLogger next) {
        return new DebugLogger(next);
    }

    /**
     * Creates a non-verbose logger filter which removes
     * debug messages from the pipeline
     *
     * @param next the next logger to which
     * the filter will pass messages
     * @return a new instance of the logger filter
     */
    public static PipelinedLogger noop(PipelinedLogger next) {
        return new NoDebugLogger(next);
    }

    @Override
    public LogMessageImpl handle(LogMessageImpl msg) {
        return msg;
    }
}