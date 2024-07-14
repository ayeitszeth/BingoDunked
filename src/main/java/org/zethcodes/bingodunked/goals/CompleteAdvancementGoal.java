package org.zethcodes.bingodunked.goals;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CompleteAdvancementGoal extends Goal {
    private final Advancement advancement;

    public CompleteAdvancementGoal(String name, ItemStack item, Advancement advancement) {
        super(name, item);
        this.advancement = advancement;
    }

    @Override
    public boolean isComplete(Player player) {
        return player.getAdvancementProgress(advancement).isDone();
    }

}

