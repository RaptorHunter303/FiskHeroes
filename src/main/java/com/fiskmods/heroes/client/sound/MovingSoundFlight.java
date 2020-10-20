package com.fiskmods.heroes.client.sound;

import com.fiskmods.heroes.common.data.SHData;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class MovingSoundFlight extends MovingSound
{
    private final EntityLivingBase theEntity;
    private int ticks;

    public MovingSoundFlight(EntityLivingBase entity)
    {
        super(new ResourceLocation(SHSounds.AMBIENT_FLIGHT.toString()));
        theEntity = entity;
        repeat = true;
        volume = 0.1F;
        field_147665_h = 0;
    }

    @Override
    public void update()
    {
        ticks += 1;

        if (!theEntity.isDead && (ticks <= 10 || SHData.GLIDING.get(theEntity)))
        {
            xPosF = (float) theEntity.posX;
            yPosF = (float) theEntity.posY;
            zPosF = (float) theEntity.posZ;
            float f = MathHelper.sqrt_double(theEntity.motionX * theEntity.motionX + theEntity.motionY * theEntity.motionY + theEntity.motionZ * theEntity.motionZ);
            float f1 = f / 2.0F;

            if (f >= 0.01D)
            {
                volume = MathHelper.clamp_float(f1 * f1, 0.0F, 1.0F);
            }
            else
            {
                volume = 0.0F;
            }

            // volume = MathHelper.clamp_float(f1 * f1, 0.0F, 1.0F);

            if (ticks < 10)
            {
                volume = 0.0F;
            }
            else if (ticks < 20)
            {
                volume *= (float) (ticks - 10) / 10;
            }

            float f2 = 0.8F;

            if (volume > f2)
            {
                field_147663_c = 1.0F + (volume - f2);
            }
            else
            {
                field_147663_c = 1.0F;
            }

            volume *= 0.6F;
        }
        else
        {
            donePlaying = true;
        }
    }
}
