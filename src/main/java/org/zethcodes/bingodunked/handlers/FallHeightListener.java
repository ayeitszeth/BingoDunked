package org.zethcodes.bingodunked.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.WorldUtil;

import java.util.HashMap;
import java.util.UUID;

public class FallHeightListener implements Listener {
    private HashMap<UUID, Integer> playerLastFallHeight;
    private HashMap<UUID, Integer> playerLargestFall;

    public FallHeightListener() {
        this.playerLastFallHeight = new HashMap<>();
        this.playerLargestFall = new HashMap<>();
    }

    public void Reset()
    {
        this.playerLastFallHeight = new HashMap<>();
        this.playerLargestFall = new HashMap<>();
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if (!(event.getPlayer().getWorld().getName().equals(WorldUtil.bingoWorldName) || event.getPlayer().getWorld().getName().equals(WorldUtil.bingoWorldName + "_nether")))
        {
            return;
        }

        if (event.getFrom().getBlockY() > event.getTo().getBlockY())
        {
            try
            {
                int lastFallHeight = playerLastFallHeight.get(player.getUniqueId());
                if (event.getFrom().getBlockY() > lastFallHeight)
                {
                    playerLastFallHeight.put(player.getUniqueId(), event.getFrom().getBlockY());
                }
            } catch (Exception e)
            {
                playerLastFallHeight.put(player.getUniqueId(), event.getFrom().getBlockY());
            }
        }

        if (player.getVelocity().getY() > -0.1 && player.getVelocity().getY() < 0)
        {
            try
            {
                int fallHeight = playerLastFallHeight.get(player.getUniqueId()) - player.getLocation().getBlockY();
                if (fallHeight > playerLargestFall.get(player.getUniqueId()))
                {
                    playerLargestFall.put(player.getUniqueId(), fallHeight);
                    if (BingoUtil.DEBUG) Bukkit.getLogger().info(player + "'s new largest fall is " + playerLargestFall.get(player.getUniqueId()));
                }
            } catch (Exception e)
            {
                playerLargestFall.put(player.getUniqueId(), 0);
            }
            playerLastFallHeight.put(player.getUniqueId(),player.getLocation().getBlockY());
        }

        //player.sendMessage("Y-Velocity: " + player.getVelocity().getY());
        //player.sendMessage("Largest Fall: " + playerLargestFall.get(player.getUniqueId()));

    }

    public boolean hasPlayerFallenFarEnough(Player player, int fallHeightNeeded)
    {
        return playerLargestFall.get(player.getUniqueId()) > fallHeightNeeded;
    }
}
