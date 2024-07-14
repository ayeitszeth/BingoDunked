package org.zethcodes.bingodunked.goals;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Goal {
    private String name;
    private ItemStack item;

    public Goal(String name,ItemStack item) {
        this.name = name;
        this.item = item;
    }

    public String getName() {
        return ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + name;
    }

    public ItemStack getItem() {
        return item;
    }

    public abstract boolean isComplete(Player player);

    @Override
    public String toString() {
        return "Goal: " + name;
    }
}
