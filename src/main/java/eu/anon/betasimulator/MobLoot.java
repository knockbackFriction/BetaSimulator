package eu.anon.betasimulator;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MobLoot implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0);

        if (event.getEntityType()== EntityType.SKELETON || event.getEntityType()==EntityType.CREEPER || event.getEntityType()==EntityType.SQUID || event.getEntityType()==EntityType.SLIME) {
            return;
        }
        event.getDrops().clear();

        switch (event.getEntityType()) {
            case SHEEP:
                Sheep sheep = (Sheep) event.getEntity();
                if (sheep.isSheared()) return;
                ItemStack wools = new ItemStack(Material.WOOL, ThreadLocalRandom.current().nextInt(1,3), sheep.getColor().getWoolData());
                event.getDrops().add(wools);
                break;
            case COW:
                event.getDrops().add(new ItemStack(Material.LEATHER, ThreadLocalRandom.current().nextInt(1,4)));
                break;
            case CHICKEN:
                event.getDrops().add(new ItemStack(Material.FEATHER, ThreadLocalRandom.current().nextInt(1, 4)));
                break;
            case SPIDER:
                event.getDrops().add(new ItemStack(Material.STRING, ThreadLocalRandom.current().nextInt(1, 4)));
                break;
            case ZOMBIE:
                event.getDrops().add(new ItemStack(Material.FEATHER, ThreadLocalRandom.current().nextInt(1,3)));
                break;
            case GHAST:
                event.getDrops().add(new ItemStack(Material.SULPHUR, ThreadLocalRandom.current().nextInt(1,4)));
                break;
            case PIG_ZOMBIE:
                for (byte i = 0; i < ThreadLocalRandom.current().nextInt(1,3); i++) {
                    ItemStack pork = new ItemStack(Material.GRILLED_PORK);
                    ItemMeta porkMeta = pork.getItemMeta();
                    porkMeta.setDisplayName(UUID.randomUUID().toString());
                    pork.setItemMeta(porkMeta);
                    event.getDrops().add(pork);
                }
                break;
            case PIG:
                for (byte i = 0; i < ThreadLocalRandom.current().nextInt(1,3); i++) {
                    ItemStack pork = new ItemStack(Material.PORK);
                    if (event.getEntity().getFireTicks() > 0) {
                        pork.setType(Material.GRILLED_PORK);
                    }
                    ItemMeta porkMeta = pork.getItemMeta();
                    porkMeta.setDisplayName(UUID.randomUUID().toString());
                    pork.setItemMeta(porkMeta);
                    event.getDrops().add(pork);
                }

        }
    }
}
