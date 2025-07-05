package org.zethcodes.bingodunked.managers;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.zethcodes.bingodunked.goals.*;
import org.zethcodes.bingodunked.listeners.BreakBlockTypeListener;
import org.zethcodes.bingodunked.listeners.TravelListener;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.*;

import static org.bukkit.Bukkit.getServer;
import static org.zethcodes.bingodunked.managers.GameManager.*;
import static org.zethcodes.bingodunked.util.BingoUtil.wrapAndColorLore;

public class BoardManager {
    // INV
    Inventory BingoCard;
    List<Integer> validSlots = new ArrayList<>();
    public HashMap<Integer, Goal> goals = new HashMap<>();

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

    public TravelListener.TYPE activeTravelType = null;
    public Set<BreakBlockTypeListener.BlockType> activeBlockTypes = new HashSet<>();

    GoalManager goalManager = new GoalManager();
    int stage = 0;

    int animateCounter = 0;

    public void BoardSetUp() {
        BingoCard = Bukkit.createInventory(null, 27, "Bingo Card");

        redBoard = new boolean[3][3];
        blueBoard = new boolean[3][3];
        greenBoard = new boolean[3][3];
        yellowBoard = new boolean[3][3];
        orangeBoard = new boolean[3][3];
        purpleBoard = new boolean[3][3];
        cyanBoard = new boolean[3][3];
        brownBoard = new boolean[3][3];

        activeTravelType = null;
        activeBlockTypes.clear();

        stage = 0;
        goalManager.ClearGoals();
        goalManager.SetBiomeGoals();
        goalManager.AddGoals(stage);

        SetCardsInv();

        validSlots.clear();
        for (int col = 0; col < 3; ++col) {
            for (int row = 0; row < 3; ++row) {
                validSlots.add(row * 9 + col + 3);
            }
        }
    }

    public void FillCard() {
        for (int slot : validSlots) {
            newGoal(slot);
        }

        AnimateBingoCard();
    }

    //region Goal Functions

