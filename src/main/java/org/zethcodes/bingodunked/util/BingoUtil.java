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
import org.zethcodes.bingodunked.BingoDunked;
import org.zethcodes.bingodunked.goals.*;
import org.zethcodes.bingodunked.listeners.*;
import org.zethcodes.bingodunked.managers.BoardManager;
import org.zethcodes.bingodunked.managers.SettingsManager;
import org.zethcodes.bingodunked.managers.TeamsManager;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;
import static org.zethcodes.bingodunked.managers.GameManager.plugin;

public class BingoUtil {

    Goal testGoal = null;

    public List<Biome> findBiomes(Location location, int radius, int step)
    {
        Set<Biome> uniqueBiomes = new HashSet<>();
        int centreX = location.getBlockX();
        int centreZ = location.getBlockZ();
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

    public static void showStats()
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

    public static void spawnFirework(Location location, Color color, FireworkEffect.Type type) {
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

    public void BingoCompleteGoal(int slot, Team team, Player player)
    {
        completeGoal(slot,team,player);
    }

    public void incrementPlayerGoalsCompleted(Player player) {
        numOfGoalsCompleted++;
        playerGoalsCompleted.put(player.getUniqueId(), playerGoalsCompleted.getOrDefault(player.getUniqueId(), 0) + 1);
        updatePlayerTabListName(player);
    }

    public static void updatePlayerTabListName(Player player) {
        int goalsCompleted = playerGoalsCompleted.getOrDefault(player.getUniqueId(), 0);

        ChatColor teamChatColour = teams.getTeamChatColour(player);

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

    public static void BingoWhisper(Player player, String message)
    {
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED WHISPER] " + ChatColor.GRAY + message);
    }

    public static void BingoAnnounce(String message)
    {
        if (message.isEmpty())
        {
            Bukkit.broadcastMessage("");
        } else
        {
            Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED] " + ChatColor.LIGHT_PURPLE + message);
        }
    }

    public static void BroadcastPlayerTitle(String title, String subtitle)
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

    public static Location getNearestPlayerNotOnTeam(Player player)
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

    public static List<String> wrapAndColorLore(String text, int wrapLength, ChatColor color) {
        List<String> result = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (String word : text.split(" ")) {
            if (line.length() + word.length() + 1 > wrapLength) {
                result.add(color + line.toString().trim());
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        if (line.length() > 0) {
            result.add(color + line.toString().trim());
        }
        return result;
    }
}
