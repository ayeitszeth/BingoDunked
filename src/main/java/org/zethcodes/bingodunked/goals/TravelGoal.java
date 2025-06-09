package org.zethcodes.bingodunked.goals;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.listeners.TravelListener;

public class TravelGoal extends Goal {
    private double distance;
    private TravelListener travelListener;
    public TravelListener.TYPE type;

    public TravelGoal(String name, ItemStack item, double distance, TravelListener.TYPE type, TravelListener travelListener) {
        super(name, item);
        this.distance = distance;
        this.travelListener = travelListener;
        this.type = type;
    }

    @Override
    public boolean isComplete(Player player) {
        return travelListener.hasPlayerTravelledEnough(player, distance,type);
    }
}
