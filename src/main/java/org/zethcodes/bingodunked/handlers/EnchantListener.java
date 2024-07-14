package org.zethcodes.bingodunked.handlers;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.zethcodes.bingodunked.util.BingoUtil;
import org.zethcodes.bingodunked.util.Enchant;

import java.util.*;

public class EnchantListener implements Listener {
    private HashMap<UUID, List<Enchant>> playerEnchants;

    public EnchantListener() {
        this.playerEnchants = new HashMap<>();
    }

    public void Reset()
    {
        playerEnchants = new HashMap<>();
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (BingoUtil.gameState == BingoUtil.GameState.FINISHED) return;
        Player player = event.getEnchanter();
        List<Enchant> latestEnchants = new ArrayList<>();
        Map<Enchantment, Integer> encMap = event.getEnchantsToAdd();
        for (Enchantment enchantment : encMap.keySet())
        {
            latestEnchants.add(new Enchant(enchantment,encMap.get(enchantment)));
        }
        playerEnchants.put(player.getUniqueId(), latestEnchants);
    }

    public boolean hasPlayerGotEnchant(Player player, Enchant enchant) {
        List<Enchant> playerEncList = playerEnchants.get(player.getUniqueId());

        for (Enchant enc : playerEncList)
        {
            if (enc.enchantment.equals(enchant.enchantment) && enc.level <= enchant.level)
            {
                if (BingoUtil.DEBUG) Bukkit.getLogger().info(player + " got the enchant: " + enc);
                return true;
            }
        }
        //if (BingoUtil.DEBUG) Bukkit.getLogger().info(player + " does not have the enchant: " + enchant);
        return false;
    }
}

