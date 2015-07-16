/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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

import net.tridentsdk.Handler;
import net.tridentsdk.permission.Permission;
import net.tridentsdk.plugin.cmd.ServerConsole;
import net.tridentsdk.util.TridentLogger;

public class TridentConsole implements ServerConsole {
    private volatile String lastCommand;
    private volatile String lastMessage;

    @Override
    public void runCommand(String message) {
        Handler.forCommands().handleCommand(message, this);
        lastCommand = message;
    }

    @Override
    public String getLastCommand() {
        return lastCommand;
    }

    @Override
    public boolean isOperator() {
        return true;
    }

	@Override
	public void setOperator(boolean op) {
		
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return true;
	}
	
	public void setPermission(Permission permission, boolean enabled) {
		
	}

    @Override
    public void sendRaw(String... messages) {
        // TODO: convert MessageBuilder json to console output

        for (String s : messages) {
            TridentLogger.log(s);
        }

        lastMessage = messages[messages.length - 1];
    }

    @Override
    public String getLastMessage() {
        return lastMessage;
    }
}
