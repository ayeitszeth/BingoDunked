package org.zethcodes.bingodunked.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.managers.TeamsManager;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BingoCheat implements CommandExecutor, TabExecutor {

    List<Integer> slots;
    HashMap<Integer,Integer> slotMap = new HashMap<>();

    public BingoCheat() {
        slots = Arrays.asList(3,4,5,12,13,14,21,22,23);
        for (int i = 0; i < slots.size(); i++) {
            slotMap.put(i + 1, slots.get(i));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command!!!");
            return true;
        }

        if (args.length < 1) {
            BingoUtil.BingoWhisper((Player) sender,"Please enter at least the slot number.");
            return true;
        }

        int slot;
        try {
            slot = Integer.parseInt(args[0]);
            slot = slotMap.get(slot);
        } catch (NumberFormatException e) {
            BingoUtil.BingoWhisper((Player) sender,"Invalid slot number.");
            return true;
        }

        if (args.length == 1) {
            int col = (slot % 9) - 3;
            int row = slot / 9;
            GameManager.instance.boardManager.ResetBoardsAtSlot(col,row);
            GameManager.instance.boardManager.newGoal(slot);
            return true;
        }

        TeamsManager.Team team;
        try {
            team = TeamsManager.Team.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            BingoUtil.BingoWhisper((Player) sender,"Please enter a valid team");
            return true;
        }

        Player targetPlayer;
        if (args.length >= 3) {
            targetPlayer = Bukkit.getPlayer(args[2]);
            if (targetPlayer == null) {
                BingoUtil.BingoWhisper((Player) sender,"Player not found.");
                return true;
            } else
            {
                team = GameManager.instance.teamsManager.getTeam(targetPlayer);
            }
        } else {
            targetPlayer = (Player) sender;
        }

        GameManager.instance.boardManager.completeGoal(slot, team, targetPlayer);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9");
        } else if (args.length == 2) {
            completions = Arrays.stream(TeamsManager.Team.values())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        } else if (args.length == 3) {
            completions = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }

        String currentArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(currentArg))
                .collect(Collectors.toList());
    }
}
