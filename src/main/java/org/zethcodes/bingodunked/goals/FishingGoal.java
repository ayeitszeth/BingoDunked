package org.zethcodes.bingodunked.goals;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.handlers.FishingListener;

public class FishingGoal extends Goal {
    private final Material requiredCaught;
    private final FishingListener fishingListener;

    public FishingGoal(String name, ItemStack item, Material requiredCaught, FishingListener fishingListener) {
        super(name, item);
        this.requiredCaught = requiredCaught;
        this.fishingListener = fishingListener;
    }

    @Override
    public boolean isComplete(Player player) {
        return fishingListener.hasPlayerGotFishItem(player, requiredCaught);
    }
}
