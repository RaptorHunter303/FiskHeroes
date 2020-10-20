package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ArrowRendererPhantom extends ArrowRenderer
{
    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);
        float f = 0.1F;
        GL11.glTranslatef(0, -0.625F, 0);
        GL11.glScalef(f, f, f);
        GL11.glRotatef(90, 0, 0, 1);

        SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);

        for (int i = 0; i < 3; ++i)
        {
            GL11.glTranslatef(1, 0, 0);
            itemRenderer.renderItem(mc.thePlayer, new ItemStack(ModBlocks.subatomicParticleShell), 0);
        }

        if (!inQuiver)
        {
            Tessellator tessellator = Tessellator.instance;
            float prevWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
            float ticks = arrow.ticksExisted + partialTicks;
            float offset = ticks * 2;

            float angleIncr = 10;
            float range = (5 + (1 - MathHelper.sin(ticks / 10))) / 6;
            float color = MathHelper.sin(ticks / 10);

            if (arrow.addedToChunk)
            {
                offset += arrow.getEntityId() * 100 % 1000;
            }

            GL11.glTranslatef(-1, 0, 0);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
            GL11.glLineWidth(10);

            for (int i = 0; i < 3; ++i)
            {
                tessellator.startDrawing(GL11.GL_LINE_STRIP);
                tessellator.setColorRGBA(54 + (int) (color * 25), 84, 181, 255);

                for (int j = 0; j <= MathHelper.ceiling_float_int(360F / angleIncr); ++j)
                {
                    Vec3 vec3 = Vec3.createVectorHelper(0, range, 0);
                    float pitch = j * angleIncr + offset;
                    float yaw = i * 120 + offset;
                    float roll = i * 120 + offset;
                    vec3.rotateAroundX(-pitch * (float) Math.PI / 180.0F);
                    vec3.rotateAroundY(-yaw * (float) Math.PI / 180.0F);
                    vec3.rotateAroundZ(-roll * (float) Math.PI / 180.0F);
                    tessellator.addVertex(vec3.xCoord, vec3.yCoord, vec3.zCoord);
                }

                tessellator.draw();
            }

            GL11.glLineWidth(prevWidth);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        SHRenderHelper.resetLighting();
    }
}
