package eu.anon.betasimulator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Fishing implements Listener {
    ItemStack rawFish = new ItemStack(Material.RAW_FISH);

    private static Vector calcRodVelocity(LivingEntity livingEntity, Location source) {
        float angle = (float) Math.toDegrees(Math.atan2(source.getX() - livingEntity.getLocation().getX(), source.getZ() - livingEntity.getLocation().getZ()));

        double finalX = Math.sin(angle * 3.1415927F / 180.0F) * 0.4f;
        double finalZ = Math.cos(angle * 3.1415927F / 180.0F) * 0.4f;

        Vector velo = livingEntity.getVelocity();
        velo.setX((velo.getX() / 2.0f) - finalX);
        velo.setY(0.4f);
        velo.setZ((velo.getZ() / 2.0f) - finalZ);

        return velo;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getHitEntity() instanceof LivingEntity)) return;
        if (event.getHitEntity() instanceof Player) return; //might add config entry to allow rods to work on players

        if (!(event.getEntity() instanceof FishHook)) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        LivingEntity victim = (LivingEntity) event.getHitEntity();
        if (victim.getNoDamageTicks() > victim.getMaximumNoDamageTicks() / 2f) return;

        victim.setVelocity( calcRodVelocity(victim, ((Player) event.getEntity().getShooter()).getLocation()) );
        victim.damage(0.001d);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        event.setExpToDrop(0);
        if (event.getCaught() == null) return;

        switch (event.getState()) {
            case CAUGHT_ENTITY:
                if (event.getCaught() instanceof Player || event.getCaught() instanceof Item || !(event.getCaught() instanceof LivingEntity)) {
                    event.setCancelled(true);
                    event.getHook().remove();
                    return;
                } else {
                    if (!(event.getHook().getShooter() instanceof Player)) return;
                    Player shooter = (Player) event.getHook().getShooter();
                    Entity caught = event.getCaught();
                    float heightDiff = (float) (shooter.getLocation().getY() - caught.getLocation().getY()) / 24f;
                    if (heightDiff < 0.0f) return;

                    Vector vector = caught.getVelocity();
                    vector.setY(heightDiff);
                    caught.setVelocity(vector);
                }
                break;
            case CAUGHT_FISH:
                if (event.getCaught() instanceof Item) {
                    Item caughtItem = (Item) (event.getCaught());
                    caughtItem.setItemStack(rawFish);
                }
                break;
        }
    }
}
