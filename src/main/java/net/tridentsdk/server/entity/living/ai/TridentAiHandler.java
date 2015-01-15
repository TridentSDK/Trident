package net.tridentsdk.server.entity.living.ai;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.entity.EntityType;
import net.tridentsdk.entity.living.ai.AiHandler;
import net.tridentsdk.entity.living.ai.AiModule;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of interface
 *
 * @author The TridentSDK Team
 */
public class TridentAiHandler implements AiHandler {
    private final Map<EntityType, AiModule> modules = new ConcurrentHashMapV8<>();
    private final Map<EntityType, AiModule> nativeModules = new HashMap<>();

    public TridentAiHandler () {
        // TODO add default AIs
        nativeModules.put(EntityType.CREEPER, new CreeperAiModule());
    }

    @Override
    public AiModule defaultAIFor(EntityType type) {
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
    public AiModule nativeAIFor(EntityType type) {
        return nativeModules.get(type);
    }
}
