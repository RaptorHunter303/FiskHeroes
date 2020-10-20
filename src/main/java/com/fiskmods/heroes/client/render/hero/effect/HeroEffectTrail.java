package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.fiskmods.heroes.client.json.hero.JsonHeroRenderer;
import com.fiskmods.heroes.client.json.trail.JsonTrail;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.LightningData;
import com.fiskmods.heroes.client.render.effect.EffectRenderHandler;
import com.fiskmods.heroes.client.render.effect.EffectTrail;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntitySpeedBlur;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;
import com.fiskmods.heroes.util.VectorHelper;
import com.google.common.collect.Iterables;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class HeroEffectTrail extends HeroEffect
{
    public static Map<EntityPlayer, LinkedList<EntitySpeedBlur>> trailData = new HashMap<>();
    public static Map<EntityPlayer, LinkedList<LightningData>> lightningData = new HashMap<>();
    public static Map<EntityPlayer, Float> traveledBlocks = new HashMap<>();
    public static Map<EntityPlayer, Vec3> lastPos = new HashMap<>();

    protected JsonTrail type;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return false;
    }

    @Override
    public void onClientTick(EntityPlayer player, HeroIteration iter, Phase phase)
    {
        super.onClientTick(player, iter, phase);

        if (SpeedsterHelper.isOnTreadmill(player))
        {
            float f = Math.min(SHData.TREADMILL_LIMB_FACTOR.get(player), 1);
            Vec3 color = type.getTrailLightning().getVecColor(player);

            for (float f1 = 0.2F; f1 < f; f1 += 0.2F)
            {
                if (Math.random() < 0.4)
                {
                    SHRenderHelper.doLightningAura(player, color, 8, 1, 0.1F);
                }
            }
        }

        tick(player, type, phase, shouldTick(player, phase));
    }

    public static void tick(EntityPlayer player, JsonTrail type, Phase phase, boolean variables)
    {
        if (phase == Phase.START)
        {
            if (variables)
            {
                Vec3 pos = player.getPosition(1);
                Vec3 prev = lastPos.get(player);

                if (prev != null && !VectorHelper.equal(pos, prev))
                {
                    float dist = traveledBlocks.getOrDefault(player, 0F) + (float) player.getDistance(prev.xCoord, prev.yCoord, prev.zCoord);

                    if (dist >= player.width + player.width / 10)
                    {
                        LinkedList<EntitySpeedBlur> list = getTrail(player);
                        EntitySpeedBlur entity = new EntitySpeedBlur(player.worldObj, player);
                        int fade = entity.trail.fade;

                        if (entity.trail.particles != null)
                        {
                            fade = Math.max(fade, entity.trail.particles.getFade());
                        }

                        while (list.size() >= fade)
                        {
                            list.remove(0);
                        }

                        list.add(entity);
                        dist = 0;

                        trailData.put(player, list);
                        player.worldObj.spawnEntityInWorld(entity);
                    }

                    traveledBlocks.put(player, dist);
                }

                lastPos.put(player, pos);
            }

            if (type.flicker != null && (variables || !type.flicker.get().isConfined()) && Math.random() < type.flicker.get().getFrequency())
            {
                SHRenderHelper.doLightningAura(player, type);
            }
        }
        else if (!(getTrail(player).isEmpty() && getLightningData(player).isEmpty()) && !Iterables.any(EffectRenderHandler.get(player), e -> e.effect == EffectTrail.INSTANCE))
        {
            EffectRenderHandler.add(player, EffectTrail.INSTANCE, -1);
        }
    }

    public boolean shouldTick(EntityPlayer player, Phase phase)
    {
        return phase == Phase.START && conditionals.evaluate(player);
    }

    public static LinkedList<EntitySpeedBlur> getTrail(EntityPlayer player)
    {
        return trailData.computeIfAbsent(player, k -> new LinkedList<>());
    }

    public static void addLightningData(EntityPlayer player, LightningData data)
    {
        LinkedList<LightningData> list = getLightningData(player);
        list.add(data);

        lightningData.put(player, list);
    }

    public static LinkedList<LightningData> getLightningData(EntityPlayer player)
    {
        return lightningData.computeIfAbsent(player, k -> new LinkedList<>());
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("type") && next == JsonToken.STRING)
        {
            type = JsonTrail.read(JsonTrail.GSON_FACTORY.create(), mc.getResourceManager(), new ResourceLocation(in.nextString()));
        }
        else
        {
            in.skipValue();
        }
    }

    @Override
    public void load(JsonHeroRenderer json, IResourceManager manager, TextureManager textureManager) throws IOException
    {
        type.load(manager, textureManager);
    }

    public JsonTrail getType(EntityLivingBase entity)
    {
        return type;
    }
}
