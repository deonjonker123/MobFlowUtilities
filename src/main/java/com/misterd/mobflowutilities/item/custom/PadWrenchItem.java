package com.misterd.mobflowutilities.item.custom;

import java.util.List;

import com.misterd.mobflowutilities.block.custom.ControllerBlock;
import com.misterd.mobflowutilities.block.custom.DamagePadBlock;
import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.component.custom.PadWrenchData;
import com.misterd.mobflowutilities.entity.custom.ControllerBlockEntity;
import com.misterd.mobflowutilities.entity.custom.DamagePadBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PadWrenchItem extends Item {
    public PadWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            toggleOperationMode(stack, player);
        } else {
            toggleSelectionMode(stack, player);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockState state = level.getBlockState(pos);

        if (player == null) {
            return InteractionResult.FAIL;
        }

        PadWrenchData data = stack.getOrDefault(MFUDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);

        if (state.getBlock() instanceof ControllerBlock) {
            selectController(stack, pos, player);
            return InteractionResult.SUCCESS;
        } else if (state.getBlock() instanceof DamagePadBlock) {
            if (data.selectionMode() == PadWrenchData.SelectionMode.SINGLE) {
                handleSinglePadAction(level, pos, stack, player);
            } else {
                handleMultiPadAction(level, pos, stack, player);
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    private void toggleOperationMode(ItemStack stack, Player player) {
        PadWrenchData currentData = stack.getOrDefault(MFUDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);
        PadWrenchData.OperationMode newMode = currentData.operationMode() == PadWrenchData.OperationMode.ADD
                ? PadWrenchData.OperationMode.REMOVE
                : PadWrenchData.OperationMode.ADD;
        PadWrenchData newData = currentData.withOperationMode(newMode);
        stack.set(MFUDataComponents.PAD_WRENCH_DATA.get(), newData);

        Component message = newMode == PadWrenchData.OperationMode.ADD
                ? Component.translatable("item.mobflowutilities.pad_wrench.mode.add")
                : Component.translatable("item.mobflowutilities.pad_wrench.mode.remove");
        player.displayClientMessage(message, true);
    }

    private void toggleSelectionMode(ItemStack stack, Player player) {
        PadWrenchData currentData = stack.getOrDefault(MFUDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);
        PadWrenchData.SelectionMode newMode = currentData.selectionMode() == PadWrenchData.SelectionMode.SINGLE
                ? PadWrenchData.SelectionMode.MULTI
                : PadWrenchData.SelectionMode.SINGLE;
        PadWrenchData newData = currentData.withSelectionMode(newMode);
        stack.set(MFUDataComponents.PAD_WRENCH_DATA.get(), newData);

        Component message = newMode == PadWrenchData.SelectionMode.SINGLE
                ? Component.translatable("item.mobflowutilities.pad_wrench.selection.single")
                : Component.translatable("item.mobflowutilities.pad_wrench.selection.multi");
        player.displayClientMessage(message, true);
    }

    private void selectController(ItemStack stack, BlockPos controllerPos, Player player) {
        PadWrenchData currentData = stack.getOrDefault(MFUDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);
        PadWrenchData newData = currentData.withSelectedController(controllerPos);
        stack.set(MFUDataComponents.PAD_WRENCH_DATA.get(), newData);

        Component message = Component.translatable(
                "item.mobflowutilities.pad_wrench.controller.selected",
                controllerPos.getX(), controllerPos.getY(), controllerPos.getZ()
        );
        player.displayClientMessage(message, true);
    }

    private void handleSinglePadAction(Level level, BlockPos padPos, ItemStack stack, Player player) {
        PadWrenchData data = stack.getOrDefault(MFUDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);

        if (data.selectedController() == null) {
            Component message = Component.translatable("item.mobflowutilities.pad_wrench.error.no_controller");
            player.displayClientMessage(message, true);
            return;
        }

        BlockEntity be = level.getBlockEntity(padPos);
        if (!(be instanceof DamagePadBlockEntity padEntity)) {
            return;
        }

        if (data.operationMode() == PadWrenchData.OperationMode.ADD) {
            padEntity.setControllerPos(data.selectedController());

            BlockEntity controllerBE = level.getBlockEntity(data.selectedController());
            if (controllerBE instanceof ControllerBlockEntity controller) {
                controller.addPad(padPos);
            }

            Component message = Component.translatable("item.mobflowutilities.pad_wrench.pad.linked");
            player.displayClientMessage(message, true);
        } else {
            BlockPos oldControllerPos = padEntity.getControllerPos();
            padEntity.clearControllerPos();

            if (oldControllerPos != null) {
                BlockEntity oldControllerBE = level.getBlockEntity(oldControllerPos);
                if (oldControllerBE instanceof ControllerBlockEntity oldController) {
                    oldController.removePad(padPos);
                }
            }

            Component message = Component.translatable("item.mobflowutilities.pad_wrench.pad.unlinked");
            player.displayClientMessage(message, true);
        }
    }

    private void handleMultiPadAction(Level level, BlockPos padPos, ItemStack stack, Player player) {
        PadWrenchData data = stack.getOrDefault(MFUDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);

        if (!player.isShiftKeyDown()) {
            return;
        }

        if (data.firstMultiPos() == null) {
            PadWrenchData newData = data.withFirstMultiPos(padPos);
            stack.set(MFUDataComponents.PAD_WRENCH_DATA.get(), newData);
            Component message = Component.translatable("item.mobflowutilities.pad_wrench.multi.start", padPos.getX(), padPos.getY(), padPos.getZ());
            player.displayClientMessage(message, true);
        } else {
            processMultiSelection(level, data.firstMultiPos(), padPos, stack, player);
            PadWrenchData newData = data.withFirstMultiPos(null);
            stack.set(MFUDataComponents.PAD_WRENCH_DATA.get(), newData);
        }
    }

    private void processMultiSelection(Level level, BlockPos pos1, BlockPos pos2, ItemStack stack, Player player) {
        PadWrenchData data = stack.getOrDefault(MFUDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);

        if (data.selectedController() == null && data.operationMode() == PadWrenchData.OperationMode.ADD) {
            Component message = Component.translatable("item.mobflowutilities.pad_wrench.error.no_controller");
            player.displayClientMessage(message, true);
            return;
        }

        int minX = Math.min(pos1.getX(), pos2.getX());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        ControllerBlockEntity controller = null;
        if (data.selectedController() != null) {
            BlockEntity be = level.getBlockEntity(data.selectedController());
            if (be instanceof ControllerBlockEntity controllerBE) {
                controller = controllerBE;
            }
        }

        int processedCount = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(currentPos);
                    if (!(state.getBlock() instanceof DamagePadBlock)) continue;

                    BlockEntity maybePadBE = level.getBlockEntity(currentPos);
                    if (!(maybePadBE instanceof DamagePadBlockEntity padEntity)) continue;

                    if (data.operationMode() == PadWrenchData.OperationMode.ADD) {
                        padEntity.setControllerPos(data.selectedController());
                        if (controller != null) {
                            controller.addPad(currentPos);
                        }
                    } else {
                        BlockPos oldControllerPos = padEntity.getControllerPos();
                        padEntity.clearControllerPos();
                        if (oldControllerPos != null) {
                            BlockEntity oldControllerBE = level.getBlockEntity(oldControllerPos);
                            if (oldControllerBE instanceof ControllerBlockEntity oldController) {
                                oldController.removePad(currentPos);
                            }
                        }
                    }

                    processedCount++;
                }
            }
        }

        Component message = data.operationMode() == PadWrenchData.OperationMode.ADD
                ? Component.translatable("item.mobflowutilities.pad_wrench.multi.linked", processedCount)
                : Component.translatable("item.mobflowutilities.pad_wrench.multi.unlinked", processedCount);

        player.displayClientMessage(message, true);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.mobflowutilities.pad_wrench.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));

        PadWrenchData data = stack.getOrDefault(MFUDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);

        Component operationText = data.operationMode() == PadWrenchData.OperationMode.ADD
                ? Component.translatable("item.mobflowutilities.pad_wrench.tooltip.operation.add").withStyle(ChatFormatting.GREEN)
                : Component.translatable("item.mobflowutilities.pad_wrench.tooltip.operation.remove").withStyle(ChatFormatting.GOLD);
        tooltipComponents.add(operationText);

        Component selectionText = data.selectionMode() == PadWrenchData.SelectionMode.SINGLE
                ? Component.translatable("item.mobflowutilities.pad_wrench.tooltip.selection.single").withStyle(ChatFormatting.AQUA)
                : Component.translatable("item.mobflowutilities.pad_wrench.tooltip.selection.multi").withStyle(ChatFormatting.RED);
        tooltipComponents.add(selectionText);

        MutableComponent controllerText;
        if (data.selectedController() != null) {
            controllerText = Component.translatable(
                    "item.mobflowutilities.pad_wrench.tooltip.controller.selected",
                    data.selectedController().getX(), data.selectedController().getY(), data.selectedController().getZ()
            ).withStyle(ChatFormatting.YELLOW);
            tooltipComponents.add(controllerText);
        } else {
            controllerText = Component.translatable("item.mobflowutilities.pad_wrench.tooltip.controller.none").withStyle(ChatFormatting.GOLD);
            tooltipComponents.add(controllerText);
        }

        if (data.firstMultiPos() != null) {
            MutableComponent multiText = Component.translatable(
                    "item.mobflowutilities.pad_wrench.tooltip.multi.active",
                    data.firstMultiPos().getX(), data.firstMultiPos().getY(), data.firstMultiPos().getZ()
            ).withStyle(ChatFormatting.YELLOW);
            tooltipComponents.add(multiText);
        }

        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.translatable("item.mobflowutilities.pad_wrench.tooltip.usage.selection").withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(Component.translatable("item.mobflowutilities.pad_wrench.tooltip.usage.operation").withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(Component.translatable("item.mobflowutilities.pad_wrench.tooltip.usage.controller").withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(Component.translatable("item.mobflowutilities.pad_wrench.tooltip.usage.pad").withStyle(ChatFormatting.DARK_GRAY));
    }
}