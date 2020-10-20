package com.fiskmods.heroes.common.item;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fiskmods.heroes.common.BlockStack;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemDisplayCase extends ItemUntextured
{
    public ItemDisplayCase()
    {
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag)
    {
        DisplayCase casing = getCasing(itemstack);

        format(list, casing.top, "top");
        format(list, casing.bottom, "bottom");
        format(list, casing.corners, "corners");
        format(list, casing.front, "front");
        format(list, casing.walls, "walls");
    }

    private String format(List list, BlockStack stack, String s)
    {
        if (stack != null && stack.block.getMaterial() != Material.air)
        {
            list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocalFormatted("tooltip.displayCase." + s, EnumChatFormatting.GRAY + new ItemStack(stack.block, 1, stack.metadata).getDisplayName() + EnumChatFormatting.DARK_GRAY));
        }

        return null;
    }

    public static DisplayCase getCasing(ItemStack itemstack)
    {
        DisplayCase casing = new DisplayCase();

        if (itemstack != null)
        {
            if (isModified(itemstack))
            {
                NBTTagCompound nbttagcompound = itemstack.getTagCompound().getCompoundTag("Case");
                casing.setTop(BlockStack.fromString(nbttagcompound.getString("Top")));
                casing.setBottom(BlockStack.fromString(nbttagcompound.getString("Bottom")));
                casing.setCorners(BlockStack.fromString(nbttagcompound.getString("Corners")));
                casing.setFront(BlockStack.fromString(nbttagcompound.getString("Front")));
                casing.setWalls(BlockStack.fromString(nbttagcompound.getString("Walls")));
            }
            else
            {
                casing.setTop(new BlockStack(Blocks.stone_slab));
                casing.setBottom(new BlockStack(Blocks.stone_slab));
                casing.setCorners(new BlockStack(Blocks.stone_slab));
                casing.setFront(new BlockStack(Blocks.stained_glass));
                casing.setWalls(new BlockStack(Blocks.stained_glass));
            }
        }

        return casing;
    }

    public static void setCasing(ItemStack itemstack, DisplayCase casing)
    {
        if (!itemstack.hasTagCompound())
        {
            itemstack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Top", BlockStack.toStringSafe(casing.top));
        compound.setString("Bottom", BlockStack.toStringSafe(casing.bottom));
        compound.setString("Corners", BlockStack.toStringSafe(casing.corners));
        compound.setString("Front", BlockStack.toStringSafe(casing.front));
        compound.setString("Walls", BlockStack.toStringSafe(casing.walls));

        itemstack.getTagCompound().setTag("Case", compound);
    }

    public static boolean isModified(ItemStack itemstack)
    {
        return itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Case", NBT.TAG_COMPOUND);
    }

    public static class DisplayCase
    {
        public BlockStack top;
        public BlockStack bottom;
        public BlockStack corners;
        public BlockStack front;
        public BlockStack walls;

        public DisplayCase()
        {
            setBottom(new BlockStack(Blocks.stone_slab));
        }

        public DisplayCase copy()
        {
            return new DisplayCase().setTop(top).setBottom(bottom).setCorners(corners).setFront(front).setWalls(walls);
        }

        public DisplayCase setTop(BlockStack stack)
        {
            top = stack;
            return this;
        }

        public DisplayCase setBottom(BlockStack stack)
        {
            bottom = stack;
            return this;
        }

        public DisplayCase setCorners(BlockStack stack)
        {
            corners = stack;
            return this;
        }

        public DisplayCase setFront(BlockStack stack)
        {
            front = stack;
            return this;
        }

        public DisplayCase setWalls(BlockStack stack)
        {
            walls = stack;
            return this;
        }

        public boolean hasBaseplate()
        {
            return bottom != null && bottom.block != Blocks.air;
        }

        public Map<List<AxisAlignedBB>, BlockStack> getBounds(int metadata)
        {
            Map<List<AxisAlignedBB>, BlockStack> map = new LinkedHashMap<>();
            List<AxisAlignedBB> topList = new LinkedList<>();
            List<AxisAlignedBB> bottomList = new LinkedList<>();
            List<AxisAlignedBB> cornersList = new LinkedList<>();
            List<AxisAlignedBB> frontList = new LinkedList<>();
            List<AxisAlignedBB> wallsList = new LinkedList<>();

            int rotation = metadata % 8;
            float thickness = 1F / 64;
            float f = 1F / 16;

            for (int i = 0; i < 2; ++i)
            {
                float minY = metadata < 8 ? i : i - 1;
                float maxY = metadata < 8 ? i + 1 : i;

                if (!hasBaseplate())
                {
                    if (i == 1)
                    {
                        maxY -= f;
                    }
                    else
                    {
                        minY -= f;
                    }
                }

                if (i == 1)
                {
                    topList.add(AxisAlignedBB.getBoundingBox(0, maxY - f, 0, 1, maxY, 1));
                }
                else
                {
                    bottomList.add(AxisAlignedBB.getBoundingBox(0, minY, 0, 1, minY + f, 1));
                }

                minY += i == 0 ? f : 0;
                maxY -= i == 1 ? f : 0;

                int index = rotation / 2;
                List[] alist = rotation % 2 == 0 ? new List[] {frontList, wallsList, wallsList, wallsList} : new List[] {frontList, wallsList, wallsList, frontList};
                alist[(index + 0) % 4].add(AxisAlignedBB.getBoundingBox(f, minY, 1 - thickness, 1 - f, maxY, 1));
                alist[(index + 1) % 4].add(AxisAlignedBB.getBoundingBox(1 - thickness, minY, f, 1, maxY, 1 - f));
                alist[(index + 2) % 4].add(AxisAlignedBB.getBoundingBox(f, minY, 0, 1 - f, maxY, thickness));
                alist[(index + 3) % 4].add(AxisAlignedBB.getBoundingBox(0, minY, f, thickness, maxY, 1 - f));

                alist = rotation % 2 == 1 ? new List[] {frontList, cornersList, cornersList, cornersList} : new List[] {cornersList, cornersList, cornersList, cornersList};
                alist[(index + 0) % 4].add(AxisAlignedBB.getBoundingBox(0, minY, 1 - f, f, maxY, 1));
                alist[(index + 1) % 4].add(AxisAlignedBB.getBoundingBox(1 - f, minY, 1 - f, 1, maxY, 1));
                alist[(index + 2) % 4].add(AxisAlignedBB.getBoundingBox(1 - f, minY, 0, 1, maxY, f));
                alist[(index + 3) % 4].add(AxisAlignedBB.getBoundingBox(0, minY, 0, f, maxY, f));
            }

            map.put(topList, top);
            map.put(bottomList, bottom);
            map.put(cornersList, corners);
            map.put(frontList, front);
            map.put(wallsList, walls);

            return map;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof DisplayCase)
            {
                DisplayCase casing = (DisplayCase) obj;

                return matching(casing.top, top) && matching(casing.bottom, bottom) && matching(casing.corners, corners) && matching(casing.front, front) && matching(casing.walls, walls);
            }

            return false;
        }

        private boolean matching(BlockStack stack1, BlockStack stack2)
        {
            if (stack1 == null && stack2 == null)
            {
                return true;
            }
            else if (stack1 != null)
            {
                return stack1.equals(stack2);
            }

            return false;
        }
    }
}
