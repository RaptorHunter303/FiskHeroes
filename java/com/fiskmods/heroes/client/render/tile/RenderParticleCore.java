package com.fiskmods.heroes.client.render.tile;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.tileentity.TileEntityParticleCore;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderParticleCore extends TileEntitySpecialRenderer
{
    public void render(TileEntityParticleCore tile, double x, double y, double z, float partialTicks)
    {
        int range = tile.radius * 3;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GL11.glScalef(1, -1, -1);

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            ItemStack stack = tile.getStackInSlot(dir.ordinal());

            if (stack != null)
            {
                Minecraft mc = Minecraft.getMinecraft();
                Tessellator tessellator = Tessellator.instance;
                int passes = stack.getItem().requiresMultipleRenderPasses() ? stack.getItem().getRenderPasses(stack.getItemDamage()) : 1;

                mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
                TextureUtil.func_152777_a(false, false, 1.0F);
                GL11.glPushMatrix();
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glScalef(-1, -1, 1);
                GL11.glTranslatef(0, dir.offsetY != 0 ? 0.5F * dir.offsetY : 0, 0);

                if (dir.offsetY != 0)
                {
                    GL11.glRotatef(90 * dir.offsetY, 1, 0, 0);
                }
                else
                {
                    GL11.glRotatef(90 * new int[] {0, 0, 2, 0, 3, 1}[dir.ordinal()], 0, 1, 0);
                }

                GL11.glTranslatef(-0.5F, -0.5F, -(dir.offsetY == 0 ? 0.5F : 0));

                for (int pass = 0; pass < passes; ++pass)
                {
                    IIcon icon = stack.getItem().getIcon(stack, pass);

                    if (icon == null)
                    {
                        icon = ((TextureMap) mc.getTextureManager().getTexture(TextureMap.locationItemsTexture)).getAtlasSprite("missingno");
                    }

                    SHRenderHelper.applyColorFromItemStack(stack, pass);
                    ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
                }

                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                GL11.glPopMatrix();
                TextureUtil.func_147945_b();
            }
        }

        if (range > 0)
        {
            Tessellator tessellator = Tessellator.instance;

            float angleIncr = 1;
            float rot = SHRenderHelper.interpolate(tile.animationTicks, tile.prevAnimationTicks);
            SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

            float prevWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
            GL11.glLineWidth(5);

            for (int i = 0; i < 2; ++i)
            {
                if (i == 1)
                {
                    range /= 8;
                }

                for (int j = 0; j < 3; ++j)
                {
                    GL11.glPushMatrix();
                    tessellator.startDrawing(3);

                    if (tile.getBlockType() == ModBlocks.tachyonicParticleCore)
                    {
                        tessellator.setColorRGBA(255, 216, 76, 200);
                    }
                    else
                    {
                        float f = Math.max(-tile.gravity + tile.getType().gravity, 1);
                        tessellator.setColorRGBA(Math.round(f * 54), Math.round(f * 84), Math.round(f * 181), Math.round(Math.abs(tile.gravity) * 200));
                    }

                    for (int k = 0; k <= 360F / angleIncr; ++k)
                    {
                        Vec3 vec3 = Vec3.createVectorHelper(0, range, 0);
                        float pitch = k * angleIncr + rot + tile.zCoord * 100 % 1000;
                        float yaw = j * 120 + rot + tile.zCoord * 100 % 1000;
                        float roll = j * 120 + rot + tile.zCoord * 100 % 1000;
                        vec3.rotateAroundX(-pitch * (float) Math.PI / 180.0F);
                        vec3.rotateAroundY(-yaw * (float) Math.PI / 180.0F);
                        vec3.rotateAroundZ(-roll * (float) Math.PI / 180.0F);
                        tessellator.addVertex(vec3.xCoord, vec3.yCoord, vec3.zCoord);
                    }

                    tessellator.draw();
                    GL11.glPopMatrix();
                }
            }

            GL11.glLineWidth(prevWidth);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            SHRenderHelper.resetLighting();
        }

        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks)
    {
        render((TileEntityParticleCore) tile, x, y, z, partialTicks);
    }
}
