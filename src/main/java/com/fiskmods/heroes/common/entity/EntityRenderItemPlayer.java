package com.fiskmods.heroes.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.MovementInputFromOptions;

@SideOnly(Side.CLIENT)
public class EntityRenderItemPlayer extends EntityClientPlayerMP
{
    public EntityRenderItemPlayer(Minecraft mc)
    {
        super(mc, mc.theWorld, mc.getSession(), mc.getNetHandler(), new StatFileWriter());
        movementInput = new MovementInputFromOptions(mc.gameSettings);
        width = 0.6F;
        height = 1.8F;
        yOffset = 0;
        capabilities.isFlying = true;
        rotationYawHead = 0;
        setInvisible(true);
        setDead();
    }

    @Override
    public void onUpdate()
    {
        ++ticksExisted;
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean isInvisible()
    {
        return true;
    }
}
