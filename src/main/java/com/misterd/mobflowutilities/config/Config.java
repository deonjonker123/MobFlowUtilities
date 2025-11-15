package com.misterd.mobflowutilities.config;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

public class Config {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    private static ModConfigSpec COMMON_CONFIG;

    // Gloom Spore Configuration
    private static ModConfigSpec.IntValue GLOOM_SPORE_CONVERSION_AREA;

    // Dark Dirt Configuration
    private static ModConfigSpec.IntValue DARK_DIRT_CONVERSION_LIGHT_LEVEL;
    private static ModConfigSpec.IntValue DARK_DIRT_SPAWNING_LIGHT_LEVEL;
    private static ModConfigSpec.IntValue DARK_DIRT_MOBS_PER_AREA;
    private static ModConfigSpec.IntValue DARK_DIRT_CHECK_INTERVAL;
    private static ModConfigSpec.BooleanValue DARK_DIRT_REVERT_ENABLED;
    private static ModConfigSpec.BooleanValue DARK_DIRT_PARTICLES_ENABLED;
    private static ModConfigSpec.IntValue DARK_DIRT_REVERSION_DELAY;

    // Glimmer Sprout Configuration
    private static ModConfigSpec.IntValue GLIMMER_SPROUT_CONVERSION_AREA;

    // Glimmer Grass Configuration
    private static ModConfigSpec.IntValue GLIMMER_GRASS_CONVERSION_LIGHT_LEVEL;
    private static ModConfigSpec.IntValue GLIMMER_GRASS_SPAWNING_LIGHT_LEVEL;
    private static ModConfigSpec.IntValue GLIMMER_GRASS_MOBS_PER_AREA;
    private static ModConfigSpec.IntValue GLIMMER_GRASS_CHECK_INTERVAL;
    private static ModConfigSpec.BooleanValue GLIMMER_GRASS_REVERT_ENABLED;
    private static ModConfigSpec.BooleanValue GLIMMER_GRASS_PARTICLES_ENABLED;
    private static ModConfigSpec.IntValue GLIMMER_GRASS_REVERSION_DELAY;

    static {
        buildCommonConfig();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }

    private static void buildCommonConfig() {
        buildGloomSporeConfig();
        buildDarkDirtConfig();
        buildGlimmerSproutConfig();
        buildGlimmerGrassConfig();
    }

    private static void buildGloomSporeConfig() {
        COMMON_BUILDER.comment("Gloom Spore - Configure conversion settings for Gloom Spores")
                .push("gloom_spore");

        GLOOM_SPORE_CONVERSION_AREA = COMMON_BUILDER
                .comment(
                        "Conversion area size (blocks)",
                        "Area is square, centered on clicked block",
                        "Default: 5 (converts 5x5 area)"
                )
                .defineInRange("conversion_area", 5, 3, 15);

        COMMON_BUILDER.pop();
    }

    private static void buildDarkDirtConfig() {
        COMMON_BUILDER.comment("Dark Dirt - Configure spawning and behavior settings for Dark Dirt blocks")
                .push("dark_dirt");

        DARK_DIRT_CONVERSION_LIGHT_LEVEL = COMMON_BUILDER
                .comment(
                        "Maximum light level for conversion to Dark Dirt",
                        "Gloom Spore will only convert dirt at or below this light level",
                        "Default: 7"
                )
                .defineInRange("conversion_light_level", 7, 0, 15);

        DARK_DIRT_SPAWNING_LIGHT_LEVEL = COMMON_BUILDER
                .comment(
                        "Maximum light level for mob spawning on Dark Dirt",
                        "Dark Dirt will only spawn mobs at or below this light level",
                        "Default: 2"
                )
                .defineInRange("spawning_light_level", 2, 0, 15);

        DARK_DIRT_MOBS_PER_AREA = COMMON_BUILDER
                .comment(
                        "Number of mobs to spawn per 16x16 area",
                        "Higher numbers increase mob density but may impact performance",
                        "Default: 12"
                )
                .defineInRange("mobs_per_area", 12, 1, 50);

        DARK_DIRT_CHECK_INTERVAL = COMMON_BUILDER
                .comment(
                        "Mob spawning check interval (ticks)",
                        "How often Dark Dirt checks for spawning opportunities",
                        "20 ticks = 1 second",
                        "Default: 20"
                )
                .defineInRange("check_interval", 20, 5, 200);

        DARK_DIRT_REVERT_ENABLED = COMMON_BUILDER
                .comment(
                        "Enable Dark Dirt reversion to normal dirt",
                        "If true, Dark Dirt reverts to normal dirt in bright light",
                        "Default: true"
                )
                .define("revert_enabled", true);

        DARK_DIRT_PARTICLES_ENABLED = COMMON_BUILDER
                .comment(
                        "Enable Dark Dirt particle effects",
                        "If true, Dark Dirt shows gray particle effects",
                        "Default: true"
                )
                .define("particles_enabled", true);

        DARK_DIRT_REVERSION_DELAY = COMMON_BUILDER
                .comment(
                        "Reversion delay in seconds",
                        "How long Dark Dirt waits in bright light before reverting to dirt",
                        "Default: 10"
                )
                .defineInRange("reversion_delay_seconds", 10, 1, 60);

        COMMON_BUILDER.pop();
    }

