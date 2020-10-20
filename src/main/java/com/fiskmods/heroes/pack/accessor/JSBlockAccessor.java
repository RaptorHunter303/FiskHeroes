package com.fiskmods.heroes.pack.accessor;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class JSBlockAccessor implements JSAccessor<JSBlockAccessor>
{
    public static final JSBlockAccessor EMPTY = new Empty();

    private final World world;
    private final Block block;
    private final int x, y, z;

    private int metadata = -1;

    private JSBlockAccessor(World world, Block block, int x, int y, int z)
    {
        this.world = world;
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean matches(JSBlockAccessor t)
    {
        return block == t.block && metadata() == t.metadata();
    }

    public static JSBlockAccessor wrap(World world, int x, int y, int z)
    {
        if (world == null)
        {
            return EMPTY;
        }

        Block block = world.getBlock(x, y, z);
        return block != Blocks.air ? new JSBlockAccessor(world, block, x, y, z) : EMPTY;
    }

    public boolean isEmpty()
    {
        return false;
    }

    public String name()
    {
        return Block.blockRegistry.getNameForObject(block);
    }

    public int metadata()
    {
        if (metadata == -1)
        {
            metadata = world.getBlockMetadata(x, y, z);
        }

        return metadata;
    }

    public float hardness()
    {
        return block.getBlockHardness(world, x, y, z);
    }

    public JSItemAccessor item()
    {
        return JSItemAccessor.wrap(new ItemStack(block));
    }

    public JSItemAccessor itemDropped(int fortune)
    {
        int meta = metadata();
        return JSItemAccessor.wrap(new ItemStack(block.getItemDropped(meta, world.rand, fortune), block.quantityDropped(meta, fortune, world.rand), block.damageDropped(meta)));
    }

    @Override
    public String toString()
    {
        return name() + "@" + metadata();
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

    private static class Empty extends JSBlockAccessor
    {
        public Empty()
        {
            super(null, null, 0, 0, 0);
        }

        @Override
        public boolean matches(JSBlockAccessor t)
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
        public int metadata()
        {
            return 0;
        }

        @Override
        public float hardness()
        {
            return 0;
        }

        @Override
        public JSItemAccessor item()
        {
            return JSItemAccessor.EMPTY;
        }

        @Override
        public JSItemAccessor itemDropped(int fortune)
        {
            return JSItemAccessor.EMPTY;
        }
    }
}
