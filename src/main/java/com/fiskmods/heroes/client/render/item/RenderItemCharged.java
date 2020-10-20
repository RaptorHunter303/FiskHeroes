package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.item.ITachyonCharged;
import com.fiskmods.heroes.common.item.ItemSubatomicBattery;
import com.fiskmods.heroes.common.item.ItemTachyonDevice;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class RenderItemCharged implements IItemRenderer
{
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final RenderItem renderItem = RenderItem.getInstance();

    private final IItemRenderer wrapped;
    private final ChargeType chargeType;

    private RenderItemCharged(IItemRenderer renderer, ChargeType type)
    {
        wrapped = renderer;
        chargeType = type;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return wrapped != null && wrapped.handleRenderType(item, type) || type == ItemRenderType.INVENTORY && chargeType.handleRenderType(item);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return wrapped != null && wrapped.shouldUseRenderHelper(type, item, helper) && helper != ItemRendererHelper.INVENTORY_BLOCK;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        if (wrapped != null)
        {
            GL11.glPushMatrix();

            if (type == ItemRenderType.INVENTORY && wrapped.shouldUseRenderHelper(type, item, ItemRendererHelper.INVENTORY_BLOCK))
            {
                GL11.glTranslatef(-2, 3, 0);
                GL11.glScalef(10, 10, 10);
                GL11.glTranslatef(1, 0.5F, 1);
                GL11.glScalef(1, 1, -1);
                GL11.glRotatef(210, 1, 0, 0);
                GL11.glRotatef(45, 0, 1, 0);
                GL11.glRotatef(-90, 0, 1, 0);
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            wrapped.renderItem(type, item, data);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
        else
        {
            renderItem.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), item, 0, 0, true);
        }

        if (type == ItemRenderType.INVENTORY && chargeType.handleRenderType(item))
        {
            float filled = chargeType.getChargeAmount(item);
            int y = 13;

            if (item.getItem().showDurabilityBar(item))
            {
                y -= 2;
            }

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            Tessellator tessellator = Tessellator.instance;
            tessellator.addTranslation(0, 0, renderItem.zLevel + 300);
            renderQuad(tessellator, 2, y, 13, 2, 0);
            renderQuad(tessellator, 2, y, 12, 1, chargeType.colors[0]);
            renderQuad(tessellator, 2, y, filled * 13, 1, chargeType.colors[1], chargeType.colors[2]);
            tessellator.addTranslation(0, 0, -(renderItem.zLevel + 300));
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
    }

    private void renderQuad(Tessellator tessellator, float x, float y, float width, float height, int color1, int color2)
    {
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(color1);
        tessellator.addVertex(x, y, 0);
        tessellator.addVertex(x, y + height, 0);
        tessellator.setColorOpaque_I(color2);
        tessellator.addVertex(x + width, y + height, 0);
        tessellator.addVertex(x + width, y, 0);
        tessellator.draw();
    }

    private void renderQuad(Tessellator tessellator, float x, float y, float width, float height, int color)
    {
        renderQuad(tessellator, x, y, width, height, color, color);
    }

    public enum ChargeType
    {
        TACHYON(0x383800, 0x515100, 0xFFFF00)
        {
            @Override
            public boolean handleRenderType(ItemStack item)
            {
                return item.getItem() instanceof ITachyonCharged && ((ITachyonCharged) item.getItem()).renderTachyonBar(item);
            }

            @Override
            public float getChargeAmount(ItemStack item)
            {
                return (float) ItemTachyonDevice.getCharge(item) / ItemTachyonDevice.getMaxCharge(item);
            }
        },
        SUBATOMIC(0x002660, 0x0000AA, 0x00BBFF)
        {
            @Override
            public boolean handleRenderType(ItemStack item)
            {
                return item.getItem() instanceof ItemSubatomicBattery;
            }

            @Override
            public float getChargeAmount(ItemStack item)
            {
                return Math.min(ItemSubatomicBattery.getCharge(item), 1);
            }
        };

        private final int[] colors = new int[3];

        private ChargeType(int background, int primary, int secondary)
        {
            colors[0] = background;
            colors[1] = primary;
            colors[2] = secondary;
        }

        public final IItemRenderer wrap(IItemRenderer renderer)
        {
            return new RenderItemCharged(renderer, this);
        }

        public boolean handleRenderType(ItemStack item)
        {
            return false;
        }

        public float getChargeAmount(ItemStack item)
        {
            return 0;
        }
    }
}
