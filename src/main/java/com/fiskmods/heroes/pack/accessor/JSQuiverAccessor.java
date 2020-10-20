package com.fiskmods.heroes.pack.accessor;

import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.util.QuiverHelper.Quiver;

import net.minecraft.entity.Entity;

public class JSQuiverAccessor
{
    public static final JSQuiverAccessor EMPTY = new JSQuiverAccessor(new Quiver(0), null);

    private final Quiver quiver;
    private final Entity entity;

    private JSQuiverAccessor(Quiver quiver, Entity entity)
    {
        this.quiver = quiver;
        this.entity = entity;
    }

    public static JSQuiverAccessor wrap(Quiver quiver, Entity entity)
    {
        return quiver != null ? new JSQuiverAccessor(quiver, entity) : EMPTY;
    }

    public boolean hasQuiver()
    {
        return entity != null;
    }

    public String getType(int slot)
    {
        return String.valueOf(ArrowType.getNameForArrow(quiver.getType(slot)));
    }

    public int getAmount(int slot)
    {
        return quiver.getAmount(slot);
    }

    public int getFirstSlot()
    {
        return quiver.getFirstSlot();
    }

    public int getActiveSlot()
    {
        return entity != null ? quiver.getActiveSlot(entity) : 0;
    }

    public boolean isEmpty(int slot)
    {
        return quiver.isEmpty(slot);
    }

    public boolean isEmpty()
    {
        return quiver.isEmpty();
    }

    @Override
    public String toString()
    {
        return quiver.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (obj instanceof String && toString().equals(obj))
        {
            return true;
        }

        return toString().equals(String.valueOf(obj));
    }
}
