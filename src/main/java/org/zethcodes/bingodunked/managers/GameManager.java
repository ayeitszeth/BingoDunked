package org.zethcodes.bingodunked.managers;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.zethcodes.bingodunked.BingoDunked;
import org.zethcodes.bingodunked.goals.*;
import org.zethcodes.bingodunked.listeners.*;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.WorldUtil;

import java.util.*;

import static org.bukkit.Bukkit.getServer;
import static org.zethcodes.bingodunked.util.BingoUtil.*;

public class GameManager {

    public static GameManager instance;
    public enum GameState { LOADING, STARTED, FINISHED }

    public KillEntityListener killEntityListener;
    public BreedEntityListener breedEntityListener;
    public PotionEffectListener potionEffectListener;
    public EnchantListener enchantListener;
    public FishingListener fishingListener;
    public BreakBlockTypeListener blockTypeListener;
    public FallHeightListener fallHeightListener;
    public ExperienceListener experienceListener;
    public EatListener eatListener;
    public BlockInteractListener blockInteractListener;
    public ArmorStandInteractListener armorStandInteractListener;
    public TravelListener travelListener;
    public DeathListener deathListener;
    public CauldronListener cauldronListener;
    public AdvancementListener advancementListener;
    public static BingoDunked plugin;
    public static boolean DEBUG = true;

    public TeamsManager teamsManager;
    public BoardManager boardManager;

    public static GameState gameState = GameState.FINISHED;
    int numOfGoalsCompleted = 0;
    public Map<UUID, Integer> playerGoalsCompleted = new HashMap<>();
    private int timeLeft;
    boolean overTime = false;
    int goalsToWinOT = -1;

    public GameManager(BingoDunked plugin, KillEntityListener killEntityListener, BreedEntityListener breedEntityListener, PotionEffectListener potionEffectListener,
                     EnchantListener enchantListener, FishingListener fishingListener, FallHeightListener fallHeightListener,
                     ExperienceListener experienceListener, EatListener eatListener, BlockInteractListener blockInteractListener,
                     ArmorStandInteractListener armorStandInteractListener, DeathListener deathListener, CauldronListener cauldronListener, AdvancementListener advancementListener) {
        instance = this;
        GameManager.plugin = plugin;
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
        this.cauldronListener = cauldronListener;
        this.advancementListener = advancementListener;

        this.travelListener = new TravelListener();
        this.blockTypeListener = new BreakBlockTypeListener();

        teamsManager = new TeamsManager();
        boardManager = new BoardManager();

        getServer().getPluginManager().registerEvents(travelListener, plugin);
        getServer().getPluginManager().registerEvents(blockTypeListener, plugin);
    }

    //region Bingo Processes

