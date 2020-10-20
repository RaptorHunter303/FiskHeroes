package com.fiskmods.heroes.client.sound;

import com.fiskmods.heroes.common.hero.Heroes;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MovingSoundSpodermen extends MovingSound
{
    public final EntityLivingBase theEntity;

    public MovingSoundSpodermen(EntityLivingBase entity, float vol, float pitch)
    {
        super(new ResourceLocation(SHSounds.RANDOM_SPIDER.toString()));
        theEntity = entity;
        volume = vol;
        field_147663_c = pitch;
    }

    @Override
    public void update()
    {
        if (theEntity.isDead || SHHelper.getHero(theEntity) != Heroes.spodermen)
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
