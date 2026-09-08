package com.misterd.mobflowutilities.fluid;

import com.misterd.mobflowutilities.blockentity.custom.CollectorBlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class LiquidXpFluidTank implements IFluidHandler {

    public static final int MB_PER_XP = 20;

    private final CollectorBlockEntity collector;

    public LiquidXpFluidTank(CollectorBlockEntity collector) {
        this.collector = collector;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        int xp = collector.getStoredXP();
        if (xp <= 0) return FluidStack.EMPTY;
        long amount = (long) xp * MB_PER_XP;
        if (amount > Integer.MAX_VALUE) amount = Integer.MAX_VALUE;
        return new FluidStack(MFUFluids.LIQUID_XP_SOURCE.get(), (int) amount);
    }

    @Override
    public int getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return stack.getFluid() == MFUFluids.LIQUID_XP_SOURCE.get();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(0, resource)) return 0;

        int amount = resource.getAmount();
        int acceptable = amount - (amount % MB_PER_XP);
        if (acceptable <= 0) return 0;

        if (action.execute()) {
            int xpToAdd = acceptable / MB_PER_XP;
            collector.setStoredXP(collector.getStoredXP() + xpToAdd);
        }
        return acceptable;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || resource.getFluid() != MFUFluids.LIQUID_XP_SOURCE.get()) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        int drainable = maxDrain - (maxDrain % MB_PER_XP);
        if (drainable <= 0) return FluidStack.EMPTY;

        int xpNeeded = drainable / MB_PER_XP;
        int xpAvailable = Math.min(collector.getStoredXP(), xpNeeded);
        if (xpAvailable <= 0) return FluidStack.EMPTY;

        if (action.execute()) {
            collector.setStoredXP(collector.getStoredXP() - xpAvailable);
        }
        return new FluidStack(MFUFluids.LIQUID_XP_SOURCE.get(), xpAvailable * MB_PER_XP);
    }
}