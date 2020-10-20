package com.fiskmods.heroes.common.move;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MovingObjectPosition;

public class MoveChargedPunch extends Move
{
    public static final String KEY_DAMAGE = "DAMAGE";
    public static final String KEY_KNOCKBACK = "KNOCKBACK";

    public MoveChargedPunch(int tier)
    {
        super(tier, MouseAction.LEFT_CLICK.with(MoveSensitivity.ENTITY));
        setModifierFormat(Number::floatValue);
    }

    @Override
    public String localizeModifier(String key)
    {
        switch (key)
        {
        case KEY_DAMAGE:
            return "move.modifier.damage";
        case KEY_KNOCKBACK:
            return "move.modifier.knockback";
        default:
            return null;
        }
    }

    @Override
    public boolean onActivated(EntityLivingBase entity, Hero hero, MovingObjectPosition mop, MoveActivation activation, ImmutableMap<String, Number> modifiers, float focus)
    {
        if (mop != null && mop.entityHit instanceof EntityLivingBase)
        {
            EntityLivingBase target = (EntityLivingBase) mop.entityHit;
            float damage = modifiers.getOrDefault(KEY_DAMAGE, 0).floatValue();
            float knockback = modifiers.getOrDefault(KEY_KNOCKBACK, 0).floatValue();

            if (damage > 0)
            {
                target.attackEntityFrom(new EntityDamageSource(entity instanceof EntityPlayer ? "player" : "mob", entity), damage);
            }

            if (knockback > 0)
            {
                SHHelper.knockbackWithoutNotify(target, entity, knockback);
                target.motionY += knockback / 15;
            }

            return true;
        }

        return false;
    }
}
