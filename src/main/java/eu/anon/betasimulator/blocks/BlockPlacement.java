package eu.anon.betasimulator.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlacement implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Material mat = event.getBlock().getType();
        switch (mat) {
            case WOOD_STEP: // slabs can only be at the bottom half of the block
                event.getBlock().setData((byte) 0);
                break;
            case STEP: // slabs can only be at the bottom half of the block
                byte dataValue = event.getBlock().getData();
                if (dataValue > 3) {
                    event.getBlock().setData((byte) (dataValue % 4));
                }
                break;
            case SUGAR_CANE_BLOCK:
                Location sugarLoc = event.getBlock().getLocation();
                int x = sugarLoc.getBlockX();
                int supportBlock = sugarLoc.getBlockY() - 1;
                int z = sugarLoc.getBlockZ();
                // sugar canes can not be placed on sand
                if (sugarLoc.getBlockY() == 0 || event.getBlock().getWorld().getBlockAt( x, supportBlock, z ).getType() == Material.SAND) {
                    event.setCancelled(true);
                }
                break;
        }
    }
}
