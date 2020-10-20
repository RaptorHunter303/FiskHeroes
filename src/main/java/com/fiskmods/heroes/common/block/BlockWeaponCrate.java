package com.fiskmods.heroes.common.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.FiskMath;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockWeaponCrate extends Block
{
    private static final Map<ItemStack, Float> DROPS = new HashMap<>();

    public BlockWeaponCrate()
    {
        super(Material.iron);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(FiskMath.getWeighted(world.rand, DROPS).copy());

        return list;
    }

    static
    {
        DROPS.put(new ItemStack(ModItems.blackCanarysTonfas), 1F);
        DROPS.put(new ItemStack(ModItems.boStaff), 1F);
        DROPS.put(new ItemStack(ModItems.captainAmericasShield), 0.75F);
        DROPS.put(new ItemStack(ModItems.chronosRifle), 0.25F);
        DROPS.put(new ItemStack(ModItems.coldGun), 1F);
        DROPS.put(new ItemStack(ModItems.compoundBow), 1F);
        DROPS.put(new ItemStack(ModItems.deadpoolsSwords), 1F);
        DROPS.put(new ItemStack(ModItems.grapplingGun), 1F);
        DROPS.put(new ItemStack(ModItems.heatGun), 1F);
        DROPS.put(new ItemStack(ModItems.prometheusSword), 1F);
        DROPS.put(new ItemStack(ModItems.quiver), 1F);
        DROPS.put(new ItemStack(ModItems.ripsGun), 0.5F);

        DROPS.put(new ItemStack(ModItems.gunBase), 1F);
        DROPS.put(new ItemStack(ModItems.handle), 2F);
        DROPS.put(new ItemStack(ModItems.katanaBlade), 2F);
        DROPS.put(new ItemStack(ModItems.rifleBase), 0.75F);
    }
}
