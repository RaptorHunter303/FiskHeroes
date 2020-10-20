package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelGrapplingHook;
import com.fiskmods.heroes.common.entity.gadget.EntityGrapplingHook;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
public class RenderGrapplingHook extends Render
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/grappling_hook.png");
    public static final ModelGrapplingHook MODEL = new ModelGrapplingHook();

    public void render(EntityGrapplingHook entity, double x, double y, double z, float f, float partialTicks)
    {
        GL11.glPushMatrix();
        Tessellator tessellator = Tessellator.instance;
        RenderManager renderManager = RenderManager.instance;

        if (entity.getShooter() instanceof EntityPlayer)
        {
            EntityPlayer shooter = (EntityPlayer) entity.getShooter();

            float f9 = shooter.getSwingProgress(partialTicks);
            float f10 = MathHelper.sin(MathHelper.sqrt_float(f9) * (float) Math.PI);
            Vec3 vec3 = Vec3.createVectorHelper(-0.375D, -0.15D, 0.5D);
            vec3.rotateAroundX(-SHRenderHelper.interpolate(shooter.rotationPitch, shooter.prevRotationPitch) * (float) Math.PI / 180.0F);
            vec3.rotateAroundY(-SHRenderHelper.interpolate(shooter.rotationYaw, shooter.prevRotationYaw) * (float) Math.PI / 180.0F);
            vec3.rotateAroundY(f10 * 0.5F);

            double entityPosX = SHRenderHelper.interpolate(entity.posX, entity.prevPosX);
            double entityPosY = SHRenderHelper.interpolate(entity.posY, entity.prevPosY);
            double entityPosZ = SHRenderHelper.interpolate(entity.posZ, entity.prevPosZ);
            double playerPosX = SHRenderHelper.interpolate(shooter.posX, shooter.prevPosX) + vec3.xCoord;
            double playerPosY = SHRenderHelper.interpolate(shooter.posY, shooter.prevPosY) + vec3.yCoord;
            double playerPosZ = SHRenderHelper.interpolate(shooter.posZ, shooter.prevPosZ) + vec3.zCoord;

            if (renderManager.options.thirdPersonView > 0 || shooter != Minecraft.getMinecraft().thePlayer)
            {
                float renderYawOffset = SHRenderHelper.interpolate(shooter.renderYawOffset, shooter.prevRenderYawOffset) * (float) Math.PI / 180.0F;
                double side = 0.4D;
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
            double width = 1F / 64;
            double length = src.distanceTo(dst);
            byte segments = 24;

            x = entityPosX - RenderManager.renderPosX;
            y = entityPosY - RenderManager.renderPosY;
            z = entityPosZ - RenderManager.renderPosZ;
            GL11.glTranslated(x, y, z);
            SHRenderHelper.faceVec(src, dst);

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);

            for (int i = 0; i < segments; ++i)
            {
                double segmentLength = length / segments;
                double start = i * segmentLength;
                double end = i * segmentLength + segmentLength;

                tessellator.startDrawingQuads();

                if (i % 2 == 0)
                {
                    tessellator.setColorRGBA_F(0.2F, 0.2F, 0.2F, 1.0F);
                }
                else
                {
                    tessellator.setColorRGBA_F(0.25F, 0.25F, 0.25F, 1.0F);
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

        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(90, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(180, 0, 1, 0);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        float f11 = entity.arrowShake - partialTicks;

        if (f11 > 0.0F)
        {
            float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        bindEntityTexture(entity);
        GL11.glTranslatef(0, -0.0625F * 5, 0);
        MODEL.render(SHRenderHelper.interpolate(entity.grappleTimer, entity.prevGrappleTimer));

        GL11.glRotatef(90, 0.0F, 0.0F, -1.0F);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        render((EntityGrapplingHook) entity, x, y, z, f, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return TEXTURE;
    }
}
