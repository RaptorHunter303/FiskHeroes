package com.fiskmods.heroes.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySHIcicleBreakFX extends EntitySHFX
{
    public EntitySHIcicleBreakFX(World world, double x, double y, double z)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        particleRed = particleGreen = particleBlue = 1.0F;
        particleGravity = Blocks.snow.blockParticleGravity;
        particleScale /= 2.0F;
        setParticleTextureIndex(16);
    }

    public EntitySHIcicleBreakFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        this(world, x, y, z);
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float f, float f1, float f2, float f3, float f5)
    {
        float f6 = (particleTextureIndexX + particleTextureJitterX / 4.0F) / 16.0F;
        float f7 = f6 + 0.015609375F;
        float f8 = (particleTextureIndexY + particleTextureJitterY / 4.0F) / 16.0F;
        float f9 = f8 + 0.015609375F;
        float f10 = 0.1F * particleScale;
        float f11 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float f12 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float f13 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
        tessellator.setColorOpaque_F(particleRed, particleGreen, particleBlue);
        tessellator.addVertexWithUV(f11 - f * f10 - f3 * f10, f12 - f1 * f10, f13 - f2 * f10 - f5 * f10, f6, f9);
        tessellator.addVertexWithUV(f11 - f * f10 + f3 * f10, f12 + f1 * f10, f13 - f2 * f10 + f5 * f10, f6, f8);
        tessellator.addVertexWithUV(f11 + f * f10 + f3 * f10, f12 + f1 * f10, f13 + f2 * f10 + f5 * f10, f7, f8);
        tessellator.addVertexWithUV(f11 + f * f10 - f3 * f10, f12 - f1 * f10, f13 + f2 * f10 - f5 * f10, f7, f9);
    }
}
