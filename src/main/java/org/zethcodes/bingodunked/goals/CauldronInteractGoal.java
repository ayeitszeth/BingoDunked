package org.zethcodes.bingodunked.goals;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.listeners.CauldronListener;
import org.zethcodes.bingodunked.listeners.ExperienceListener;

public class CauldronInteractGoal extends Goal {
    private final CauldronListener cauldronListener;
    private final int level;

    public CauldronInteractGoal(String name, ItemStack item, int level, CauldronListener cauldronListener) {
        super(name, item);
        this.cauldronListener = cauldronListener;
        this.level = level;
    }

    @Override
    public boolean isComplete(Player player) {
        return cauldronListener.isCauldronFilled(player, level);
    }
}
