package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.arrow.EntityGrappleArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ArrowRendererGrapplingHook extends ArrowRenderer
{
    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);

        if (!inQuiver)
        {
            GL11.glPushMatrix();
            float f = 0.2F;
            GL11.glScalef(f, f, f);
            GL11.glRotatef(180, 0, 0, 1);
            GL11.glTranslatef(0, 2.1F, 0);
            itemRenderer.renderItem(mc.thePlayer, new ItemStack(Blocks.sticky_piston), 0);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void preRender(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        renderCable(arrow, x, y, z, partialTicks, 0x7F664C, 0x5A4736);
    }

    public void renderCable(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, int color1, int color2)
    {
        GL11.glPushMatrix();
        Tessellator tessellator = Tessellator.instance;
        RenderManager renderManager = RenderManager.instance;

        if (arrow.getShooter() instanceof EntityPlayer)
        {
            EntityPlayer shooter = (EntityPlayer) arrow.getShooter();

            if (arrow instanceof EntityGrappleArrow && !((EntityGrappleArrow) arrow).getIsCableCut())
            {
                EntityGrappleArrow grappleArrow = (EntityGrappleArrow) arrow;

                float f9 = shooter.getSwingProgress(partialTicks);
                float f10 = MathHelper.sin(MathHelper.sqrt_float(f9) * (float) Math.PI);
                Vec3 vec3 = Vec3.createVectorHelper(-0.35D, -0.1D, 0.35D);
                vec3.rotateAroundX(-SHRenderHelper.interpolate(shooter.rotationPitch, shooter.prevRotationPitch) * (float) Math.PI / 180.0F);
                vec3.rotateAroundY(-SHRenderHelper.interpolate(shooter.rotationYaw, shooter.prevRotationYaw) * (float) Math.PI / 180.0F);
                vec3.rotateAroundY(f10 * 0.5F);

                double entityPosX = SHRenderHelper.interpolate(arrow.posX, arrow.prevPosX);
                double entityPosY = SHRenderHelper.interpolate(arrow.posY, arrow.prevPosY);
                double entityPosZ = SHRenderHelper.interpolate(arrow.posZ, arrow.prevPosZ);
                double playerPosX = SHRenderHelper.interpolate(shooter.posX, shooter.prevPosX) + vec3.xCoord;
                double playerPosY = SHRenderHelper.interpolate(shooter.posY, shooter.prevPosY) + vec3.yCoord;
                double playerPosZ = SHRenderHelper.interpolate(shooter.posZ, shooter.prevPosZ) + vec3.zCoord;

                if (renderManager.options.thirdPersonView > 0 || shooter != Minecraft.getMinecraft().thePlayer)
                {
                    float renderYawOffset = SHRenderHelper.interpolate(shooter.renderYawOffset, shooter.prevRenderYawOffset) * (float) Math.PI / 180.0F;
                    double side = 0.3D;
                    double forward = 0.3D;
                    double yOffset = (shooter == Minecraft.getMinecraft().thePlayer ? 0.0D : (double) shooter.getEyeHeight()) - 0.3D;
                    double d = MathHelper.sin(renderYawOffset);
                    double d1 = MathHelper.cos(renderYawOffset);

                    playerPosX = SHRenderHelper.interpolate(shooter.posX, shooter.prevPosX) - d1 * side - d * forward;
                    playerPosY = SHRenderHelper.interpolate(shooter.posY, shooter.prevPosY) + yOffset - 0.45D;
                    playerPosZ = SHRenderHelper.interpolate(shooter.posZ, shooter.prevPosZ) - d * side + d1 * forward;
                }

                Vec3 src = Vec3.createVectorHelper(entityPosX, entityPosY, entityPosZ);
                Vec3 dst = Vec3.createVectorHelper(playerPosX, playerPosY, playerPosZ);
                double width = 1F / 48;
                double length = src.distanceTo(dst);
                byte segments = 24;

                x = entityPosX - RenderManager.renderPosX;
                y = entityPosY - RenderManager.renderPosY;
                z = entityPosZ - RenderManager.renderPosZ;
                GL11.glTranslated(x, y, z);
                SHRenderHelper.faceVec(src, dst);

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);

                Vec3 vecColor1 = SHRenderHelper.getColorFromHex(color1);
                Vec3 vecColor2 = SHRenderHelper.getColorFromHex(color2);

                for (int i = 0; i < segments; ++i)
                {
                    double segmentLength = length / segments;
                    double start = i * segmentLength;
                    double end = i * segmentLength + segmentLength;
                    float burn = MathHelper.clamp_float(1 - (1 - (float) i / segments) * grappleArrow.fireSpread * segments, 0, 1);

                    tessellator.startDrawingQuads();

                    if (i % 2 == 0)
                    {
                        Vec3 color = VectorHelper.multiply(vecColor1, burn);
                        tessellator.setColorRGBA_F((float) color.xCoord, (float) color.yCoord, (float) color.zCoord, 1.0F);
                    }
                    else
                    {
                        Vec3 color = VectorHelper.multiply(vecColor2, burn);
                        tessellator.setColorRGBA_F((float) color.xCoord, (float) color.yCoord, (float) color.zCoord, 1.0F);
                    }

                    tessellator.addVertex(width, width, end);
                    tessellator.addVertex(width, width, start);
                    tessellator.addVertex(-width, width, start);
                    tessellator.addVertex(-width, width, end);
                    tessellator.addVertex(-width, -width, start);
                    tessellator.addVertex(width, -width, start);
                    tessellator.addVertex(width, -width, end);
                    tessellator.addVertex(-width, -width, end);
                    tessellator.addVertex(-width, width, start);
                    tessellator.addVertex(-width, -width, start);
                    tessellator.addVertex(-width, -width, end);
                    tessellator.addVertex(-width, width, end);
                    tessellator.addVertex(width, -width, end);
                    tessellator.addVertex(width, -width, start);
                    tessellator.addVertex(width, width, start);
                    tessellator.addVertex(width, width, end);

                    if (i == segments - 1)
                    {
                        tessellator.addVertex(width, -width, end);
                        tessellator.addVertex(width, width, end);
                        tessellator.addVertex(-width, width, end);
                        tessellator.addVertex(-width, -width, end);
                    }
                    else if (i == 0)
                    {
                        tessellator.addVertex(-width, width, start);
                        tessellator.addVertex(width, width, start);
                        tessellator.addVertex(width, -width, start);
                        tessellator.addVertex(-width, -width, start);
                    }

                    tessellator.draw();
                }

                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_LIGHTING);
            }
        }

        GL11.glPopMatrix();
    }
}
