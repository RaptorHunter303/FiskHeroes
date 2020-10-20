package com.fiskmods.heroes.client.render.hero.effect;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.json.hero.MultiTexture;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHClientUtils;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.VectorHelper;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class HeroEffectCape extends HeroEffect
{
    protected MultiTexture texture = MultiTexture.NULL;
    protected int length = 24;

    @Override
    public boolean shouldRenderPass(ModelBipedMultiLayer model, Entity entity, int pass)
    {
        return texture.hasTexture(pass) || !Pass.isTexturePass(pass);
    }

    @Override
    public void postRenderBody(ModelBipedMultiLayer model, Entity entity, int layer, float f, float f1, float f2, float f3, float f4, float scale)
    {
        if (conditionals.evaluate(entity))
        {
            model.renderParts(entity, model.bipedBody, scale, anim ->
            {
                renderCape(model, entity, layer, length);
            });
        }
    }

    public void renderCape(ModelBipedMultiLayer model, Entity entity, int layer, int capeLength)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE); // TODO: Add new Batman cape animation
        model.bipedBody.postRender(0.0625F);

        bindTexture(entity, model.armorSlot, texture, layer);

        int texWidth = 14;
        float quality = 1;
        float segments = 200 * quality;
        float width = texWidth * 0.0625F;
        float length = capeLength * 0.0625F / segments;

        GL11.glTranslatef(0, -0.02F, 0.1575F);
        float f2 = 0;
        float f4 = 1;
        float f5 = 0;
        float f11 = 0;
        float f12 = 0;
        float f13 = capeLength / 32F;
        float f14 = texWidth / 64F;

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;

            if (!SHClientUtils.isInanimate(player))
            {
                float scale = SHData.SCALE.interpolate(entity);
                double d3 = (SHRenderHelper.interpolate(player.field_71094_bP, player.field_71091_bM) - SHRenderHelper.interpolate(player.posX, player.prevPosX)) / scale;
                double d4 = (SHRenderHelper.interpolate(player.field_71095_bQ, player.field_71096_bN) - SHRenderHelper.interpolate(player.posY, player.prevPosY)) / scale;
                double d0 = (SHRenderHelper.interpolate(player.field_71085_bR, player.field_71097_bO) - SHRenderHelper.interpolate(player.posZ, player.prevPosZ)) / scale;
                float bodyYaw = SHRenderHelper.interpolate(player.renderYawOffset, player.prevRenderYawOffset);
                double d1 = MathHelper.sin(bodyYaw * (float) Math.PI / 180);
                double d2 = -MathHelper.cos(bodyYaw * (float) Math.PI / 180);
                float f7 = (float) d4 * 10;

                if (f7 < -6)
                {
                    f7 = -6;
                }

                if (f7 > 32)
                {
                    f7 = 32;
                }

                float f8 = (float) (d3 * d1 + d0 * d2) * 100;
                float f9 = (float) (d3 * d2 - d0 * d1) * 100;

                if (f8 < 0)
                {
                    f8 = 0;
                }

                float f10 = FiskServerUtils.interpolate(player.cameraYaw, player.prevCameraYaw, f7);
                f7 += MathHelper.sin(FiskServerUtils.interpolate(player.distanceWalkedModified, player.prevDistanceWalkedModified, f7) / scale * 6) * 32 * f10;

                f11 = MathHelper.clamp_float(f8 / 2 + f7, 0, 110);
                f12 = f11 / 100;
//                f13 = capeLength / 32F;
//                f14 = texWidth / 64F;

                float f = SHData.WING_ANIMATION_TIMER.interpolate(player);
                float f1 = 1 - f * f * 0.8F;
                f2 = -0.261799F;
                float f3 = 1;
                f4 = 1 - Math.min(f * 2, 1);
                f5 = MathHelper.clamp_float((f - 0.5F) * 2F, 0, 1);

                Vec3 motion = VectorHelper.multiply(SHRenderHelper.getMotion(player), 1F / scale);

                if (motion.yCoord < 0)
                {
                    motion = motion.normalize();
                    f3 = 1 - (float) Math.pow(-motion.yCoord, 1.5);
                }

                f2 = f3 * -1.570796F + (1 - f3) * f2;
                GL11.glRotatef((6 + f11) * f4 + 6 * f5, 1, 0, 0);
            }
        }

        if (f2 == 0)
        {
            GL11.glRotatef(6, 1, 0, 0);
        }

        for (int i = 0; i < 2; ++i)
        {
            GL11.glPushMatrix();

            if (f4 > 0)
            {
                for (int j = 0; j < segments; ++j)
                {
                    GL11.glRotatef(MathHelper.cos(f12 * 1.5F) * f12 / quality * f4, 1, 0, 0);
                    tessellator.startDrawingQuads();

                    if (i == 0)
                    {
                        tessellator.addVertexWithUV(width / 2, 0, 0, 0, f13 / segments * j);
                        tessellator.addVertexWithUV(width / 2, length, 0, 0, f13 / segments * (j + 1));
                        tessellator.addVertexWithUV(-width / 2, length, 0, f14, f13 / segments * (j + 1));
                        tessellator.addVertexWithUV(-width / 2, 0, 0, f14, f13 / segments * j);
                    }
                    else
                    {
                        tessellator.addVertexWithUV(-width / 2, length, 0, f14, f13 / segments * (j + 1));
                        tessellator.addVertexWithUV(width / 2, length, 0, f14 * 2, f13 / segments * (j + 1));
                        tessellator.addVertexWithUV(width / 2, 0, 0, f14 * 2, f13 / segments * j);
                        tessellator.addVertexWithUV(-width / 2, 0, 0, f14, f13 / segments * j);
                    }

                    tessellator.draw();
                    GL11.glTranslatef(0, length, 0);
                }
            }
            else
            {
                // f5 = (float)(player.ticksExisted % 100) / 100;
                // f5 = 0.5F;
                float fn = 1 + f5 * (float) (f2 / Math.PI * 2);
                float fm = 1 - fn;

                // for (int j = 0; j < 2; ++j)
                // {
                // tessellator.startDrawingQuads();
                // tessellator.addVertexWithUV(width / 2 - width / 2 * fm, 0, 0, 0, 0);
                // tessellator.addVertexWithUV(width / 2 - width / 2 * fm + 0.4F * fm, (capeLength + 16 * fm) * 0.0625F, 0, 0, f13 * 1F);
                // tessellator.addVertexWithUV(-width / 2 * fm * (1 - fm * 0.5F), (capeLength + 6 * fm) * 0.0625F, 0, f14 / 2, f13 * 1F);
                // tessellator.addVertexWithUV(-width / 2 * fm * (1 - fm * 0.5F), 0, 0, f14 / 2, 0);
                //
                // tessellator.addVertexWithUV(-width / 2 * fm * (1 - fm * 0.5F), 0, 0, 0, 0);
                // tessellator.addVertexWithUV(-width / 2 * fm * (1 - fm * 0.5F), (capeLength + 6 * fm) * 0.0625F, 0, 0, f13 * 1F);
                // tessellator.addVertexWithUV(-width / 2 * fm * (1 + fm * 0.5F), (capeLength + 3 * fm) * 0.0625F, 0, f14 / 2, f13 * 1F);
                // tessellator.addVertexWithUV(-width / 2 * fm * (1 + fm * 0), 0, 0, f14 / 2, 0);
                //
                // tessellator.addVertexWithUV(-width / 2 * fm * (1 - fm * 0.5F), 0, 0, 0, 0);
                // tessellator.addVertexWithUV(-width / 2 * fm * (1 - fm * 0.5F), (capeLength + 6 * fm) * 0.0625F, 0, 0, f13 * 1F);
                // tessellator.addVertexWithUV(-width / 2 * fm * (1 + fm * 0.5F), (capeLength + 3 * fm) * 0.0625F, 0, f14 / 2, f13 * 1F);
                // tessellator.addVertexWithUV(-width / 2 * fm * (1 + fm * 0), 0, 0, f14 / 2, 0);
                //
                //
                //// tessellator.addVertexWithUV(-width / 2 * fm, 0, 0, 0, 0);
                //// tessellator.addVertexWithUV(-width / 2 * fm * (1 + 0.5F * (1 - 0.35F * fm)), (capeLength + 7) * (1 - 0.35F * fm) * 0.0625F * fm, 0, 0, f13 * 1F);
                //// tessellator.addVertexWithUV(-width / 2 * fm * (1 + 1.5F), (capeLength - 17) * 0.0625F * fm, 0, f14 / 2, f13 * 1F);
                //// tessellator.addVertexWithUV(-width / 2 * fm * (1 + 1.5F), 0, 0, f14 / 2, 0);
                //
                //// tessellator.addVertexWithUV(-width / 2 * fm * (1 + fm * 1.5F), 0, 0, 0, 0);
                //// tessellator.addVertexWithUV(-width / 2 * fm * (1 + fm * 1.5F), (capeLength - 17 * fm) * 0.0625F, 0, 0, f13 * 1F);
                //// tessellator.addVertexWithUV(-width / 2 * fm * (1 + fm * 3.5F), (capeLength * fn) * 0.0625F, 0, f14 / 2, f13 * 1F);
                //// tessellator.addVertexWithUV(-width / 2 * fm * (1 + fm * 3.5F), 0, 0, f14 / 2, 0);
                //
                //
                //
                //
                // GL11.glPushMatrix();
                // GL11.glRotatef(90 * fm * (j == 0 ? -1 : 1), 0, 0, 1);
                // GL11.glRotatef(j == 0 ? 0 : 180, 0, 1, 0);
                // tessellator.draw();
                // GL11.glPopMatrix();
                // }

                tessellator.startDrawingQuads();

                if (i == 0)
                {
                    tessellator.addVertexWithUV(width / 2 * fn + width * 2.75F * fm, -0.4F * fm, 0, 0, 0);
                    tessellator.addVertexWithUV(width / 2 * fn + width * 2.25F * fm, capeLength * 0.3F * 0.0625F, 0, 0, f13 * 0.3F);
                    tessellator.addVertexWithUV(0, capeLength * 0.3F * 0.0625F, 0, f14 / 2, f13 * 0.3F);
                    tessellator.addVertexWithUV(0, 0, 0, f14 / 2, 0);
                    tessellator.addVertexWithUV(width / 2 * fn + width * 2.25F * fm, capeLength * 0.3F * 0.0625F, 0, 0, f13 * 0.3F);
                    tessellator.addVertexWithUV(width / 2 * fn + width * 0.9F * fm, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, 0, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 / 2, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(0, capeLength * 0.3F * 0.0625F, 0, f14 / 2, f13 * 0.3F);
                    tessellator.addVertexWithUV(width / 2 * fn + width * 0.9F * fm, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, 0, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(width / 2 * fn + width * 0.25F * fm, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, 0, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 / 2, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 / 2, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(width / 2 * fn + width * 0.25F * fm, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, 0, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(width / 2 * fn, capeLength * (1 + 0.4F * fm) * 0.0625F, 0, 0, f13);
                    tessellator.addVertexWithUV(0, capeLength * (1 + 0.4F * fm) * 0.0625F, 0, f14 / 2, f13);
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 / 2, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(0, capeLength * 0.3F * 0.0625F, 0, f14 / 2, f13 * 0.3F);
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 2.25F * fm, capeLength * 0.3F * 0.0625F, 0, f14, f13 * 0.3F);
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 2.75F * fm, -0.4F * fm, 0, f14, 0);
                    tessellator.addVertexWithUV(0, 0, 0, f14 / 2, 0);
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 / 2, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 0.9F * fm, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 2.25F * fm, capeLength * 0.3F * 0.0625F, 0, f14, f13 * 0.3F);
                    tessellator.addVertexWithUV(0, capeLength * 0.3F * 0.0625F, 0, f14 / 2, f13 * 0.3F);
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 / 2, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 0.25F * fm, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 0.9F * fm, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 / 2, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 + 0.4F * fm) * 0.0625F, 0, f14 / 2, f13);
                    tessellator.addVertexWithUV(-width / 2 * fn, capeLength * (1 + 0.4F * fm) * 0.0625F, 0, f14, f13);
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 0.25F * fm, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 / 2, f13 * (1 - 0.1F * fm));
                }
                else
                {
                    tessellator.addVertexWithUV(0, capeLength * 0.3F * 0.0625F, 0, f14 + f14 / 2, f13 * 0.3F);
                    tessellator.addVertexWithUV(width / 2 * fn + width * 2.25F * fm, capeLength * 0.3F * 0.0625F, 0, f14, f13 * 0.3F);
                    tessellator.addVertexWithUV(width / 2 * fn + width * 2.75F * fm, -0.4F * fm, 0, f14, 0);
                    tessellator.addVertexWithUV(0, 0, 0, f14 + f14 / 2, 0);
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 + f14 / 2, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(width / 2 * fn + width * 0.9F * fm, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 + 0, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(width / 2 * fn + width * 2.25F * fm, capeLength * 0.3F * 0.0625F, 0, f14 + 0, f13 * 0.3F);
                    tessellator.addVertexWithUV(0, capeLength * 0.3F * 0.0625F, 0, f14 + f14 / 2, f13 * 0.3F);
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 + f14 / 2, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(width / 2 * fn + width * 0.25F * fm, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 + 0, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(width / 2 * fn + width * 0.9F * fm, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 + 0, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 + f14 / 2, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 + 0.4F * fm) * 0.0625F, 0, f14 + f14 / 2, f13);
                    tessellator.addVertexWithUV(width / 2 * fn, capeLength * (1 + 0.4F * fm) * 0.0625F, 0, f14 + 0, f13);
                    tessellator.addVertexWithUV(width / 2 * fn + width * 0.25F * fm, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 + 0, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 + f14 / 2, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 2.75F * fm, -0.4F * fm, 0, f14 + f14, 0);
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 2.25F * fm, capeLength * 0.3F * 0.0625F, 0, f14 + f14, f13 * 0.3F);
                    tessellator.addVertexWithUV(0, capeLength * 0.3F * 0.0625F, 0, f14 + f14 / 2, f13 * 0.3F);
                    tessellator.addVertexWithUV(0, 0, 0, f14 + f14 / 2, 0);
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 2.25F * fm, capeLength * 0.3F * 0.0625F, 0, f14 + f14, f13 * 0.3F);
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 0.9F * fm, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 + f14, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 + f14 / 2, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(0, capeLength * 0.3F * 0.0625F, 0, f14 + f14 / 2, f13 * 0.3F);
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 0.9F * fm, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 + f14, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 0.25F * fm, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 + f14, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 + f14 / 2, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.4F * fm) * 0.0625F, 0, f14 + f14 / 2, f13 * (1 - 0.4F * fm));
                    tessellator.addVertexWithUV(-width / 2 * fn - width * 0.25F * fm, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 + f14, f13 * (1 - 0.1F * fm));
                    tessellator.addVertexWithUV(-width / 2 * fn, capeLength * (1 + 0.4F * fm) * 0.0625F, 0, f14 + f14, f13);
                    tessellator.addVertexWithUV(0, capeLength * (1 + 0.4F * fm) * 0.0625F, 0, f14 + f14 / 2, f13);
                    tessellator.addVertexWithUV(0, capeLength * (1 - 0.1F * fm) * 0.0625F, 0, f14 + f14 / 2, f13 * (1 - 0.1F * fm));
                }

                tessellator.draw();
            }

            GL11.glPopMatrix();
        }

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    @Override
    public void read(JsonReader in, String name, JsonToken next) throws IOException
    {
        if (name.equals("texture"))
        {
            texture = MultiTexture.read(in);
        }
        else if (name.equals("length") && next == JsonToken.NUMBER)
        {
            length = (int) in.nextDouble();
        }
        else
        {
            in.skipValue();
        }
    }
}
