package org.zethcodes.bingodunked.goals;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.listeners.BreakBlockTypeListener;

public class BreakBlockTypeGoal extends Goal {
    public final BreakBlockTypeListener.BlockType requiredBlock;
    private final BreakBlockTypeListener breakBlockTypeListener;
    private final int count;

    public BreakBlockTypeGoal(String name, ItemStack item, BreakBlockTypeListener.BlockType requiredBlock, int count, BreakBlockTypeListener breakBlockTypeListener) {
        super(name, item);
        this.requiredBlock = requiredBlock;
        this.breakBlockTypeListener = breakBlockTypeListener;
        this.count = count;
    }

    @Override
    public boolean isComplete(Player player) {
        return breakBlockTypeListener.hasPlayerBrokeEnoughBlocks(player, requiredBlock,count);
    }
}
