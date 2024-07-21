package org.zethcodes.bingodunked.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.zethcodes.bingodunked.util.BingoUtil;

public class SettingsListener implements Listener {

    BingoUtil bingoUtil;

    public SettingsListener(BingoUtil bingoUtil)
    {
        this.bingoUtil = bingoUtil;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        String cardName = event.getView().getTitle();
        if (cardName.equals("Bingo Settings")) {
            event.setCancelled(true);

            if (BingoUtil.gameState == BingoUtil.GameState.STARTED ||
                    BingoUtil.gameState == BingoUtil.GameState.LOADING) return;

            int slot = event.getSlot();
            BingoUtil.Mode mode = null;
            BingoUtil.Difficulty difficulty = null;
            BingoUtil.PvP pvp = null;

            switch (slot)
            {
                case 12: // enable teams
                    mode = BingoUtil.Mode.TEAM;
                    break;
                case 15: // enable ffa
                    mode = BingoUtil.Mode.FFA;
                    break;
                case 21: // normal difficulty
                    difficulty = BingoUtil.Difficulty.NORMAL;
                    break;
                case 24: // insane difficulty
                    difficulty = BingoUtil.Difficulty.INSANE;
                    break;
                case 28: // no pvp
                    pvp = BingoUtil.PvP.NOPVP;
                    break;
                case 30: // pvp
                    pvp = BingoUtil.PvP.PVP;
                    break;
                case 33: // glowing pvp
                    pvp = BingoUtil.PvP.GLOWING_PVP;
                    break;
                case 35: // tracking pvp
                    pvp = BingoUtil.PvP.TRACKING_PVP;
                    break;
            }

            bingoUtil.updateGameMode(mode);
            bingoUtil.updateDifficulty(difficulty);
            bingoUtil.updatePvp(pvp);
        }
    }
}
