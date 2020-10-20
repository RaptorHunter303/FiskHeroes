package com.fiskmods.heroes.client.sound;

import com.fiskmods.heroes.client.gui.GuiGameboii;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.batfish.BatfishSounds;
import com.fiskmods.heroes.gameboii.batfish.ScreenCredits;
import com.fiskmods.heroes.gameboii.batfish.ScreenGameOver;
import com.fiskmods.heroes.gameboii.engine.GameboiiSound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MovingSoundGameboii extends MovingSound
{
    public final EntityLivingBase entity;
    public final GameboiiSound sound;

    private final float defVolume;

    public MovingSoundGameboii(EntityLivingBase entity, GameboiiSound sound, float volume, float pitch, boolean loop)
    {
        super(new ResourceLocation(sound.sound));
        this.entity = entity;
        this.sound = sound;
        field_147663_c = pitch;
        defVolume = this.volume = volume;

        if (loop)
        {
            repeat = true;
            field_147665_h = 0;
            field_147666_i = ISound.AttenuationType.NONE;
        }
    }

    @Override
    public void update()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (sound == BatfishSounds.TITLE && (Gameboii.getScreen() instanceof ScreenGameOver || Gameboii.getScreen() instanceof ScreenCredits))
        {
            donePlaying = true;
            return;
        }

        if (entity.isDead || entity.getHeldItem() == null || entity.getHeldItem().getItem() != ModItems.gameboii || entity == mc.thePlayer && !(mc.currentScreen instanceof GuiGameboii))
        {
            donePlaying = true;
        }
        else
        {
            xPosF = (float) entity.posX;
            yPosF = (float) entity.posY;
            zPosF = (float) entity.posZ;
            volume = defVolume * sound.category.getVolume();
        }
    }
}
