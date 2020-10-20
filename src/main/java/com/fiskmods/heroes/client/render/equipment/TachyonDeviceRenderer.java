package com.fiskmods.heroes.client.render.equipment;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.render.item.RenderItemTachyonDevice;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public enum TachyonDeviceRenderer implements EquipmentRenderer
{
    INSTANCE;

    private final ResourceLocation glow = new ResourceLocation(FiskHeroes.MODID, "textures/models/tachyon_device_glow.png");

    @Override
    public boolean test(EntityPlayer player)
    {
        return SpeedsterHelper.hasTachyonDevice(player);
    }

    @Override
    public float[] getOffset(EntityPlayer player, HeroIteration iter, ModelBiped model, float partialTicks)
    {
        return new float[] {0, 0.225F, -0.175F - (SHHelper.getHeroFromArmor(player, 2) != null ? 0.025F : player.getCurrentArmor(2) != null ? 0.05F : 0)};
    }

    @Override
    public void render(EntityPlayer player, HeroIteration iter, ModelBiped model, RenderEquipmentEvent event, float partialTicks)
    {
        model.bipedBody.postRender(0.0625F);
        GL11.glTranslatef(event.xOffset, event.yOffset, event.zOffset);
        RenderItemTachyonDevice.render();

        Tessellator tessellator = Tessellator.instance;
        float charge = SHData.TACHYON_CHARGE.interpolate(player);
        float size = 0.3F * charge / charge;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-size, size, 0, 0, 1);
        tessellator.addVertexWithUV(size, size, 0, 1, 1);
        tessellator.addVertexWithUV(size, -size, 0, 0, 1);
        tessellator.addVertexWithUV(-size, -size, 0, 0, 0);

        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
        GL11.glColor4f(0.2F, 0.4F, 1F, 0.3F * charge);
        mc.getTextureManager().bindTexture(glow);
        tessellator.draw();
        SHRenderHelper.resetLighting();
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }
}
