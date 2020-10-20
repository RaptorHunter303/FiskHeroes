package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.SHReflection;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityEarthCrack extends Entity implements IEntityAdditionalSpawnData
{
    public EntityLivingBase caster, target;

    public EntityEarthCrack(World world)
    {
        super(world);
        noClip = true;
        renderDistanceWeight = 10;
        ignoreFrustumCheck = true;
        preventEntitySpawning = false;
        setSize(0.1F, 0.1F);
    }

    public EntityEarthCrack(World world, EntityLivingBase caster, EntityLivingBase target)
    {
        this(world);
        this.caster = caster;
        this.target = target;

        int x = MathHelper.floor_double(target.posX);
        int y = MathHelper.floor_double(target.posY) - 1;
        int z = MathHelper.floor_double(target.posZ);
        Block block = null;

        while (y > 0 && ((block = worldObj.getBlock(x, y, z)).isAir(worldObj, x, y, z) || !block.isSideSolid(worldObj, x, y, z, ForgeDirection.UP)))
        {
            --y;
        }

        if (block != null)
        {
            y += block.getBlockBoundsMaxY();
        }

        setPosition(x + 0.5, y, z + 0.5);
    }

    @Override
    public void onUpdate()
    {
        if (target != null)
        {
            float f = SHConstants.TICKS_EARTHCRACK;
            target.motionX = target.motionZ = 0;

            if (ticksExisted < f / 2.5F)
            {
                target.motionY += 0.075 + 0.03 * FiskMath.curveCrests(Math.min(ticksExisted / f * 2.5F, 1));
            }
            else if (ticksExisted < f - 15)
            {
                target.motionY *= 0.9;
                target.motionY += 0.075;
            }
            else
            {
                target.motionY -= 0.45;
                target.fallDistance = 0;
            }
        }

        if (++ticksExisted > SHConstants.TICKS_EARTHCRACK || target != null && target.isDead)
        {
            if (target != null && !target.isDead && !worldObj.isRemote)
            {
                if (target instanceof EntityCreeper)
                {
                    SHReflection.creeperExplode((EntityCreeper) target);
                }
                else
                {
                    EntityLivingBase entity = SHHelper.filterDuplicate(caster);
                    
                    target.hurtResistantTime = 0;
                    target.attackEntityFrom(ModDamageSources.EARTH_CRACK.apply(entity), Rule.DMG_SPELL_EARTHSWALLOW.getHero(caster));
                    playSound("random.fizz", 2, 1.5F);
                }
            }

            setDead();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    @Override
    public boolean isBurning()
    {
        return false;
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound nbttagcompound)
    {
        return false;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void writeSpawnData(ByteBuf buf)
    {
        buf.writeInt(caster != null ? caster.getEntityId() : -1);
        buf.writeInt(target != null ? target.getEntityId() : -1);
    }

    @Override
    public void readSpawnData(ByteBuf buf)
    {
        Entity entity = worldObj.getEntityByID(buf.readInt());

        if (entity instanceof EntityLivingBase)
        {
            caster = (EntityLivingBase) entity;
        }

        entity = worldObj.getEntityByID(buf.readInt());

        if (entity instanceof EntityLivingBase)
        {
            target = (EntityLivingBase) entity;
        }
    }
}
