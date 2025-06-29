package org.zethcodes.bingodunked.managers;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.zethcodes.bingodunked.BingoDunked;
import org.zethcodes.bingodunked.goals.*;
import org.zethcodes.bingodunked.listeners.*;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.WorldUtil;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;
import static org.zethcodes.bingodunked.util.BingoUtil.*;

public class GameManager {

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
    public static BingoDunked plugin;
    public static boolean DEBUG = false;

    TeamsManager teamsManager;
    BoardManager boardManager;
    TaskManager taskManager;
    SettingsManager settingsManager;
    GoalFactory goalFactory;

    HashMap<Integer, Goal> goals = new HashMap<>();
    public static BingoUtil.GameState gameState = BingoUtil.GameState.FINISHED;
    int numOfGoalsCompleted = 0;
    private final Map<UUID, Integer> playerGoalsCompleted = new HashMap<>();
    private int timeLeft;
    boolean overTime = false;
    int goalsToWinOT = -1;

    public TravelListener.TYPE activeTravelType = null;
    public Set<BreakBlockTypeListener.BlockType> activeBlockTypes = new HashSet<>();

    public GameManager(BingoDunked plugin, KillEntityListener killEntityListener, BreedEntityListener breedEntityListener, PotionEffectListener potionEffectListener,
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

        teamsManager = new TeamsManager();
        boardManager = new BoardManager();
        taskManager = new TaskManager();
        settingsManager = new SettingsManager();
        goalFactory = new GoalFactory();

        getServer().getPluginManager().registerEvents(travelListener, plugin);
        getServer().getPluginManager().registerEvents(blockTypeListener, plugin);
    }

    //region Bingo Processes

    public void BingoSetUp(BingoUtil.Mode mode, int time) {
        taskManager.cancelAllTasks();
        settingsManager.gameMode = mode;
        gameState = BingoUtil.GameState.LOADING;

        if (settingsManager.gameMode == BingoUtil.Mode.TEAM) {
            settingsManager.setGUI(12, true);
            settingsManager.setGUI(15, false);
        } else if (mode == BingoUtil.Mode.FFA) {
            settingsManager.setGUI(12, false);
            settingsManager.setGUI(15, true);
        }

        boardManager.BoardSetUp();

        goalFactory.SetGoals();
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
        overTime = false;
        goalsToWinOT = -1;

        activeTravelType = null;
        activeBlockTypes.clear();

        if (settingsManager.gameMode == BingoUtil.Mode.FFA) {
            teamsManager.FFATeamsSetUp();
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

        if (settingsManager.pvp != BingoUtil.PvP.NOPVP)
        {
            taskManager.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 2 minutes remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.89f);
                }
            }, 20L * (1 * 60 + 10)).getTaskId());

            taskManager.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 1 minutes remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.78f);
                }
            }, 20L * (2 * 60 + 10)).getTaskId());

            taskManager.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 30 seconds remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.68f);
                }
            }, 20L * (2 * 60 + 30 + 10)).getTaskId());

            taskManager.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 10 seconds remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.5f);
                }
            }, 20L * (2 * 60 + 50 + 10)).getTaskId());

            taskManager.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                SettingsManager.isPvpEnabled = true;
                BingoAnnounce("The Grace Period has ended...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 0f);
                }
            }, 20L * (3 * 60 + 10)).getTaskId());
        }

        BingoAnnounce("Bingo will start in 10 seconds...");

        Map<UUID, BingoUtil.Team> TeamMap = teamsManager.GetTeamMap();

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

            if (settingsManager.gameMode == BingoUtil.Mode.TEAM && !TeamMap.containsKey(player.getUniqueId())) {
                BingoWhisper(player,"Psst... You haven't joined a team yet...");
            }
        }

        taskManager.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 5 seconds...");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.5f);
            }
        }, 20L * 5).getTaskId());

        taskManager.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 3!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.68f);
            }
        }, 20L * 7).getTaskId());

        taskManager.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 2!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.78f);
            }
        }, 20L * 8).getTaskId());

        taskManager.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 1!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.89f);
            }
        }, 20L * 9).getTaskId());

        taskManager.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoStart();
            if (settingsManager.gameMode == BingoUtil.Mode.FFA)
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
            if (random.nextInt(2) == 0 && !startBiomes.isEmpty() && difficulty == BingoUtil.Difficulty.NORMAL) { // 50/50 coin flip if it's a biome goal
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
                    difficulty == BingoUtil.Difficulty.NORMAL) {

                if (allGoals.isEmpty()) SetGoals();

                ranGoal = random.nextInt(allGoals.size());
                goal = allGoals.get(ranGoal);

                if (lateGameGoals.contains(goal) && difficulty == BingoUtil.Difficulty.NORMAL)
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
            List<String> lore = wrapAndColorLore(goal.getName(), 30, ChatColor.DARK_PURPLE);
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
            if (pvp == BingoUtil.PvP.TRACKING_PVP)
            {
                player.getInventory().addItem(new ItemStack(Material.COMPASS,1));
            }
        }

        gameState = BingoUtil.GameState.STARTED;

        animateBingoCard();
        BingoAnnounce("");
        BingoAnnounce("Bingo has begun!");
        BingoAnnounce("");

        if (pvp != BingoUtil.PvP.NOPVP)
        {
            BingoAnnounce("");
            BingoAnnounce("The Grace Period will end in 3 minutes...");
        }
    }

    public void BingoEnd(BingoUtil.Team team) {
        cancelAllTasks();
        gameState = BingoUtil.GameState.FINISHED;

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

        if (gameMode == BingoUtil.Mode.TEAM)
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

        gameMode = BingoUtil.Mode.TEAM;

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

    //endregion

    //region Time Functions

    public void SendPlayerTime(Player player) {
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

    private void startTimer() {
        final float[] pitch = {0.7f};
        taskManager.addTaskId(new BukkitRunnable() {
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

    //endregion

    //region Other

    public void resetPlayerGoalsCompleted() {
        playerGoalsCompleted.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerGoalsCompleted.put(player.getUniqueId(), 0);
            updatePlayerTabListName(player);
        }
    }

    public void determineWinner() {
        Map<Team, Integer> teamGoalsCompleted = new HashMap<>();
        Team winner = null;
        int maxGoals = 0;
        boolean tie = false;

        for (Map.Entry<UUID, Integer> entry : playerGoalsCompleted.entrySet()) {
            Team team = teamsManager.TeamMap.get(entry.getKey());
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

    //endregion
}
