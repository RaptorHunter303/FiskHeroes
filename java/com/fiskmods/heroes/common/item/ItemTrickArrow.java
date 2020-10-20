package com.fiskmods.heroes.common.item;

import java.util.List;

import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.book.widget.IItemListEntry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemTrickArrow extends ItemFood implements IItemListEntry
{
    public static final String NO_ENTITY = "NoEntity";

    public ItemTrickArrow()
    {
        super(0, 0, false);
        setHasSubtypes(true);
    }

    public static ArrowType getType(ItemStack itemstack)
    {
        return ArrowType.getArrowById(itemstack.getItemDamage());
    }

    public static ItemStack getItem(ItemStack itemstack)
    {
        if (itemstack.hasTagCompound())
        {
            if (itemstack.getTagCompound().hasKey("FireworksItem"))
            {
                itemstack.getTagCompound().setTag("AttachedItem", itemstack.getTagCompound().getCompoundTag("FireworksItem"));
                itemstack.getTagCompound().removeTag("FireworksItem");
            }

            if (itemstack.getTagCompound().hasKey("AttachedItem"))
            {
                return ItemStack.loadItemStackFromNBT(itemstack.getTagCompound().getCompoundTag("AttachedItem"));
            }
        }

        return null;
    }

    public static void setItem(ItemStack itemstack, ItemStack itemstack1)
    {
        if (itemstack1 == null)
        {
            if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("AttachedItem"))
            {
                itemstack.getTagCompound().removeTag("AttachedItem");
            }

            return;
        }

        if (!itemstack.hasTagCompound())
        {
            itemstack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbttagcompound = new NBTTagCompound();
        itemstack1.writeToNBT(nbttagcompound);
        itemstack.getTagCompound().setTag("AttachedItem", nbttagcompound);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        if (getType(itemstack).isEdible(itemstack, player))
        {
            player.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
        }

        return itemstack;
    }

    @Override
    public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer player)
    {
        ArrowType type = getType(itemstack);

        if (getItemUseAction(itemstack) == EnumAction.eat)
        {
            player.getFoodStats().func_151686_a(this, itemstack);
            world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

            onFoodEaten(itemstack, world, player);
        }

        ItemStack[] result = type.onEaten(itemstack, world, player);

        if (!player.capabilities.isCreativeMode)
        {
            if (result.length > 0)
            {
                for (int i = 1; i < result.length; ++i)
                {
                    player.inventory.addItemStackToInventory(result[i]);
                }

                if (--itemstack.stackSize <= 0)
                {
                    return result[0];
                }

                player.inventory.addItemStackToInventory(result[0]);
            }
            else
            {
                --itemstack.stackSize;
            }
        }

        return itemstack;
    }

    @Override
    public int func_150905_g(ItemStack itemstack)
    {
        return getType(itemstack).getHealAmount(itemstack);
    }

    @Override
    public float func_150906_h(ItemStack itemstack)
    {
        return getType(itemstack).getSaturationModifier(itemstack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return getType(itemstack).getItemUseAction(itemstack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return getType(itemstack).getMaxItemUseDuration(itemstack);
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag)
    {
        getType(itemstack).addInformation(itemstack, player, list, flag);

        if (flag && itemstack.hasTagCompound() && itemstack.getTagCompound().getBoolean(NO_ENTITY))
        {
            list.add(EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tooltip.trickArrow.noEntity"));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return "item.arrow_" + getType(itemstack).getRegistryName().getResourcePath();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (ArrowType type : ArrowType.REGISTRY)
        {
            type.getSubItems(item, tab, list);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack itemstack, int pass)
    {
        return getType(itemstack).hasEffect(itemstack, pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass)
    {
        return ArrowType.getArrowById(damage).getItemIcon(damage, pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        return ArrowType.getArrowById(damage).getItemIcon(damage, 0);
    }

    @Override
    public int getRenderPasses(int metadata)
    {
        return ArrowType.getArrowById(metadata).getRenderPasses(metadata);
    }

    @Override
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemstack, int pass)
    {
        return getType(itemstack).getColor(itemstack, pass);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        for (ArrowType type : ArrowType.REGISTRY)
        {
            type.registerIcons(iconRegister);
        }
    }

    @Override
    public void getListItems(Item item, CreativeTabs tab, List list)
    {
        for (ArrowType type : ArrowType.REGISTRY)
        {
            type.getListItems(item, tab, list);
        }
    }

    @Override
    public String getPageLink(ItemStack itemstack)
    {
        return getType(itemstack).getPageLink(itemstack);
    }
}
