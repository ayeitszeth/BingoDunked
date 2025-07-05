package org.zethcodes.bingodunked.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.managers.SettingsManager;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.ArrayList;
import java.util.List;

public class BingoTime implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (SettingsManager.gameMode == SettingsManager.Mode.FFA)
        {
            GameManager.instance.SendPlayerTime((Player) sender);
        } else
        {
            BingoUtil.BingoWhisper((Player) sender, "The time is not relevant or has not been started as of yet.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        return new ArrayList<>();
    }
}
