package com.fiskmods.heroes.client.particle;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntitySHSpellWaveFX extends EntityFX
{
    public EntitySHSpellWaveFX(World world, double x, double y, double z, Vec3 motion, float[] color)
    {
        super(world, x, y, z, 0, 0, 0);
        motionX = motion.xCoord * 1.5;
        motionY = motion.yCoord * 1.5;
        motionZ = motion.zCoord * 1.5;
        particleRed = color[0];
        particleGreen = color[1];
        particleBlue = color[2];
        particleMaxAge = 30;
        noClip = true;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float f, float f1, float f2, float f3, float f4)
    {
        float x = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float y = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float z = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
        float f5 = (particleAge + partialTicks) / particleMaxAge;
        float opacity = 0.4F * FiskMath.curve(1 - f5);
        float radius = f5 + f5 * (1 + f5) * 10;
        float radius1 = f5 + f5 * (1 + f5) * 5;
        float corners = 36;

        tessellator.draw();
        tessellator.startDrawingQuads();
        float[] pr = {0, 0}, pr1 = {0, 0};

        for (int i = 0; i <= corners; ++i)
        {
            float rot = -(float) Math.toRadians(360 / corners * i);
            float[] r = {radius * MathHelper.cos(rot), radius * -MathHelper.sin(rot)};
            float[] r1 = {radius1 * MathHelper.cos(rot), radius1 * -MathHelper.sin(rot)};

            tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, 0);
            tessellator.addVertex(r1[0], r1[1], 0);
            tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, opacity);
            tessellator.addVertex(r[0], r[1], 0);
            tessellator.addVertex(pr[0], pr[1], 0);
            tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, 0);
            tessellator.addVertex(pr1[0], pr1[1], 0);
            pr = r;
            pr1 = r1;
        }

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        GL11.glTranslatef(x, y + height / 2, z);
        SHRenderHelper.faceVec(Vec3.createVectorHelper(lastTickPosX, lastTickPosY, lastTickPosZ), Vec3.createVectorHelper(posX, posY, posZ));
        SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
        tessellator.draw();
        SHRenderHelper.resetLighting();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glPopMatrix();
        tessellator.startDrawingQuads();
    }

    @Override
    public int getBrightnessForRender(float partialTicks)
    {
        return SHRenderHelper.FULLBRIGHT;
    }

    @Override
    public float getBrightness(float partialTicks)
    {
        return 1.0F;
    }

    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (++particleAge >= particleMaxAge)
        {
            setDead();
        }

        moveEntity(motionX, motionY, motionZ);
    }
}
