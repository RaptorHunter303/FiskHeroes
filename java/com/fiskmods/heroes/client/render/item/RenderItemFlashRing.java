package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelFlashRing;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.item.ItemFlashRing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemFlashRing implements IItemRenderer
{
    INSTANCE;

    private static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/the_flash_ring.png");
    private static final ModelFlashRing MODEL = new ModelFlashRing();

    private static final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack itemstack, Object... data)
    {
        HeroIteration iter = ItemFlashRing.getContainedHero(itemstack);
        float scale;

        GL11.glPushMatrix();
        MODEL.lightningBolt1.showModel = iter == null;

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glTranslatef(0.7F, 0.4F, 0.2F);
            GL11.glRotatef(-5, 1, 0, 0);
            GL11.glRotatef(210, 0, 0, 1);
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glRotatef(70 * MathHelper.sin(Math.max(mc.thePlayer.rotationPitch / 90, 0)), -1, 1, 1);

            scale = 0.75F;
            GL11.glScalef(scale, scale, scale);
            render();
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glRotatef(15, 1, 0, 0);
            GL11.glRotatef(-79, 0, 0, 1);
            GL11.glRotatef(-105, 1, 0, 0);
            GL11.glTranslatef(0.2125F, -0.185F, 0.325F);

            scale = 0.4F;
            GL11.glScalef(scale, scale, scale);
            render();
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glRotatef(45, 0, 1, 0);
            GL11.glRotatef(40, 1, 0, 0);
            GL11.glRotatef(180, 0, 1, 0);
            GL11.glTranslatef(0, 0.4F, 0);

            scale = 2.75F;
            GL11.glScalef(scale, -scale, -scale);
            render();
        }
        else if (type == ItemRenderType.ENTITY)
        {
            GL11.glRotatef(-90, 0, 1, 0);
            GL11.glRotatef(90, 1, 0, 0);
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glTranslatef(0, 0.2F, 0);

            scale = 1;
            GL11.glScalef(scale, -scale, -scale);
            render();
        }

        if (iter != null)
        {
            GL11.glRotatef(-90, 1, 0, 0);
            GL11.glTranslatef(0, 0, -0.0625F);
            ItemStack item = null;

            if (iter.hero.hasPieceOfSet(1))
            {
                item = iter.createChestplate();
            }
            else
            {
                item = iter.createArmor(iter.hero.getFirstPieceOfSet());
            }

            if (item != null)
            {
                if (type == ItemRenderType.INVENTORY)
                {
                    scale = 0.75F / 64;
                    GL11.glScalef(-scale, -scale, scale);
                    RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), item, -8, -8);
                }
                else
                {
                    IIcon icon = item.getIconIndex();
                    ResourceLocation location = mc.getTextureManager().getResourceLocation(item.getItemSpriteNumber());
                    mc.getTextureManager().bindTexture(location);

                    if (icon == null)
                    {
                        icon = ((TextureMap) mc.getTextureManager().getTexture(location)).getAtlasSprite("missingno");
                    }

                    scale = 0.75F / 4;
                    GL11.glColor4f(1, 1, 1, 1);
                    GL11.glScalef(scale, scale, scale);
                    GL11.glTranslatef(-0.5F, -0.5F, 0);
                    ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
                }
            }
        }

        GL11.glPopMatrix();
    }

    public static void render()
    {
        mc.getTextureManager().bindTexture(TEXTURE);
        MODEL.render();
    }
}
