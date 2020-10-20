package com.fiskmods.heroes.client.particle;

import java.util.Random;

import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectPropelledFlight.FlightParticleRenderer;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public enum FlightParticlesIronMan implements FlightParticleRenderer
{
    INSTANCE;

    @Override
    public void doParticles(EntityLivingBase entity, HeroIteration iter, HeroRenderer renderer, Random rand, Vec3 pos, double d, double d1, double yaw, float scale, boolean clientPlayer, boolean firstPerson)
    {
        boolean hovering = SHData.HOVERING.get(entity);

        if (!firstPerson && !hovering)
        {
            Vec3 offsets = VectorHelper.multiply(Vec3.createVectorHelper(-0.4, -0.81, 0), scale);

            for (int side = 0; side < 2; ++side)
            {
                offsets.xCoord *= -1;
                Vec3 vec3 = VectorHelper.add(pos, orient(offsets, yaw, d1));

                for (int i = 0; i < 2; ++i)
                {
                    scale(SHParticlesClient.spawnParticleClient(SHParticleType.SHORT_FLAME, vec3.xCoord, vec3.yCoord, vec3.zCoord, (rand.nextDouble() * 2 - 1) / 40 * scale, -scale * 0.25, (rand.nextDouble() * 2 - 1) / 40 * scale), scale);
                }
            }
        }

        if (hovering && SHData.SUIT_OPEN_TIMER.get(entity) == 0)
        {
            Vec3 offsets = VectorHelper.multiply(Vec3.createVectorHelper(0.1, -0.5, -0.175), scale);

            for (int side = 0; side < 2; ++side)
            {
                offsets.xCoord *= -1;
                Vec3 vec3 = VectorHelper.add(pos, orient(offsets, yaw, d1));

                for (int i = 0; i < 2; ++i)
                {
                    scale(SHParticlesClient.spawnParticleClient(SHParticleType.SHORT_FLAME, vec3.xCoord, vec3.yCoord, vec3.zCoord, 0, -0.1 * scale, 0), scale);
                }
            }
        }

        if (firstPerson)
        {
            yaw = entity.rotationYaw;
        }

        Vec3 offsets = VectorHelper.multiply(Vec3.createVectorHelper(0.15, -1.62, 0), scale);

        for (int side = 0; side < 2; ++side)
        {
            offsets.xCoord *= -1;
            Vec3 vec3 = VectorHelper.add(pos, orient(offsets, yaw, d1));

            for (int i = 0; i < 2; ++i)
            {
                scale(SHParticlesClient.spawnParticleClient(SHParticleType.SHORT_FLAME, vec3.xCoord, vec3.yCoord, vec3.zCoord, (rand.nextDouble() * 2 - 1) / 40 * scale, -0.25 * scale, (rand.nextDouble() * 2 - 1) / 40 * scale), scale);
            }
        }
    }
}
