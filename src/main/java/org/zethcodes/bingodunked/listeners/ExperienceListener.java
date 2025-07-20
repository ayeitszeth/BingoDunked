package org.zethcodes.bingodunked.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.HashMap;
import java.util.UUID;

public class ExperienceListener implements Listener {
    private HashMap<UUID, Integer> playerExperience ;

    public ExperienceListener() {
        this.playerExperience = new HashMap<>();
    }

    public void Reset()
    {
        playerExperience = new HashMap<>();
    }

    @EventHandler
    public void onLevelChanged(PlayerLevelChangeEvent event) {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Player player = event.getPlayer();
        int level = player.getLevel();
        playerExperience.put(player.getUniqueId(), level);
    }

    public boolean hasPlayerReachedLevel(Player player, int level) {
        if (GameManager.DEBUG) Bukkit.getLogger().info(player + " is level " + playerExperience.get(player.getUniqueId()));
        return level <= playerExperience.getOrDefault(player.getUniqueId(), 0);
    }
}
