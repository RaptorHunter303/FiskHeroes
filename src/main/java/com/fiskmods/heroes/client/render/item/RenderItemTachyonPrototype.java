package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelTachyonPrototype;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemTachyonPrototype implements IItemRenderer
{
    INSTANCE;

    private static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/tachyon_device_rf.png");
    private static final ModelTachyonPrototype MODEL = new ModelTachyonPrototype();

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
            GL11.glTranslatef(0.7F, 0.5F, 0.1F);
            GL11.glRotatef(-5, 1, 0, 0);
            GL11.glRotatef(210, 0, 0, 1);
            GL11.glRotatef(90, 0, 1, 0);

            float scale = 1.5F;
            GL11.glScalef(scale, scale, scale);
            render();
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(190, 0, 1, 0);
            GL11.glRotatef(-15, 0, 0, 1);
            GL11.glRotatef(-30, 1, 0, 0);
            GL11.glTranslatef(-0.425F, 0.25F, -0.15F);

            float scale = 1.5F;
            GL11.glScalef(scale, scale, scale);
            render();
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(45, 0, 1, 0);
            GL11.glRotatef(-30, 1, 0, 0);
            GL11.glTranslatef(0.0F, 0.0F, 0.0F);

            float scale = 2.75F;
            GL11.glScalef(scale, -scale, -scale);
            render();
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.ENTITY)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glTranslatef(0.0F, 0.0F, 0.075F);

            float scale = 1.5F;
            GL11.glScalef(scale, -scale, -scale);
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
