package org.zethcodes.bingodunked.util;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Biome;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.*;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.checkerframework.checker.units.qual.K;
import org.zethcodes.bingodunked.BingoDunked;
import org.zethcodes.bingodunked.goals.*;
import org.zethcodes.bingodunked.listeners.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;

public class BingoUtil {

    Inventory BingoCard;
    Inventory settingsGUI;
    List<Integer> validSlots = new ArrayList<>();
    ItemStack redWool = new ItemStack(Material.RED_WOOL, 1);
    ItemStack blueWool = new ItemStack(Material.BLUE_WOOL, 1);
    ItemStack greenWool = new ItemStack(Material.LIME_WOOL, 1);
    ItemStack yellowWool = new ItemStack(Material.YELLOW_WOOL, 1);
    ItemStack orangeWool = new ItemStack(Material.ORANGE_WOOL, 1);
    ItemStack purpleWool = new ItemStack(Material.PURPLE_WOOL, 1);
    ItemStack cyanWool = new ItemStack(Material.CYAN_WOOL, 1);
    ItemStack brownWool = new ItemStack(Material.BROWN_WOOL, 1);
    // TEAMS
    boolean[][] redBoard = new boolean[3][3];
    boolean[][] blueBoard = new boolean[3][3];
    boolean[][] greenBoard = new boolean[3][3];
    boolean[][] yellowBoard = new boolean[3][3];
    // Additional for FFA
    boolean[][] orangeBoard = new boolean[3][3];
    boolean[][] purpleBoard = new boolean[3][3];
    boolean[][] cyanBoard = new boolean[3][3];
    boolean[][] brownBoard = new boolean[3][3];
    HashMap<Integer, Goal> goals = new HashMap<>();
    List<Goal> allGoals = new ArrayList<>();
    List<Goal> lateGameGoals = new ArrayList<>();
    HashMap<Biome, Goal> biomeGoals = new HashMap<Biome, Goal>();
    KillEntityListener killEntityListener;
    BreedEntityListener breedEntityListener;
    PotionEffectListener potionEffectListener;
    EnchantListener enchantListener;
    FishingListener fishingListener;
    BreakBlockTypeListener blockTypeListener;
    FallHeightListener fallHeightListener;
    ExperienceListener experienceListener;
    EatListener eatListener;
    BlockInteractListener blockInteractListener;
    ArmorStandInteractListener armorStandInteractListener;
    TravelListener travelListener;
    DeathListener deathListener;
    public boolean isPvpEnabled;
    Goal testGoal = null;
    int numOfGoalsCompleted = 0;
    private final Map<UUID, Integer> playerGoalsCompleted = new HashMap<>();
    private Map<UUID, Integer> playerTotalGoalsCompleted = new HashMap<>();
    Map<UUID, Team> TeamMap = new HashMap<>();
    List<Biome> startBiomes;
    private int timeLeft;
    public static GameState gameState = GameState.FINISHED;
    boolean overTime = false;
    int goalsToWinOT = -1;
    int animateCounter = 0;

    public enum Team {RED, BLUE, GREEN, YELLOW, ORANGE, PURPLE, CYAN, BROWN, NONE};
    public enum Mode { TEAM, FFA }
    public enum Difficulty { NORMAL, INSANE }
    public enum PvP { NOPVP, PVP, GLOWING_PVP, TRACKING_PVP }
    public enum GameState { LOADING, STARTED, FINISHED }
    public Mode gameMode = Mode.TEAM;
    public Difficulty difficulty = Difficulty.NORMAL;
    public PvP pvp = PvP.PVP;
    private final List<Integer> taskIds = new ArrayList<>();
    BingoDunked plugin;
    public static boolean DEBUG = true;

    boolean activeFallGoal = false;
    boolean activeExpGoal = false;
    boolean activeEffectGoal = false;
    boolean activeFishGoal = false;
    boolean activeEncGoal = false;
    boolean activeColouredGoal = false;
    boolean activeAdvancementGoal = false;
    boolean activeTravelGoal = false;

    ItemStack enabled = new ItemStack(Material.LIME_WOOL, 1);
    ItemStack disabled = new ItemStack(Material.RED_WOOL, 1);

    public TravelListener.TYPE activeTravelType = null;
    public Set<BreakBlockTypeListener.BlockType> activeBlockTypes = new HashSet<>();

    public BingoUtil(BingoDunked plugin, KillEntityListener killEntityListener, BreedEntityListener breedEntityListener, PotionEffectListener potionEffectListener,
                     EnchantListener enchantListener, FishingListener fishingListener, FallHeightListener fallHeightListener,
                     ExperienceListener experienceListener, EatListener eatListener, BlockInteractListener blockInteractListener,
                     ArmorStandInteractListener armorStandInteractListener, DeathListener deathListener) {
        this.plugin = plugin;
        this.killEntityListener = killEntityListener;
        this.breedEntityListener = breedEntityListener;
        this.potionEffectListener = potionEffectListener;
        this.enchantListener = enchantListener;
        this.fishingListener = fishingListener;
        this.fallHeightListener = fallHeightListener;
        this.experienceListener = experienceListener;
        this.eatListener = eatListener;
        this.blockInteractListener = blockInteractListener;
        this.armorStandInteractListener = armorStandInteractListener;
        this.deathListener = deathListener;

        this.travelListener = new TravelListener(this);
        this.blockTypeListener = new BreakBlockTypeListener(this);
        getServer().getPluginManager().registerEvents(travelListener, plugin);
        getServer().getPluginManager().registerEvents(blockTypeListener, plugin);

        BingoCard = Bukkit.createInventory(null, 27, "Bingo Card");
        settingsGUI = Bukkit.createInventory(null, 45, "Bingo Settings");

        for (int col = 0; col < 3; ++col) {
            for (int row = 0; row < 3; ++row) {
                validSlots.add(row * 9 + col + 3);
            }
        }

        ItemMeta enabledMeta = enabled.getItemMeta();
        enabledMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Enabled");
        enabled.setItemMeta(enabledMeta);

        ItemMeta disabledMeta = disabled.getItemMeta();
        disabledMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Disabled");
        disabled.setItemMeta(disabledMeta);

        SetCardsInv();
    }

