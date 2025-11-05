package eu.anon.betasimulator;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class CraftingManager implements Listener {
    boolean allowModernFenceRecipe = BetaSimulator.instance.configuration.allowModernFenceRecipe;

    Material[] allowedCraftables = {Material.STONE, Material.COBBLESTONE, Material.WOOD, Material.GLASS, Material.LAPIS_BLOCK,
            Material.DISPENSER, Material.SANDSTONE, Material.NOTE_BLOCK, Material.BED, Material.POWERED_RAIL, Material.DETECTOR_RAIL,
            Material.PISTON_STICKY_BASE, Material.PISTON_BASE, Material.WOOL, Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.STEP,
            Material.WOOD_STEP, Material.BRICK, Material.TNT, Material.BOOKSHELF, Material.OBSIDIAN, Material.TORCH,
            Material.WOOD_STAIRS, Material.CHEST, Material.DIAMOND_BLOCK, Material.WORKBENCH, Material.FURNACE, Material.LADDER,
            Material.RAILS, Material.COBBLESTONE_STAIRS, Material.LEVER, Material.STONE_PLATE, Material.WOOD_PLATE,
            Material.REDSTONE_TORCH_ON, Material.STONE_BUTTON, Material.SNOW_BLOCK, Material.CLAY, Material.JUKEBOX, Material.FENCE,
            Material.PUMPKIN, Material.GLOWSTONE, Material.JACK_O_LANTERN, Material.TRAP_DOOR, Material.IRON_SPADE,
            Material.IRON_PICKAXE, Material.IRON_AXE, Material.FLINT_AND_STEEL, Material.APPLE, Material.BOW, Material.ARROW,
            Material.COAL, Material.DIAMOND, Material.IRON_INGOT, Material.GOLD_INGOT, Material.IRON_SWORD, Material.WOOD_SWORD,
            Material.WOOD_SPADE, Material.WOOD_PICKAXE, Material.WOOD_AXE, Material.STONE_SWORD, Material.STONE_SPADE,
            Material.STONE_PICKAXE, Material.STONE_AXE, Material.DIAMOND_SWORD, Material.DIAMOND_SPADE, Material.DIAMOND_PICKAXE,
            Material.DIAMOND_AXE, Material.STICK, Material.BOWL, Material.MUSHROOM_SOUP, Material.GOLD_SWORD, Material.GOLD_SPADE,
            Material.GOLD_PICKAXE, Material.GOLD_AXE, Material.STRING, Material.FEATHER, Material.WOOD_HOE, Material.STONE_HOE,
            Material.IRON_HOE, Material.DIAMOND_HOE, Material.GOLD_HOE, Material.SEEDS, Material.WHEAT, Material.BREAD,
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
            Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.GOLD_HELMET,
            Material.GOLD_LEGGINGS, Material.GOLD_CHESTPLATE, Material.GOLD_BOOTS, Material.GRILLED_PORK, Material.PAINTING,
            Material.GOLDEN_APPLE, Material.SIGN, Material.WOOD_DOOR, Material.BUCKET, Material.MINECART, Material.IRON_DOOR,
            Material.REDSTONE, Material.SNOW_BALL, Material.BOOK, Material.STORAGE_MINECART, Material.POWERED_MINECART,
            Material.COMPASS, Material.FISHING_ROD, Material.WATCH, Material.GLOWSTONE_DUST, Material.COOKED_FISH,
            Material.INK_SACK, Material.SUGAR, Material.CAKE, Material.DIODE, Material.COOKIE, Material.EMPTY_MAP,
            Material.SHEARS, Material.BOAT, Material.BOAT_ACACIA, Material.BOAT_BIRCH, Material.BOAT_DARK_OAK,
            Material.BOAT_JUNGLE, Material.BOAT_SPRUCE
    };

    private boolean resultWhitelisted(Material material) {
        for (Material m : allowedCraftables) {
            if (m == material) {
                return true;
            }
        }
        return false;
    }

    ItemStack nothing = new ItemStack(Material.AIR);

    @EventHandler
    public void craftItem(PrepareItemCraftEvent e) {
        if (e.getRecipe() == null) return;
        if (e.isRepair()) return;

        Material itemResult = e.getRecipe().getResult().getType();
        ItemMeta itemMeta;

        if (resultWhitelisted(itemResult)) {
            switch (itemResult) {
                case WOOD_DOOR:
                case IRON_DOOR:
                case SIGN:
                case BREAD:
                    // all the cases above are not stackable
                    e.getInventory().getContents()[0].setAmount(1);
                    itemMeta = e.getInventory().getContents()[0].getItemMeta();
                    itemMeta.setDisplayName(UUID.randomUUID().toString());
                    e.getInventory().getContents()[0].setItemMeta(itemMeta);
                    break;
                case WOOD:
                    e.getInventory().getContents()[0].setDurability((short) 0); // all planks need to be Oak
                    break;
                case GOLDEN_APPLE:
                    for (ItemStack is : e.getInventory().getContents()) {
                        if (is.getType() == Material.GOLD_INGOT) { // do not use the cheap recipe with gold ingots
                            e.getInventory().setResult(nothing);
                            return;
                        }
                    }
                    // golden apple is not a stackable item either
                    e.getInventory().getContents()[0].setAmount(1);
                    itemMeta = e.getInventory().getContents()[0].getItemMeta();
                    itemMeta.setDisplayName(UUID.randomUUID().toString());
                    e.getInventory().getContents()[0].setItemMeta(itemMeta);
                    break;
                case FENCE:
                    if (!allowModernFenceRecipe) {
                        for (ItemStack is : e.getInventory().getContents()) {
                            if (is.getType() == Material.WOOD) {
                                e.getInventory().setResult(nothing);
                                return;
                            }
                        }
                    }
                    break;
                case LADDER:
                    e.getInventory().getContents()[0].setAmount(2);
                    break;
                case STEP:
                case WOOD_STEP:
                    e.getInventory().getContents()[0].setAmount(3);
                    break;
            }
        } else {
            e.getInventory().setResult(nothing);
        }
    }
}
