package org.zethcodes.bingodunked.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.ArrayList;
import java.util.List;

public class BingoSettings implements CommandExecutor, TabExecutor {
    private BingoUtil bingoUtil;

    public BingoSettings(BingoUtil bingoUtil) {
        this.bingoUtil = bingoUtil;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {

        bingoUtil.openSettings((Player) sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        return new ArrayList<>();
    }
}
