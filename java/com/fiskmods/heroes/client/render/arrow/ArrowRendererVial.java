package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.item.ItemTrickArrow;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;

public class ArrowRendererVial extends ArrowRenderer
{
    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);
        ItemStack potion = ItemTrickArrow.getItem(arrow.getArrowItem());

        if (potion != null && !inQuiver)
        {
            float scale = 0.3F;
            potion = potion.copy();
            potion.setItemDamage(potion.getItemDamage() & ~16384 | 8192);

            GL11.glPushMatrix();
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(180, 0, 0, 1);
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glTranslatef(0.58F, -2.3F, 0.035F);
            GL11.glRotatef(-335, 0, 0, 1);
            GL11.glRotatef(-50, 0, 1, 0);

            int damage = PotionHelper.func_77915_a(potion.getItemDamage(), false);
            float r = (damage >> 16 & 255) / 255.0F;
            float g = (damage >> 8 & 255) / 255.0F;
            float b = (damage & 255) / 255.0F;

            GL11.glColor3f(r, g, b);
            itemRenderer.renderItem(mc.thePlayer, potion, 0);
            GL11.glColor3f(1, 1, 1);
            itemRenderer.renderItem(mc.thePlayer, potion, 1);
            GL11.glPopMatrix();
        }
    }
}
