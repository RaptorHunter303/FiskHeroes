package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.json.cloud.JsonCloud;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class HeroEffectTeleportation extends HeroEffect
{
    protected JsonCloud particles;
    protected float[] glowColor;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
//        return pass == GLOW && glowColor != null;
        return false;
    }

    @Override
    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float f5)
    {
        render(model, entity, pass, () -> model.renderBody(entity, pass, f, f1, f2, f3, f4, f5));
    }

    @Override
    public void postRenderArm(ModelBipedMultiLayer model, EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass)
    {
        render(model, player, pass, () -> model.renderArm(player, itemstack, iter, pass, 0.0625F));
    }

    // FIXME: Render other effects
    protected void render(ModelBipedMultiLayer model, Entity entity, int pass, Runnable runnable)
    {
        float f;

        if (glowColor != null && conditionals.evaluate(entity) && (f = SHData.TELEPORT_DELAY.get(entity)) > 0)
        {
            f -= FiskHeroes.proxy.getRenderTick();
            f /= SHConstants.TICKS_TELEPORT_DELAY * 2;
            f = 1 - FiskMath.curveCrests(f * 2);

            if (f > 0)
            {
                renderer.resetTexture(pass);

                GL11.glDepthMask(false);
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                SHRenderHelper.setGlColor(glowColor, f);
                SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
                runnable.run();
                SHRenderHelper.resetLighting();
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glDepthMask(true);
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
        if (name.equals("glowColor"))
        {
            glowColor = SHRenderHelper.hexToRGB(readInt(in, 0xFFFFFF));
        }
        else if (name.equals("particles") && next == JsonToken.STRING)
        {
            particles = JsonCloud.read(JsonCloud.GSON_FACTORY.create(), mc.getResourceManager(), new ResourceLocation(in.nextString()));
        }
        else
        {
            in.skipValue();
        }
    }
}
