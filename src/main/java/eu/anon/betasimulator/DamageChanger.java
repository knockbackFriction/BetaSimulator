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
    private short getMaxDurability(Material mat) {
        switch (mat) {
            case LEATHER_HELMET:
                return 11 * 3;
            case LEATHER_CHESTPLATE:
                return 16 * 3;
            case LEATHER_LEGGINGS:
                return 15 * 3;
            case LEATHER_BOOTS:
                return 13 * 3;

            case GOLD_HELMET:
            case CHAINMAIL_HELMET:
                return 11 * 3 << 1;
            case GOLD_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
                return 16 * 3 << 1;
            case GOLD_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
                return 15 * 3 << 1;
            case GOLD_BOOTS:
            case CHAINMAIL_BOOTS:
                return 13 * 3 << 1;

            case IRON_HELMET:
                return 11 * 3 << 2;
            case IRON_CHESTPLATE:
                return 16 * 3 << 2;
            case IRON_LEGGINGS:
                return 15 * 3 << 2;
            case IRON_BOOTS:
                return 13 * 3 << 2;

            case DIAMOND_HELMET:
                return 11 * 3 << 3;
            case DIAMOND_CHESTPLATE:
                return 16 * 3 << 3;
            case DIAMOND_LEGGINGS:
                return 15 * 3 << 3;
            case DIAMOND_BOOTS:
                return 13 * 3 << 3;
        }
        return -1;
    }

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

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            double dmg = event.getDamage();

            for (ItemStack armorPiece : player.getEquipment().getArmorContents()) {
                if (armorPiece != null) {
                    if ( (getMaxDurability(armorPiece.getType()) - armorPiece.getDurability()) - dmg > 0) {
                        armorPiece.setDurability( (short) (armorPiece.getDurability() + dmg) );
                    } else {
                        armorPiece.setAmount(0);
                    }
                }
            }
        }

        float dmgReduction = 0.0f;

        for (ItemStack armorPiece : player.getEquipment().getArmorContents()) {
            short maxDura = -1;

            if (armorPiece != null) {
                maxDura = getMaxDurability(armorPiece.getType());
            }

            if (maxDura != -1) {
                short dura = armorPiece.getDurability();
                dmgReduction += ( (float) (maxDura - dura) / (float) maxDura ) * -0.24f;
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
