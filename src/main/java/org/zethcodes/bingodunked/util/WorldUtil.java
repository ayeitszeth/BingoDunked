package org.zethcodes.bingodunked.util;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class WorldUtil {

    public static World lobbyWorld;
    public static String bingoWorldName;
    private Plugin plugin;
    private BingoUtil bingoUtil;

    public WorldUtil(Plugin plugin, BingoUtil bingoUtil) {
        this.plugin = plugin;
        this.bingoUtil = bingoUtil;
        lobbyWorld = Bukkit.getWorld("world");
        bingoWorldName = "world";
    }

    public void createNewWorld() {
        bingoUtil.cancelAllTasks();
        lobbyWorld = Bukkit.getWorld("world");
        bingoUtil.BingoAnnounce("A new world is being generated!");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(lobbyWorld.getSpawnLocation());
        }
        bingoWorldName = "bingo_world";

        File bingoWorld = new File(bingoWorldName);
        if (bingoWorld.exists())
        {
            Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(bingoWorldName), false);
            deleteWorldFolder(bingoWorld);
        }

        File bingoNetherWorld = new File(bingoWorldName + "_nether");
        if (bingoNetherWorld.exists())
        {
            Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(bingoWorldName + "_nether"), false);
            deleteWorldFolder(bingoNetherWorld);
        }

        File bingoEndWorld = new File(bingoWorldName + "_the_end");
        if (bingoEndWorld.exists())
        {
            Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(bingoWorldName + "_the_end"), false);
            deleteWorldFolder(bingoEndWorld);
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.createWorld(new WorldCreator(bingoWorldName).environment(World.Environment.NORMAL));
            Bukkit.createWorld(new WorldCreator(bingoWorldName + "_nether").environment(World.Environment.NETHER));
            Bukkit.createWorld(new WorldCreator(bingoWorldName + "_the_end").environment(World.Environment.THE_END));

            bingoUtil.BingoAnnounce("All worlds have been generated!");
        });
    }

    private void deleteWorldFolder(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        path.delete();
    }
}
