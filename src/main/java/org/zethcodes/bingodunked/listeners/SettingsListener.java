package org.zethcodes.bingodunked.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.zethcodes.bingodunked.managers.GameManager;
import org.zethcodes.bingodunked.managers.SettingsManager;
import org.zethcodes.bingodunked.util.BingoUtil;

public class SettingsListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        String cardName = event.getView().getTitle();
        if (cardName.equals("Bingo Settings")) {
            event.setCancelled(true);

            if (GameManager.gameState == GameManager.GameState.STARTED ||
                    GameManager.gameState == GameManager.GameState.LOADING) return;

            int slot = event.getSlot();
            SettingsManager.Mode mode = null;
            SettingsManager.Difficulty difficulty = null;
            SettingsManager.PvP pvp = null;

            switch (slot)
            {
                case 12: // enable teams
                    mode = SettingsManager.Mode.TEAM;
                    break;
                case 15: // enable ffa
                    mode = SettingsManager.Mode.FFA;
                    break;
                case 21: // normal difficulty
                    difficulty = SettingsManager.Difficulty.NORMAL;
                    break;
                case 24: // insane difficulty
                    difficulty = SettingsManager.Difficulty.INSANE;
                    break;
                case 28: // no pvp
                    pvp = SettingsManager.PvP.NOPVP;
                    break;
                case 30: // pvp
                    pvp = SettingsManager.PvP.PVP;
                    break;
                case 33: // glowing pvp
                    pvp = SettingsManager.PvP.GLOWING_PVP;
                    break;
                case 35: // tracking pvp
                    pvp = SettingsManager.PvP.TRACKING_PVP;
                    break;
            }

            SettingsManager.instance.updateGameMode(mode);
            SettingsManager.instance.updateDifficulty(difficulty);
            SettingsManager.instance.updatePvp(pvp);
        }
    }
}
