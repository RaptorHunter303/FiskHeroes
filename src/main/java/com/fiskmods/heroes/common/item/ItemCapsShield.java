package com.fiskmods.heroes.common.item;

import java.util.List;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.hero.Hero.Permission;
import com.fiskmods.heroes.common.network.MessageThrowShield;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.IOffhandWield;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

@Interface(modid = "battlegear2", iface = "mods.battlegear2.api.IOffhandWield")
public class ItemCapsShield extends ItemUntextured implements IPunchWeapon, IBattlegearSheathed, IOffhandWield, IEquipmentItem
{
    public static final String TAG_STEALTH = "Stealth";

    public ItemCapsShield()
    {
        setMaxStackSize(1);
        setMaxDamage(SHConstants.MAX_DMG_CPT_AMERICA_SHIELD);
        setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
    {
        if (isStealth(itemstack))
        {
            list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocalFormatted(getUnlocalizedName() + ".stealth"));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item));
        list.add(setStealth(new ItemStack(item), true));
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
    public boolean canEquip(ItemStack itemstack, TileEntityDisplayStand tile)
    {
        return true;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.block;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        if (SHHelper.hasPermission(player, Permission.USE_SHIELD))
        {
            player.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
        }

        return itemstack;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack itemstack)
    {
        if (entity.worldObj.isRemote && FiskHeroes.proxy.isClientPlayer(entity))
        {
            if (entity.isSneaking() && SHHelper.hasPermission(entity, Permission.THROW_SHIELD) && Cooldown.SHIELD_THROW.available(entity))
            {
                SHNetworkManager.wrapper.sendToServer(new MessageThrowShield());
                Cooldown.SHIELD_THROW.set(entity);
            }
        }

        return false;
    }

    @Override
    public boolean isDamageable()
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
        return itemstack2.getItem() == ModItems.vibraniumPlate || super.getIsRepairable(itemstack1, itemstack2);
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
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", SHConstants.DMG_CPT_AMERICA_SHIELD, 0));
        return multimap;
    }

    public static boolean isStealth(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean(TAG_STEALTH);
    }

    public static ItemStack setStealth(ItemStack stack, boolean stealth)
    {
        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setBoolean(TAG_STEALTH, stealth);
        return stack;
    }
}
