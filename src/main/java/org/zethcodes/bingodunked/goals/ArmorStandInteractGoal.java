package org.zethcodes.bingodunked.goals;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.handlers.ArmorStandInteractListener;

public class ArmorStandInteractGoal extends Goal {
    private final EntityType entityType;
    private final ArmorStandInteractListener armorStandInteractListener;

    public ArmorStandInteractGoal(String name, ItemStack item, EntityType entityType, ArmorStandInteractListener armorStandInteractListener) {
        super(name, item);
        this.entityType = entityType;
        this.armorStandInteractListener = armorStandInteractListener;
    }

    @Override
    public boolean isComplete(Player player) {
        return armorStandInteractListener.isEntityInDesiredState(player, entityType);
    }
}
