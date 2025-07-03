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

public class BingoStart implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        Player player = (Player) sender;
        int time = 30; // default 30 minute timer for ffa, does not affect duel mode

        if (args.length == 0)
        {
            GameManager.instance.BingoSetUp(SettingsManager.gameMode, time);
            return true;
        }

        String mode = args[0].toLowerCase();

        if (mode.equals("team") || mode.equals("ffa"))
        {
            SettingsManager.Mode m = mode.equals("team") ? SettingsManager.Mode.TEAM : SettingsManager.Mode.FFA;
            if (m == SettingsManager.Mode.FFA)
            {
                try
                {
                    time = Integer.parseInt(args[1]);
                } catch (Exception ignored) {}
            }

            GameManager.instance.BingoSetUp(m, time);
        } else
        {
            BingoUtil.BingoWhisper(player, "Incorrect Mode name. Input either 'team' or 'ffa'");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        List<String> modes = new ArrayList<>();
        modes.add("team");
        modes.add("ffa");
        return modes;
    }
}
