package org.zethcodes.bingodunked.goals;

import org.bukkit.inventory.ItemStack;

public class CollectColouredItemGoal extends CollectItemGoal {
    private ItemStack itemToCollect;

    public CollectColouredItemGoal(String name, ItemStack itemToCollect) {
        super(name, itemToCollect);
        this.itemToCollect = itemToCollect;
    }
}

