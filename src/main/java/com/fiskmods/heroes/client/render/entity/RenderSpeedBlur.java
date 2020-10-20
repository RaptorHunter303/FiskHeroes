package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.client.json.trail.JsonTrailBlur;
import com.fiskmods.heroes.common.entity.EntitySpeedBlur;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderSpeedBlur extends Render
{
    private ModelBiped modelBiped = new ModelBiped(0);

    public void doRender(EntitySpeedBlur entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        EntityPlayer player = entity.anchorEntity;

        if (player != null && entity.trail != null && entity.trail.blur != null)
        {
            Minecraft mc = Minecraft.getMinecraft();

            if (player == mc.thePlayer && mc.gameSettings.thirdPersonView == 0)
            {
                return;
            }

            JsonTrailBlur blur = entity.trail.blur;
            float scale = player.height / 1.8F;
//            float offset = (scale - 1) * 1.62F - player.height;
//            Vec3 pos = player.getPosition(partialTicks);
//            Vec3 playerPos = pos.addVector(0, offset + (1 - scale) * 1.62F * 2, 0);

            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_CULL_FACE);
            modelBiped.bipedHeadwear.showModel = false;
            modelBiped.onGround = entity.getSwingProgress(1);
            modelBiped.isRiding = entity.isRiding();
            modelBiped.isChild = entity.isChild();

            try
            {
                float bodyYaw = interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, 1);
                float headYaw = interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, 1);
                float ticks = entity.ticksExisted + 1;
                float pitch = entity.rotationPitch;
                float f = 0.0625F;

                float progress = 1 - (entity.progress + partialTicks) / entity.trail.fade;
                float limbSwingAmount = entity.limbSwingAmount;
                float limbSwing = entity.limbSwing;

                if (entity.isChild())
                {
                    limbSwing *= 3;
                }

                if (limbSwingAmount > 1)
                {
                    limbSwingAmount = 1;
                }

                scale *= 0.9375F;
//                int j = 1;
//
//                if (entity.progress == 1)
//                {
//                    j = 2;
//                }

//                GL11.glDepthMask(false);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
                SHRenderHelper.setGlColor(blur.getVecColor(), progress * blur.getOpacity());
                modelBiped.setLivingAnimations(entity, limbSwing, limbSwingAmount, 1);

//                for (int i = 0; i < j; ++i)
                {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float) x, (float) y, (float) z);
                    GL11.glRotatef(180 - bodyYaw, 0, 1, 0);
                    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                    GL11.glScalef(-scale, -scale, scale);
                    GL11.glTranslatef(0, -24 * f - 0.0078125F, 0);
                    modelBiped.render(entity, limbSwing, limbSwingAmount, ticks, headYaw - bodyYaw, pitch, f);
                    GL11.glPopMatrix();

//                    if (i == 0 && j > 1)
//                    {
//                        Vec3 entityPos = entity.getPosition(partialTicks);
//
////                        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
////                        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
////                        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
////                        x += d0 - entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
////                        y += d1 - entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
////                        z += d2 - entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
//
//                        x += SHRenderHelper.interpolate(entityPos.xCoord, playerPos.xCoord) - entityPos.xCoord;
//                        y += SHRenderHelper.interpolate(entityPos.yCoord, playerPos.yCoord) - entityPos.yCoord;
//                        z += SHRenderHelper.interpolate(entityPos.zCoord, playerPos.zCoord) - entityPos.zCoord;
//                    }
                }

                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                GL11.glDisable(GL11.GL_BLEND);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_CULL_FACE);
//            GL11.glDepthMask(false);
            GL11.glPopMatrix();
        }
    }

    private float interpolateRotation(float p_77034_1_, float p_77034_2_, float partialTicks)
    {
        float delta;

        for (delta = p_77034_2_ - p_77034_1_; delta < -180; delta += 360)
        {
            ;
        }

        while (delta >= 180)
        {
            delta -= 360;
        }

        return p_77034_1_ + partialTicks * delta;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        doRender((EntitySpeedBlur) entity, x, y, z, entityYaw, partialTicks);
    }
}
