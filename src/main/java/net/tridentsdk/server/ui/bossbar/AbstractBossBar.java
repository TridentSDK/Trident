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
package net.tridentsdk.server.ui.bossbar;

import net.tridentsdk.ui.bossbar.BossBar;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public abstract class AbstractBossBar implements BossBar {
    /**
     * Set of used UUIDs to prevent conflicts
     */
    private static final Set<UUID> uuids = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * The UUID of the boss bar
     */
    private final UUID uuid;

    /**
     * Creates a new boss bar and assigns a UUID
     */
    public AbstractBossBar() {
        while (true) {
            UUID uuid = UUID.randomUUID();
            if (uuids.add(uuid)) {
                this.uuid = uuid;
                break;
            }
        }
    }

    @Override
    public final UUID getUuid() {
        return this.uuid;
    }

    @Override
    public abstract AbstractBossBar clone();

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof AbstractBossBar &&
                this.uuid.equals(((AbstractBossBar) obj).getUuid());
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }
}