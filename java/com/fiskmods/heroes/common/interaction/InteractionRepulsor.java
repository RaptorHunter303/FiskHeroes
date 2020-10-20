package com.fiskmods.heroes.common.interaction;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityRepulsorBlast;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

public class InteractionRepulsor extends InteractionBase
{
    public InteractionRepulsor()
    {
        requireModifier(Ability.REPULSOR_BLAST);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return SHData.AIMING.get(player);
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return Cooldown.REPULSOR.available(player);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            shoot(sender);
        }
        else if (sender == clientPlayer)
        {
            Cooldown.REPULSOR.set(sender);
        }
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
    
    public static void shoot(EntityLivingBase sender)
    {
        Hero hero = SHHelper.getHero(sender);
        float range = Rule.RANGE_REPULSOR.get(sender, hero);
        
        MovingObjectPosition rayTrace = SHHelper.rayTrace(sender, range, 4, 1);

        if (rayTrace != null && rayTrace.typeOfHit == MovingObjectType.ENTITY && rayTrace.entityHit instanceof EntityLivingBase)
        {
            EntityLivingBase entity = (EntityLivingBase) rayTrace.entityHit;
            entity.attackEntityFrom(ModDamageSources.REPULSOR.apply(sender), Rule.DMG_REPULSOR.get(sender, hero));

            double d0 = sender.posX - entity.posX;
            double d1;

            for (d1 = sender.posZ - entity.posZ; d0 * d0 + d1 * d1 < 1E-4; d1 = (Math.random() - Math.random()) * 0.01)
            {
                d0 = (Math.random() - Math.random()) * 0.01;
            }

            float f1 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
            float f2 = 1;
            entity.isAirBorne = true;
            entity.motionX /= 2;
            entity.motionY /= 2;
            entity.motionZ /= 2;
            entity.motionX -= d0 / f1 * f2;
            entity.motionY += f2;
            entity.motionZ -= d1 / f1 * f2;

            if (entity.motionY > 0.4)
            {
                entity.motionY = 0.4;
            }
        }

        double frontOffset = 0.6;
        double sideOffset = -0.3;
        Vec3 src = VectorHelper.getOffsetCoords(sender, sideOffset, -0.3, frontOffset);
        Vec3 dst = VectorHelper.getOffsetCoords(sender, sideOffset, 0, range);
        
        if (rayTrace != null && rayTrace.hitVec != null)
        {
            dst = rayTrace.hitVec;
        }

        sender.worldObj.spawnEntityInWorld(new EntityRepulsorBlast(sender.worldObj, sender, src, dst));
        sender.worldObj.playSoundAtEntity(sender, SHSounds.ABILITY_REPULSOR_BLAST.toString(), 2, 1);
    }
}
