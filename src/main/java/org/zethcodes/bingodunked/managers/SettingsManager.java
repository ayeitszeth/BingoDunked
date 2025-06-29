package org.zethcodes.bingodunked.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zethcodes.bingodunked.util.BingoUtil;

public class SettingsManager {

    Inventory settingsGUI;
    public static boolean isPvpEnabled;

    ItemStack enabled = new ItemStack(Material.LIME_WOOL, 1);
    ItemStack disabled = new ItemStack(Material.RED_WOOL, 1);

    public BingoUtil.Mode gameMode = BingoUtil.Mode.TEAM;
    public BingoUtil.Difficulty difficulty = BingoUtil.Difficulty.NORMAL;
    public BingoUtil.PvP pvp = BingoUtil.PvP.PVP;

    public void SettingsSetUp() {
        settingsGUI = Bukkit.createInventory(null, 45, "Bingo Settings");

        ItemMeta enabledMeta = enabled.getItemMeta();
        enabledMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Enabled");
        enabled.setItemMeta(enabledMeta);

        ItemMeta disabledMeta = disabled.getItemMeta();
        disabledMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Disabled");
        disabled.setItemMeta(disabledMeta);

        SettingsInvSetUp();
    }

    public void SettingsInvSetUp() {
        ItemStack teams = new ItemStack(Material.SHIELD, 1);
        ItemMeta teamsMeta = teams.getItemMeta();
        teamsMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "TEAMS Mode");
        teams.setItemMeta(teamsMeta);
        settingsGUI.setItem(11, teams);

        settingsGUI.setItem(12, enabled);
        settingsGUI.setItem(21, enabled);
        settingsGUI.setItem(30, enabled);

        settingsGUI.setItem(15, disabled);
        settingsGUI.setItem(24, disabled);
        settingsGUI.setItem(28, disabled);
        settingsGUI.setItem(33, disabled);
        settingsGUI.setItem(35, disabled);

        ItemStack ffaMode = new ItemStack(Material.NETHERITE_SWORD, 1);
        ItemMeta ffaModeMeta = ffaMode.getItemMeta();
        ffaModeMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "FFA Mode");
        ffaMode.setItemMeta(ffaModeMeta);
        settingsGUI.setItem(14, ffaMode);

        ItemStack normalMode = new ItemStack(Material.GRASS_BLOCK, 1);
        ItemMeta normalModeMeta = normalMode.getItemMeta();
        normalModeMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Normal Difficulty");
        normalMode.setItemMeta(normalModeMeta);
        settingsGUI.setItem(20, normalMode);

        ItemStack insaneMode = new ItemStack(Material.CRYING_OBSIDIAN, 1);
        ItemMeta insaneModeMeta = insaneMode.getItemMeta();
        insaneModeMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Insane Difficulty");
        insaneMode.setItemMeta(insaneModeMeta);
        settingsGUI.setItem(23, insaneMode);

        ItemStack noPvP = new ItemStack(Material.CAKE, 1);
        ItemMeta noPvPMeta = noPvP.getItemMeta();
        noPvPMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "No PvP");
        noPvP.setItemMeta(noPvPMeta);
        settingsGUI.setItem(27, noPvP);

        ItemStack pvp = new ItemStack(Material.CROSSBOW, 1);
        ItemMeta pvpMeta = pvp.getItemMeta();
        pvpMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "PvP");
        pvp.setItemMeta(pvpMeta);
        settingsGUI.setItem(29, pvp);

        ItemStack glowingPvP = new ItemStack(Material.SPECTRAL_ARROW, 1);
        ItemMeta glowingPvPMeta = glowingPvP.getItemMeta();
        glowingPvPMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Glowing PvP");
        glowingPvP.setItemMeta(glowingPvPMeta);
        settingsGUI.setItem(32, glowingPvP);

        ItemStack trackingPvP = new ItemStack(Material.COMPASS, 1);
        ItemMeta trackingPvPMeta = trackingPvP.getItemMeta();
        trackingPvPMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Tracking PvP");
        trackingPvP.setItemMeta(trackingPvPMeta);
        settingsGUI.setItem(34, trackingPvP);
    }

    public void updateGameMode(BingoUtil.Mode mode) {
        if (mode == null) return;
        gameMode = mode;

        if (mode == BingoUtil.Mode.TEAM)
        {
            settingsGUI.setItem(12,enabled);
            settingsGUI.setItem(15,disabled);
        } else if (mode == BingoUtil.Mode.FFA)
        {
            settingsGUI.setItem(12,disabled);
            settingsGUI.setItem(15,enabled);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 2.0f);
        }

        BingoAnnounce("The game mode has been updated to " + ChatColor.BOLD + gameMode.name() + "!");
    }

    public void updateDifficulty(BingoUtil.Difficulty difficulty) {
        if (difficulty == null) return;
        this.difficulty = difficulty;

        if (difficulty == BingoUtil.Difficulty.NORMAL)
        {
            settingsGUI.setItem(21,enabled);
            settingsGUI.setItem(24,disabled);
        } else if (difficulty == BingoUtil.Difficulty.INSANE)
        {
            settingsGUI.setItem(21,disabled);
            settingsGUI.setItem(24,enabled);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 2.0f);
        }

        BingoUtil.BingoAnnounce("The difficulty has been updated to " + ChatColor.BOLD + difficulty.name() + "!");
    }

    public void updatePvp(BingoUtil.PvP pvp)
    {
        if (pvp == null) return;
        this.pvp = pvp;

        if (pvp == BingoUtil.PvP.NOPVP)
        {
            settingsGUI.setItem(28,enabled);
            settingsGUI.setItem(30,disabled);
            settingsGUI.setItem(33,disabled);
            settingsGUI.setItem(35,disabled);
        } else if (pvp == BingoUtil.PvP.PVP)
        {
            settingsGUI.setItem(28,disabled);
            settingsGUI.setItem(30,enabled);
            settingsGUI.setItem(33,disabled);
            settingsGUI.setItem(35,disabled);
        } else if (pvp == BingoUtil.PvP.GLOWING_PVP)
        {
            settingsGUI.setItem(28,disabled);
            settingsGUI.setItem(30,disabled);
            settingsGUI.setItem(33,enabled);
            settingsGUI.setItem(35,disabled);
        } else if (pvp == BingoUtil.PvP.TRACKING_PVP)
        {
            settingsGUI.setItem(28,disabled);
            settingsGUI.setItem(30,disabled);
            settingsGUI.setItem(33,disabled);
            settingsGUI.setItem(35,enabled);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 2.0f);
            updatePlayerTabListName(p);
        }

        BingoUtil.BingoAnnounce("The difficulty has been updated to " + ChatColor.BOLD + pvp.name() + "!");

    }

    public void openSettings(Player player)
    {
        player.openInventory(settingsGUI);
    }

    public void setGUI(int slot, boolean isEnabled) {
        ItemStack guiItem = isEnabled ? enabled : disabled;
        settingsGUI.setItem(slot,guiItem);
    }
}