    private static void buildGlimmerSproutConfig() {
        COMMON_BUILDER.comment("Glimmer Sprout - Configure conversion settings for Glimmer Sprouts")
                .push("glimmer_sprout");

        GLIMMER_SPROUT_CONVERSION_AREA = COMMON_BUILDER
                .comment(
                        "Conversion area size (blocks)",
                        "Area is square, centered on clicked block",
                        "Default: 5 (converts 5x5 area)"
                )
                .defineInRange("conversion_area", 5, 3, 15);

        COMMON_BUILDER.pop();
    }

    private static void buildGlimmerGrassConfig() {
        COMMON_BUILDER.comment("Glimmer Grass - Configure spawning and behavior settings for Glimmer Grass blocks")
                .push("glimmer_grass");

        GLIMMER_GRASS_CONVERSION_LIGHT_LEVEL = COMMON_BUILDER
                .comment(
                        "Minimum light level for conversion to Glimmer Grass",
                        "Glimmer Sprout will only convert dirt at or above this light level",
                        "Default: 7"
                )
                .defineInRange("conversion_light_level", 7, 1, 15);

        GLIMMER_GRASS_SPAWNING_LIGHT_LEVEL = COMMON_BUILDER
                .comment(
                        "Minimum light level for mob spawning on Glimmer Grass",
                        "Glimmer Grass will only spawn mobs at or above this light level",
                        "Default: 7"
                )
                .defineInRange("spawning_light_level", 7, 1, 15);

        GLIMMER_GRASS_MOBS_PER_AREA = COMMON_BUILDER
                .comment(
                        "Number of mobs to spawn per 16x16 area",
                        "Higher numbers increase mob density but may impact performance",
                        "Default: 12"
                )
                .defineInRange("mobs_per_area", 12, 1, 50);

        GLIMMER_GRASS_CHECK_INTERVAL = COMMON_BUILDER
                .comment(
                        "Mob spawning check interval (ticks)",
                        "How often Glimmer Grass checks for spawning opportunities",
                        "20 ticks = 1 second",
                        "Default: 20"
                )
                .defineInRange("check_interval", 20, 5, 200);

        GLIMMER_GRASS_REVERT_ENABLED = COMMON_BUILDER
                .comment(
                        "Enable Glimmer Grass reversion to normal dirt",
                        "If true, Glimmer Grass reverts to normal dirt in low light",
                        "Default: true"
                )
                .define("revert_enabled", true);

        GLIMMER_GRASS_PARTICLES_ENABLED = COMMON_BUILDER
                .comment(
                        "Enable Glimmer Grass particle effects",
                        "If true, Glimmer Grass shows purple particle effects",
                        "Default: true"
                )
                .define("particles_enabled", true);

        GLIMMER_GRASS_REVERSION_DELAY = COMMON_BUILDER
                .comment(
                        "Reversion delay in seconds",
                        "How long Glimmer Grass waits in darkness before reverting to dirt",
                        "Default: 10"
                )
                .defineInRange("reversion_delay_seconds", 10, 1, 60);

        COMMON_BUILDER.pop();
    }

    // Gloom Spore Getters
    public static int getGloomSporeConversionArea() {
        return GLOOM_SPORE_CONVERSION_AREA.get();
    }

    // Dark Dirt Getters
    public static int getDarkDirtConversionLightLevel() {
        return DARK_DIRT_CONVERSION_LIGHT_LEVEL.get();
    }

    public static int getDarkDirtSpawningLightLevel() {
        return DARK_DIRT_SPAWNING_LIGHT_LEVEL.get();
    }

    public static int getDarkDirtMobsPerArea() {
        return DARK_DIRT_MOBS_PER_AREA.get();
    }

    public static int getDarkDirtCheckInterval() {
        return DARK_DIRT_CHECK_INTERVAL.get();
    }

    public static boolean isDarkDirtRevertEnabled() {
        return DARK_DIRT_REVERT_ENABLED.get();
    }

    public static boolean isDarkDirtParticlesEnabled() {
        return DARK_DIRT_PARTICLES_ENABLED.get();
    }

    public static int getDarkDirtReversionDelayTicks() {
        return DARK_DIRT_REVERSION_DELAY.get() * 20;
    }

    // Glimmer Sprout Getters
    public static int getGlimmerSproutConversionArea() {
        return GLIMMER_SPROUT_CONVERSION_AREA.get();
    }

