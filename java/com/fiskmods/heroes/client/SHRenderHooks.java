package com.fiskmods.heroes.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.common.IOffhandRender;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.event.ClientEventHandler;
import com.fiskmods.heroes.common.item.IDualItem;
import com.fiskmods.heroes.util.SHClientUtils;
import com.fiskmods.heroes.util.SHEnumHelper;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.TextureHelper;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.client.MinecraftForgeClient;

public class SHRenderHooks extends SHRenderHelper
{
    public static void renderFire(float scale, float scaleY)
    {
        GL11.glPushMatrix();
        SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
        float x = -0.5F;
        float y = 0;
        float z = -0.5F;

        Tessellator tessellator = Tessellator.instance;
        IIcon iicon = Blocks.fire.getFireIcon(0);
        IIcon iicon1 = Blocks.fire.getFireIcon(1);
        double d0 = iicon.getMinU();
        double d1 = iicon.getMinV();
        double d2 = iicon.getMaxU();
        double d3 = iicon.getMaxV();
        double d5;
        double d6;
        double d7;
        double d8;
        double d9;
        double d10;
        double d11;

        mc.getTextureMapBlocks();
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        tessellator.startDrawingQuads();
        double d4 = x + 0.5D + 0.2D;
        d5 = x + 0.5D - 0.2D;
        d6 = z + 0.5D + 0.2D;
        d7 = z + 0.5D - 0.2D;
        d8 = x + 0.5D - 0.3D;
        d9 = x + 0.5D + 0.3D;
        d10 = z + 0.5D - 0.3D;
        d11 = z + 0.5D + 0.3D;
        tessellator.addVertexWithUV(d8, y + scaleY, z + 1, d2, d1);
        tessellator.addVertexWithUV(d4, y + 0, z + 1, d2, d3);
        tessellator.addVertexWithUV(d4, y + 0, z + 0, d0, d3);
        tessellator.addVertexWithUV(d8, y + scaleY, z + 0, d0, d1);
        tessellator.addVertexWithUV(d9, y + scaleY, z + 0, d2, d1);
        tessellator.addVertexWithUV(d5, y + 0, z + 0, d2, d3);
        tessellator.addVertexWithUV(d5, y + 0, z + 1, d0, d3);
        tessellator.addVertexWithUV(d9, y + scaleY, z + 1, d0, d1);
        d0 = iicon1.getMinU();
        d1 = iicon1.getMinV();
        d2 = iicon1.getMaxU();
        d3 = iicon1.getMaxV();
        tessellator.addVertexWithUV(x + 1, y + scaleY, d11, d2, d1);
        tessellator.addVertexWithUV(x + 1, y + 0, d7, d2, d3);
        tessellator.addVertexWithUV(x + 0, y + 0, d7, d0, d3);
        tessellator.addVertexWithUV(x + 0, y + scaleY, d11, d0, d1);
        tessellator.addVertexWithUV(x + 0, y + scaleY, d10, d2, d1);
        tessellator.addVertexWithUV(x + 0, y + 0, d6, d2, d3);
        tessellator.addVertexWithUV(x + 1, y + 0, d6, d0, d3);
        tessellator.addVertexWithUV(x + 1, y + scaleY, d10, d0, d1);
        d4 = x + 0.5D - 0.5D;
        d5 = x + 0.5D + 0.5D;
        d6 = z + 0.5D - 0.5D;
        d7 = z + 0.5D + 0.5D;
        d8 = x + 0.5D - 0.4D;
        d9 = x + 0.5D + 0.4D;
        d10 = z + 0.5D - 0.4D;
        d11 = z + 0.5D + 0.4D;
        tessellator.addVertexWithUV(d8, y + scaleY, z + 0, d0, d1);
        tessellator.addVertexWithUV(d4, y + 0, z + 0, d0, d3);
        tessellator.addVertexWithUV(d4, y + 0, z + 1, d2, d3);
        tessellator.addVertexWithUV(d8, y + scaleY, z + 1, d2, d1);
        tessellator.addVertexWithUV(d9, y + scaleY, z + 1, d0, d1);
        tessellator.addVertexWithUV(d5, y + 0, z + 1, d0, d3);
        tessellator.addVertexWithUV(d5, y + 0, z + 0, d2, d3);
        tessellator.addVertexWithUV(d9, y + scaleY, z + 0, d2, d1);
        d0 = iicon.getMinU();
        d1 = iicon.getMinV();
        d2 = iicon.getMaxU();
        d3 = iicon.getMaxV();
        tessellator.addVertexWithUV(x + 0, y + scaleY, d11, d0, d1);
        tessellator.addVertexWithUV(x + 0, y + 0, d7, d0, d3);
        tessellator.addVertexWithUV(x + 1, y + 0, d7, d2, d3);
        tessellator.addVertexWithUV(x + 1, y + scaleY, d11, d2, d1);
        tessellator.addVertexWithUV(x + 1, y + scaleY, d10, d0, d1);
        tessellator.addVertexWithUV(x + 1, y + 0, d6, d0, d3);
        tessellator.addVertexWithUV(x + 0, y + 0, d6, d2, d3);
        tessellator.addVertexWithUV(x + 0, y + scaleY, d10, d2, d1);
        GL11.glRotatef(180, 1, 0, 0);
        GL11.glScalef(scale, scale, scale);
        tessellator.draw();
        GL11.glPopMatrix();
    }

