package org.zethcodes.bingodunked.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.zethcodes.bingodunked.goals.*;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.ArrayList;
import java.util.List;

import static org.zethcodes.bingodunked.managers.GameManager.gameState;
import static org.zethcodes.bingodunked.managers.GameManager.plugin;
import static org.zethcodes.bingodunked.util.BingoUtil.wrapAndColorLore;

public class BoardManager {
    // INV
    Inventory BingoCard;
    List<Integer> validSlots = new ArrayList<>();

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

    int animateCounter = 0;

    public void BoardSetUp()
    {
        BingoCard = Bukkit.createInventory(null, 27, "Bingo Card");

        redBoard = new boolean[3][3];
        blueBoard = new boolean[3][3];
        greenBoard = new boolean[3][3];
        yellowBoard = new boolean[3][3];
        orangeBoard = new boolean[3][3];
        purpleBoard = new boolean[3][3];
        cyanBoard = new boolean[3][3];
        brownBoard = new boolean[3][3];

        validSlots.clear();
        for (int col = 0; col < 3; ++col) {
            for (int row = 0; row < 3; ++row) {
                validSlots.add(row * 9 + col + 3);
            }
        }
    }

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
        for (int i = 0; i < 3; ++i)
        {
            BingoCard.setItem(i * 9, black);
            BingoCard.setItem(i * 9 + 1, black);
            BingoCard.setItem(i * 9 + 2, black);
            BingoCard.setItem(i * 9 + 6, black);
            BingoCard.setItem(i * 9 + 7, black);
            BingoCard.setItem(i * 9 + 8, black);
        }
    }

    public boolean hasTeamCompletedGoal(BingoUtil.Team team, int col, int row)
    {
        if (team == BingoUtil.Team.NONE) return false;

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

    private boolean isGoalToBeDunked(int col, int row, BingoUtil.Team currentTeam) {
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

    public void AnimateBingoCard() {
        if (gameState == BingoUtil.GameState.STARTED) {
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

                            if (isGoalToBeDunked(col, row, BingoUtil.Team.NONE) || items.size() <= 1)
                            {
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
}
