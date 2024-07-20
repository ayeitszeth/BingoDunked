package org.zethcodes.bingodunked.goals;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.handlers.DeathListener;

public class DeathGoal extends Goal {
    private String message;
    private DeathListener deathListener;

    public DeathGoal(String name, ItemStack item, String message, DeathListener deathListener)
    {
        super(name,item);
        this.message = message;
        this.deathListener = deathListener;
    }

    @Override
    public boolean isComplete(Player player) { return deathListener.hasPlayerGotDeathMessage(player,message); }
}
