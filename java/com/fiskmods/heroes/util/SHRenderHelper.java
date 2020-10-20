package com.fiskmods.heroes.util;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.client.json.trail.JsonTrail;
import com.fiskmods.heroes.client.json.trail.JsonTrailFlicker;
import com.fiskmods.heroes.client.render.Lightning;
import com.fiskmods.heroes.client.render.LightningData;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectTrail;
import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.event.ClientEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class SHRenderHelper
{
    protected static Minecraft mc = Minecraft.getMinecraft();
    protected static RenderItem renderItem = RenderItem.getInstance();

    private static float lastBrightnessX;
    private static float lastBrightnessY;

    public static final int FULLBRIGHT = 0xF000F0;
    public static final Vec3 WHITE = Vec3.createVectorHelper(1, 1, 1);

    private static Map<EntityPlayer, Vec3> prevMotion = new HashMap<>();

    public static void setLighting(int lighting)
    {
        storeLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lighting % 65536, lighting / 65536);
    }

    public static void storeLighting()
    {
        lastBrightnessX = OpenGlHelper.lastBrightnessX;
        lastBrightnessY = OpenGlHelper.lastBrightnessY;
    }

    public static void resetLighting()
    {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
    }

    public static void setGlColor(Color color, float alpha)
    {
        GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alpha);
    }

    public static void setGlColor(Color color)
    {
        setGlColor(color, color.getAlpha() / 255F);
    }

    public static void setGlColor(Vec3 color, float alpha)
    {
        GL11.glColor4f((float) color.xCoord, (float) color.yCoord, (float) color.zCoord, alpha);
    }

    public static void setGlColor(Vec3 color)
    {
        setGlColor(color, 1);
    }

    public static void setGlColor(float[] color, float alpha)
    {
        GL11.glColor4f(color[0], color[1], color[2], alpha);
    }

    public static void setGlColor(float[] color)
    {
        setGlColor(color, 1);
    }

    public static void applyColorFromItemStack(ItemStack itemstack, int pass)
    {
        int color = itemstack.getItem().getColorFromItemStack(itemstack, pass);
        float r = (color >> 16 & 255) / 255F;
        float g = (color >> 8 & 255) / 255F;
        float b = (color & 255) / 255F;
        GL11.glColor4f(r, g, b, 1);
    }

    public static Vec3 getColorFromHex(int hex)
    {
        float r = (hex >> 16 & 255) / 255F;
        float g = (hex >> 8 & 255) / 255F;
        float b = (hex & 255) / 255F;
        return Vec3.createVectorHelper(r, g, b);
    }

    public static int getHex(Vec3 color)
    {
        int r = (int) Math.round(color.xCoord * 255);
        int g = (int) Math.round(color.yCoord * 255);
        int b = (int) Math.round(color.zCoord * 255);
        return r << 16 | g << 8 | b;
    }

    public static float[] hexToRGB(int hex)
    {
        float r = ((hex & 0xFF0000) >> 16) / 255F;
        float g = ((hex & 0xFF00) >> 8) / 255F;
        float b = (hex & 0xFF) / 255F;
        return new float[] {r, g, b};
    }

    public static void setAlpha(float alpha)
    {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
        floatBuffer.rewind();
        GL11.glGetFloat(GL11.GL_CURRENT_COLOR, floatBuffer);
        GL11.glColor4f(floatBuffer.get(), floatBuffer.get(), floatBuffer.get(), alpha);
    }

    public static float getAlpha()
    {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
        floatBuffer.rewind();
        GL11.glGetFloat(GL11.GL_CURRENT_COLOR, floatBuffer);

        return floatBuffer.get(3);
    }

    public static Vec3 fade(Vec3 from, Vec3 to, float percentage)
    {
        percentage = MathHelper.clamp_float(percentage, 0, 1);
        float f = 1 - percentage;

        if (percentage == 1)
        {
            return to;
        }
        else if (percentage == 0)
        {
            return from;
        }

        return VectorHelper.add(VectorHelper.multiply(from, f), VectorHelper.multiply(to, percentage));
    }

    public static Vec3 fade(int from, int to, float percentage)
    {
        if (percentage == 1)
        {
            return getColorFromHex(to);
        }
        else if (percentage == 0)
        {
            return getColorFromHex(from);
        }

        return fade(getColorFromHex(from), getColorFromHex(to), percentage);
    }

    public static Color blend(Color c1, Color c2, float ratio)
    {
        if (ratio > 1F)
        {
            ratio = 1F;
        }
        else if (ratio < 0F)
        {
            ratio = 0F;
        }

        Float iRatio = 1 - ratio;

        int i1 = c1.getRGB();
        int i2 = c2.getRGB();

        int a1 = i1 >> 24 & 0xFF;
        int r1 = (i1 & 0xFF0000) >> 16;
        int g1 = (i1 & 0xFF00) >> 8;
        int b1 = i1 & 0xFF;

        int a2 = i2 >> 24 & 0xFF;
        int r2 = (i2 & 0xFF0000) >> 16;
        int g2 = (i2 & 0xFF00) >> 8;
        int b2 = i2 & 0xFF;

        int a = (int) (a1 * iRatio + a2 * ratio);
        int r = (int) (r1 * iRatio + r2 * ratio);
        int g = (int) (g1 * iRatio + g2 * ratio);
        int b = (int) (b1 * iRatio + b2 * ratio);

        return new Color(a << 24 | r << 16 | g << 8 | b);
    }

    public static void startGlScissor(int x, int y, int width, int height)
    {
        ScaledResolution reso = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        double scaleW = mc.displayWidth / reso.getScaledWidth_double();
        double scaleH = mc.displayHeight / reso.getScaledHeight_double();

        if (width <= 0 || height <= 0)
        {
            return;
        }
        if (x < 0)
        {
            x = 0;
        }
        if (y < 0)
        {
            y = 0;
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) Math.floor(x * scaleW), (int) Math.floor(mc.displayHeight - (y + height) * scaleH), (int) Math.floor((x + width) * scaleW) - (int) Math.floor(x * scaleW), (int) Math.floor(mc.displayHeight - y * scaleH) - (int) Math.floor(mc.displayHeight - (y + height) * scaleH));
    }

    public static void endGlScissor()
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void renderLightning(Lightning lightning, float opacity)
    {
        if (lightning.rotateAngleZ != 0)
        {
            GL11.glRotatef(lightning.rotateAngleZ, 0, 0, 1);
        }

        if (lightning.rotateAngleY != 0)
        {
            GL11.glRotatef(lightning.rotateAngleY, 0, 1, 0);
        }

        if (lightning.rotateAngleX != 0)
        {
            GL11.glRotatef(lightning.rotateAngleX, 1, 0, 0);
        }

        drawLightningLine(Vec3.createVectorHelper(0, 0, 0), Vec3.createVectorHelper(0, lightning.length, 0), 5, 1, lightning.lightningColor, lightning.scale, opacity, opacity);
        GL11.glTranslatef(0, lightning.length, 0);

        for (Lightning lightning1 : lightning.children)
        {
            GL11.glPushMatrix();
            renderLightning(lightning1, opacity);
            GL11.glPopMatrix();
        }
    }

    public static void doLightningAura(EntityPlayer player, Vec3 color, int density, float spread, float length)
    {
        float scale = SHData.SCALE.interpolate(player);

        for (int i = 0; i < density; ++i)
        {
            double d0 = player.getRNG().nextFloat() * 2 - 1;
            double d1 = player.getRNG().nextFloat() * 2 - 1;
            double d2 = player.getRNG().nextFloat() * 2 - 1;

            if (d0 * d0 + d1 * d1 + d2 * d2 >= 1)
            {
                double d3 = d0 * player.width / 4 * spread;
                double d4 = (d1 * spread * scale + player.height) / 2;
                double d5 = d2 * player.width / 4 * spread;

                Lightning lightning = SHHelper.createLightning(color, 0, length * scale, scale);
                HeroEffectTrail.addLightningData(player, new LightningData(lightning, d3, d4, d5));
            }
        }
    }

    public static void doLightningAura(EntityPlayer player, JsonTrail trail)
    {
        if (trail.flicker != null)
        {
            JsonTrailFlicker f = trail.flicker.get();
            doLightningAura(player, trail.flicker.getVecColor(player), f.getDensity(), f.getSpread(), f.getLength());
        }
    }

    public static boolean shouldOverrideView(EntityPlayer player)
    {
        return SHData.SCALE.get(player) != 1 || SHHelper.getSideStandingOn(player) != 1 || SHData.GLIDING.get(player) || SHData.SHADOWFORM_TIMER.get(player) > 0;
    }

    public static boolean shouldOverrideThirdPersonDistance(EntityPlayer player)
    {
        return SHHelper.getHero(player) != null || SHHelper.getPrevHero(player) != null;
    }

    public static double interpolate(double curr, double prev)
    {
        return FiskServerUtils.interpolate(prev, curr, ClientEventHandler.renderTick);
    }

    public static float interpolate(float curr, float prev)
    {
        return FiskServerUtils.interpolate(prev, curr, ClientEventHandler.renderTick);
    }

    public static void faceVec(Vec3 src, Vec3 dst)
    {
        double x = dst.xCoord - src.xCoord;
        double y = dst.yCoord - src.yCoord;
        double z = dst.zCoord - src.zCoord;
        double diff = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180 / Math.PI) - 90;
        float pitch = (float) -(Math.atan2(y, diff) * 180 / Math.PI);
        GL11.glRotated(-yaw, 0, 1, 0);
        GL11.glRotated(pitch, 1, 0, 0);
    }

    public static void drawLightningLine(Vec3 start, Vec3 end, float lineWidth, float innerLineWidth, Vec3 color, float scale, float alphaStart, float alphaEnd)
    {
        if (start == null || end == null)
        {
            return;
        }

        Tessellator tessellator = Tessellator.instance;

        if (SHConfig.oldLightning.test())
        {
            float prevWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);

            if (lineWidth > 0)
            {
                GL11.glLineWidth(lineWidth);
                tessellator.startDrawing(3);
                tessellator.setColorRGBA_F((float) color.xCoord, (float) color.yCoord, (float) color.zCoord, alphaStart);
                tessellator.addVertex(start.xCoord, start.yCoord, start.zCoord);
                tessellator.setColorRGBA_F((float) color.xCoord, (float) color.yCoord, (float) color.zCoord, alphaEnd);
                tessellator.addVertex(end.xCoord, end.yCoord, end.zCoord);
                tessellator.draw();
            }

            if (innerLineWidth > 0)
            {
                GL11.glLineWidth(innerLineWidth);
                tessellator.startDrawing(3);
                tessellator.setColorRGBA_F(1, 1, 1, Math.max(alphaStart - 0.2F, 0));
                tessellator.addVertex(start.xCoord, start.yCoord, start.zCoord);
                tessellator.setColorRGBA_F(1, 1, 1, Math.max(alphaEnd - 0.2F, 0));
                tessellator.addVertex(end.xCoord, end.yCoord, end.zCoord);
                tessellator.draw();
            }

            GL11.glLineWidth(prevWidth);
            return;
        }

        float[] afloat = {(float) color.xCoord, (float) color.yCoord, (float) color.zCoord};
        double length = start.distanceTo(end);
        int ao = mc.gameSettings.ambientOcclusion;
        int layers = 6 + ao * 3;

        GL11.glPushMatrix();
        GL11.glTranslated(start.xCoord, start.yCoord, start.zCoord);
        SHRenderHelper.faceVec(start, end);

        for (int layer = 0; layer <= layers; ++layer)
        {
            double size = (innerLineWidth * 0.6 + (layer < layers ? lineWidth * layer * layer * (0.125 / layers) : 0)) * scale;
            double width = size / 48;

            float opacityStart;
            float opacityEnd;

            if (layer < layers)
            {
                GL11.glDepthMask(false);
                opacityStart = alphaStart * 0.5F / layers;
                opacityEnd = alphaEnd * 0.5F / layers;
            }
            else
            {
                GL11.glDepthMask(true);
                opacityStart = Math.max(alphaStart - 0.2F, 0);
                opacityEnd = Math.max(alphaEnd - 0.2F, 0);
                afloat = new float[] {1, 1, 1};
            }

            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacityEnd);
            tessellator.addVertex(width, width, length);
            tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacityStart);
            tessellator.addVertex(width, width, 0);
            tessellator.addVertex(-width, width, 0);
            tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacityEnd);
            tessellator.addVertex(-width, width, length);
            tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacityStart);
            tessellator.addVertex(-width, -width, 0);
            tessellator.addVertex(width, -width, 0);
            tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacityEnd);
            tessellator.addVertex(width, -width, length);
            tessellator.addVertex(-width, -width, length);
            tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacityStart);
            tessellator.addVertex(-width, width, 0);
            tessellator.addVertex(-width, -width, 0);
            tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacityEnd);
            tessellator.addVertex(-width, -width, length);
            tessellator.addVertex(-width, width, length);
            tessellator.addVertex(width, -width, length);
            tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacityStart);
            tessellator.addVertex(width, -width, 0);
            tessellator.addVertex(width, width, 0);
            tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacityEnd);
            tessellator.addVertex(width, width, length);
            tessellator.addVertex(width, -width, length);
            tessellator.addVertex(width, width, length);
            tessellator.addVertex(-width, width, length);
            tessellator.addVertex(-width, -width, length);
            tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], opacityStart);
            tessellator.addVertex(-width, width, 0);
            tessellator.addVertex(width, width, 0);
            tessellator.addVertex(width, -width, 0);
            tessellator.addVertex(-width, -width, 0);
            tessellator.draw();
        }

        GL11.glPopMatrix();
    }

    public static void setupRenderLightning()
    {
        if (SHConfig.vibrantLightningColors)
        {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        }
        else
        {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, 1);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        setLighting(FULLBRIGHT);
    }

    public static void finishRenderLightning()
    {
        resetLighting();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();

        if (SHConfig.vibrantLightningColors)
        {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public static void updatePrevMotion(EntityPlayer player)
    {
        prevMotion.put(player, player == mc.thePlayer ? Vec3.createVectorHelper(player.motionX, player.motionY, player.motionZ) : Vec3.createVectorHelper(player.posX - player.prevPosX, player.posY - player.prevPosY, player.posZ - player.prevPosZ));
    }

    public static Vec3 getMotion(EntityPlayer player)
    {
        Vec3 curr = player == mc.thePlayer ? Vec3.createVectorHelper(player.motionX, player.motionY, player.motionZ) : Vec3.createVectorHelper(player.posX - player.prevPosX, player.posY - player.prevPosY, player.posZ - player.prevPosZ);
        Vec3 prev = prevMotion.containsKey(player) ? prevMotion.get(player) : curr;

        return Vec3.createVectorHelper(interpolate(curr.xCoord, prev.xCoord), interpolate(curr.yCoord, prev.yCoord), interpolate(curr.zCoord, prev.zCoord));
    }

    public static void setupRenderItemIntoGUI()
    {
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        RenderHelper.enableGUIStandardItemLighting();
    }

    public static void finishRenderItemIntoGUI()
    {
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1, 1, 1, 1);
    }

    public static void setupRenderHero(boolean light)
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (light)
        {
            GL11.glDisable(GL11.GL_LIGHTING);
            SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
        }
    }

    public static void finishRenderHero(boolean light)
    {
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_BLEND);

        if (light)
        {
            GL11.glEnable(GL11.GL_LIGHTING);
            SHRenderHelper.resetLighting();
        }
    }
}