    public void BingoSetUp(SettingsManager.Mode mode, int time) {
        TaskManager.instance.cancelAllTasks();
        SettingsManager.gameMode = mode;
        gameState = GameState.LOADING;

        boardManager.BoardSetUp();
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
        cauldronListener.Reset();
        advancementListener.Reset();
        numOfGoalsCompleted = 0;
        timeLeft = time * 60;
        overTime = false;
        goalsToWinOT = -1;


        if (SettingsManager.gameMode == SettingsManager.Mode.FFA) {
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

        for (Biome biome : BingoUtil.findBiomes(spawnLoc, 64, 5))
        {
            boardManager.goalManager.AddBiomeGoals(biome);
        }

        foundStructures.clear();
        BingoUtil.findStructures(spawnLoc, 128);

        for (Structure structure : foundStructures)
        {
            boardManager.goalManager.AddStructureGoals(structure);
        }

        Collections.shuffle(boardManager.goalManager.availableGoals);

        BingoAnnounce("The board is being set up. Please wait one moment.");

        resetPlayerGoalsCompleted();

        if (SettingsManager.pvp != SettingsManager.PvP.NOPVP)
        {
            TaskManager.instance.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 2 minutes remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.89f);
                }
            }, 20L * (1 * 60 + 10)).getTaskId());

            TaskManager.instance.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 1 minutes remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.78f);
                }
            }, 20L * (2 * 60 + 10)).getTaskId());

            TaskManager.instance.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 30 seconds remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.68f);
                }
            }, 20L * (2 * 60 + 30 + 10)).getTaskId());

            TaskManager.instance.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BingoAnnounce("The Grace Period has 10 seconds remaining...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.5f);
                }
            }, 20L * (2 * 60 + 50 + 10)).getTaskId());

            TaskManager.instance.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                SettingsManager.isPvpEnabled = true;
                BingoAnnounce("The Grace Period has ended...");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 0f);
                }
            }, 20L * (3 * 60 + 10)).getTaskId());
        }

        BingoAnnounce("Bingo will start in 15 seconds...");

        Map<UUID, TeamsManager.Team> TeamMap = teamsManager.GetTeamMap();

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

            if (SettingsManager.gameMode == SettingsManager.Mode.TEAM && !TeamMap.containsKey(player.getUniqueId())) {
                BingoWhisper(player,"Psst... You haven't joined a team yet...");
            }
        }

        TaskManager.instance.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 5 seconds...");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.5f);
            }
        }, 20L * 10).getTaskId());

        TaskManager.instance.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 3!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.68f);
            }
        }, 20L * 12).getTaskId());

        TaskManager.instance.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 2!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.78f);
            }
        }, 20L * 13).getTaskId());

        TaskManager.instance.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("Bingo will start in 1!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.89f);
            }
        }, 20L * 14).getTaskId());

        TaskManager.instance.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoStart();
            if (SettingsManager.gameMode == SettingsManager.Mode.FFA)
            {
                startTimer();
            }
        }, 20L * 15).getTaskId());
    }

    public void BingoStart() {
        boardManager.FillCard();

        gameState = GameState.STARTED;

        BingoAnnounce("");
        BingoAnnounce("Bingo has begun!");
        BingoAnnounce("");

        for (Player player : Bukkit.getOnlinePlayers()) {
            boardManager.OpenInv(player);
        }

        if (SettingsManager.pvp != SettingsManager.PvP.NOPVP)
        {
            BingoAnnounce("");
            BingoAnnounce("The Grace Period will end in 3 minutes...");
        }
    }

    public void BingoEnd(TeamsManager.Team team) {
        TaskManager.instance.cancelAllTasks();
        gameState = GameState.FINISHED;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(Bukkit.getWorld(WorldUtil.bingoWorldName).getSpawnLocation());
            boardManager.OpenInv(player);
        }

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
        teamColor = teamsManager.getTeamChatColour(team);

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
        TaskManager.instance.addTaskId(new BukkitRunnable() {
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

    public void CheckOverTime()
    {
        if (overTime) determineWinner();
    }

    public void determineWinner() {
        Map<TeamsManager.Team, Integer> teamGoalsCompleted = new HashMap<>();
        TeamsManager.Team winner = null;
        int maxGoals = 0;
        boolean tie = false;

        for (Map.Entry<UUID, Integer> entry : playerGoalsCompleted.entrySet()) {
            TeamsManager.Team team = teamsManager.TeamMap.get(entry.getKey());
            if (team != null && team != TeamsManager.Team.NONE) {
                teamGoalsCompleted.put(team, teamGoalsCompleted.getOrDefault(team, 0) + entry.getValue());
            }
        }

        for (Map.Entry<TeamsManager.Team, Integer> entry : teamGoalsCompleted.entrySet()) {
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

    public void incrementPlayerGoalsCompleted(Player player) {
        numOfGoalsCompleted++;
        playerGoalsCompleted.put(player.getUniqueId(), playerGoalsCompleted.getOrDefault(player.getUniqueId(), 0) + 1);

        if (numOfGoalsCompleted % 10 == 0) // new stage
        {
            boardManager.incrementStage();
        }

        updatePlayerTabListName(player);
    }

    //endregion
}
