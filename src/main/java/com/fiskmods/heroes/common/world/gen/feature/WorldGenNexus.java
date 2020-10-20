//package com.fiskmods.heroes.common.world.gen.feature;
//
//import java.util.Random;
//
//import com.fiskmods.heroes.common.block.ModBlocks;
//import com.fiskmods.heroes.common.world.structure.StructureHelper;
//
//import net.minecraft.block.Block;
//import net.minecraft.init.Blocks;
//import net.minecraft.world.World;
//import net.minecraft.world.gen.feature.WorldGenerator;;
//
//public class WorldGenNexus extends WorldGenerator
//{
//    @Override
//    public boolean generate(World world, Random rand, int x, int y, int z)
//    {
//        int height = 7 + rand.nextInt(2);
//        int xR = 7 + rand.nextInt(3);
//        int zR = 7 + rand.nextInt(3);
//        int x1;
//        int y1;
//        int z1;
//
//        int wall = 5;
//        int minX = x - xR - 1;
//        int maxX = x + xR + 1;
//        int minZ = z - zR - 1;
//        int maxZ = z + zR + 1;
//
////        for (x1 = minX; x1 <= maxX; ++x1)
////        {
////            for (y1 = y - 1; y1 <= y + b0 + 1; ++y1)
////            {
////                for (z1 = minZ; z1 <= maxZ; ++z1)
////                {
////                    Material material = world.getBlock(x1, y1, z1).getMaterial();
////
////                    if (y1 == y - 1 && !material.isSolid())
////                    {
////                        return false;
////                    }
////
////                    if (y1 == y + b0 + 1 && !material.isSolid())
////                    {
////                        return false;
////                    }
////
////                    if ((x1 == minX || x1 == maxX || z1 == minZ || z1 == maxZ) && y1 == y && !world.isAirBlock(x1, y1, z1) && !world.isAirBlock(x1, y1 + 1, z1))
////                    {
////                        ++j1;
////                    }
////                }
////            }
////        }
////
////        if (j1 >= 1 && j1 <= 5)
////        if (rand.nextInt(10) == 0)
//        if (world != null)
//        {
//            for (x1 = minX - wall; x1 <= maxX + wall; ++x1)
//            {
//                for (y1 = y - 1 - wall; y1 <= y + height + wall; ++y1)
//                {
//                    for (z1 = minZ - wall; z1 <= maxZ + wall; ++z1)
//                    {
//                        boolean flag = x1 < minX || y1 < y - 1 || z1 < minZ || x1 > maxX || y1 > y + height || z1 > maxZ;
//
//                        if (x1 == minX || y1 == y - 1 || z1 == minZ || x1 == maxX || y1 == y + height || z1 == maxZ)
//                        {
//                            if (!flag)
//                            {
//                                world.setBlock(x1, y1, z1, ModBlocks.eterniumStone, 0, 2);
//                            }
//                        }
//                        else if (flag)
//                        {
//                            int i = Math.max(minX - x1, 0);
//                            i = Math.max(y - 1 - y1, i);
//                            i = Math.max(minZ - z1, i);
//                            i = Math.max(x1 - maxX, i);
//                            i = Math.max(y1 - (y + height), i);
//                            i = Math.max(z1 - maxZ, i);
//
//                            if (i <= 0 + rand.nextInt(wall))
//                            {
//                                world.setBlock(x1, y1, z1, ModBlocks.eterniumStone, 0, 2);
//                            }
//                        }
//                        else
//                        {
//                            world.setBlockToAir(x1, y1, z1);
//                        }
//                    }
//                }
//            }
//
//            for (int i = 0; i <= height - 7; ++i)
//            {
//                world.setBlock(x, y + 3 + i, z, ModBlocks.superchargedEternium);
//            }
//
//            setCenter(x, y, z);
//
//            for (x1 = minX + 1; x1 < maxX; ++x1)
//            {
//                for (z1 = minZ + 1; z1 < maxZ; ++z1)
//                {
//                    set(world, rand, x1 - x, 0, z1 - z, ModBlocks.nexusSoil, 0);
//                    set(world, rand, x1 - x, height - 1, z1 - z, ModBlocks.nexusBrickSlab, 8);
//
//                    if (x1 == xCoord || z1 == zCoord)
//                    {
//                        set(world, rand, x1 - x, 1, z1 - z, ModBlocks.nexusBricks, 0);
//                        set(world, rand, x1 - x, height - 1, z1 - z, ModBlocks.nexusBricks, 0);
//
//                        if (x1 == xCoord)
//                        {
//                            mirrorX = true;
//                            set(world, rand, x1 - x + 1, 1, z1 - z, ModBlocks.nexusBrickStairs, 1);
//                            mirrorX = false;
//                        }
//                        else
//                        {
//                            mirrorZ = true;
//                            set(world, rand, x1 - x, 1, z1 - z + 1, ModBlocks.nexusBrickStairs, 3);
//                            mirrorZ = false;
//                        }
//                    }
//                }
//            }
//
//            for (int i = 0; i < 5; ++i)
//            {
//                for (int j = 0; j < 5; ++j)
//                {
//                    set(world, rand, i - 2, 1, j - 2, ModBlocks.nexusBricks, 0);
//                    set(world, rand, i - 2, height - 1, j - 2, ModBlocks.nexusBricks, 0);
//                }
//            }
//
//            // Start middle platform
//            set(world, rand, 0, 2, 0, ModBlocks.nexusBricks, 0);
//            mirrorX = mirrorZ = true;
//            set(world, rand, 1, 2, 0, ModBlocks.nexusBricks, 0);
//            set(world, rand, 1, 2, 1, ModBlocks.nexusBrickSlab, 0);
//            set(world, rand, 2, 2, 0, ModBlocks.nexusBrickStairs, 1);
//
//            // Start inner ring
//            set(world, rand, 4, 1, 4, ModBlocks.nexusBrickSlab, 0);
//            set(world, rand, 4, 1, 3, ModBlocks.nexusBrickStairs, 0);
//            set(world, rand, 3, 1, 4, ModBlocks.nexusBrickStairs, 2);
//            set(world, rand, 6, 1, 3, ModBlocks.nexusBrickStairs, 1);
//            set(world, rand, 3, 1, 6, ModBlocks.nexusBrickStairs, 3);
//
//            for (int i = 1; i < height; ++i)
//            {
//                set(world, rand, 5, i, 3, ModBlocks.nexusBricks, 0);
//                set(world, rand, 3, i, 5, ModBlocks.nexusBricks, 0);
//            }
//
//            mirrorZ = false;
//
//            // Start lava corners
//            for (int i = 1; i < height; ++i)
//            {
//                set(world, rand, xR, height - i, zR, Blocks.lava, 0);
//                set(world, rand, xR, height - i, -zR, Blocks.lava, 0);
//            }
//
//            for (int i = 0; i <= 4; i += 4)
//            {
//                y1 = i > 0 ? height - 1 : 1;
//                set(world, rand, xR - 1, y1, zR, ModBlocks.nexusBrickStairs, i);
//                set(world, rand, xR, y1, zR - 1, ModBlocks.nexusBrickStairs, i + 2);
//
//                set(world, rand, xR - 1, y1, -zR, ModBlocks.nexusBrickStairs, i);
//                set(world, rand, xR, y1, -zR + 1, ModBlocks.nexusBrickStairs, i + 3);
//            }
//
//            mirrorX = mirrorZ = false;
//
//            return true;
//        }
//        else
//        {
//            return false;
//        }
//    }
//
//    private int xCoord;
//    private int yCoord;
//    private int zCoord;
//
//    private boolean mirrorX;
//    private boolean mirrorZ;
//
//    private void setCenter(int x, int y, int z)
//    {
//        xCoord = x;
//        yCoord = y;
//        zCoord = z;
//    }
//
//    private void set(World world, Random rand, int x, int y, int z, Block block, int metadata)
//    {
//        if (mirrorX && mirrorZ)
//        {
//            if (x != 0 || z != 0)
//            {
//                setBlock(world, rand, xCoord + z, yCoord + y, zCoord - x, block, StructureHelper.mirrorX(block, StructureHelper.rotate(block, metadata)));
//                setBlock(world, rand, xCoord - z, yCoord + y, zCoord + x, block, StructureHelper.mirrorZ(block, StructureHelper.rotate(block, metadata)));
//                setBlock(world, rand, xCoord - x, yCoord + y, zCoord - z, block, StructureHelper.mirrorXZ(block, metadata));
//            }
//        }
//        else
//        {
//            if (mirrorX && x != 0)
//            {
//                setBlock(world, rand, xCoord - x, yCoord + y, zCoord + z, block, StructureHelper.mirrorX(block, metadata));
//            }
//
//            if (mirrorZ && z != 0)
//            {
//                setBlock(world, rand, xCoord + x, yCoord + y, zCoord - z, block, StructureHelper.mirrorZ(block, metadata));
//            }
//        }
//
//        setBlock(world, rand, xCoord + x, yCoord + y, zCoord + z, block, metadata);
//    }
//
//    private void setBlock(World world, Random rand, int x, int y, int z, Block block, int metadata)
//    {
//        if (world.getBlock(x, y, z).getBlockHardness(world, x, y, z) < 0)
//        {
//            return;
//        }
//
//        float f = 0.15F;
//
//        if (block == ModBlocks.nexusBricks && metadata == 0 && rand.nextFloat() < f)
//        {
//            if (rand.nextFloat() < 0.25F)
//            {
//                block = ModBlocks.nexusBrickSlab;
//                metadata = rand.nextBoolean() ? 0 : 8;
//            }
//            else
//            {
//                block = ModBlocks.nexusBrickStairs;
//                metadata = rand.nextInt(8);
//            }
//        }
//        else if (block == ModBlocks.nexusBrickStairs && rand.nextFloat() < f)
//        {
//            block = ModBlocks.nexusBrickSlab;
//            metadata = (metadata & 4) == 4 ? 8 : 0;
//        }
//
//        world.setBlock(x, y, z, block, metadata, 2);
//    }
//}
