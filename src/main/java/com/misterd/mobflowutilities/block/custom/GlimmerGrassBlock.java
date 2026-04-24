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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class GlimmerGrassBlock extends Block {

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
        int minX = pos.getX() - 8;
        int maxX = pos.getX() + 8;
        int minZ = pos.getZ() - 8;
        int maxZ = pos.getZ() + 8;
        AABB spawnArea = new AABB(minX, pos.getY() - 5, minZ, maxX, pos.getY() + 5, maxZ);
        int existingMobs = level.getEntitiesOfClass(Animal.class, spawnArea).size();

        if (existingMobs >= Config.getGlimmerGrassMobsPerArea()) return;

        WeightedList<SpawnerData> spawns = level.getBiome(pos).value().getMobSettings().getMobs(MobCategory.CREATURE);
        if (spawns.isEmpty()) spawns = level.getBiome(pos).value().getMobSettings().getMobs(MobCategory.AMBIENT);
        if (spawns.isEmpty()) return;

        int mobsToSpawn = Math.min(Config.getGlimmerGrassMobsPerArea() - existingMobs, 3);
        for (int i = 0; i < mobsToSpawn; i++) {
            WeightedList<SpawnerData> finalSpawns = spawns;
            finalSpawns.getRandom(random).ifPresent(spawnerData -> {
                EntityType<?> entityType = spawnerData.type();
                BlockPos spawnPos = this.findSpawnPosition(level, minX, maxX, minZ, maxZ, pos.getY(), random);
                if (spawnPos == null) return;
                try {
                    Mob mob = (Mob) entityType.create(level, EntitySpawnReason.NATURAL);
                    if (mob != null) {
                        mob.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0.0F, 0.0F);
                        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.NATURAL, null);
                        level.addFreshEntity(mob);
                    }
                } catch (Exception e) {
                    System.out.println("Failed to spawn passive mob: " + e.getMessage());
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