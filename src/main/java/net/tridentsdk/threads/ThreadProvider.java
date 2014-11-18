/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.threads;

import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.threads.*;
import net.tridentsdk.api.world.World;
import net.tridentsdk.plugin.TridentPlugin;

public interface ThreadProvider {
    net.tridentsdk.api.threads.TaskExecutor provideEntityThread(Entity entity);

    net.tridentsdk.api.threads.TaskExecutor providePlayerThread(Player player);

    net.tridentsdk.api.threads.TaskExecutor providePluginThread(TridentPlugin plugin);

    net.tridentsdk.api.threads.TaskExecutor provideWorldThread(World world);
}
