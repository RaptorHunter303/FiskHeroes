package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.render.entity.RenderFireBlast;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArrowRendererFireball extends ArrowRendererItemBall
{
    public ArrowRendererFireball()
    {
        super(new ItemStack(Items.fire_charge));
    }

    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);

        if (!inQuiver)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -0.65F, 0);
            RenderFireBlast.render(0.2F, arrow.ticksExisted + partialTicks);
            GL11.glPopMatrix();
        }
    }
}
