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

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This logger is normally regarded as the underlying
 * logger as it is designed to be the final logger in the
 * pipeline; it does not pass the logger messages any
 * further and logs its output directly to the output.
 *
 * <p>Thus such, this class contains the init code required
 * to setup the loggers.</p>
 */
public class DefaultLogger extends PipelinedLogger {
    /**
     * The underlying stream that to which output is passed
     */
    private final PrintStream stream = System.out;

    /**
     * Creates a new logger that prints messages to the out
     * stream at the bottom of the pipeline.
     */
    public DefaultLogger() {
        super(null);
    }

    @Override
    public LogMessageImpl handle(LogMessageImpl msg) {
        return null;
    }

    @Override
    public LogMessageImpl handlep(LogMessageImpl msg) {
        return null;
    }

    @Override
    public void log(LogMessageImpl msg) {
        System.out.println(msg.format(1));
    }

    @Override
    public void logp(LogMessageImpl msg) {
        System.out.print(msg.format(1));
    }

    @Override
    public void success(LogMessageImpl msg) {
        System.out.println(msg.format(1));
    }

    @Override
    public void successp(LogMessageImpl msg) {
        System.out.print(msg.format(1));
    }

    @Override
    public void warn(LogMessageImpl msg) {
        System.out.println(msg.format(1));
    }

    @Override
    public void warnp(LogMessageImpl msg) {
        System.out.print(msg.format(1));
    }

    @Override
    public void error(LogMessageImpl msg) {
        System.out.println(msg.format(1));
    }

    @Override
    public void errorp(LogMessageImpl msg) {
        System.out.print(msg.format(1));
    }

    @Override
    public void debug(LogMessageImpl msg) {
        System.out.println(msg.format(1));
    }

    @Override
    public OutputStream out() {
        return stream;
    }
}