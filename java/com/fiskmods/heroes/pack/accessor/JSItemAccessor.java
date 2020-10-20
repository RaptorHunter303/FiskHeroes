package com.fiskmods.heroes.pack.accessor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class JSItemAccessor implements JSAccessor<JSItemAccessor>
{
    public static final JSItemAccessor EMPTY = new Empty();

    private final ItemStack stack;

    private JSItemAccessor(ItemStack itemstack)
    {
        stack = itemstack;
    }

    @Override
    public boolean matches(JSItemAccessor t)
    {
        return ItemStack.areItemStacksEqual(stack, t.stack);
    }

    public static JSItemAccessor wrap(ItemStack stack)
    {
        return stack != null ? new JSItemAccessor(stack) : EMPTY;
    }

    public boolean isEmpty()
    {
        return false;
    }

    public String name()
    {
        return Item.itemRegistry.getNameForObject(stack.getItem());
    }

    public int stackSize()
    {
        return stack.stackSize;
    }

    public int maxStackSize()
    {
        return stack.getMaxStackSize();
    }

    public int damage()
    {
        return stack.getItemDamage();
    }

    public int maxDamage()
    {
        return stack.getMaxDamage();
    }

    public String displayName()
    {
        return stack.getDisplayName();
    }

    public JSNBTAccessor nbt()
    {
        return JSNBTAccessor.wrap(stack.getTagCompound());
    }

    @Override
    public String toString()
    {
        return name() + "@" + damage();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (obj instanceof String && name().equals(obj))
        {
            return true;
        }

        return toString().equals(String.valueOf(obj));
    }

    private static class Empty extends JSItemAccessor
    {
        public Empty()
        {
            super(null);
        }

        @Override
        public boolean matches(JSItemAccessor t)
        {
            return t == EMPTY;
        }

        @Override
        public boolean isEmpty()
        {
            return true;
        }

        @Override
        public String name()
        {
            return "minecraft:air";
        }

        @Override
        public int stackSize()
        {
            return 0;
        }

        @Override
        public int maxStackSize()
        {
            return 0;
        }

        @Override
        public int damage()
        {
            return 0;
        }

        @Override
        public int maxDamage()
        {
            return 0;
        }

        @Override
        public String displayName()
        {
            return "null";
        }

        @Override
        public JSNBTAccessor nbt()
        {
            return JSNBTAccessor.EMPTY;
        }
    }
}
