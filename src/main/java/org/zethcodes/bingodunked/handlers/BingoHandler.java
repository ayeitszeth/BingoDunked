package org.zethcodes.bingodunked.handlers;


import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.zethcodes.bingodunked.goals.*;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.WorldUtil;

public class BingoHandler implements Listener {

    JavaPlugin plugin;
    BingoUtil bingoUtil;

    public BingoHandler(JavaPlugin plugin, BingoUtil bingoUtil) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (bingoUtil.DEBUG) Bukkit.getLogger().info(plugin + "");
        this.plugin = plugin;
        this.bingoUtil = bingoUtil;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        bingoUtil.updatePlayerTabListName(event.getPlayer());
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

        if (event.getPlayer().getWorld().equals(WorldUtil.lobbyWorld) || bingoUtil.gameState == BingoUtil.GameState.FINISHED)
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
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        if (event.getEntity() instanceof Player)
        {
            Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete((Player) event.getEntity(), CollectItemGoal.class));
        }
    }

    @EventHandler
    public void onBucketItem(PlayerBucketFillEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(event.getPlayer(), CollectItemGoal.class));
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(event.getPlayer(), BreakBlockTypeGoal.class));
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.craftGoalAutoComplete((Player) event.getWhoClicked(), event.getCursor()));
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete((Player) event.getWhoClicked(), CollectItemGoal.class));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete((Player) event.getPlayer(), CollectItemGoal.class));
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(event.getPlayer(), EatGoal.class));
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Entity breeder = event.getBreeder();
        if (event.getBreeder() instanceof Player) {
            Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete((Player) breeder, BreedEntityGoal.class));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(killer, KillEntityGoal.class));
        } else if (event.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK)||
                event.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FALL))
        {
            for (Player player : Bukkit.getOnlinePlayers())
            {
                Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(player, KillEntityGoal.class));
            }
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Player player = event.getEnchanter();
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(player, EnchantItemGoal.class));
    }

    @EventHandler
    public void onExperienceChange(PlayerExpChangeEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(player, ExperienceGoal.class));
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(player, PotionEffectGoal.class));
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(player, FishingGoal.class));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(player, TravelGoal.class));

        if (player.getVelocity().getY() < 0)
        {
            Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(player, FallGoal.class));
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event)
    {
        if (!bingoUtil.isPvpEnabled)
        {
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(event.getPlayer(), CompleteAdvancementGoal.class));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(event.getPlayer(), BlockInteractGoal.class));
    }

    @EventHandler
    public void onPlayerInteract(PlayerArmorStandManipulateEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(event.getPlayer(), ArmorStandInteractGoal.class));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Bukkit.getScheduler().runTask(plugin, () -> bingoUtil.goalAutoComplete(event.getEntity(), DeathGoal.class));
    }
}
