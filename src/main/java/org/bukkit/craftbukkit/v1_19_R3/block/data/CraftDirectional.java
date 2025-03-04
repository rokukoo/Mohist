package org.bukkit.craftbukkit.v1_19_R3.block.data;

import org.bukkit.block.data.Directional;

public abstract class CraftDirectional extends CraftBlockData implements Directional {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> FACING = getEnum("facing");

    @Override
    public org.bukkit.block.BlockFace getFacing() {
        return get(FACING, org.bukkit.block.BlockFace.class);
    }

    @Override
    public void setFacing(org.bukkit.block.BlockFace facing) {
        set(FACING, facing);
    }

    @Override
    public java.util.Set<org.bukkit.block.BlockFace> getFaces() {
        return getValues(FACING, org.bukkit.block.BlockFace.class);
    }
}
