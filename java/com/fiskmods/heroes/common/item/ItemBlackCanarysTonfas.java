package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.common.data.SHData;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import mods.battlegear2.api.IAllowItem;
import mods.battlegear2.api.IOffhandWield;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@InterfaceList(value = {@Interface(modid = "battlegear2", iface = "mods.battlegear2.api.IOffhandWield"), @Interface(modid = "battlegear2", iface = "mods.battlegear2.api.IAllowItem")})
public class ItemBlackCanarysTonfas extends ItemUntextured implements IDualItem, IPunchWeapon, IOffhandWield, IAllowItem
{
    public ItemBlackCanarysTonfas()
    {
        setMaxStackSize(1);
        setMaxDamage(SHConstants.MAX_DMG_BLACK_CANARY_TONFAS);
    }

    @Override
    public float getSwingProgress(EntityPlayer player, float partialTicks)
    {
        return SHRenderHooks.getSwingProgress(player, partialTicks);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack itemstack)
    {
        SHData.RIGHT_TONFA_STATE.setWithoutNotify(entity, !SHData.RIGHT_TONFA_STATE.get(entity));
        return false;
    }

    @Override
    public boolean onEntitySwingOffHand(EntityPlayer player, ItemStack itemstack)
    {
        SHData.LEFT_TONFA_STATE.setWithoutNotify(player, !SHData.LEFT_TONFA_STATE.get(player));
        return false;
    }

    @Override
    public boolean onSwingEnd(EntityPlayer player, ItemStack itemstack, boolean right)
    {
        return false;
    }

    @Override
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public int getItemEnchantability()
    {
        return 10;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemstack1, ItemStack itemstack2)
    {
        return itemstack2.getItem() == ModItems.handle || super.getIsRepairable(itemstack1, itemstack2);
    }

    @Override
    public boolean hitEntity(ItemStack itemstack, EntityLivingBase entity1, EntityLivingBase entity2)
    {
        itemstack.damageItem(1, entity2);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase entity)
    {
        if (block.getBlockHardness(world, x, y, z) != 0.0D)
        {
            itemstack.damageItem(2, entity);
        }

        return true;
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack itemstack)
    {
        Multimap multimap = super.getAttributeModifiers(itemstack);
        multimap.removeAll(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", SHConstants.DMG_BLACK_CANARY_TONFAS, 0));
        return multimap;
    }

    @Override
    public boolean isOffhandWieldable(ItemStack itemstack, EntityPlayer player)
    {
        return false;
    }

    @Override
    public boolean allowOffhand(ItemStack main, ItemStack off)
    {
        return false;
    }
}
