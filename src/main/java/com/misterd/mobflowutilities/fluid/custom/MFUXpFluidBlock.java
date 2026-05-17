package com.misterd.mobflowutilities.fluid.custom;

import com.misterd.mobflowutilities.fluid.MFUFluids;
import net.minecraft.world.level.block.LiquidBlock;

public class MFUXpFluidBlock extends LiquidBlock {

    public MFUXpFluidBlock(Properties properties) {
        super(MFUFluids.LIQUID_XP_SOURCE.get(), properties);
    }
}