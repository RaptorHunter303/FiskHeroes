package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelGrapplingGun;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemGrapplingGun implements IItemRenderer
{
    INSTANCE;

    private static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/grappling_gun.png");
    private static final ModelGrapplingGun MODEL = new ModelGrapplingGun();

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
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.9F, 0.4F, 0.25F);
            GL11.glRotatef(70, 0, 1, 0);
            GL11.glRotatef(-30, 1, 0, 0);
            GL11.glRotatef(0, 0, 0, 1);

            float scale = 1.5F;
            GL11.glScalef(scale, -scale, -scale);
            render();
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.425F, 0.125F, 0.0F);
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glRotatef(-12, 0, 1, 0);
            GL11.glRotatef(-13, 0, 0, 1);

            float scale = 1.5F;
            GL11.glScalef(scale, scale, scale);
            render();
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -0.2F, -0.2F);
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glRotatef(90, 0, 1, 0);

            float scale = 1.7F;
            GL11.glScalef(scale, scale, scale);
            render();
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.ENTITY)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, -0.1F);
            GL11.glRotatef(180, 1, 0, 0);
            render();
            GL11.glPopMatrix();
        }
    }

    public static void render()
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        MODEL.render();
    }
}
