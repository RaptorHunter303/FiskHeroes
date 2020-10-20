package com.fiskmods.heroes.client.render.hero;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.model.ModelMartianManhunter;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.TextureHelper;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HeroRendererMartianManhunter extends HeroRenderer
{
    private ModelBiped playerModel = new ModelBiped();

    public HeroRendererMartianManhunter()
    {
        super(new ModelMartianManhunter());
    }

    @Override
    public boolean shouldRenderDefaultModel(EntityPlayer player, HeroIteration iter, boolean body)
    {
        return false;
    }

    @Override
    public boolean preRenderArm(EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
        String disguise = SHData.DISGUISE.get(player);

        if (SHData.SHAPE_SHIFT_TIMER.get(player) == 0)
        {
            if (disguise != null)
            {
                mc.getTextureManager().bindTexture(TextureHelper.getSkin(disguise));
                ModelBipedMultiLayer.sync(model.bipedRightArm, playerModel.bipedRightArm);
                playerModel.bipedRightArm.render(0.0625F);

                return false;
            }
        }
        else
        {
            float t = SHData.SHAPE_SHIFT_TIMER.get(player);
            t = 1 - (t > 0.5F ? 1 - t : t) * 2;

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
            GL11.glColor4f(1, 1, 1, t);

            if (disguise != null)
            {
                mc.getTextureManager().bindTexture(TextureHelper.getSkin(disguise));
                ModelBipedMultiLayer.sync(model.bipedRightArm, playerModel.bipedRightArm);
                playerModel.bipedRightArm.render(0.0625F);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                GL11.glPopMatrix();

                return false;
            }

            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glPopMatrix();
        }

        return true;
    }
}
