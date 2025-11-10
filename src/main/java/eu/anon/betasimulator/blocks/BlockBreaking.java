package eu.anon.betasimulator.blocks;

import eu.anon.betasimulator.BetaSimulator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class BlockBreaking implements Listener {
    boolean simulate_b166 = BetaSimulator.instance.configuration.simulate_b166;

    private Location centerLocation(Location l) {
        l.add(0.5, 0.0, 0.5);
        return l;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setExpToDrop(0);

        switch (event.getBlock().getType()) {
            case TNT:
                if (!simulate_b166) return;
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);

                Location tntLoc = centerLocation(event.getBlock().getLocation());
                event.getBlock().getWorld().spawnEntity(centerLocation(tntLoc), EntityType.PRIMED_TNT);
                break;
            case LEAVES: // Apples do not drop in Beta
                byte saplingType = (byte) ((event.getBlock().getData()+1) % 3);

                if (ThreadLocalRandom.current().nextInt(20) == 1) {
                    event.getBlock().getWorld().dropItem(
                            centerLocation(event.getBlock().getLocation()),
                            new ItemStack(Material.SAPLING, 1, (short) 0, saplingType)
                    );
                }
                event.setDropItems(false);
                break;
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) { // Apples do not drop in Beta
        byte saplingType = (byte) ((event.getBlock().getData()+1) % 3);

        if (ThreadLocalRandom.current().nextInt(20) == 1) {
            event.getBlock().getWorld().dropItem(
                    centerLocation(event.getBlock().getLocation()),
                    new ItemStack(Material.SAPLING, 1, (short) 0, saplingType)
            );
        }
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
    }
}
