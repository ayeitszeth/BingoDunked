package org.zethcodes.bingodunked.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.WorldUtil;

import java.util.HashMap;
import java.util.UUID;

public class TravelListener implements Listener {
    private HashMap<UUID, Double> playerNonVehicleDistances;
    private HashMap<UUID, Double> playerBoatDistances;
    private HashMap<UUID, Double> playerMinecartDistances;
    private HashMap<UUID, Double> playerPigDistances;
    private HashMap<UUID, Double> playerHorseDistances;
    private HashMap<UUID, Double> playerStriderDistances;

    private HashMap<UUID, Integer> lastRunningNotification;
    private HashMap<UUID, Integer> lastBoatNotification;
    private HashMap<UUID, Integer> lastMinecartNotification;
    private HashMap<UUID, Integer> lastPigNotification;
    private HashMap<UUID, Integer> lastHorseNotification;
    private HashMap<UUID, Integer> lastStriderNotification;
    public enum TYPE { BOAT, RUNNING, MINECART, PIG, HORSE, STRIDER };

    public TravelListener() {
        this.playerNonVehicleDistances = new HashMap<>();
        this.playerBoatDistances = new HashMap<>();
        this.playerMinecartDistances = new HashMap<>();
        this.playerPigDistances = new HashMap<>();
        this.playerHorseDistances = new HashMap<>();
        this.playerStriderDistances = new HashMap<>();
        this.lastBoatNotification = new HashMap<>();
        this.lastMinecartNotification = new HashMap<>();
        this.lastRunningNotification = new HashMap<>();
        this.lastPigNotification = new HashMap<>();
        this.lastHorseNotification = new HashMap<>();
        this.lastStriderNotification = new HashMap<>();
    }

