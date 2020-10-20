package com.fiskmods.heroes.pack.accessor;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class JSWorldAccessor
{
    public static final JSWorldAccessor NULL = new Null();

    private final World world;

    private JSWorldAccessor(World worldObj)
    {
        world = worldObj;
    }

    public static JSWorldAccessor wrap(World world)
    {
        return world != null ? new JSWorldAccessor(world) : NULL;
    }

    public int getDimension()
    {
        return world.provider.dimensionId;
    }

    public String name()
    {
        return world.getWorldInfo().getWorldName();
    }

    public boolean isDaytime()
    {
        return world.isDaytime();
    }

    public boolean isRaining()
    {
        return world.isRaining();
    }

    public boolean isThundering()
    {
        return world.isThundering();
    }

    public JSBlockAccessor blockAt(int x, int y, int z)
    {
        return JSBlockAccessor.wrap(world, x, y, z);
    }

    public JSBlockAccessor blockAt(JSVectorAccessor vector)
    {
        int[] i = vector.toIntArray();
        return blockAt(i[0], i[1], i[2]);
    }

    public String getBlock(int x, int y, int z)
    {
        return Block.blockRegistry.getNameForObject(world.getBlock(x, y, z));
    }

    public String getBlock(JSVectorAccessor vector)
    {
        int[] i = vector.toIntArray();
        return getBlock(i[0], i[1], i[2]);
    }

    public int getBlockMetadata(int x, int y, int z)
    {
        return world.getBlockMetadata(x, y, z);
    }

    public int getBlockMetadata(JSVectorAccessor vector)
    {
        int[] i = vector.toIntArray();
        return getBlockMetadata(i[0], i[1], i[2]);
    }

    private static class Null extends JSWorldAccessor
    {
        public Null()
        {
            super(null);
        }

        @Override
        public int getDimension()
        {
            return 0;
        }

        @Override
        public String name()
        {
            return "null";
        }

        @Override
        public boolean isDaytime()
        {
            return false;
        }

        @Override
        public boolean isRaining()
        {
            return false;
        }

        @Override
        public boolean isThundering()
        {
            return false;
        }

        @Override
        public JSBlockAccessor blockAt(int x, int y, int z)
        {
            return JSBlockAccessor.EMPTY;
        }

        @Override
        public String getBlock(int x, int y, int z)
        {
            return "null";
        }

        @Override
        public int getBlockMetadata(int x, int y, int z)
        {
            return 0;
        }
    }
}
