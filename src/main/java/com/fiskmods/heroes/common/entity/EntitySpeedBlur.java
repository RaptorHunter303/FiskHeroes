package com.fiskmods.heroes.common.entity;

import java.util.LinkedList;

import com.fiskmods.heroes.client.json.trail.JsonTrail;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectTrail;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySpeedBlur extends EntityLivingBase
{
    public EntityPlayer anchorEntity;
    public JsonTrail trail;

    public float[] lightningFactor;
    public float[] particleFactor;
    public float[][] particleMotion;
    public float[][] particleOffset;
    public float[][] prevParticleOffset;
    public int progress;

    public EntitySpeedBlur(World world)
    {
        super(world);
        setSize(0.1F, 0.1F);
        init();
    }

    public EntitySpeedBlur(World world, EntityPlayer player)
    {
        super(world);
        anchorEntity = player;
        trail = SpeedsterHelper.getJsonTrail(player);

        if (trail.lightning != null)
        {
            lightningFactor = new float[trail.lightning.get().getDensity() * 2 + 1];

            for (int i = 0; i < lightningFactor.length; i++)
            {
                lightningFactor[i] += (Math.random() * 2 - 1) / 2 * width;
            }
        }

        if (trail.particles != null)
        {
            particleFactor = new float[trail.particles.getDensity() * 2 + 1];
            particleMotion = new float[trail.particles.getDensity()][3];
            particleOffset = new float[trail.particles.getDensity()][3];
            prevParticleOffset = new float[trail.particles.getDensity()][3];

            for (int i = 0; i < particleFactor.length; i++)
            {
                particleFactor[i] += (Math.random() * 2 - 1) / 2 * width;
            }

            for (int i = 0; i < particleMotion.length; i++)
            {
                for (int j = 0; j < 3; ++j)
                {
                    particleMotion[i][j] = (float) (Math.random() * 2 - 1) * trail.particles.getMotion();
                }
            }
        }

        setLocationAndAngles(player.prevPosX, player.prevPosY - player.yOffset, player.prevPosZ, player.rotationYaw, player.rotationPitch);
        setSize(player.width, player.height);
        init();

        ticksExisted = player.ticksExisted;
        swingProgress = player.swingProgress;
        prevSwingProgress = player.swingProgress;
        prevRenderYawOffset = player.renderYawOffset;
        renderYawOffset = player.renderYawOffset;
        prevRotationYawHead = player.rotationYawHead;
        rotationYawHead = player.rotationYawHead;
        prevRotationPitch = player.rotationPitch;
        rotationPitch = player.rotationPitch;
        limbSwingAmount = player.limbSwingAmount;
        prevLimbSwingAmount = player.limbSwingAmount;
        limbSwing = player.limbSwing;
    }

    public void init()
    {
        noClip = true;
        renderDistanceWeight = 10;
        ignoreFrustumCheck = true;
        preventEntitySpawning = false;
    }

    public Vec3 getLightningPosVector(int index)
    {
        if (lightningFactor != null)
        {
            float f = trail.lightning.get().getDiffer() / 0.435F * anchorEntity.width / 0.6F;
            return Vec3.createVectorHelper(posX + lightningFactor[index % lightningFactor.length] * f, posY, posZ + lightningFactor[(index + trail.lightning.get().getDensity()) % lightningFactor.length] * f);
        }

        return getPosition(1);
    }

    public Vec3 getParticlePosVector(int index)
    {
        if (particleFactor != null)
        {
            float scale = anchorEntity.width / 0.6F;
            float f = trail.particles.getDiffer() / 0.435F * scale;

            return Vec3.createVectorHelper(posX + particleFactor[index % particleFactor.length] * f + SHRenderHelper.interpolate(particleOffset[index][0], prevParticleOffset[index][0]) * scale, posY + SHRenderHelper.interpolate(particleOffset[index][1], prevParticleOffset[index][1]) * scale, posZ + particleFactor[(index + trail.particles.getDensity()) % particleFactor.length] * f + SHRenderHelper.interpolate(particleOffset[index][2], prevParticleOffset[index][2]) * scale);
        }

        return getPosition(1);
    }

    @Override
    public boolean isBurning()
    {
        return false;
    }

    @Override
    public void onUpdate()
    {
        int fade = trail.fade;

        if (trail.particles != null)
        {
            fade = Math.max(fade, trail.particles.getFade());
        }

        if (++progress > fade)
        {
            setDead();
            LinkedList<EntitySpeedBlur> list = HeroEffectTrail.getTrail(anchorEntity);

            if (list.contains(this))
            {
                list.remove(this);
                HeroEffectTrail.trailData.put(anchorEntity, list);
            }

            return;
        }

        if (particleMotion != null)
        {
            for (int i = 0; i < particleMotion.length; i++)
            {
                for (int j = 0; j < 3; ++j)
                {
                    prevParticleOffset[i][j] = particleOffset[i][j];
                    particleOffset[i][j] += particleMotion[i][j];
                    particleMotion[i][j] *= trail.particles.getSpeed();
                }
            }
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public boolean isEntityAlive()
    {
        return !isDead;
    }

    @Override
    public void setHealth(float f)
    {
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
    public ItemStack getHeldItem()
    {
        return anchorEntity.getHeldItem();
    }

    @Override
    public ItemStack getEquipmentInSlot(int slot)
    {
        return anchorEntity.getEquipmentInSlot(slot);
    }

    @Override
    public void setCurrentItemOrArmor(int i, ItemStack itemstack)
    {
    }

    @Override
    public ItemStack[] getLastActiveItems()
    {
        return new ItemStack[0];
    }
}
