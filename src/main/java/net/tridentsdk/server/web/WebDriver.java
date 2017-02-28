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

import fi.iki.elonen.NanoHTTPD;
import net.tridentsdk.command.logger.Logger;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class WebDriver extends NanoHTTPD {
    
    /**
     * The logger used for debugging
     */
    private static final Logger LOGGER = Logger.get(WebDriver.class);
    
    private final WebSocketDriver webSocket;
    
    public WebDriver(String address, int port) throws Exception {
        super(address, port);
        start();
    
        webSocket = new WebSocketDriver(address, port + 1);
        
        String url = address;
        
        if(url.equals("0.0.0.0")){
            url = "localhost";
        }
    
        LOGGER.log("Web Admin started on http://" + url + ":" + port);
        LOGGER.log("Websocket started on port " + (port + 1));
    }
    
    @Override
    public Response serve(IHTTPSession session) {
        switch(session.getUri()){
            case "/css/bootstrap.min.css":
            case "/js/ace.min.js":
            case "/js/bootstrap.min.js":
            case "/js/jquery.min.js":
            case "/js/tether.min.js":
            case "/css/style.css":
                String mimeType = session.getUri().startsWith("/js") ? "text/js" : "text/css";
                return newChunkedResponse(Response.Status.OK, mimeType, WebDriver.class.getResourceAsStream("/web" + session.getUri()));
            case "/logo.png":
                return newChunkedResponse(Response.Status.OK, "image/png", WebDriver.class.getResourceAsStream("/web" + session.getUri()));
            case "/":
                try{
                    JtwigTemplate template = JtwigTemplate.classpathTemplate("web/templates/console.twig");
                    return newFixedLengthResponse(template.render(JtwigModel.newModel()));
                }catch(Exception e){
                    e.printStackTrace();
                }
        }
        
        
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html", "<center><h1>404</h1></center>");
    }
    
    
    public static void run(String address, int port){
        Thread thread = new Thread(() -> {
            try{
                new WebDriver(address, port);
            }catch(Exception e){
                e.printStackTrace();
            }
        });
        
        thread.setDaemon(true);
        thread.start();
    }
    
}
