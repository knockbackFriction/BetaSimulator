package eu.anon.betasimulator;

import eu.anon.betasimulator.blocks.*;
import eu.anon.betasimulator.mobs.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public final class BetaSimulator extends JavaPlugin implements Listener {
    public static BetaSimulator instance;
    public Configuration configuration;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configuration = new Configuration();

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new MobLoot(), this);
        getServer().getPluginManager().registerEvents(new MobReplacement(), this);
        getServer().getPluginManager().registerEvents(new Knockback(), this);
        getServer().getPluginManager().registerEvents(new RightClicking(), this);
        getServer().getPluginManager().registerEvents(new CraftingManager(), this);
        getServer().getPluginManager().registerEvents(new DamageChanger(), this);
        getServer().getPluginManager().registerEvents(new BlockBreaking(), this);
        getServer().getPluginManager().registerEvents(new BlockPlacement(), this);

        // add fence recipe (top)
        NamespacedKey fence_key = new NamespacedKey(this, "FENCE");
        ShapedRecipe fence_recipe = new ShapedRecipe( fence_key, new ItemStack(Material.FENCE, 2) );
        fence_recipe.shape("111","111","   ");
        fence_recipe.setIngredient('1', Material.STICK);
        getServer().addRecipe(fence_recipe);

        // add fence recipe (down)
        NamespacedKey fence_key2 = new NamespacedKey(this, "FENCE2");
        ShapedRecipe fence_recipe2 = new ShapedRecipe( fence_key2, new ItemStack(Material.FENCE, 2) );
        fence_recipe2.shape("   ","222","222");
        fence_recipe2.setIngredient('2', Material.STICK);
        getServer().addRecipe(fence_recipe2);

        // add leatherless book recipe (left)
        NamespacedKey book_key = new NamespacedKey(this, "BOOK");
        ShapedRecipe book_recipe = new ShapedRecipe(book_key, new ItemStack(Material.BOOK));
        book_recipe.shape("1  ","1  ","1  ");
        book_recipe.setIngredient('1', Material.PAPER);
        getServer().addRecipe(book_recipe);

        // add leatherless book recipe (middle)
        NamespacedKey book_key2 = new NamespacedKey(this, "BOOK2");
        ShapedRecipe book_recipe2 = new ShapedRecipe(book_key2, new ItemStack(Material.BOOK));
        book_recipe2.shape(" 2 "," 2 "," 2 ");
        book_recipe2.setIngredient('2', Material.PAPER);
        getServer().addRecipe(book_recipe2);

        // add leatherless book recipe (right)
        NamespacedKey book_key3 = new NamespacedKey(this, "BOOK3");
        ShapedRecipe book_recipe3 = new ShapedRecipe(book_key3, new ItemStack(Material.BOOK));
        book_recipe3.shape("  3","  3","  3");
        book_recipe3.setIngredient('3', Material.PAPER);
        getServer().addRecipe(book_recipe3);

        // add golden apple recipe with gold blocks
        NamespacedKey goldBlockAppleKey = new NamespacedKey(this, "GOLDBLOCKAPPLE");
        ShapedRecipe goldBlockAppleRecipe = new ShapedRecipe(goldBlockAppleKey, new ItemStack(Material.GOLDEN_APPLE));
        goldBlockAppleRecipe.shape("GGG", "GAG", "GGG");
        goldBlockAppleRecipe.setIngredient('G', Material.GOLD_BLOCK);
        goldBlockAppleRecipe.setIngredient('A', Material.APPLE);
        getServer().addRecipe(goldBlockAppleRecipe);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().setFoodLevel(4);
        e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(999);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                e.getPlayer().setFoodLevel(4);
            }
        }.runTaskLater(this, 1);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setFoodLevel(4);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (configuration.simulate_b166 && event.getEntityType() == EntityType.SHEEP) {
            Sheep sheep = (Sheep) event.getEntity();
            if (sheep.isSheared()) return;
            sheep.setSheared(true);

            ItemStack woolItem = new ItemStack(Material.WOOL, 1, sheep.getColor().getWoolData());

            for (byte i = 0; i < ThreadLocalRandom.current().nextInt(1,4); i++) {
                sheep.getWorld().dropItem(sheep.getLocation(), woolItem);
            }
        }
    }

    ItemStack rawFish = new ItemStack(Material.RAW_FISH);

    private static Vector calcRodVelocity(LivingEntity livingEntity, Location source) {
        float angle = (float) Math.toDegrees(Math.atan2(source.getX() - livingEntity.getLocation().getX(), source.getZ() - livingEntity.getLocation().getZ()));

        double finalX = Math.sin(angle * 3.1415927F / 180.0F) * 0.4f;
        double finalZ = Math.cos(angle * 3.1415927F / 180.0F) * 0.4f;

        Vector velo = livingEntity.getVelocity();
        velo.setX((velo.getX() / 2.0f) - finalX);
        velo.setY(0.4f);
        velo.setZ((velo.getZ() / 2.0f) - finalZ);

        if (velo.getY() > 1.0f) {
            velo.setY(1.0f);
        }
        return velo;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getHitEntity() instanceof LivingEntity)) return;
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
                if (event.getCaught() instanceof Item || !(event.getCaught() instanceof LivingEntity)) {
                    event.setCancelled(true);
                    event.getHook().remove();
                    return;
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
