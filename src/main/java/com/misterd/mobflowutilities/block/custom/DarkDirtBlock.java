package com.misterd.mobflowutilities.block.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final Map<BlockPos, Long> reversionTimers = new HashMap();

    public DarkDirtBlock(Properties properties) {
        super(properties);
    }

    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, Config.getDarkDirtCheckInterval());
        }

    }

    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        int lightLevel;
        if (Config.isDarkDirtRevertEnabled()) {
            lightLevel = level.getMaxLocalRawBrightness(pos.above());
            boolean inSunlight = level.canSeeSky(pos.above()) && level.isDay();
            if (lightLevel <= Config.getDarkDirtConversionLightLevel() && !inSunlight) {
                reversionTimers.remove(pos);
            } else {
                long currentTime = level.getGameTime();
                if (!reversionTimers.containsKey(pos)) {
                    reversionTimers.put(pos, currentTime + Config.getDarkDirtReversionDelayTicks());
                } else if (currentTime >= reversionTimers.get(pos)) {
                    level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
                    reversionTimers.remove(pos);
                    return;
                }
            }
        }

        lightLevel = level.getMaxLocalRawBrightness(pos.above());
        if (lightLevel <= Config.getDarkDirtSpawningLightLevel()) {
            this.attemptMobSpawning(level, pos, random);
        }

        level.scheduleTick(pos, this, Config.getDarkDirtCheckInterval());
    }

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
        int centerX = pos.getX();
        int centerZ = pos.getZ();
        int minX = centerX - 8;
        int maxX = centerX + 8;
        int minZ = centerZ - 8;
        int maxZ = centerZ + 8;
        AABB spawnArea = new AABB(minX, (pos.getY() - 5), minZ, maxX, (pos.getY() + 5), maxZ);
        long existingMobs = level.getEntitiesOfClass(Monster.class, spawnArea).size();
        if (existingMobs < Config.getDarkDirtMobsPerArea()) {
            List<SpawnerData> hostileSpawns = (level.getBiome(pos).value()).getMobSettings().getMobs(MobCategory.MONSTER).unwrap();
            if (!hostileSpawns.isEmpty()) {
                int mobsToSpawn = Math.min(Config.getDarkDirtMobsPerArea() - (int)existingMobs, 3);

                for(int i = 0; i < mobsToSpawn; ++i) {
                    SpawnerData spawnerData = hostileSpawns.get(random.nextInt(hostileSpawns.size()));
                    EntityType<?> entityType = spawnerData.type;
                    BlockPos spawnPos = this.findSpawnPosition(level, minX, maxX, minZ, maxZ, pos.getY(), random);
                    if (spawnPos != null && SpawnPlacements.checkSpawnRules(entityType, level, MobSpawnType.NATURAL, spawnPos, random)) {
                        try {
                            Mob mob = (Mob)entityType.create(level);
                            if (mob != null) {
                                mob.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.NATURAL, (SpawnGroupData)null);
                                level.addFreshEntity(mob);
                            }
                        } catch (Exception e) {
                            System.out.println("Failed to spawn mob: " + e.getMessage());
                        }
                    }
                }

            }
        }
    }

    private BlockPos findSpawnPosition(ServerLevel level, int minX, int maxX, int minZ, int maxZ, int baseY, RandomSource random) {
        for(int attempts = 0; attempts < 10; ++attempts) {
            int x = random.nextIntBetweenInclusive(minX, maxX);
            int z = random.nextIntBetweenInclusive(minZ, maxZ);

            for(int yOffset = -2; yOffset <= 2; ++yOffset) {
                BlockPos checkPos = new BlockPos(x, baseY + yOffset, z);
                BlockPos abovePos = checkPos.above();
                if (level.getBlockState(checkPos).getBlock() == this && (level.getBlockState(abovePos).isAir() || this.flowPadsAllowed(level.getBlockState(abovePos).getBlock())) && level.getBlockState(abovePos.above()).isAir() && level.getBlockState(checkPos).isFaceSturdy(level, checkPos, Direction.UP)) {
                    int lightLevel = level.getMaxLocalRawBrightness(abovePos);
                    if (lightLevel <= Config.getDarkDirtSpawningLightLevel()) {
                        return abovePos;
                    }
                }
            }
        }

        return null;
    }

    private boolean flowPadsAllowed(Block block) {
        return block == MFUBlocks.FAST_FLOW_PAD.get() || block == MFUBlocks.FASTER_FLOW_PAD.get() || block == MFUBlocks.FASTEST_FLOW_PAD.get();
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            reversionTimers.remove(pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
