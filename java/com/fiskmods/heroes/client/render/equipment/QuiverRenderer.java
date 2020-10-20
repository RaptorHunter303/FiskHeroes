package com.fiskmods.heroes.client.render.equipment;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.arrow.ArrowRenderer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectArchery;
import com.fiskmods.heroes.common.Pair;
import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.QuiverHelper;
import com.fiskmods.heroes.util.QuiverHelper.Quiver;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public enum QuiverRenderer implements EquipmentRenderer
{
    INSTANCE;

    @Override
    public boolean test(EntityPlayer player)
    {
        ItemStack stack = player.getHeldItem();
        return QuiverHelper.hasQuiver(player) && (stack == null || stack.getItem() != ModItems.quiver || player instanceof EntityDisplayMannequin);
    }

    @Override
    public float[] getOffset(EntityPlayer player, HeroIteration iter, ModelBiped model, float partialTicks)
    {
        return new float[] {0, -0.02F, 0};
    }

    @Override
    public void render(EntityPlayer player, HeroIteration iter, ModelBiped model, RenderEquipmentEvent event, float partialTicks)
    {
        HeroRenderer heroRenderer = HeroRenderer.get(iter);
        Quiver quiver = QuiverHelper.getQuiver(player);
        int metadata = quiver.getMetadata();
        float scale = 1.1F;
        boolean flag = false;

        ResourceLocation location = metadata == 0 ? HeroEffectArchery.QUIVER_TEXTURE : HeroEffectArchery.AUTO_QUIVER_TEXTURE;

        if (heroRenderer != null && iter != null)
        {
            if (!heroRenderer.shouldRenderDefaultModel(player, iter, true))
            {
                model.bipedBody.offsetY -= 256;
                flag = true;
            }

            HeroEffectArchery archery = heroRenderer.getEffect(HeroEffectArchery.class, player);

            if (archery != null)
            {
                ResourceLocation rl = archery.getQuiver(player, metadata);

                if (rl != null)
                {
                    location = rl;
                }
            }
        }

        mc.getTextureManager().bindTexture(location);
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(event.xOffset, event.yOffset, event.zOffset);
        ModelBipedMultiLayer.sync(model.bipedBody, HeroEffectArchery.MODEL.bipedBody);
        HeroEffectArchery.MODEL.bipedBody.showModel = true;
        HeroEffectArchery.MODEL.bipedBody.isHidden = false;
        HeroEffectArchery.MODEL.render();

        if (quiver.isEnchanted())
        {
            SHRenderHooks.renderEnchanted(HeroEffectArchery.MODEL::render);
        }

        HeroEffectArchery.MODEL.bipedBody.postRender(0.0625F);
        HeroEffectArchery.MODEL.baseRight.postRender(0.0625F);
        GL11.glRotatef(180, 0, 0, 1);
        GL11.glTranslatef(0.225F, 0.175F, 0.19F);

        if (flag)
        {
            model.bipedBody.offsetY += 256;
        }

        Random rand = new Random();

        for (int slot = 0; slot < 5; ++slot)
        {
            Pair<ArrowType, Integer> p = quiver.get(slot);
            int amount = p.getValue();

            if (amount > 0)
            {
                ArrowType type = p.getKey();
                ArrowRenderer renderer = ArrowRenderer.get(type);

                GL11.glPushMatrix();
                GL11.glTranslatef(0.0075F, 0, -0.022F);

                for (int i = 0; i < amount; ++i)
                {
                    GL11.glPushMatrix();

                    try
                    {
                        rand.setSeed((i + 1) * 1000000000);
                        GL11.glTranslatef((rand.nextFloat() - 0.5F) / 20, rand.nextFloat() / 10 - 0.075F, (rand.nextFloat() - 0.5F) / 20);
                        rand.setSeed(slot * 100000000);
                        GL11.glTranslatef((rand.nextFloat() - 0.5F) / 10, 0, (rand.nextFloat() - 0.5F) / 10);

                        scale = 0.7F;
                        GL11.glScalef(scale, scale, scale);
                        renderer.render(type.getDummyEntity(player), 0, 0, 0, partialTicks, true);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    GL11.glPopMatrix();
                }

                GL11.glPopMatrix();
            }
        }
    }
}
