package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityLaserBolt;
import com.fiskmods.heroes.common.entity.EntityLaserBolt.Type;
import com.fiskmods.heroes.common.entity.EntityRepulsorBlast;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Hero.Permission;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemChronosRifle extends ItemUntextured implements IReloadWeapon, IScopeWeapon
{
    public ItemChronosRifle()
    {
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        HeroIteration iter;

        if (SHData.AIMING.get(player) && (iter = SHHelper.getHeroIter(player)) != null && iter.hero.hasPermission(player, Permission.USE_CHRONOS_RIFLE))
        {
            if (stack.hasTagCompound() && stack.getTagCompound().getCompoundTag("fisktag").getBoolean("Automatic"))
            {
                player.setItemInUse(stack, getMaxItemUseDuration(stack));
            }
            else if (SHData.RELOAD_TIMER.get(player) == 0)
            {
                shoot(stack, player, iter);
            }
        }

        return stack;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
    {
        int rate = 3;
        float windup = 5;
        count = getMaxItemUseDuration(stack) - count;

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fisktag", NBT.TAG_COMPOUND))
        {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("fisktag");

            if (tag.hasKey("AutoRate", NBT.TAG_INT))
            {
                rate = tag.getInteger("AutoRate");
            }

            if (tag.hasKey("AutoWindup", NBT.TAG_ANY_NUMERIC))
            {
                windup = tag.getInteger("AutoWindup");
            }
        }

        int offset = (int) Math.max(rate + windup - count / 10F, rate);

        if (offset == 1 || count % offset == offset - 1)
        {
            HeroIteration iter;

            if (SHData.AIMING.get(player) && (iter = SHHelper.getHeroIter(player)) != null && iter.hero.hasPermission(player, Permission.USE_CHRONOS_RIFLE))
            {
                shoot(stack, player, iter);
            }
            else
            {
                player.stopUsingItem();
            }
        }
    }

    public void shoot(ItemStack stack, EntityPlayer player, HeroIteration iter)
    {
        if (!player.worldObj.isRemote)
        {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fisktag", NBT.TAG_COMPOUND))
            {
                NBTTagCompound tag = stack.getTagCompound().getCompoundTag("fisktag");
                Type type = Type.RIFLE;

                if (tag.hasKey("Type", NBT.TAG_STRING))
                {
                    try
                    {
                        type = Type.valueOf(tag.getString("Type"));
                    }
                    catch (IllegalArgumentException e)
                    {
                        tag.removeTag("Type");
                    }
                }

                float spread = tag.getFloat("Spread");
                int amount = tag.hasKey("Amount", NBT.TAG_INT) ? tag.getInteger("Amount") : 1;
                boolean sniper = tag.getBoolean("Sniper");

                if (tag.getBoolean("Scatter"))
                {
                    int j = (amount - 1) / 2;
                    float f = player.rotationYaw;

                    for (int i = -j; i <= j; ++i)
                    {
                        player.rotationYaw = f + i * spread * 10;
                        shoot(player, type, iter, sniper, 0, 0, 0);
                    }

                    player.rotationYaw = f;
                }
                else if (amount > 1 || spread > 0)
                {
                    for (int i = 0; i < amount; ++i)
                    {
                        shoot(player, type, iter, sniper, (itemRand.nextFloat() * 2 - 1) * spread, (itemRand.nextFloat() * 2 - 1) * spread, (itemRand.nextFloat() * 2 - 1) * spread);
                    }
                }
                else
                {
                    shoot(player, type, iter, sniper, 0, 0, 0);
                }
            }
            else
            {
                player.worldObj.spawnEntityInWorld(new EntityLaserBolt(player.worldObj, player, Type.RIFLE, iter, true));
            }
        }

        player.playSound(SHSounds.ITEM_CHRONOSRIFLE_SHOOT.toString(), 1, 1.0F / (itemRand.nextFloat() * 0.3F + 0.7F));
        SHData.RELOAD_TIMER.setWithoutNotify(player, 1.0F);
    }

    private void shoot(EntityPlayer player, Type type, HeroIteration iter, boolean flag, float mX, float mY, float mZ)
    {
        if (flag)
        {
            double dist = 256;
            float yaw = player.rotationYaw, pitch = player.rotationPitch;
            player.rotationYaw += mX * 15;
            player.rotationPitch += mY * 15;

            MovingObjectPosition mop = SHHelper.rayTrace(player, dist, 4, 1);
            player.rotationYaw = yaw;
            player.rotationPitch = pitch;

            if (mop != null)
            {
                float scope = SHData.SCOPE_TIMER.get(player);

                if (mop.entityHit != null)
                {
                    float damage = Rule.DMG_LASERBOLT.get(player, iter) * type.damage;

                    if (scope > 0)
                    {
                        damage *= 1.6F;
                    }

                    mop.entityHit.hurtResistantTime = 0;
                    mop.entityHit.attackEntityFrom(ModDamageSources.causeLaserDamage(null, player), damage);
                }

                if (mop.hitVec != null)
                {
                    player.worldObj.spawnEntityInWorld(new EntityRepulsorBlast(player.worldObj, player, VectorHelper.getOffsetCoords(player, -0.25 * (1 - scope), -0.25, 0.9), mop.hitVec));
                }
            }
        }
        else
        {
            EntityLaserBolt entity = new EntityLaserBolt(player.worldObj, player, type, iter, true);
            entity.motionX += mX;
            entity.motionY += mY;
            entity.motionZ += mZ;

            player.worldObj.spawnEntityInWorld(entity);
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 200;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getReloadTime(ItemStack stack, EntityPlayer player, Hero hero)
    {
        int i = Rule.COOLDOWN_CHRONOSRIFLE.get(player, hero);

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fisktag", NBT.TAG_COMPOUND))
        {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("fisktag");
            return tag.hasKey("Cooldown", NBT.TAG_ANY_NUMERIC) ? (int) (tag.getFloat("Cooldown") * i) : i;
        }

        return i;
    }

    @Override
    public boolean canUseScope(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fisktag", NBT.TAG_COMPOUND))
        {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("fisktag");
            return !tag.hasKey("Scope", NBT.TAG_ANY_NUMERIC) || tag.getBoolean("Scope");
        }

        return true;
    }

    @Override
    public boolean isProperScope()
    {
        return true;
    }
}
