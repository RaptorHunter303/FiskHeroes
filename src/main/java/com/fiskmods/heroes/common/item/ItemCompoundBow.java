package com.fiskmods.heroes.common.item;

import java.util.List;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.enchantment.SHEnchantments;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.util.QuiverHelper;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.IOffhandWield;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

@Interface(modid = "battlegear2", iface = "mods.battlegear2.api.IOffhandWield")
public class ItemCompoundBow extends ItemBow implements IPunchWeapon, IBattlegearSheathed, IOffhandWield
{
    @SideOnly(Side.CLIENT)
    private IIcon iconBroken;

    public ItemCompoundBow()
    {
        setFull3D();
        setMaxStackSize(1);
        setMaxDamage(SHConstants.MAX_DMG_COMPOUND_BOW);
    }

    @Override
    public boolean renderSheathed(boolean back)
    {
        return false;
    }

    @Override
    public boolean isOffhandWieldable(ItemStack itemstack, EntityPlayer player)
    {
        return false;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag)
    {
        if (isBroken(itemstack))
        {
            list.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tooltip.bow.broken"));
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer player, int charge)
    {
        ItemStack arrow = QuiverHelper.getArrowToFire(player);

        if (arrow != null)
        {
            int duration = getMaxItemUseDuration(itemstack) - charge;
            float f = duration / SHAttributes.BOW_DRAWBACK.get(player, 30);
            f = Math.min((f * f + f * 2) / 3, 1);

            if (f >= 0.1)
            {
                world.playSoundAtEntity(player, "random.bow", 1, 1F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                if (f > 0.5F)
                {
                    world.playSoundAtEntity(player, SHSounds.ITEM_BOW_SHOOT.toString(), f, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                }

                EntityTrickArrow entity;

                if ((entity = ItemTrickArrow.getType(arrow).shoot(world, player, null, itemstack, arrow, f)) != null)
                {
                    itemstack.damageItem(1, player);

                    if (!player.capabilities.isCreativeMode)
                    {
                        ItemStack quiverStack = QuiverHelper.getEquippedQuiver(player);

                        if (quiverStack != null)
                        {
                            int i = EnchantmentHelper.getEnchantmentLevel(SHEnchantments.infinity.effectId, quiverStack);

                            if (i > 0 && itemRand.nextFloat() < 0.1F * i)
                            {
                                entity.canBePickedUp = 2;
                                return;
                            }
                        }

                        QuiverHelper.getQuiverInventory(player).consumeArrowItemStack();
                    }
                }
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        ItemStack arrow = QuiverHelper.getArrowToFire(player);

        if (arrow != null && !isBroken(itemstack))
        {
            player.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
        }

        return itemstack;
    }

    @Override
    public int getItemEnchantability()
    {
        return 10;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemstack1, ItemStack itemstack2)
    {
        return itemstack2.getItem() == Items.iron_ingot || super.getIsRepairable(itemstack1, itemstack2);
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
        if (block.getBlockHardness(world, x, y, z) != 0)
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
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", SHConstants.DMG_COMPOUND_BOW, 0));
        return multimap;
    }

    @Override
    public IIcon getIconIndex(ItemStack stack)
    {
        return isBroken(stack) ? iconBroken : itemIcon;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon(getIconString());
        iconBroken = iconRegister.registerIcon(getIconString() + "_broken");
    }

    public static ItemStack setBroken(ItemStack itemstack, boolean broken)
    {
        if (!itemstack.hasTagCompound())
        {
            itemstack.setTagCompound(new NBTTagCompound());
        }

        itemstack.getTagCompound().setBoolean("Broken", broken);

        return itemstack;
    }

    public static boolean isBroken(ItemStack itemstack)
    {
        return itemstack.hasTagCompound() && itemstack.getTagCompound().getBoolean("Broken");
    }
}
