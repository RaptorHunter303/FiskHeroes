package com.fiskmods.heroes.common.tileentity;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.FabricatorHelper;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityCosmicFabricator extends TileEntitySuitFabricator
{
    @Override
    public String getInventoryName()
    {
        return "gui.cosmic_suit_fabricator";
    }

    @Override
    public void receive(EntityPlayer sender, ByteBuf buf)
    {
        Hero hero = Hero.REGISTRY.getObject(ByteBufUtils.readUTF8String(buf));

        if (hero != null && !hero.isHidden() && hero.isCosmic() && FabricatorHelper.getMaxTier(sender) >= hero.getTier().tier)
        {
            selectedHero = hero;
        }
    }
}
