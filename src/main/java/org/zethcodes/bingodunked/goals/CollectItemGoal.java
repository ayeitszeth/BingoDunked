package org.zethcodes.bingodunked.goals;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.util.BingoUtil;

public class CollectItemGoal extends Goal {
    private ItemStack itemToCollect;

    public CollectItemGoal(String name, ItemStack itemToCollect) {
        super(name, itemToCollect);
        this.itemToCollect = itemToCollect;
    }

    public ItemStack getItemToCollect() {
        return itemToCollect;
    }

    @Override
    public boolean isComplete(Player player) {
        ItemStack[] pInv = player.getInventory().getContents();

        for (ItemStack item : pInv)
        {
            if (!(item == null))
            {
                if (item.getType().equals(itemToCollect.getType()))
                {
                    if (GameManager.DEBUG) Bukkit.getLogger().info(player + " has the item: " + itemToCollect.getType());
                    return true;
                }
            }
        }
        //if (BingoUtil.DEBUG) Bukkit.getLogger().info(player + " does not have the item: " + itemToCollect.getType());
        return false;
    }

    public boolean isCompleteItem(ItemStack item, Player player) {
        return item.getType().equals(itemToCollect.getType());
    }
}

