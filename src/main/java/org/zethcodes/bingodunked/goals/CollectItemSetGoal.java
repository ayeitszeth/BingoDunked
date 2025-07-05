package org.zethcodes.bingodunked.goals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.List;

public class CollectItemSetGoal extends CollectItemGoal{
    public List<Material> items;

    public CollectItemSetGoal(String name, List<Material> items) {
        super(name, new ItemStack(items.get(0), 1));
        this.items = items;
    }

    @Override
    public boolean isComplete(Player player) {
        ItemStack[] pInv = player.getInventory().getContents();


        for (Material itemMat : items)
        {
            if (!doesInvContain(pInv, itemMat))
            {
                if (GameManager.DEBUG) Bukkit.getLogger().info(player + " does not have the item: " + itemMat);
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

    @Override
    public boolean isCompleteItem(ItemStack item, Player player) {
        return isComplete(player);
    }
}
