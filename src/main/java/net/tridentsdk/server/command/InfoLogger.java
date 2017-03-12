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

import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.doc.Policy;

import javax.annotation.concurrent.ThreadSafe;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.temporal.ChronoField.*;

/**
 * Not part of the pipeline, this logger is the surface
 * level logger that is given to plugins and appends the
 * information such as date and time and the logger name to
 * the message logged to the logger.
 */
@Policy("not pipelined")
@ThreadSafe
public class InfoLogger extends LoggerHandlers implements Logger {
    /**
     * The time, using standard 24-hour format
     */
    private static final DateTimeFormatter TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();
    /**
     * The date, using MMM DD YYYY
     */
    private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendText(MONTH_OF_YEAR, TextStyle.SHORT)
            .appendLiteral(' ')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .appendValue(YEAR, 4)
            .toFormatter();
    /**
     * The logger cache
     */
    private static final Map<String, InfoLogger> CACHE = new ConcurrentHashMap<>();

    // logger level constants
    private static final String INFO = "INFO";
    private static final String WARN = "WARN";
    private static final String ERROR = "ERROR";
    private static final String DEBUG = "DEBUG";

    /**
     * The top logger in the pipeline
     */
    private final PipelinedLogger next;
    /**
     * The name of this logger
     */
    private final String name;

    /**
     * Creates a new logger handler interceptor with the
     * given String the name to format the output.
     *
     * @param name the name
     */
    public InfoLogger(PipelinedLogger next, String name) {
        super(null);
        this.next = next;
        this.name = name;
    }

    /**
     * Gets a logger from the cache
     *
     * @param next the next logger
     * @param name the name of the logger from the cache
     * @return a cached logger, or a new one
     */
    public static Logger get(PipelinedLogger next, String name) {
        return CACHE.computeIfAbsent(name, (k) -> new InfoLogger(next, name));
    }

    /**
     * Handles normal messages
     */
    private LogMessageImpl handle(String level, String s) {
        ZonedDateTime time = ZonedDateTime.now();
        String[] components = new String[]{time.format(DATE_FORMAT),
                time.format(TIME_FORMAT),
                "[" + this.name + "/" + level + "]"};
        return super.handle(new LogMessageImpl(this, components, s, time));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void log(String s) {
        this.next.log(this.handle(INFO, s));
    }

    @Override
    public void success(String s) {
        this.next.success(this.handle(INFO, s));
    }

    @Override
    public void warn(String s) {
        this.next.warn(this.handle(WARN, s));
    }

    @Override
    public void error(String s) {
        this.next.error(this.handle(ERROR, s));
    }

    @Override
    public void debug(String s) {
        this.next.debug(this.handle(DEBUG, s));
    }

    @Override
    public OutputStream getOutputStream() {
        return this.next.out();
    }
}
