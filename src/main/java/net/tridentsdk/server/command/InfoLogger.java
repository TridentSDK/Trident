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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.tridentsdk.command.logger.Logger;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.concurrent.ExecutionException;

import static java.time.temporal.ChronoField.*;

/**
 * Not part of the pipeline, this logger is the surface
 * level logger that is given to plugins and appends the
 * information such as date and time and the logger name to
 * the message logged to the logger.
 */
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
    private static final Cache<String, InfoLogger> CACHE =
            CacheBuilder.newBuilder().build();

    // logger level constants
    private static final String INFO = "INFO";
    private static final String WARN = "WARN";
    private static final String ERROR = "ERROR";
    private static final String DEBUG = "DEBUG";

    /**
     * The lock that guards the given last partial write
     */
    private static final Object lock = new Object();
    /**
     * The last logger to use the partial write method
     */
    @GuardedBy("lock")
    private static Logger p = null;

    /**
     * The top logger in the pipeline
     */
    private final PipelinedLogger next;
    /**
     * The name of this logger
     */
    private final String name;
    /**
     * Underlying stream prevents a full pipeline read
     */
    private final PrintStream underlying;

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
        this.underlying = (PrintStream) next.out();
    }

    /**
     * Gets a logger from the cache
     *
     * @param next the next logger
     * @param name the name of the logger from the cache
     * @return a cached logger, or a new one
     */
    public static Logger get(PipelinedLogger next, String name) {
        try {
            return CACHE.get(name, () -> new InfoLogger(next, name));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles normal messages
     */
    private LogMessageImpl handle(String level, String s) {
        ZonedDateTime time = ZonedDateTime.now();
        String[] components = new String[]{time.format(DATE_FORMAT),
                time.format(TIME_FORMAT),
                "[" + name + "/" + level + "]"};
        boolean noInfo = false;

        synchronized (lock) {
            // if the last partial output was this, then it
            // will be printed on the same line
            if (p == this) {
                noInfo = true;
                // otherwise, if it is another logger, then we
                // log the message as if the last one wasn't
                // partial in order to allow server owners to
                // see what time the operation completed
            } else if (p != null) {
                underlying.println();
            }

            // null the last partial message as this is a
            // full message
            p = null;
        }

        return super.handle(new LogMessageImpl(this, components, s, time, noInfo));
    }

    /**
     * Handles partial messages
     */
    private LogMessageImpl handlep(String level, String s) {
        synchronized (lock) {
            // if the last message was sent by another
            // logger, then we print this partial one on
            // another line
            if (p != null && p != this) {
                underlying.println();
            }

            // set the last partial message to this logger
            p = this;
        }

        ZonedDateTime time = ZonedDateTime.now();
        String[] components = new String[]{time.format(DATE_FORMAT),
                time.format(TIME_FORMAT),
                "[" + name + "/" + level + "]"};
        LogMessageImpl message = new LogMessageImpl(this, components, s, time, false);
        return super.handle(message);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void log(String s) {
        next.log(handle(INFO, s));
    }

    @Override
    public void logp(String s) {
        next.logp(handlep(INFO, s));
    }

    @Override
    public void success(String s) {
        next.success(handle(INFO, s));
    }

    @Override
    public void successp(String s) {
        next.successp(handlep(INFO, s));
    }

    @Override
    public void warn(String s) {
        next.warn(handle(WARN, s));
    }

    @Override
    public void warnp(String s) {
        next.warnp(handlep(WARN, s));
    }

    @Override
    public void error(String s) {
        next.error(handle(ERROR, s));
    }

    @Override
    public void errorp(String s) {
        next.errorp(handlep(ERROR, s));
    }

    @Override
    public void debug(String s) {
        next.debug(handle(DEBUG, s));
    }

    @Override
    public OutputStream out() {
        return next.out();
    }
}