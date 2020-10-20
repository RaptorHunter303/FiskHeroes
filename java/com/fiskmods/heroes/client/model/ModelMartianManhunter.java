package com.fiskmods.heroes.client.model;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.TextureHelper;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;

public class ModelMartianManhunter extends ModelBipedMultiLayer
{
    protected static final ModelBiped MODEL_PLAYER = new ModelBiped();

    @Override
    public void renderBody(Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float f5)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            float opacity = SHHelper.getInvisibility(player, mc.thePlayer);
            float t = SHData.SHAPE_SHIFT_TIMER.get(player);

            if (pass != Pass.ENCHANTMENT && pass != Pass.HURT && (SHData.INVISIBLE.get(player) ? opacity < 1 : opacity < 0.4F))
            {
                GL11.glDepthMask(false);
            }

            if (t > 0)
            {
                opacity *= 1 - (float) Math.sin(t * Math.PI);
            }

            SHRenderHelper.setAlpha(SHRenderHelper.getAlpha() * opacity);

            if (SHData.DISGUISE.get(player) != null)
            {
                String from = SHData.SHAPE_SHIFTING_FROM.get(player);
                String to = SHData.SHAPE_SHIFTING_TO.get(player);

                renderNormal(player, pass, f, f1, f2, f3, f4, f5);

                if (!StringUtils.isNullOrEmpty(from) && !StringUtils.isNullOrEmpty(to) || !StringUtils.isNullOrEmpty(from) && StringUtils.isNullOrEmpty(to) && t > 0.5F || t < 0.5F)
                {
                    SHRenderHelper.setAlpha(0);
                }

                return;
            }
        }

        super.renderBody(entity, pass, f, f1, f2, f3, f4, f5);
    }

    private void renderNormal(EntityPlayer player, int pass, float f, float f1, float f2, float f3, float f4, float f5)
    {
        if (pass == Pass.ENCHANTMENT)
        {
            return;
        }

        mc.getTextureManager().bindTexture(TextureHelper.getSkin(SHData.DISGUISE.get(player)));

        MODEL_PLAYER.aimedBow = aimedBow;
        MODEL_PLAYER.heldItemLeft = heldItemLeft;
        MODEL_PLAYER.heldItemRight = heldItemRight;
        MODEL_PLAYER.isChild = isChild;
        MODEL_PLAYER.isRiding = isRiding;
        MODEL_PLAYER.isSneak = isSneak;
        MODEL_PLAYER.onGround = onGround;
        sync(bipedHead, MODEL_PLAYER.bipedHead);
        sync(bipedHeadwear[0], MODEL_PLAYER.bipedHeadwear);
        sync(bipedBody, MODEL_PLAYER.bipedBody);
        sync(bipedRightArm, MODEL_PLAYER.bipedRightArm);
        sync(bipedLeftArm, MODEL_PLAYER.bipedLeftArm);
        sync(bipedRightLeg, MODEL_PLAYER.bipedRightLeg);
        sync(bipedLeftLeg, MODEL_PLAYER.bipedLeftLeg);
        sync(bipedEars, MODEL_PLAYER.bipedEars);
        sync(bipedCloak, MODEL_PLAYER.bipedCloak);
        MODEL_PLAYER.bipedBody.showModel = armorSlot == 1;
        MODEL_PLAYER.render(player, f, f1, f2, f3, f4, f5);
    }
}
