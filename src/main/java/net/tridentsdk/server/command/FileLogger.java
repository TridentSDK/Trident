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
import net.tridentsdk.util.Misc;

import javax.annotation.concurrent.GuardedBy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * This class represents the file logger which writes the
 * messages sent by loggers to the log file.
 *
 * <p>In addition, this class also manages the log files,
 * moving them to appropriate directories when they fill to
 * the text editor limits.</p>
 *
 * <p>This class is also the first logger in the server
 * logger pipeline, because plugins must call to this logger
 * in order for it to log plugin messages to the file as
 * well.</p>
 *
 * <p>The server pipeline usually looks something like this:
 * <pre>{@code
 *              [Plugin Loggers]
 *                     ||
 *                     \/
 *               FileLogger
 *                     ||
 *                     \/
 *             [Logger handlers]
 *                     ||
 *                    /  \
 *      NoDebugLogger ?? DebugLogger
 *                    \  /
 *                     ||
 *                     \/
 *               ColorizerLogger
 *                     ||
 *                     \/
 *                DefaultLogger
 * }</pre></p>
 */
public class FileLogger implements Logger {
    /**
     * The directory to the log files
     */
    private static final Path DIR = Paths.get(Misc.HOME, "/logs");
    /**
     * Max file length, 80 mb
     */
    private static final int MAX_LEN = 83886080;
    /**
     * The index separator for the
     */
    public static final String IDX_SEPARATOR = Pattern.quote(".");

    /**
     * The next logger in the pipeline
     */
    private final Logger next;

    /**
     * The file writer
     */
    @GuardedBy("lock")
    private BufferedWriter out;
    /**
     * Current log file
     */
    @GuardedBy("lock")
    private Path current;
    /**
     * The file writer lock
     */
    private final Object lock = new Object();

    /**
     * Creates a new log file logger, which logs items to
     * a file before being sent to the underlying logger.
     *
     * @param next the next logger in the pipeline
     */
    private FileLogger(Logger next) {
        this.next = next;
    }

    /**
     * Initializes the files and directories, attempts to
     * find the last log file.
     */
    public static FileLogger init(Logger next) throws Exception {
        FileLogger logger = new FileLogger(next);

        if (!Files.exists(DIR)) {
            Files.createDirectory(DIR);
        }

        File[] files = DIR.toFile().listFiles();
        if (files != null && files.length > 0) {
            int idx = -1;
            File f = null;
            for (File file : files) {
                String[] split = file.getName().split(IDX_SEPARATOR);
                int i = Integer.parseInt(split[1]);
                if (i > idx) {
                    idx = i;
                    f = file;
                }
            }

            if (f == null) throw new RuntimeException();

            synchronized (logger.lock) {
                logger.makeNewLog(f.toPath());
            }
        } else {
            synchronized (logger.lock) {
                logger.makeNewLog(0);
            }
        }

        return logger;
    }

    /**
     * Handles normal messages.
     *
     * @param s the message
     * @return the same message
     */
    public String handle(String s) {
        check();
        BufferedWriter out;
        synchronized (lock) {
            out = this.out;
        }

        String time = ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        try {
            out.write(time + " " + s);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return s;
    }

    /**
     * Handles partial messages.
     *
     * @param s the message
     * @return the same partial message
     */
    public String handlep(String s) {
        check();
        BufferedWriter out;
        synchronized (lock) {
            out = this.out;
        }

        String time = ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        try {
            out.write(time + " " + s);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return s;
    }

    /**
     * Checks if the file needs to be truncated
     */
    public void check() {
        if (ThreadLocalRandom.current().nextInt(20) == 1) {
            synchronized (lock) {
                try {
                    if (Files.size(current) > MAX_LEN) {
                        makeNewLog(current);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Creates a new log file
     *
     * @param last the last log file
     * @throws IOException if something dumb went wrong
     */
    public void makeNewLog(Path last) throws IOException {
        String[] split = last.toFile().getName().split(IDX_SEPARATOR);
        int curIdx = Integer.parseInt(split[1]) + 1;
        makeNewLog(curIdx);
    }

    /**
     * Creates a new log file based from the new index
     *
     * @param idx the new index
     * @throws IOException if something dumb went wrong
     */
    public void makeNewLog(int idx) throws IOException {
        Path path = DIR.resolve("log." + idx + ".log");
        Files.createFile(path);

        current = path;
        out = Files.newBufferedWriter(path);
    }
    
    @Override
    public void log(String s) {
        next.log(handle(s));
    }

    @Override
    public void logp(String s) {
        next.logp(handlep(s));
    }

    @Override
    public void success(String s) {
        next.success(handle(s));
    }

    @Override
    public void successp(String s) {
        next.successp(handlep(s));
    }

    @Override
    public void warn(String s) {
        next.warn(handle(s));
    }

    @Override
    public void warnp(String s) {
        next.warnp(handlep(s));
    }

    @Override
    public void error(String s) {
        next.error(handle(s));
    }

    @Override
    public void errorp(String s) {
        next.errorp(handlep(s));
    }

    @Override
    public void debug(String s) {
        next.debug(handle(s));
    }

    @Override
    public OutputStream out() {
        return next.out();
    }
}