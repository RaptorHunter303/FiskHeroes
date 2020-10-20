package com.fiskmods.heroes.common.data.arrow;

import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.entity.arrow.EntityGrappleArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class GrappleArrowData extends DefaultArrowData
{
    private boolean isCableCut;

    public GrappleArrowData(ArrowType type, EntityTrickArrow arrow)
    {
        super(type, arrow);

        if (arrow instanceof EntityGrappleArrow)
        {
            setIsCableCut(((EntityGrappleArrow) arrow).getIsCableCut());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        isCableCut = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(isCableCut);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        isCableCut = nbt.getBoolean("IsCableCut");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setBoolean("IsCableCut", isCableCut);
    }

    @Override
    public EntityTrickArrow getEntity(World world)
    {
        EntityTrickArrow arrow = super.getEntity(world);

        if (arrow instanceof EntityGrappleArrow)
        {
            ((EntityGrappleArrow) arrow).setIsCableCut(isCableCut);
        }

        return arrow;
    }

    public boolean getIsCableCut()
    {
        return isCableCut;
    }

    public void setIsCableCut(boolean flag)
    {
        isCableCut = flag;
    }
}
