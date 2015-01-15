package net.tridentsdk.server.entity.living.ai;

import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.living.ai.AiModule;

/**
 * The AI Module that provides the default implentation of AI for creepers on the server
 */
public class CreeperAiModule implements AiModule {
    // TODO make this think
    @Override
    public int think(LivingEntity entity) {
        return 0;
    }
}
