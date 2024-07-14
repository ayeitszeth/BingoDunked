package org.zethcodes.bingodunked.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorStandInteractListener implements Listener {
    private Map<UUID, Entity> playerEntityMap = new HashMap<>();

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerArmorStandManipulateEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        playerEntityMap.put(player.getUniqueId(), entity);
    }

    public void Reset() {
        playerEntityMap = new HashMap<>();
    }

    public boolean isEntityInDesiredState(Player player, EntityType entityType) {
        Entity entity = playerEntityMap.get(player.getUniqueId());
        if (entity == null || entity.getType() != entityType) {
            return false;
        }

        switch (entityType) {
            case ARMOR_STAND:
                if (entity instanceof ArmorStand) {
                    ArmorStand armorStand = (ArmorStand) entity;
                    ItemStack helmet = armorStand.getEquipment().getHelmet();
                    ItemStack chestplate = armorStand.getEquipment().getChestplate();
                    ItemStack leggings = armorStand.getEquipment().getLeggings();
                    ItemStack boots = armorStand.getEquipment().getBoots();
                    if (BingoUtil.DEBUG) Bukkit.getLogger().info(player + " has interacted with an armour stand with pieces: " + helmet + " " + chestplate + " " + leggings + " " + boots);
                    return helmet.getType() != Material.AIR && chestplate.getType() != Material.AIR && leggings.getType() != Material.AIR && boots.getType() != Material.AIR;
                }
                break;
            default:
                return false;
        }
        return false;
    }
}
