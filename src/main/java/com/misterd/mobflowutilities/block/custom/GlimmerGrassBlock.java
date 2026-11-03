package com.misterd.mobflowutilities.block.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.config.Config;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class GlimmerGrassBlock extends Block {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int SPAWN_RADIUS = 8;
    private static final int SPAWN_HEIGHT = 5;
    private static final int AMBIENT_COUNT_RADIUS = 32;
    private static final int AMBIENT_COUNT_HEIGHT = 16;

    public GlimmerGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, Config.getGlimmerGrassCheckInterval());
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        if (level.getMaxLocalRawBrightness(pos.above()) >= 7) {
            this.attemptMobSpawning(level, pos, random);
        }

        level.scheduleTick(pos, this, Config.getGlimmerGrassCheckInterval());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (Config.isGlimmerGrassParticlesEnabled() && random.nextInt(10) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 1.0D;
            double z = pos.getZ() + random.nextDouble();
            DustParticleOptions aquaDust = new DustParticleOptions(ARGB.color(255, 0, 255, 204), 1.0F);
            level.addParticle(aquaDust, x, y, z, 0.0D, 0.05D, 0.0D);
        }
    }

    private void attemptMobSpawning(ServerLevel level, BlockPos pos, RandomSource random) {
        MobSpawnSettings spawnSettings = level.environmentAttributes().getValue(EnvironmentAttributes.NATURAL_MOB_SPAWNS, pos);
        WeightedList<SpawnerData> creatureSpawns = spawnSettings.getMobsInCategory(MobCategory.CREATURE);
        final MobCategory category = creatureSpawns.isEmpty() ? MobCategory.AMBIENT : MobCategory.CREATURE;
        final WeightedList<SpawnerData> spawns = creatureSpawns.isEmpty() ? spawnSettings.getMobsInCategory(MobCategory.AMBIENT) : creatureSpawns;
        if (spawns.isEmpty()) return;

        int minX = pos.getX() - SPAWN_RADIUS;
        int maxX = pos.getX() + SPAWN_RADIUS;
        int minZ = pos.getZ() - SPAWN_RADIUS;
        int maxZ = pos.getZ() + SPAWN_RADIUS;

        AABB countArea = category == MobCategory.AMBIENT
                ? new AABB(pos).inflate(AMBIENT_COUNT_RADIUS, AMBIENT_COUNT_HEIGHT, AMBIENT_COUNT_RADIUS)
                : new AABB(minX, pos.getY() - SPAWN_HEIGHT, minZ, maxX, pos.getY() + SPAWN_HEIGHT, maxZ);

        int cap = Config.getGlimmerGrassMobsPerArea();
        int existingMobs = level.getEntitiesOfClass(Mob.class, countArea,
                mob -> mob.getType().getCategory() == category).size();
        if (existingMobs >= cap) return;

        int mobsToSpawn = Math.min(cap - existingMobs, 3);
        for (int i = 0; i < mobsToSpawn; i++) {
            spawns.getRandom(random).ifPresent(spawnerData -> {
                EntityType<?> entityType = spawnerData.type();
                BlockPos spawnPos = this.findSpawnPosition(level, minX, maxX, minZ, maxZ, pos.getY(), random);
                if (spawnPos == null) return;
                try {
                    Entity entity = entityType.create(level, EntitySpawnReason.NATURAL);
                    if (entity instanceof Mob mob) {
                        mob.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0.0F, 0.0F);
                        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.NATURAL, null);
                        level.addFreshEntity(mob);
                    } else if (entity != null) {
                        entity.discard();
                    }
                } catch (Exception e) {
                    LOGGER.warn("Glimmer Grass failed to spawn {} at {}", entityType, spawnPos, e);
                }
            });
        }
    }

    private BlockPos findSpawnPosition(ServerLevel level, int minX, int maxX, int minZ, int maxZ, int baseY, RandomSource random) {
        for (int attempts = 0; attempts < 10; attempts++) {
            int x = random.nextIntBetweenInclusive(minX, maxX);
            int z = random.nextIntBetweenInclusive(minZ, maxZ);

            for (int yOffset = -2; yOffset <= 2; yOffset++) {
                BlockPos checkPos = new BlockPos(x, baseY + yOffset, z);
                BlockPos abovePos = checkPos.above();

                if (level.getBlockState(checkPos).getBlock() == this &&
                        (level.getBlockState(abovePos).isAir() || this.flowPadsAllowed(level.getBlockState(abovePos).getBlock())) &&
                        level.getBlockState(abovePos.above()).isAir() &&
                        level.getBlockState(checkPos).isFaceSturdy(level, checkPos, Direction.UP) &&
                        level.getMaxLocalRawBrightness(abovePos) >= 7) {
                    return abovePos;
                }
            }
        }
        return null;
    }

    private boolean flowPadsAllowed(Block block) {
        return block == MFUBlocks.FAST_FLOW_PAD.get() ||
                block == MFUBlocks.FASTER_FLOW_PAD.get() ||
                block == MFUBlocks.FASTEST_FLOW_PAD.get();
    }
}