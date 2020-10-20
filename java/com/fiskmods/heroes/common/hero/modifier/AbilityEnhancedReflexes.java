package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.entity.gadget.EntityProjectile;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.FiskServerUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class AbilityEnhancedReflexes extends Ability
{
    public AbilityEnhancedReflexes(int tier)
    {
        super(tier);
    }

    @Override
    public boolean canTakeDamage(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount)
    {
        ItemStack heldItem = entity.getHeldItem();
        Entity projectile = source.getSourceOfDamage();

        if (projectile instanceof EntityArrow && !(projectile instanceof EntityProjectile))
        {
            if (heldItem == null && FiskServerUtils.isEntityLookingAt(entity, projectile))
            {
                if (projectile instanceof EntityTrickArrow)
                {
                    return !((EntityTrickArrow) projectile).onCaught(entity);
                }

                ItemStack arrowItem = new ItemStack(Items.arrow);

                if (arrowItem != null)
                {
                    arrowItem.stackSize = 1;
                    entity.setCurrentItemOrArmor(0, arrowItem);
                }

                projectile.setDead();

                return false;
            }
        }

        return super.canTakeDamage(entity, attacker, hero, source, amount);
    }
}
