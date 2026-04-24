package com.misterd.mobflowutilities.block.custom;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DarkDirtBlock extends Block {

    public DarkDirtBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, Config.getDarkDirtCheckInterval());
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        boolean isDaytime = level.dimensionType().hasSkyLight() && level.isBrightOutside();
        boolean isExposed = level.canSeeSky(pos.above());
        if (isDaytime && isExposed) {
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
            return;
        }

        int lightLevel = level.getMaxLocalRawBrightness(pos.above());
        if (lightLevel == 0) {
            this.attemptMobSpawning(level, pos, random);
        }

        level.scheduleTick(pos, this, Config.getDarkDirtCheckInterval());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (Config.isDarkDirtParticlesEnabled() && random.nextInt(10) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 1.0D;
            double z = pos.getZ() + random.nextDouble();
            DustParticleOptions grayDust = new DustParticleOptions(ARGB.color(255, 77, 77, 77), 1.0F);
            level.addParticle(grayDust, x, y, z, 0.0D, 0.05D, 0.0D);
        }
    }

    private void attemptMobSpawning(ServerLevel level, BlockPos pos, RandomSource random) {
        int radius = 8;
        int searchRadius = radius * 2;
        AABB searchBox = new AABB(
                pos.getX() - radius, pos.getY() - 2, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + 2, pos.getZ() + radius
        );
        List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, searchBox, mob -> mob.getType().getCategory() == MobCategory.MONSTER);

        if (nearbyMobs.size() >= Config.getDarkDirtMobsPerArea()) return;

        int minX = pos.getX() - searchRadius;
        int maxX = pos.getX() + searchRadius;
        int minZ = pos.getZ() - searchRadius;
        int maxZ = pos.getZ() + searchRadius;
        BlockPos spawnPos = this.findSpawnPosition(level, minX, maxX, minZ, maxZ, pos.getY(), random);
        if (spawnPos == null) return;

        WeightedList<SpawnerData> mobList = level.getBiome(spawnPos).value().getMobSettings().getMobs(MobCategory.MONSTER);

        WeightedList.Builder<SpawnerData> builder = WeightedList.<SpawnerData>builder().addAll(mobList);
        boolean hasSlime = mobList.unwrap().stream().anyMatch(w -> w.value().type() == EntityType.SLIME);
        if (!hasSlime) builder.add(new SpawnerData(EntityType.SLIME, 1, 3), 10);
        WeightedList<SpawnerData> spawners = builder.build();

        spawners.getRandom(random).ifPresent(spawner -> {
            EntityType<?> entityType = spawner.type();
            try {
                Mob mob = (Mob) entityType.create(level, EntitySpawnReason.NATURAL);
                if (mob != null) {
                    mob.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
                    if (SpawnPlacements.checkSpawnRules(entityType, level, EntitySpawnReason.NATURAL, spawnPos, random) || entityType == EntityType.SLIME) {
                        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.NATURAL, null);
                        level.addFreshEntity(mob);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
                        level.getBlockState(checkPos).isFaceSturdy(level, checkPos, Direction.UP)) {

                    if (level.getMaxLocalRawBrightness(abovePos) == 0) {
                        return abovePos;
                    }
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