package org.zethcodes.bingodunked.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.zethcodes.bingodunked.util.BingoUtil;
import java.util.List;

public class TeamChatHandler implements Listener {

    BingoUtil bingoUtil;

    public TeamChatHandler(BingoUtil bingoUtil)
    {
        this.bingoUtil = bingoUtil;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.STARTED)
        {
            if (bingoUtil.gameMode == BingoUtil.Mode.TEAM)
            {
                BingoUtil.Team team = bingoUtil.getTeam(event.getPlayer());
                List<Player> teammatesList = bingoUtil.GetPlayersOnTeam(team);

                for (Player p : teammatesList)
                {
                    p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [TEAM CHAT] " + bingoUtil.getTeamChatColour(team) + ChatColor.BOLD + event.getPlayer().getName() + ChatColor.WHITE + ": " + event.getMessage());

                }

            } else if (bingoUtil.gameMode == BingoUtil.Mode.FFA)
            {
                ChatColor teamChatColour = bingoUtil.getTeamChatColour(event.getPlayer());
                Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [ALL CHAT] " + teamChatColour + ChatColor.BOLD + event.getPlayer().getName() + ChatColor.WHITE + ": " + event.getMessage());
            }
        } else
        {
            ChatColor teamChatColour = bingoUtil.getTeamChatColour(event.getPlayer());
            Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [ALL CHAT] " + teamChatColour + ChatColor.BOLD + event.getPlayer().getName() + ChatColor.WHITE + ": " + event.getMessage());
        }

        event.setCancelled(true);
    }
}
