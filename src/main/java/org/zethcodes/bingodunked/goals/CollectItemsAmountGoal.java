package org.zethcodes.bingodunked.goals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.List;

public class CollectItemsAmountGoal extends CollectItemGoal {
    public List<Material> items;
    private int amount;

    public CollectItemsAmountGoal(String name, ItemStack itemToCollect, List<Material> items, int amount) {
        super(name, itemToCollect);
        this.items = items;
        this.amount = amount;
    }

    @Override
    public boolean isComplete(Player player) {
        ItemStack[] pInv = player.getInventory().getContents();
        int count = 0;

        for (ItemStack item : pInv)
        {
            if (!(item == null))
            {
                if (items.contains(item.getType()))
                {
                    count += item.getAmount();
                }
            }
        }

        //if (BingoUtil.DEBUG) Bukkit.getLogger().info(player + " has got " + count + " of the items " + items);
        return count >= amount;
    }

    @Override
    public boolean isCompleteItem(ItemStack item, Player player) {
        return isComplete(player);
    }
}

