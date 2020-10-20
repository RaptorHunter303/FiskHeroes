package com.fiskmods.heroes.common.tileentity;

import com.fiskmods.heroes.common.block.BlockTreadmill;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.network.MessageTileTrigger.ITileDataCallback;
import com.fiskmods.heroes.util.SHHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockDirectional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class TileEntityTreadmill extends TileEntitySH implements IMultiTile, ITileDataCallback
{
    public int playerId = -1;

    // public float animationTimer;

    @Override
    public void updateEntity()
    {
        EntityPlayer player = getPlayer();
        int metadata = getBlockMetadata();
        int direction = BlockDirectional.getDirection(metadata);

        if (player != null)
        {
            float yaw = direction * 90 % 360;
            double yOffset = player.yOffset + 0.32F;

            player.setPosition(xCoord + 0.5F + BlockTreadmill.directions[direction][0] * 0.3F, yCoord + yOffset, zCoord + 0.5F + BlockTreadmill.directions[direction][1] * 0.3F);
            player.fallDistance = 0;
            player.renderYawOffset = yaw;
            player.rotationYaw = MathHelper.clamp_float(player.rotationYaw, yaw - 30, yaw + 30);
            player.setSprinting(false);

            Hero hero;

            if (player.isSneaking() || (hero = SHHelper.getHero(player)) == null || !hero.hasEnabledModifier(player, Ability.SUPER_SPEED))
            {
                setPlayer(null);
                player.setPosition(xCoord + 0.5F - BlockTreadmill.directions[direction][0], yCoord + yOffset, zCoord + 0.5F - BlockTreadmill.directions[direction][1]);
            }
        }

        // animationTimer = MathHelper.clamp_float(animationTimer, 0, 1);
    }

    public void setPlayer(EntityPlayer player)
    {
        if (player == null)
        {
            playerId = -1;
            markBlockForUpdate();
        }
        else
        {
            if (playerId != player.getEntityId())
            {
                playerId = player.getEntityId();
                markBlockForUpdate();
            }
        }
    }

    public EntityPlayer getPlayer()
    {
        if (playerId >= 0 && worldObj.getEntityByID(playerId) instanceof EntityPlayer)
        {
            return (EntityPlayer) worldObj.getEntityByID(playerId);
        }

        return null;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(1, 0, 1);
    }

    @Override
    protected void writeCustomNBT(NBTTagCompound nbt)
    {
    }

    @Override
    protected void readCustomNBT(NBTTagCompound nbt)
    {
    }

    @Override
    public int[] getBaseOffsets(int metadata)
    {
        if (BlockTreadmill.isBlockFrontOfTreadmill(metadata))
        {
            int[] offsets = BlockTreadmill.directions[BlockDirectional.getDirection(metadata)];

            return new int[] {-offsets[0], 0, -offsets[1]};
        }

        return new int[3];
    }

    @Override
    public void receive(EntityPlayer sender, ByteBuf buf)
    {
        setPlayer(sender);
    }
}
