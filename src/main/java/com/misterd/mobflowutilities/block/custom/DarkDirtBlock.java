package com.misterd.mobflowutilities.block.custom;

import java.util.List;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

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

        // Reversion: Only revert if in direct sunlight
        boolean inDirectSunlight = level.canSeeSky(pos.above()) && level.isDay();
        if (inDirectSunlight) {
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
            return;
        }

        // Spawning: Only spawn when light level == 0
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
            DustParticleOptions grayDust = new DustParticleOptions(new Vector3f(0.3F, 0.3F, 0.3F), 1.0F);
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

        if (nearbyMobs.size() < Config.getDarkDirtMobsPerArea()) {
            int minX = pos.getX() - searchRadius;
            int maxX = pos.getX() + searchRadius;
            int minZ = pos.getZ() - searchRadius;
            int maxZ = pos.getZ() + searchRadius;
            BlockPos spawnPos = this.findSpawnPosition(level, minX, maxX, minZ, maxZ, pos.getY(), random);

            if (spawnPos != null) {
                List<SpawnerData> spawners = level.getBiome(spawnPos).value().getMobSettings().getMobs(MobCategory.MONSTER).unwrap();

                if (!spawners.isEmpty()) {
                    int totalWeight = spawners.stream().mapToInt(data -> data.getWeight().asInt()).sum();
                    int randomWeight = random.nextInt(totalWeight);
                    int cumulativeWeight = 0;

                    for (SpawnerData spawner : spawners) {
                        cumulativeWeight += spawner.getWeight().asInt();
                        if (randomWeight < cumulativeWeight) {
                            EntityType<?> entityType = spawner.type;

                            try {
                                Mob mob = (Mob) entityType.create(level);
                                if (mob != null) {
                                    mob.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);

                                    if (mob instanceof Monster && SpawnPlacements.checkSpawnRules(entityType, level, MobSpawnType.NATURAL, spawnPos, random)) {
                                        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.NATURAL, (SpawnGroupData) null);
                                        level.addFreshEntity(mob);
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Failed to spawn mob: " + e.getMessage());
                            }
                            break;
                        }
                    }
                }
            }
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
                        level.getBlockState(checkPos).isFaceSturdy(level, checkPos, Direction.UP)) {

                    // Only spawn at light level == 0
                    int lightLevel = level.getMaxLocalRawBrightness(abovePos);
                    if (lightLevel == 0) {
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