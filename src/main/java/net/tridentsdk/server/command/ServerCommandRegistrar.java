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

import net.tridentsdk.Defaults;
import net.tridentsdk.Trident;
import net.tridentsdk.concurrent.TaskExecutor;

public class ServerCommandRegistrar {
    private static volatile boolean registered;

    public static void registerAll() {
        if (registered)
            return;

        // Set it here in case some idiot tries to register twice
        registered = true;

        Trident.commandHandler().addCommand(null, Defaults.DIRECT_EXECUTOR, new ShutdownCommand());
    }
}
