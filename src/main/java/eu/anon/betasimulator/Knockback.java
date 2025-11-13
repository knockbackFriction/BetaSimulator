package eu.anon.betasimulator;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Knockback implements Listener {
    double playerKnockbackVertical = BetaSimulator.instance.configuration.playerKnockbackVertical;
    double playerKnockbackHorizontal = BetaSimulator.instance.configuration.playerKnockbackHorizontal;
    double playerKnockbackFriction = BetaSimulator.instance.configuration.playerKnockbackFriction;
    double playerKnockbackLimitVertical = BetaSimulator.instance.configuration.playerKnockbackLimitVertical;

    HashMap<Player, Vector> playerKnockbackHashMap = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerVelocityEvent(PlayerVelocityEvent event) {
        if (!playerKnockbackHashMap.containsKey(event.getPlayer())) return;
        event.setVelocity(playerKnockbackHashMap.get(event.getPlayer()));
        playerKnockbackHashMap.remove(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        if (event.getEntityType() == EntityType.PLAYER) {
            Player victim = (Player) event.getEntity();
            Entity attacker = event.getDamager();

            // Below is taken from MWHunter/KohiKB
            // Figure out base knockback direction
            double d0 = attacker.getLocation().getX() - victim.getLocation().getX();
            double d1;

            for (d1 = attacker.getLocation().getZ() - victim.getLocation().getZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                d0 = (Math.random() - Math.random()) * 0.01D;
            }

            double magnitude = Math.sqrt(d0 * d0 + d1 * d1);

            // Get player knockback taken before any friction applied
            Vector playerVelocity = victim.getVelocity();

            // apply friction then add the base knockback
            playerVelocity.setX((playerVelocity.getX() / playerKnockbackFriction) - (d0 / magnitude * playerKnockbackHorizontal));
            playerVelocity.setY((playerVelocity.getY() / playerKnockbackFriction) + playerKnockbackVertical);
            playerVelocity.setZ((playerVelocity.getZ() / playerKnockbackFriction) - (d1 / magnitude * playerKnockbackHorizontal));

            if (playerVelocity.getY() > playerKnockbackLimitVertical) playerVelocity.setY(playerKnockbackLimitVertical);
            playerKnockbackHashMap.put(victim, playerVelocity);
        } else {
            LivingEntity le = (LivingEntity) event.getEntity();
            if (le.getNoDamageTicks() > 10) return; //event fires even if entity is currently invincible
            //we simply want to fix the dirty 1.9+ vertical kb
            Vector velo = event.getEntity().getVelocity();
            velo.setY((velo.getY() / 2.0f) + 0.4f);
            if (velo.getY() > 0.4f) velo.setY(0.4f);
            event.getEntity().setVelocity(velo);
        }
    }
}
