package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.MovementInputFromOptions;

@SideOnly(Side.CLIENT)
public class EntityBookPlayer extends EntityClientPlayerMP
{
    public EntityBookPlayer(Minecraft mc)
    {
        super(mc, mc.theWorld, mc.getSession(), mc.getNetHandler(), new StatFileWriter());
        movementInput = new MovementInputFromOptions(mc.gameSettings);
        width = 0.6F;
        height = 1.8F;
        yOffset = 0;
        capabilities.isFlying = true;
        rotationYawHead = 0;
        setInvisible(false);

        if (riddenByEntity == null)
        {
            riddenByEntity = new EntityHorse(worldObj);
        }
    }

    @Override
    public void onUpdate()
    {
        ++ticksExisted;
        SHData.onUpdate(this);

        Hero hero = SHHelper.getHero(this);

        if (hero != null)
        {
            byte maskOpenTimer = SHData.MASK_OPEN_TIMER.get(this);
            boolean isMaskOpen = SHData.MASK_OPEN.get(this);

            if (maskOpenTimer < 5 && isMaskOpen)
            {
                SHData.MASK_OPEN_TIMER.incrWithoutNotify(this, (byte) 1);
            }
            else if (maskOpenTimer > 0 && !isMaskOpen)
            {
                SHData.MASK_OPEN_TIMER.incrWithoutNotify(this, (byte) -1);
            }

            SHHelper.incr(SHData.MASK_OPEN_TIMER2, this, 5, isMaskOpen);
            SHHelper.incr(SHData.STEEL_TIMER, this, SHConstants.TICKS_STEEL_CONVERT, SHData.STEELED.get(this));
            SHHelper.incr(SHData.TRANSFORM_TIMER, this, SHConstants.TICKS_TRANSFORMATION, SHData.TRANSFORMED.get(this));
            SHHelper.incr(SHData.SHIELD_TIMER, this, SHConstants.TICKS_SHIELD_UNFOLD, SHData.SHIELD.get(this));
            SHHelper.incr(SHData.BLADE_TIMER, this, SHConstants.TICKS_BLADE_UNFOLD, SHData.BLADE.get(this));
        }
    }

    @Override
    public void playSound(String sound, float volume, float pitch)
    {
        mc.thePlayer.playSound(sound, volume, pitch);
    }
}
