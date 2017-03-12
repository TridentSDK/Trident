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
package net.tridentsdk.server.command;

import net.tridentsdk.command.logger.LogMessage;

import javax.annotation.concurrent.ThreadSafe;
import java.time.ZonedDateTime;

/**
 * This class represents a message sent by a logger
 */
@ThreadSafe
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
    private volatile String message;
    /**
     * The time at which this log message was created
     */
    private final ZonedDateTime time;

    /**
     * Creates a new log message
     *
     * @param source the logger that created the message
     * @param components additional related info
     * @param message the message that was passed
     * @param time the time the message was created
     */
    public LogMessageImpl(InfoLogger source, String[] components,
                          String message, ZonedDateTime time) {
        this.source = source;
        this.components = components;
        this.message = message;
        this.time = time;
    }

    @Override
    public InfoLogger getLogger() {
        return this.source;
    }

    @Override
    public String[] getComponents() {
        return this.components;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public ZonedDateTime getTime() {
        return this.time;
    }

    /**
     * Appends all of the components and the message.
     *
     * @param start the start of the component index to add
     * @return the string
     */
    public String format(int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < this.components.length; i++) {
            builder.append(this.components[i]).append(' ');
        }

        builder.append(this.message);
        return builder.toString();
    }
}