    public void newGoal(int slot) {
        if (goalManager.availableGoals.isEmpty()) {
            goalManager.ClearGoals();
            goalManager.AddGoals(stage);
        }

        goals.remove(slot);

        int col = (slot % 9) - 3;
        int row = slot / 9;

        Random random = new Random();
        int ranGoal = random.nextInt(goalManager.availableGoals.size());
        Goal goal = goalManager.availableGoals.get(ranGoal);
        goalManager.availableGoals.remove(goal);

        HashSet<Class<? extends Goal>> currentGoalTypes = new HashSet<>();

        for (int i = 0; i < 9; ++i) {
            Goal curGoal = goals.get(i);
            if (curGoal == null) continue;

            Class<? extends Goal> curClass = curGoal.getClass();
            if (curGoal instanceof CollectItemGoal && !(curGoal instanceof CollectColouredItemGoal)) continue;

            currentGoalTypes.add(curClass);
        }

        while ((currentGoalTypes.contains(goal.getClass()) ||
                (goals.containsValue(goal))) &&
                SettingsManager.difficulty == SettingsManager.Difficulty.NORMAL) {

            if (goalManager.availableGoals.isEmpty()) {
                goalManager.ClearGoals();
                goalManager.AddGoals(stage);
            }

            ranGoal = random.nextInt(goalManager.availableGoals.size());
            goal = goalManager.availableGoals.get(ranGoal);
            goalManager.availableGoals.remove(goal);
        }

        if (goal instanceof TravelGoal) {
            activeTravelType = ((TravelGoal) goal).type;
        } else if (goal instanceof BreakBlockTypeGoal) {
            activeBlockTypes.add(((BreakBlockTypeGoal) goal).requiredBlock);
        }

        ItemStack item = goal.getItem();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = wrapAndColorLore(goal.getName(), 30, ChatColor.DARK_PURPLE);
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Goal " + (col + row * 3 + 1));
        item.setItemMeta(meta);

        goals.put(slot, goal);
        BingoCard.setItem(slot, item);
        BingoUtil.BingoAnnounce("The new goal is " + ChatColor.BOLD + goal.getName() + "!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (goal instanceof CollectItemGoal || goal instanceof ExperienceGoal ||
                    goal instanceof CompleteAdvancementGoal || goal instanceof BreakBlockTypeGoal ||
                    goal instanceof TravelGoal) {
                if (goal.isComplete(player)) {
                    completeGoal(slot, instance.teamsManager.getTeam(player), player);
                }
            }
        }
    }

    public void completeGoal(int slot, TeamsManager.Team team, Player player) {
        if (gameState == GameManager.GameState.FINISHED) {
            return;
        }

        if (team == TeamsManager.Team.NONE) {
            if (GameManager.DEBUG) Bukkit.getLogger().info(player + " is not on a team");
            return;
        }

        int col = (slot % 9) - 3;
        int row = slot / 9;

        Goal goal = goals.get(slot);
        GameManager.instance.incrementPlayerGoalsCompleted(player);

        boolean[][] teamBoard = GetTeamBoard(team);
        ChatColor teamColor = instance.teamsManager.getTeamChatColour(player);
        ItemStack teamWool = instance.teamsManager.getTeamWool(team);
        Sound teamSound = Sound.BLOCK_NOTE_BLOCK_PLING;
        Sound otherTeamSound = Sound.BLOCK_BELL_USE;
        teamBoard[col][row] = true;

        if (isGoalToBeDunked(col, row, team)) {
            ResetBoardsAtSlot(col, row);

            String playerName;
            try {
                playerName = player.getName();
            } catch (Exception ex) {
                playerName = "<left server>";
            }

            String message = teamColor + playerName + ChatColor.WHITE + " has dunked the goal " + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + goal.getName() + ChatColor.WHITE + "!";
            BingoUtil.BroadcastPlayerTitle(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "DUNKED!", teamColor + goal.getName());
            BingoUtil.BingoAnnounce(message);
            BingoUtil.BingoAnnounce("");

            if (goal instanceof TravelGoal) {
                activeTravelType = null;
            } else if (goal instanceof BreakBlockTypeGoal) {
                activeBlockTypes.remove(((BreakBlockTypeGoal) goal).requiredBlock);
            }

            newGoal(slot);

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 3f, 2f);
            }

            instance.CheckOverTime();

            return;
        }

        for (Map.Entry<UUID, TeamsManager.Team> entry : instance.teamsManager.GetTeamMap().entrySet()) {
            Player p = getServer().getPlayer(entry.getKey());
            TeamsManager.Team playerTeam = entry.getValue();

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
        } catch (Exception ex) {
            playerName = "<left server>";
        }

        String message = teamColor + playerName + ChatColor.WHITE + " has completed " + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + goal.getName();
        BingoUtil.BroadcastPlayerTitle(ChatColor.LIGHT_PURPLE + "GOAL!", teamColor + goal.getName());
        BingoUtil.BingoAnnounce(message);

        if (checkBingo(team)) {
            GameManager.instance.BingoEnd(team);
        }

        instance.CheckOverTime();
    }

    public void goalAutoComplete(Player player, Class goalType) {
        TeamsManager.Team team = instance.teamsManager.getTeam(player);
        if (team == TeamsManager.Team.NONE) return;

        for (int slot : validSlots) {
            Goal goal = goals.get(slot);
            if (goalType.isInstance(goal)) {
                int col = (slot % 9) - 3;
                int row = slot / 9;
                boolean teamCompletedGoalAlready = hasTeamCompletedGoal(team, col, row);
                if (!teamCompletedGoalAlready) {
                    if (goal.isComplete(player)) {
                        completeGoal(slot, team, player);
                        return;
                    }
                }
            }
        }
    }

    public void craftGoalAutoComplete(Player player, ItemStack item) {
        TeamsManager.Team team = instance.teamsManager.getTeam(player);
        if (team == TeamsManager.Team.NONE) return;

        for (int slot : validSlots) {
            Goal goal = goals.get(slot);
            if (goal instanceof CollectItemGoal) {
                int col = (slot % 9) - 3;
                int row = slot / 9;
                boolean teamCompletedGoalAlready = hasTeamCompletedGoal(team, col, row);
                if (!teamCompletedGoalAlready) {
                    if (((CollectItemGoal) goal).isCompleteItem(item, player)) {
                        completeGoal(slot, team, player);
                        return;
                    }
                }
            }
        }
    }

    //endregion

    //region Bingo Checks

    public void ResetBoardsAtSlot(int col, int row) {
        redBoard[col][row] = false;
        blueBoard[col][row] = false;
        greenBoard[col][row] = false;
        yellowBoard[col][row] = false;
        orangeBoard[col][row] = false;
        purpleBoard[col][row] = false;
        cyanBoard[col][row] = false;
        brownBoard[col][row] = false;
    }

    public void SetCardsInv() {
        ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta blackMeta = black.getItemMeta();
        blackMeta.setDisplayName(ChatColor.BLACK + " ");
        black.setItemMeta(blackMeta);
        for (int i = 0; i < 3; ++i) {
            BingoCard.setItem(i * 9, black);
            BingoCard.setItem(i * 9 + 1, black);
            BingoCard.setItem(i * 9 + 2, black);
            BingoCard.setItem(i * 9 + 6, black);
            BingoCard.setItem(i * 9 + 7, black);
            BingoCard.setItem(i * 9 + 8, black);
        }
    }

    public boolean hasTeamCompletedGoal(TeamsManager.Team team, int col, int row) {
        if (team == TeamsManager.Team.NONE) return false;

        switch (team) {
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

    private boolean isGoalToBeDunked(int col, int row, TeamsManager.Team currentTeam) {
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

    public boolean checkBingo(TeamsManager.Team team) {
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

        return board[2][0] && board[1][1] && board[0][2];
    }

    //endregion

    //region Other

    public boolean[][] GetTeamBoard(TeamsManager.Team team) {
        switch (team) {
            case RED:
                return redBoard;
            case BLUE:
                return blueBoard;
            case GREEN:
                return greenBoard;
            case YELLOW:
                return yellowBoard;
            case ORANGE:
                return orangeBoard;
            case PURPLE:
                return purpleBoard;
            case CYAN:
                return cyanBoard;
            case BROWN:
                return brownBoard;
            default:
                return new boolean[3][3];
        }
    }

    public void AnimateBingoCard() {
        if (gameState == GameManager.GameState.STARTED) {
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

                            if (isGoalToBeDunked(col, row, TeamsManager.Team.NONE) || items.size() <= 1) {
                                continue;
                            }

                            ItemStack item = new ItemStack(items.get(animateCounter % items.size()), goal.getItem().getAmount());
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = wrapAndColorLore(goal.getName(), 30, ChatColor.DARK_PURPLE);
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
                        AnimateBingoCard();

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    public void OpenInv(Player player) {
        player.openInventory(BingoCard);
    }

    public void incrementStage()
    {
        stage++;
        goalManager.AddGoals(stage);

        for (Map.Entry<UUID, TeamsManager.Team> entry : instance.teamsManager.GetTeamMap().entrySet()) {
            Player p = getServer().getPlayer(entry.getKey());
            if (p == null) continue;

            for (Biome biome : BingoUtil.findBiomes(p.getLocation(), 50, 20))
            {
                goalManager.AddBiomeGoals(biome);
            }

            for (Structure structure : BingoUtil.findStructures(p.getLocation(), 64))
            {
                goalManager.AddStructureGoals(structure);
            }
        }
    }

    //endregion


}
