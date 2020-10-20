package com.fiskmods.heroes.common.registry;

import java.util.function.Function;

import com.fiskmods.heroes.FiskHeroes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;

public class BlockRegistry
{
    public static RegistryBuilder builder(String name)
    {
        return new RegistryBuilder(name);
    }

    public static class RegistryBuilder
    {
        private final String name;
        private String oreName;
        private String tileName;

        private Function<Block, ItemBlock> item;
        private Class<? extends ItemBlock> itemClass;
        private Class<? extends TileEntity> tileClass;

        public RegistryBuilder(String name)
        {
            this.name = name;
        }

        public RegistryBuilder ore(String oreName)
        {
            this.oreName = oreName;
            return this;
        }

        public RegistryBuilder item(Function<Block, ItemBlock> item)
        {
            this.item = item;
            return this;
        }

        public RegistryBuilder item(Class<? extends ItemBlock> itemClass)
        {
            this.itemClass = itemClass;
            return this;
        }

        public RegistryBuilder tile(Class<? extends TileEntity> tileClass)
        {
            this.tileClass = tileClass;
            return this;
        }

        public RegistryBuilder tile(Class<? extends TileEntity> tileClass, String tileName)
        {
            this.tileClass = tileClass;
            this.tileName = tileName;
            return this;
        }

        public <T extends Block> T register(T block)
        {
            block.setBlockName(name);
            block.setBlockTextureName(FiskHeroes.MODID + ":" + name);
            block.setCreativeTab(FiskHeroes.TAB_ITEMS);

            if (itemClass != null)
            {
                GameRegistry.registerBlock(block, itemClass, name);
            }
            else if (item != null)
            {
                GameRegistry.registerBlock(block, null, name);
                Item.itemRegistry.addObject(Block.getIdFromBlock(block), name, item.apply(block).setUnlocalizedName(name));
            }
            else
            {
                GameRegistry.registerBlock(block, name);
            }

            if (tileClass != null)
            {
                GameRegistry.registerTileEntity(tileClass, tileName != null ? tileName : name);
            }

            if (oreName != null)
            {
                OreDictionary.registerOre(oreName, block);
            }

            return block;
        }
    }
}
