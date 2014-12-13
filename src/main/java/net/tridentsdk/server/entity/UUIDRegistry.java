package net.tridentsdk.server.entity;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * Like Random class, but for UUID's, to make sure they do not appear twice in the server. Then it wouldn't be so
 * unique.
 *
 * @author The TridentSDK Team
 */
public final class UUIDRegistry {
    private static final UUIDRegistry DEFAULT = new UUIDRegistry();
    private final Set<UUID> registry = Collections.newSetFromMap(new ConcurrentHashMapV8<UUID, Boolean>());

    private UUIDRegistry() {
    }

    public static UUIDRegistry getDefaultPool() {
        return DEFAULT;
    }

    public UUID obtain() {
        while (true) {
            UUID random = UUID.randomUUID();
            if (registry.add(random))
                return random;
        }
    }

    public void register(UUID uuid) {
        registry.add(uuid);
    }
}
