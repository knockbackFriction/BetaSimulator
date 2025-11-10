package eu.anon.betasimulator.blocks;

import eu.anon.betasimulator.BetaSimulator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlacement implements Listener {
    private float wrapDegrees(float value) {
        value = value % 360.0F;
        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;
        return value;
    }

    private byte determineBlockFacing(float yaw) {
        yaw = wrapDegrees(yaw);
        if (yaw < -135.0f || yaw > 135.0f) {
            return 3;
        } else if (yaw > 45.0f) {
            return 5;
        } else if (yaw > -45.0f) {
            return 2;
        } else {
            return 4;
        }
    }

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
            case DISPENSER:
                if (event.getBlockPlaced().getData() > 1) return;
                event.getBlockPlaced().setData(determineBlockFacing(event.getPlayer().getLocation().getYaw()));
        }
    }
}
