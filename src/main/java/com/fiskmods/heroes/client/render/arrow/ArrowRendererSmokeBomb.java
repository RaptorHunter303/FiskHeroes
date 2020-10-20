package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelSmokePellet;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.util.ResourceLocation;

public class ArrowRendererSmokeBomb extends ArrowRenderer
{
    private ResourceLocation texturePellet = new ResourceLocation(FiskHeroes.MODID, "textures/models/smoke_pellet.png");
    private ModelSmokePellet modelPellet = new ModelSmokePellet();

    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, true);

        float f = 0.25F;
        GL11.glScalef(f, f, f);
        GL11.glTranslatef(0, -0.8F, 0);
        GL11.glRotatef(90, 0, 0, 1);
        GL11.glRotatef(90, 1, 0, 0);

        mc.getTextureManager().bindTexture(texturePellet);
        modelPellet.render();
    }
}
