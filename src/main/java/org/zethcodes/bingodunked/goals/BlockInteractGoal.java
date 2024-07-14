package org.zethcodes.bingodunked.goals;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zethcodes.bingodunked.handlers.BlockInteractListener;

public class BlockInteractGoal extends Goal {
    private final Material blockType;
    private final BlockInteractListener blockInteractListener;

    public BlockInteractGoal(String name, ItemStack item, Material blockType, BlockInteractListener blockInteractListener) {
        super(name, item);
        this.blockType = blockType;
        this.blockInteractListener = blockInteractListener;
    }

    @Override
    public boolean isComplete(Player player) {
        return blockInteractListener.isBlockInDesiredState(player, blockType);
    }
}
