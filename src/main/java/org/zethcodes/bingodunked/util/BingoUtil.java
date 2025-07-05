package org.zethcodes.bingodunked.util;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.generator.structure.Structure;
import org.bukkit.util.StructureSearchResult;
import org.zethcodes.bingodunked.managers.*;

import java.util.*;

import static org.bukkit.Bukkit.getServer;
import static org.zethcodes.bingodunked.managers.GameManager.plugin;
import static org.zethcodes.bingodunked.managers.SettingsManager.pvp;

public class BingoUtil {

    static List<Structure> structures = Arrays.asList(
            Structure.ANCIENT_CITY,
            Structure.BASTION_REMNANT,
            Structure.BURIED_TREASURE,
            Structure.FORTRESS,
            Structure.JUNGLE_PYRAMID,
            Structure.MINESHAFT,
            Structure.OCEAN_RUIN_COLD,
            Structure.OCEAN_RUIN_WARM,
            Structure.PILLAGER_OUTPOST,
            Structure.RUINED_PORTAL,
            Structure.SHIPWRECK,
            Structure.SWAMP_HUT,
            Structure.STRONGHOLD,
            Structure.TRAIL_RUINS,
            Structure.VILLAGE_SAVANNA,
            Structure.VILLAGE_TAIGA,
            Structure.VILLAGE_DESERT,
            Structure.VILLAGE_PLAINS,
            Structure.VILLAGE_SNOWY,
            Structure.MONUMENT,
            Structure.TRIAL_CHAMBERS
    );


    public static List<Biome> findBiomes(Location location, int radius, int step)
    {
        Set<Biome> uniqueBiomes = new HashSet<>();
        int centreX = location.getBlockX();
        int y = (int) location.getY();
        int centreZ = location.getBlockZ();
        World world = location.getWorld();


        for (int x = centreX - radius; x <= centreX + radius; x += step)
        {
            for (int z = centreZ - radius; z <= centreZ + radius; z += step)
            {
                Biome biomeA = world.getBiome(x,y,z);
                uniqueBiomes.add(biomeA);
            }
        }
        List<Biome> biomes = new ArrayList<>(uniqueBiomes);
        return biomes;
    }

    // input radius is in blocks like the biomes search
    public static List<Structure> findStructures(Location location, int radius) {
        Set<Structure> uniqueStructures = new HashSet<>();
        World world = location.getWorld();
        int chunksRadius = radius / 16;

        for (Structure structure : structures)
        {
            StructureSearchResult result = world.locateNearestStructure(location, structure, chunksRadius, false);
            if (result == null) continue;

            uniqueStructures.add(result.getStructure());
        }

        List<Structure> structures = new ArrayList<>(uniqueStructures);
        return structures;
    }

    public static void showStats()
    {
        Map<UUID, Integer> playerTotalGoalsCompleted = new HashMap<>();
        GameManager.instance.playerGoalsCompleted.forEach((uuid, numOfGoals) ->
        {
            playerTotalGoalsCompleted.put(uuid, playerTotalGoalsCompleted.getOrDefault(uuid,0) + numOfGoals);
        });


        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage("");
            BingoAnnounce(ChatColor.LIGHT_PURPLE + "Made by " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "ayeitszeth");
            Bukkit.broadcastMessage("");
        }, 20L * 5);

        TaskManager.instance.addTaskId(Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BingoAnnounce("");
            Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " [DUNKED GAME STATS] ");
            List<Map.Entry<UUID, Integer>> sortedEntries = new ArrayList<>(GameManager.instance.playerGoalsCompleted.entrySet());
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
                ChatColor teamChatColour = GameManager.instance.teamsManager.getTeamChatColour(player);
                Bukkit.broadcastMessage("    " + teamChatColour + playerName + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + goalsCompleted + ChatColor.WHITE + " goals completed");
            }
            Bukkit.broadcastMessage("");
        }, 20L * 10).getTaskId());
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



    public static void updatePlayerTabListName(Player player) {
        int goalsCompleted = GameManager.instance.playerGoalsCompleted.getOrDefault(player.getUniqueId(), 0);

        ChatColor teamChatColour = GameManager.instance.teamsManager.getTeamChatColour(player);

        if (pvp == SettingsManager.PvP.GLOWING_PVP) {
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
            if (p.getWorld().equals(locationWorld) && GameManager.instance.teamsManager.getTeam(player) != GameManager.instance.teamsManager.getTeam(p)) {
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
