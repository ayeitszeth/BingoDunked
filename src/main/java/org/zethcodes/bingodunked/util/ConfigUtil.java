package org.zethcodes.bingodunked.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigUtil {
    private File file;
    private FileConfiguration config;

    public ConfigUtil(Plugin plugin, String path)
    {
        this(plugin.getDataFolder().getAbsolutePath() + "/" + path);
    }

    public ConfigUtil(Plugin plugin, Player player)
    {
        this(plugin.getDataFolder().getAbsolutePath() + "/player/" + player.getUniqueId());
    }

    public static String getPath(Player player)
    {
        return Bukkit.getPluginManager().getPlugin("ZombiesArena").getDataFolder().getAbsolutePath() + "/player/" + player.getUniqueId();
    }

    public ConfigUtil(String path)
    {
        this.file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public boolean save()
    {
        try
        {
            this.config.save(this.file);
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public File getFile()
    {
        return this.file;
    }

    public FileConfiguration getConfig()
    {
        return this.config;
    }

}
