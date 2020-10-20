package com.fiskmods.heroes.common.entity.arrow;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityGrossArrow extends EntityTrickArrow
{
    public EntityGrossArrow(World world)
    {
        super(world);
    }

    public EntityGrossArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityGrossArrow(World world, EntityLivingBase shooter, float velocity)
    {
        super(world, shooter, velocity);
    }

    public EntityGrossArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontal)
    {
        super(world, shooter, velocity, horizontal);
    }

    @Override
    protected void handlePostDamageEffects(EntityLivingBase entityHit)
    {
        super.handlePostDamageEffects(entityHit);
        entityHit.addPotionEffect(new PotionEffect(Potion.confusion.id, 180, 0));
    }
}