    public void BingoSetUp(Mode mode, int time) {
        cancelAllTasks();
        gameMode = mode;
        gameState = GameState.LOADING;

        if (gameMode == Mode.TEAM) {
            settingsGUI.setItem(12, enabled);
            settingsGUI.setItem(15, disabled);
        } else if (mode == Mode.FFA) {
            settingsGUI.setItem(12, disabled);
            settingsGUI.setItem(15, enabled);
        }

        redBoard = new boolean[3][3];
        blueBoard = new boolean[3][3];
        greenBoard = new boolean[3][3];
        yellowBoard = new boolean[3][3];
        orangeBoard = new boolean[3][3];
        purpleBoard = new boolean[3][3];
        cyanBoard = new boolean[3][3];
        brownBoard = new boolean[3][3];
        SetGoals();
        killEntityListener.Reset();
        breedEntityListener.Reset();
        potionEffectListener.Reset();
        enchantListener.Reset();
        fishingListener.Reset();
        blockTypeListener.Reset();
        fallHeightListener.Reset();
        experienceListener.Reset();
        eatListener.Reset();
        blockInteractListener.Reset();
        travelListener.Reset();
        deathListener.Reset();
        numOfGoalsCompleted = 0;
        timeLeft = time * 60;
        isPvpEnabled = false;
        overTime = false;
        goalsToWinOT = -1;
        animateCounter = 0;
        startBiomes = new ArrayList<>();

        activeFallGoal = false;
        activeExpGoal = false;
        activeEffectGoal = false;
        activeFishGoal = false;
        activeEncGoal = false;
        activeColouredGoal = false;
        activeAdvancementGoal = false;
        activeTravelGoal = false;

        activeTravelType = null;
        activeBlockTypes.clear();

        if (gameMode == Mode.FFA) {
            TeamMap = new HashMap<>();
            Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            int numOfPlayers = players.length;
            if (numOfPlayers <= 8) {
                Team[] teams = Team.values();

                for (int i = 0; i < numOfPlayers; ++i) {
                    TeamMap.put(players[i].getUniqueId(), teams[i]);
                    updatePlayerTabListName(players[i]);
                }

            } else {
                BingoAnnounce("This game mode only is functional with less than 8 players... Start Aborting...");
                return;
            }
        }

        for (Player p : Bukkit.getOnlinePlayers())
        {
            updatePlayerTabListName(p);
        }

        Location spawnLoc;

        try
        {
            spawnLoc = Bukkit.getWorld(WorldUtil.bingoWorldName).getSpawnLocation();
        } catch (NullPointerException e)
        {
            BingoAnnounce("Please generate a world using /newworld before starting Bingo");
            return;
        }

        BingoAnnounce("The board is being set up. Please wait one moment.");

        resetPlayerGoalsCompleted();
        startBiomes = findBiomes(getServer().getWorld(WorldUtil.bingoWorldName).getSpawnLocation(), 250);

        if (pvp != PvP.NOPVP)
        {
            addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 2 minutes remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.89f);
                }
            }, 20L * (1 * 60 + 10)).getTaskId());

            addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 1 minutes remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.78f);
                }
            }, 20L * (2 * 60 + 10)).getTaskId());

            addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 30 seconds remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.68f);
                }
            }, 20L * (2 * 60 + 30 + 10)).getTaskId());

            addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 10 seconds remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.5f);
                }
            }, 20L * (2 * 60 + 50 + 10)).getTaskId());

            addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                isPvpEnabled = true;
                BingoAnnounce("The Grace Period has ended...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0f);
                }
            }, 20L * (3 * 60 + 10)).getTaskId());
        }

        BingoAnnounce("Bingo will start in 10 seconds...");

        Bukkit.getWorld(WorldUtil.bingoWorldName).setTime(0);
        for (Player player : Bukkit.getOnlinePlayers()) {
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

            for (Iterator<Advancement> it = getServer().advancementIterator(); it.hasNext(); ) {
                Advancement advancement = it.next();
                AdvancementProgress progress = player.getAdvancementProgress(advancement);
                for (String criteria : progress.getAwardedCriteria()) {
                        progress.revokeCriteria(criteria);
                }
            }

            if (gameMode == Mode.TEAM && !TeamMap.containsKey(player.getUniqueId())) {
                BingoWhisper(player,"Psst... You haven't joined a team yet...");
            }
        }

        addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 5 seconds...");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.5f);
            }
        }, 20L * 5).getTaskId());

        addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 3!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.68f);
            }
        }, 20L * 7).getTaskId());

        addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 2!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.78f);
            }
        }, 20L * 8).getTaskId());

        addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 1!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.89f);
            }
        }, 20L * 9).getTaskId());

        addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoStart();
            if (gameMode == Mode.FFA)
            {
                startTimer();
            }
        }, 20L * 10).getTaskId());
    }

    public void BingoStart() {
        Random random = new Random();
        goals = new HashMap<>();

        for (int slot : validSlots) {
            Goal goal = allGoals.get(0);
            int ranGoal = 0;
            if (random.nextInt(2) == 0 && !startBiomes.isEmpty() && difficulty == Difficulty.NORMAL) { // 50/50 coin flip if it's a biome goal
                Biome biome = startBiomes.get(0);
                startBiomes.remove(biome);
                if (biomeGoals.containsKey(biome)) {
                    goal = biomeGoals.get(biome);
                    biomeGoals.remove(biome);
                    allGoals.remove(biome);
                } else {
                    ranGoal = random.nextInt(allGoals.size());
                    goal = allGoals.get(ranGoal);
                    allGoals.remove(goal);
                }
            } else {
                ranGoal = random.nextInt(allGoals.size());
                goal = allGoals.get(ranGoal);
                allGoals.remove(goal);
            }

            while (((goal instanceof FallGoal && activeFallGoal) ||
                    (goal instanceof ExperienceGoal && activeExpGoal) ||
                    (goal instanceof PotionEffectGoal && activeEffectGoal) ||
                    (goal instanceof FishingGoal && activeFishGoal) ||
                    (goal instanceof EnchantItemGoal && activeEncGoal) ||
                    (goal instanceof CollectColouredItemGoal && activeColouredGoal) ||
                    (goal instanceof CompleteAdvancementGoal && activeAdvancementGoal) ||
                    (goal instanceof TravelGoal && activeTravelGoal) ||
                    (lateGameGoals.contains(goal)) ||
                    (goals.containsValue(goal))) &&
                    difficulty == Difficulty.NORMAL) {

                if (allGoals.isEmpty()) SetGoals();

                ranGoal = random.nextInt(allGoals.size());
                goal = allGoals.get(ranGoal);

                if (lateGameGoals.contains(goal) && difficulty == Difficulty.NORMAL)
                {
                    continue;
                }

                allGoals.remove(goal);
            }

            if (testGoal != null && slot == 13)
            {
                goal = testGoal;
            }

            if (goal instanceof FallGoal) {
                activeFallGoal = true;
            } else if (goal instanceof ExperienceGoal) {
                activeExpGoal = true;
            } else if (goal instanceof PotionEffectGoal) {
                activeEffectGoal = true;
            } else if (goal instanceof FishingGoal) {
                activeFishGoal = true;
            } else if (goal instanceof EnchantItemGoal) {
                activeEncGoal = true;
            } else if (goal instanceof CollectColouredItemGoal) {
                activeColouredGoal = true;
            } else if (goal instanceof CompleteAdvancementGoal) {
                activeAdvancementGoal = true;
            } else if (goal instanceof TravelGoal)
            {
                activeTravelGoal = true;
                activeTravelType = ((TravelGoal) goal).type;
            } else if (goal instanceof BreakBlockTypeGoal)
            {
                activeBlockTypes.add(((BreakBlockTypeGoal) goal).requiredBlock);
            }

            int col = (slot % 9) - 3;
            int row = slot / 9;

            ItemStack item = goal.getItem();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_PURPLE + goal.getName());
            meta.setLore(lore);
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Goal " + (col + row * 3 + 1));
            item.setItemMeta(meta);

            goals.put(slot, goal);
            BingoCard.setItem(slot, item);
        }

        activeTravelGoal = true;

        for (Player player : Bukkit.getOnlinePlayers()) {
            OpenInv(player);
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 2f);
            if (pvp == PvP.TRACKING_PVP)
            {
                player.getInventory().addItem(new ItemStack(Material.COMPASS,1));
            }
        }

        gameState = GameState.STARTED;

        animateBingoCard();
        BingoAnnounce("");
        BingoAnnounce("Bingo has begun!");
        BingoAnnounce("");

        if (pvp != PvP.NOPVP)
        {
            BingoAnnounce("");
            BingoAnnounce("The Grace Period will end in 3 minutes...");
        }
    }

    public void animateBingoCard() {
        if (gameState == GameState.STARTED) {
            // Run the animation asynchronously
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep((long) (1.25f * 1000));
                        animateCounter++;

                        for (int slot : validSlots) {
                            Goal goal = goals.get(slot);
                            int col = (slot % 9) - 3;
                            int row = slot / 9;
                            List<Material> items = new ArrayList<>();

                            if (goal instanceof CollectItemsAmountGoal) {
                                CollectItemsAmountGoal newGoal = (CollectItemsAmountGoal) goal;
                                items = newGoal.items;
                            } else if (goal instanceof CollectItemSetAmountGoal) {
                                CollectItemSetAmountGoal newGoal = (CollectItemSetAmountGoal) goal;
                                items = newGoal.items;
                            } else if (goal instanceof CollectItemSetGoal) {
                                CollectItemSetGoal newGoal = (CollectItemSetGoal) goal;
                                items = newGoal.items;
                            } else if (goal instanceof CollectItemsGoal) {
                                CollectItemsGoal newGoal = (CollectItemsGoal) goal;
                                items = newGoal.items;
                            } else {
                                continue;
                            }

                            if (isGoalToBeDunked(col, row, Team.NONE) || items.size() <= 1)
                            {
                                continue;
                            }

                            ItemStack item = new ItemStack(items.get(animateCounter % items.size()), goal.getItem().getAmount());
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = new ArrayList<>();
                            lore.add(ChatColor.DARK_PURPLE + goal.getName());
                            meta.setLore(lore);
                            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Goal " + (col + row * 3 + 1));
                            item.setItemMeta(meta);

                            // Schedule the UI update back to the main thread
                            int finalSlot = slot;
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                BingoCard.setItem(finalSlot, item);
                            });
                        }

                        // Repeat the animation
                        animateBingoCard();

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    public List<Biome> findBiomes(Location location, int radius)
    {
        Set<Biome> uniqueBiomes = new HashSet<>();
        int centreX = location.getBlockX();
        int centreZ = location.getBlockZ();
        int step = 50;
        World world = location.getWorld();

        for (int x = centreX - radius; x <= centreX + radius; x += step)
        {
            for (int z = centreZ - radius; z <= centreZ + radius; z += step)
            {
                Biome biomeA = world.getBiome(x,64,z);
                uniqueBiomes.add(biomeA);
                Biome biomeB = world.getBiome(x,-32,z);
                uniqueBiomes.add(biomeB);
            }
        }
        List<Biome> biomes = new ArrayList<>(uniqueBiomes);
        Collections.shuffle(biomes);
        return biomes;
    }

    public void FFALateJoin(Player player)
    {
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        int numOfPlayers = players.length;
        if (numOfPlayers <= 8)
        {
            Team[] teams = Team.values();

            for (int i = 0; i < numOfPlayers; ++i)
            {
                TeamMap.put(players[i].getUniqueId(),teams[i]);
                updatePlayerTabListName(players[i]);
            }

        } else
        {
            player.setGameMode(GameMode.SPECTATOR);
            BingoWhisper(player,"There are too many players already in the game... You will spectate this round.");

        }
    }

    public void newGoal(int slot)
    {
        if (allGoals.isEmpty()) {
            SetGoals();
        }

        goals.remove(slot);

        int col = (slot % 9) - 3;
        int row = slot / 9;

        Random random = new Random();
        Goal goal = allGoals.get(0);
        int ranGoal = 0;

        if (random.nextInt(4) != 3 && difficulty == Difficulty.NORMAL) { // 75/25 flip if it's a biome goal
            if (!startBiomes.isEmpty())
            {
                Biome biome = startBiomes.get(0);
                startBiomes.remove(biome);
                if (biomeGoals.containsKey(biome)) {
                    goal = biomeGoals.get(biome);
                    biomeGoals.remove(biome);
                    allGoals.remove(biome);
                } else {
                    ranGoal = random.nextInt(allGoals.size());
                    goal = allGoals.get(ranGoal);
                    allGoals.remove(goal);
                }
            } else
            {
                Player player = getPlayerWithLeastGoals();
                Biome biome = player.getLocation().getBlock().getBiome();

                if (biomeGoals.containsKey(biome)) {
                    goal = biomeGoals.get(biome);
                    biomeGoals.remove(biome);
                } else {
                    ranGoal = random.nextInt(allGoals.size());
                    goal = allGoals.get(ranGoal);
                    allGoals.remove(goal);
                }
            }

        } else {
            ranGoal = random.nextInt(allGoals.size());
            goal = allGoals.get(ranGoal);
            allGoals.remove(goal);
        }

        while (((goal instanceof FallGoal && activeFallGoal) ||
                    (goal instanceof ExperienceGoal && activeExpGoal) ||
                    (goal instanceof PotionEffectGoal && activeEffectGoal) ||
                    (goal instanceof FishingGoal && activeFishGoal) ||
                    (goal instanceof EnchantItemGoal && activeEncGoal) ||
                    (goal instanceof CollectColouredItemGoal && activeColouredGoal) ||
                    (goal instanceof CompleteAdvancementGoal && activeAdvancementGoal) ||
                    (goal instanceof TravelGoal && activeTravelGoal) ||
                    (gameMode == Mode.FFA && difficulty == Difficulty.NORMAL && lateGameGoals.contains(goal) && numOfGoalsCompleted < 12) ||
                    (gameMode == Mode.TEAM && difficulty == Difficulty.NORMAL && lateGameGoals.contains(goal) && numOfGoalsCompleted < 12 - Math.min(playerGoalsCompleted.size(),12)) ||
                    (goals.containsValue(goal))) &&
                    difficulty == Difficulty.NORMAL) {
            if (allGoals.isEmpty()) {
                SetGoals();
            }

            ranGoal = random.nextInt(allGoals.size());
            goal = allGoals.get(ranGoal);

            if ((gameMode == Mode.FFA && difficulty == Difficulty.NORMAL && lateGameGoals.contains(goal) && numOfGoalsCompleted < 12) ||
                    (gameMode == Mode.TEAM && difficulty == Difficulty.NORMAL && lateGameGoals.contains(goal) && numOfGoalsCompleted < 12 - Math.min(playerGoalsCompleted.size(),12)))
            {
                continue;
            }
            allGoals.remove(goal);
        }

        // force test goal
        //goal = testGoal;

        if (goal instanceof FallGoal) {
            activeFallGoal = true;
        } else if (goal instanceof ExperienceGoal) {
            activeExpGoal = true;
        } else if (goal instanceof PotionEffectGoal) {
            activeEffectGoal = true;
        } else if (goal instanceof FishingGoal) {
            activeFishGoal = true;
        } else if (goal instanceof EnchantItemGoal) {
            activeEncGoal = true;
        } else if (goal instanceof CollectColouredItemGoal) {
            activeColouredGoal = true;
        } else if (goal instanceof CompleteAdvancementGoal) {
            activeAdvancementGoal = true;
        } else if (goal instanceof TravelGoal)
        {
            activeTravelGoal = true;
            activeTravelType = ((TravelGoal) goal).type;
        } else if (goal instanceof BreakBlockTypeGoal)
        {
            activeBlockTypes.add(((BreakBlockTypeGoal) goal).requiredBlock);
        }

        ItemStack item = goal.getItem();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + goal.getName());
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Goal " + (col + row * 3 + 1));
        item.setItemMeta(meta);

        goals.put(slot,goal);
        BingoCard.setItem(slot, item);
        BingoAnnounce("The new goal is " + ChatColor.BOLD + goal.getName() + "!");

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (goal instanceof CollectItemGoal || goal instanceof ExperienceGoal ||
            goal instanceof CompleteAdvancementGoal || goal instanceof BreakBlockTypeGoal ||
            goal instanceof TravelGoal)
            {
                if (goal.isComplete(player)) {
                    completeGoal(slot, getTeam(player), player);
                }
            }
        }
    }

    public void JoinTeam(Player player, Team team)
    {
        if (gameMode == Mode.FFA)
        {
            BingoWhisper(player,"There is no need to join teams in FFA mode.");
            return;
        }

        String playerName;

        try {
            playerName = player.getName();
        } catch (Exception ex)
        {
            playerName = "<left server>";
        }

        TeamMap.put(player.getUniqueId(),team);
        BingoAnnounce(playerName + ChatColor.WHITE + " has joined the " + getTeamChatColour(team) + ChatColor.BOLD + team.name().charAt(0) + team.name().substring(1).toLowerCase() + " Team" + ChatColor.WHITE + ".");
        updatePlayerTabListName(player);
    }

    public void SetCardsInv()
    {
        ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta blackMeta = black.getItemMeta();
        blackMeta.setDisplayName(ChatColor.BLACK + " ");
        black.setItemMeta(blackMeta);
        for (int i = 0; i < 3; ++i)
        {
            BingoCard.setItem(i * 9, black);
            BingoCard.setItem(i * 9 + 1, black);
            BingoCard.setItem(i * 9 + 2, black);
            BingoCard.setItem(i * 9 + 6, black);
            BingoCard.setItem(i * 9 + 7, black);
            BingoCard.setItem(i * 9 + 8, black);
        }

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

    public void SetGoals()
    {
        allGoals.clear();
        lateGameGoals.clear();
        biomeGoals.clear();

        ItemStack diamondBlock = new ItemStack(Material.DIAMOND_BLOCK, 1);
        CollectItemGoal diamondBlockGoal = new CollectItemGoal("Collect a Diamond Block", diamondBlock);
        allGoals.add(diamondBlockGoal);

        ItemStack decoratedPot = new ItemStack(Material.DECORATED_POT, 1);
        CollectItemGoal decoratedPotGoal = new CollectItemGoal("Craft a Decorated Pot", decoratedPot);
        allGoals.add(decoratedPotGoal);

        ItemStack bucketOfAxolotl = new ItemStack(Material.AXOLOTL_BUCKET, 1);
        CollectItemGoal bucketOfAxolotlGoal = new CollectItemGoal("Collect a Bucket of Axoltol", bucketOfAxolotl);
        biomeGoals.put(Biome.LUSH_CAVES, bucketOfAxolotlGoal);

        ItemStack brush = new ItemStack(Material.BRUSH, 1);
        CollectItemGoal brushGoal = new CollectItemGoal("Collect a Brush", brush);
        allGoals.add(brushGoal);

        ItemStack spyglass = new ItemStack(Material.SPYGLASS, 1);
        CollectItemGoal spyglassGoal = new CollectItemGoal("Collect a Spyglass", spyglass);
        allGoals.add(spyglassGoal);

        ItemStack clock = new ItemStack(Material.CLOCK, 1);
        CollectItemGoal clockGoal = new CollectItemGoal("Collect a Clock", clock);
        allGoals.add(clockGoal);

        ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
        CollectItemGoal crossbowGoal = new CollectItemGoal("Collect a Crossbow", crossbow);
        allGoals.add(crossbowGoal);

        ItemStack milk = new ItemStack(Material.MILK_BUCKET, 1);
        CollectItemGoal milkGoal = new CollectItemGoal("Collect a Bucket of Milk", milk);
        allGoals.add(milkGoal);

        ItemStack heartOfTheSea = new ItemStack(Material.HEART_OF_THE_SEA, 1);
        CollectItemGoal heartOfTheSeaGoal = new CollectItemGoal("Collect a Heart of the Sea", heartOfTheSea);
        allGoals.add(heartOfTheSeaGoal);

        ItemStack bundle = new ItemStack(Material.BUNDLE, 1);
        CollectItemGoal bundleGoal = new CollectItemGoal("Collect a Bundle", bundle);
        allGoals.add(bundleGoal);

        ItemStack magmacream = new ItemStack(Material.MAGMA_CREAM, 1);
        CollectItemGoal magmacreamGoal = new CollectItemGoal("Collect a Magma Cream", magmacream);
        biomeGoals.put(Biome.BASALT_DELTAS, magmacreamGoal);
        lateGameGoals.add(magmacreamGoal);

        ItemStack emeraldBlock = new ItemStack(Material.EMERALD_BLOCK, 1);
        CollectItemGoal emeraldBlockGoal = new CollectItemGoal("Collect an Emerald Block", emeraldBlock);
        allGoals.add(emeraldBlockGoal);

        List<Material> candles = new ArrayList<>();
        candles.add(Material.CANDLE);
        candles.add(Material.WHITE_CANDLE);
        candles.add(Material.ORANGE_CANDLE);
        candles.add(Material.MAGENTA_CANDLE);
        candles.add(Material.LIGHT_BLUE_CANDLE);
        candles.add(Material.YELLOW_CANDLE);
        candles.add(Material.LIME_CANDLE);
        candles.add(Material.PINK_CANDLE);
        candles.add(Material.GRAY_CANDLE);
        candles.add(Material.LIGHT_GRAY_CANDLE);
        candles.add(Material.CYAN_CANDLE);
        candles.add(Material.PURPLE_CANDLE);
        candles.add(Material.BLUE_CANDLE);
        candles.add(Material.BROWN_CANDLE);
        candles.add(Material.GREEN_CANDLE);
        candles.add(Material.RED_CANDLE);
        candles.add(Material.BLACK_CANDLE);
        CollectItemsGoal candleGoal = new CollectItemsGoal("Collect any Candle", candles);
        allGoals.add(candleGoal);

        List<Material> hangingSigns = new ArrayList<>();
        hangingSigns.add(Material.OAK_HANGING_SIGN);
        hangingSigns.add(Material.ACACIA_HANGING_SIGN);
        hangingSigns.add(Material.BAMBOO_HANGING_SIGN);
        hangingSigns.add(Material.BIRCH_HANGING_SIGN);
        hangingSigns.add(Material.CRIMSON_HANGING_SIGN);
        hangingSigns.add(Material.DARK_OAK_HANGING_SIGN);
        hangingSigns.add(Material.JUNGLE_HANGING_SIGN);
        hangingSigns.add(Material.MANGROVE_HANGING_SIGN);
        hangingSigns.add(Material.OAK_HANGING_SIGN);
        hangingSigns.add(Material.SPRUCE_HANGING_SIGN);
        hangingSigns.add(Material.WARPED_HANGING_SIGN);
        hangingSigns.add(Material.PALE_OAK_HANGING_SIGN);
        CollectItemsGoal hangingSignGoal = new CollectItemsGoal("Collect a Hanging Sign of Any Type", hangingSigns);
        allGoals.add(hangingSignGoal);

        ItemStack loom = new ItemStack(Material.LOOM, 1);
        CollectItemGoal loomGoal = new CollectItemGoal("Collect a Loom", loom);
        allGoals.add(loomGoal);

        /* DISABLED - villages too strong
        ItemStack bell = new ItemStack(Material.BELL, 1);
        CollectItemGoal bellGoal = new CollectItemGoal("Collect a Bell", bell);
        allGoals.add(bellGoal);*/

        ItemStack endermanEgg = new ItemStack(Material.ENDERMAN_SPAWN_EGG,1);
        KillEntityGoal endermanKillGoal = new KillEntityGoal("Kill an Enderman", endermanEgg, EntityType.ENDERMAN, killEntityListener);
        allGoals.add(endermanKillGoal);

        ItemStack endermiteEgg = new ItemStack(Material.ENDERMITE_SPAWN_EGG,1);
        KillEntityGoal endermiteKillGoal = new KillEntityGoal("Kill an Endermite", endermiteEgg, EntityType.ENDERMITE, killEntityListener);
        allGoals.add(endermiteKillGoal);
        lateGameGoals.add(endermiteKillGoal);

        ItemStack guardianEgg = new ItemStack(Material.GUARDIAN_SPAWN_EGG,1);
        KillEntityGoal guardianKillGoal = new KillEntityGoal("Kill a Guardian", guardianEgg, EntityType.GUARDIAN,killEntityListener);
        biomeGoals.put(Biome.DEEP_OCEAN,guardianKillGoal);

        ItemStack elderGuardianEgg = new ItemStack(Material.ELDER_GUARDIAN_SPAWN_EGG,1);
        KillEntityGoal elderGuardianKillGoal = new KillEntityGoal("Kill an Elder Guardian", elderGuardianEgg, EntityType.GUARDIAN,killEntityListener);
        biomeGoals.put(Biome.DEEP_LUKEWARM_OCEAN,elderGuardianKillGoal);
        lateGameGoals.add(elderGuardianKillGoal);

        ItemStack zombiePiglinEgg = new ItemStack(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG,1);
        KillEntityGoal zombiePiglinGoal = new KillEntityGoal("Kill a Zombie Piglin", zombiePiglinEgg, EntityType.ZOMBIFIED_PIGLIN, killEntityListener);
        allGoals.add(zombiePiglinGoal);
        lateGameGoals.add(zombiePiglinGoal);

        ItemStack zoglinEgg = new ItemStack(Material.ZOGLIN_SPAWN_EGG, 1);
        KillEntityGoal zoglinGoal = new KillEntityGoal("Kill a Zoglin", zoglinEgg, EntityType.ZOGLIN, killEntityListener);
        allGoals.add(zoglinGoal);
        lateGameGoals.add(zoglinGoal);

        ItemStack witchEgg = new ItemStack(Material.WITCH_SPAWN_EGG,1);
        KillEntityGoal witchKillGoal = new KillEntityGoal("Kill a Witch", witchEgg, EntityType.WITCH, killEntityListener);
        allGoals.add(witchKillGoal);

        ItemStack wheat = new ItemStack(Material.WHEAT,1);
        BreedEntityGoal sheepBreedGoal = new BreedEntityGoal("Breed Two Sheep", wheat, EntityType.SHEEP, breedEntityListener);
        allGoals.add(sheepBreedGoal);

        ItemStack goldenapple = new ItemStack(Material.GOLDEN_APPLE,1);
        BreedEntityGoal donkeyBreedGoal = new BreedEntityGoal("Breed Two Donkeys", goldenapple, EntityType.DONKEY, breedEntityListener);
        biomeGoals.put(Biome.MEADOW, donkeyBreedGoal);

        ItemStack leather = new ItemStack(Material.LEATHER, 1);
        BreedEntityGoal cowBreedGoal = new BreedEntityGoal("Breed Two Cows", leather, EntityType.COW, breedEntityListener);
        allGoals.add(cowBreedGoal);

        ItemStack honeyBlock = new ItemStack(Material.HONEY_BLOCK, 1);
        BreedEntityGoal beeBreedGoal = new BreedEntityGoal("Breed Two Bees", honeyBlock, EntityType.BEE, breedEntityListener);
        allGoals.add(beeBreedGoal);

        ItemStack bone = new ItemStack(Material.BONE,1);
        BreedEntityGoal dogBreedGoal = new BreedEntityGoal("Breed Two Wolves", bone, EntityType.WOLF, breedEntityListener);
        allGoals.add(dogBreedGoal);

        ItemStack seed = new ItemStack(Material.WHEAT_SEEDS,1);
        BreedEntityGoal chickenBreedGoal = new BreedEntityGoal("Breed Two Chickens", seed, EntityType.CHICKEN, breedEntityListener);
        allGoals.add(chickenBreedGoal);

        ItemStack warpedFungus = new ItemStack(Material.WARPED_FUNGUS,1);
        BreedEntityGoal striderBreedGoal = new BreedEntityGoal("Breed Two Striders", warpedFungus, EntityType.STRIDER, breedEntityListener);
        allGoals.add(striderBreedGoal);
        lateGameGoals.add(striderBreedGoal);

        ItemStack fireResPotion = new ItemStack(Material.POTION,1);
        PotionMeta fireResPotionMeta = (PotionMeta) fireResPotion.getItemMeta();
        fireResPotionMeta.setBasePotionType(PotionType.FIRE_RESISTANCE);
        fireResPotion.setItemMeta(fireResPotionMeta);
        PotionEffectGoal fireResGoal = new PotionEffectGoal("Get the Fire Resistance Effect", fireResPotion, PotionEffectType.FIRE_RESISTANCE, potionEffectListener);
        allGoals.add(fireResGoal);
        lateGameGoals.add(fireResGoal);

        ItemStack blindPotion = new ItemStack(Material.POTION,1);
        PotionMeta blindPotionMeta = (PotionMeta) blindPotion.getItemMeta();
        blindPotionMeta.setColor(Color.fromRGB(0, 0, 0));
        blindPotion.setItemMeta(blindPotionMeta);
        PotionEffectGoal blindGoal = new PotionEffectGoal("Get the Blindness Effect", blindPotion, PotionEffectType.BLINDNESS, potionEffectListener);
        biomeGoals.put(Biome.PLAINS,blindGoal);
        lateGameGoals.add(blindGoal);

        ItemStack satPotion = new ItemStack(Material.POTION,1);
        PotionMeta satPotionMeta = (PotionMeta) blindPotion.getItemMeta();
        blindPotionMeta.setColor(Color.fromRGB(155, 122, 1));
        blindPotion.setItemMeta(satPotionMeta);
        PotionEffectGoal satGoal = new PotionEffectGoal("Get the Saturation Effect", satPotion, PotionEffectType.SATURATION, potionEffectListener);
        allGoals.add(satGoal);
        lateGameGoals.add(satGoal);

        ItemStack jumpPotion = new ItemStack(Material.POTION,1);
        PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
        jumpPotionMeta.setBasePotionType(PotionType.LEAPING);
        jumpPotion.setItemMeta(jumpPotionMeta);
        PotionEffectGoal jumpBoostGoal = new PotionEffectGoal("Get the Jump Boost Effect", jumpPotion, PotionEffectType.JUMP_BOOST, potionEffectListener);
        biomeGoals.put(Biome.SUNFLOWER_PLAINS,jumpBoostGoal);
        lateGameGoals.add(jumpBoostGoal);

        ItemStack poisonPotion = new ItemStack(Material.POTION,1);
        PotionMeta poisonPotionMeta = (PotionMeta) poisonPotion.getItemMeta();
        poisonPotionMeta.setBasePotionType(PotionType.POISON);
        poisonPotion.setItemMeta(poisonPotionMeta);
        PotionEffectGoal poisonGoal = new PotionEffectGoal("Get the Poison Effect", poisonPotion, PotionEffectType.POISON, potionEffectListener);
        allGoals.add(poisonGoal);

        ItemStack regenPotion = new ItemStack(Material.POTION,1);
        PotionMeta regenPotionMeta = (PotionMeta) regenPotion.getItemMeta();
        regenPotionMeta.setBasePotionType(PotionType.REGENERATION);
        regenPotion.setItemMeta(regenPotionMeta);
        PotionEffectGoal regenGoal = new PotionEffectGoal("Get the Regeneration Effect", regenPotion, PotionEffectType.REGENERATION, potionEffectListener);
        allGoals.add(regenGoal);

        ItemStack nightVisionPotion = new ItemStack(Material.POTION,1);
        PotionMeta nightVisionPotionItemMeta = (PotionMeta) nightVisionPotion.getItemMeta();
        nightVisionPotionItemMeta.setBasePotionType(PotionType.NIGHT_VISION);
        nightVisionPotion.setItemMeta(nightVisionPotionItemMeta);
        PotionEffectGoal nightVisionGoal = new PotionEffectGoal("Get the Night Vision Effect", nightVisionPotion, PotionEffectType.NIGHT_VISION, potionEffectListener);
        allGoals.add(nightVisionGoal);
        lateGameGoals.add(nightVisionGoal);

        ItemStack weaknessPotion = new ItemStack(Material.POTION,1);
        PotionMeta weaknessPotionItemMeta = (PotionMeta) weaknessPotion.getItemMeta();
        weaknessPotionItemMeta.setBasePotionType(PotionType.WEAKNESS);
        weaknessPotion.setItemMeta(weaknessPotionItemMeta);
        PotionEffectGoal weaknessGoal = new PotionEffectGoal("Get the Weakness Effect", weaknessPotion, PotionEffectType.WEAKNESS, potionEffectListener);
        biomeGoals.put(Biome.FLOWER_FOREST,weaknessGoal);
        lateGameGoals.add(weaknessGoal);

        ItemStack infestationPotion = new ItemStack(Material.POTION, 1);
        PotionMeta infestationPotionItemMeta = (PotionMeta) infestationPotion.getItemMeta();
        infestationPotionItemMeta.setBasePotionType(PotionType.INFESTED);
        infestationPotion.setItemMeta(infestationPotionItemMeta);
        PotionEffectGoal infestationGoal = new PotionEffectGoal("Get the Infestation Effect", infestationPotion, PotionEffectType.INFESTED, potionEffectListener);
        allGoals.add(infestationGoal);
        lateGameGoals.add(infestationGoal);

        ItemStack swiftnessPotion = new ItemStack(Material.POTION, 1);
        PotionMeta swiftnessPotionItemMeta = (PotionMeta) swiftnessPotion.getItemMeta();
        swiftnessPotionItemMeta.setBasePotionType(PotionType.SWIFTNESS);
        swiftnessPotion.setItemMeta(swiftnessPotionItemMeta);
        PotionEffectGoal swiftnessGoal = new PotionEffectGoal("Get the Speed Effect", swiftnessPotion, PotionEffectType.SPEED, potionEffectListener);
        allGoals.add(swiftnessGoal);
        lateGameGoals.add(swiftnessGoal);

        ItemStack strengthPotion = new ItemStack(Material.POTION, 1);
        PotionMeta strengthPotionItemMeta = (PotionMeta) strengthPotion.getItemMeta();
        strengthPotionItemMeta.setBasePotionType(PotionType.STRENGTH);
        strengthPotion.setItemMeta(strengthPotionItemMeta);
        PotionEffectGoal strengthGoal = new PotionEffectGoal("Get the Strength Effect", strengthPotion, PotionEffectType.STRENGTH, potionEffectListener);
        allGoals.add(strengthGoal);
        lateGameGoals.add(strengthGoal);

        ItemStack sharpnessBook = new ItemStack(Material.ENCHANTED_BOOK,1);
        EnchantmentStorageMeta sharpnessMeta = (EnchantmentStorageMeta) sharpnessBook.getItemMeta();
        sharpnessMeta.addStoredEnchant(Enchantment.SHARPNESS,2, false);
        EnchantItemGoal sharpnessGoal = new EnchantItemGoal("Enchant an Item with at least Sharpness II", sharpnessBook, Enchantment.SHARPNESS,2, enchantListener);
        allGoals.add(sharpnessGoal);
        lateGameGoals.add(sharpnessGoal);

        ItemStack protectionBook = new ItemStack(Material.ENCHANTED_BOOK,1);
        EnchantmentStorageMeta protectionMeta = (EnchantmentStorageMeta) protectionBook.getItemMeta();
        protectionMeta.addStoredEnchant(Enchantment.PROTECTION,2, false);
        EnchantItemGoal protectionGoal = new EnchantItemGoal("Enchant an Item with at least Protection II", protectionBook, Enchantment.PROTECTION,2, enchantListener);
        allGoals.add(protectionGoal);
        lateGameGoals.add(protectionGoal);

        ItemStack efficiencyBook = new ItemStack(Material.ENCHANTED_BOOK,1);
        EnchantmentStorageMeta efficiencyMeta = (EnchantmentStorageMeta) efficiencyBook.getItemMeta();
        efficiencyMeta.addStoredEnchant(Enchantment.EFFICIENCY,2, false);
        EnchantItemGoal efficiencyGoal = new EnchantItemGoal("Enchant an Item with at least Efficiency II", efficiencyBook, Enchantment.EFFICIENCY,2, enchantListener);
        allGoals.add(efficiencyGoal);
        lateGameGoals.add(efficiencyGoal);

        ItemStack powerBook = new ItemStack(Material.ENCHANTED_BOOK,1);
        EnchantmentStorageMeta powerMeta = (EnchantmentStorageMeta) powerBook.getItemMeta();
        powerMeta.addStoredEnchant(Enchantment.POWER,2, false);
        EnchantItemGoal powerGoal = new EnchantItemGoal("Enchant an Item with at least Power II", powerBook, Enchantment.POWER,2, enchantListener);
        allGoals.add(powerGoal);
        lateGameGoals.add(powerGoal);

        ItemStack piercingBook = new ItemStack(Material.ENCHANTED_BOOK,1);
        EnchantmentStorageMeta piercingMeta = (EnchantmentStorageMeta) piercingBook.getItemMeta();
        piercingMeta.addStoredEnchant(Enchantment.PIERCING,2, false);
        EnchantItemGoal piercingGoal = new EnchantItemGoal("Enchant an Item with at least Piercing II", piercingBook, Enchantment.PIERCING,2, enchantListener);
        allGoals.add(piercingGoal);
        lateGameGoals.add(piercingGoal);

        ItemStack unbreakingBook = new ItemStack(Material.ENCHANTED_BOOK, 1);
        EnchantmentStorageMeta unbreakingMeta = (EnchantmentStorageMeta) unbreakingBook.getItemMeta();
        unbreakingMeta.addStoredEnchant(Enchantment.UNBREAKING, 2, false);
        EnchantItemGoal unbreakingGoal = new EnchantItemGoal("Enchant an Item with at least Unbreaking II", unbreakingBook, Enchantment.UNBREAKING, 2, enchantListener);
        allGoals.add(unbreakingGoal);
        lateGameGoals.add(unbreakingGoal);

        ItemStack nautShell = new ItemStack(Material.NAUTILUS_SHELL,1);
        FishingGoal nautShellGoal = new FishingGoal("Fish up some Treasure", nautShell, Material.NAUTILUS_SHELL, fishingListener);
        allGoals.add(nautShellGoal);

        ItemStack lilyPad = new ItemStack(Material.LILY_PAD,1);
        FishingGoal leatherBootsGoal = new FishingGoal("Fish up some Junk", lilyPad, Material.LEATHER_BOOTS, fishingListener);
        allGoals.add(leatherBootsGoal);

        ItemStack pufferFish = new ItemStack(Material.PUFFERFISH,1);
        FishingGoal pufferFishGoal = new FishingGoal("Fish up a Pufferfish", pufferFish, Material.PUFFERFISH, fishingListener);
        allGoals.add(pufferFishGoal);

        ItemStack stone = new ItemStack(Material.STONE, 1);
        BreakBlockTypeGoal stoneBreakGoal = new BreakBlockTypeGoal("Break 200 Blocks of Stone", stone, BreakBlockTypeListener.BlockType.STONE, 200, blockTypeListener);
        allGoals.add(stoneBreakGoal);

        ItemStack oakLog = new ItemStack(Material.OAK_LOG, 1);
        BreakBlockTypeGoal logBreakGoal = new BreakBlockTypeGoal("Break 200 Logs", oakLog, BreakBlockTypeListener.BlockType.LOG, 200, blockTypeListener);
        allGoals.add(logBreakGoal);

        ItemStack diamondOre = new ItemStack(Material.DIAMOND_ORE, 1);
        BreakBlockTypeGoal oreBreakGoal = new BreakBlockTypeGoal("Break 100 Ore Blocks", diamondOre, BreakBlockTypeListener.BlockType.ORE, 100, blockTypeListener);
        allGoals.add(oreBreakGoal);

        ItemStack netherGoldOre = new ItemStack(Material.NETHER_GOLD_ORE, 1);
        BreakBlockTypeGoal netherOreBreakGoal = new BreakBlockTypeGoal("Break 100 Nether Ore Blocks", netherGoldOre, BreakBlockTypeListener.BlockType.NETHERORE, 100, blockTypeListener);
        allGoals.add(netherOreBreakGoal);
        lateGameGoals.add(netherOreBreakGoal);

        ItemStack feather = new ItemStack(Material.FEATHER,1);
        FallGoal fall75Goal = new FallGoal("Fall from 75 Blocks", feather, 75, fallHeightListener);
        allGoals.add(fall75Goal);

        FallGoal fall150Goal = new FallGoal("Fall from 150 Blocks", feather, 150, fallHeightListener);
        allGoals.add(fall150Goal);

        FallGoal fall225Goal = new FallGoal("Fall from 225 Blocks", feather, 225, fallHeightListener);
        allGoals.add(fall225Goal);

        ItemStack xpBottle5 = new ItemStack(Material.EXPERIENCE_BOTTLE, 5);
        ExperienceGoal exp5Goal = new ExperienceGoal("Get to Level 5", xpBottle5, 5, experienceListener);
        allGoals.add(exp5Goal);

        ItemStack xpBottle15 = new ItemStack(Material.EXPERIENCE_BOTTLE, 15);
        ExperienceGoal exp15Goal = new ExperienceGoal("Get to Level 15", xpBottle15, 15, experienceListener);
        allGoals.add(exp15Goal);

        ItemStack xpBottle30 = new ItemStack(Material.EXPERIENCE_BOTTLE, 30);
        ExperienceGoal exp30Goal = new ExperienceGoal("Get to Level 30", xpBottle30, 30, experienceListener);
        allGoals.add(exp30Goal);
        lateGameGoals.add(exp30Goal);

        List<Material> discs = new ArrayList<>();
        discs.add(Material.MUSIC_DISC_5);
        discs.add(Material.MUSIC_DISC_OTHERSIDE);
        discs.add(Material.MUSIC_DISC_11);
        discs.add(Material.MUSIC_DISC_13);
        discs.add(Material.MUSIC_DISC_CAT);
        discs.add(Material.MUSIC_DISC_BLOCKS);
        discs.add(Material.MUSIC_DISC_CHIRP);
        discs.add(Material.MUSIC_DISC_FAR);
        discs.add(Material.MUSIC_DISC_MALL);
        discs.add(Material.MUSIC_DISC_MELLOHI);
        discs.add(Material.MUSIC_DISC_PIGSTEP);
        discs.add(Material.MUSIC_DISC_RELIC);
        discs.add(Material.MUSIC_DISC_STAL);
        discs.add(Material.MUSIC_DISC_STRAD);
        discs.add(Material.MUSIC_DISC_WAIT);
        discs.add(Material.MUSIC_DISC_WARD);
        discs.add(Material.MUSIC_DISC_CREATOR);
        discs.add(Material.MUSIC_DISC_CREATOR_MUSIC_BOX);
        discs.add(Material.MUSIC_DISC_PRECIPICE);
        CollectItemsGoal discGoal = new CollectItemsGoal("Collect any Music Disc",discs);
        allGoals.add(discGoal);

        List<Material> horseArmors = new ArrayList<>();
        horseArmors.add(Material.IRON_HORSE_ARMOR);
        horseArmors.add(Material.LEATHER_HORSE_ARMOR);
        horseArmors.add(Material.GOLDEN_HORSE_ARMOR);
        horseArmors.add(Material.DIAMOND_HORSE_ARMOR);
        CollectItemsGoal horseArmorGoal = new CollectItemsGoal("Collect any Horse Armor", horseArmors);
        allGoals.add(horseArmorGoal);

        List<Material> smithingTemplates = new ArrayList<>();
        smithingTemplates.add(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
        smithingTemplates.add(Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
        CollectItemsGoal smithingTemplateGoal = new CollectItemsGoal("Collect any Smithing Template", smithingTemplates);
        allGoals.add(smithingTemplateGoal);
        lateGameGoals.add(smithingTemplateGoal);

        ItemStack cake = new ItemStack(Material.CAKE,1);
        CollectItemGoal cakeGoal = new CollectItemGoal("Craft a Cake", cake);
        allGoals.add(cakeGoal);
        lateGameGoals.add(cakeGoal);

        ItemStack netheriteScrap = new ItemStack(Material.NETHERITE_SCRAP, 1);
        CollectItemGoal netheriteScrapGoal = new CollectItemGoal("Collect a Netherite Scrap", netheriteScrap);
        allGoals.add(netheriteScrapGoal);
        lateGameGoals.add(netheriteScrapGoal);

        ItemStack cookedRabbit = new ItemStack(Material.COOKED_RABBIT, 1);
        EatGoal cookedRabbitGoal = new EatGoal("Eat some Cooked Rabbit", cookedRabbit, eatListener);
        biomeGoals.put(Biome.BADLANDS, cookedRabbitGoal);

        /* DISABLED - villages too strong
        ItemStack rabbitStew = new ItemStack(Material.RABBIT_STEW, 1);
        EatGoal rabbitStewGoal = new EatGoal("Eat some Rabbit Stew", rabbitStew, eatListener);
        allGoals.add(rabbitStewGoal);*/

        ItemStack cookie = new ItemStack(Material.COOKIE, 1);
        EatGoal cookieGoal = new EatGoal("Eat a Cookie", cookie, eatListener);
        biomeGoals.put(Biome.JUNGLE, cookieGoal);

        ItemStack pumpkinPie = new ItemStack(Material.PUMPKIN_PIE, 1);
        EatGoal pumpkinPieGoal = new EatGoal("Eat a Pumpkin Pie", pumpkinPie, eatListener);
        allGoals.add(pumpkinPieGoal);

        ItemStack driedKelp = new ItemStack(Material.DRIED_KELP, 1);
        EatGoal driedKelpGoal = new EatGoal("Eat some Dried Kelp", driedKelp, eatListener);
        allGoals.add(driedKelpGoal);

        ItemStack comparator = new ItemStack(Material.COMPARATOR, 1);
        CollectItemGoal comparatorGoal = new CollectItemGoal("Craft a Comparator", comparator);
        allGoals.add(comparatorGoal);
        lateGameGoals.add(comparatorGoal);

        ItemStack magentaBanner = new ItemStack(Material.MAGENTA_BANNER, 1);
        CollectColouredItemGoal magentaBannerGoal = new CollectColouredItemGoal("Craft a Magenta Banner", magentaBanner);
        allGoals.add(magentaBannerGoal);

        ItemStack orangeBanner = new ItemStack(Material.ORANGE_BANNER, 1);
        CollectColouredItemGoal orangeBannerGoal = new CollectColouredItemGoal("Craft an Orange Banner", orangeBanner);
        allGoals.add(orangeBannerGoal);

        ItemStack pinkBanner = new ItemStack(Material.PINK_BANNER, 1);
        CollectColouredItemGoal pinkBannerGoal = new CollectColouredItemGoal("Craft an Pink Banner", pinkBanner);
        allGoals.add(pinkBannerGoal);

        ItemStack lightblueBanner = new ItemStack(Material.LIGHT_BLUE_BANNER, 1);
        CollectColouredItemGoal lightblueBannerGoal = new CollectColouredItemGoal("Craft an Light Blue Banner", lightblueBanner);
        allGoals.add(lightblueBannerGoal);

        ItemStack magentaConcrete = new ItemStack(Material.MAGENTA_CONCRETE, 1);
        CollectColouredItemGoal magentaConcreteGoal = new CollectColouredItemGoal("Collect a Block of Magenta Concrete", magentaConcrete);
        allGoals.add(magentaConcreteGoal);

        ItemStack orangeConcrete = new ItemStack(Material.ORANGE_CONCRETE, 1);
        CollectColouredItemGoal orangeConcreteGoal = new CollectColouredItemGoal("Collect a Block of Orange Concrete", orangeConcrete);
        allGoals.add(orangeConcreteGoal);

        ItemStack pinkConcrete = new ItemStack(Material.PINK_CONCRETE, 1);
        CollectColouredItemGoal pinkConcreteGoal = new CollectColouredItemGoal("Collect a Block of Pink Concrete", pinkConcrete);
        allGoals.add(pinkConcreteGoal);

        ItemStack lightBlueConcrete = new ItemStack(Material.LIGHT_BLUE_CONCRETE, 1);
        CollectColouredItemGoal lightBlueConcreteGoal = new CollectColouredItemGoal("Collect a Block of Light Blue Concrete", lightBlueConcrete);
        allGoals.add(lightBlueConcreteGoal);

        ItemStack magentaGlass = new ItemStack(Material.MAGENTA_STAINED_GLASS, 1);
        CollectColouredItemGoal magentaGlassGoal = new CollectColouredItemGoal("Collect a Block of Magenta Stained Glass", magentaGlass);
        allGoals.add(magentaGlassGoal);

        ItemStack orangeGlass = new ItemStack(Material.ORANGE_STAINED_GLASS, 1);
        CollectColouredItemGoal orangeGlassGoal = new CollectColouredItemGoal("Collect a Block of Orange Stained Glass", orangeGlass);
        allGoals.add(orangeGlassGoal);

        ItemStack pinkGlass = new ItemStack(Material.PINK_STAINED_GLASS, 1);
        CollectColouredItemGoal pinkGlassGoal = new CollectColouredItemGoal("Collect a Block of Pink Stained Glass", pinkGlass);
        allGoals.add(pinkGlassGoal);

        ItemStack lightBlueGlass = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS, 1);
        CollectColouredItemGoal lightBlueGlassGoal = new CollectColouredItemGoal("Collect a Block of Light Blue Stained Glass", lightBlueGlass);
        allGoals.add(lightBlueGlassGoal);

        ItemStack soulLantern = new ItemStack(Material.SOUL_LANTERN, 1);
        CollectItemGoal soulLanternGoal = new CollectItemGoal("Craft a Soul Lantern", soulLantern);
        allGoals.add(soulLanternGoal);
        lateGameGoals.add(soulLanternGoal);

        ItemStack crafter = new ItemStack(Material.CRAFTER, 1);
        CollectItemGoal crafterGoal = new CollectItemGoal("Craft a Crafter", crafter);
        allGoals.add(crafterGoal);

        ItemStack copperBulb = new ItemStack(Material.COPPER_BULB, 1);
        CollectItemGoal copperBulbGoal = new CollectItemGoal("Collect a Copper Bulb", copperBulb);
        allGoals.add(copperBulbGoal);
        lateGameGoals.add(copperBulbGoal);

        ItemStack boggedEgg = new ItemStack(Material.BOGGED_SPAWN_EGG,1);
        KillEntityGoal boggedGoal = new KillEntityGoal("Kill a Bogged", boggedEgg, EntityType.BOGGED, killEntityListener);
        biomeGoals.put(Biome.SWAMP,boggedGoal);

        ItemStack goatHorn = new ItemStack(Material.GOAT_HORN, 1);
        CollectItemGoal goatHornGoal = new CollectItemGoal("Collect a Goat Horn", goatHorn);
        biomeGoals.put(Biome.JAGGED_PEAKS,goatHornGoal);

        ItemStack armascute = new ItemStack(Material.ARMADILLO_SCUTE, 1);
        BreedEntityGoal armaBreedGoal = new BreedEntityGoal("Breed Two Armadillos", armascute, EntityType.ARMADILLO, breedEntityListener);
        biomeGoals.put(Biome.SAVANNA, armaBreedGoal);

        ItemStack polarEgg = new ItemStack(Material.POLAR_BEAR_SPAWN_EGG, 1);
        KillEntityGoal polarGoal = new KillEntityGoal("Kill a Polar Bear", polarEgg, EntityType.POLAR_BEAR, killEntityListener);
        biomeGoals.put(Biome.SNOWY_PLAINS, polarGoal);

        ItemStack deadBush = new ItemStack(Material.DEAD_BUSH, 1);
        CollectItemGoal deadBushGoal = new CollectItemGoal("Collect a Dead Bush", deadBush);
        biomeGoals.put(Biome.WOODED_BADLANDS,deadBushGoal);

        ItemStack pinkPetal = new ItemStack(Material.PINK_PETALS, 1);
        CollectItemGoal pinkPetalGoal = new CollectItemGoal("Collect some Pink Petals", pinkPetal);
        biomeGoals.put(Biome.CHERRY_GROVE, pinkPetalGoal);

        ItemStack scaffolding = new ItemStack(Material.SCAFFOLDING,1);
        CollectItemGoal scaffoldingGoal = new CollectItemGoal("Craft some Scaffolding", scaffolding);
        biomeGoals.put(Biome.BAMBOO_JUNGLE,scaffoldingGoal);

        ItemStack lichen = new ItemStack(Material.GLOW_LICHEN, 1);
        CollectItemGoal lichenGoal = new CollectItemGoal("Collect Glow Lichen", lichen);
        allGoals.add(lichenGoal);

        ItemStack greenConcrete = new ItemStack(Material.GREEN_CONCRETE, 1);
        CollectColouredItemGoal greenConcreteGoal = new CollectColouredItemGoal("Collect a Block of Green Concrete", greenConcrete);
        biomeGoals.put(Biome.DESERT,greenConcreteGoal);

        List<Material> potterySherds = new ArrayList<>();
        potterySherds.add(Material.ANGLER_POTTERY_SHERD);
        potterySherds.add(Material.ARCHER_POTTERY_SHERD);
        potterySherds.add(Material.ARMS_UP_POTTERY_SHERD);
        potterySherds.add(Material.BLADE_POTTERY_SHERD);
        potterySherds.add(Material.BREWER_POTTERY_SHERD);
        potterySherds.add(Material.BURN_POTTERY_SHERD);
        potterySherds.add(Material.DANGER_POTTERY_SHERD);
        potterySherds.add(Material.EXPLORER_POTTERY_SHERD);
        potterySherds.add(Material.FRIEND_POTTERY_SHERD);
        potterySherds.add(Material.HEART_POTTERY_SHERD);
        potterySherds.add(Material.HEARTBREAK_POTTERY_SHERD);
        potterySherds.add(Material.HOWL_POTTERY_SHERD);
        potterySherds.add(Material.MINER_POTTERY_SHERD);
        potterySherds.add(Material.MOURNER_POTTERY_SHERD);
        potterySherds.add(Material.PLENTY_POTTERY_SHERD);
        potterySherds.add(Material.PRIZE_POTTERY_SHERD);
        potterySherds.add(Material.SHEAF_POTTERY_SHERD);
        potterySherds.add(Material.SHELTER_POTTERY_SHERD);
        potterySherds.add(Material.SKULL_POTTERY_SHERD);
        potterySherds.add(Material.SNORT_POTTERY_SHERD);
        potterySherds.add(Material.FLOW_POTTERY_SHERD);
        potterySherds.add(Material.GUSTER_POTTERY_SHERD);
        potterySherds.add(Material.SCRAPE_POTTERY_SHERD);
        CollectItemsGoal potterySherdGoal = new CollectItemsGoal("Collect any Pottery Sherd", potterySherds);
        allGoals.add(potterySherdGoal);

        ItemStack anvil = new ItemStack(Material.ANVIL, 1);
        CollectItemGoal anvilGoal = new CollectItemGoal("Collect an Anvil", anvil);
        allGoals.add(anvilGoal);

        ItemStack respawnAnchor = new ItemStack(Material.RESPAWN_ANCHOR,1);
        CollectItemGoal respawnAnchorGoal = new CollectItemGoal("Craft a Respawn Anchor", respawnAnchor);
        allGoals.add(respawnAnchorGoal);
        lateGameGoals.add(respawnAnchorGoal);

        ItemStack seaCucum = new ItemStack(Material.SEA_PICKLE,1);
        CollectItemGoal seaPickleGoal = new CollectItemGoal("Collect a Sea Pickle", seaCucum);
        biomeGoals.put(Biome.WARM_OCEAN, seaPickleGoal);

        List<Material> bannerPatterns = new ArrayList<>();
        bannerPatterns.add(Material.FLOWER_BANNER_PATTERN);
        bannerPatterns.add(Material.CREEPER_BANNER_PATTERN);
        bannerPatterns.add(Material.SKULL_BANNER_PATTERN);
        bannerPatterns.add(Material.MOJANG_BANNER_PATTERN);
        bannerPatterns.add(Material.GLOBE_BANNER_PATTERN);
        bannerPatterns.add(Material.PIGLIN_BANNER_PATTERN);
        bannerPatterns.add(Material.FLOW_BANNER_PATTERN);
        bannerPatterns.add(Material.GUSTER_BANNER_PATTERN);
        bannerPatterns.add(Material.FIELD_MASONED_BANNER_PATTERN);
        bannerPatterns.add(Material.BORDURE_INDENTED_BANNER_PATTERN);
        CollectItemsGoal bannerPatternGoal = new CollectItemsGoal("Collect any Banner Pattern", bannerPatterns);
        allGoals.add(bannerPatternGoal);

        ItemStack fireCharge = new ItemStack(Material.FIRE_CHARGE,1);
        CompleteAdvancementGoal returnToSender = new CompleteAdvancementGoal("Complete the advancement Return to Sender", fireCharge, Bukkit.getAdvancement(new NamespacedKey("minecraft","nether/return_to_sender")));
        allGoals.add(returnToSender);
        lateGameGoals.add(returnToSender);

        ItemStack shield = new ItemStack(Material.SHIELD,1);
        CompleteAdvancementGoal nottodayGoal = new CompleteAdvancementGoal("Complete the advancement Not Today, Thank You", shield, Bukkit.getAdvancement(new NamespacedKey("minecraft", "story/deflect_arrow")));
        allGoals.add(nottodayGoal);

        ItemStack diaBoots = new ItemStack(Material.DIAMOND_BOOTS,1);
        CompleteAdvancementGoal subspaceGoal = new CompleteAdvancementGoal("Complete the advancement Subspace Bubble", diaBoots, Bukkit.getAdvancement(new NamespacedKey("minecraft","nether/fast_travel")));
        allGoals.add(subspaceGoal);
        lateGameGoals.add(subspaceGoal);

        ItemStack eyeOfEnder = new ItemStack(Material.ENDER_EYE,1);
        CompleteAdvancementGoal eyeSpyGoal = new CompleteAdvancementGoal("Complete the advancement Eye Spy", eyeOfEnder, Bukkit.getAdvancement(new NamespacedKey("minecraft","story/follow_ender_eye")));
        allGoals.add(eyeSpyGoal);
        lateGameGoals.add(eyeSpyGoal);

        ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT,1);
        CompleteAdvancementGoal ohshinyGoal = new CompleteAdvancementGoal("Complete the advancement Oh Shiny", goldIngot, Bukkit.getAdvancement(new NamespacedKey("minecraft","nether/distract_piglin")));
        allGoals.add(ohshinyGoal);
        lateGameGoals.add(ohshinyGoal);

        ItemStack netheriteBoots = new ItemStack(Material.NETHERITE_BOOTS,1);
        CompleteAdvancementGoal hotTouristGoal = new CompleteAdvancementGoal("Complete the advancement Hot Tourist Destinations", netheriteBoots, Bukkit.getAdvancement(new NamespacedKey("minecraft","nether/explore_nether")));
        allGoals.add(hotTouristGoal);
        lateGameGoals.add(hotTouristGoal);

        ItemStack sculkSensor = new ItemStack(Material.SCULK_SENSOR,1);
        CompleteAdvancementGoal sneakGoal = new CompleteAdvancementGoal("Complete the advancement Sneak 100", sculkSensor, Bukkit.getAdvancement(new NamespacedKey("minecraft","adventure/avoid_vibration")));
        biomeGoals.put(Biome.DEEP_DARK, sneakGoal);

        ItemStack wolfArmour = new ItemStack(Material.WOLF_ARMOR, 1);
        CollectItemGoal wolfArmourGoal = new CollectItemGoal("Collect some Wolf Armor", wolfArmour);
        biomeGoals.put(Biome.SAVANNA_PLATEAU,wolfArmourGoal);

        ItemStack carvedPumpkin = new ItemStack(Material.CARVED_PUMPKIN, 1);
        CompleteAdvancementGoal hiredHelpGoal = new CompleteAdvancementGoal("Complete the advancement Hired Help", carvedPumpkin, Bukkit.getAdvancement(new NamespacedKey("minecraft","adventure/summon_iron_golem")));
        allGoals.add(hiredHelpGoal);

        ItemStack leatherBoots = new ItemStack(Material.LEATHER_BOOTS, 1);
        CompleteAdvancementGoal lightAsRabbitGoal = new CompleteAdvancementGoal("Complete the advancement Light as a Rabbit", leatherBoots, Bukkit.getAdvancement(new NamespacedKey("minecraft", "adventure/walk_on_powder_snow_with_leather_boots")));
        biomeGoals.put(Biome.SNOWY_SLOPES, lightAsRabbitGoal);

        ItemStack targetblock = new ItemStack(Material.TARGET, 1);
        CompleteAdvancementGoal bullseyeGoal = new CompleteAdvancementGoal("Complete the advancement Bullseye", targetblock, Bukkit.getAdvancement(new NamespacedKey("minecraft", "adventure/bullseye")));
        allGoals.add(bullseyeGoal);

        ItemStack glowInkSac = new ItemStack(Material.GLOW_INK_SAC, 1);
        CompleteAdvancementGoal glowAndBeholdGoal = new CompleteAdvancementGoal("Complete the advancement Glow and Behold!", glowInkSac, Bukkit.getAdvancement(new NamespacedKey("minecraft", "husbandry/make_a_sign_glow")));
        allGoals.add(glowAndBeholdGoal);

        ItemStack bucSalmon = new ItemStack(Material.SALMON_BUCKET, 1);
        CompleteAdvancementGoal tacFishGoal = new CompleteAdvancementGoal("Complete the advancement Tactical Fishing!", bucSalmon, Bukkit.getAdvancement(new NamespacedKey("minecraft", "husbandry/tactical_fishing")));
        biomeGoals.put(Biome.COLD_OCEAN,tacFishGoal);

        ItemStack squidEgg = new ItemStack(Material.SQUID_SPAWN_EGG,1);
        KillEntityGoal killSquidGoal = new KillEntityGoal("Kill a Squid",squidEgg,EntityType.SQUID,killEntityListener);
        biomeGoals.put(Biome.DEEP_COLD_OCEAN,killSquidGoal);

        ItemStack sealantern = new ItemStack(Material.SEA_LANTERN,1);
        CollectItemGoal seaLanternGoal = new CollectItemGoal("Collect a Sea Lantern",sealantern);
        biomeGoals.put(Biome.DEEP_FROZEN_OCEAN,seaLanternGoal);

        ItemStack armorStandItem = new ItemStack(Material.ARMOR_STAND, 1);
        ArmorStandInteractGoal fillArmorStandGoal = new ArmorStandInteractGoal("Fill all slots of an Armor Stand", armorStandItem, EntityType.ARMOR_STAND, armorStandInteractListener);
        allGoals.add(fillArmorStandGoal);

        ItemStack composterItem = new ItemStack(Material.COMPOSTER, 1);
        BlockInteractGoal fillComposterGoal = new BlockInteractGoal("Completely fill a Composter", composterItem, Material.COMPOSTER, blockInteractListener);
        allGoals.add(fillComposterGoal);

        ItemStack chiseledBookshelf = new ItemStack(Material.CHISELED_BOOKSHELF, 1);
        BlockInteractGoal fillchiseledBookshelfGoal = new BlockInteractGoal("Completely fill a Chiseled Bookshelf", chiseledBookshelf, Material.CHISELED_BOOKSHELF, blockInteractListener);
        allGoals.add(fillchiseledBookshelfGoal);

        /* DISABLED - villages too storng
        ItemStack rawCod = new ItemStack(Material.COD,1);
        BreedEntityGoal catBreedGoal = new BreedEntityGoal("Breed two Cats", rawCod, EntityType.CAT, breedEntityListener);
        allGoals.add(catBreedGoal);*/

        ItemStack tnt = new ItemStack(Material.TNT,1);
        CollectItemGoal tntGoal = new CollectItemGoal("Craft a Block of TNT", tnt);
        allGoals.add(tntGoal);

        ItemStack lightningRod = new ItemStack(Material.LIGHTNING_ROD,1);
        CollectItemGoal lightningRodGoal = new CollectItemGoal("Craft a Lightning Rod", lightningRod);
        allGoals.add(lightningRodGoal);

        List<Material> flowers = new ArrayList<>();
        flowers.add(Material.DANDELION);
        flowers.add(Material.POPPY);
        flowers.add(Material.BLUE_ORCHID);
        flowers.add(Material.ALLIUM);
        flowers.add(Material.AZURE_BLUET);
        flowers.add(Material.RED_TULIP);
        flowers.add(Material.ORANGE_TULIP);
        flowers.add(Material.WHITE_TULIP);
        flowers.add(Material.PINK_TULIP);
        flowers.add(Material.OXEYE_DAISY);
        flowers.add(Material.CORNFLOWER);
        flowers.add(Material.LILY_OF_THE_VALLEY);
        flowers.add(Material.WITHER_ROSE);
        flowers.add(Material.TORCHFLOWER);
        flowers.add(Material.SUNFLOWER);
        flowers.add(Material.LILAC);
        flowers.add(Material.ROSE_BUSH);
        flowers.add(Material.PEONY);
        flowers.add(Material.CLOSED_EYEBLOSSOM);
        flowers.add(Material.OPEN_EYEBLOSSOM);
        flowers.add(Material.WILDFLOWERS);
        CollectItemsAmountGoal flowersGoal = new CollectItemsAmountGoal("Collect 64 Flowers of Any Type", flowers,64);
        allGoals.add(flowersGoal);

        List<Material> leaves = new ArrayList<>();
        leaves.add(Material.OAK_LEAVES);
        leaves.add(Material.SPRUCE_LEAVES);
        leaves.add(Material.BIRCH_LEAVES);
        leaves.add(Material.JUNGLE_LEAVES);
        leaves.add(Material.ACACIA_LEAVES);
        leaves.add(Material.DARK_OAK_LEAVES);
        leaves.add(Material.MANGROVE_LEAVES);
        leaves.add(Material.CHERRY_LEAVES);
        leaves.add(Material.AZALEA_LEAVES);
        leaves.add(Material.FLOWERING_AZALEA_LEAVES);
        CollectItemsAmountGoal leavesGoal = new CollectItemsAmountGoal("Collect 64 Leaves of Any Type", leaves, 64);
        allGoals.add(leavesGoal);

        List<Material> mobDrops = new ArrayList<>();
        mobDrops.add(Material.ROTTEN_FLESH);
        mobDrops.add(Material.BONE);
        mobDrops.add(Material.STRING);
        mobDrops.add(Material.SPIDER_EYE);
        mobDrops.add(Material.GUNPOWDER);
        mobDrops.add(Material.ENDER_PEARL);
        mobDrops.add(Material.BLAZE_ROD);
        mobDrops.add(Material.GHAST_TEAR);
        mobDrops.add(Material.SLIME_BALL);
        mobDrops.add(Material.MAGMA_CREAM);
        mobDrops.add(Material.PHANTOM_MEMBRANE);
        mobDrops.add(Material.SHULKER_SHELL);
        mobDrops.add(Material.PRISMARINE_SHARD);
        mobDrops.add(Material.WITHER_SKELETON_SKULL);
        CollectItemsAmountGoal mobDropsGoal = new CollectItemsAmountGoal("Collect 32 Hostile Mob Drops of Any Type", mobDrops,32);
        allGoals.add(mobDropsGoal);

        List<Material> rawBlocks = new ArrayList<>();
        rawBlocks.add(Material.RAW_IRON_BLOCK);
        rawBlocks.add(Material.RAW_COPPER_BLOCK);
        rawBlocks.add(Material.RAW_GOLD_BLOCK);
        CollectItemsAmountGoal rawBlocksGoal = new CollectItemsAmountGoal("Collect 16 Blocks of Any Raw Ore", rawBlocks, 16);
        allGoals.add(rawBlocksGoal);

        ItemStack bread = new ItemStack(Material.BREAD, 32);
        List<Material> foodItems = new ArrayList<>();
        foodItems.add(Material.APPLE);
        foodItems.add(Material.GOLDEN_APPLE);
        foodItems.add(Material.ENCHANTED_GOLDEN_APPLE);
        foodItems.add(Material.MELON_SLICE);
        foodItems.add(Material.GLISTERING_MELON_SLICE);
        foodItems.add(Material.CARROT);
        foodItems.add(Material.GOLDEN_CARROT);
        foodItems.add(Material.POTATO);
        foodItems.add(Material.BAKED_POTATO);
        foodItems.add(Material.POISONOUS_POTATO);
        foodItems.add(Material.BEETROOT);
        foodItems.add(Material.BEETROOT_SOUP);
        foodItems.add(Material.BREAD);
        foodItems.add(Material.PUMPKIN_PIE);
        foodItems.add(Material.COOKIE);
        foodItems.add(Material.CAKE);
        foodItems.add(Material.MUSHROOM_STEW);
        foodItems.add(Material.RABBIT_STEW);
        foodItems.add(Material.SUSPICIOUS_STEW);
        foodItems.add(Material.DRIED_KELP);
        foodItems.add(Material.COD);
        foodItems.add(Material.SALMON);
        foodItems.add(Material.TROPICAL_FISH);
        foodItems.add(Material.PUFFERFISH);
        foodItems.add(Material.COOKED_COD);
        foodItems.add(Material.COOKED_SALMON);
        foodItems.add(Material.ROTTEN_FLESH);
        foodItems.add(Material.SPIDER_EYE);
        foodItems.add(Material.COOKED_BEEF);
        foodItems.add(Material.COOKED_CHICKEN);
        foodItems.add(Material.COOKED_MUTTON);
        foodItems.add(Material.COOKED_PORKCHOP);
        foodItems.add(Material.COOKED_RABBIT);
        foodItems.add(Material.RABBIT);
        foodItems.add(Material.CHICKEN);
        foodItems.add(Material.MUTTON);
        foodItems.add(Material.PORKCHOP);
        foodItems.add(Material.BEEF);
        foodItems.add(Material.DRIED_KELP);
        foodItems.add(Material.HONEY_BOTTLE);
        foodItems.add(Material.SWEET_BERRIES);
        foodItems.add(Material.GLOW_BERRIES);
        foodItems.add(Material.SUSPICIOUS_STEW);
        foodItems.add(Material.CHORUS_FRUIT);
        CollectItemsAmountGoal foodGoal = new CollectItemsAmountGoal("Collect 32 Food of Any Type", foodItems, 32);
        allGoals.add(foodGoal);

        ItemStack fireworkRocket = new ItemStack(Material.FIREWORK_ROCKET, 16);
        CollectItemsAmountGoal fireworksGoal = new CollectItemsAmountGoal("Collect 16 Fireworks", fireworkRocket);
        allGoals.add(fireworksGoal);

        ItemStack spectralArrows = new ItemStack(Material.SPECTRAL_ARROW, 32);
        CollectItemsAmountGoal spectralArrowsGoal = new CollectItemsAmountGoal("Collect 32 Spectral Arrows", spectralArrows);
        allGoals.add(spectralArrowsGoal);
        lateGameGoals.add(spectralArrowsGoal);

        ItemStack enderPearls = new ItemStack(Material.ENDER_PEARL, 16);
        CollectItemsAmountGoal enderPearlsGoal = new CollectItemsAmountGoal("Collect 16 Ender Pearls", enderPearls);
        allGoals.add(enderPearlsGoal);
        lateGameGoals.add(enderPearlsGoal);

        List<Material> netherHyphaeMaterials = new ArrayList<>();
        netherHyphaeMaterials.add(Material.CRIMSON_HYPHAE);
        netherHyphaeMaterials.add(Material.WARPED_HYPHAE);
        CollectItemsAmountGoal netherHyphaeGoal = new CollectItemsAmountGoal("Collect 32 Nether Hyphae", netherHyphaeMaterials, 32);
        allGoals.add(netherHyphaeGoal);
        lateGameGoals.add(netherHyphaeGoal);

        List<Material> strippedLogsMaterials = new ArrayList<>();
        strippedLogsMaterials.add(Material.STRIPPED_OAK_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_SPRUCE_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_BIRCH_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_JUNGLE_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_ACACIA_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_DARK_OAK_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_MANGROVE_LOG);
        strippedLogsMaterials.add(Material.STRIPPED_CHERRY_LOG);
        CollectItemsAmountGoal strippedLogsGoal = new CollectItemsAmountGoal("Collect 64 Stripped Logs of Any Variant", strippedLogsMaterials, 64);
        allGoals.add(strippedLogsGoal);

        ItemStack chiseledStoneBricks = new ItemStack(Material.CHISELED_STONE_BRICKS, 16);
        CollectItemsAmountGoal stoneBricksGoal = new CollectItemsAmountGoal("Collect 16 Chiseled Stone Bricks", chiseledStoneBricks);
        allGoals.add(stoneBricksGoal);

        List<Material> walls = new ArrayList<>();
        walls.add(Material.COBBLESTONE_WALL);
        walls.add(Material.MOSSY_COBBLESTONE_WALL);
        walls.add(Material.BRICK_WALL);
        walls.add(Material.PRISMARINE_WALL);
        walls.add(Material.RED_SANDSTONE_WALL);
        walls.add(Material.MOSSY_STONE_BRICK_WALL);
        walls.add(Material.GRANITE_WALL);
        walls.add(Material.STONE_BRICK_WALL);
        walls.add(Material.NETHER_BRICK_WALL);
        walls.add(Material.ANDESITE_WALL);
        walls.add(Material.RED_NETHER_BRICK_WALL);
        walls.add(Material.SANDSTONE_WALL);
        walls.add(Material.END_STONE_BRICK_WALL);
        walls.add(Material.DIORITE_WALL);
        walls.add(Material.BLACKSTONE_WALL);
        walls.add(Material.POLISHED_BLACKSTONE_WALL);
        walls.add(Material.POLISHED_BLACKSTONE_BRICK_WALL);
        walls.add(Material.COBBLED_DEEPSLATE_WALL);
        walls.add(Material.POLISHED_DEEPSLATE_WALL);
        walls.add(Material.DEEPSLATE_BRICK_WALL);
        walls.add(Material.DEEPSLATE_TILE_WALL);
        walls.add(Material.MUD_BRICK_WALL);
        walls.add(Material.TUFF_WALL);
        walls.add(Material.POLISHED_TUFF_WALL);
        CollectItemsAmountGoal wallsGoal = new CollectItemsAmountGoal("Collect 64 Walls of Any Type", walls, 64);
        allGoals.add(wallsGoal);

        List<Material> concreteBlocks = new ArrayList<>();
        concreteBlocks.add(Material.WHITE_CONCRETE);
        concreteBlocks.add(Material.ORANGE_CONCRETE);
        concreteBlocks.add(Material.MAGENTA_CONCRETE);
        concreteBlocks.add(Material.LIGHT_BLUE_CONCRETE);
        concreteBlocks.add(Material.YELLOW_CONCRETE);
        concreteBlocks.add(Material.LIME_CONCRETE);
        concreteBlocks.add(Material.PINK_CONCRETE);
        concreteBlocks.add(Material.GRAY_CONCRETE);
        concreteBlocks.add(Material.LIGHT_GRAY_CONCRETE);
        concreteBlocks.add(Material.CYAN_CONCRETE);
        concreteBlocks.add(Material.PURPLE_CONCRETE);
        concreteBlocks.add(Material.BLUE_CONCRETE);
        concreteBlocks.add(Material.BROWN_CONCRETE);
        concreteBlocks.add(Material.GREEN_CONCRETE);
        concreteBlocks.add(Material.RED_CONCRETE);
        concreteBlocks.add(Material.BLACK_CONCRETE);
        CollectItemsAmountGoal concreteGoal = new CollectItemsAmountGoal("Collect 64 Concrete of Any Colour", concreteBlocks, 64);
        allGoals.add(concreteGoal);

        List<Material> glassBlocks = new ArrayList<>();
        glassBlocks.add(Material.GLASS);
        glassBlocks.add(Material.WHITE_STAINED_GLASS);
        glassBlocks.add(Material.ORANGE_STAINED_GLASS);
        glassBlocks.add(Material.MAGENTA_STAINED_GLASS);
        glassBlocks.add(Material.LIGHT_BLUE_STAINED_GLASS);
        glassBlocks.add(Material.YELLOW_STAINED_GLASS);
        glassBlocks.add(Material.LIME_STAINED_GLASS);
        glassBlocks.add(Material.PINK_STAINED_GLASS);
        glassBlocks.add(Material.GRAY_STAINED_GLASS);
        glassBlocks.add(Material.LIGHT_GRAY_STAINED_GLASS);
        glassBlocks.add(Material.CYAN_STAINED_GLASS);
        glassBlocks.add(Material.PURPLE_STAINED_GLASS);
        glassBlocks.add(Material.BLUE_STAINED_GLASS);
        glassBlocks.add(Material.BROWN_STAINED_GLASS);
        glassBlocks.add(Material.GREEN_STAINED_GLASS);
        glassBlocks.add(Material.RED_STAINED_GLASS);
        glassBlocks.add(Material.BLACK_STAINED_GLASS);
        glassBlocks.add(Material.TINTED_GLASS);
        CollectItemsAmountGoal glassGoal = new CollectItemsAmountGoal("Collect 64 of Any Glass Blocks", glassBlocks, 64);
        allGoals.add(glassGoal);

        /* Weird/Frustraing Goal
        List<Material> torches = new ArrayList<>();
        torches.add(Material.TORCH);
        torches.add(Material.SOUL_TORCH);
        torches.add(Material.REDSTONE_TORCH);
        CollectItemsAmountGoal torchesGoal = new CollectItemsAmountGoal("Collect 128 Torches of Any Type", torches, 128);
        allGoals.add(torchesGoal); */

        List<Material> netherBlocks = new ArrayList<>();
        netherBlocks.add(Material.NETHERRACK);
        netherBlocks.add(Material.NETHER_BRICKS);
        netherBlocks.add(Material.RED_NETHER_BRICKS);
        netherBlocks.add(Material.NETHER_WART_BLOCK);
        netherBlocks.add(Material.CHISELED_NETHER_BRICKS);
        netherBlocks.add(Material.CRACKED_NETHER_BRICKS);
        netherBlocks.add(Material.QUARTZ_BLOCK);
        netherBlocks.add(Material.GLOWSTONE);
        netherBlocks.add(Material.SOUL_SAND);
        netherBlocks.add(Material.SOUL_SOIL);
        netherBlocks.add(Material.BASALT);
        netherBlocks.add(Material.POLISHED_BASALT);
        netherBlocks.add(Material.BLACKSTONE);
        netherBlocks.add(Material.POLISHED_BLACKSTONE);
        netherBlocks.add(Material.POLISHED_BLACKSTONE_BRICKS);
        netherBlocks.add(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        netherBlocks.add(Material.CHISELED_POLISHED_BLACKSTONE);
        CollectItemsAmountGoal netherBlocksGoal = new CollectItemsAmountGoal("Collect 64 Nether Blocks of Any Type", netherBlocks, 64);
        allGoals.add(netherBlocksGoal);
        lateGameGoals.add(netherBlocksGoal);

        List<Material> saplings = new ArrayList<>();
        saplings.add(Material.OAK_SAPLING);
        saplings.add(Material.SPRUCE_SAPLING);
        saplings.add(Material.BIRCH_SAPLING);
        saplings.add(Material.JUNGLE_SAPLING);
        saplings.add(Material.ACACIA_SAPLING);
        saplings.add(Material.DARK_OAK_SAPLING);
        saplings.add(Material.MANGROVE_PROPAGULE);
        saplings.add(Material.CHERRY_SAPLING);
        saplings.add(Material.BAMBOO);
        saplings.add(Material.PALE_OAK_SAPLING);
        CollectItemsAmountGoal saplingsGoal = new CollectItemsAmountGoal("Collect 32 Saplings of Any Type", saplings, 32);
        allGoals.add(saplingsGoal);

        List<Material> mushrooms = new ArrayList<>();
        mushrooms.add(Material.RED_MUSHROOM);
        mushrooms.add(Material.BROWN_MUSHROOM);
        mushrooms.add(Material.WARPED_FUNGUS);
        mushrooms.add(Material.CRIMSON_FUNGUS);
        CollectItemsAmountGoal mushroomsGoal = new CollectItemsAmountGoal("Collect 32 Mushrooms of Any Type", mushrooms, 32);
        biomeGoals.put(Biome.DARK_FOREST, mushroomsGoal);

        ItemStack tintedGlass = new ItemStack(Material.TINTED_GLASS,1);
        CollectItemGoal tintedGlassGoal = new CollectItemGoal("Collect a Block of Tinted Glass",tintedGlass);
        allGoals.add(tintedGlassGoal);

        ItemStack endCrystal = new ItemStack(Material.END_CRYSTAL, 1);
        CollectItemGoal endCrystalGoal = new CollectItemGoal("Collect an End Crystal", endCrystal);
        allGoals.add(endCrystalGoal);
        lateGameGoals.add(endCrystalGoal);

        ItemStack tuffBricks = new ItemStack(Material.TUFF, 16);
        CollectItemsAmountGoal tuffBricksGoal = new CollectItemsAmountGoal("Collect 16 Tuff Bricks", tuffBricks);
        allGoals.add(tuffBricksGoal);

        ItemStack redMushroom = new ItemStack(Material.RED_MUSHROOM,1);
        BreedEntityGoal mooshroomBreedGoal = new BreedEntityGoal("Breed a Mooshroom", redMushroom, EntityType.MOOSHROOM,breedEntityListener);
        biomeGoals.put(Biome.MUSHROOM_FIELDS,mooshroomBreedGoal);

        ItemStack seagrass = new ItemStack(Material.SEAGRASS, 64);
        CollectItemsAmountGoal seagrassGoal = new CollectItemsAmountGoal("Collect 64 Seagrass", seagrass);
        allGoals.add(seagrassGoal);

        ItemStack sugarcane = new ItemStack(Material.SUGAR_CANE, 32);
        CollectItemsAmountGoal sugarcaneGoal = new CollectItemsAmountGoal("Collect 32 Sugar Cane", sugarcane);
        allGoals.add(sugarcaneGoal);

        ItemStack goldBoots = new ItemStack(Material.GOLDEN_BOOTS,1);
        TravelGoal run4000Goal = new TravelGoal("Run 4000 Blocks", goldBoots, 4000.0, TravelListener.TYPE.RUNNING, travelListener);
        allGoals.add(run4000Goal);

        TravelGoal run2000Goal = new TravelGoal("Run 2000 Blocks", goldBoots, 2000.0, TravelListener.TYPE.RUNNING, travelListener);
        allGoals.add(run2000Goal);

        TravelGoal run3000Goal = new TravelGoal("Run 3000 Blocks", goldBoots, 3000.0, TravelListener.TYPE.RUNNING, travelListener);
        allGoals.add(run3000Goal);

        ItemStack boat = new ItemStack(Material.OAK_BOAT,1);
        TravelGoal boat500Goal = new TravelGoal("Use a Boat to travel 500 Blocks", boat, 500.0, TravelListener.TYPE.BOAT, travelListener);
        allGoals.add(boat500Goal);

        TravelGoal boat2000Goal = new TravelGoal("Use a Boat to travel 2000 Blocks", boat, 2000.0, TravelListener.TYPE.BOAT, travelListener);
        allGoals.add(boat2000Goal);

        TravelGoal boat1500Goal = new TravelGoal("Use a Boat to travel 1500 Blocks", boat, 1500.0, TravelListener.TYPE.BOAT, travelListener);
        allGoals.add(boat1500Goal);

        ItemStack saddle = new ItemStack(Material.SADDLE,1);
        TravelGoal pig50Goal = new TravelGoal("Use a Pig to travel 50 Blocks",saddle,50.0,TravelListener.TYPE.PIG,travelListener);
        allGoals.add(pig50Goal);

        TravelGoal pig100Goal = new TravelGoal("Use a Pig to travel 100 Blocks",saddle,100.0,TravelListener.TYPE.PIG,travelListener);
        allGoals.add(pig100Goal);

        ItemStack striderSaddle = new ItemStack(Material.WARPED_FUNGUS_ON_A_STICK, 1);

        TravelGoal horse250Goal = new TravelGoal("Use a Horse to travel 250 Blocks", saddle, 250.0, TravelListener.TYPE.HORSE, travelListener);
        allGoals.add(horse250Goal);

        TravelGoal horse500Goal = new TravelGoal("Use a Horse to travel 500 Blocks", saddle, 500.0, TravelListener.TYPE.HORSE, travelListener);
        allGoals.add(horse500Goal);

        TravelGoal strider100Goal = new TravelGoal("Use a Strider to travel 50 Blocks", striderSaddle, 100.0, TravelListener.TYPE.STRIDER, travelListener);
        allGoals.add(strider100Goal);
        lateGameGoals.add(strider100Goal);

        TravelGoal strider200Goal = new TravelGoal("Use a Strider to travel 100 Blocks", striderSaddle, 200.0, TravelListener.TYPE.STRIDER, travelListener);
        allGoals.add(strider200Goal);
        lateGameGoals.add(strider200Goal);

        /* Goal wasn't being detected by spigot
        ItemStack minecart = new ItemStack(Material.MINECART,1);
        TravelGoal cart250Goal = new TravelGoal("Use a Minecart to travel 250 Blocks", minecart, 250.0, TravelListener.TYPE.MINECART, travelListener);
        allGoals.add(cart250Goal);

        TravelGoal cart500Goal = new TravelGoal("Use a Minecart to travel 500 Blocks", minecart, 500.0, TravelListener.TYPE.MINECART, travelListener);
        allGoals.add(cart500Goal);*/

        ItemStack gunpowder = new ItemStack(Material.GUNPOWDER,1);
        KillEntityWithCauseGoal killCreeperWithTntGoal = new KillEntityWithCauseGoal("Kill a Creeper with a TNT Block",gunpowder, EntityType.CREEPER, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, killEntityListener);
        allGoals.add(killCreeperWithTntGoal);

        ItemStack stalac = new ItemStack(Material.POINTED_DRIPSTONE,1);
        KillEntityWithCauseGoal killZombieWithFallingGoal = new KillEntityWithCauseGoal("Kill a Zombie with a Falling Block",stalac, EntityType.ZOMBIE, EntityDamageEvent.DamageCause.FALLING_BLOCK, killEntityListener);
        allGoals.add(killZombieWithFallingGoal);

        ItemStack bow = new ItemStack(Material.BOW,1);
        KillEntityWithCauseGoal killSkeletonwithProjGoal = new KillEntityWithCauseGoal("Kill a Skeleton with a Projectile",bow, EntityType.SKELETON, EntityDamageEvent.DamageCause.PROJECTILE, killEntityListener);
        allGoals.add(killSkeletonwithProjGoal);

        ItemStack spEye = new ItemStack(Material.SPIDER_EYE,1);
        KillEntityWithCauseGoal killSpiderFallGoal = new KillEntityWithCauseGoal("Kill a Spider with Fall Damage",spEye, EntityType.SPIDER, EntityDamageEvent.DamageCause.FALL, killEntityListener);
        allGoals.add(killSpiderFallGoal);

        ItemStack batSpawnEgg = new ItemStack(Material.BAT_SPAWN_EGG,1);
        KillEntityGoal batKillGoal = new KillEntityGoal("Kill a Bat", batSpawnEgg, EntityType.BAT,killEntityListener);
        allGoals.add(batKillGoal);

        ItemStack ferEye = new ItemStack(Material.FERMENTED_SPIDER_EYE,1);
        CollectItemGoal ferEyeGoal = new CollectItemGoal("Craft a Fermented Spider Eye",ferEye);
        allGoals.add(ferEyeGoal);

        ItemStack lecternItem = new ItemStack(Material.LECTERN, 1);
        BlockInteractGoal placeBookOnLecternGoal = new BlockInteractGoal("Place a Book on a Lectern", lecternItem, Material.LECTERN, blockInteractListener);
        allGoals.add(placeBookOnLecternGoal);

        List<Material> fishTypes = new ArrayList<>();
        fishTypes.add(Material.COD);
        fishTypes.add(Material.SALMON);
        fishTypes.add(Material.TROPICAL_FISH);
        fishTypes.add(Material.PUFFERFISH);
        CollectItemsAmountGoal fishGoal = new CollectItemsAmountGoal("Collect 32 Fish of Any Type", fishTypes, 32);
        allGoals.add(fishGoal);

        List<Material> railTypes = new ArrayList<>();
        railTypes.add(Material.RAIL);
        railTypes.add(Material.POWERED_RAIL);
        railTypes.add(Material.DETECTOR_RAIL);
        railTypes.add(Material.ACTIVATOR_RAIL);
        CollectItemsAmountGoal railGoal = new CollectItemsAmountGoal("Collect 64 Rails of Any Type", railTypes, 64);
        allGoals.add(railGoal);

        /*ItemStack chainmailArmor = new ItemStack(Material.CHAINMAIL_HELMET, 1);
        List<Material> chainmailArmors = new ArrayList<>();
        chainmailArmors.add(Material.CHAINMAIL_HELMET);
        chainmailArmors.add(Material.CHAINMAIL_CHESTPLATE);
        chainmailArmors.add(Material.CHAINMAIL_LEGGINGS);
        chainmailArmors.add(Material.CHAINMAIL_BOOTS);
        CollectItemsGoal chainmailArmorGoal = new CollectItemsGoal("Collect any piece of Chainmail Armor", chainmailArmor, chainmailArmors);
        allGoals.add(chainmailArmorGoal);*/

        ItemStack mudBricks = new ItemStack(Material.MUD_BRICKS, 4);
        CollectItemsAmountGoal mudBricksGoal = new CollectItemsAmountGoal("Collect 4 Mud Bricks", mudBricks);
        allGoals.add(mudBricksGoal);

        ItemStack encBook = new ItemStack(Material.ENCHANTED_BOOK,1);
        CollectItemGoal encbookGoal = new CollectItemGoal("Collect Any Enchanted Book", encBook);
        allGoals.add(encbookGoal);

        /* Goal wasn't being detected by spigot
        ItemStack berries = new ItemStack(Material.SWEET_BERRIES, 64);
        List<Material> berriesList = new ArrayList<>();
        mudBrickList.add(Material.SWEET_BERRIES);
        CollectItemsAmountGoal berryGoal = new CollectItemsAmountGoal("Collect 64 Sweet Berries", berries, berriesList, 64);
        biomeGoals.put(Biome.TAIGA,berryGoal);*/

        ItemStack coarsedirt = new ItemStack(Material.COARSE_DIRT, 64);
        CollectItemsAmountGoal coarseDirtGoal = new CollectItemsAmountGoal("Collect 64 Coarse Dirt Blocks", coarsedirt);
        biomeGoals.put(Biome.OLD_GROWTH_SPRUCE_TAIGA,coarseDirtGoal);

        /* Disabled due to spigot
        ItemStack snowballs = new ItemStack(Material.SNOWBALL, 64);
        List<Material> snowList = new ArrayList<>();
        mudBrickList.add(Material.SNOWBALL);
        CollectItemsAmountGoal snowballGoal = new CollectItemsAmountGoal("Collect 64 Snowballs", snowballs, snowList, 64);
        biomeGoals.put(Biome.SNOWY_TAIGA,snowballGoal);*/

        ItemStack muddyMangroveRoots = new ItemStack(Material.MUDDY_MANGROVE_ROOTS, 16);
        CollectItemsAmountGoal mangroveRootsGoal = new CollectItemsAmountGoal("Collect 16 Muddy Mangrove Roots", muddyMangroveRoots);
        biomeGoals.put(Biome.MANGROVE_SWAMP, mangroveRootsGoal);

        /* Frustraing Goal
        List<Material> stairsList = new ArrayList<>();
        stairsList.add(Material.OAK_STAIRS);
        stairsList.add(Material.STONE_STAIRS);
        stairsList.add(Material.COBBLESTONE_STAIRS);
        stairsList.add(Material.BRICK_STAIRS);
        stairsList.add(Material.STONE_BRICK_STAIRS);
        stairsList.add(Material.NETHER_BRICK_STAIRS);
        stairsList.add(Material.SANDSTONE_STAIRS);
        stairsList.add(Material.SPRUCE_STAIRS);
        stairsList.add(Material.BIRCH_STAIRS);
        stairsList.add(Material.JUNGLE_STAIRS);
        stairsList.add(Material.ACACIA_STAIRS);
        stairsList.add(Material.DARK_OAK_STAIRS);
        stairsList.add(Material.MANGROVE_STAIRS);
        stairsList.add(Material.CRIMSON_STAIRS);
        stairsList.add(Material.WARPED_STAIRS);
        stairsList.add(Material.RED_SANDSTONE_STAIRS);
        stairsList.add(Material.PURPUR_STAIRS);
        stairsList.add(Material.PRISMARINE_STAIRS);
        stairsList.add(Material.PRISMARINE_BRICK_STAIRS);
        stairsList.add(Material.DARK_PRISMARINE_STAIRS);
        stairsList.add(Material.POLISHED_GRANITE_STAIRS);
        stairsList.add(Material.SMOOTH_RED_SANDSTONE_STAIRS);
        stairsList.add(Material.MOSSY_STONE_BRICK_STAIRS);
        stairsList.add(Material.POLISHED_DIORITE_STAIRS);
        stairsList.add(Material.MOSSY_COBBLESTONE_STAIRS);
        stairsList.add(Material.END_STONE_BRICK_STAIRS);
        stairsList.add(Material.STONE_STAIRS);
        stairsList.add(Material.SMOOTH_SANDSTONE_STAIRS);
        stairsList.add(Material.SMOOTH_QUARTZ_STAIRS);
        stairsList.add(Material.GRANITE_STAIRS);
        stairsList.add(Material.ANDESITE_STAIRS);
        stairsList.add(Material.RED_NETHER_BRICK_STAIRS);
        stairsList.add(Material.POLISHED_ANDESITE_STAIRS);
        stairsList.add(Material.DIORITE_STAIRS);
        stairsList.add(Material.COBBLED_DEEPSLATE_STAIRS);
        stairsList.add(Material.POLISHED_DEEPSLATE_STAIRS);
        stairsList.add(Material.DEEPSLATE_BRICK_STAIRS);
        stairsList.add(Material.DEEPSLATE_TILE_STAIRS);
        stairsList.add(Material.PALE_OAK_STAIRS);
        CollectItemsAmountGoal stairsGoal = new CollectItemsAmountGoal("Collect 128 Stairs of Any Type", stairsList, 128);
        allGoals.add(stairsGoal); */

        ItemStack waterBucket = new ItemStack(Material.WATER_BUCKET,1);
        DeathGoal drownGoal = new DeathGoal("Achieve the Death Message '<player> drowned'", waterBucket,"drowned",deathListener);
        allGoals.add(drownGoal);

        ItemStack tntMinecart = new ItemStack(Material.TNT_MINECART,1);
        DeathGoal tntCartGoal = new DeathGoal("Achieve the Death Message '<player> blew up'", tntMinecart, "blew up",deathListener);
        allGoals.add(tntCartGoal);

        ItemStack bed = new ItemStack(Material.RED_BED,1);
        DeathGoal netherBedGoal = new DeathGoal("Achieve the Death Message '<player> was killed by [Intentional Game Design]'", bed, "was killed by [Intentional Game Design]",deathListener);
        allGoals.add(netherBedGoal);
        lateGameGoals.add(netherBedGoal);

        ItemStack ladder = new ItemStack(Material.LADDER,1);
        DeathGoal ladderFallGoal = new DeathGoal("Achieve the Death Message '<player> fell off a ladder'",ladder,"fell off a ladder",deathListener);
        allGoals.add(ladderFallGoal);

        ItemStack flintAndSteel = new ItemStack(Material.FLINT_AND_STEEL,1);
        DeathGoal flamesGoal = new DeathGoal("Achieve the Death Message '<player> went up in flames'",flintAndSteel,"went up in flames",deathListener);
        allGoals.add(flamesGoal);

        ItemStack magmaBlock = new ItemStack(Material.MAGMA_BLOCK,1);
        DeathGoal floorLavaGoal = new DeathGoal("Achieve the Death Message '<player> discovered the floor was lava'",magmaBlock,"discovered the floor was lava",deathListener);
        allGoals.add(floorLavaGoal);

        ItemStack gravel = new ItemStack(Material.GRAVEL,1);
        DeathGoal suffocationGoal = new DeathGoal("Achieve the Death Message '<player> suffocated in a wall'",gravel,"suffocated in a wall",deathListener);
        allGoals.add(suffocationGoal);

        ItemStack berries = new ItemStack(Material.SWEET_BERRIES,1);
        DeathGoal berryPokedGoal = new DeathGoal("Achieve the Death Message \n'<player> was poked to death by a sweet berry bush'",berries,"was poked to death by a sweet berry bush",deathListener);
        biomeGoals.put(Biome.TAIGA,berryPokedGoal);

        ItemStack trident = new ItemStack(Material.TRIDENT,1);
        DeathGoal tridentDeathGoal = new DeathGoal("Achieve the Death Message '<player> was impaled by Drowned'",trident,"was impaled by Drowned",deathListener);
        allGoals.add(tridentDeathGoal);

        ItemStack nameTag = new ItemStack(Material.NAME_TAG, 1);
        CollectItemGoal nameTagGoal = new CollectItemGoal("Collect a Name Tag", nameTag);
        allGoals.add(nameTagGoal);

        List<Material> diamondArmour = new ArrayList<>();
        diamondArmour.add(Material.DIAMOND_HELMET);
        diamondArmour.add(Material.DIAMOND_CHESTPLATE);
        diamondArmour.add(Material.DIAMOND_LEGGINGS);
        diamondArmour.add(Material.DIAMOND_BOOTS);
        CollectItemSetGoal diamondArmourGoal = new CollectItemSetGoal("Collect a Full Set of Diamond Armour", diamondArmour);
        allGoals.add(diamondArmourGoal);

        List<Material> goldenTools = new ArrayList<>();
        goldenTools.add(Material.GOLDEN_PICKAXE);
        goldenTools.add(Material.GOLDEN_AXE);
        goldenTools.add(Material.GOLDEN_SHOVEL);
        goldenTools.add(Material.GOLDEN_HOE);
        CollectItemSetGoal goldenToolsGoal = new CollectItemSetGoal("Collect a Full Set of Golden Tools", goldenTools);
        allGoals.add(goldenToolsGoal);

        List<Material> goldenArmour = new ArrayList<>();
        goldenArmour.add(Material.GOLDEN_HELMET);
        goldenArmour.add(Material.GOLDEN_CHESTPLATE);
        goldenArmour.add(Material.GOLDEN_LEGGINGS);
        goldenArmour.add(Material.GOLDEN_BOOTS);
        CollectItemSetGoal goldenArmourGoal = new CollectItemSetGoal("Collect a Full Set of Golden Armour", goldenArmour);
        allGoals.add(goldenArmourGoal);

        List<Material> diamondTools = new ArrayList<>();
        diamondTools.add(Material.DIAMOND_PICKAXE);
        diamondTools.add(Material.DIAMOND_AXE);
        diamondTools.add(Material.DIAMOND_SHOVEL);
        diamondTools.add(Material.DIAMOND_HOE);
        CollectItemSetGoal diamondToolsGoal = new CollectItemSetGoal("Collect a Full Set of Diamond Tools", diamondTools);
        allGoals.add(diamondToolsGoal);

        List<Material> allFurnaces = new ArrayList<>();
        allFurnaces.add(Material.FURNACE);
        allFurnaces.add(Material.BLAST_FURNACE);
        allFurnaces.add(Material.SMOKER);
        CollectItemSetGoal furnaceGoal = new CollectItemSetGoal("Collect Every Type of Furnace", allFurnaces);
        allGoals.add(furnaceGoal);

        ItemStack cobweb = new ItemStack(Material.COBWEB, 32);
        CollectItemsAmountGoal cobwebGoal = new CollectItemsAmountGoal("Collect 32 Cobwebs", cobweb);
        allGoals.add(cobwebGoal);

        List<Material> oakWoodBlocks = new ArrayList<>();
        oakWoodBlocks.add(Material.OAK_LOG);
        oakWoodBlocks.add(Material.OAK_WOOD);
        oakWoodBlocks.add(Material.STRIPPED_OAK_LOG);
        oakWoodBlocks.add(Material.STRIPPED_OAK_WOOD);
        oakWoodBlocks.add(Material.OAK_PLANKS);
        oakWoodBlocks.add(Material.OAK_SLAB);
        oakWoodBlocks.add(Material.OAK_STAIRS);
        oakWoodBlocks.add(Material.OAK_FENCE);
        oakWoodBlocks.add(Material.OAK_FENCE_GATE);
        oakWoodBlocks.add(Material.OAK_DOOR);
        oakWoodBlocks.add(Material.OAK_TRAPDOOR);
        oakWoodBlocks.add(Material.OAK_BUTTON);
        oakWoodBlocks.add(Material.OAK_PRESSURE_PLATE);
        oakWoodBlocks.add(Material.OAK_SIGN);
        oakWoodBlocks.add(Material.OAK_HANGING_SIGN);
        CollectItemSetGoal oakWoodGoal = new CollectItemSetGoal("Collect All Oak Wood Related Blocks", oakWoodBlocks);
        allGoals.add(oakWoodGoal);

        CollectItemSetAmountGoal flowerGoal = new CollectItemSetAmountGoal("Collect 5 Unique Types of Flowers", flowers, 5);
        allGoals.add(flowerGoal);

        /* Frustrating Goal
        List<Material> pressurePlates = new ArrayList<>();
        pressurePlates.add(Material.OAK_PRESSURE_PLATE);
        pressurePlates.add(Material.SPRUCE_PRESSURE_PLATE);
        pressurePlates.add(Material.BIRCH_PRESSURE_PLATE);
        pressurePlates.add(Material.JUNGLE_PRESSURE_PLATE);
        pressurePlates.add(Material.ACACIA_PRESSURE_PLATE);
        pressurePlates.add(Material.DARK_OAK_PRESSURE_PLATE);
        pressurePlates.add(Material.CRIMSON_PRESSURE_PLATE);
        pressurePlates.add(Material.WARPED_PRESSURE_PLATE);
        pressurePlates.add(Material.STONE_PRESSURE_PLATE);
        pressurePlates.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        pressurePlates.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        pressurePlates.add(Material.MANGROVE_PRESSURE_PLATE);
        pressurePlates.add(Material.CHERRY_PRESSURE_PLATE);
        pressurePlates.add(Material.PALE_OAK_PRESSURE_PLATE);
        CollectItemSetAmountGoal pressurePlateGoal = new CollectItemSetAmountGoal("Collect 5 Unique Types of Pressure Plates", pressurePlates, 5);
        allGoals.add(pressurePlateGoal); */

        List<Material> graniteBlocks = new ArrayList<>();
        graniteBlocks.add(Material.GRANITE);
        graniteBlocks.add(Material.POLISHED_GRANITE);
        graniteBlocks.add(Material.GRANITE_SLAB);
        graniteBlocks.add(Material.POLISHED_GRANITE_SLAB);
        graniteBlocks.add(Material.GRANITE_STAIRS);
        graniteBlocks.add(Material.POLISHED_GRANITE_STAIRS);
        graniteBlocks.add(Material.GRANITE_WALL);
        CollectItemSetAmountGoal graniteGoal = new CollectItemSetAmountGoal("Collect 7 Unique Types of Granite Blocks", graniteBlocks, 7);
        allGoals.add(graniteGoal);

        List<Material> dioriteBlocks = new ArrayList<>();
        dioriteBlocks.add(Material.DIORITE);
        dioriteBlocks.add(Material.POLISHED_DIORITE);
        dioriteBlocks.add(Material.DIORITE_SLAB);
        dioriteBlocks.add(Material.POLISHED_DIORITE_SLAB);
        dioriteBlocks.add(Material.DIORITE_STAIRS);
        dioriteBlocks.add(Material.POLISHED_DIORITE_STAIRS);
        dioriteBlocks.add(Material.DIORITE_WALL);
        CollectItemSetAmountGoal dioriteGoal = new CollectItemSetAmountGoal("Collect 7 Unique Types of Diorite Blocks", dioriteBlocks, 7);
        allGoals.add(dioriteGoal);

        List<Material> andesiteBlocks = new ArrayList<>();
        andesiteBlocks.add(Material.ANDESITE);
        andesiteBlocks.add(Material.POLISHED_ANDESITE);
        andesiteBlocks.add(Material.ANDESITE_SLAB);
        andesiteBlocks.add(Material.POLISHED_ANDESITE_SLAB);
        andesiteBlocks.add(Material.ANDESITE_STAIRS);
        andesiteBlocks.add(Material.POLISHED_ANDESITE_STAIRS);
        andesiteBlocks.add(Material.ANDESITE_WALL);
        CollectItemSetAmountGoal andesiteGoal = new CollectItemSetAmountGoal("Collect 7 Unique Types of Andesite Blocks", andesiteBlocks, 7);
        allGoals.add(andesiteGoal);

        ItemStack deepslate = new ItemStack(Material.DEEPSLATE, 64);
        CollectItemsAmountGoal deepslateGoal = new CollectItemsAmountGoal("Collect 64 Deepslate Blocks", deepslate);
        allGoals.add(deepslateGoal);

        List<Material> sandBlocks = new ArrayList<>();
        sandBlocks.add(Material.SAND);
        sandBlocks.add(Material.SANDSTONE);
        sandBlocks.add(Material.SANDSTONE_SLAB);
        sandBlocks.add(Material.SANDSTONE_STAIRS);
        sandBlocks.add(Material.CHISELED_SANDSTONE);
        sandBlocks.add(Material.CUT_SANDSTONE);
        sandBlocks.add(Material.CUT_SANDSTONE_SLAB);
        sandBlocks.add(Material.SMOOTH_SANDSTONE);
        sandBlocks.add(Material.SMOOTH_SANDSTONE_SLAB);
        sandBlocks.add(Material.SMOOTH_SANDSTONE_STAIRS);
        sandBlocks.add(Material.SANDSTONE_WALL);
        sandBlocks.add(Material.RED_SAND);
        sandBlocks.add(Material.RED_SANDSTONE);
        sandBlocks.add(Material.RED_SANDSTONE_SLAB);
        sandBlocks.add(Material.RED_SANDSTONE_STAIRS);
        sandBlocks.add(Material.CHISELED_RED_SANDSTONE);
        sandBlocks.add(Material.CUT_RED_SANDSTONE);
        sandBlocks.add(Material.CUT_RED_SANDSTONE_SLAB);
        sandBlocks.add(Material.SMOOTH_RED_SANDSTONE);
        sandBlocks.add(Material.SMOOTH_RED_SANDSTONE_SLAB);
        sandBlocks.add(Material.SMOOTH_RED_SANDSTONE_STAIRS);
        sandBlocks.add(Material.RED_SANDSTONE_WALL);
        CollectItemSetAmountGoal sandGoal = new CollectItemSetAmountGoal("Collect 10 Unique Types of Sand Blocks", sandBlocks, 10);
        allGoals.add(sandGoal);

        List<Material> gravityBlocks = new ArrayList<>();
        gravityBlocks.add(Material.SAND);
        gravityBlocks.add(Material.RED_SAND);
        gravityBlocks.add(Material.POINTED_DRIPSTONE);
        gravityBlocks.add(Material.GRAVEL);
        gravityBlocks.add(Material.ANVIL);
        CollectItemSetAmountGoal gravityGoal = new CollectItemSetAmountGoal("Collect 3 Unique Types of Gravity-Affected Blocks", gravityBlocks, 3);
        allGoals.add(gravityGoal);

        List<Material> redstoneItems = new ArrayList<>();
        redstoneItems.add(Material.REDSTONE_TORCH);
        redstoneItems.add(Material.REDSTONE_BLOCK);
        redstoneItems.add(Material.PISTON);
        redstoneItems.add(Material.REPEATER);
        redstoneItems.add(Material.COMPARATOR);
        redstoneItems.add(Material.DISPENSER);
        redstoneItems.add(Material.DROPPER);
        redstoneItems.add(Material.OBSERVER);
        redstoneItems.add(Material.DAYLIGHT_DETECTOR);
        redstoneItems.add(Material.CRAFTER);
        redstoneItems.add(Material.TARGET);
        redstoneItems.add(Material.COPPER_BULB);
        redstoneItems.add(Material.WAXED_COPPER_BULB);
        redstoneItems.add(Material.POWERED_RAIL);
        redstoneItems.add(Material.ACTIVATOR_RAIL);
        redstoneItems.add(Material.DETECTOR_RAIL);
        redstoneItems.add(Material.REDSTONE_LAMP);
        redstoneItems.add(Material.COMPASS);
        redstoneItems.add(Material.CLOCK);
        CollectItemSetAmountGoal redstoneGoal = new CollectItemSetAmountGoal("Collect 7 Unique Items Crafted with Redstone", redstoneItems, 7);
        allGoals.add(redstoneGoal);

        List<Material> fish = new ArrayList<>();
        fish.add(Material.COD);
        fish.add(Material.COOKED_COD);
        fish.add(Material.SALMON);
        fish.add(Material.COOKED_SALMON);
        fish.add(Material.TROPICAL_FISH);
        fish.add(Material.PUFFERFISH);
        CollectItemSetAmountGoal fishUniqueGoal = new CollectItemSetAmountGoal("Collect 5 Unique Fish", fish, 5);
        allGoals.add(fishUniqueGoal);

        ItemStack breezeRod = new ItemStack(Material.BREEZE_ROD, 1);
        KillEntityGoal killBreezeGoal = new KillEntityGoal("Kill a Breeze", breezeRod, EntityType.BREEZE, killEntityListener);
        allGoals.add(killBreezeGoal);
        lateGameGoals.add(killBreezeGoal);

        ItemStack ominousBottle = new ItemStack(Material.OMINOUS_BOTTLE, 1);
        PotionEffectGoal trialOmenGoal = new PotionEffectGoal("Get the Trial Omen Effect", ominousBottle, PotionEffectType.TRIAL_OMEN, potionEffectListener);
        allGoals.add(trialOmenGoal);
        lateGameGoals.add(trialOmenGoal);

        ItemStack codBucket = new ItemStack(Material.COD_BUCKET, 1);
        KillEntityWithCauseGoal codWithLavaGoal = new KillEntityWithCauseGoal("Kill a Cod with Lava", codBucket, EntityType.COD, EntityDamageEvent.DamageCause.LAVA, killEntityListener);
        allGoals.add(codWithLavaGoal);

        ItemStack creakingEgg = new ItemStack(Material.CREAKING_SPAWN_EGG, 1);
        KillEntityGoal killCreakingGoal = new KillEntityGoal("Kill a Creaking", creakingEgg, EntityType.CREAKING, killEntityListener);
        biomeGoals.put(Biome.PALE_GARDEN, killCreakingGoal);

        ItemStack bush = new ItemStack(Material.BUSH, 16);
        CollectItemsAmountGoal collectBushesGoal = new CollectItemsAmountGoal("Collect 16 Bushes", bush);
        allGoals.add(collectBushesGoal);

        ItemStack lodestone = new ItemStack(Material.LODESTONE, 1);
        CollectItemGoal loadstoneGoal = new CollectItemGoal("Craft a Lodestone", lodestone);
        allGoals.add(loadstoneGoal);

        ItemStack ghastEgg = new ItemStack(Material.GHAST_SPAWN_EGG, 1);
        KillEntityWithCauseGoal ghastWithSword = new KillEntityWithCauseGoal("Kill a Ghast with a Melee Attack", ghastEgg, EntityType.GHAST, EntityDamageEvent.DamageCause.ENTITY_ATTACK, killEntityListener);
        allGoals.add(ghastWithSword);
        lateGameGoals.add(ghastWithSword);

        DeathGoal bangGoal = new DeathGoal("Achieve the Death Message '<player> went off with a bang'",fireworkRocket ,"went off with a bang",deathListener);
        allGoals.add(bangGoal);

        ItemStack shroomLight = new ItemStack(Material.SHROOMLIGHT, 32);
        CollectItemsAmountGoal shroomLightGoal = new CollectItemsAmountGoal("Collect 32 Shroomlights", shroomLight);
        allGoals.add(shroomLightGoal);
        lateGameGoals.add(shroomLightGoal);

        ItemStack snifferEgg = new ItemStack(Material.SNIFFER_EGG, 1);
        CollectItemGoal snifferEggGoal = new CollectItemGoal("Find a Sniffer Egg", snifferEgg);
        allGoals.add(snifferEggGoal);
        lateGameGoals.add(snifferEggGoal);

        ItemStack calibratedSculkSensor = new ItemStack(Material.CALIBRATED_SCULK_SENSOR, 1);
        CollectItemGoal calibratedSculkSensorGoal = new CollectItemGoal("Craft a Calibrated Sculk Sensor", calibratedSculkSensor);
        allGoals.add(calibratedSculkSensorGoal);
        lateGameGoals.add(calibratedSculkSensorGoal);

        ItemStack dragonEgg = new ItemStack(Material.DRAGON_EGG);
        CollectItemGoal dragonEggGoal = new CollectItemGoal("Collect the Dragon Egg", dragonEgg);
        allGoals.add(dragonEggGoal);
        lateGameGoals.add(dragonEggGoal);

        ItemStack dragonsBreath = new ItemStack(Material.DRAGON_BREATH, 1);
        CollectItemGoal dragonsBreathGoal = new CollectItemGoal("Collect a Dragon's Breath", dragonsBreath);
        allGoals.add(dragonsBreathGoal);
        lateGameGoals.add(dragonsBreathGoal);

        if (difficulty == Difficulty.INSANE)
        {
            biomeGoals.forEach((key,goal) ->
            {
                allGoals.add(goal);
            });
        }

        //testGoal = graniteGoal;
    }

    public void goalAutoComplete(Player player, Class goalType)
    {
        Team team = getTeam(player);
        if (team == Team.NONE) return;

        for (int slot : validSlots) {
            Goal goal = goals.get(slot);
            if (goalType.isInstance(goal)) {
                int col = (slot % 9) - 3;
                int row = slot / 9;
                boolean teamCompletedGoalAlready = hasTeamCompletedGoal(team,col,row);
                if (!teamCompletedGoalAlready) {
                    if (goal.isComplete(player)) {
                        completeGoal(slot,team,player);
                        return;
                    }
                }
            }
        }
    }

    public void craftGoalAutoComplete(Player player, ItemStack item)
    {
        Team team = getTeam(player);
        if (team == Team.NONE) return;

        for (int slot : validSlots) {
            Goal goal = goals.get(slot);
            if (goal instanceof CollectItemGoal) {
                int col = (slot % 9) - 3;
                int row = slot / 9;
                boolean teamCompletedGoalAlready = hasTeamCompletedGoal(team,col,row);
                if (!teamCompletedGoalAlready) {
                    if (((CollectItemGoal) goal).isCompleteItem(item,player)) {
                        completeGoal(slot,team,player);
                        return;
                    }
                }
            }
        }
    }

    public boolean hasTeamCompletedGoal(Team team, int col, int row)
    {
        if (team == Team.NONE) return false;

        switch (team)
        {
            case RED:
                return redBoard[col][row];
            case BLUE:
                return blueBoard[col][row];
            case GREEN:
                return greenBoard[col][row];
            case YELLOW:
                return yellowBoard[col][row];
            case ORANGE:
                return orangeBoard[col][row];
            case PURPLE:
                return purpleBoard[col][row];
            case CYAN:
                return cyanBoard[col][row];
            case BROWN:
                return brownBoard[col][row];
            default:
                return false;
        }
    }

    public void completeGoal(int slot, Team team, Player player) {
        if (gameState == GameState.FINISHED) {
            return;
        }

        if (team == Team.NONE)
        {
            if (DEBUG) Bukkit.getLogger().info(player + " is not on a team");
            return;
        }


        int col = (slot % 9) - 3;
        int row = slot / 9;

        Goal goal = goals.get(slot);
        incrementPlayerGoalsCompleted(player);

        if (goal instanceof PotionEffectGoal) {
            activeEffectGoal = false;
        } else if (goal instanceof FishingGoal) {
            activeFishGoal = false;
        } else if (goal instanceof CollectColouredItemGoal) {
            activeColouredGoal = false;
        } else if (goal instanceof CompleteAdvancementGoal) {
            activeAdvancementGoal = false;
        }

        boolean[][] teamBoard;
        ChatColor teamColor;
        ItemStack teamWool;
        Sound teamSound;
        Sound otherTeamSound = Sound.BLOCK_BELL_USE;

        switch (team) {
            case RED:
                teamBoard = redBoard;
                teamWool = redWool;
                break;
            case BLUE:
                teamBoard = blueBoard;
                teamWool = blueWool;
                break;
            case GREEN:
                teamBoard = greenBoard;
                teamWool = greenWool;
                break;
            case YELLOW:
                teamBoard = yellowBoard;
                teamWool = yellowWool;
                break;
            case ORANGE:
                teamBoard = orangeBoard;
                teamWool = orangeWool;
                break;
            case PURPLE:
                teamBoard = purpleBoard;
                teamWool = purpleWool;
                break;
            case CYAN:
                teamBoard = cyanBoard;
                teamWool = cyanWool;
                break;
            case BROWN:
                teamBoard = brownBoard;
                teamWool = brownWool;
                break;
            default:
                return;
        }
        teamSound = Sound.BLOCK_NOTE_BLOCK_PLING;
        teamColor = getTeamChatColour(player);

        teamBoard[col][row] = true;

        if (isGoalToBeDunked(col, row, team)) {
            resetBoards(col, row);

            String playerName;
            try {
                playerName = player.getName();
            } catch (Exception ex)
            {
                playerName = "<left server>";
            }

            String message = teamColor + playerName + ChatColor.WHITE + " has dunked the goal " + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + goal.getName() + ChatColor.WHITE + "!";
            BroadcastPlayerTitle(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "DUNKED!", teamColor + goal.getName());
            BingoAnnounce(message);
            BingoAnnounce("");

            if (goal instanceof TravelGoal)
            {
                activeTravelType = null;
            } else if (goal instanceof BreakBlockTypeGoal)
            {
                activeBlockTypes.remove(((BreakBlockTypeGoal) goal).requiredBlock);
            }

            newGoal(slot);

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 3f, 2f);
            }

            if (overTime)
            {
                determineWinner();
            }
            return;
        }

        for (Map.Entry<UUID, Team> entry : TeamMap.entrySet()) {
            Player p = getServer().getPlayer(entry.getKey());
            Team playerTeam = entry.getValue();

            if (p == null) continue;

            if (playerTeam == team) {
                p.playSound(p, teamSound, 10f, 2f);
            } else {
                p.playSound(p, otherTeamSound, 5f, 0f);
            }
        }

        ItemMeta meta = teamWool.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + goals.get(slot).getName());
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Goal " + (col + row * 3 + 1));
        teamWool.setItemMeta(meta);
        BingoCard.setItem(slot, teamWool);

        String playerName;
        try {
            playerName = player.getName();
        } catch (Exception ex)
        {
            playerName = "<left server>";
        }

        String message = teamColor + playerName + ChatColor.WHITE + " has completed " + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + goal.getName();
        BroadcastPlayerTitle(ChatColor.LIGHT_PURPLE + "GOAL!", teamColor + goal.getName());
        BingoAnnounce(message);

        if (checkBingo(team)) {
            BingoEnd(team);
        }

        if (overTime)
        {
            determineWinner();
        }
    }

    private boolean isGoalToBeDunked(int col, int row, Team currentTeam) {
        switch (currentTeam) {
            case RED:
                return blueBoard[col][row] || greenBoard[col][row] || yellowBoard[col][row] ||
                        orangeBoard[col][row] || purpleBoard[col][row] || cyanBoard[col][row] || brownBoard[col][row];
            case BLUE:
                return redBoard[col][row] || greenBoard[col][row] || yellowBoard[col][row] ||
                        orangeBoard[col][row] || purpleBoard[col][row] || cyanBoard[col][row] || brownBoard[col][row];
            case GREEN:
                return redBoard[col][row] || blueBoard[col][row] || yellowBoard[col][row] ||
                        orangeBoard[col][row] || purpleBoard[col][row] || cyanBoard[col][row] || brownBoard[col][row];
            case YELLOW:
                return redBoard[col][row] || blueBoard[col][row] || greenBoard[col][row] ||
                        orangeBoard[col][row] || purpleBoard[col][row] || cyanBoard[col][row] || brownBoard[col][row];
            case ORANGE:
                return redBoard[col][row] || blueBoard[col][row] || greenBoard[col][row] || yellowBoard[col][row] ||
                        purpleBoard[col][row] || cyanBoard[col][row] || brownBoard[col][row];
            case PURPLE:
                return redBoard[col][row] || blueBoard[col][row] || greenBoard[col][row] || yellowBoard[col][row] ||
                        orangeBoard[col][row] || cyanBoard[col][row] || brownBoard[col][row];
            case CYAN:
                return redBoard[col][row] || blueBoard[col][row] || greenBoard[col][row] || yellowBoard[col][row] ||
                        orangeBoard[col][row] || purpleBoard[col][row] || brownBoard[col][row];
            case BROWN:
                return redBoard[col][row] || blueBoard[col][row] || greenBoard[col][row] || yellowBoard[col][row] ||
                        orangeBoard[col][row] || purpleBoard[col][row] || cyanBoard[col][row];
            case NONE:
                return redBoard[col][row] || blueBoard[col][row] || greenBoard[col][row] || yellowBoard[col][row] ||
                        orangeBoard[col][row] || purpleBoard[col][row] || cyanBoard[col][row] || brownBoard[col][row];
            default:
                return false;
        }
    }

    public void resetBoards(int col, int row) {
        redBoard[col][row] = false;
        blueBoard[col][row] = false;
        greenBoard[col][row] = false;
        yellowBoard[col][row] = false;
        orangeBoard[col][row] = false;
        purpleBoard[col][row] = false;
        cyanBoard[col][row] = false;
        brownBoard[col][row] = false;
    }

    public void BingoEnd(Team team)
    {
        cancelAllTasks();
        gameState = GameState.FINISHED;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(Bukkit.getWorld(WorldUtil.bingoWorldName).getSpawnLocation());
            OpenInv(player);
        }

        /* RANKED FOR FFA IS DISABLED FOR THE MOMENT

        List<Player> winningTeamPlayers = TeamMap.entrySet().stream()
                .filter(entry -> entry.getValue() == team)
                .map(entry -> plugin.getServer().getPlayer(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Team, List<Player>> losingTeamsPlayers = TeamMap.entrySet().stream()
                .filter(entry -> entry.getValue() != team)
                .collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(entry -> plugin.getServer().getPlayer(entry.getKey()), Collectors.toList())));

        for (List<Player> losingTeamPlayers : losingTeamsPlayers.values()) {
            rankedUtil.calculateElo(winningTeamPlayers, losingTeamPlayers.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        }
        rankedUtil.saveRanks();*/

        if (gameMode == Mode.TEAM)
        {
            List<Player> winningTeamPlayers = TeamMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == team)
                    .map(entry -> plugin.getServer().getPlayer(entry.getKey()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            List<Player> losingTeamPlayers = TeamMap.entrySet().stream()
                    .filter(entry -> entry.getValue() != team)
                    .map(entry -> plugin.getServer().getPlayer(entry.getKey()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        gameMode = Mode.TEAM;

        ChatColor teamColor;
        String teamName;
        Color fireworkColor;

        switch (team) {
            case RED:
                teamName = "Red";
                fireworkColor = Color.RED;
                break;
            case BLUE:
                teamName = "Blue";
                fireworkColor = Color.BLUE;
                break;
            case GREEN:
                teamName = "Green";
                fireworkColor = Color.GREEN;
                break;
            case YELLOW:
                teamName = "Yellow";
                fireworkColor = Color.YELLOW;
                break;
            case ORANGE:
                teamName = "Orange";
                fireworkColor = Color.ORANGE;
                break;
            case PURPLE:
                teamName = "Purple";
                fireworkColor = Color.PURPLE;
                break;
            case CYAN:
                teamName = "Cyan";
                fireworkColor = Color.AQUA;
                break;
            case BROWN:
                teamName = "Brown";
                fireworkColor = Color.MAROON;
                break;
            default:
                return;
        }
        teamColor = getTeamChatColour(team);

        String message = teamColor + "" + ChatColor.BOLD + teamName + " team has won Bingo!";
        BroadcastPlayerTitle(ChatColor.LIGHT_PURPLE + "BINGO!",message);
        BingoAnnounce(message);
        spawnFirework(Bukkit.getWorld(WorldUtil.bingoWorldName).getSpawnLocation(), fireworkColor, FireworkEffect.Type.BALL_LARGE);

        showStats();
    }

    public void showStats()
    {
        playerGoalsCompleted.forEach((uuid, numOfGoals) ->
        {
            playerTotalGoalsCompleted.put(uuid, playerTotalGoalsCompleted.getOrDefault(uuid,0) + numOfGoals);
        });


        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage("");
            BingoAnnounce(ChatColor.LIGHT_PURPLE + "Made by " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "ayeitszeth");
            Bukkit.broadcastMessage("");
        }, 20L * 5);

        addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("");
            Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED GAME STATS] ");
            List<Map.Entry<UUID, Integer>> sortedEntries = new ArrayList<>(playerGoalsCompleted.entrySet());
            sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

            for (Map.Entry<UUID, Integer> entry : sortedEntries) {
                Player player = getServer().getPlayer(entry.getKey());
                String playerName;

                try {
                    playerName = player.getName();
                } catch (Exception ex)
                {
                    playerName = "<left server>";
                }

                Integer goalsCompleted = entry.getValue();
                ChatColor teamChatColour = getTeamChatColour(player);
                Bukkit.broadcastMessage("    " + teamChatColour + playerName + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + goalsCompleted + ChatColor.WHITE + " goals completed");
            }
            Bukkit.broadcastMessage("");
        }, 20L * 10).getTaskId());
    }

    public boolean checkBingo(Team team) {
        boolean[][] board;

        switch (team) {
            case RED:
                board = redBoard;
                break;
            case BLUE:
                board = blueBoard;
                break;
            case GREEN:
                board = greenBoard;
                break;
            case YELLOW:
                board = yellowBoard;
                break;
            case ORANGE:
                board = orangeBoard;
                break;
            case PURPLE:
                board = purpleBoard;
                break;
            case CYAN:
                board = cyanBoard;
                break;
            case BROWN:
                board = brownBoard;
                break;
            default:
                return false;
        }

        return checkBoardForBingo(board);
    }

    private boolean checkBoardForBingo(boolean[][] board) {
        for (int col = 0; col < 3; ++col) {
            if (board[col][0] && board[col][1] && board[col][2]) {
                return true;
            }
        }

        for (int row = 0; row < 3; ++row) {
            if (board[0][row] && board[1][row] && board[2][row]) {
                return true;
            }
        }

        if (board[0][0] && board[1][1] && board[2][2]) {
            return true;
        }

        if (board[2][0] && board[1][1] && board[0][2]) {
            return true;
        }

        return false;
    }

    public void OpenInv(Player player)
    {
        player.openInventory(BingoCard);
    }

    private void spawnFirework(Location location, Color color, FireworkEffect.Type type) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(color)
                .with(type)
                .withTrail()
                .withFlicker()
                .build();

        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);
    }

    public void cancelAllTasks()
    {
        for (Integer taskId : taskIds) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        taskIds.clear();
    }

    public void addTaskId(int taskId)
    {
        taskIds.add(taskId);
    }

    public void BingoCompleteGoal(int slot, Team team, Player player)
    {
        completeGoal(slot,team,player);
    }

    public Team getTeam(Player player)
    {
        try {
            UUID playerUUID = player.getUniqueId();
            return TeamMap.getOrDefault(playerUUID, Team.NONE);
        } catch (Exception ex)
        {
            return Team.NONE;
        }

    }

    public void incrementPlayerGoalsCompleted(Player player) {
        numOfGoalsCompleted++;
        playerGoalsCompleted.put(player.getUniqueId(), playerGoalsCompleted.getOrDefault(player.getUniqueId(), 0) + 1);
        updatePlayerTabListName(player);
    }

    public void updatePlayerTabListName(Player player) {
        int goalsCompleted = playerGoalsCompleted.getOrDefault(player.getUniqueId(), 0);

        ChatColor teamChatColour = getTeamChatColour(player);

        if (pvp == PvP.GLOWING_PVP) {
            player.setGlowing(true);

            // Create or get the scoreboard
            ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = scoreboardManager.getMainScoreboard();

            // Create or get the team
            String teamName = "team_" + teamChatColour.name();
            org.bukkit.scoreboard.Team team = scoreboard.getTeam(teamName);
            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
                team.setColor(teamChatColour);
            }

            // Add the player to the team
            team.addEntry(player.getName());

            // Set the player's team to apply the color glow
            player.setScoreboard(scoreboard);
        } else {
            player.setGlowing(false);

            // Optionally, remove the player from the team if the glowing effect is removed
            ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
            org.bukkit.scoreboard.Team team = scoreboard.getEntryTeam(player.getName());
            if (team != null) {
                team.removeEntry(player.getName());
            }
        }

        player.setDisplayName(teamChatColour + player.getName());
        player.setPlayerListName(teamChatColour + player.getName() + ChatColor.LIGHT_PURPLE+ " (" + goalsCompleted + ")");
    }

    public void resetPlayerGoalsCompleted() {
        playerGoalsCompleted.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerGoalsCompleted.put(player.getUniqueId(), 0);
            updatePlayerTabListName(player);
        }
    }

    public ChatColor getTeamChatColour(Player player)
    {
        Team team = getTeam(player);

        switch (team) {
            case RED:
                return ChatColor.RED;
            case BLUE:
                return ChatColor.BLUE;
            case GREEN:
                return ChatColor.GREEN;
            case YELLOW:
                return ChatColor.YELLOW;
            case ORANGE:
                return ChatColor.GOLD;
            case PURPLE:
                return ChatColor.DARK_PURPLE;
            case CYAN:
                return ChatColor.DARK_AQUA;
            case BROWN:
                return ChatColor.DARK_RED;
            default:
                return ChatColor.GRAY;
        }
    }

    public ChatColor getTeamChatColour(Team team)
    {
        switch (team) {
            case RED:
                return ChatColor.RED;
            case BLUE:
                return ChatColor.BLUE;
            case GREEN:
                return ChatColor.GREEN;
            case YELLOW:
                return ChatColor.YELLOW;
            case ORANGE:
                return ChatColor.GOLD;
            case PURPLE:
                return ChatColor.DARK_PURPLE;
            case CYAN:
                return ChatColor.DARK_AQUA;
            case BROWN:
                return ChatColor.DARK_RED;
            default:
                return ChatColor.GRAY;
        }
    }

    private void startTimer() {
        final float[] pitch = {0.7f};
        addTaskId(new BukkitRunnable() {
            @Override
            public void run() {
                if (timeLeft % 300 == 0 && timeLeft > 60) {
                    BingoAnnounce((timeLeft / 60) + " minutes remaining ...");
                    for (Player player : Bukkit.getOnlinePlayers())
                    {
                        player.playSound(player.getLocation(),Sound.BLOCK_BELL_USE, 3f, 1.1f);
                    }
                } else if (timeLeft == 60) {
                    BingoAnnounce("1 minute remaining ...");
                    for (Player player : Bukkit.getOnlinePlayers())
                    {
                        player.playSound(player.getLocation(),Sound.BLOCK_BELL_USE, 3f, 1.5f);
                    }
                } else if (timeLeft == 30) {
                    BingoAnnounce("30 seconds remaining ...");
                    for (Player player : Bukkit.getOnlinePlayers())
                    {
                        player.playSound(player.getLocation(),Sound.BLOCK_BELL_USE, 3f, 1.5f);
                    }
                } else if (timeLeft <= 10 && timeLeft > 0) {
                    BingoAnnounce(timeLeft + " seconds remaining ...");
                    for (Player player : Bukkit.getOnlinePlayers())
                    {
                        player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_PLING, 3f, pitch[0]);
                    }
                    pitch[0] += 0.11f;
                } else if (timeLeft == 0) {
                    this.cancel();
                    determineWinner();
                    return;
                }

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0, 20L).getTaskId());
    }

    public void determineWinner() {
        Map<Team, Integer> teamGoalsCompleted = new HashMap<>();
        Team winner = null;
        int maxGoals = 0;
        boolean tie = false;

        for (Map.Entry<UUID, Integer> entry : playerGoalsCompleted.entrySet()) {
            Team team = TeamMap.get(entry.getKey());
            if (team != null && team != Team.NONE) {
                teamGoalsCompleted.put(team, teamGoalsCompleted.getOrDefault(team, 0) + entry.getValue());
            }
        }

        for (Map.Entry<Team, Integer> entry : teamGoalsCompleted.entrySet()) {
            if (entry.getValue() > maxGoals) {
                winner = entry.getKey();
                maxGoals = entry.getValue();
                tie = false;
            } else if (entry.getValue() == maxGoals) {
                tie = true;
            }
        }

        if (tie) {
            BingoAnnounce("");
            BingoAnnounce(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "    OVERTIME !!!");
            overTime = true;
            goalsToWinOT = maxGoals + 1;
            BingoAnnounce("");
            BingoAnnounce("The first team to " + goalsToWinOT + " goals OR get a Bingo WINS !!!");
            BingoAnnounce("");
        } else if (winner != null) {
            BingoEnd(winner);
        } else {
            BingoAnnounce("No winner can be determined...");
        }
    }

    public static void BingoWhisper(Player player, String message)
    {
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED WHISPER] " + ChatColor.GRAY + message);
    }

    public void BingoAnnounce(String message)
    {
        if (message.isEmpty())
        {
            Bukkit.broadcastMessage("");
        } else
        {
            Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED] " + ChatColor.LIGHT_PURPLE + message);
        }
    }

    public void SendPlayerTime(Player player)
    {
        int minutes = timeLeft / 60;
        int remainingSeconds = timeLeft % 60;

        String minutePart = minutes + " minute" + (minutes == 1 ? "" : "s");
        String secondPart = remainingSeconds + " second" + (remainingSeconds == 1 ? "" : "s");

        if (minutes > 0 && remainingSeconds > 0) {
            BingoWhisper(player, minutePart + " and " + secondPart + " left.");
        } else if (minutes > 0) {
            BingoWhisper(player, minutePart + " left.");
        } else {
            BingoWhisper(player, secondPart + " left.");
        }
    }

    public List<Player> GetPlayersOnTeam(Team team)
    {
        List<Player> pList = new ArrayList<>();

        for (Map.Entry<UUID, Team> entry : TeamMap.entrySet()) {
            if (entry.getValue().equals(team)) {
                pList.add(Bukkit.getPlayer(entry.getKey()));
            }
        }

        return pList;
    }

    public void PrintTeams()
    {
        for (Map.Entry<UUID, Team> entry : TeamMap.entrySet()) {
            UUID playerUUID = entry.getKey();
            Team team = entry.getValue();
            if (BingoUtil.DEBUG) Bukkit.getLogger().info("Player UUID: " + playerUUID + ", Team: " + team);
        }
    }

    public void BroadcastPlayerTitle(String title, String subtitle)
    {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(
                    ChatColor.LIGHT_PURPLE + title,
                    subtitle,
                    10, // Fade-in duration (ticks)
                    70, // Stay duration (ticks)
                    20  // Fade-out duration (ticks)
            );
        }
    }

    public Player getPlayerWithLeastGoals() {
        UUID playerWithLeastGoals = null;
        int minGoals = Integer.MAX_VALUE;

        for (Map.Entry<UUID, Integer> entry : playerGoalsCompleted.entrySet()) {
            if (entry.getValue() < minGoals) {
                minGoals = entry.getValue();
                playerWithLeastGoals = entry.getKey();
            }
        }

        return Bukkit.getPlayer(playerWithLeastGoals);
    }

    public void openSettings(Player player)
    {
        player.openInventory(settingsGUI);
    }

    public void updateGameMode(Mode mode)
    {
        if (mode == null) return;
        gameMode = mode;

        if (mode == Mode.TEAM)
        {
            settingsGUI.setItem(12,enabled);
            settingsGUI.setItem(15,disabled);
        } else if (mode == Mode.FFA)
        {
            settingsGUI.setItem(12,disabled);
            settingsGUI.setItem(15,enabled);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 2.0f);
        }

        BingoAnnounce("The game mode has been updated to " + ChatColor.BOLD + gameMode.name() + "!");
    }

    public void updateDifficulty(Difficulty difficulty)
    {
        if (difficulty == null) return;
        this.difficulty = difficulty;

        if (difficulty == Difficulty.NORMAL)
        {
            settingsGUI.setItem(21,enabled);
            settingsGUI.setItem(24,disabled);
        } else if (difficulty == Difficulty.INSANE)
        {
            settingsGUI.setItem(21,disabled);
            settingsGUI.setItem(24,enabled);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 2.0f);
        }

        BingoAnnounce("The difficulty has been updated to " + ChatColor.BOLD + difficulty.name() + "!");
    }

    public void updatePvp(PvP pvp)
    {
        if (pvp == null) return;
        this.pvp = pvp;

        if (pvp == PvP.NOPVP)
        {
            settingsGUI.setItem(28,enabled);
            settingsGUI.setItem(30,disabled);
            settingsGUI.setItem(33,disabled);
            settingsGUI.setItem(35,disabled);
        } else if (pvp == PvP.PVP)
        {
            settingsGUI.setItem(28,disabled);
            settingsGUI.setItem(30,enabled);
            settingsGUI.setItem(33,disabled);
            settingsGUI.setItem(35,disabled);
        } else if (pvp == PvP.GLOWING_PVP)
        {
            settingsGUI.setItem(28,disabled);
            settingsGUI.setItem(30,disabled);
            settingsGUI.setItem(33,enabled);
            settingsGUI.setItem(35,disabled);
        } else if (pvp == PvP.TRACKING_PVP)
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

        BingoAnnounce("The difficulty has been updated to " + ChatColor.BOLD + pvp.name() + "!");

    }

    public Location getNearestPlayerNotOnTeam(Player player)
    {
        Player nearestPlayer = null;
        double nearestDistanceSquared = Double.MAX_VALUE;
        World locationWorld = player.getWorld();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(locationWorld) && getTeam(player) != getTeam(p)) {
                double distanceSquared = p.getLocation().distanceSquared(player.getLocation());
                if (distanceSquared < nearestDistanceSquared) {
                    nearestDistanceSquared = distanceSquared;
                    nearestPlayer = p;
                }
            }
        }

        if (nearestPlayer == null) {
            nearestPlayer = player;
            BingoWhisper(player, "No player has been found...");
        } else
        {
            BingoWhisper(player, "Your nearest rival has been found...");
        }


        return nearestPlayer.getLocation();
    }
}
