package org.zethcodes.bingodunked.goals;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.listeners.KillEntityListener;

public class KillEntityWithCauseGoal extends KillEntityGoal {

    private final EntityType requiredEntityType;
    private EntityDamageEvent.DamageCause cause;
    private final KillEntityListener killEntityListener;

    public KillEntityWithCauseGoal(String name, ItemStack item, EntityType requiredEntityType, EntityDamageEvent.DamageCause cause, KillEntityListener killEntityListener) {
        super(name, item, requiredEntityType, killEntityListener);
        this.requiredEntityType = requiredEntityType;
        this.cause = cause;
        this.killEntityListener = killEntityListener;
    }

    @Override
    public boolean isComplete(Player player) {
        return killEntityListener.hasPlayerKilledEntity(player, requiredEntityType, cause);
    }
}
