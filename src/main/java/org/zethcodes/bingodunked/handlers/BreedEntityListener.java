package org.zethcodes.bingodunked.handlers;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.HashMap;
import java.util.UUID;

public class BreedEntityListener implements Listener {
    private HashMap<UUID, EntityType> playerBreeds;

    public BreedEntityListener() {
        this.playerBreeds = new HashMap<>();
    }

    public void Reset()
    {
        playerBreeds = new HashMap<>();
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        EntityType entityType = event.getEntity().getType();
        if (event.getBreeder() instanceof Player) {
            Player breeder = (Player) event.getBreeder();
            playerBreeds.put(breeder.getUniqueId(), entityType);
        }
    }

    public boolean hasPlayerBredEntity(Player player, EntityType entityType) {
        if (BingoUtil.DEBUG) Bukkit.getLogger().info("The most recent entity " + player + " has bred is a " + playerBreeds.get(player.getUniqueId()));
        return entityType.equals(playerBreeds.get(player.getUniqueId()));
    }
}
