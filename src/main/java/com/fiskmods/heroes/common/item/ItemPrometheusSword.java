package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.hero.Heroes;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ItemPrometheusSword extends ItemSword implements IBattlegearSheathed, IEquipmentItem
{
    public ItemPrometheusSword()
    {
        super(ToolMaterial.IRON);
        setMaxDamage(SHConstants.MAX_DMG_PROMETHEUS_SWORD);
    }

    @Override
    public boolean renderSheathed(boolean back)
    {
        return false;
    }

    @Override
    public boolean canEquip(ItemStack itemstack, TileEntityDisplayStand tile)
    {
        return SHHelper.getHeroFromArmor(tile.fakePlayer, 2) == Heroes.prometheus;
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack itemstack)
    {
        Multimap multimap = super.getAttributeModifiers(itemstack);
        multimap.removeAll(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", SHConstants.DMG_PROMETHEUS_SWORD, 0));
        return multimap;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
    }
}
