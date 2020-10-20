package com.fiskmods.heroes.client.particle;

import java.util.Random;

import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectPropelledFlight.FlightParticleRenderer;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.VectorHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public enum FlightParticlesFirestorm implements FlightParticleRenderer
{
    INSTANCE;

    @Override
    public void doParticles(EntityLivingBase entity, HeroIteration iter, HeroRenderer renderer, Random rand, Vec3 pos, double d, double d1, double yaw, float scale, boolean clientPlayer, boolean firstPerson)
    {
        Vec3 offsets = Vec3.createVectorHelper(-0.4, -0.81, 0);
        int particles = 20;

        if (firstPerson)
        {
            yaw = entity.rotationYaw;
            d1 = Math.max(d1, 0);
            particles = 10;

            offsets.xCoord -= 0.25;
            offsets.zCoord -= 0.25;
        }

        offsets = VectorHelper.multiply(offsets, scale);

        for (int side = 0; side < 2; ++side)
        {
            if (side == 0 || !SHData.AIMING.get(entity))
            {
                offsets.xCoord *= -1;
                Vec3 vec3 = VectorHelper.add(pos, orient(offsets, yaw, d1));

                for (int i = 0; i < particles; ++i)
                {
                    scale(SHParticlesClient.spawnParticleClient(SHParticleType.FLAME, vec3.xCoord + (rand.nextDouble() * 2 - 1) / 10, vec3.yCoord, vec3.zCoord + (rand.nextDouble() * 2 - 1) / 10, (rand.nextDouble() * 2 - 1) / 20 * scale, -0.25 * scale, (rand.nextDouble() * 2 - 1) / 20 * scale), scale);
                }
            }
        }
    }
}
