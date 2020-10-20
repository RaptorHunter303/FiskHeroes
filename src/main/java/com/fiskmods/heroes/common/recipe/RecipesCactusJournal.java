package com.fiskmods.heroes.common.recipe;

import com.fiskmods.heroes.common.item.ItemCactusJournal;
import com.fiskmods.heroes.common.item.ModItems;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesCactusJournal implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        ItemStack journal = null;
        ItemStack entry = null;
        int slots = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (stack.getItem() == ModItems.cactusJournal)
                {
                    journal = stack;
                }
                else if (stack.getItem() == ModItems.journalEntry)
                {
                    entry = stack;
                }

                ++slots;
            }
        }

        return slots == 2 && journal != null && entry != null && !ItemCactusJournal.hasEntry(journal, entry.getItemDamage());
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        ItemStack journal = null;
        ItemStack entry = null;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                if (stack.getItem() == ModItems.cactusJournal)
                {
                    journal = stack;
                }
                else
                {
                    entry = stack;
                }
            }
        }

        if (journal != null && entry != null)
        {
            byte[] entries = ItemCactusJournal.getEntries(journal);
            byte[] data = new byte[entries.length + 1];

            System.arraycopy(entries, 0, data, 0, entries.length);
            data[entries.length] = (byte) entry.getItemDamage();

            return ItemCactusJournal.setEntries(journal.copy(), data);
        }

        return null;
    }

    @Override
    public int getRecipeSize()
    {
        return 10;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return new ItemStack(ModItems.cactusJournal);
    }
}
