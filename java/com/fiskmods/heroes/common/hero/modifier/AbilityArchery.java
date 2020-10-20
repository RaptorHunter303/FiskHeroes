package com.fiskmods.heroes.common.hero.modifier;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.damagesource.IExtendedDamage.DamageType;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.item.ItemCompoundBow;
import com.fiskmods.heroes.common.item.ModItems;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class AbilityArchery extends Ability
{
    public static final String KEY_HORIZONTAL = "HORIZONTAL_BOW";

    public AbilityArchery(int tier)
    {
        super(tier);
    }

    @Override
    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase, boolean enabled)
    {
        if (phase == Phase.END)
        {
            if (entity.worldObj.isRemote && FiskHeroes.proxy.isClientPlayer(entity) && entity instanceof EntityPlayer)
            {
                SHData.HORIZONTAL_BOW.set(entity, SHData.HORIZONTAL_BOW.get(entity) && ((EntityPlayer) entity).isUsingItem() || hero.isKeyPressed(entity, KEY_HORIZONTAL));
            }
        }
    }

    @Override
    public float damageTaken(EntityLivingBase entity, EntityLivingBase attacker, Hero hero, DamageSource source, float amount, float originalAmount)
    {
        amount = super.damageTaken(entity, attacker, hero, source, amount, originalAmount);

        if (!entity.worldObj.isRemote && DamageType.SHURIKEN.isPresent(source) && SHData.HORIZONTAL_BOW_TIMER.get(entity) > 0)
        {
            ItemStack heldItem = entity.getHeldItem();

            if (heldItem != null && heldItem.getItem() == ModItems.compoundBow)
            {
                ItemCompoundBow.setBroken(heldItem, true);
                entity.worldObj.playSoundAtEntity(entity, "random.break", 0.8F, 0.8F + rand.nextFloat() * 0.4F);
            }
        }

        return amount;
    }
}
