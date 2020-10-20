package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.keybinds.SHKeyBinding;
import com.fiskmods.heroes.client.keybinds.SHKeyBinds;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Heroes;
import com.fiskmods.heroes.common.interaction.InteractionType;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressSpodermen extends KeyPressBase
{
    public KeyPressSpodermen()
    {
        setPredicate((e, h) -> h == Heroes.spodermen && SHData.SPODERMEN.get(e));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SHKeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return SHKeyBinds.ABILITY_1;
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isClient())
        {
            FiskHeroes.proxy.playSound(sender, "spodermen", 1.0F, 1.0F + (sender.getRNG().nextFloat() - sender.getRNG().nextFloat()) * 0.2F);
        }
    }

    @Override
    public boolean syncWithServer()
    {
        return true;
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_ALL;
    }
}
