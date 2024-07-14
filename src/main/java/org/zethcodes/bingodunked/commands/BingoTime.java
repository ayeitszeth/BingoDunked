package org.zethcodes.bingodunked.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.ArrayList;
import java.util.List;

public class BingoTime implements CommandExecutor, TabExecutor {
    private BingoUtil bingoUtil;

    public BingoTime(BingoUtil bingoUtil) {
        this.bingoUtil = bingoUtil;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (bingoUtil.gameMode == BingoUtil.Mode.FFA)
        {
            bingoUtil.SendPlayerTime((Player) sender);
        } else
        {
            bingoUtil.BingoWhisper((Player) sender, "The time is not relevant or has not been started as of yet.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        return new ArrayList<>();
    }
}
