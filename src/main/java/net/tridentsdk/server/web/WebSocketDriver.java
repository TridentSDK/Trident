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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.iki.elonen.NanoWSD;
import net.tridentsdk.server.command.LogMessageImpl;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class WebSocketDriver extends NanoWSD {
    
    /**
     * How many log lines to send on initial call
     */
    private static final int INITIAL_LOG_LINES = 250;
    
    private final CopyOnWriteArrayList<WebSocket> sockets;
    
    protected WebSocketDriver(String hostname, int port) throws Exception {
        super(hostname, port);
        
        // We can use infinite timeout, as it will never have a lot of connections
        start(0, true);
        
        sockets = new CopyOnWriteArrayList<>();
    
        Thread logThread = new Thread(() -> {
            CopyOnWriteArrayList<LogMessageImpl> logList = new CopyOnWriteArrayList<>();
            CollectorLogger.getInstance().registerListener(logList::add);
            
            while(true){
    
                if(logList.size() > 0){
                    List<LogMessageImpl> lines = (List<LogMessageImpl>) logList.clone();
                    logList.clear();
    
                    JsonArray jsonLines = new JsonArray();
    
                    for(int i = 0; i < lines.size(); i++){
                        jsonLines.add(lines.get(i).format(1));
                    }
    
                    JsonObject object = new JsonObject();
                    object.add("type", new JsonPrimitive("2"));
                    object.add("lines", jsonLines);
                    broadcast(object.toString());
                }
                
                try{
                    Thread.sleep(500);
                }catch(InterruptedException e){
                    return;
                }
            }
        });
        
        logThread.setDaemon(true);
        logThread.start();
    }
    
    @Override
    protected NanoWSD.WebSocket openWebSocket(IHTTPSession session){
        return new WebSocket(session);
    }
    
    public void broadcast(String message){
        sockets.forEach(socket -> {
            try{
                socket.send(message);
            }catch(IOException e){
                e.printStackTrace();
            }
        });
    }
    
    private class WebSocket extends NanoWSD.WebSocket {
    
        protected WebSocket(IHTTPSession session) {
            super(session);
            sockets.add(this);
        }
        
        @Override
        protected void onOpen() {
        }
        
        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean byRemote) {
            sockets.remove(this);
        }
        
        @Override
        protected void onMessage(WebSocketFrame message) {
            switch(message.getTextPayload()){
                case "1":
                    try{
                        JsonArray jsonLines = new JsonArray();
                        List<LogMessageImpl> lines = CollectorLogger.getInstance().getLastNLines(INITIAL_LOG_LINES);
    
                        for(int i = 0; i < lines.size(); i++){
                            jsonLines.add(lines.get(i).format(1));
                        }
    
                        JsonObject object = new JsonObject();
                        object.add("type", new JsonPrimitive("1"));
                        object.add("log", jsonLines);
                        
                        send(object.toString());
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
        
        @Override
        protected void onPong(WebSocketFrame pong) {
        }
        
        @Override
        protected void onException(IOException exception) {
            if(!(exception instanceof SocketTimeoutException) && !(exception instanceof SocketException)){
                exception.printStackTrace();
            }
        }
        
    }
    
}
