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

import net.tridentsdk.command.logger.LogMessage;

import javax.annotation.concurrent.Immutable;
import java.time.ZonedDateTime;

/**
 * This class represents a message sent by a logger
 */
@Immutable
public class LogMessageImpl implements LogMessage {
    /**
     * The logger that sent the message
     */
    private final InfoLogger source;
    /**
     * The components to be added to the output
     */
    private final String[] components;
    /**
     * The message that was passed to the logger
     */
    private final String message;
    /**
     * The time at which this log message was created
     */
    private final ZonedDateTime time;
    /**
     * Whether this is printed after a partial line
     */
    private final boolean noInfo;

    /**
     * Creates a new log message
     *
     * @param source the logger that created the message
     * @param components additional related info
     * @param message the message that was passed
     * @param time the time the message was created
     */
    public LogMessageImpl(InfoLogger source, String[] components,
                          String message, ZonedDateTime time,
                          boolean noInfo) {
        this.source = source;
        this.components = components;
        this.message = message;
        this.time = time;
        this.noInfo = noInfo;
    }

    @Override
    public InfoLogger source() {
        return source;
    }

    @Override
    public String[] components() {
        return components;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public ZonedDateTime time() {
        return time;
    }

    @Override
    public boolean noInfo() {
        return noInfo;
    }

    /**
     * Appends all of the components and the message.
     *
     * @param start the start of the component index to add
     * @return the string
     */
    public String format(int start) {
        if (noInfo && start != 0) {
            return message;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = start; i < components.length; i++) {
            builder.append(components[i]).append(' ');
        }

        builder.append(message);
        return builder.toString();
    }
}