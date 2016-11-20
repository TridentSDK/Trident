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
package net.tridentsdk.server.ui.bossbar;

import com.google.common.collect.Sets;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.ui.bossbar.BossBar;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public abstract class AbstractBossBar implements BossBar {
    /**
     * Holds the state changes of the boss bar.
     */
    public static final AtomicIntegerFieldUpdater<AbstractBossBar> STATE =
            AtomicIntegerFieldUpdater.newUpdater(AbstractBossBar.class, "b");
    /**
     * Set of used UUIDs to prevent conflicts
     */
    private static final Set<UUID> uuids = Sets.newConcurrentHashSet();

    /**
     * The UUID of the boss bar
     */
    private final UUID uuid;

    /**
     * Boss bar state field which holds what options have
     * been changed
     */
    // int holds title, health, style, and flags booleans
    //             3  |   2   |   1  |   0
    @Policy("Use STATE field to make changes")
    protected volatile int b;

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

    /**
     * Unsets the changed flags of the boss bar
     */
    public void unsetChanged() {
        STATE.set(this, 0);
    }

    public abstract AbstractBossBar clone();
}