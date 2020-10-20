package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.asm.ASMHooks;
import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.world.ModDimensions;
import com.fiskmods.heroes.util.FiskServerUtils;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

public class AbilitySizeManipulation extends Ability
{
    public static final String FUNC_MIN_SIZE = "getMinSize";
    public static final String FUNC_MAX_SIZE = "getMaxSize";
    public static final String FUNC_INSTANT = "isInstant";

    public static final String KEY_SHRINK = "SHRINK";
    public static final String KEY_GROW = "GROW";

    public AbilitySizeManipulation(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled && entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            float scale = SHData.SCALE.get(player);
            float min = hero.getFuncFloat(player, FUNC_MIN_SIZE, 0.5F);
            float max = hero.getFuncFloat(player, FUNC_MAX_SIZE, 1);
            boolean instant = hero.getFuncBoolean(player, FUNC_INSTANT, true);

            float increment;

            if (instant)
            {
                increment = (max - min) / 7;
            }
            else
            {
                increment = (max - min) / 60;

                if (!SHData.GROWING.get(player))
                {
                    increment *= 2;
                }
            }

            if (player.dimension != ModDimensions.QUANTUM_REALM_ID)
            {
                if (SHData.GROWING.get(player))
                {
                    if (instant && scale <= min)
                    {
                        player.playSound(SHSounds.ABILITY_SIZEMANIPULATION_GROW.toString(), 1.0F, 1.0F);
                    }

                    if (scale < max)
                    {
                        double d = scale + increment;
                        double width = player.width / scale * d;
                        double height = player.height / scale * d;
                        boolean flag = !Rule.ALLOW_QR.get(player) && SHData.QR_TIMER.get(player) >= 0.9F;

                        if (!flag)
                        {
                            AxisAlignedBB aabb = player.boundingBox;
                            aabb = AxisAlignedBB.getBoundingBox(aabb.minX, aabb.minY, aabb.minZ, aabb.minX + width, aabb.minY + height, aabb.minZ + width);
                            flag = player.worldObj.getCollidingBoundingBoxes(player, aabb).isEmpty();
                        }

                        if (flag)
                        {
                            SHData.SCALE.incrWithoutNotify(player, increment);
                        }
                    }
                    else
                    {
                        SHData.GROWING.set(player, false);
                    }
                }
                else if (SHData.SHRINKING.get(player))
                {
                    if (instant && scale >= max)
                    {
                        player.playSound(SHSounds.ABILITY_SIZEMANIPULATION_SHRINK.toString(), 1.0F, 1.0F);
                    }

                    if (scale > min)
                    {
                        SHData.SCALE.incrWithoutNotify(player, -increment);
                    }
                    else
                    {
                        SHData.SHRINKING.set(player, false);
                    }
                }

                if (entity.worldObj.isRemote && FiskHeroes.proxy.isClientPlayer(entity))
                {
                    boolean shrink = hero.isKeyPressed(entity, KEY_SHRINK);
                    boolean grow = hero.isKeyPressed(entity, KEY_GROW);

                    if (instant)
                    {
                        if (shrink)
                        {
                            SHData.SHRINKING.set(player, true);
                            SHData.GROWING.set(player, false);
                        }

                        if (grow)
                        {
                            double width = player.width / scale * max;
                            double height = player.height / scale * max;

                            AxisAlignedBB aabb = player.boundingBox;
                            aabb = AxisAlignedBB.getBoundingBox(aabb.minX, aabb.minY, aabb.minZ, aabb.minX + width, aabb.minY + height, aabb.minZ + width);

                            if (player.worldObj.getCollidingBoundingBoxes(player, aabb).isEmpty())
                            {
                                SHData.SHRINKING.set(player, false);
                                SHData.GROWING.set(player, true);
                            }
                        }
                    }
                    else
                    {
                        SHData.SHRINKING.set(player, shrink);
                        SHData.GROWING.set(player, grow);
                    }
                }
            }

            SHData.SCALE.clampWithoutNotify(player, min, max);
        }
    }

    @Override
    public boolean canTakeDamage(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount)
    {
        if (entity instanceof EntityPlayer && SHData.SCALE.get(entity) <= hero.getFuncFloat(entity, FUNC_MIN_SIZE, 0.5F))
        {
            if (!(attacker instanceof EntityPlayer) && FiskServerUtils.isMeleeDamage(source) && rand.nextInt(3) != 0)
            {
                if (entity.hurtResistantTime == 0)
                {
                    entity.hurtResistantTime = 5;
                }

                return false;
            }
        }

        return super.canTakeDamage(entity, attacker, hero, source, amount);
    }

    @Override
    public float damageDealt(EntityLivingBase entity, EntityLivingBase target, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        amount = super.damageDealt(entity, target, hero, source, amount, originalAmount);

        if (entity instanceof EntityPlayer && FiskServerUtils.isMeleeDamage(source))
        {
            EntityPlayer player = (EntityPlayer) entity;
            float scale = SHData.SCALE.get(entity);

            if (scale < 1)
            {
                float f = hero.getFuncFloat(player, FUNC_MIN_SIZE, 0.5F) / scale;
                amount *= 1 + f * 0.25F;
            }
            else
            {
                amount *= ASMHooks.getStrengthScale(player);
            }
        }

        return amount;
    }
}
