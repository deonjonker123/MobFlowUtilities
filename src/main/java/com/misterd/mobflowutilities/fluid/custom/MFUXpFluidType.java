package com.misterd.mobflowutilities.fluid.custom;

import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

public class MFUXpFluidType extends FluidType {

    public MFUXpFluidType() {
        super(Properties.create()
                .density(1000)
                .viscosity(1000)
                .temperature(300)
                .canSwim(false)
                .canDrown(false)
                .canPushEntity(false)
                .canExtinguish(false)
                .supportsBoating(false)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY));
    }
}