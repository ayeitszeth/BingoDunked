package org.zethcodes.bingodunked.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.WorldUtil;

import java.util.HashMap;
import java.util.UUID;

public class TravelListener implements Listener {
    private HashMap<UUID, Double> playerNonVehicleDistances;
    private HashMap<UUID, Double> playerBoatDistances;
    private HashMap<UUID, Double> playerMinecartDistances;
    private HashMap<UUID, Integer> lastBoatNotification;
    private HashMap<UUID, Integer> lastMinecartNotification;
    private HashMap<UUID, Integer> lastRunningNotification;
    private BingoUtil bingoUtil;
    public enum TYPE { BOAT, RUNNING, MINECART };

    public TravelListener(BingoUtil bingoUtil) {
        this.playerNonVehicleDistances = new HashMap<>();
        this.playerBoatDistances = new HashMap<>();
        this.playerMinecartDistances = new HashMap<>();
        this.lastBoatNotification = new HashMap<>();
        this.lastMinecartNotification = new HashMap<>();
        this.lastRunningNotification = new HashMap<>();
        this.bingoUtil = bingoUtil;
    }

    public void Reset()
    {
        this.playerNonVehicleDistances = new HashMap<>();
        this.playerBoatDistances = new HashMap<>();
        this.playerMinecartDistances = new HashMap<>();
        this.lastBoatNotification = new HashMap<>();
        this.lastMinecartNotification = new HashMap<>();
        this.lastRunningNotification = new HashMap<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Player player = event.getPlayer();

        if (!(event.getPlayer().getWorld().getName().equals(WorldUtil.bingoWorldName) || event.getPlayer().getWorld().getName().equals(WorldUtil.bingoWorldName + "_nether")))
        {
            return;
        }

        double newDistance = Math.sqrt(Math.pow(event.getTo().getX()-event.getFrom().getX(),2) + Math.pow(event.getTo().getZ()-event.getFrom().getZ(),2));
        if (player.isInsideVehicle())
        {
            if (player.getVehicle().getType() == EntityType.BOAT || player.getVehicle().getType() == EntityType.CHEST_BOAT)
            {
                playerBoatDistances.put(player.getUniqueId(), playerBoatDistances.getOrDefault(player.getUniqueId(), 0.0) + newDistance);
                int dist = (int) Math.floor(playerBoatDistances.get(player.getUniqueId()));
                if (dist % 100 == 0 && dist > 0 && (!lastBoatNotification.containsKey(player.getUniqueId()) || lastBoatNotification.get(player.getUniqueId()) < dist) && bingoUtil.activeTravelType == TYPE.BOAT)
                {
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED WHISPER] " + ChatColor.GRAY + "You have travelled " + (dist) + " blocks on a boat.");
                    lastBoatNotification.put(player.getUniqueId(),dist);
                }
            } /*else if (player.getVehicle().getType() == EntityType.MINECART)
            {
                playerMinecartDistances.put(player.getUniqueId(), playerMinecartDistances.getOrDefault(player.getUniqueId(), 0.0) + newDistance);
                int dist = (int) Math.floor(playerMinecartDistances.get(player.getUniqueId()));
                if (dist % 50 == 0 && dist > 0  && (!lastMinecartNotification.containsKey(player.getUniqueId()) || lastMinecartNotification.get(player.getUniqueId()) < dist) && bingoUtil.activeTravelType == TYPE.MINECART)
                {
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED WHISPER] " + ChatColor.GRAY + "You have travelled " + (dist) + " blocks in a minecart.");
                    lastMinecartNotification.put(player.getUniqueId(),dist);
                }
            }*/
        } else
        {
            playerNonVehicleDistances.put(player.getUniqueId(), playerNonVehicleDistances.getOrDefault(player.getUniqueId(), 0.0) + newDistance);
            int dist = (int) Math.floor(playerNonVehicleDistances.get(player.getUniqueId()));
            if (dist % 250 == 0 && dist > 0 && (!lastRunningNotification.containsKey(player.getUniqueId()) || lastRunningNotification.get(player.getUniqueId()) < dist) && bingoUtil.activeTravelType == TYPE.RUNNING)
            {
                player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED WHISPER] " + ChatColor.GRAY + "You have travelled " + (dist) + " blocks on foot.");
                lastRunningNotification.put(player.getUniqueId(),dist);
            }
        }
    }

    public boolean hasPlayerTravelledEnough(Player player, double travelDistanceNeeded, TYPE type)
    {
        if (type == TYPE.BOAT)
        {
            try
            {
                if ((int) Math.floor(playerBoatDistances.get(player.getUniqueId())) % 25 == 0) Bukkit.getLogger().info(player + "has travelled " + playerBoatDistances.get(player.getUniqueId()) + " in a boat");
                return playerBoatDistances.get(player.getUniqueId()) > travelDistanceNeeded;
            } catch (Exception e)
            {
                return false;
            }
        } else if (type == TYPE.RUNNING)
        {
            try
            {
                if ((int) Math.floor(playerNonVehicleDistances.get(player.getUniqueId())) % 25 == 0) Bukkit.getLogger().info(player + "has travelled " + playerNonVehicleDistances.get(player.getUniqueId()) + " on foot");
                return playerNonVehicleDistances.get(player.getUniqueId()) > travelDistanceNeeded;
            } catch (Exception e)
            {
                return false;
            }
        } /*else if (type == TYPE.MINECART)
        {
            try
            {
                if ((int) Math.floor(playerMinecartDistances.get(player.getUniqueId())) % 25 == 0) Bukkit.getLogger().info(player + " has travelled " + playerMinecartDistances.get(player.getUniqueId()) + " in a minecart");
                return playerMinecartDistances.get(player.getUniqueId()) > travelDistanceNeeded;
            } catch (Exception e)
            {
                return false;
            }
        }*/
        return false;
    }
}
