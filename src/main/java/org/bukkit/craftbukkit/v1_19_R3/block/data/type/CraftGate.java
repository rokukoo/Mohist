package org.bukkit.craftbukkit.v1_19_R3.block.data.type;

import org.bukkit.block.data.type.Gate;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;

public abstract class CraftGate extends CraftBlockData implements Gate {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty IN_WALL = getBoolean("in_wall");

    @Override
    public boolean isInWall() {
        return get(IN_WALL);
    }

    @Override
    public void setInWall(boolean inWall) {
        set(IN_WALL, inWall);
    }
}
