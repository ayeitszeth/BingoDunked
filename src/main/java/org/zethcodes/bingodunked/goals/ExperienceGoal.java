package org.zethcodes.bingodunked.goals;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.listeners.ExperienceListener;

public class ExperienceGoal extends Goal {
    private final ExperienceListener experienceListener;
    private final int level;

    public ExperienceGoal(String name, ItemStack item, int level, ExperienceListener experienceListener) {
        super(name, item);
        this.experienceListener = experienceListener;
        this.level = level;
    }

    @Override
    public boolean isComplete(Player player) {
        return experienceListener.hasPlayerReachedLevel(player, level);
    }
}
