package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.particle.FlightParticlesAtom;
import com.fiskmods.heroes.client.particle.FlightParticlesBlackLightning;
import com.fiskmods.heroes.client.particle.FlightParticlesFirestorm;
import com.fiskmods.heroes.client.particle.FlightParticlesIronMan;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.VectorHelper;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public class HeroEffectPropelledFlight extends HeroEffect
{
    private static final Map<String, FlightParticleRenderer> REGISTRY = new HashMap<>();

    protected String type;
    private FlightParticleRenderer renderer;

    public static void registerFlightParticles(String key, FlightParticleRenderer particles)
    {
        REGISTRY.put(key, particles);
    }

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return false;
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("particleType") && next == JsonToken.STRING)
        {
            type = in.nextString();
        }
        else
        {
            in.skipValue();
        }
    }

    public FlightParticleRenderer getRenderer()
    {
        if (renderer == null && type != null)
        {
            renderer = REGISTRY.get(type);
        }

        return renderer;
    }

    public void doParticles(EntityLivingBase entity, HeroIteration iter, HeroRenderer renderer, Random rand, float scale, boolean clientPlayer, boolean firstPerson)
    {
        FlightParticleRenderer particles = getRenderer();

        if (particles != null)
        {
            Vec3 pos = VectorHelper.getPosition(entity, 1).addVector(0, VectorHelper.getOffset(entity), 0);
            double d = Math.min(Math.sqrt((entity.prevPosX - entity.posX) * (entity.prevPosX - entity.posX) + (entity.prevPosZ - entity.posZ) * (entity.prevPosZ - entity.posZ)) * 2, 1);
            double d1 = d * SHData.FLIGHT_ANIMATION.get(entity);
            float yaw = entity.renderYawOffset;

            particles.doParticles(entity, iter, renderer, rand, pos, d, d1, yaw, scale, clientPlayer, firstPerson);
        }
    }

    public interface FlightParticleRenderer
    {
        void doParticles(EntityLivingBase entity, HeroIteration iter, HeroRenderer renderer, Random rand, Vec3 pos, double d, double d1, double yaw, float scale, boolean clientPlayer, boolean firstPerson);

        default void scale(EntityFX entity, float scale)
        {
            if (entity != null)
            {
                entity.multipleParticleScaleBy(scale);
            }
        }

        default Vec3 orient(Vec3 vec3, double yaw, double d1)
        {
            vec3 = VectorHelper.copy(vec3);
            vec3.rotateAroundX((float) Math.toRadians(-d1 * 60));
            vec3.rotateAroundY((float) Math.toRadians(-yaw));

            return vec3;
        }
    }

    static
    {
        registerFlightParticles("IRON_MAN", FlightParticlesIronMan.INSTANCE);
        registerFlightParticles("FIRESTORM", FlightParticlesFirestorm.INSTANCE);
        registerFlightParticles("ATOM", FlightParticlesAtom.INSTANCE);
        registerFlightParticles("BLACK_LIGHTNING", FlightParticlesBlackLightning.INSTANCE);
    }
}
