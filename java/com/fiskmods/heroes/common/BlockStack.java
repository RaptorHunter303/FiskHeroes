package com.fiskmods.heroes.common;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockStack
{
    public Block block;
    public int metadata;

    public BlockStack(Block block, int metadata)
    {
        this.block = block;
        this.metadata = metadata;
    }

    public BlockStack(Block block)
    {
        this(block, 0);
    }

    public static BlockStack fromItemStack(ItemStack itemstack)
    {
        if (itemstack != null && itemstack.getItem() instanceof ItemBlock)
        {
            return new BlockStack(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());
        }

        return null;
    }

    public static ItemStack toItemStackSafe(BlockStack stack)
    {
        if (stack != null && stack.block != null && stack.block != Blocks.air)
        {
            return new ItemStack(stack.block, 1, stack.metadata);
        }

        return null;
    }

    public static String toStringSafe(BlockStack stack)
    {
        if (stack != null)
        {
            return stack.toString();
        }

        return "empty";
    }

    public static BlockStack fromString(String id)
    {
        if (id != null)
        {
            int metadata = 0;

            if (id.contains("@"))
            {
                metadata = Integer.valueOf(id.substring(id.lastIndexOf("@") + 1));
                id = id.substring(0, id.lastIndexOf("@"));
            }

            Block block = (Block) Block.blockRegistry.getObject(id);

            if (block != null)
            {
                return new BlockStack(block, metadata);
            }
        }

        return null;
    }

    public BlockStack copy()
    {
        return new BlockStack(block, metadata);
    }

    @Override
    public int hashCode()
    {
        return block.hashCode() | metadata << 16;
    }

    @Override
    public String toString()
    {
        return Block.blockRegistry.getNameForObject(block) + "@" + metadata;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof BlockStack)
        {
            BlockStack stack = (BlockStack) obj;

            return stack.block == block && stack.metadata == metadata;
        }

        return false;
    }
}
