package org.zethcodes.bingodunked.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.HashMap;
import java.util.UUID;

public class PotionEffectListener implements Listener {
    private HashMap<UUID, PotionEffectType> playerPotionEffects;

    public PotionEffectListener() {
        this.playerPotionEffects = new HashMap<>();
    }

    public void Reset()
    {
        playerPotionEffects = new HashMap<>();
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PotionEffectType effectType = event.getModifiedType();
            playerPotionEffects.put(player.getUniqueId(), effectType);
        }
    }

    public boolean hasPlayerReceivedPotionEffect(Player player, PotionEffectType effectType) {
        if (GameManager.DEBUG) Bukkit.getLogger().info(player + "'s most recent potion effect is " + playerPotionEffects.get(player.getUniqueId()));
        return effectType.equals(playerPotionEffects.get(player.getUniqueId()));
    }
}
