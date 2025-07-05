package org.zethcodes.bingodunked.goals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.List;

public class CollectItemsGoal extends CollectItemGoal {
    public List<Material> items;

    public CollectItemsGoal(String name, List<Material> items) {
        super(name, new ItemStack(items.get(0), 1));
        this.items = items;
    }

    @Override
    public boolean isComplete(Player player) {
        ItemStack[] pInv = player.getInventory().getContents();

        for (ItemStack item : pInv)
        {
            if (!(item == null))
            {
                if (items.contains(item.getType()))
                {
                    if (GameManager.DEBUG) Bukkit.getLogger().info(player + " has the item: " + item.getType());
                    return true;
                }
            }
        }
        //if (BingoUtil.DEBUG) Bukkit.getLogger().info(player + " does not have the item: " + items);
        return false;
    }
}

