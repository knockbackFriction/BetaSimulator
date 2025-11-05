package eu.anon.betasimulator;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageChanger implements Listener {
    boolean extraBaseDmg = BetaSimulator.instance.configuration.barehandDamageFullHeart;

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity) || !(event.getDamager() instanceof Player)) return;
        LivingEntity damager = (LivingEntity) event.getDamager();

        Material weapon = damager.getEquipment().getItemInMainHand().getType();
        int damage = extraBaseDmg ? 1 : 0;

        switch (weapon) {
            case DIAMOND_SWORD:
                damage += 10;
                break;
            case IRON_SWORD:
                damage += 8;
                break;
            case STONE_SWORD:
            case DIAMOND_AXE:
                damage += 6;
                break;
            case WOOD_SWORD:
            case GOLD_SWORD:
            case STONE_AXE:
                damage += 4;
                break;
            case IRON_AXE:
                damage += 5;
                break;
            case WOOD_AXE:
            case GOLD_AXE:
                damage += 3;
                break;
            default:
                damage += (int) event.getDamage();
        }

        event.setDamage(damage);
    }
}
