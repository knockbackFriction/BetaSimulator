package eu.anon.betasimulator;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class RightClicking implements Listener {
    private void setHealth(Player p, int health) {
        double newHealth = Math.min(20, p.getHealth() + health);
        p.setHealth(newHealth);
        p.setFoodLevel(4);
    }

    ItemStack nothing = new ItemStack(Material.AIR);

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Action a = event.getAction();
        if (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) return;
        if (!event.hasItem() && !event.hasBlock()) return;

        //cake eating
        if (event.hasBlock() && event.getClickedBlock().getType() == Material.CAKE_BLOCK) {
            if (event.getPlayer().getHealth()==20) {
                event.setCancelled(true);
            } else {
                setHealth(event.getPlayer(), 3);
            }
            return;
        }

        if (event.hasItem()) {
            Player p = event.getPlayer();
            ItemStack item = event.getItem();
            Material material = item.getType();

            switch (material) {
                case PORK:
                    setHealth(p, 3);
                    event.setCancelled(true);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), nothing);
                    break;
                case BREAD:
                case COOKED_FISH:
                    setHealth(p, 5);
                    event.setCancelled(true);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), nothing);
                    break;
                case GRILLED_PORK:
                    setHealth(p, 8);
                    event.setCancelled(true);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), nothing);
                    break;
                case MUSHROOM_SOUP:
                    setHealth(p, 10);
                    event.setCancelled(true);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemStack(Material.BOWL, 1));
                    break;
                case GOLDEN_APPLE:
                    event.getPlayer().setHealth(20);
                    event.setCancelled(true);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), nothing);
                    break;
                case RAW_FISH:
                    setHealth(p, 2);
                    event.setCancelled(true);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), nothing);
                    break;
                case APPLE:
                    setHealth(p, 4);
                    event.setCancelled(true);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), nothing);
                    break;
                case COOKIE:
                    setHealth(p, 1);
                    item = nothing;
                    if (event.getItem().getAmount() > 1) {
                        item = new ItemStack(event.getItem());
                        item.setAmount(item.getAmount() - 1);
                    }
                    event.setCancelled(true);
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), item);
                    break;
                case STRING:
                case STONE_SPADE:
                case GOLD_SPADE:
                case DIAMOND_SPADE:
                case IRON_SPADE:
                case WOOD_SPADE:
                    event.setCancelled(true);
                    break;
                case BOW:
                    event.setCancelled(true);
                    if (p.getInventory().contains(Material.ARROW)) {
                        Arrow arrow = p.launchProjectile(Arrow.class);
                        Vector vec = arrow.getVelocity();
                        vec.setX( vec.getX()/1.5f );
                        vec.setY( vec.getY()/1.5f );
                        vec.setZ( vec.getZ()/1.5f );
                        arrow.setVelocity(vec);
                        ItemStack is = p.getInventory().getItem( p.getInventory().first(Material.ARROW) );
                        is.setAmount(is.getAmount()-1);
                    }

            }
        }
    }
}
