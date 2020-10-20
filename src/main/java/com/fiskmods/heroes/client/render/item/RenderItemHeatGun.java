package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelHeatGun;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemHeatGun implements IItemRenderer
{
    INSTANCE;

    private static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/heat_gun.png");
    private static final ResourceLocation TEXTURE_LIGHTS = new ResourceLocation(FiskHeroes.MODID, "textures/models/heat_gun_lights.png");
    private static final ModelHeatGun MODEL = new ModelHeatGun();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        EntityPlayer player = null;

        if (data.length > 1 && data[1] instanceof EntityPlayer)
        {
            player = (EntityPlayer) data[1];
        }

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            float f = FiskMath.curve(SHData.AIMING_TIMER.interpolate(player));
            GL11.glPushMatrix();
            GL11.glTranslatef(1.1F - f * 0.3F, 0.3F + f * 0.2F, 0.1F - f * 0.25F);
            GL11.glRotatef(-10 + f * 5, 1, 0, 0);
            GL11.glRotatef(200 + f * 10, 0, 0, 1);
            GL11.glRotatef(115 - f * 20, 0, 1, 0);

            float scale = 1.3F;
            GL11.glScalef(scale, scale, scale);
            render();
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.425F, 0.175F, 0.05F);
            GL11.glRotatef(11, 0, 1, 0);
            GL11.glRotatef(15, 0, 0, 1);
            GL11.glRotatef(-170, 1, 0, 0);

            float scale = 1.2F;
            GL11.glScalef(scale, scale, scale);
            render();
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -0.1F, 0);
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glRotatef(90, 0, 1, 0);

            float scale = 1.4F;
            GL11.glScalef(scale, scale, scale);
            render();
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.ENTITY)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(180, 1, 0, 0);
            render();
            GL11.glPopMatrix();
        }
    }

    public static void render()
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        MODEL.render();
        GL11.glDisable(GL11.GL_LIGHTING);
        SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_LIGHTS);
        MODEL.render();
        SHRenderHelper.resetLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
