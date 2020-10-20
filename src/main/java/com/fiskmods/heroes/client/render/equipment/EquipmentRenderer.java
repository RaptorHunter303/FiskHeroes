package com.fiskmods.heroes.client.render.equipment;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.hero.HeroIteration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public interface EquipmentRenderer extends Predicate<EntityPlayer>
{
    Set<EquipmentRenderer> REGISTRY = new HashSet<>();

    Minecraft mc = Minecraft.getMinecraft();

    public static void register()
    {
        REGISTRY.add(ConquestBadgeRenderer.INSTANCE);
        REGISTRY.add(QuiverRenderer.INSTANCE);
        REGISTRY.add(RewardGemRenderer.INSTANCE);
        REGISTRY.add(ShieldRenderer.INSTANCE);
        REGISTRY.add(TachyonDeviceRenderer.INSTANCE);
        REGISTRY.add(TachyonPrototypeRenderer.INSTANCE);
    }

    public static void render(EntityPlayer player, HeroIteration iter, ModelBiped model, float partialTicks)
    {
        REGISTRY.forEach(t -> render(t, player, iter, model, partialTicks));
    }

    public static void render(EquipmentRenderer renderer, EntityPlayer player, HeroIteration iter, ModelBiped model, float partialTicks)
    {
        if (renderer.test(player))
        {
            float[] offset = renderer.getOffset(player, iter, model, partialTicks);
            RenderEquipmentEvent event = new RenderEquipmentEvent(player, renderer, offset);

            if (!MinecraftForge.EVENT_BUS.post(event))
            {
                GL11.glPushMatrix();
                renderer.render(player, iter, model, event, partialTicks);
                GL11.glPopMatrix();
            }
        }
    }

    float[] getOffset(EntityPlayer player, HeroIteration iter, ModelBiped model, float partialTicks);

    void render(EntityPlayer player, HeroIteration iter, ModelBiped model, RenderEquipmentEvent event, float partialTicks);
}
