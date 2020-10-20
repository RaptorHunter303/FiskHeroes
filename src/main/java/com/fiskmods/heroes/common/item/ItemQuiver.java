package com.fiskmods.heroes.common.item;

import java.util.List;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.container.ContainerQuiver;
import com.fiskmods.heroes.common.container.InventoryQuiver;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemQuiver extends Item implements IEquipmentItem
{
    public static final String[] NAMES = {"", "auto_"};

    @SideOnly(Side.CLIENT)
    public IIcon[] icons;

    public ItemQuiver()
    {
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public boolean isItemTool(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getItemEnchantability()
    {
        return 10;
    }

    @Override
    public boolean canEquip(ItemStack itemstack, TileEntityDisplayStand tile)
    {
        return true;
    }

    @Override
    public void onUpdate(ItemStack itemstack, World world, Entity entity, int slot, boolean currentlyHeld)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;

            if (!itemstack.hasTagCompound())
            {
                itemstack.setTagCompound(new NBTTagCompound());
            }

            if (itemstack.getItemDamage() == 1 && player.ticksExisted % 8 == 0 && !player.worldObj.isRemote && !(player.openContainer instanceof ContainerQuiver) && !currentlyHeld)
            {
                InventoryQuiver quiver = new InventoryQuiver(player, slot);

                for (int i = 0; i < player.inventory.mainInventory.length; ++i)
                {
                    ItemStack inventoryStack = player.inventory.mainInventory[i];

                    if (inventoryStack != null && quiver.isItemValidForSlot(0, inventoryStack))
                    {
                        ItemStack itemstack1 = inventoryStack.copy();
                        itemstack1.stackSize = 1;

                        if (quiver.addItemStackToInventory(itemstack1))
                        {
                            if (--inventoryStack.stackSize <= 0)
                            {
                                player.inventory.mainInventory[i] = null;
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        if (!itemstack.hasTagCompound())
        {
            itemstack.setTagCompound(new NBTTagCompound());
        }

        player.openGui(FiskHeroes.MODID, 0, world, player.inventory.currentItem, 0, 0);
        return itemstack;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        int i = MathHelper.clamp_int(itemstack.getItemDamage(), 0, NAMES.length);
        return "item." + NAMES[i] + "quiver";
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (int i = 0; i < NAMES.length; ++i)
        {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage)
    {
        int i = MathHelper.clamp_int(damage, 0, NAMES.length);
        return icons[i];
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        icons = new IIcon[NAMES.length];

        for (int i = 0; i < icons.length; ++i)
        {
            icons[i] = iconRegister.registerIcon(FiskHeroes.MODID + ":" + NAMES[i] + "quiver");
        }
    }
}
