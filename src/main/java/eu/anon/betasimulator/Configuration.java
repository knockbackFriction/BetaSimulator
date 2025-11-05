package eu.anon.betasimulator;

import org.bukkit.configuration.file.FileConfiguration;

public class Configuration {
    FileConfiguration config = BetaSimulator.instance.getConfig();
    public boolean simulate_b166 = config.getBoolean("simulate-b1-6-x");
    public boolean allowModernFenceRecipe = config.getBoolean("allow-modern-fence-recipe");
    public double playerKnockbackVertical = config.getDouble("player-knockback-vertical");
    public double playerKnockbackHorizontal = config.getDouble("player-knockback-horizontal");
    public double playerKnockbackFriction = config.getDouble("player-knockback-friction");
    public double playerKnockbackLimitVertical = config.getDouble("player-knockback-limit-vertical");
    public boolean barehandDamageFullHeart = config.getBoolean("barehand-damage-full-heart");
}
