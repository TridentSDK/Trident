package net.tridentsdk.server.entity.living.ai;

import net.tridentsdk.entity.EntityType;
import net.tridentsdk.entity.living.ai.AiHandler;
import net.tridentsdk.entity.living.ai.AiModule;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of interface
 */
public class TridentAiHandler implements AiHandler {

    private final Map<EntityType, AiModule> modules = new ConcurrentHashMap<>();
    private final Map<EntityType, AiModule> nativeModules = new HashMap<>();

    public TridentAiHandler () {
        // TODO add default AIs
        nativeModules.put(EntityType.CREEPER, new CreeperAiModule());
    }

    @Override
    public AiModule getDefaultAiFor(EntityType type) {
        if(modules.get(type) == null) {
            return nativeModules.get(type);
        }
        else {
            return modules.get(type);
        }
    }

    @Override
    public void setDefaultAiFor(EntityType type, AiModule module) {
        modules.put(type,module);
    }

    @Override
    public AiModule getNativeAiFor(EntityType type) {
        return nativeModules.get(type);
    }
}
