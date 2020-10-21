package com.fiskmods.heroes.common.data.arrow;

import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public interface IArrowData
{
    void fromBytes(ByteBuf buf);

    void toBytes(ByteBuf buf);

    void readFromNBT(CompoundNBT nbt);

    void writeToNBT(CompoundNBT nbt);

    EntityTrickArrow getEntity(World world);

    ArrowType getType();
}
