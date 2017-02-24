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

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import javax.annotation.Nonnull;
import java.io.PrintStream;

/**
 * A few custom overrides of PrintStream in order to produce
 * the command-line-esque effect.
 */
public class TridentPrintStream extends PrintStream {
    public TridentPrintStream() {
        super(AnsiConsole.out);
    }

    @Override
    public void println(String line) {
        synchronized (this) {
            super.print(Ansi.ansi().cursorLeft(2) + line);
            super.println();
            super.print("$ ");
            super.flush();
        }
    }

    @Override
    public void println(Object x) {
        this.println(x.toString());
    }
    
    @Override
    public void println() {
        this.println("");
    }

    @Override
    public void println(boolean x) {
        this.println(x ? "true" : "false");
    }

    @Override
    public void println(char x) {
        this.println(String.valueOf(x));
    }

    @Override
    public void println(int x) {
        this.println(String.valueOf(x));
    }

    @Override
    public void println(long x) {
        this.println(String.valueOf(x));
    }

    @Override
    public void println(float x) {
        this.println(String.valueOf(x));
    }

    @Override
    public void println(double x) {
        this.println(String.valueOf(x));
    }

    @Override
    public void println(@Nonnull char x[]) {
        this.println(new String(x));
    }
}