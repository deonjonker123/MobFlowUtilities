package com.misterd.mobflowutilities.fluid.custom;

import com.misterd.mobflowutilities.fluid.MFUFluids;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class MFUXpFluid extends BaseFlowingFluid {

    public static final Properties PROPERTIES = new Properties(
            MFUFluids.LIQUID_XP_TYPE,
            MFUFluids.LIQUID_XP_FLOWING,
            MFUFluids.LIQUID_XP_SOURCE
    ).bucket(MFUFluids.LIQUID_XP_BUCKET).block(MFUFluids.LIQUID_XP_BLOCK);

    protected MFUXpFluid(Properties properties) {
        super(properties);
    }

    @Override
    public Fluid getFlowing() {
        return MFUFluids.LIQUID_XP_FLOWING.get();
    }

    @Override
    public Fluid getSource() {
        return MFUFluids.LIQUID_XP_SOURCE.get();
    }

    @Override
    public Item getBucket() {
        return MFUFluids.LIQUID_XP_BUCKET.get();
    }

    @Override
    protected boolean canConvertToSource(ServerLevel level) {
        return false;
    }

    public static class Source extends MFUXpFluid {
        public Source() {
            super(PROPERTIES);
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends MFUXpFluid {
        public Flowing() {
            super(PROPERTIES);
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
}