package com.fiskmods.heroes.common.item;

import java.util.List;

import com.fiskmods.heroes.gameboii.GameboiiCartridge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class ItemGameboiiCartridge extends Item
{
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemGameboiiCartridge()
    {
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
    {
        GameboiiCartridge cartridge = GameboiiCartridge.get(itemstack);
        list.add(StatCollector.translateToLocalFormatted("gameboii.cartridge." + cartridge.id));
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (GameboiiCartridge cartridge : GameboiiCartridge.values())
        {
            list.add(cartridge.create());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int metadata)
    {
        return icons[GameboiiCartridge.get(metadata).ordinal()];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        icons = new IIcon[GameboiiCartridge.values().length];

        for (GameboiiCartridge cartridge : GameboiiCartridge.values())
        {
            icons[cartridge.ordinal()] = iconRegister.registerIcon(getIconString() + "_" + cartridge.id);
        }
    }
}
