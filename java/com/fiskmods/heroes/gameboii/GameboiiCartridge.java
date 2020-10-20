package com.fiskmods.heroes.gameboii;

import java.util.function.Supplier;

import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.gameboii.batfish.Batfish;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public enum GameboiiCartridge
{
    BATFISH("batfish", () -> Batfish.INSTANCE);

    public final String id;
    final Supplier<?> supplier;

    GameboiiCartridge(String id, Supplier<?> supplier)
    {
        this.id = id;
        this.supplier = supplier;
    }

    public ItemStack create()
    {
        return new ItemStack(ModItems.gameboiiCartridge, 1, ordinal());
    }

    public static GameboiiCartridge get(int damage)
    {
        return values()[MathHelper.clamp_int(damage, 0, values().length - 1)];
    }

    public static GameboiiCartridge get(ItemStack stack)
    {
        return stack != null ? get(stack.getItemDamage()) : null;
    }
}
