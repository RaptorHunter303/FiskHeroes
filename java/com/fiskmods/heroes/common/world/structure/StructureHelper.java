package com.fiskmods.heroes.common.world.structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockStairs;

public class StructureHelper
{
    public static int mirrorX(Block block, int metadata)
    {
        if (block instanceof BlockStairs)
        {
            switch (metadata & 7)
            {
            case 0:
                return 1;
            case 1:
                return 0;
            case 4:
                return 5;
            case 5:
                return 4;
            }
        }
        else if (block instanceof BlockButton)
        {
            switch (metadata)
            {
            case 1:
                return 2;
            case 2:
                return 1;
            }
        }

        return metadata;
    }

    public static int mirrorZ(Block block, int metadata)
    {
        if (block instanceof BlockStairs)
        {
            switch (metadata & 7)
            {
            case 2:
                return 3;
            case 3:
                return 2;
            case 6:
                return 7;
            case 7:
                return 6;
            }
        }
        else if (block instanceof BlockButton)
        {
            switch (metadata)
            {
            case 1:
                return 2;
            case 2:
                return 1;
            }
        }

        return metadata;
    }

    public static int mirrorXZ(Block block, int metadata)
    {
        if (block instanceof BlockStairs)
        {
            switch (metadata & 7)
            {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return 3;
            case 3:
                return 2;
            case 4:
                return 5;
            case 5:
                return 4;
            case 6:
                return 7;
            case 7:
                return 6;
            }
        }
        else if (block instanceof BlockButton)
        {
            switch (metadata)
            {
            case 1:
                return 2;
            case 2:
                return 1;
            }
        }

        return metadata;
    }

    public static int rotate(Block block, int metadata)
    {
        if (block instanceof BlockStairs)
        {
            switch (metadata & 7)
            {
            case 0:
                return 3;
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 0;
            case 4:
                return 7;
            case 5:
                return 6;
            case 6:
                return 5;
            case 7:
                return 4;
            }
        }
        else if (block instanceof BlockButton)
        {
            return metadata + 1 & 3;
        }

        return metadata;
    }
}
