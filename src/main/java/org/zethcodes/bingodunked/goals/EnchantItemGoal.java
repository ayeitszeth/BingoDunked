package org.zethcodes.bingodunked.goals;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.handlers.EnchantListener;
import org.zethcodes.bingodunked.util.Enchant;

public class EnchantItemGoal extends Goal {
    private final Enchantment requiredEnchant;
    private final int minLevelRequired;
    private final EnchantListener enchantListener;

    public EnchantItemGoal(String name, ItemStack item, Enchantment requiredEnchant, int minLevelRequired, EnchantListener enchantListener) {
        super(name, item);
        this.requiredEnchant = requiredEnchant;
        this.enchantListener = enchantListener;
        this.minLevelRequired = minLevelRequired;
    }

    @Override
    public boolean isComplete(Player player) {
        return enchantListener.hasPlayerGotEnchant(player, new Enchant(requiredEnchant,minLevelRequired));
    }
}
