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
 * Colorizer console is a pipeline console which replaces
 * color codes with the ANSI equivalents before passing to
 * the next console.
 */
public class ColorizerConsole implements Logger {
    // TODO ansi
    // TODO chat color

    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static void main(String[] args) {
        System.out.println(RED + "lol" + RESET);
    }

    /**
     * The next console in the pipeline
     */
    private final Logger next;

    /**
     * Creates a new console that colorizes the output
     *
     * @param next the next console in the pipeline
     */
    public ColorizerConsole(Logger next) {
        this.next = next;
    }

    /**
     * Shorthand method for handling the input message to
     * log.
     */
    // TODO interpret chatcolors when they are added
    private String handle(String color, String msg) {
        return color + msg + RESET;
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
        next.success(handle(GREEN, s));
    }

    @Override
    public void successp(String s) {
        next.successp(handle(GREEN, s));
    }

    @Override
    public void warn(String s) {
        next.warn(handle(YELLOW, s));
    }

    @Override
    public void warnp(String s) {
        next.warnp(handle(YELLOW, s));
    }

    @Override
    public void error(String s) {
        next.error(handle(RED, s));
    }

    @Override
    public void errorp(String s) {
        next.errorp(handle(RED, s));
    }

    @Override
    public void debug(String s) {
        next.debug(handle(WHITE, s));
    }

    @Override
    public OutputStream out() {
        return next.out();
    }
}