package com.fiskmods.heroes.client.particle;

import java.lang.reflect.Constructor;
import java.util.List;

import com.fiskmods.heroes.FiskHeroes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SHParticlesClient
{
    private static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation(FiskHeroes.MODID, "textures/particle/particles.png");
    public static final int FX_LAYERS = 5;
    public static final int LAYER_SH = 4;

    @SideOnly(Side.CLIENT)
    private static Minecraft mc = Minecraft.getMinecraft();

    private static final Class[] CONSTRUCTOR = {World.class, double.class, double.class, double.class, double.class, double.class, double.class};

    @SideOnly(Side.CLIENT)
    public static EntityFX spawnParticleClient(SHParticleType type, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null && mc.theWorld != null)
        {
            if (mc.theWorld.isRemote)
            {
                int particleSetting = mc.gameSettings.particleSetting;

                if (type == SHParticleType.QUANTUM_PARTICLE)
                {
                    particleSetting = 0;
                }

                if (particleSetting == 1 && mc.theWorld.rand.nextInt(3) == 0)
                {
                    particleSetting = 2;
                }

                double diffX = mc.renderViewEntity.posX - x;
                double diffY = mc.renderViewEntity.posY - y;
                double diffZ = mc.renderViewEntity.posZ - z;
                double maxDistance = Math.min(mc.gameSettings.renderDistanceChunks * 16, 64);
                boolean flag = type != SHParticleType.QUANTUM_PARTICLE;

                if (diffX * diffX + diffY * diffY + diffZ * diffZ > maxDistance * maxDistance && flag)
                {
                    return null;
                }
                else if (particleSetting > 1)
                {
                    return null;
                }

                try
                {
                    Constructor c = type.particleClass.call().getConstructor(CONSTRUCTOR);
                    EntityFX particle = (EntityFX) c.newInstance(mc.theWorld, x, y, z, motionX, motionY, motionZ);

                    mc.effectRenderer.addEffect(particle);

                    return particle;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public static EntityFX spawnParticleClient(EntityFX particle)
    {
        if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null && mc.theWorld != null)
        {
            if (mc.theWorld.isRemote)
            {
                int particleSetting = mc.gameSettings.particleSetting;

                if (particleSetting == 1 && mc.theWorld.rand.nextInt(3) == 0)
                {
                    particleSetting = 2;
                }

                double diffX = mc.renderViewEntity.posX - particle.posX;
                double diffY = mc.renderViewEntity.posY - particle.posY;
                double diffZ = mc.renderViewEntity.posZ - particle.posZ;
                double maxDistance = Math.min(mc.gameSettings.renderDistanceChunks * 16, 64);

                if (diffX * diffX + diffY * diffY + diffZ * diffZ > maxDistance * maxDistance)
                {
                    return null;
                }
                else if (particleSetting > 1)
                {
                    return null;
                }

                mc.effectRenderer.addEffect(particle);
                return particle;
            }
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public static int getParticlesInWorld(List[] list)
    {
        int i = 0;

        for (int j = 0; j < list.length - 1; ++j)
        {
            i += list[j].size();
        }

        return i;
    }

    @SideOnly(Side.CLIENT)
    public static void bindParticleTextures(int layer)
    {
        if (layer == LAYER_SH)
        {
            mc.getTextureManager().bindTexture(PARTICLE_TEXTURES);
        }
    }
}
