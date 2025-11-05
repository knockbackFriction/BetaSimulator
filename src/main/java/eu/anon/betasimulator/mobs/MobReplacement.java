package eu.anon.betasimulator.mobs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MobReplacement implements Listener {
    EntityType[] replacementHostiles = {EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.CREEPER};
    EntityType[] replacementPassives = {EntityType.PIG, EntityType.SHEEP, EntityType.COW, EntityType.CHICKEN};
    ItemStack bow = new ItemStack(Material.BOW);

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Location loc;
        LivingEntity livingEntity;

        switch (event.getEntityType()) {
            case ENDERMAN:
            case HUSK:
            case STRAY:
            case WITCH:
                loc = event.getLocation();
                event.setCancelled(true);
                loc.getWorld().spawnEntity(loc, replacementHostiles[new Random().nextInt(replacementHostiles.length)] );
                break;
            case MULE:
            case DONKEY:
            case PARROT:
            case HORSE:
                loc = event.getLocation();
                event.setCancelled(true);
                loc.getWorld().spawnEntity(loc, replacementPassives[new Random().nextInt(replacementPassives.length)] );
                break;
            case BAT:
                event.setCancelled(true);
                break;
            //below is dedicated to replacing/removing equipment and baby state from a few mobs
            case SKELETON:
                livingEntity = (LivingEntity) event.getEntity();
                livingEntity.getEquipment().clear();
                livingEntity.getEquipment().setItemInMainHand(bow);
                break;
            case ZOMBIE:
                livingEntity = (LivingEntity) event.getEntity();
                livingEntity.getEquipment().clear();
                Zombie zombieEntity = (Zombie) livingEntity;
                zombieEntity.setBaby(false);
        }
    }
}
