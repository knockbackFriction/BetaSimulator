package eu.anon.betasimulator;

import eu.anon.betasimulator.mobs.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

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
        getServer().getPluginManager().registerEvents(new RightClicking(), this);
        getServer().getPluginManager().registerEvents(new Knockback(), this);
        getServer().getPluginManager().registerEvents(new CraftingManager(), this);

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
        e.getPlayer().setFoodLevel(4);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setFoodLevel(4);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (configuration.simulate_b166 && event.getBlock().getType() == Material.TNT) {
            event.getBlock().setType(Material.AIR);
            event.setCancelled(true);
            event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.PRIMED_TNT);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Material mat = event.getBlock().getType();
        switch (mat) {
            case WOOD_STEP:
                event.getBlock().setData((byte) 0);
                break;
            case STEP:
                byte dataValue = event.getBlock().getData();
                if (dataValue > 3) {
                    event.getBlock().setData((byte) (dataValue % 4));
                }
        }
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
}
