package org.zethcodes.bingodunked.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.entity.Player;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.HashMap;
import java.util.UUID;

public class KillEntityListener implements Listener {
    private HashMap<UUID, EntityType> playerKills;
    private HashMap<UUID, EntityDamageEvent.DamageCause> playerCauses;

    public KillEntityListener() {
        this.playerKills = new HashMap<>();
        this.playerCauses = new HashMap<>();
    }

    public void Reset()
    {
        this.playerKills = new HashMap<>();
        this.playerCauses = new HashMap<>();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        EntityType entityType = event.getEntity().getType();

        if (entityType == EntityType.PLAYER) return;

        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            playerKills.put(killer.getUniqueId(), entityType);
            playerCauses.put(killer.getUniqueId(), event.getEntity().getLastDamageCause().getCause());
        } else if (event.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK) ||
                event.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FALL))
        {
            Player player = getNearestPlayer(event.getEntity().getLocation());
            if (player == null) return;
            playerKills.put(player.getUniqueId(), entityType);
            playerCauses.put(player.getUniqueId(), event.getEntity().getLastDamageCause().getCause());
        }
    }

    public boolean hasPlayerKilledEntity(Player player, EntityType entityType) {
        if (GameManager.DEBUG)Bukkit.getLogger().info(player + "'s most recent kill is " + playerKills.get(player.getUniqueId()));
        return entityType.equals(playerKills.get(player.getUniqueId()));
    }

    public boolean hasPlayerKilledEntity(Player player, EntityType entityType, EntityDamageEvent.DamageCause damageCause) {
        if (GameManager.DEBUG) Bukkit.getLogger().info(player + "'s most recent kill is " + playerKills.get(player.getUniqueId()) + " with cause " + playerCauses.get(player.getUniqueId()));
        return entityType.equals(playerKills.get(player.getUniqueId())) && damageCause.equals(playerCauses.get(player.getUniqueId()));
    }

    public Player getNearestPlayer(Location location) {
        Player nearestPlayer = null;
        double nearestDistanceSquared = Double.MAX_VALUE;
        World locationWorld = location.getWorld();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(locationWorld)) {
                double distanceSquared = player.getLocation().distanceSquared(location);
                if (distanceSquared < nearestDistanceSquared) {
                    nearestDistanceSquared = distanceSquared;
                    nearestPlayer = player;
                }
            }
        }
        return nearestPlayer.getLocation().distanceSquared(location) < 25 ? nearestPlayer : null;
    }
}

