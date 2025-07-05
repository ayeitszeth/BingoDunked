package org.zethcodes.bingodunked.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.managers.SettingsManager;
import org.zethcodes.bingodunked.managers.TeamsManager;
import org.zethcodes.bingodunked.util.BingoUtil;
import java.util.List;

public class TeamChatHandler implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.STARTED)
        {
            if (SettingsManager.gameMode == SettingsManager.Mode.TEAM)
            {
                TeamsManager.Team team = GameManager.instance.teamsManager.getTeam(event.getPlayer());
                List<Player> teammatesList = GameManager.instance.teamsManager.GetPlayersOnTeam(team);

                for (Player p : teammatesList)
                {
                    if (p == null) continue;
                    p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [TEAM CHAT] " + GameManager.instance.teamsManager.getTeamChatColour(team) + ChatColor.BOLD + event.getPlayer().getName() + ChatColor.WHITE + ": " + event.getMessage());

                }

            } else if (SettingsManager.gameMode == SettingsManager.Mode.FFA)
            {
                ChatColor teamChatColour = GameManager.instance.teamsManager.getTeamChatColour(event.getPlayer());
                Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [ALL CHAT] " + teamChatColour + ChatColor.BOLD + event.getPlayer().getName() + ChatColor.WHITE + ": " + event.getMessage());
            }
        } else
        {
            ChatColor teamChatColour = GameManager.instance.teamsManager.getTeamChatColour(event.getPlayer());
            Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [ALL CHAT] " + teamChatColour + ChatColor.BOLD + event.getPlayer().getName() + ChatColor.WHITE + ": " + event.getMessage());
        }

        event.setCancelled(true);
    }
}
