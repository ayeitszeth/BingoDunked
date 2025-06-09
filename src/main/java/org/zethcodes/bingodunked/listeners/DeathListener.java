package org.zethcodes.bingodunked.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.HashMap;
import java.util.UUID;

public class DeathListener implements Listener {
    private HashMap<UUID, String> playerRecentDeathMessage;

    public DeathListener() { playerRecentDeathMessage = new HashMap<>(); }

    public void Reset() { playerRecentDeathMessage = new HashMap<>(); }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Player player = event.getEntity();
        playerRecentDeathMessage.put(player.getUniqueId(),event.getDeathMessage());
    }

    public boolean hasPlayerGotDeathMessage(Player player, String message)
    {
        if (BingoUtil.DEBUG) Bukkit.getLogger().info(player + "'s most recent death message is: " + playerRecentDeathMessage.get(player.getUniqueId()));
        return playerRecentDeathMessage.getOrDefault(player.getUniqueId(),"") .equals(player.getName() + " " + message);
    }
}
