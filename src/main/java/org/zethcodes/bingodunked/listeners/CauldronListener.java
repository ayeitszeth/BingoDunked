package org.zethcodes.bingodunked.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.util.Enchant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CauldronListener implements Listener {
    private HashMap<UUID, Integer> playerCauldronLevel;

    public CauldronListener() {
        this.playerCauldronLevel = new HashMap<>();
    }

    @EventHandler
    public void FillCauldronEvent(CauldronLevelChangeEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;

        if (event.getEntity() instanceof Player)
        {

            Player player = (Player) event.getEntity();
            Block block = event.getBlock();
            Material type = block.getType();

            if (GameManager.DEBUG) Bukkit.getLogger().info("Interacted with type: " + type);

            if (type == Material.CAULDRON)
            {

                if (event.getReason() == CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY)
                {
                    if (GameManager.DEBUG) Bukkit.getLogger().info(player.getName() + " filled an empty Cauldron with a bucket");
                    playerCauldronLevel.put(player.getUniqueId(), 3);
                } else if (event.getReason() == CauldronLevelChangeEvent.ChangeReason.BOTTLE_EMPTY)
                {
                    if (GameManager.DEBUG) Bukkit.getLogger().info(player.getName() + " filled an empty Cauldron with a water bottle");
                    playerCauldronLevel.put(player.getUniqueId(), 1);
                }
            }
            else if (type == Material.WATER_CAULDRON || type == Material.LAVA_CAULDRON || type == Material.POWDER_SNOW_CAULDRON)
            {
                BlockData data = block.getBlockData();

                if (data instanceof Levelled) {
                    Levelled levelled = (Levelled) data;
                    if (GameManager.DEBUG) Bukkit.getLogger().info("[PRE-CHANGE LEVEL] Levelled data: " + levelled);

                    if (event.getReason() == CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY)
                    {
                        if (GameManager.DEBUG) Bukkit.getLogger().info(player.getName() + " filled a Cauldron with a water bottle");
                        playerCauldronLevel.put(player.getUniqueId(), 3);
                    } else if (event.getReason() == CauldronLevelChangeEvent.ChangeReason.BUCKET_FILL)
                    {
                        if (GameManager.DEBUG) Bukkit.getLogger().info(player.getName() + " filled an emptied a Cauldron");
                        playerCauldronLevel.put(player.getUniqueId(), 0);
                    }
                    else if (event.getReason() == CauldronLevelChangeEvent.ChangeReason.BOTTLE_EMPTY)
                    {
                        if (GameManager.DEBUG) Bukkit.getLogger().info(player.getName() + " filled a Cauldron with a water bottle");
                        playerCauldronLevel.put(player.getUniqueId(), levelled.getLevel() + 1);
                    } else if (event.getReason() == CauldronLevelChangeEvent.ChangeReason.BOTTLE_FILL ||
                            event.getReason() == CauldronLevelChangeEvent.ChangeReason.ARMOR_WASH ||
                            event.getReason() == CauldronLevelChangeEvent.ChangeReason.BANNER_WASH ||
                            event.getReason() == CauldronLevelChangeEvent.ChangeReason.SHULKER_WASH)
                    {
                        if (GameManager.DEBUG) Bukkit.getLogger().info(player.getName() + " emptied a Cauldron 'somehow'");
                        playerCauldronLevel.put(player.getUniqueId(), levelled.getLevel() - 1);
                    }
                }
            }
        }
    }


    public void Reset() {
        playerCauldronLevel = new HashMap<>();
    }

    public boolean isCauldronFilled(Player player, int level) {
        if (GameManager.DEBUG) Bukkit.getLogger().info("Cauldron is at level " + playerCauldronLevel.get(player.getUniqueId()));
        return level <= playerCauldronLevel.getOrDefault(player.getUniqueId(), 0);
    }
}
