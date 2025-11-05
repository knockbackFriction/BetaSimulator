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
    Random random = new Random();

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Location loc;
        LivingEntity livingEntity;

        switch (event.getEntityType()) {
            case ENDERMAN:
            case HUSK:
            case STRAY:
            case CAVE_SPIDER:
            case WITCH:
                loc = event.getLocation();
                event.setCancelled(true);
                loc.getWorld().spawnEntity(loc, replacementHostiles[random.nextInt(replacementHostiles.length)] );
                break;
            case MULE:
            case DONKEY:
            case PARROT:
            case OCELOT:
            case LLAMA:
            case RABBIT:
            case HORSE:
                loc = event.getLocation();
                event.setCancelled(true);
                loc.getWorld().spawnEntity(loc, replacementPassives[random.nextInt(replacementPassives.length)] );
                break;
            case BAT:
                event.setCancelled(true);
                break;
            // Below are valid mobs, but their equipment among other things have to be changed
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
                break;
            case SPIDER:
                livingEntity = (LivingEntity) event.getEntity();
                livingEntity.getActivePotionEffects().clear();
                livingEntity.getPassengers().clear();
                break;
            case CHICKEN:
                livingEntity = (LivingEntity) event.getEntity();
                livingEntity.getPassengers().clear();
        }
    }
}
