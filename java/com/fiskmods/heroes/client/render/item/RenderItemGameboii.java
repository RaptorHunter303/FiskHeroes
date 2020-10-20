package com.fiskmods.heroes.client.render.item;

import java.util.Locale;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.gui.GuiGameboii;
import com.fiskmods.heroes.client.model.item.ModelGameboii;
import com.fiskmods.heroes.common.event.ClientEventHandler;
import com.fiskmods.heroes.gameboii.GameboiiColor;
import com.fiskmods.heroes.gameboii.IGameboiiGame;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemGameboii implements IItemRenderer
{
    INSTANCE;

    private final ResourceLocation[] texture;
    private final ModelGameboii model = new ModelGameboii();

    private RenderItemGameboii()
    {
        texture = new ResourceLocation[GameboiiColor.values().length];

        for (int i = 0; i < texture.length; ++i)
        {
            texture[i] = new ResourceLocation(FiskHeroes.MODID, "textures/models/gameboii/gameboii_" + GameboiiColor.get(i).name().toLowerCase(Locale.ROOT) + ".png");
        }
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return type == ItemRenderType.EQUIPPED || type == ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        Minecraft mc = Minecraft.getMinecraft();
        float scale = 0.0625F;

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            RenderPlayer render = (RenderPlayer) RenderManager.instance.getEntityRenderObject(mc.thePlayer);
            float partialTicks = ClientEventHandler.renderTick;

            float pitch = mc.thePlayer.prevRotationPitch + (mc.thePlayer.rotationPitch - mc.thePlayer.prevRotationPitch) * partialTicks;
            float swing = mc.thePlayer.getSwingProgress(partialTicks);
            float f = 1.6F;

            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslatef(-f - 1, 0.8F, f - 1);
            GL11.glRotatef(-45, 0, 1, 0);
            GL11.glScalef(4, 4, 4);
            GL11.glTranslatef(-MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI) * 0.4F, MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI * 2) * 0.2F, -MathHelper.sin(swing * (float) Math.PI) * 0.2F);
            swing = 1 - pitch / 45 + 0.1F;

            if (swing < 0)
            {
                swing = 0;
            }

            if (swing > 1)
            {
                swing = 1;
            }

            swing = -MathHelper.cos(swing * (float) Math.PI) * 0.5F + 0.5F;
            GL11.glTranslatef(0, -swing * 0.5F + 0.04F, -0.72F);
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glRotatef(swing * -85, 0, 0, 1);
            GL11.glTranslatef(-0.225F, 0.3F, 0);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            mc.getTextureManager().bindTexture(mc.thePlayer.getLocationSkin());

            for (int i = 0; i < 2; ++i)
            {
                int j = i * 2 - 1;
                GL11.glPushMatrix();
                GL11.glTranslatef(-0, -0.65F, 1.225F * j);
                GL11.glRotatef(-45 * j, 1, 0, 0);
                GL11.glRotatef(-90, 0, 0, 1);
                GL11.glRotatef(59, 0, 0, 1);
                GL11.glRotatef(-65 * j, 0, 1, 0);
                render.renderFirstPersonArm(mc.thePlayer);
                GL11.glPopMatrix();
            }

            GL11.glRotatef(90, 0, 1, 0);
            GL11.glTranslatef(0, -0.175F, 0.15F);
            scale *= 0.625F;
        }

        GL11.glPushMatrix();

        if (type == ItemRenderType.INVENTORY)
        {
            scale *= -9;
            GL11.glTranslatef(8, 7.5F, 0);
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glRotatef(45, 0, 1, 0);
            GL11.glTranslatef(-0.2F, 0, 1.1F);
            GL11.glRotatef(-70, 1, 0, 0);
            GL11.glRotatef(100, 0, 1, 0);
        }
        else if (type == ItemRenderType.ENTITY)
        {
            scale *= 0.5F;
            GL11.glRotatef(-90, 0, 1, 0);
            GL11.glTranslatef(0, 0.05F, -0.05F);
        }

        GL11.glScalef(-scale, -scale, scale);
        mc.getTextureManager().bindTexture(texture[GameboiiColor.get(item).ordinal()]);
        model.render(1);

        if (type != ItemRenderType.INVENTORY)
        {
            scale = 0.1F;
            GL11.glPushMatrix();
            GL11.glTranslatef(8.75F, 0.65F, -0.86F);
            GL11.glScalef(-scale, -scale, -scale);
            GL11.glRotatef(180, 0, 0, 1);
            mc.fontRenderer.drawString("Z", 0, 0, -1); // Red
            mc.fontRenderer.drawString("X", 15, 15, -1); // Blue
            mc.fontRenderer.drawString("C", 15, -15, -1); // Yellow
            mc.fontRenderer.drawString("V", 30, 0, -1); // Green
            GL11.glPopMatrix();
        }

        if (data.length > 1 && data[1] == mc.thePlayer && mc.currentScreen instanceof GuiGameboii)
        {
            IGameboiiGame game = ((GuiGameboii) mc.currentScreen).game;

            scale = 16;
            model.screen.postRender(1);
            GL11.glScalef(scale, scale, scale);
            GL11.glDisable(GL11.GL_LIGHTING);
            SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
            game.draw(ClientEventHandler.renderTick);
            SHRenderHelper.resetLighting();
            GL11.glEnable(GL11.GL_LIGHTING);
        }

        GL11.glPopMatrix();
    }
}
