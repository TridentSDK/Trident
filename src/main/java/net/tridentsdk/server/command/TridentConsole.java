package net.tridentsdk.server.command;

import net.tridentsdk.plugin.cmd.Console;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.util.TridentLogger;

public class TridentConsole implements Console {

    private volatile String lastCommand;
    private volatile String lastMessage;

    @Override
    public void invokeCommand(String message) {
        TridentServer.getInstance().commandHandler().handleCommand(message, this);
        lastCommand = message;
    }

    @Override
    public String lastCommand() {
        return lastCommand;
    }

    @Override
    public void sendRaw(String... messages) {
        // TODO: convert MessageBuilder json to console output

        for(String s : messages) {
            TridentLogger.log(s);
        }

        lastMessage = messages[messages.length - 1];
    }

    @Override
    public String lastMessage() {
        return lastMessage;
    }
}
