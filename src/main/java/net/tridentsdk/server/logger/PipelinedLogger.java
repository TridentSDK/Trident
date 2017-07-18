/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.logger;

import javax.annotation.concurrent.Immutable;
import java.io.OutputStream;

/**
 * This class is the superclass of every logger in the
 * pipeline.
 *
 * <p>The server pipeline usually looks something like
 * this:</p>
 * <pre>{@code
 *              [Plugin Loggers]
 *                     ||
 *                     \/
 *                 FileLogger
 *                     ||
 *                     \/
 *             [Logger handlers]
 *                     ||
 *                    /  \
 *       NoDebugLogger ?? DebugLogger
 *                    \  /
 *                     ||
 *                     \/
 *               ColorizerLogger
 *                     ||
 *                     \/
 *                DefaultLogger
 * }</pre>
 */
@Immutable
public abstract class PipelinedLogger {
    /**
     * The next logger in the pipeline
     */
    protected final PipelinedLogger next;

    /**
     * The super constructor for the pipeline logger.
     *
     * @param next the next logger in the pipeline
     */
    public PipelinedLogger(PipelinedLogger next) {
        this.next = next;
    }

    /**
     * Initialization code
     *
     * @param verbose whether to enable verbose
     * @return a new logger
     *
     * @throws Exception pass down exception
     */
    public static PipelinedLogger init(boolean verbose) throws Exception {
        // tail of pipeline
        PipelinedLogger underlying = new DefaultLogger();
        PipelinedLogger colorizer = new ColorizerLogger(underlying);
        PipelinedLogger debugger = verbose ? DebugLogger.verbose(colorizer) : DebugLogger.noop(colorizer);
        PipelinedLogger handler = new LoggerHandlers(debugger);
        return FileLogger.init(handler); // head of pipeline
    }

    /**
     * Handles normal messages.
     *
     * @param msg the message
     * @return the same message
     */
    public abstract LogMessageImpl handle(LogMessageImpl msg);

    /**
     * Obtains the next logger in the pipeline.
     *
     * @return the next logger
     */
    public PipelinedLogger next() {
        return this.next;
    }

    /**
     * Logs a message
     *
     * @param msg the message
     */
    public void log(LogMessageImpl msg) {
        if (msg == null) return;
        this.next.log(this.handle(msg));
    }

    /**
     * Logs a message
     *
     * @param msg the message
     */
    public void success(LogMessageImpl msg) {
        if (msg == null) return;
        this.next.success(this.handle(msg));
    }

    /**
     * Logs a message
     *
     * @param msg the message
     */
    public void warn(LogMessageImpl msg) {
        if (msg == null) return;
        this.next.warn(this.handle(msg));
    }

    /**
     * Logs a message
     *
     * @param msg the message
     */
    public void error(LogMessageImpl msg) {
        if (msg == null) return;
        this.next.error(this.handle(msg));
    }

    /**
     * Logs a message
     *
     * @param msg the message
     */
    public void debug(LogMessageImpl msg) {
        if (msg == null) return;
        this.next.debug(this.handle(msg));
    }

    /**
     * Obtains the underlying output.
     *
     * @return the output
     */
    public OutputStream out() {
        return this.next.out();
    }
}