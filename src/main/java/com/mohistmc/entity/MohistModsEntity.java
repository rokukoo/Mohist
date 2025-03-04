package com.mohistmc.entity;

import com.mohistmc.api.EntityAPI;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;

public class MohistModsEntity extends CraftEntity {

    public String entityName;

    public MohistModsEntity(CraftServer server, net.minecraft.world.entity.Entity entity) {
        super(server, entity);
        this.entityName = EntityAPI.entityName(entity);
    }

    @Override
    public String toString() {
        return "MohistModsEntity{" + entityName + '}';
    }

    @Override
    public EntityType getType() {
        return EntityAPI.entityType(entityName);
    }
}
