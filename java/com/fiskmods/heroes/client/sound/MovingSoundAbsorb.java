package com.fiskmods.heroes.client.sound;

import com.fiskmods.heroes.common.block.BlockSubatomicCore.CoreType;
import com.fiskmods.heroes.common.tileentity.TileEntityParticleCore;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MovingSoundAbsorb extends MovingSound
{
    public final TileEntityParticleCore tileentity;

    public MovingSoundAbsorb(TileEntityParticleCore tile)
    {
        super(new ResourceLocation(SHSounds.ITEM_BATTERY_ABSORB.toString()));
        tileentity = tile;
        repeat = true;
    }

    @Override
    public void update()
    {
        float draining = SHRenderHelper.interpolate(tileentity.draining, tileentity.prevDraining);

        if (tileentity.isInvalid() || draining <= 0 || tileentity.getType() == CoreType.BLACK)
        {
            donePlaying = true;
        }
        else
        {
            xPosF = tileentity.xCoord;
            yPosF = tileentity.yCoord;
            zPosF = tileentity.zCoord;
            volume = 8 * draining;
        }
    }
}
