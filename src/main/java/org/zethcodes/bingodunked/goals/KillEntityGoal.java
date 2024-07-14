package org.zethcodes.bingodunked.goals;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.handlers.KillEntityListener;

public class KillEntityGoal extends Goal {

    private EntityType requiredEntityType;
    private KillEntityListener killEntityListener;

    public KillEntityGoal(String name, ItemStack item, EntityType requiredEntityType, KillEntityListener killEntityListener) {
        super(name, item);
        this.requiredEntityType = requiredEntityType;
        this.killEntityListener = killEntityListener;
    }

    @Override
    public boolean isComplete(Player player) {
        return killEntityListener.hasPlayerKilledEntity(player, requiredEntityType);
    }
}
