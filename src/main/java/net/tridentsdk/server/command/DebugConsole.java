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

import net.tridentsdk.command.Console;

/**
 * Console filter which prevents debug messages from being
 * passed on.
 */
class NoDebugConsole extends DebugConsole {
    public NoDebugConsole(Console underlying) {
        super(underlying);
    }

    @Override
    public void debug(String s) {
        // No op
    }
}

/**
 * A debug filter console which allows debug messages to be
 * passed along the pipeline, useful for verbose mode.
 */
public class DebugConsole implements Console {
    /**
     * The underlying console which logs the messages given
     * by the (no)debug console to the shell
     */
    private final Console underlying;

    /**
     * Create a new console which logs to the underlying
     * system specific console.
     *
     * @param underlying the next console in the pipeline
     */
    protected DebugConsole(Console underlying) {
        this.underlying = underlying;
    }

    /**
     * Creates a verbose console filter
     *
     * @param underlying the underlying console to which
     *                   the filter will pass messages
     * @return a new instance of the console filter
     */
    public static Console verbose(Console underlying) {
        return new DebugConsole(underlying);
    }

    /**
     * Creates a non-verbose console filter which removes
     * debug messages from the pipeline
     *
     * @param underlying the underlying console to which
     *                   the filter will pass messages
     * @return a new instance of the console filter
     */
    public static Console noop(Console underlying) {
        return new NoDebugConsole(underlying);
    }

    @Override
    public void log(String s) {
        underlying.log(s);
    }

    @Override
    public void success(String s) {
        underlying.success(s);
    }

    @Override
    public void warn(String s) {
        underlying.warn(s);
    }

    @Override
    public void error(String s) {
        underlying.error(s);
    }

    @Override
    public void debug(String s) {
        underlying.debug(s);
    }
}