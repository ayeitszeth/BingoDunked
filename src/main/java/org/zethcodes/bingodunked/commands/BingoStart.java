package org.zethcodes.bingodunked.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.ArrayList;
import java.util.List;

public class BingoStart implements CommandExecutor, TabExecutor {

    BingoUtil bingoUtil;

    public BingoStart(BingoUtil bingoUtil)
    {
        this.bingoUtil = bingoUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        Player player = (Player) sender;

        if (args[0] == null)
        {
            bingoUtil.BingoWhisper(player, "Please enter a game mode. Input either 'team' or 'ffa'");
            return true;
        }

        String mode = args[0].toLowerCase();

        if (mode.equals("team") || mode.equals("ffa"))
        {
            BingoUtil.Mode m = mode.equals("team") ? BingoUtil.Mode.TEAM : BingoUtil.Mode.FFA;
            int time = 30; // default 30 minute timer for ffa, does not affect duel mode
            if (m == BingoUtil.Mode.FFA)
            {
                try
                {
                    time = Integer.parseInt(args[1]);
                } catch (Exception ignored) {}
            }

            bingoUtil.BingoSetUp(m,time);
        } else
        {
            bingoUtil.BingoWhisper(player, "Incorrect Mode name. Input either 'team' or 'ffa'");
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
