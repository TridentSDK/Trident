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

import net.tridentsdk.command.*;
import net.tridentsdk.command.annotation.AllowedSourceTypes;
import net.tridentsdk.command.annotation.PermissionRequired;
import net.tridentsdk.server.TridentServer;

public class StopCommand implements CommandListener {
    @Command(name = "stop", aliases = { "shutdown", "fuck" }, help = "/stop", desc = "Stops the server and shuts-down")
    @PermissionRequired("minecraft.stop")
    @AllowedSourceTypes({ CommandSourceType.CONSOLE, CommandSourceType.PLAYER })
    public void stop(CommandSource source, String[] args) {
        TridentServer.getInstance().shutdown();
    }
}
