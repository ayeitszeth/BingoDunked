package org.zethcodes.bingodunked.commands;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.managers.SettingsManager;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.WorldUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BingoLateJoin implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        Player player = (Player) sender;
        Location spawnLoc;
        try
        {
            spawnLoc = Bukkit.getWorld(WorldUtil.bingoWorldName).getSpawnLocation();
        } catch (NullPointerException e)
        {
            BingoUtil.BingoWhisper(player, "A new world is yet to be generated for a game of Bingo.");
            return true;
        }

        player.teleport(spawnLoc);
        player.setBedSpawnLocation(spawnLoc, true);
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.getInventory().clear();
        player.setLevel(0);
        player.setExp(0);
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
        for (PotionEffectType effectType : PotionEffectType.values()) {
            if (effectType != null) {
                player.removePotionEffect(effectType);
            }
        }

        for (Iterator<Advancement> it = Bukkit.getServer().advancementIterator(); it.hasNext(); ) {
            Advancement advancement = it.next();
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }

        if (SettingsManager.gameMode == SettingsManager.Mode.FFA)
        {
            GameManager.instance.teamsManager.FFALateJoin(player);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        return new ArrayList<>();
    }
}
