package org.zethcodes.bingodunked.goals;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.handlers.EatListener;

public class EatGoal extends Goal {
    private Material requiredFoodType;
    private EatListener eatListener;

    public EatGoal(String name, ItemStack item, EatListener eatListener) {
        super(name, item);
        this.requiredFoodType = item.getType();
        this.eatListener = eatListener;
    }

    @Override
    public boolean isComplete(Player player) {
        return eatListener.hasPlayerAteItem(player, requiredFoodType);
    }
}
