package org.zethcodes.bingodunked.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.UUID;

public class BreakBlockTypeListener implements Listener {
    private EnumMap<BlockType, HashMap<UUID, Integer>> playerBlockCounts;
    private EnumSet<Material> stones = EnumSet.of(Material.STONE, Material.DEEPSLATE, Material.GRANITE, Material.DIORITE, Material.ANDESITE);
    private EnumSet<Material> logs = EnumSet.of(Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG, Material.CHERRY_LOG, Material.PALE_OAK_LOG);
    private EnumSet<Material> ores = EnumSet.of(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE, Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE);
    private EnumSet<Material> netherOres = EnumSet.of(Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE, Material.ANCIENT_DEBRIS);
    private BingoUtil bingoUtil;

    public enum BlockType {
        STONE, LOG, ORE, NETHERORE
    }

    public BreakBlockTypeListener(BingoUtil bingoUtil) {
        this.bingoUtil = bingoUtil;
        this.playerBlockCounts = new EnumMap<>(BlockType.class);
        for (BlockType type : BlockType.values()) {
            playerBlockCounts.put(type, new HashMap<>());
        }
    }

    public void Reset() {
        playerBlockCounts = new EnumMap<>(BlockType.class);
        for (BlockType type : BlockType.values()) {
            playerBlockCounts.put(type, new HashMap<>());
        }
    }

    private void incrementBlockCount(Player player, BlockType type) {
        UUID playerId = player.getUniqueId();
        HashMap<UUID, Integer> counts = playerBlockCounts.get(type);
        int curCount = counts.getOrDefault(playerId, 0);
        curCount++;
        if (curCount % 25 == 0 && bingoUtil.activeBlockTypes.contains(type)) {
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED WHISPER] " + ChatColor.GRAY + "You have broken " + curCount + " " + type.name().toLowerCase() + " blocks.");
        }
        counts.put(playerId, curCount);
        if (BingoUtil.DEBUG && curCount % 5 == 0) Bukkit.getLogger().info(player + " has broken " + counts.getOrDefault(playerId, 0) + " of the block type: " + type);
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();

        if (stones.contains(block)) {
            incrementBlockCount(player, BlockType.STONE);
        } else if (logs.contains(block)) {
            incrementBlockCount(player, BlockType.LOG);
        } else if (ores.contains(block)) {
            incrementBlockCount(player, BlockType.ORE);
        } else if (netherOres.contains(block)) {
            incrementBlockCount(player, BlockType.NETHERORE);
        }
    }

    public boolean hasPlayerBrokeEnoughBlocks(Player player, BlockType type, int requiredToBreak) {
        UUID playerId = player.getUniqueId();
        HashMap<UUID, Integer> counts = playerBlockCounts.get(type);
        return counts.getOrDefault(playerId, 0) >= requiredToBreak;
    }
}