    public void Reset()
    {
        this.playerNonVehicleDistances = new HashMap<>();
        this.playerBoatDistances = new HashMap<>();
        this.playerMinecartDistances = new HashMap<>();
        this.playerPigDistances = new HashMap<>();
        this.playerHorseDistances = new HashMap<>();
        this.playerStriderDistances = new HashMap<>();
        this.lastBoatNotification = new HashMap<>();
        this.lastMinecartNotification = new HashMap<>();
        this.lastRunningNotification = new HashMap<>();
        this.lastPigNotification = new HashMap<>();
        this.lastHorseNotification = new HashMap<>();
        this.lastStriderNotification = new HashMap<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Player player = event.getPlayer();

        if (!(event.getPlayer().getWorld().getName().equals(WorldUtil.bingoWorldName) || event.getPlayer().getWorld().getName().equals(WorldUtil.bingoWorldName + "_nether")))
        {
            return;
        }

        double newDistance = Math.sqrt(Math.pow(event.getTo().getX()-event.getFrom().getX(),2) + Math.pow(event.getTo().getZ()-event.getFrom().getZ(),2));
        if (player.isInsideVehicle())
        {
            if (player.getVehicle().getType() == EntityType.ACACIA_BOAT || player.getVehicle().getType() == EntityType.ACACIA_CHEST_BOAT
                    || player.getVehicle().getType() == EntityType.BIRCH_BOAT || player.getVehicle().getType() == EntityType.BIRCH_CHEST_BOAT
                    || player.getVehicle().getType() == EntityType.CHERRY_BOAT || player.getVehicle().getType() == EntityType.CHERRY_CHEST_BOAT
                    || player.getVehicle().getType() == EntityType.DARK_OAK_BOAT || player.getVehicle().getType() == EntityType.DARK_OAK_CHEST_BOAT
                    || player.getVehicle().getType() == EntityType.JUNGLE_BOAT || player.getVehicle().getType() == EntityType.JUNGLE_CHEST_BOAT
                    || player.getVehicle().getType() == EntityType.OAK_BOAT || player.getVehicle().getType() == EntityType.OAK_CHEST_BOAT
                    || player.getVehicle().getType() == EntityType.PALE_OAK_BOAT || player.getVehicle().getType() == EntityType.PALE_OAK_CHEST_BOAT
                    || player.getVehicle().getType() == EntityType.SPRUCE_BOAT || player.getVehicle().getType() == EntityType.SPRUCE_CHEST_BOAT
                    || player.getVehicle().getType() == EntityType.MANGROVE_BOAT || player.getVehicle().getType() == EntityType.MANGROVE_CHEST_BOAT
                    || player.getVehicle().getType() == EntityType.BAMBOO_RAFT || player.getVehicle().getType() == EntityType.BAMBOO_CHEST_RAFT)
            {
                playerBoatDistances.put(player.getUniqueId(), playerBoatDistances.getOrDefault(player.getUniqueId(), 0.0) + newDistance);
                int dist = (int) Math.floor(playerBoatDistances.get(player.getUniqueId()));
                if (dist % 100 == 0 && dist > 0 && (!lastBoatNotification.containsKey(player.getUniqueId()) || lastBoatNotification.get(player.getUniqueId()) < dist) && GameManager.instance.boardManager.activeTravelType == TYPE.BOAT)
                {
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED WHISPER] " + ChatColor.GRAY + "You have travelled " + (dist) + " blocks on a boat.");
                    lastBoatNotification.put(player.getUniqueId(),dist);
                }
            } else if (player.getVehicle().getType() == EntityType.PIG)
            {
                playerPigDistances.put(player.getUniqueId(), playerPigDistances.getOrDefault(player.getUniqueId(), 0.0) + newDistance);
                int dist = (int) Math.floor(playerPigDistances.get(player.getUniqueId()));
                if (dist % 25 == 0 && dist > 0 && (!lastPigNotification.containsKey(player.getUniqueId()) || lastPigNotification.get(player.getUniqueId()) < dist) && GameManager.instance.boardManager.activeTravelType == TYPE.PIG)
                {
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED WHISPER] " + ChatColor.GRAY + "You have travelled " + (dist) + " blocks on a pig.");
                    lastPigNotification.put(player.getUniqueId(),dist);
                }
            } else if (player.getVehicle().getType() == EntityType.HORSE)
            {
                playerHorseDistances.put(player.getUniqueId(), playerHorseDistances.getOrDefault(player.getUniqueId(), 0.0) + newDistance);
                int dist = (int) Math.floor(playerHorseDistances.get(player.getUniqueId()));
                if (dist % 50 == 0 && dist > 0 && (!lastHorseNotification.containsKey(player.getUniqueId()) || lastHorseNotification.get(player.getUniqueId()) < dist) && GameManager.instance.boardManager.activeTravelType == TYPE.HORSE)
                {
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED WHISPER] " + ChatColor.GRAY + "You have travelled " + (dist) + " blocks on a horse.");
                    lastHorseNotification.put(player.getUniqueId(),dist);
                }
            } else if (player.getVehicle().getType() == EntityType.STRIDER)
            {
                playerStriderDistances.put(player.getUniqueId(), playerStriderDistances.getOrDefault(player.getUniqueId(), 0.0) + newDistance);
                int dist = (int) Math.floor(playerStriderDistances.get(player.getUniqueId()));
                if (dist % 25 == 0 && dist > 0 && (!lastStriderNotification.containsKey(player.getUniqueId()) || lastStriderNotification.get(player.getUniqueId()) < dist) && GameManager.instance.boardManager.activeTravelType == TYPE.STRIDER)
                {
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED WHISPER] " + ChatColor.GRAY + "You have travelled " + (dist) + " blocks on a strider.");
                    lastStriderNotification.put(player.getUniqueId(),dist);
                }
            }
        } else
        {
            playerNonVehicleDistances.put(player.getUniqueId(), playerNonVehicleDistances.getOrDefault(player.getUniqueId(), 0.0) + newDistance);
            int dist = (int) Math.floor(playerNonVehicleDistances.get(player.getUniqueId()));
            if (dist % 250 == 0 && dist > 0 && (!lastRunningNotification.containsKey(player.getUniqueId()) || lastRunningNotification.get(player.getUniqueId()) < dist) && GameManager.instance.boardManager.activeTravelType == TYPE.RUNNING)
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
            if ((int) Math.floor(playerBoatDistances.getOrDefault(player.getUniqueId(), 0.0)) % 25 == 0 && GameManager.DEBUG) Bukkit.getLogger().info(player + "has travelled " + Math.floor(playerBoatDistances.getOrDefault(player.getUniqueId(),0.0)) + " in a boat");
            return playerBoatDistances.getOrDefault(player.getUniqueId(),0.0) > travelDistanceNeeded;
        } else if (type == TYPE.RUNNING)
        {
            if ((int) Math.floor(playerNonVehicleDistances.getOrDefault(player.getUniqueId(),0.0)) % 25 == 0 && GameManager.DEBUG) Bukkit.getLogger().info(player + "has travelled " + Math.floor(playerNonVehicleDistances.getOrDefault(player.getUniqueId(),0.0)) + " on foot");
            return playerNonVehicleDistances.getOrDefault(player.getUniqueId(),0.0) > travelDistanceNeeded;
        } else if (type == TYPE.PIG)
        {
            if ((int) Math.floor(playerPigDistances.getOrDefault(player.getUniqueId(),0.0)) % 25 == 0 && GameManager.DEBUG) Bukkit.getLogger().info(player + "has travelled " + Math.floor(playerPigDistances.getOrDefault(player.getUniqueId(),0.0)) + " on a pig.");
            return playerPigDistances.getOrDefault(player.getUniqueId(),0.0) > travelDistanceNeeded;
        } else if (type == TYPE.HORSE)
        {
            if ((int) Math.floor(playerHorseDistances.getOrDefault(player.getUniqueId(),0.0)) % 25 == 0 && GameManager.DEBUG) Bukkit.getLogger().info(player + "has travelled " + Math.floor(playerHorseDistances.getOrDefault(player.getUniqueId(),0.0)) + " on a horse.");
            return playerHorseDistances.getOrDefault(player.getUniqueId(),0.0) > travelDistanceNeeded;
        } else if (type == TYPE.STRIDER)
        {
            if ((int) Math.floor(playerStriderDistances.getOrDefault(player.getUniqueId(),0.0)) % 25 == 0 && GameManager.DEBUG) Bukkit.getLogger().info(player + "has travelled " + Math.floor(playerStriderDistances.getOrDefault(player.getUniqueId(),0.0)) + " on a strider.");
            return playerStriderDistances.getOrDefault(player.getUniqueId(),0.0) > travelDistanceNeeded;
        }
        return false;
    }
}