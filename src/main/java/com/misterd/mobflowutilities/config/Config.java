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

    private static ModConfigSpec.IntValue GLOOM_SPORE_CONVERSION_AREA;

    private static ModConfigSpec.IntValue DARK_DIRT_MOBS_PER_AREA;
    private static ModConfigSpec.IntValue DARK_DIRT_CHECK_INTERVAL;
    private static ModConfigSpec.BooleanValue DARK_DIRT_PARTICLES_ENABLED;

    private static ModConfigSpec.IntValue GLIMMER_SPROUT_CONVERSION_AREA;

    private static ModConfigSpec.IntValue GLIMMER_GRASS_MOBS_PER_AREA;
    private static ModConfigSpec.IntValue GLIMMER_GRASS_CHECK_INTERVAL;
    private static ModConfigSpec.BooleanValue GLIMMER_GRASS_PARTICLES_ENABLED;
    private static ModConfigSpec.IntValue GENESIS_CHAMBER_SPAWN_CAP;

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
        buildGenesisChamberConfig();
    }

    private static void buildGloomSporeConfig() {
        COMMON_BUILDER.comment("Gloom Spore - Configure conversion settings for Gloom Spores")
                .push("gloom_spore");

        GLOOM_SPORE_CONVERSION_AREA = COMMON_BUILDER
                .comment(
                        "Conversion area size (blocks)",
                        "Area is square, centered on clicked block",
                        "Converts a 5x5 area (default)"
                )
                .defineInRange("conversion_area", 5, 3, 15);

        COMMON_BUILDER.pop();
    }

    private static void buildDarkDirtConfig() {
        COMMON_BUILDER.comment("Dark Dirt - Configure spawning and behavior settings for Dark Dirt blocks")
                .push("dark_dirt");

        DARK_DIRT_MOBS_PER_AREA = COMMON_BUILDER
                .comment(
                        "Number of mobs to spawn per 16x16 area",
                        "Higher numbers increase mob density but may impact performance"
                )
                .defineInRange("mobs_per_area", 12, 1, 50);

        DARK_DIRT_CHECK_INTERVAL = COMMON_BUILDER
                .comment(
                        "Mob spawning check interval (ticks)",
                        "How often Dark Dirt checks for spawning opportunities",
                        "20 ticks = 1 second"
                )
                .defineInRange("check_interval", 20, 5, 200);

        DARK_DIRT_PARTICLES_ENABLED = COMMON_BUILDER
                .comment(
                        "Enable Dark Dirt particle effects",
                        "If true, Dark Dirt shows gray particle effects"
                )
                .define("particles_enabled", true);

        COMMON_BUILDER.pop();
    }

    private static void buildGlimmerSproutConfig() {
        COMMON_BUILDER.comment("Glimmer Sprout - Configure conversion settings for Glimmer Sprouts")
                .push("glimmer_sprout");

        GLIMMER_SPROUT_CONVERSION_AREA = COMMON_BUILDER
                .comment(
                        "Conversion area size (blocks)",
                        "Area is square, centered on clicked block",
                        "Converts a 5x5 area (default)"
                )
                .defineInRange("conversion_area", 5, 3, 15);

        COMMON_BUILDER.pop();
    }

    private static void buildGlimmerGrassConfig() {
        COMMON_BUILDER.comment("Glimmer Grass - Configure spawning and behavior settings for Glimmer Grass blocks")
                .push("glimmer_grass");

        GLIMMER_GRASS_MOBS_PER_AREA = COMMON_BUILDER
                .comment(
                        "Number of mobs to spawn per 16x16 area",
                        "Higher numbers increase mob density but may impact performance"
                )
                .defineInRange("mobs_per_area", 12, 1, 50);

        GLIMMER_GRASS_CHECK_INTERVAL = COMMON_BUILDER
                .comment(
                        "Mob spawning check interval (ticks)",
                        "How often Glimmer Grass checks for spawning opportunities",
                        "20 ticks = 1 second"
                )
                .defineInRange("check_interval", 20, 5, 200);

        GLIMMER_GRASS_PARTICLES_ENABLED = COMMON_BUILDER
                .comment(
                        "Enable Glimmer Grass particle effects",
                        "If true, Glimmer Grass shows purple particle effects"
                )
                .define("particles_enabled", true);

        COMMON_BUILDER.pop();
    }

    private static void buildGenesisChamberConfig() {
        COMMON_BUILDER.comment("Genesis Chamber - Configure spawning behavior for Genesis Chamber blocks")
                .push("genesis_chamber");

        GENESIS_CHAMBER_SPAWN_CAP = COMMON_BUILDER
                .comment(
                        "Maximum number of mobs allowed in Genesis Chamber spawn zone",
                        "Genesis Chamber stops spawning when this limit is reached",
                        "Higher numbers allow more mobs but may impact performance"
                )
                .defineInRange("spawn_cap", 12, 1, 100);

        COMMON_BUILDER.pop();
    }

    public static int getGloomSporeConversionArea() {
        return GLOOM_SPORE_CONVERSION_AREA.get();
    }

    public static int getDarkDirtMobsPerArea() {
        return DARK_DIRT_MOBS_PER_AREA.get();
    }

    public static int getDarkDirtCheckInterval() {
        return DARK_DIRT_CHECK_INTERVAL.get();
    }

    public static boolean isDarkDirtParticlesEnabled() {
        return DARK_DIRT_PARTICLES_ENABLED.get();
    }

    public static int getGlimmerSproutConversionArea() {
        return GLIMMER_SPROUT_CONVERSION_AREA.get();
    }

    public static int getGlimmerGrassMobsPerArea() {
        return GLIMMER_GRASS_MOBS_PER_AREA.get();
    }

    public static int getGlimmerGrassCheckInterval() {
        return GLIMMER_GRASS_CHECK_INTERVAL.get();
    }

    public static boolean isGlimmerGrassParticlesEnabled() {
        return GLIMMER_GRASS_PARTICLES_ENABLED.get();
    }

    public static int getGenesisChamberSpawnCap() {
        return GENESIS_CHAMBER_SPAWN_CAP.get();
    }

    private static void validateConfig() {
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

        if (getGenesisChamberSpawnCap() > 50) {
            LOGGER.warn(
                    "Genesis Chamber spawn cap ({}) is very high and may impact server performance!",
                    getGenesisChamberSpawnCap()
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
        LOGGER.info("  Mobs per 16x16 Area: {}", getDarkDirtMobsPerArea());
        LOGGER.info("  Check Interval: {} ticks", getDarkDirtCheckInterval());
        LOGGER.info("  Particles Enabled: {}", isDarkDirtParticlesEnabled());

        LOGGER.info("Glimmer Sprout Configuration:");
        LOGGER.info("  Conversion Area: {}x{} blocks", getGlimmerSproutConversionArea(), getGlimmerSproutConversionArea());

        LOGGER.info("Glimmer Grass Configuration:");
        LOGGER.info("  Mobs per 16x16 Area: {}", getGlimmerGrassMobsPerArea());
        LOGGER.info("  Check Interval: {} ticks", getGlimmerGrassCheckInterval());
        LOGGER.info("  Particles Enabled: {}", isGlimmerGrassParticlesEnabled());

        LOGGER.info("Genesis Chamber Configuration:");
        LOGGER.info("  Spawn Cap: {} mobs", getGenesisChamberSpawnCap());
    }
}