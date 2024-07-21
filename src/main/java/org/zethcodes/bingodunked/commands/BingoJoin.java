package org.zethcodes.bingodunked.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.ArrayList;
import java.util.List;

public class BingoJoin implements CommandExecutor, TabExecutor {

    BingoUtil bingoUtil;

    public BingoJoin(BingoUtil bingoUtil)
    {
        this.bingoUtil = bingoUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0)
        {
            BingoUtil.BingoWhisper(player, "Please input a team name.");
            return true;
        }

        String strTeam = args[0].toLowerCase();

        if (strTeam.equals("red") || strTeam.equals("blue") || strTeam.equals("green") || strTeam.equals("yellow"))
        {
            if (strTeam.equals("red")) {
                bingoUtil.JoinTeam(player, BingoUtil.Team.RED);
            } else if (strTeam.equals("blue")) {
                bingoUtil.JoinTeam(player, BingoUtil.Team.BLUE);
            } else if (strTeam.equals("green")) {
                bingoUtil.JoinTeam(player, BingoUtil.Team.GREEN);
            } else {
                bingoUtil.JoinTeam(player, BingoUtil.Team.YELLOW);
            }
        } else
        {
            BingoUtil.BingoWhisper(player,"Incorrect team name. Input either red, blue, green or yellow.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        List<String> teamNames = new ArrayList<>();
        teamNames.add("red");
        teamNames.add("blue");
        teamNames.add("green");
        teamNames.add("yellow");
        return teamNames;
    }
}