    // Glimmer Grass Getters
    public static int getGlimmerGrassConversionLightLevel() {
        return GLIMMER_GRASS_CONVERSION_LIGHT_LEVEL.get();
    }

    public static int getGlimmerGrassSpawningLightLevel() {
        return GLIMMER_GRASS_SPAWNING_LIGHT_LEVEL.get();
    }

    public static int getGlimmerGrassMobsPerArea() {
        return GLIMMER_GRASS_MOBS_PER_AREA.get();
    }

    public static int getGlimmerGrassCheckInterval() {
        return GLIMMER_GRASS_CHECK_INTERVAL.get();
    }

    public static boolean isGlimmerGrassRevertEnabled() {
        return GLIMMER_GRASS_REVERT_ENABLED.get();
    }

    public static boolean isGlimmerGrassParticlesEnabled() {
        return GLIMMER_GRASS_PARTICLES_ENABLED.get();
    }

    public static int getGlimmerGrassReversionDelayTicks() {
        return GLIMMER_GRASS_REVERSION_DELAY.get() * 20;
    }

    // Validation
    private static void validateConfig() {
        if (getDarkDirtConversionLightLevel() < getDarkDirtSpawningLightLevel()) {
            LOGGER.warn(
                    "Dark Dirt conversion light level ({}) is lower than spawning light level ({}). " +
                            "This may cause unexpected behavior.",
                    getDarkDirtConversionLightLevel(),
                    getDarkDirtSpawningLightLevel()
            );
        }

        if (getGlimmerGrassConversionLightLevel() > getGlimmerGrassSpawningLightLevel()) {
            LOGGER.warn(
                    "Glimmer Grass conversion light level ({}) is higher than spawning light level ({}). " +
                            "This may cause unexpected behavior.",
                    getGlimmerGrassConversionLightLevel(),
                    getGlimmerGrassSpawningLightLevel()
            );
        }

        if (getDarkDirtMobsPerArea() > 25) {
            LOGGER.warn(
                    "Dark Dirt mob spawn count ({}) is high and may impact server performance!",
                    getDarkDirtMobsPerArea()
            );
        }

        if (getGlimmerGrassMobsPerArea() > 25) {
            LOGGER.warn(
                    "Glimmer Grass mob spawn count ({}) is high and may impact server performance!",
                    getGlimmerGrassMobsPerArea()
            );
        }

        if (getDarkDirtCheckInterval() < 10) {
            LOGGER.warn(
                    "Dark Dirt check interval ({} ticks) is very fast and may impact server performance!",
                    getDarkDirtCheckInterval()
            );
        }

        if (getGlimmerGrassCheckInterval() < 10) {
            LOGGER.warn(
                    "Glimmer Grass check interval ({} ticks) is very fast and may impact server performance!",
                    getGlimmerGrassCheckInterval()
            );
        }
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            LOGGER.info("Mob Flow Utilities configuration loaded");
            logConfigValues();
            validateConfig();
        }
    }

    private static void logConfigValues() {
        LOGGER.info("Gloom Spore Configuration:");
        LOGGER.info("  Conversion Area: {}x{} blocks", getGloomSporeConversionArea(), getGloomSporeConversionArea());

        LOGGER.info("Dark Dirt Configuration:");
        LOGGER.info("  Conversion Light Level: {} (max)", getDarkDirtConversionLightLevel());
        LOGGER.info("  Spawning Light Level: {} (max)", getDarkDirtSpawningLightLevel());
        LOGGER.info("  Mobs per 16x16 Area: {}", getDarkDirtMobsPerArea());
        LOGGER.info("  Check Interval: {} ticks", getDarkDirtCheckInterval());
        LOGGER.info("  Reversion Enabled: {}", isDarkDirtRevertEnabled());
        LOGGER.info("  Particles Enabled: {}", isDarkDirtParticlesEnabled());

        LOGGER.info("Glimmer Sprout Configuration:");
        LOGGER.info("  Conversion Area: {}x{} blocks", getGlimmerSproutConversionArea(), getGlimmerSproutConversionArea());

        LOGGER.info("Glimmer Grass Configuration:");
        LOGGER.info("  Conversion Light Level: {} (min)", getGlimmerGrassConversionLightLevel());
        LOGGER.info("  Spawning Light Level: {} (min)", getGlimmerGrassSpawningLightLevel());
        LOGGER.info("  Mobs per 16x16 Area: {}", getGlimmerGrassMobsPerArea());
        LOGGER.info("  Check Interval: {} ticks", getGlimmerGrassCheckInterval());
        LOGGER.info("  Reversion Enabled: {}", isGlimmerGrassRevertEnabled());
        LOGGER.info("  Particles Enabled: {}", isGlimmerGrassParticlesEnabled());
    }
}