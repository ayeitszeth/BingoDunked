package org.zethcodes.bingodunked;

import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.zethcodes.bingodunked.commands.*;
import org.zethcodes.bingodunked.handlers.*;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.DelayedTask;
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
        new DelayedTask(this);
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

        BingoUtil bingoUtil = new BingoUtil(this,killEntityListener,breedEntityListener,potionEffectListener, enchantListener,
                fishingListener,fallHeightListener,experienceListener, eatListener, blockInteractListener,
                armorStandInteractListener);

        new BingoHandler(this,bingoUtil);

        TeamChatHandler teamChatHandler = new TeamChatHandler(bingoUtil);
        getServer().getPluginManager().registerEvents(teamChatHandler, this);

        PortalListener portalListener = new PortalListener();
        getServer().getPluginManager().registerEvents(portalListener, this);
        BingoCard bingoCard = new BingoCard(bingoUtil);
        getCommand("bingocard").setExecutor(bingoCard);
        getCommand("bingocard").setTabCompleter(bingoCard);
        BingoJoin bingoJoin = new BingoJoin(bingoUtil);
        getCommand("bingojoin").setExecutor(bingoJoin);
        getCommand("bingojoin").setTabCompleter(bingoJoin);
        BingoStart bingoStart = new BingoStart(bingoUtil);
        getCommand("bingostart").setExecutor(bingoStart);
        getCommand("bingostart").setTabCompleter(bingoStart);
        NewWorld newWorld = new NewWorld(new WorldUtil(this,bingoUtil));
        getCommand("newworld").setExecutor(newWorld);
        getCommand("newworld").setTabCompleter(newWorld);
        BingoCheat bingoCheat = new BingoCheat(bingoUtil);
        getCommand("cheat").setExecutor(bingoCheat);
        getCommand("cheat").setTabCompleter(bingoCheat);
        BingoLateJoin bingoLateJoin = new BingoLateJoin(bingoUtil);
        getCommand("latejoin").setExecutor(bingoLateJoin);
        getCommand("latejoin").setTabCompleter(bingoLateJoin);
        AllChat allChat = new AllChat(bingoUtil);
        getCommand("all").setExecutor(allChat);
        getCommand("all").setTabCompleter(allChat);
        BingoTime bingoTime = new BingoTime(bingoUtil);
        getCommand("time").setExecutor(bingoTime);
        getCommand("time").setTabCompleter(bingoTime);

    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("DUNKED is shutting down...");
    }
}
