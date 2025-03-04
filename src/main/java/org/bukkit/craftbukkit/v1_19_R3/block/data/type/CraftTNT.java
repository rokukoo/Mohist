package org.bukkit.craftbukkit.v1_19_R3.block.data.type;

import org.bukkit.block.data.type.TNT;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;

public abstract class CraftTNT extends CraftBlockData implements TNT {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty UNSTABLE = getBoolean("unstable");

    @Override
    public boolean isUnstable() {
        return get(UNSTABLE);
    }

    @Override
    public void setUnstable(boolean unstable) {
        set(UNSTABLE, unstable);
    }
}
