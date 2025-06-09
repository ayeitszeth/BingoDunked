package org.zethcodes.bingodunked.goals;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.zethcodes.bingodunked.listeners.PotionEffectListener;

public class PotionEffectGoal extends Goal {
    private final PotionEffectType requiredPotionEffect;
    private final PotionEffectListener potionEffectListener;

    public PotionEffectGoal(String name, ItemStack item, PotionEffectType requiredPotionEffect, PotionEffectListener potionEffectListener) {
        super(name, item);
        this.requiredPotionEffect = requiredPotionEffect;
        this.potionEffectListener = potionEffectListener;
    }

    @Override
    public boolean isComplete(Player player) {
        return potionEffectListener.hasPlayerReceivedPotionEffect(player, requiredPotionEffect);
    }
}
