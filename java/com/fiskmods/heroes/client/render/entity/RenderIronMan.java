package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderIronMan extends RenderBiped
{
    public RenderIronMan()
    {
        super(new ModelBiped(), 0.5F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTicks)
    {
        float f1 = 0.9375F;
        GL11.glScalef(f1, f1, f1);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return new ResourceLocation(FiskHeroes.MODID, "textures/null.png");
    }
}
