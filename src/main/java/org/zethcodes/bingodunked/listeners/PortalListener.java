package org.zethcodes.bingodunked.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.WorldUtil;

public class PortalListener implements Listener {

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Location location = event.getFrom();
        World fromWorld = location.getWorld();
        Location destination = event.getTo();

        if (fromWorld == null || destination == null) return;

        String bingoWorld = WorldUtil.bingoWorldName;
        World.Environment targetEnv = destination.getWorld().getEnvironment();

        if (fromWorld.getName().equals(bingoWorld)) {
            if (targetEnv == World.Environment.NETHER) {
                Location netherLoc = new Location(Bukkit.getWorld(bingoWorld + "_nether"), location.getX() / 8, location.getY() / 8, location.getZ() / 8);
                event.setTo(netherLoc);
            } else if (targetEnv == World.Environment.THE_END) {
                World endWorld = Bukkit.getWorld(bingoWorld + "_the_end");
                if (endWorld != null) {
                    Location endSpawn = new Location(endWorld, 100, 49, 0);
                    event.setTo(endSpawn);
                }
            }
        } else if (fromWorld.getName().equals(bingoWorld + "_nether")) {
            Location overworldLoc = new Location(Bukkit.getWorld(bingoWorld), location.getX() * 8, location.getY(), location.getZ() * 8);
            event.setTo(overworldLoc);
        } else if (fromWorld.getName().equals(bingoWorld + "_the_end")) {
            Location overworldSpawn = Bukkit.getWorld(bingoWorld).getSpawnLocation();
            event.setTo(overworldSpawn);
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        Location location = event.getFrom();
        World fromWorld = location.getWorld();
        Location destination = event.getTo();

        if (fromWorld == null || destination == null) return;

        String bingoWorld = WorldUtil.bingoWorldName;
        World.Environment targetEnv = destination.getWorld().getEnvironment();

        if (fromWorld.getName().equals(bingoWorld)) {
            if (targetEnv == World.Environment.NETHER) {
                Location netherLoc = new Location(Bukkit.getWorld(bingoWorld + "_nether"), location.getX() / 8, location.getY() / 8, location.getZ() / 8);
                event.setTo(netherLoc);
            } else if (targetEnv == World.Environment.THE_END) {
                World endWorld = Bukkit.getWorld(bingoWorld + "_the_end");
                if (endWorld != null) {
                    Location endSpawn = new Location(endWorld, 100, 49, 0);
                    event.setTo(endSpawn);
                }
            }
        } else if (fromWorld.getName().equals(bingoWorld + "_nether")) {
            Location overworldLoc = new Location(Bukkit.getWorld(bingoWorld), location.getX() * 8, location.getY(), location.getZ() * 8);
            event.setTo(overworldLoc);
        } else if (fromWorld.getName().equals(bingoWorld + "_the_end")) {
            Location overworldSpawn = Bukkit.getWorld(bingoWorld).getSpawnLocation();
            event.setTo(overworldSpawn);
        }
    }
}

