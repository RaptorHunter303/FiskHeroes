package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.interaction.InteractionType;
import com.fiskmods.heroes.common.item.ItemFlashRing;
import com.fiskmods.heroes.common.item.ModItems;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class KeyPressMiniaturizeSuit extends KeyPressBase
{
    public static final String KEY_MINIATURIZE_SUIT = "MINIATURIZE_SUIT";

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return player.getHeldItem() == null && super.serverRequirements(player, type, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public KeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return hero.getKey(player, KEY_MINIATURIZE_SUIT);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            ItemStack itemstack = new ItemStack(ModItems.miniATOMSuit);
            itemstack.setTagCompound(new NBTTagCompound());
            ItemFlashRing.setContainedArmor(itemstack, sender.getEquipmentInSlot(4), sender.getEquipmentInSlot(3), sender.getEquipmentInSlot(2), sender.getEquipmentInSlot(1));

            for (int i = 0; i < sender.inventory.armorInventory.length; ++i)
            {
                sender.setCurrentItemOrArmor(i + 1, null);
            }

            sender.setCurrentItemOrArmor(0, itemstack);
        }
    }

    @Override
    public boolean syncWithServer()
    {
        return true;
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
