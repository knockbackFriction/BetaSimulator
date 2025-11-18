package eu.anon.betasimulator;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class DamageChanger implements Listener {
    boolean extraBaseDmg = BetaSimulator.instance.configuration.barehandDamageFullHeart;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            event.setCancelled(true);
            return;
        }

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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        float dmgReduction = 0.0f;

        for (ItemStack armorPiece : player.getEquipment().getArmorContents()) {
            short maxDura = -1;

            if (armorPiece != null) {
                switch (armorPiece.getType()) {
                    case LEATHER_BOOTS:
                        maxDura = 65;
                        break;
                    case LEATHER_CHESTPLATE:
                        maxDura = 80;
                        break;
                    case LEATHER_HELMET:
                        maxDura = 55;
                        break;
                    case LEATHER_LEGGINGS:
                        maxDura = 75;
                        break;
                    case CHAINMAIL_BOOTS:
                    case IRON_BOOTS:
                        maxDura = 195;
                        break;
                    case CHAINMAIL_CHESTPLATE:
                    case IRON_CHESTPLATE:
                        maxDura = 240;
                        break;
                    case CHAINMAIL_HELMET:
                    case IRON_HELMET:
                        maxDura = 165;
                        break;
                    case CHAINMAIL_LEGGINGS:
                    case IRON_LEGGINGS:
                        maxDura = 225;
                        break;
                    case GOLD_BOOTS:
                        maxDura = 91;
                        break;
                    case GOLD_CHESTPLATE:
                        maxDura = 112;
                        break;
                    case GOLD_HELMET:
                        maxDura = 77;
                        break;
                    case GOLD_LEGGINGS:
                        maxDura = 105;
                        break;
                    case DIAMOND_BOOTS:
                        maxDura = 429;
                        break;
                    case DIAMOND_CHESTPLATE:
                        maxDura = 528;
                        break;
                    case DIAMOND_HELMET:
                        maxDura = 363;
                        break;
                    case DIAMOND_LEGGINGS:
                        maxDura = 495;
                        break;
                }
            }

            if (maxDura != -1) {
                short dura = armorPiece.getDurability();
                dmgReduction += ( (float) (maxDura - dura) / (float) maxDura ) * -0.25f;
            }
        }

        if (dmgReduction < 0.0f) {
            event.setDamage(
                    EntityDamageEvent.DamageModifier.ARMOR,
                    dmgReduction * event.getDamage()
            );
        }
    }
}
