package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.client.model.item.ModelBoStaff;
import com.fiskmods.heroes.common.event.ClientEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemBoStaff implements IItemRenderer
{
    INSTANCE;

    private static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/bo_staff.png");
    private static final ModelBoStaff MODEL = new ModelBoStaff();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return type == ItemRenderType.EQUIPPED || type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        EntityLivingBase entity = null;
        boolean split = false;
        float f = 1;

        if (data.length > 1 && data[1] instanceof EntityLivingBase)
        {
            entity = (EntityLivingBase) data[1];
        }

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.9F, 0.4F, 0.25F);
            GL11.glRotatef(70, 0, 1, 0);

            if (entity != null)
            {
                float swing = entity.getSwingProgress(ClientEventHandler.renderTick);
                float f1 = MathHelper.sin((float) (swing * Math.PI));

                if (swing > 0)
                {
                    GL11.glRotatef(-90 * f1, 0, 0, 1);
                }
            }

            float scale = 1.25F;
            GL11.glScalef(scale, -scale, -scale);
            render(item, split, f);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(45, 0, 1, 0);
            GL11.glRotatef(-70, 1, 0, 0);
            GL11.glTranslatef(0, -0.9F, 0.9F);

            if (entity != null)
            {
                float swing = entity.getSwingProgress(ClientEventHandler.renderTick);
                float f1 = MathHelper.sin((float) (swing * Math.PI));

                if (swing > 0)
                {
                    GL11.glRotatef(-20 * f1, 1, 0, 0);
                    GL11.glRotatef(40 * f1, 0, 0, 1);
                }
            }

            float scale = 2.25F;
            GL11.glScalef(scale, scale, scale);
            render(item, split, f);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(-45, 0, 1, 0);
            GL11.glRotatef(-40, 1, 0, 0);
            GL11.glRotatef(-25, 0, 1, 0);

            float scale = 1.8F;
            GL11.glScalef(scale, -scale, -scale);
            render(item, split, 0);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.ENTITY)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, 0);
            GL11.glRotatef(90, 1, 0, 0);
            render(item, split, f);
            GL11.glPopMatrix();
        }
    }

    public static void render(ItemStack item, boolean split, float f)
    {
        render(item != null && item.hasEffect(0), split, f);
    }

    public static void render(boolean enchanted, boolean split, float f)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        Runnable r = () ->
        {
            MODEL.render(f);

            if (!split)
            {
                GL11.glPushMatrix();
                GL11.glRotatef(180, 1, 0, 0);
                MODEL.render(f);
                GL11.glPopMatrix();
            }
        };

        r.run();

        if (enchanted)
        {
            SHRenderHooks.renderEnchanted(r);
        }
    }
}
