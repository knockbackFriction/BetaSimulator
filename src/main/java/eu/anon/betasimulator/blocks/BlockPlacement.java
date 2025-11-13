package eu.anon.betasimulator.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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

    private boolean hasASolidNeighbour(Block block) {
        World w = block.getWorld();
        Location l = block.getLocation();
        Material neighbour;

        l.add(0,0,-1);
        neighbour = w.getBlockAt(l).getType();
        if (neighbour.isSolid() && neighbour.isOccluding()) return true;

        l.add(0,0,2);
        neighbour = w.getBlockAt(l).getType();
        if (neighbour.isSolid() && neighbour.isOccluding()) return true;

        l.add(-1,0,-1);
        neighbour = w.getBlockAt(l).getType();
        if (neighbour.isSolid() && neighbour.isOccluding()) return true;

        l.add(2,0,0);
        neighbour = w.getBlockAt(l).getType();
        return neighbour.isSolid() && neighbour.isOccluding();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        byte dataValue;
        Material mat = event.getBlock().getType();
        switch (mat) {
            case WOOD_STEP: // slabs can only be at the bottom half of the block
                event.getBlock().setData((byte) 0);
                break;
            case STEP: // slabs can only be at the bottom half of the block
                dataValue = event.getBlock().getData();
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
                break;
            case STONE_BUTTON: // do not place top/bottom buttons
                if (event.getBlockPlaced().getData() == 0 || event.getBlockPlaced().getData() == 5) {
                    event.setCancelled(true);
                }
                break;
            case LEVER:
                dataValue = event.getBlock().getData();
                if (dataValue == 0 || dataValue == 7 || dataValue == 8 || dataValue == 15) {
                    event.setCancelled(true);
                }
                break;
            case FENCE:
                Location fenceLoc = event.getBlock().getLocation();
                // fences can not be placed on non-solid blocks
                if (fenceLoc.getBlockY() == 0 || !event.getBlock().getWorld().getBlockAt(
                        fenceLoc.getBlockX(), fenceLoc.getBlockY() - 1, fenceLoc.getBlockZ()).getType().isSolid()) {
                    event.setCancelled(true);
                }
                break;
            case TRAP_DOOR:
                if ( !hasASolidNeighbour(event.getBlock()) ) {
                    event.setCancelled(true);
                }
                break;
        }
    }
}
