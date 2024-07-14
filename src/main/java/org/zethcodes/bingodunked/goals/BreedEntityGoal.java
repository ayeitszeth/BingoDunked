package org.zethcodes.bingodunked.goals;

import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.handlers.BreedEntityListener;

public class BreedEntityGoal extends Goal {
    private EntityType requiredEntityType;
    private BreedEntityListener breedEntityListener;

    public BreedEntityGoal(String name, ItemStack item, EntityType requiredEntityType, BreedEntityListener breedEntityListener) {
        super(name, item);
        this.requiredEntityType = requiredEntityType;
        this.breedEntityListener = breedEntityListener;
    }

    @Override
    public boolean isComplete(Player player) {
        return breedEntityListener.hasPlayerBredEntity(player, requiredEntityType);
    }
}
