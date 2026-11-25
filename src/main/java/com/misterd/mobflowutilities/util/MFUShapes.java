package com.misterd.mobflowutilities.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public final class MFUShapes {

    private MFUShapes() {}

    public static Map<Direction, VoxelShape> rotateHorizontal(VoxelShape north) {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        shapes.put(Direction.NORTH, north);
        shapes.put(Direction.EAST, rotate(north, Direction.EAST));
        shapes.put(Direction.SOUTH, rotate(north, Direction.SOUTH));
        shapes.put(Direction.WEST, rotate(north, Direction.WEST));
        return shapes;
    }

    private static VoxelShape rotate(VoxelShape north, Direction facing) {
        VoxelShape result = Shapes.empty();
        for (AABB box : north.toAabbs()) {
            AABB rotated = switch (facing) {
                case EAST -> new AABB(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX);
                case SOUTH -> new AABB(1 - box.maxX, box.minY, 1 - box.maxZ, 1 - box.minX, box.maxY, 1 - box.minZ);
                case WEST -> new AABB(box.minZ, box.minY, 1 - box.maxX, box.maxZ, box.maxY, 1 - box.minX);
                default -> box;
            };
            result = Shapes.or(result, Shapes.create(rotated));
        }
        return result.optimize();
    }
}
