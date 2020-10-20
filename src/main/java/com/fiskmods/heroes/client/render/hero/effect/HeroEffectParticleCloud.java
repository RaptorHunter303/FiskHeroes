package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.json.cloud.JsonCloud;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.particle.EntitySHCloudSmokeFX;
import com.fiskmods.heroes.client.particle.SHParticlesClient;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class HeroEffectParticleCloud extends HeroEffect
{
    protected JsonCloud particles;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return false;
    }

    @Override
    public void onClientTick(EntityPlayer player, HeroIteration iter, Phase phase)
    {
        if (phase == Phase.END && particles != null && particles.color != null && conditionals.evaluate(player))
        {
            double x = player.posX;
            double y = player.boundingBox.minY + player.height / 2;
            double z = player.posZ;
            double spread = 0.2F;
            double m = 1;
            double m1 = 0;
            int thickness = 4;

            if (FiskHeroes.proxy.isClientPlayer(player) && mc.gameSettings.thirdPersonView == 0)
            {
                spread *= 6;
                m = -0.5;
                thickness = 1;
            }

//            m = -1;
//            m1 = 0.1;
//            spread = 0.5;
//            thickness = 8;
//            y = player.boundingBox.minY;

            for (int i = 0; i < thickness; ++i)
            {
                EntitySHCloudSmokeFX particle = new EntitySHCloudSmokeFX(player.worldObj, particles.color, x + (Math.random() * 2 - 1) * spread, y + (Math.random() * 2 - 1) * spread, z + (Math.random() * 2 - 1) * spread, player.motionX * m + (Math.random() * 2 - 1) * m1, player.motionY * m + (Math.random() * 2 - 1) * m1, player.motionZ * m + (Math.random() * 2 - 1) * m1);
//                particle.pa
                SHParticlesClient.spawnParticleClient(particle);
            }
        }
    }

    public JsonCloud getParticles()
    {
        return particles;
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("particles") && next == JsonToken.STRING)
        {
            particles = JsonCloud.read(JsonCloud.GSON_FACTORY.create(), mc.getResourceManager(), new ResourceLocation(in.nextString()));
        }
        else
        {
            in.skipValue();
        }
    }
}
