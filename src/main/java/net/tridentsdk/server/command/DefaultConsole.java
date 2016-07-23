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

import java.io.PrintStream;

/**
 * This console is normally regarded as the underlying
 * console as it is designed to be the final console in the
 * pipeline; it does not pass the logger messages any
 * further and logs its output directly to the output.
 * <p>
 * <p>Thus such, this class contains the init code required
 * to setup the loggers.</p>
 */
public class DefaultConsole implements Console {
    // TODO use system.out
    // also system.err
    private final PrintStream stream = System.out;

    @Override
    public void log(String s) {
        stream.println(s);
    }

    @Override
    public void success(String s) {
        stream.println(s);
    }

    @Override
    public void warn(String s) {
        stream.println(s);
    }

    @Override
    public void error(String s) {
        stream.println(s);
    }

    @Override
    public void debug(String s) {
        stream.println(s);
    }
}