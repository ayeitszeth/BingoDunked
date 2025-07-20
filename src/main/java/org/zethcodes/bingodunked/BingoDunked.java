package org.zethcodes.bingodunked;

import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.zethcodes.bingodunked.commands.*;
import org.zethcodes.bingodunked.handlers.*;
import org.zethcodes.bingodunked.listeners.*;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.managers.SettingsManager;
import org.zethcodes.bingodunked.managers.TaskManager;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.WorldUtil;

public final class BingoDunked extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("DUNKED has been successfully loaded!");

        KillEntityListener killEntityListener = new KillEntityListener();
        BreedEntityListener breedEntityListener = new BreedEntityListener();
        PotionEffectListener potionEffectListener = new PotionEffectListener();
        EnchantListener enchantListener = new EnchantListener();
        FishingListener fishingListener = new FishingListener();
        FallHeightListener fallHeightListener = new FallHeightListener();
        ExperienceListener experienceListener = new ExperienceListener();
        EatListener eatListener = new EatListener();
        BlockInteractListener blockInteractListener = new BlockInteractListener();
        ArmorStandInteractListener armorStandInteractListener = new ArmorStandInteractListener();
        DeathListener deathListener = new DeathListener();
        CauldronListener cauldronListener = new CauldronListener();
        AdvancementListener advancementListener = new AdvancementListener();

        getServer().getPluginManager().registerEvents(killEntityListener, this);
        getServer().getPluginManager().registerEvents(breedEntityListener, this);
        getServer().getPluginManager().registerEvents(potionEffectListener, this);
        getServer().getPluginManager().registerEvents(enchantListener, this);
        getServer().getPluginManager().registerEvents(fishingListener, this);
        getServer().getPluginManager().registerEvents(fallHeightListener, this);
        getServer().getPluginManager().registerEvents(experienceListener, this);
        getServer().getPluginManager().registerEvents(eatListener, this);
        getServer().getPluginManager().registerEvents(blockInteractListener, this);
        getServer().getPluginManager().registerEvents(armorStandInteractListener, this);
        getServer().getPluginManager().registerEvents(deathListener,this);
        getServer().getPluginManager().registerEvents(cauldronListener, this);
        getServer().getPluginManager().registerEvents(advancementListener, this);

        new SettingsManager();
        new TaskManager();

        GameManager gameManager = new GameManager(this,killEntityListener,breedEntityListener,potionEffectListener, enchantListener,
                fishingListener,fallHeightListener,experienceListener, eatListener, blockInteractListener,
                armorStandInteractListener, deathListener, cauldronListener, advancementListener);

        new BingoHandler();

        SettingsListener settingsListener = new SettingsListener();
        getServer().getPluginManager().registerEvents(settingsListener, this);

        TeamChatHandler teamChatHandler = new TeamChatHandler();
        getServer().getPluginManager().registerEvents(teamChatHandler, this);

        PortalListener portalListener = new PortalListener();
        getServer().getPluginManager().registerEvents(portalListener, this);
        BingoCard bingoCard = new BingoCard();
        getCommand("bingocard").setExecutor(bingoCard);
        getCommand("bingocard").setTabCompleter(bingoCard);
        BingoJoin bingoJoin = new BingoJoin();
        getCommand("bingojoin").setExecutor(bingoJoin);
        getCommand("bingojoin").setTabCompleter(bingoJoin);
        BingoStart bingoStart = new BingoStart();
        getCommand("bingostart").setExecutor(bingoStart);
        getCommand("bingostart").setTabCompleter(bingoStart);
        NewWorld newWorld = new NewWorld(new WorldUtil());
        getCommand("newworld").setExecutor(newWorld);
        getCommand("newworld").setTabCompleter(newWorld);
        BingoCheat bingoCheat = new BingoCheat();
        getCommand("cheat").setExecutor(bingoCheat);
        getCommand("cheat").setTabCompleter(bingoCheat);
        BingoLateJoin bingoLateJoin = new BingoLateJoin();
        getCommand("latejoin").setExecutor(bingoLateJoin);
        getCommand("latejoin").setTabCompleter(bingoLateJoin);
        AllChat allChat = new AllChat();
        getCommand("all").setExecutor(allChat);
        getCommand("all").setTabCompleter(allChat);
        BingoTime bingoTime = new BingoTime();
        getCommand("bingotime").setExecutor(bingoTime);
        getCommand("bingotime").setTabCompleter(bingoTime);
        BingoSettings bingoSettings = new BingoSettings();
        getCommand("settings").setExecutor(bingoSettings);
        getCommand("settings").setTabCompleter(bingoSettings);
        EndGame endGame = new EndGame();
        getCommand("endgame").setExecutor(endGame);
        getCommand("endgame").setTabCompleter(endGame);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("DUNKED is shutting down...");
    }
}
