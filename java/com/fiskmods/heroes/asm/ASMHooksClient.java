package com.fiskmods.heroes.asm;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.render.entity.player.RenderPlayerHand;
import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.event.ClientEventHandler;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class ASMHooksClient
{
    private static Minecraft mc = Minecraft.getMinecraft();

    private static boolean intangible;

    public static void onRenderTick()
    {
        intangible = SHData.INTANGIBLE.get(mc.thePlayer);
    }

    public static int getBrightnessForRender(Entity entity)
    {
        if (SHData.isTracking(entity) && (SHData.SCALE.get(entity) < 1 || SHData.INTANGIBLE.get(entity) || SHData.GLIDING.get(entity) || SHData.SHADOWFORM.get(entity)))
        {
            return MathHelper.floor_double(entity.boundingBox.minY);
        }

        double d0 = (entity.boundingBox.maxY - entity.boundingBox.minY) * 0.66D;
        return MathHelper.floor_double(entity.posY - entity.yOffset + d0);
    }

    public static void applyPlayerRenderTranslation(RenderPlayer render, AbstractClientPlayer player, double x, double y, double z)
    {
        boolean isClientPlayer = player == mc.thePlayer;
        float scale = SHData.SCALE.interpolate(player);

        if (isClientPlayer)
        {
            GL11.glTranslatef(0, 1.62F * (scale - 1), 0);
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0, player.yOffset - 1.62F, 0);
        }
        else
        {
            GL11.glTranslatef((float) x, (float) y, (float) z);
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(-(float) x, -(float) y, -(float) z);
        }

        GL11.glTranslatef((float) x, (float) y, (float) z);

        int face = SHHelper.getSideStandingOn(player);

        if (face != 1)
        {
            if (face == 2)
            {
                GL11.glRotatef(-90, 1, 0, 0);
                GL11.glTranslatef(0, -player.width / 2, player.width / 2);
            }
        }
    }

    public static double getScaledSneakOffset(EntityPlayer player)
    {
        return 0.125D;
    }

    public static float getViewBobbing(float amount, Entity entity, float partialTicks)
    {
        if (mc.gameSettings.thirdPersonView == 0)
        {
            return amount;
        }
        else
        {
            return amount * ASMHooks.getEntityScale(entity);
        }
    }

    public static float getDistanceWalked(float dist, Entity entity, float partialTicks)
    {
        return dist / ASMHooks.getModifiedEntityScale(entity);
    }

    public static float getScaledWalkSpeedForFOV(PlayerCapabilities capabilities, EntityPlayerSP player)
    {
        return capabilities.getWalkSpeed() * ASMHooks.getStrengthScale(player);
    }

    public static float getPotionParticleScale(String name)
    {
        if (name.matches("mobSpell_.+"))
        {
            return Float.valueOf(name.split("_")[1]);
        }

        return 1;
    }

    public static boolean shouldSideBeRendered(boolean result, Block block, IBlockAccess world, int x, int y, int z, int side)
    {
        return result || intangible && !SHConfig.get().canPhase(block) && SHConfig.get().canPhase(world.getBlock(x, y, z));
    }

    public static float getInventoryPlayerScale(int scale, EntityLivingBase entity)
    {
        return scale / ASMHooks.getModifiedEntityScale(entity);
    }

    public static float getInventoryPlayerOffset(int offset, int originalScale, EntityLivingBase entity)
    {
        return offset;
    }

    public static float getInventoryPlayerOffsetPost(int offset, int originalScale, EntityLivingBase entity)
    {
        return entity.yOffset - 1.62F;
    }

    public static void orientCameraPre(EntityRenderer entityRenderer, float partialTicks)
    {
    }

    public static void orientCameraPost(EntityRenderer entityRenderer, float partialTicks)
    {
        // GL11.glRotatef(90, 1, 0, 0);
        // GL11.glTranslatef(0, 0, 1.62F - mc.thePlayer.width);
    }

    public static boolean renderFirstPersonArm(RenderPlayer render, EntityPlayer player)
    {
        Render rend = RenderManager.instance.getEntityRenderObject(player);
        RenderPlayerHand renderHand = ClientEventHandler.INSTANCE.renderHandInstance;

        if (rend instanceof RenderPlayer && !(rend instanceof RenderPlayerHand) && SHHelper.getHeroFromArmor(player, 2) != null)
        {
            RenderManager.instance.entityRenderMap.put(player.getClass(), renderHand);
            renderHand.setParent((RenderPlayer) rend);
            renderHand.renderFirstPersonArm(player);
            RenderManager.instance.entityRenderMap.put(player.getClass(), rend);

            return true;
        }

        return false;
    }
}
