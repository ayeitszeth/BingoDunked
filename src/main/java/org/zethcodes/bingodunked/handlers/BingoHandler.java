package org.zethcodes.bingodunked.handlers;


import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.zethcodes.bingodunked.goals.*;
import org.zethcodes.bingodunked.listeners.CauldronListener;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.managers.SettingsManager;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.WorldUtil;

public class BingoHandler implements Listener {

    public BingoHandler() {
        Bukkit.getPluginManager().registerEvents(this, GameManager.plugin);
        if (GameManager.DEBUG) Bukkit.getLogger().info(GameManager.plugin + "");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        BingoUtil.updatePlayerTabListName(event.getPlayer());
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String cardName = event.getView().getTitle();
        if (cardName.equals("Bingo Card")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();

        player.setFoodLevel(20);
        player.setSaturation(20);

        if (event.getPlayer().getWorld().equals(WorldUtil.lobbyWorld) || GameManager.gameState == GameManager.GameState.FINISHED)
        {
            Location spawnLoc = WorldUtil.lobbyWorld.getSpawnLocation();
            player.setBedSpawnLocation(spawnLoc, true);
            event.setRespawnLocation(spawnLoc);
            return;
        }

        if (!event.isBedSpawn())
        {
            Location spawnLoc = Bukkit.getWorld(WorldUtil.bingoWorldName).getSpawnLocation();
            event.getPlayer().setBedSpawnLocation(spawnLoc, true);
            event.setRespawnLocation(spawnLoc);
        }
    }

    @EventHandler
    public void onPickUpItem(EntityPickupItemEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        if (event.getEntity() instanceof Player)
        {
            Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete((Player) event.getEntity(), CollectItemGoal.class));
        }
    }

    @EventHandler
    public void onBucketItem(PlayerBucketFillEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(event.getPlayer(), CollectItemGoal.class));
    }

    @EventHandler
    public void onBucketEntity(PlayerBucketEntityEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(event.getPlayer(), CollectItemGoal.class));
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(event.getPlayer(), BreakBlockTypeGoal.class));
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.craftGoalAutoComplete((Player) event.getWhoClicked(), event.getCursor()));
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete((Player) event.getWhoClicked(), CollectItemGoal.class));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete((Player) event.getPlayer(), CollectItemGoal.class));
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(event.getPlayer(), EatGoal.class));
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Entity breeder = event.getBreeder();
        if (event.getBreeder() instanceof Player) {
            Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete((Player) breeder, BreedEntityGoal.class));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(killer, KillEntityGoal.class));
        } else if (event.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK)||
                event.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FALL))
        {
            for (Player player : Bukkit.getOnlinePlayers())
            {
                Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(player, KillEntityGoal.class));
            }
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Player player = event.getEnchanter();
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(player, EnchantItemGoal.class));
    }

    @EventHandler
    public void onExperienceChange(PlayerExpChangeEvent event) {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(player, ExperienceGoal.class));
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(player, PotionEffectGoal.class));
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(player, FishingGoal.class));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(player, TravelGoal.class));

        if (player.getVelocity().getY() < 0)
        {
            Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(player, FallGoal.class));
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event)
    {
        if (!SettingsManager.isPvpEnabled || SettingsManager.pvp == SettingsManager.PvP.NOPVP)
        {
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerHitProj(ProjectileHitEvent event)
    {
        if (!SettingsManager.isPvpEnabled || SettingsManager.pvp == SettingsManager.PvP.NOPVP)
        {
            if (event.getHitEntity() instanceof Player && event.getEntity().getShooter() instanceof Player && event.getHitEntity() != event.getEntity().getShooter())
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void InteractWithCompass(PlayerInteractEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.STARTED && SettingsManager.pvp == SettingsManager.PvP.TRACKING_PVP)
        {
            if (event.hasItem())
            {
                if (event.getItem().getType() == Material.COMPASS)
                {
                    CompassMeta meta = (CompassMeta) event.getItem().getItemMeta();
                    meta.setLodestone(BingoUtil.getNearestPlayerNotOnTeam(event.getPlayer()));
                    meta.setLodestoneTracked(false);
                    event.getItem().setItemMeta(meta);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(event.getPlayer(), CompleteAdvancementGoal.class));
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(event.getPlayer(), CompleteNumberAdvancementGoal.class));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(event.getPlayer(), BlockInteractGoal.class));
    }

    @EventHandler
    public void onPlayerInteract(PlayerArmorStandManipulateEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(event.getPlayer(), ArmorStandInteractGoal.class));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(event.getEntity(), DeathGoal.class));
    }

    @EventHandler
    public void FillCauldronEvent(CauldronLevelChangeEvent event)
    {
        if (GameManager.gameState == GameManager.GameState.FINISHED) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Bukkit.getScheduler().runTask(GameManager.plugin, () -> GameManager.instance.boardManager.goalAutoComplete(player, CauldronInteractGoal.class));
        }
    }
}
