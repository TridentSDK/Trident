package net.tridentsdk.server.web;

import fi.iki.elonen.NanoWSD;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CopyOnWriteArrayList;


public class WebSocketDriver extends NanoWSD {
    
    private final CopyOnWriteArrayList<WebSocket> sockets;
    
    protected WebSocketDriver(String hostname, int port) throws Exception {
        super(hostname, port);
        start();
        
        sockets = new CopyOnWriteArrayList<>();
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
        }
        
        @Override
        protected void onPong(WebSocketFrame pong) {
        }
        
        @Override
        protected void onException(IOException exception) {
            if(!(exception instanceof SocketTimeoutException)){
                exception.printStackTrace();
            }
        }
        
    }
    
}
