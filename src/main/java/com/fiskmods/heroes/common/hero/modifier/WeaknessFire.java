package com.fiskmods.heroes.common.hero.modifier;

import java.util.List;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.hero.Hero;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

public class WeaknessFire extends Weakness
{
    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END && enabled && !entity.worldObj.isRemote)
        {
            boolean flag = entity.isBurning();
            float radius;

            if (!flag && (radius = Rule.RADIUS_FIREWEAKNESS.get(entity, hero)) > 0)
            {
                AxisAlignedBB aabb = entity.boundingBox.expand(radius, radius, radius);
                flag = entity.worldObj.func_147470_e(aabb);

                if (!flag)
                {
                    for (Entity target : (List<Entity>) entity.worldObj.selectEntitiesWithinAABB(Entity.class, aabb, IEntitySelector.selectAnything))
                    {
                        if (target.isBurning() && !(target instanceof EntityHanging))
                        {
                            flag = true;
                            break;
                        }
                    }
                }
            }

            if (flag)
            {
                int duration = Rule.TICKS_FIREWEAKNESS.get(entity, hero);

                if (duration > 0)
                {
                    entity.addPotionEffect(new PotionEffect(Potion.confusion.id, duration, 0));
                    entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, duration, 3));
                    entity.addPotionEffect(new PotionEffect(Potion.weakness.id, duration, 2));
                }
            }
        }
    }

    @Override
    public float damageReduction(EntityLivingBase entity, Hero hero, DamageSource source, float reduction)
    {
        reduction = super.damageReduction(entity, hero, source, reduction);
        return source.isFireDamage() ? reduction / 2 : reduction;
    }
}
