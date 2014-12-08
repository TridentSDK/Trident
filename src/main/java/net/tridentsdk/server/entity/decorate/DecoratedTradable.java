package net.tridentsdk.server.entity.decorate;

import net.tridentsdk.entity.VillagerCareer;
import net.tridentsdk.entity.decorate.Tradeable;
import net.tridentsdk.window.trade.Trade;

import java.util.ArrayList;
import java.util.Collection;

public class DecoratedTradable implements Tradeable {
    private final Collection<Trade> trades = new ArrayList<>();

    @Override
    public Collection<Trade> getTrades() {
        return null;
    }

    public void applyUpdateTrades(VillagerCareer career) {
        // TODO logic
    }
}
