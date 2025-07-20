package org.zethcodes.bingodunked.goals;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.listeners.AdvancementListener;

public class CompleteNumberAdvancementGoal extends Goal {
    private final int target;
    private final AdvancementListener advancementListener;

    public CompleteNumberAdvancementGoal(String name, ItemStack item, int target, AdvancementListener advancementListener) {
        super(name, item);
        this.target = target;
        this.advancementListener = advancementListener;
    }

    @Override
    public boolean isComplete(Player player) {
        return advancementListener.hasPlayerGotEnoughAdvancements(player, target);
    }

}

