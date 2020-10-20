package com.fiskmods.heroes.client.sound;

import java.util.Random;

import com.fiskmods.heroes.common.data.SHData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MovingSoundIceCharge extends MovingSound
{
    public final EntityLivingBase theEntity;
    private final Random rand = new Random();

    public MovingSoundIceCharge(EntityLivingBase entity)
    {
        super(new ResourceLocation(SHSounds.ABILITY_CRYOKINESIS_CHARGE.toString()));
        theEntity = entity;
        repeat = true;
        field_147665_h = 0;
        volume = 1.0F;
    }

    @Override
    public void update()
    {
        field_147663_c = 0.5F + rand.nextFloat();

        if (theEntity.isDead || !SHData.CRYO_CHARGING.get(theEntity))
        {
            donePlaying = true;
        }
        else
        {
            xPosF = (float) theEntity.posX;
            yPosF = (float) theEntity.posY;
            zPosF = (float) theEntity.posZ;
        }
    }
}
