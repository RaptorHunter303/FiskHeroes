package com.fiskmods.heroes.common.arrowtype;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.item.ItemTrickArrow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ArrowTypeVial<T extends EntityTrickArrow> extends ArrowType<T>
{
    @SideOnly(Side.CLIENT)
    public IIcon potionOverlay;

    public ArrowTypeVial(Class<T> clazz)
    {
        super(clazz);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        List<ItemStack> subItems = new ArrayList<>();
        Items.potionitem.getSubItems(Items.potionitem, Items.potionitem.getCreativeTab(), subItems);

        for (ItemStack potion : subItems)
        {
            if (!ItemPotion.isSplash(potion.getItemDamage()))
            {
                ItemStack itemstack = makeItem();
                potion.setItemDamage(potion.getItemDamage() & ~8192 | 16384);

                ItemTrickArrow.setItem(itemstack, potion);
                list.add(itemstack);
            }
        }
    }

    @Override
    public void getListItems(Item item, CreativeTabs tab, List list)
    {
        ItemStack itemstack = new ItemStack(item, 1, getIdFromArrow(this));
        ItemTrickArrow.setItem(itemstack, new ItemStack(Items.potionitem, 1, 16384));
        list.add(itemstack);
    }

    @Override
    public ItemStack[] onEaten(ItemStack itemstack, World world, EntityPlayer player)
    {
        ItemStack itemstack1 = ItemTrickArrow.getItem(itemstack);

        if (itemstack1 != null && !world.isRemote)
        {
            List list = Items.potionitem.getEffects(itemstack1);

            if (list != null)
            {
                Iterator iterator = list.iterator();

                while (iterator.hasNext())
                {
                    PotionEffect potioneffect = (PotionEffect) iterator.next();
                    player.addPotionEffect(new PotionEffect(potioneffect));
                }
            }
        }

        return new ItemStack[] {ArrowTypeManager.NORMAL.makeItem(), new ItemStack(Items.glass_bottle)};
    }

    @Override
    public boolean isEdible(ItemStack itemstack, EntityPlayer player)
    {
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.drink;
    }

    @Override
    public boolean hasEffect(ItemStack itemstack, int pass)
    {
        if (pass == 0)
        {
            ItemStack potion = ItemTrickArrow.getItem(itemstack);

            if (potion != null)
            {
                List list = Items.potionitem.getEffects(potion);
                return list != null && !list.isEmpty();
            }
        }

        return false;
    }

    @Override
    public IIcon getItemIcon(int damage, int pass)
    {
        if (pass == 0)
        {
            return potionOverlay;
        }

        return super.getItemIcon(damage, pass);
    }

    @Override
    public int getRenderPasses(int metadata)
    {
        return 2;
    }

    @Override
    public int getColor(ItemStack itemstack, int pass)
    {
        if (pass == 0)
        {
            ItemStack potion = ItemTrickArrow.getItem(itemstack);

            if (potion != null)
            {
                return Items.potionitem.getColorFromDamage(potion.getItemDamage());
            }
        }

        return super.getColor(itemstack, pass);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        potionOverlay = iconRegister.registerIcon(getTextureName() + "_overlay");
    }
}
