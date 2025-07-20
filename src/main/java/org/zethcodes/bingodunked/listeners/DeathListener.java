package org.zethcodes.bingodunked.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.managers.SettingsManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class DeathListener implements Listener {
    private HashMap<UUID, String> playerRecentDeathMessage;
    private HashMap<UUID, ItemStack[]> playerLastHotbars;

    public DeathListener() {
        playerRecentDeathMessage = new HashMap<>();
        playerLastHotbars = new HashMap<>();
    }

    public void Reset() {
        playerRecentDeathMessage = new HashMap<>();
        playerLastHotbars = new HashMap<>();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;

        Player player = event.getEntity();
        playerRecentDeathMessage.put(player.getUniqueId(), event.getDeathMessage());

        if (SettingsManager.difficulty == SettingsManager.Difficulty.NORMAL) {
            ItemStack[] hotbar = new ItemStack[9];
            ItemStack[] playerInv = player.getInventory().getContents();

            System.arraycopy(playerInv, 0, hotbar, 0, 9);
            playerLastHotbars.put(player.getUniqueId(), hotbar);

            List<ItemStack> drops = event.getDrops();
            for (int i = 0; i < 9; ++i) {
                ItemStack item = playerInv[i];
                if (item != null && !item.getType().isAir()) {
                    Iterator<ItemStack> it = drops.iterator();
                    while (it.hasNext()) {
                        ItemStack drop = it.next();
                        if (drop.isSimilar(item)) {
                            it.remove();
                            break; // remove only one matching item
                        }
                    }
                }
            }


            // Clear hotbar from player inventory
            for (int i = 0; i < 9; ++i) {
                player.getInventory().setItem(i, null);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (SettingsManager.difficulty == SettingsManager.Difficulty.NORMAL) {
            Inventory inv = event.getPlayer().getInventory();
            ItemStack[] lastHotbar = playerLastHotbars.get(event.getPlayer().getUniqueId());

            for (int i = 0; i < 9; ++i) {
                inv.setItem(i, lastHotbar[i]);
            }
        }
    }

    public boolean hasPlayerGotDeathMessage(Player player, String message)
    {
        if (GameManager.DEBUG) Bukkit.getLogger().info(player + "'s most recent death message is: " + playerRecentDeathMessage.get(player.getUniqueId()));
        return playerRecentDeathMessage.getOrDefault(player.getUniqueId(),"") .equals(player.getName() + " " + message);
    }
}
