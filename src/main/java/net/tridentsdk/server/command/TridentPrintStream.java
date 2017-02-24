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

import org.fusesource.jansi.AnsiConsole;

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
            super.print("\r" + line);
            super.println();
            super.print("$ ");
            super.flush();
        }
    }

    @Override
    public void println(Object x) {
        synchronized (this) {
            super.print("\r" + x.toString());
            super.println();
            super.print("$ ");
            super.flush();
        }
    }
    
    @Override
    public void println() {
        this.println("");
    }

    @Override
    public void println(boolean x) {
        synchronized (this) {
            this.print(x);
            this.println();
        }
    }

    @Override
    public void println(char x) {
        synchronized (this) {
            this.print(x);
            this.println();
        }
    }

    @Override
    public void println(int x) {
        synchronized (this) {
            this.print(x);
            this.println();
        }
    }

    @Override
    public void println(long x) {
        synchronized (this) {
            this.print(x);
            this.println();
        }
    }

    @Override
    public void println(float x) {
        synchronized (this) {
            this.print(x);
            this.println();
        }
    }

    @Override
    public void println(double x) {
        synchronized (this) {
            this.print(x);
            this.println();
        }
    }

    @Override
    public void println(char x[]) {
        synchronized (this) {
            this.print(x);
            this.println();
        }
    }
}