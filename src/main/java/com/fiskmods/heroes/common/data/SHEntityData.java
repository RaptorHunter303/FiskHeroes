package com.fiskmods.heroes.common.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.data.arrow.IArrowData;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.util.TemperatureHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;

public class SHEntityData implements IExtendedEntityProperties
{
    public static final String IDENTIFIER = "FiskHeroesEntity";

    public Set<IArrowData> arrowsInEntity = new HashSet<>();
    public List<StatusEffect> activeEffects = new ArrayList<>();

    public float temperature = TemperatureHelper.DEFAULT_BODY_TEMPERATURE;

    private EntityLivingBase living;

    public static SHEntityData getData(EntityLivingBase entity)
    {
        return (SHEntityData) entity.getExtendedProperties(IDENTIFIER);
    }

    public void onUpdate()
    {
        Iterator<IArrowData> iter = arrowsInEntity.iterator();

        for (IArrowData data = null; iter.hasNext(); data = iter.next())
        {
            if (data != null)
            {
                EntityTrickArrow arrow = data.getEntity(living.worldObj);

                if (arrow != null)
                {
                    arrow.posX = arrow.prevPosX = living.posX;
                    arrow.posY = arrow.prevPosY = living.boundingBox.minY + living.height / 2;
                    arrow.posZ = arrow.prevPosZ = living.posZ;
                    arrow.inEntityUpdate(living);

                    if (!arrow.isEntityAlive())
                    {
                        iter.remove();
                    }
                }
            }
        }

        TemperatureHelper.updateTemperature(living);
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt)
    {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setFloat("Temperature", temperature);

        if (!arrowsInEntity.isEmpty())
        {
            NBTTagList list = new NBTTagList();

            for (IArrowData data : arrowsInEntity)
            {
                if (data != null)
                {
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setShort("id", (short) ArrowType.getIdFromArrow(data.getType()));

                    data.writeToNBT(tag);
                    list.appendTag(tag);
                }
            }

            compound.setTag("ArrowsInEntity", list);
        }

        if (!activeEffects.isEmpty())
        {
            NBTTagList list = new NBTTagList();

            for (StatusEffect effect : activeEffects)
            {
                list.appendTag(effect.writeToNBT());
            }

            compound.setTag("Effects", list);
        }

        nbt.setTag(IDENTIFIER, compound);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt)
    {
        NBTTagCompound compound = nbt.getCompoundTag(IDENTIFIER);

        if (compound.hasKey("ArrowsInEntity", NBT.TAG_LIST))
        {
            NBTTagList list = compound.getTagList("ArrowsInEntity", NBT.TAG_COMPOUND);

            for (int i = 0; i < list.tagCount(); ++i)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ArrowType<EntityTrickArrow> type = ArrowType.getArrowById(tag.getShort("id") & 0xFFFF);

                if (type != null)
                {
                    IArrowData data = type.getDataFactory().apply(type, null);

                    if (data != null)
                    {
                        data.readFromNBT(tag);
                    }
                }
            }
        }

        if (compound.hasKey("Effects", NBT.TAG_LIST))
        {
            NBTTagList nbttaglist = compound.getTagList("Effects", NBT.TAG_COMPOUND);
            activeEffects.clear();

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                StatusEffect effect = StatusEffect.readFromNBT(nbttaglist.getCompoundTagAt(i));

                if (effect != null)
                {
                    activeEffects.add(effect);
                }
            }
        }

        if (compound.hasKey("Temperature"))
        {
            temperature = compound.getFloat("Temperature");
        }
    }

    @Override
    public void init(Entity entity, World world)
    {
        if (entity instanceof EntityLivingBase)
        {
            living = (EntityLivingBase) entity;
        }
    }

    public void copy(SHEntityData props)
    {
        arrowsInEntity = props.arrowsInEntity;
        activeEffects = props.activeEffects;
        temperature = props.temperature;
    }
}
