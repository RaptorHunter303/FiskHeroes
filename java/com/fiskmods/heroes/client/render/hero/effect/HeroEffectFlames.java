package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityBookPlayer;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.SHHelper;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HeroEffectFlames extends HeroEffect
{
    protected boolean fullSuit = false;
    protected float scaleRatio = 1;
    protected float opacity = 1;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return pass == GLOW;
    }

    @Override
    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float f5)
    {
        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase living = (EntityLivingBase) entity;

            if (living.isEntityAlive() || living instanceof EntityBookPlayer && !living.isInvisible())
            {
                if ((!fullSuit || SHHelper.isHero(living)) && conditionals.evaluate(entity))
                {
                    float mask = 1 - SHData.MASK_OPEN_TIMER2.interpolate(living);
                    GL11.glColor4f(1, 1, 1, opacity);

                    if (scaleRatio != 0)
                    {
                        renderArm(model.bipedRightArm);
                        renderArm(model.bipedLeftArm);
                    }

                    if (mask > 0)
                    {
                        GL11.glPushMatrix();
                        model.bipedHead.showModel = true;
                        model.bipedHead.postRender(0.0625F);
                        GL11.glTranslatef(0, 0.1F, 0);
                        GL11.glColor4f(1, 1, 1, 0.3F);
                        SHRenderHooks.renderFire(0.6F, 2 * mask);
                        GL11.glTranslatef(0, -0.1F, 0);
                        SHRenderHooks.renderFire(0.65F, 1.1F * mask);
                        GL11.glPopMatrix();
                        GL11.glPushMatrix();
                        model.bipedBody.showModel = true;
                        model.bipedBody.postRender(0.0625F);
                        GL11.glTranslatef(0, 0.2F, 0);
                        GL11.glScalef(1, 1, 0.75F);
                        SHRenderHooks.renderFire(0.5F, 1.8F * mask);
                        GL11.glPopMatrix();
                    }
                }
            }
        }
    }

    @Override
    public void postRenderArm(ModelBipedMultiLayer model, EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
        if (scaleRatio != 0 && (!fullSuit || SHHelper.isHero(player)) && conditionals.evaluate(player))
        {
            GL11.glColor4f(1, 1, 1, opacity);
            renderArm(model.bipedRightArm);
        }
    }

    public void renderArm(ModelRenderer model)
    {
        GL11.glPushMatrix();
        model.showModel = true;
        model.postRender(0.0625F);

        if (model.rotationPointX > 0)
        {
            GL11.glScalef(-1, 1, 1);
        }

        GL11.glTranslatef(-0.0625F, 0.65F, 0);
        SHRenderHooks.renderFire(0.325F / scaleRatio, 1.9F * scaleRatio);
        GL11.glPopMatrix();
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("requiresFullSuit") && next == JsonToken.BOOLEAN)
        {
            fullSuit = in.nextBoolean();
        }
        else if (name.equals("scaleRatio") && next == JsonToken.NUMBER)
        {
            scaleRatio = (float) in.nextDouble();
        }
        else if (name.equals("opacity") && next == JsonToken.NUMBER)
        {
            opacity = (float) in.nextDouble();
        }
        else
        {
            in.skipValue();
        }
    }
}
