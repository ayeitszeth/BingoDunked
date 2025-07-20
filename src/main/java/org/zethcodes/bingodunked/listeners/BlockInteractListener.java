package org.zethcodes.bingodunked.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Cauldron;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockInteractListener implements Listener {
    private Map<UUID, Block> playerBlockMap = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) return;

        playerBlockMap.put(player.getUniqueId(), block);
    }

    public void Reset()
    {
        playerBlockMap = new HashMap<>();
    }

    public boolean isBlockInDesiredState(Player player, Material blockType) {
        Block block = playerBlockMap.get(player.getUniqueId());
        if (block == null || block.getType() != blockType) {
            return false;
        }

        switch (blockType) {
            case COMPOSTER:
                if (GameManager.DEBUG) Bukkit.getLogger().info(player + " interacted with a composter and it has a level of " + ((Levelled) block.getBlockData()).getLevel());
                return ((Levelled) block.getBlockData()).getLevel() == 7;
            case CHISELED_BOOKSHELF:
                if (GameManager.DEBUG) Bukkit.getLogger().info(player + " interacted with a chiseled bookshelf and it has " + ((ChiseledBookshelf) block.getBlockData()).getOccupiedSlots().size() + " books on the shelf");
                return ((ChiseledBookshelf) block.getBlockData()).getOccupiedSlots().size() == 6;
            case CAMPFIRE:
                if (GameManager.DEBUG) Bukkit.getLogger().info(player + " interacted with a campfire and has started a campfire: " + ((Campfire) block.getBlockData()).isSignalFire());
                return ((Campfire) block.getBlockData()).isSignalFire();
            case LECTERN:
                if (GameManager.DEBUG) Bukkit.getLogger().info(player + " interacted with a lecturn and it has a book: " + ((Lectern) block.getBlockData()).hasBook());
                return ((Lectern) block.getBlockData()).hasBook();
            case DISPENSER:
                return ((Dispenser) block.getBlockData()).isTriggered();
            default:
                return false;
        }
    }
}
