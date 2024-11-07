package org.zethcodes.bingodunked.goals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.List;

public class CollectItemSetGoal extends CollectItemGoal{
    public List<Material> items;

    public CollectItemSetGoal(String name, ItemStack itemToCollect, List<Material> items) {
        super(name, itemToCollect);
        this.items = items;
    }

    @Override
    public boolean isComplete(Player player) {
        ItemStack[] pInv = player.getInventory().getContents();


        for (Material itemMat : items)
        {
            if (!doesInvContain(pInv, itemMat))
            {
                if (BingoUtil.DEBUG) Bukkit.getLogger().info(player + " does not have the item: " + itemMat);
                return false;
            }
        }
        return true;
    }

    public boolean doesInvContain(ItemStack[] pInv, Material targetItem)
    {
        for (ItemStack item : pInv)
        {
            if (!(item == null))
            {
                if (targetItem.equals(item.getType()))
                {
                    return true;
                }
            }
        }
        //if (BingoUtil.DEBUG) Bukkit.getLogger().info(player + " does not have the item: " + items);
        return false;
    }
}
