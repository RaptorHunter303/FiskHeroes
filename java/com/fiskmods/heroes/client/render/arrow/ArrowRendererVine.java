package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.client.model.ModelLeashKnot;
import net.minecraft.util.ResourceLocation;

public class ArrowRendererVine extends ArrowRendererGrapplingHook
{
    private ResourceLocation textureKnot = new ResourceLocation(FiskHeroes.MODID, "textures/models/vine_knot.png");
    private ModelLeashKnot modelKnot = new ModelLeashKnot();

    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, true);
        mc.getTextureManager().bindTexture(textureKnot);

        float f = 0.25F;
        GL11.glScalef(f, f, f);
        GL11.glTranslatef(0, -1, 0);

        for (int i = 0; i < 2; ++i)
        {
            GL11.glTranslatef(0, -0.5F, 0);
            modelKnot.render(null, 0, 0, 0, 0, 0, 0.0625F);
        }
    }

    @Override
    public void preRender(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        renderCable(arrow, x, y, z, partialTicks, 0x364D23, 0x293C1B);
    }
}
