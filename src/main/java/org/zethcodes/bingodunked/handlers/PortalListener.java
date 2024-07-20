package org.zethcodes.bingodunked.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.WorldUtil;

public class PortalListener implements Listener {

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Location location = event.getFrom();
        World fromWorld = location.getWorld();

        if (fromWorld != null && fromWorld.getName().equals(WorldUtil.bingoWorldName)) {
            if (event.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
                Location netherLoc = new Location(Bukkit.getWorld(WorldUtil.bingoWorldName + "_nether"), location.getX()/8,location.getY()/8,location.getZ()/8);
                event.setTo(netherLoc);
            } else if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
                event.setTo(Bukkit.getWorld(WorldUtil.bingoWorldName).getSpawnLocation());
                BingoUtil.BingoWhisper(event.getPlayer(),"The end is disabled in this game...");
            }
        } else if (fromWorld != null && fromWorld.getName().equals(WorldUtil.bingoWorldName + "_nether")) {
            Location overworldLoc = new Location(Bukkit.getWorld(WorldUtil.bingoWorldName), location.getX()*8,location.getY()*8,location.getZ()*8);
            event.setTo(overworldLoc);
        }
    }
}

