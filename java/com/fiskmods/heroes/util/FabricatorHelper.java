package com.fiskmods.heroes.util;

import com.fiskmods.heroes.common.data.SHPlayerData;
import com.fiskmods.heroes.common.entity.attribute.ArmorAttribute;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Heroes;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.tileentity.TileEntityCosmicFabricator;
import com.fiskmods.heroes.common.tileentity.TileEntitySuitFabricator;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FabricatorHelper
{
    public static int getMaterialCost(Hero hero)
    {
        if (hero.overrideMaterialCost() >= 0)
        {
            return hero.overrideMaterialCost();
        }
        else if (hero == Heroes.spodermen)
        {
            return 1 << 16;
        }

        double cost = 512;
        double scale = hero.getDefaultScale(null);

        for (Ability ability : hero.getAbilities())
        {
            double d = ability.getTier() * 80;

            if (ability == Ability.SUPER_SPEED)
            {
                d *= SpeedsterHelper.getSuitBaseSpeed(null, hero) / 3.0;
            }
//            else if (ability == Ability.SIZE_MANIPULATION)
//            {
//                hero.getFuncFloat(null, AbilitySizeManipulation.FUNC_MAX_SIZE, 1);
//            }

            cost += d;
        }

        cost += scale * getAttributeCost(hero, SHAttributes.SWORD_DAMAGE, 4, 128);
        cost += scale * getAttributeCost(hero, SHAttributes.PUNCH_DAMAGE, 1, 64);
        cost += scale * getAttributeCost(hero, SHAttributes.ATTACK_DAMAGE, 1, 256);
        cost += scale * getAttributeCost(hero, SHAttributes.FALL_RESISTANCE, 3, 32);
        cost += scale * getAttributeCost(hero, SHAttributes.JUMP_HEIGHT, 1, 64);
        cost += scale * getAttributeCost(hero, SHAttributes.SPRINT_SPEED, FiskMath.KMPH * 20, 64);
        cost += scale * getAttributeCost(hero, SHAttributes.STEP_HEIGHT, 0.5, 128);
        cost += scale * getAttributeCost(hero, SHAttributes.MAX_HEALTH, 20, 256);
        cost += getAttributeCost(hero, SHAttributes.BOW_DRAWBACK, 25, 128);
        cost += getAttributeCost(hero, SHAttributes.ARROW_DAMAGE, 7, 128);
        cost += getAttributeCost(hero, SHAttributes.KNOCKBACK, 1, 256);

        double d = 1.2;
        cost *= Math.pow(d, hero.getTier().tier) - d + 1;

        return (int) Math.round(cost * 4F / hero.getPiecesToSet());
    }

    public static int getTotalEnergy(IInventory inventory)
    {
        boolean flag = inventory instanceof TileEntityCosmicFabricator;
        int i = 0;

        for (int j = 0; j < inventory.getSizeInventory(); ++j)
        {
            i += getEnergy(inventory.getStackInSlot(j), flag);
        }

        if (inventory instanceof TileEntitySuitFabricator)
        {
            i += ((TileEntitySuitFabricator) inventory).energy;
        }

        return i;
    }

    public static int getEnergy(ItemStack itemstack, boolean cosmic)
    {
        if (itemstack != null)
        {
            return itemstack.stackSize * getEnergy(itemstack.getItem(), cosmic);
        }

        return 0;
    }

    public static int getEnergy(Item item, boolean cosmic)
    {
        int i = 1;

        if (item instanceof IMaterialEnergy)
        {
            i = ((IMaterialEnergy) item).getEnergyValue();
        }
        else if (Block.getBlockFromItem(item) instanceof IMaterialEnergy)
        {
            i = ((IMaterialEnergy) Block.getBlockFromItem(item)).getEnergyValue();
        }

        if (cosmic && !SHHelper.isPoisonEternium(item))
        {
            i /= 32;
        }

        return i;
    }

    public static double getAttributeCost(Hero hero, ArmorAttribute attribute, double baseValue, double factor)
    {
        return factor * attribute.get(null, hero, baseValue) / baseValue - factor;
    }

    public static int getMaxTier(EntityPlayer player)
    {
        return SHPlayerData.getData(player).maxTier;
    }

    public interface IMaterialEnergy
    {
        int getEnergyValue();
    }
}
