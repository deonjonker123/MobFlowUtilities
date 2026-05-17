package com.misterd.mobflowutilities.fluid;

import com.misterd.mobflowutilities.entity.custom.CollectorBlockEntity;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class LiquidXpFluidTank implements ResourceHandler<FluidResource> {

    public static final int MB_PER_XP = 20;

    private final CollectorBlockEntity collector;
    private final Journal journal = new Journal();

    public LiquidXpFluidTank(CollectorBlockEntity collector) {
        this.collector = collector;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        if (collector.getStoredXP() <= 0) return FluidResource.EMPTY;
        return FluidResource.of(MFUFluids.LIQUID_XP_SOURCE.get());
    }

    @Override
    public long getAmountAsLong(int index) {
        int xp = collector.getStoredXP();
        if (xp > Integer.MAX_VALUE / MB_PER_XP) return Integer.MAX_VALUE;
        return (long) xp * MB_PER_XP;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        if (resource.isEmpty()) return true;
        return resource.is(MFUFluids.LIQUID_XP_SOURCE.get());
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (!isValid(index, resource)) return 0;
        int acceptable = amount - (amount % MB_PER_XP);
        if (acceptable <= 0) return 0;
        int xpToAdd = acceptable / MB_PER_XP;
        journal.updateSnapshots(transaction);
        collector.setStoredXP(collector.getStoredXP() + xpToAdd);
        return acceptable;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (!resource.is(MFUFluids.LIQUID_XP_SOURCE.get())) return 0;
        int drainable = amount - (amount % MB_PER_XP);
        if (drainable <= 0) return 0;
        int xpNeeded = drainable / MB_PER_XP;
        int xpAvailable = Math.min(collector.getStoredXP(), xpNeeded);
        if (xpAvailable <= 0) return 0;
        journal.updateSnapshots(transaction);
        collector.setStoredXP(collector.getStoredXP() - xpAvailable);
        return xpAvailable * MB_PER_XP;
    }

    private class Journal extends SnapshotJournal<Integer> {
        @Override
        protected Integer createSnapshot() {
            return collector.getStoredXP();
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            collector.setStoredXP(snapshot);
        }

        @Override
        protected void onRootCommit(Integer originalState) {
            collector.setChangedAndUpdate();
        }
    }
}