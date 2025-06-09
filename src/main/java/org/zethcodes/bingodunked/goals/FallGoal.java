package org.zethcodes.bingodunked.goals;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.listeners.FallHeightListener;

public class FallGoal extends Goal {
    private final FallHeightListener fallHeightListener;
    private final int count;

    public FallGoal(String name, ItemStack item, int count, FallHeightListener fallHeightListener) {
        super(name, item);
        this.fallHeightListener = fallHeightListener;
        this.count = count;
    }

    @Override
    public boolean isComplete(Player player) {
        return fallHeightListener.hasPlayerFallenFarEnough(player, count);
    }
}
