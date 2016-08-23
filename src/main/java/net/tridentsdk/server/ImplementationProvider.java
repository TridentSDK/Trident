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
package net.tridentsdk.server;

import net.tridentsdk.Impl;
import net.tridentsdk.Server;
import net.tridentsdk.command.logger.LogHandler;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.config.Config;
import net.tridentsdk.server.command.InfoLogger;
import net.tridentsdk.server.command.LoggerHandlers;
import net.tridentsdk.server.command.PipelinedLogger;
import net.tridentsdk.server.config.TridentConfig;

import javax.annotation.concurrent.Immutable;
import java.nio.file.Path;

/**
 * This class is the bridge between the server and the API,
 * and provides the implementation classes for the API via
 * {@link Impl}.
 */
@Immutable
public class ImplementationProvider implements Impl.ImplementationProvider {
    // class is initialized after server is created
    // safe to call this method
    private static final TridentServer inst = TridentServer.instance();
    // head of the logger pipeline
    private final PipelinedLogger head;
    // instance of the handlers class
    private final LoggerHandlers handlers;

    public ImplementationProvider(PipelinedLogger head) {
        this.head = head;

        for (PipelinedLogger logger = head; logger.next() != null; logger = logger.next()) {
            if (logger.getClass().equals(LoggerHandlers.class)) {
                this.handlers = (LoggerHandlers) logger;
                return;
            }
        }
        this.handlers = null;
    }

    @Override
    public Server svr() {
        return inst;
    }

    @Override
    public Config newCfg(Path p) {
        return TridentConfig.load(p);
    }

    @Override
    public Logger newLogger(String s) {
        return InfoLogger.get(this.head, s);
    }

    @Override
    public void attachHandler(Logger logger, LogHandler handler) {
        if (logger == null) {
            this.handlers.handlers().add(handler);
        } else {
            ((InfoLogger) logger).handlers().add(handler);
        }
    }

    @Override
    public boolean removeHandler(Logger logger, LogHandler handler) {
        if (logger == null) {
            return this.handlers.handlers().remove(handler);
        } else {
            InfoLogger info = (InfoLogger) logger;
            return info.handlers().remove(handler);
        }
    }
}