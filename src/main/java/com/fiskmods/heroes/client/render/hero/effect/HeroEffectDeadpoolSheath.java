package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.MultiTexture;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.model.item.ModelSwordSheath;
import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.fiskmods.heroes.client.render.item.RenderItemDeadpoolsSwords;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;
import com.fiskmods.heroes.common.item.ModItems;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class HeroEffectDeadpoolSheath extends HeroEffect
{
    protected static final ModelSwordSheath MODEL = new ModelSwordSheath();

    protected MultiTexture texture = MultiTexture.NULL;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return texture.hasTexture(pass) || !Pass.isTexturePass(pass);
    }

    @Override
    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float scale)
    {
        if (conditionals.evaluate(entity))
        {
            model.renderParts(entity, model.bipedBody, scale, anim ->
            {
                bindTexture(entity, model.armorSlot, texture, pass);
                float s = 0.8F;

                GL11.glPushMatrix();
                GL11.glScalef(s, s, s);
                model.bipedBody.postRender(scale);
                GL11.glTranslatef(0.0F, 0.125F, 0.05F);
                MODEL.render(scale);

                if (pass != ENCHANTMENT && entity instanceof EntityLivingBase)
                {
                    EntityLivingBase living = (EntityLivingBase) entity;
                    ItemStack stack = living.getHeldItem();
                    int i = SHData.HAS_DEADPOOLS_SWORDS.get(living);

                    if (i > 0 && (stack == null || stack.getItem() != ModItems.deadpoolsSwords || living instanceof EntityDisplayMannequin))
                    {
                        boolean enchanted = i > 1;
                        s = 0.6F;

                        GL11.glPushMatrix();
                        MODEL.rightSheath1.postRender(scale);
                        GL11.glRotatef(90, 0, 1, 0);
                        GL11.glRotatef(180, 1, 0, 0);
                        GL11.glTranslatef(0, 0.675F, 0);
                        GL11.glScalef(s, s, s);
                        RenderItemDeadpoolsSwords.render(enchanted);
                        GL11.glPopMatrix();
                        MODEL.leftSheath1.postRender(scale);
                        GL11.glRotatef(-90, 0, 1, 0);
                        GL11.glRotatef(180, 1, 0, 0);
                        GL11.glTranslatef(0, 0.675F, 0);
                        GL11.glScalef(s, s, s);
                        RenderItemDeadpoolsSwords.render(enchanted);
                    }
                }

                GL11.glPopMatrix();
            });
        }
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("texture"))
        {
            texture = MultiTexture.read(in);
        }
        else
        {
            in.skipValue();
        }
    }
}
