package org.zethcodes.bingodunked.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.HashMap;
import java.util.UUID;

public class EatListener implements Listener {
    private HashMap<UUID, Material> playerEats;

    public EatListener() {
        this.playerEats = new HashMap<>();
    }

    public void Reset()
    {
        playerEats = new HashMap<>();
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Player player = event.getPlayer();
        playerEats.put(player.getUniqueId(), event.getItem().getType());
    }

    public boolean hasPlayerAteItem(Player player, Material foodType) {
        if (BingoUtil.DEBUG) Bukkit.getLogger().info("The most recent food " + player + " ate was a " + playerEats.get(player.getUniqueId()));
        return foodType.equals(playerEats.get(player.getUniqueId()));
    }
}
