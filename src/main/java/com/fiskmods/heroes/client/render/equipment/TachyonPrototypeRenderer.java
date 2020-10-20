package com.fiskmods.heroes.client.render.equipment;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.render.item.RenderItemTachyonPrototype;
import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public enum TachyonPrototypeRenderer implements EquipmentRenderer
{
    INSTANCE;

    private final ResourceLocation glow = new ResourceLocation(FiskHeroes.MODID, "textures/models/tachyon_device_glow.png");

    @Override
    public boolean test(EntityPlayer player)
    {
        if (player instanceof EntityDisplayMannequin)
        {
            TileEntityDisplayStand tile = ((EntityDisplayMannequin) player).displayStand;
            ItemStack stack = tile.getStackInSlot(4);

            return stack != null && stack.getItem() == ModItems.tachyonPrototype;
        }

        return false;
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
        RenderItemTachyonPrototype.render();

        Tessellator tessellator = Tessellator.instance;
        float size = 0.1F;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-size, size, 0, 0, 1);
        tessellator.addVertexWithUV(size, size, 0, 1, 1);
        tessellator.addVertexWithUV(size, -size, 0, 0, 1);
        tessellator.addVertexWithUV(-size, -size, 0, 0, 0);

        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        GL11.glDisable(GL11.GL_LIGHTING);
        SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
        GL11.glColor4f(1, 0.8F, 0.2F, 1);
        mc.getTextureManager().bindTexture(glow);
        tessellator.draw();
        SHRenderHelper.resetLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }
}
