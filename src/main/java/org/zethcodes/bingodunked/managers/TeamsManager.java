package org.zethcodes.bingodunked.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.*;

public class TeamsManager {
    public enum Team {RED, BLUE, GREEN, YELLOW, ORANGE, PURPLE, CYAN, BROWN, NONE};

    // WOOL
    ItemStack redWool = new ItemStack(Material.RED_WOOL, 1);
    ItemStack blueWool = new ItemStack(Material.BLUE_WOOL, 1);
    ItemStack greenWool = new ItemStack(Material.LIME_WOOL, 1);
    ItemStack yellowWool = new ItemStack(Material.YELLOW_WOOL, 1);
    ItemStack orangeWool = new ItemStack(Material.ORANGE_WOOL, 1);
    ItemStack purpleWool = new ItemStack(Material.PURPLE_WOOL, 1);
    ItemStack cyanWool = new ItemStack(Material.CYAN_WOOL, 1);
    ItemStack brownWool = new ItemStack(Material.BROWN_WOOL, 1);

    Map<UUID, Team> TeamMap = new HashMap<>();

    //region Setup

    public void FFATeamsSetUp()
    {
        TeamMap = new HashMap<>();
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        int numOfPlayers = players.length;
        if (numOfPlayers <= 8) {
            Team[] teams = Team.values();

            for (int i = 0; i < numOfPlayers; ++i) {
                TeamMap.put(players[i].getUniqueId(), teams[i]);
//                updatePlayerTabListName(players[i]);
            }

        } else {
            BingoUtil.BingoAnnounce("This game mode only is functional with less than 8 players... Start Aborting...");
        }
    }

    //endregion

    //region Team Joining

    public void JoinTeam(Player player, Team team)
    {
//        if (gameMode == BingoUtil.Mode.FFA)
//        {
//            BingoUtil.BingoWhisper(player,"There is no need to join teams in FFA mode.");
//            return;
//        }

        String playerName;

        try {
            playerName = player.getName();
        } catch (Exception ex)
        {
            playerName = "<left server>";
        }

        TeamMap.put(player.getUniqueId(),team);
        BingoUtil.BingoAnnounce(playerName + ChatColor.WHITE + " has joined the " + getTeamChatColour(team) + ChatColor.BOLD + team.name().charAt(0) + team.name().substring(1).toLowerCase() + " Team" + ChatColor.WHITE + ".");
//        updatePlayerTabListName(player);
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
//                updatePlayerTabListName(players[i]);
            }

        } else
        {
            player.setGameMode(GameMode.SPECTATOR);
            BingoUtil.BingoWhisper(player,"There are too many players already in the game... You will spectate this round.");
        }
    }

    //endregion

    //region Getters/Setters

    public Map<UUID, Team> GetTeamMap() { return TeamMap; }

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

    public ItemStack getTeamWool(Team team)
    {
        switch (team) {
            case RED:
                return redWool;
            case BLUE:
                return blueWool;
            case GREEN:
                return greenWool;
            case YELLOW:
                return yellowWool;
            case ORANGE:
                return orangeWool;
            case PURPLE:
                return purpleWool;
            case CYAN:
                return cyanWool;
            case BROWN:
                return brownWool;
            default:
                return new ItemStack(Material.WHITE_WOOL, 1);
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

    //endregion

    //region DEBUG

    public void PrintTeams()
    {
        for (Map.Entry<UUID, Team> entry : TeamMap.entrySet()) {
            UUID playerUUID = entry.getKey();
            Team team = entry.getValue();
            if (GameManager.DEBUG) Bukkit.getLogger().info("Player UUID: " + playerUUID + ", Team: " + team);
        }
    }

    //endregion
}
