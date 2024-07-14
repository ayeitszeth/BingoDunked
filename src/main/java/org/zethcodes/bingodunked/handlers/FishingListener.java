package org.zethcodes.bingodunked.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.zethcodes.bingodunked.util.BingoUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class FishingListener implements Listener {
    private HashMap<UUID, Material> playerFishItemsCaught;
    private HashSet<Material> treasure = new HashSet<>();
    private HashSet<Material> junk = new HashSet<>();

    public FishingListener() {
        this.playerFishItemsCaught = new HashMap<>();
        treasure.add(Material.BOW);
        treasure.add(Material.ENCHANTED_BOOK);
        treasure.add(Material.FISHING_ROD);
        treasure.add(Material.NAME_TAG);
        treasure.add(Material.NAUTILUS_SHELL);
        treasure.add(Material.SADDLE);
        junk.add(Material.LILY_PAD);
        junk.add(Material.BOWL);
        junk.add(Material.LEATHER);
        junk.add(Material.LEATHER_BOOTS);
        junk.add(Material.ROTTEN_FLESH);
        junk.add(Material.STICK);
        junk.add(Material.STRING);
        junk.add(Material.POTION);
        junk.add(Material.BONE);
        junk.add(Material.INK_SAC);
        junk.add(Material.TRIPWIRE_HOOK);
    }

    public void Reset()
    {
        playerFishItemsCaught = new HashMap<>();
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            playerFishItemsCaught.put(player.getUniqueId(), ((Item) event.getCaught()).getItemStack().getType());
        }
    }

    public boolean hasPlayerGotFishItem(Player player, Material caughtItem) {
        if (BingoUtil.DEBUG) Bukkit.getLogger().info(player + " most recent item caught from fishing is: " + playerFishItemsCaught.get(player.getUniqueId()));
        if (caughtItem.equals(Material.NAUTILUS_SHELL))
        {
            return treasure.contains(playerFishItemsCaught.get(player.getUniqueId()));
        } else if (caughtItem.equals(Material.LEATHER_BOOTS))
        {
            return junk.contains(playerFishItemsCaught.get(player.getUniqueId()));
        }

        return caughtItem.equals(playerFishItemsCaught.get(player.getUniqueId()));
    }
}
