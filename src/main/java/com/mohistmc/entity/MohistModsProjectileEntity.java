package com.mohistmc.entity;

import com.mohistmc.api.EntityAPI;
import net.minecraft.world.entity.projectile.Projectile;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftProjectile;
import org.bukkit.entity.EntityType;

public class MohistModsProjectileEntity extends CraftProjectile {

    public String entityName;

    public MohistModsProjectileEntity(CraftServer server, Projectile entity) {
        super(server, entity);
        this.entityName = EntityAPI.entityName(entity);
    }


    @Override
    public EntityType getType() {
        return EntityAPI.entityType(entityName, EntityType.FORGE_MOD_PROJECTILE);
    }

    @Override
    public String toString() {
        return "MohistModsProjectileEntity{" + entityName + '}';
    }
}

