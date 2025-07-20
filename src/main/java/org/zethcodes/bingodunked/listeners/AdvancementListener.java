package org.zethcodes.bingodunked.listeners;

import org.bukkit.Bukkit;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.zethcodes.bingodunked.managers.GameManager;

import java.util.HashMap;
import java.util.UUID;

public class AdvancementListener implements Listener {
    private HashMap<UUID, Integer> playerCompletedAdvancements = new HashMap<>();

    public AdvancementListener() { playerCompletedAdvancements = new HashMap<>(); }

    public void Reset() { playerCompletedAdvancements.clear(); }

    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;

        AdvancementDisplay advancementDisplay = event.getAdvancement().getDisplay();

        if (advancementDisplay == null) return;

        if (GameManager.DEBUG) Bukkit.getLogger().info(event.getPlayer().getName() + " has completed the advancement " + advancementDisplay);

        UUID uuid = event.getPlayer().getUniqueId();
        playerCompletedAdvancements.put(uuid, playerCompletedAdvancements.getOrDefault(uuid, 0) + 1);
    }

    public boolean hasPlayerGotEnoughAdvancements(Player player, int target) {
        UUID playerId = player.getUniqueId();
        int count = playerCompletedAdvancements.getOrDefault(playerId, 0);
        if (GameManager.DEBUG) Bukkit.getLogger().info(player.getName() + " has completed " + count + " advancements.");
        return count >= target;
    }

}
