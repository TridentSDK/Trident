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

import net.tridentsdk.chat.ChatColor;

import javax.annotation.concurrent.Immutable;

/**
 * Colorizer logger is a pipeline logger which replaces
 * color codes with the ANSI equivalents before passing to
 * the next logger.
 */
@Immutable
public class ColorizerLogger extends PipelinedLogger {
    // Console colors
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BOLD = "\u001B[1m";
    public static final String ITALICS = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String STRIKETHROUGH = "\u001B[9m";

    /**
     * Creates a new logger that colorizes the output
     *
     * @param next the next logger in the pipeline
     */
    public ColorizerLogger(PipelinedLogger next) {
        super(next);
    }

    @Override
    public LogMessageImpl handle(LogMessageImpl msg) {
        char[] sq = msg.getMessage().toCharArray();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sq.length; i++) {
            char c = sq[i];
            if (c == '\u00A7') {
                int codeIdx = ++i;
                if (codeIdx >= sq.length) {
                    break;
                } else {
                    ChatColor decode = ChatColor.of(sq[codeIdx]);
                    switch (decode) {
                        case BLACK:
                            builder.append(BLACK);
                            break;
                        case DARK_BLUE:
                            builder.append(BLUE);
                            break;
                        case DARK_GREEN:
                            builder.append(GREEN);
                            break;
                        case DARK_AQUA:
                            builder.append(CYAN);
                            break;
                        case DARK_RED:
                            builder.append(RED);
                            break;
                        case DARK_PURPLE:
                            builder.append(PURPLE);
                            break;
                        case GOLD:
                            builder.append(YELLOW);
                            break;
                        case GRAY:
                            builder.append(WHITE);
                            break;
                        case DARK_GRAY:
                            builder.append(WHITE);
                            break;
                        case BLUE:
                            builder.append(BLUE);
                            break;
                        case GREEN:
                            builder.append(GREEN);
                            break;
                        case AQUA:
                            builder.append(CYAN);
                            break;
                        case RED:
                            builder.append(RED);
                            break;
                        case LIGHT_PURPLE:
                            builder.append(PURPLE);
                            break;
                        case YELLOW:
                            builder.append(YELLOW);
                            break;
                        case WHITE:
                            builder.append(WHITE);
                            break;
                        case OBFUSCATED:
                            // obfuscated is the only chat
                            // color without an ansi equiv.
                            break;
                        case BOLD:
                            builder.append(BOLD);
                            break;
                        case STRIKETHROUGH:
                            builder.append(STRIKETHROUGH);
                            break;
                        case UNDERLINE:
                            builder.append(UNDERLINE);
                            break;
                        case ITALIC:
                            builder.append(ITALICS);
                            break;
                        case RESET:
                            builder.append(RESET);
                            break;
                    }
                }
            } else {
                builder.append(c);
            }
        }

        msg.setMessage(builder.toString() + RESET);
        return msg;
    }

    /**
     * Shorthand method for handling the input message to
     * log.
     */
    private LogMessageImpl handle(String color, LogMessageImpl msg) {
        msg.setMessage(color + msg.getMessage() + RESET);
        return msg;
    }

    @Override
    public void log(LogMessageImpl msg) {
        this.next.log(this.handle(msg));
    }

    @Override
    public void success(LogMessageImpl msg) {
        this.next.success(this.handle(GREEN, msg));
    }

    @Override
    public void warn(LogMessageImpl msg) {
        this.next.warn(this.handle(YELLOW, msg));
    }

    @Override
    public void error(LogMessageImpl msg) {
        this.next.error(this.handle(RED, msg));
    }

    @Override
    public void debug(LogMessageImpl msg) {
        this.next.debug(this.handle(WHITE, msg));
    }
}
