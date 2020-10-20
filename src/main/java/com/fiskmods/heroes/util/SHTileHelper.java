package com.fiskmods.heroes.util;

import com.fiskmods.heroes.common.tileentity.IMultiTile;

import net.minecraft.tileentity.TileEntity;

public class SHTileHelper
{
    public static int[] getTileBaseOffsets(TileEntity tile, int metadata)
    {
        if (tile instanceof IMultiTile)
        {
            return ((IMultiTile) tile).getBaseOffsets(metadata);
        }

        return new int[] {0, 0, 0};
    }

    public static <T extends TileEntity> T getTileBase(T tile)
    {
        int[] offsets = getTileBaseOffsets(tile, tile != null ? tile.getBlockMetadata() : 0);

        if (offsets[0] != 0 || offsets[1] != 0 || offsets[2] != 0)
        {
            return (T) tile.getWorldObj().getTileEntity(tile.xCoord + offsets[0], tile.yCoord + offsets[1], tile.zCoord + offsets[2]);
        }

        return tile;
    }
}
