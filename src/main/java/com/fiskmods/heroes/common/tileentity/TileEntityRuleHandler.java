package com.fiskmods.heroes.common.tileentity;

import com.fiskmods.heroes.common.config.ImmutableRuleSet;
import com.fiskmods.heroes.common.config.RuleHandler;
import com.fiskmods.heroes.common.data.world.SHMapData;
import com.fiskmods.heroes.common.network.MessageTileTrigger.ITileDataCallback;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityRuleHandler extends TileEntitySH implements ITileDataCallback
{
    public ImmutableRuleSet ruleSet;
    private String ruleKey;

    public int chunkRadius = 1;
    public boolean showBounds;

    @Override
    public void updateEntity()
    {
        if (!worldObj.isRemote && ruleSet != null)
        {
            ruleSet.tick();
        }
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        SHMapData.get(worldObj).addRuleHandler(this);
    }

    @Override
    public void validate()
    {
        super.validate();
        SHMapData.get(worldObj).removeRuleHandler(this);
    }

    public void notifyRuleSetChange(String key)
    {
        if (key.equals(ruleKey))
        {
            ruleKey = null;
            set(key);
        }
    }

    public void set(String key)
    {
        if (key == null)
        {
            ruleSet = null;
            ruleKey = null;
            markDirty();
        }
        else if (ruleKey == null || !key.equals(ruleKey))
        {
            ruleSet = RuleHandler.INSTANCE.copy(ruleKey);
            ruleKey = key;
            markDirty();
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        chunkRadius = nbt.getByte("ChunkRadius") & 0xFF;

        if (nbt.hasKey("RuleSet", NBT.TAG_STRING))
        {
            set(nbt.getString("RuleSet"));
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setByte("ChunkRadius", (byte) chunkRadius);

        if (!StringUtils.isNullOrEmpty(ruleKey))
        {
            nbt.setString("RuleSet", ruleKey);
        }
    }

    @Override
    public void receive(EntityPlayer sender, ByteBuf buf)
    {
        chunkRadius = buf.readByte();
    }
}
