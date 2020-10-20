package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.item.ItemFlashRing;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.Constants.NBT;

public enum RenderItemMiniAtomSuit implements IItemRenderer
{
    INSTANCE;

    public static EntityClientPlayerMP fakePlayer;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return type == ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.7F, 0.4F, 0.2F);
            GL11.glRotatef(-5, 1, 0, 0);
            GL11.glRotatef(210, 0, 0, 1);
            GL11.glRotatef(-90, 0, 1, 0);

            float scale = 0.3F;
            GL11.glScalef(scale, scale, scale);
            render(item);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(192, 0, 0, 1);
            GL11.glRotatef(-11, 0, 1, 0);
            GL11.glRotatef(-20, 1, 0, 0);
            GL11.glTranslatef(-0.45F, -0.15F, 0.025F);

            float scale = 0.2F;
            GL11.glScalef(scale, scale, scale);
            render(item);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glRotatef(180, 0, 0, 1);
            GL11.glTranslatef(-8, 8, 0);

            float scale = 8.0F * 0.9375F;
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0, -8 * 0.0625F, 0);

            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            render(item);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.ENTITY)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glTranslatef(0, 0.1F, 0);

            float scale = 0.2F;
            GL11.glScalef(scale, -scale, -scale);
            render(item);
            GL11.glPopMatrix();
        }
    }

    public static void render(ItemStack itemstack)
    {
        if (itemstack.hasTagCompound())
        {
            ItemStack[] armorFromNBT = null;

            if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Suit", NBT.TAG_STRING))
            {
                HeroIteration iter = ItemFlashRing.getContainedHero(itemstack);

                if (iter != null)
                {
                    armorFromNBT = iter.createArmorStacks();
                }
            }
            else
            {
                armorFromNBT = ItemFlashRing.getArmorFromNBT(itemstack);
            }

            if (armorFromNBT != null)
            {
                for (int i = 0; i < armorFromNBT.length; ++i)
                {
                    fakePlayer.inventory.armorInventory[i] = armorFromNBT[3 - i];
                }

                GL11.glEnable(GL11.GL_COLOR_MATERIAL);
                GL11.glPushMatrix();
                GL11.glTranslatef(0, 1.4F, 0);
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glRotatef(180, 0, 1, 0);
                RenderManager.instance.playerViewY = 180;
                RenderManager.instance.renderEntityWithPosYaw(fakePlayer, 0, 0, 0, 0, 1);
                GL11.glPopMatrix();
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            }
        }
    }
}
