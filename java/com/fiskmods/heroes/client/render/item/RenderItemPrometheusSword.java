package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.client.model.item.ModelKatana;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemPrometheusSword implements IItemRenderer
{
    INSTANCE;

    private static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/prometheus_sword.png");
    private static final ModelKatana MODEL = new ModelKatana();

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
            GL11.glPushMatrix();
            GL11.glRotatef(85, 0, 1, 0);
            GL11.glRotatef(150, 1, 0, 0);
            GL11.glRotatef(-2, 0, 0, 1);
            GL11.glTranslatef(0.05F, 0.2F, -0.725F);
            GL11.glRotatef(-5, 1, 0, 0);

            if (player != null && player.isUsingItem())
            {
                GL11.glTranslatef(0.3F, 0.1F, 0);
            }

            float scale = 0.55F;
            GL11.glScalef(scale, scale, scale);
            render(item);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(85, 0, 1, 0);
            GL11.glRotatef(150, 1, 0, 0);
            GL11.glRotatef(-2, 0, 0, 1);
            GL11.glTranslatef(0.05F, 0.2F, -0.725F);

            if (player != null && player.isUsingItem())
            {
                GL11.glTranslatef(0.05F, 0.1F, 0);
            }

            float scale = 0.6F;
            GL11.glScalef(scale, scale, scale);
            render(item);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(-45, 0, 1, 0);
            GL11.glRotatef(-40, 1, 0, 0);
            GL11.glRotatef(-25, 0, 1, 0);
            GL11.glTranslatef(0, -0.9F, 0);

            float scale = 0.8F;
            GL11.glScalef(scale, -scale, -scale);
            render(item);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.ENTITY)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(90, 0, 0, 1);
            GL11.glTranslatef(0, -0.85F, 0);

            float scale = 0.7F;
            GL11.glScalef(scale, -scale, -scale);
            render(item);
            GL11.glPopMatrix();
        }
    }

    public static void render(ItemStack item)
    {
        render(item != null && item.hasEffect(0));
    }

    public static void render(boolean enchanted)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        MODEL.guard.isHidden = true;
        MODEL.render();

        if (enchanted)
        {
            SHRenderHooks.renderEnchanted(MODEL::render);
        }
    }
}
