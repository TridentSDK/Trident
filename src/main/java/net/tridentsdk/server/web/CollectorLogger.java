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
package net.tridentsdk.server.web;

import net.tridentsdk.server.command.LogMessageImpl;
import net.tridentsdk.server.command.PipelinedLogger;
import net.tridentsdk.util.LimitedConcurrentLinkedQueue;

import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CollectorLogger extends PipelinedLogger {
    
    /**
     * The limit of how many log lines are cached
     */
    private static final int LOG_LIMIT = 5000;
    
    private static CollectorLogger instance;
    
    private final LimitedConcurrentLinkedQueue<LogMessageImpl> messages;
    private final HashSet<Consumer<LogMessageImpl>> listeners;
    
    /**
     * Creates a logger that collects the past few log lines.
     *
     * @param next the next logger in the pipeline
     */
    public CollectorLogger(PipelinedLogger next){
        super(next);
        
        instance = this;
        
        this.messages = new LimitedConcurrentLinkedQueue<>(LOG_LIMIT);
        this.listeners = new HashSet<>();
    }
    
    public static CollectorLogger getInstance(){
        return instance;
    }
    
    @Override
    public LogMessageImpl handle(LogMessageImpl msg){
        messages.add(msg);
        listeners.forEach(l -> l.accept(msg));
        return msg;
    }
    
    public List<LogMessageImpl> getLastNLines(int count){
        return messages.stream().limit(count).collect(Collectors.toList());
    }
    
    public void registerListener(Consumer<LogMessageImpl> consumer){
        listeners.add(consumer);
    }
    
}
