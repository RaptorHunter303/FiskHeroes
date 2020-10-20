package com.fiskmods.heroes.common.item;

import com.fiskmods.heroes.FiskHeroes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemExclusivityToken extends Item
{
    private IIcon[] icons;

    public ItemExclusivityToken()
    {
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        return damage > 0 ? icons[Math.min(damage, icons.length) - 1] : itemIcon;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        icons = new IIcon[IconType.values().length];

        for (IconType type : IconType.values())
        {
            icons[type.ordinal()] = iconRegister.registerIcon(FiskHeroes.MODID + ":" + type.iconString);
        }
    }

    public enum IconType
    {
        TIER1("heroes/heatwave_1"),
        TIER2("heroes/geomancer_1"),
        TIER3("heroes/captain_america_1"),
        TIER4("heroes/colossus_xmen_1"),
        TIER5("heroes/martian_manhunter_comics_1"),
        TIER6("heroes/the_monitor_1"),
        FACEPLANT("heroes/falcon_1"),
        LAND_DEADPOOL("heroes/deadpool_xmen_3"),
        KMPH_1000("heroes/the_flash_0"),
        SPODERMEN("heroes/spodermen_0");

        private final String iconString;

        private IconType(String icon)
        {
            iconString = icon;
        }

        public ItemStack create()
        {
            return new ItemStack(ModItems.exclusivityToken, 1, ordinal() + 1);
        }
    }
}
