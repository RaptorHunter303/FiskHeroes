package com.fiskmods.heroes.client.render.effect;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderEffectEntity extends Render
{
    public void doRender(EffectEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        Entity anchor = entity.anchorEntity;

        if (anchor != null)
        {
            boolean isClientPlayer = anchor == FiskHeroes.proxy.getPlayer();
            boolean isFirstPerson = isClientPlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;

            if (anchor.ticksExisted == 0)
            {
                anchor.lastTickPosX = anchor.posX;
                anchor.lastTickPosY = anchor.posY;
                anchor.lastTickPosZ = anchor.posZ;
            }

            double d0 = anchor.lastTickPosX + (anchor.posX - anchor.lastTickPosX) * partialTicks;
            double d1 = anchor.lastTickPosY + (anchor.posY - anchor.lastTickPosY) * partialTicks;
            double d2 = anchor.lastTickPosZ + (anchor.posZ - anchor.lastTickPosZ) * partialTicks;
            x += d0 - entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            y += d1 - entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            z += d2 - entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            EffectRenderHandler.get(anchor).forEach(e -> e.effect.doRender(e, anchor, isClientPlayer, isFirstPerson, partialTicks));
            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        doRender((EffectEntity) entity, x, y, z, entityYaw, partialTicks);
    }
}
