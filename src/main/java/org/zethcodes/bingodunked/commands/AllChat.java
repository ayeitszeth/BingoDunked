package org.zethcodes.bingodunked.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.ArrayList;
import java.util.List;

public class AllChat implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        String message = "";
        for (String s : args)
        {
            message += s + " ";
        }

        ChatColor teamChatColour = GameManager.instance.teamsManager.getTeamChatColour((Player) sender);

        Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [ALL CHAT] " + teamChatColour + ChatColor.BOLD + sender.getName() + ChatColor.WHITE + ": " + message);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        return new ArrayList<>();
    }
}
