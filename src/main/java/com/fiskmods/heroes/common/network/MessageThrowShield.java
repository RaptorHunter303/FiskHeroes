package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.entity.EntityThrownShield;
import com.fiskmods.heroes.common.hero.Hero.Permission;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.SHHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class MessageThrowShield extends AbstractMessage<MessageThrowShield>
{
    public MessageThrowShield()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    @Override
    public void receive() throws MessageException
    {
        EntityPlayer player = getPlayer();
        ItemStack heldItem = player.getHeldItem();

        if (heldItem != null && heldItem.getItem() == ModItems.captainAmericasShield && player.isSneaking() && SHHelper.hasPermission(player, Permission.THROW_SHIELD))
        {
            player.worldObj.playSoundAtEntity(player, SHSounds.ITEM_SHIELD_THROW.toString(), 1, 1 + (player.getRNG().nextFloat() - 0.5F) * 0.2F);
            player.worldObj.spawnEntityInWorld(new EntityThrownShield(player.worldObj, player, heldItem.copy()));
            player.setCurrentItemOrArmor(0, null);
        }
    }
}
