package com.fiskmods.heroes.common.entity.arrow;

import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.IPiercingProjectile;
import com.fiskmods.heroes.common.hero.modifier.Weakness;
import com.fiskmods.heroes.common.hero.modifier.WeaknessMetalSkin.HeatSource;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityBlazeArrow extends EntityTrickArrow implements IPiercingProjectile
{
    public EntityBlazeArrow(World world)
    {
        super(world);
    }

    public EntityBlazeArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityBlazeArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityBlazeArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected boolean shouldSpawnParticles()
    {
        return true;
    }

    @Override
    protected void spawnTrailingParticles()
    {
        float spread = 0.1F;

        for (int i = 0; i < 3; ++i)
        {
            SHParticleType.SHORT_FLAME.spawn(posX, posY, posZ, (rand.nextFloat() - 0.5F) * spread, (rand.nextFloat() - 0.5F) * spread, (rand.nextFloat() - 0.5F) * spread);
        }
    }

    @Override
    protected DamageSource getDamageSource(Entity entity)
    {
        return super.getDamageSource(entity).setFireDamage();
    }

    @Override
    protected void handlePostDamageEffects(EntityLivingBase entityHit)
    {
        HeatSource.BLAZE_ARROW.applyHeatWithNotify(entityHit);
        super.handlePostDamageEffects(entityHit);
    }

    @Override
    protected void onImpactEntity(MovingObjectPosition mop)
    {
        int i = -1;

        if (mop.entityHit != null)
        {
            i = SHData.METAL_HEAT_COOLDOWN.get(mop.entityHit);
            SHData.METAL_HEAT_COOLDOWN.setWithoutNotify(mop.entityHit, 1);
        }

        super.onImpactEntity(mop);

        if (i != -1)
        {
            SHData.METAL_HEAT_COOLDOWN.setWithoutNotify(mop.entityHit, i);
        }
    }

    @Override
    public boolean canPierceDurability(EntityLivingBase entity)
    {
        return SHHelper.hasModifier(entity, Weakness.METAL_SKIN);
    }
}