    public static void renderItemInFirstPerson(ItemRenderer itemRenderer, float partialTicks)
    {
        EntityClientPlayerMP player = mc.thePlayer;
        IOffhandRender offhandRender = (IOffhandRender) itemRenderer;
        ItemStack heldItem = player.getHeldItem();

        if (heldItem != null && heldItem.getItem() instanceof IDualItem)
        {
            int brightness = mc.theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ), 0);
            float swingProgress = ((IDualItem) heldItem.getItem()).getSwingProgress(player, partialTicks);
            float progress = interpolate(offhandRender.getEquippedProgressSH(), offhandRender.getPrevEquippedProgressSH());
            float pitch = interpolate(player.renderArmPitch, player.prevRenderArmPitch);
            float yaw = interpolate(player.renderArmYaw, player.prevRenderArmYaw);
            float f;
            float f1;
            float f2;
            float f3;
            float f4;
            float f5;
            float f6;
            float f7;

            GL11.glPushMatrix();
            GL11.glRotatef(interpolate(player.rotationPitch, player.prevRotationPitch), 1, 0, 0);
            GL11.glRotatef(interpolate(player.rotationYaw, player.prevRotationYaw), 0, 1, 0);
            RenderHelper.enableStandardItemLighting();
            GL11.glPopMatrix();
            GL11.glRotatef((player.rotationPitch - pitch) * 0.1F, 1, 0, 0);
            GL11.glRotatef((player.rotationYaw - yaw) * 0.1F, 0, 1, 0);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness % 65536, brightness / 65536);

            if (heldItem != null)
            {
                applyColorFromItemStack(heldItem, 0);
            }

            yaw = 0.8F;

            if (heldItem != null)
            {
                GL11.glPushMatrix();

                if (player.getItemInUseCount() > 0)
                {
                    EnumAction action = heldItem.getItemUseAction();

                    if (action == EnumAction.eat || action == EnumAction.drink)
                    {
                        f1 = player.getItemInUseCount() - partialTicks + 1;
                        f = 1 - f1 / heldItem.getMaxItemUseDuration();
                        f3 = 1 - f;
                        f3 = f3 * f3 * f3;
                        f3 = f3 * f3 * f3;
                        f3 = f3 * f3 * f3;
                        f4 = 1 - f3;
                        GL11.glTranslatef(0, MathHelper.abs(MathHelper.cos(f1 / 4 * (float) Math.PI) * 0.1F) * (f > 0.2D ? 1 : 0), 0);
                        GL11.glTranslatef(f4 * 0.6F, -f4 * 0.5F, 0);
                        GL11.glRotatef(f4 * 90, 0, 1, 0);
                        GL11.glRotatef(f4 * 10, 1, 0, 0);
                        GL11.glRotatef(f4 * 30, 0, 0, 1);
                    }
                }
                else
                {
                    f2 = swingProgress;
                    f1 = MathHelper.sin(f2 * (float) Math.PI);
                    f = MathHelper.sin(MathHelper.sqrt_float(f2) * (float) Math.PI);

                    // Flip the (x direction)
                    GL11.glTranslatef(f * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f2) * (float) Math.PI * 2) * 0.2F, -f1 * 0.2F);
                }

                // Translate x in the opposite direction
                GL11.glTranslatef(-0.7F * yaw, -0.65F * yaw - (1 - progress) * 0.6F, -0.9F * yaw);

                // Rotate y in the opposite direction
                GL11.glRotatef(-45, 0, 1, 0);

                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                f2 = swingProgress;
                f1 = MathHelper.sin(f2 * f2 * (float) Math.PI);
                f = MathHelper.sin(MathHelper.sqrt_float(f2) * (float) Math.PI);

                GL11.glRotatef(-f1 * 20, 0, 1, 0);

                // Rotate z in the opposite direction
                GL11.glRotatef(f * 20, 0, 0, 1);
                GL11.glRotatef(-f * 80, 1, 0, 0);

                // Rotate y back to original position + 45
                GL11.glRotatef(90, 0, 1, 0);

                f3 = 0.4F;
                GL11.glScalef(f3, f3, f3);

                if (player.getItemInUseCount() > 0)
                {
                    EnumAction action = heldItem.getItemUseAction();

                    if (action == EnumAction.block)
                    {
                        GL11.glTranslatef(0, 0.2F, 0);
                        GL11.glRotatef(30, 0, 1, 0);
                        GL11.glRotatef(30, 1, 0, 0);
                        GL11.glRotatef(60, 0, 1, 0);
                    }
                    else if (action == EnumAction.bow)
                    {
                        GL11.glRotatef(-18, 0, 0, 1);
                        GL11.glRotatef(-12, 0, 1, 0);
                        GL11.glRotatef(-8, 1, 0, 0);
                        GL11.glTranslatef(-0.9F, 0.2F, 0);
                        f5 = heldItem.getMaxItemUseDuration() - (player.getItemInUseCount() - partialTicks + 1);
                        f6 = f5 / 20;
                        f6 = (f6 * f6 + f6 * 2) / 3;

                        if (f6 > 1)
                        {
                            f6 = 1;
                        }

                        if (f6 > 0.1F)
                        {
                            GL11.glTranslatef(0, MathHelper.sin((f5 - 0.1F) * 1.3F) * 0.01F * (f6 - 0.1F), 0);
                        }

                        GL11.glTranslatef(0, 0, f6 * 0.1F);
                        GL11.glRotatef(-335, 0, 0, 1);
                        GL11.glRotatef(-50, 0, 1, 0);
                        GL11.glTranslatef(0, 0.5F, 0);
                        f7 = 1 + f6 * 0.2F;
                        GL11.glScalef(1, 1, f7);
                        GL11.glTranslatef(0, -0.5F, 0);
                        GL11.glRotatef(50, 0, 1, 0);
                        GL11.glRotatef(335, 0, 0, 1);
                    }
                }

                if (heldItem.getItem().shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180, 0, 1, 0);
                }

                itemRenderer.renderItem(player, heldItem, 0, SHEnumHelper.EQUIPPED_FIRST_PERSON_OFFHAND);

                if (heldItem.getItem().requiresMultipleRenderPasses())
                {
                    for (int i = 1; i < heldItem.getItem().getRenderPasses(heldItem.getItemDamage()); i++)
                    {
                        applyColorFromItemStack(heldItem, i);
                        itemRenderer.renderItem(player, heldItem, i, SHEnumHelper.EQUIPPED_FIRST_PERSON_OFFHAND);
                    }
                }

                GL11.glPopMatrix();
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
        }
    }

    public static void renderItemIn3rdPerson(EntityPlayer player, ModelBiped model, float partialTicks)
    {
        ItemStack itemstack = player.getHeldItem();

        if (itemstack != null && itemstack.getItem() instanceof IDualItem)
        {
            float f;
            RenderPlayer render = (RenderPlayer) RenderManager.instance.getEntityRenderObject(player);

            if (SHClientUtils.isInanimate(player))
            {
                model.bipedLeftArm.rotationPointY = 2;
            }

            GL11.glPushMatrix();
            model.bipedLeftArm.postRender(0.0625F);
            GL11.glTranslatef(0.0625F, 7 * 0.0625F, 0.0625F);

            if (player.fishEntity != null)
            {
                itemstack = new ItemStack(Items.stick);
            }

            EnumAction enumAction = null;

            if (player.getItemInUseCount() > 0)
            {
                enumAction = itemstack.getItemUseAction();
            }

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, ItemRenderType.EQUIPPED);
            boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(ItemRenderType.EQUIPPED, itemstack, ItemRendererHelper.BLOCK_3D);

            if (itemstack.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType())))
            {
                GL11.glTranslatef(0, 3 * 0.0625F, -5 * 0.0625F);
                f = 0.5F * 0.75F;
                GL11.glRotatef(20, 1, 0, 0);
                GL11.glRotatef(45, 0, 1, 0);
                GL11.glScalef(-f, -f, f);
            }
            else if (itemstack.getItem().isFull3D())
            {
                f = 10 * 0.0625F;

                if (itemstack.getItem().shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180, 0, 0, 1);
                    GL11.glTranslatef(0, -2 * 0.0625F, 0);
                }

                if (player.getItemInUseCount() > 0 && enumAction == EnumAction.block)
                {
                    GL11.glTranslatef(-0.05F, 0, -0.1F);
                    GL11.glRotatef(50, 0, 1, 0);
                    GL11.glRotatef(10, 1, 0, 0);
                    GL11.glRotatef(40, 0, 0, 1);
                }

                GL11.glTranslatef(0, 3 * 0.0625F, 0);
                GL11.glScalef(f, -f, f);
                GL11.glRotatef(-100, 1, 0, 0);
                GL11.glRotatef(45, 0, 1, 0);
            }
            else
            {
                f = 6 * 0.0625F;
                GL11.glTranslatef(4 * 0.0625F, 3 * 0.0625F, -3 * 0.0625F);
                GL11.glScalef(f, f, f);
                GL11.glRotatef(60, 0, 0, 1);
                GL11.glRotatef(-90, 1, 0, 0);
                GL11.glRotatef(20, 0, 0, 1);
            }

            renderItemAllPasses(player, itemstack, SHEnumHelper.EQUIPPED_OFFHAND);
            GL11.glPopMatrix();
        }
    }

    public static void renderItemAllPasses(EntityLivingBase entity, ItemStack itemstack, ItemRenderType type)
    {
        if (itemstack.getItem().requiresMultipleRenderPasses())
        {
            for (int i = 0; i < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++i)
            {
                applyColorFromItemStack(itemstack, i);
                RenderManager.instance.itemRenderer.renderItem(entity, itemstack, i, ItemRenderType.EQUIPPED);
            }
        }
        else
        {
            applyColorFromItemStack(itemstack, 0);
            RenderManager.instance.itemRenderer.renderItem(entity, itemstack, 0, SHEnumHelper.EQUIPPED_OFFHAND);
        }
    }

    public static void renderBlock(Block block, IIcon icon, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;

        tessellator.setNormal(0, -1, 0);
        renderer.renderFaceYNeg(block, 0, 0, 0, icon);
        tessellator.setNormal(0, 1, 0);
        renderer.renderFaceYPos(block, 0, 0, 0, icon);
        tessellator.setNormal(0, 0, -1);
        renderer.renderFaceZNeg(block, 0, 0, 0, icon);
        tessellator.setNormal(0, 0, 1);
        renderer.renderFaceZPos(block, 0, 0, 0, icon);
        tessellator.setNormal(-1, 0, 0);
        renderer.renderFaceXNeg(block, 0, 0, 0, icon);
        tessellator.setNormal(1, 0, 0);
        renderer.renderFaceXPos(block, 0, 0, 0, icon);
    }

    public static void renderBlock(Block block, int meta, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;

        tessellator.setNormal(0, -1, 0);
        renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, meta));
        tessellator.setNormal(0, 1, 0);
        renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(1, meta));
        tessellator.setNormal(0, 0, -1);
        renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(2, meta));
        tessellator.setNormal(0, 0, 1);
        renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(3, meta));
        tessellator.setNormal(-1, 0, 0);
        renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(4, meta));
        tessellator.setNormal(1, 0, 0);
        renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(5, meta));
    }

    public static void renderBlockAllFaces(RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon)
    {
        renderer.renderFaceYNeg(block, x, y, z, icon);
        renderer.renderFaceYPos(block, x, y, z, icon);
        renderer.renderFaceZNeg(block, x, y, z, icon);
        renderer.renderFaceZPos(block, x, y, z, icon);
        renderer.renderFaceXNeg(block, x, y, z, icon);
        renderer.renderFaceXPos(block, x, y, z, icon);
    }

    public static void renderBlockAllFaces(RenderBlocks renderer, Block block, int x, int y, int z, int meta)
    {
        renderer.renderFaceYNeg(block, x, y, z, block.getIcon(0, meta));
        renderer.renderFaceYPos(block, x, y, z, block.getIcon(1, meta));
        renderer.renderFaceZNeg(block, x, y, z, block.getIcon(2, meta));
        renderer.renderFaceZPos(block, x, y, z, block.getIcon(3, meta));
        renderer.renderFaceXNeg(block, x, y, z, block.getIcon(4, meta));
        renderer.renderFaceXPos(block, x, y, z, block.getIcon(5, meta));
    }

    public static void renderItemIntoGUI(int x, int y, ItemStack itemstack)
    {
        if (itemstack != null)
        {
            FontRenderer font = itemstack.getItem().getFontRenderer(itemstack);

            if (font == null)
            {
                font = mc.fontRenderer;
            }

            renderItem.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), itemstack, x, y);

            if (itemstack.stackSize > 1)
            {
                renderItem.renderItemOverlayIntoGUI(font, mc.getTextureManager(), itemstack, x, y, itemstack.stackSize + "");
            }
        }
    }

    public static float getSwingProgress(EntityLivingBase entity, float partialTicks)
    {
        float f = SHData.SWING_PROGRESS.get(entity) - SHData.SWING_PROGRESS.getPrev(entity);
        return SHData.SWING_PROGRESS.getPrev(entity) + (f < 0 ? ++f : f) * partialTicks;
    }

    public static void renderEnchanted(Runnable render)
    {
        float ticks = mc.thePlayer.ticksExisted + ClientEventHandler.renderTick;

        mc.getTextureManager().bindTexture(TextureHelper.RES_ITEM_GLINT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(0.5F, 0.5F, 0.5F, 1);
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDepthMask(false);

        for (int i = 0; i < 2; ++i)
        {
            float f = 0.76F;
            float f1 = ticks * (0.001F + i * 0.003F) * 20;
            float f2 = 0.33333334F;

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glColor4f(0.5F * f, 0.25F * f, 0.8F * f, 1);
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            GL11.glScalef(f2, f2, f2);
            GL11.glRotatef(30 - i * 60, 0, 0, 1);
            GL11.glTranslatef(0, f1, 0);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            render.run();
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glDepthMask(true);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }
}
