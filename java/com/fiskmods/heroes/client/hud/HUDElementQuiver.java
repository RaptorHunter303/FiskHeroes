package com.fiskmods.heroes.client.hud;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.container.InventoryQuiver;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.QuiverHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class HUDElementQuiver extends HUDElement
{
    private final RenderItem renderItem = RenderItem.getInstance();

    private int highlight;
    private ItemStack hightlightStack;

    private InventoryQuiver quiver;

    public HUDElementQuiver(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public boolean isVisible(ElementType type)
    {
        return type == ElementType.BOSSHEALTH && QuiverHelper.getEquippedQuiver(mc.thePlayer) != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() == ModItems.compoundBow;
    }

    @Override
    public void postRender(ElementType type, ScreenInfo screen, int mouseX, int mouseY, float partialTicks)
    {
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_BLEND);
        mc.getTextureManager().bindTexture(WIDGETS);
        drawTexturedModalRect(x, y, 0, 0, width, height);
        drawTexturedModalRect(x - 1, y - 1 + SHData.SELECTED_ARROW.get(mc.thePlayer) * 20, 22, 0, 24, 24);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        for (int slot = 0; slot < 5; ++slot)
        {
            ItemStack itemstack = quiver.getStackInSlot(slot);

            if (itemstack != null)
            {
                renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x + 3, y + 3 + 20 * slot);
                renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x + 3, y + 3 + 20 * slot);
            }
        }

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        if (mc.gameSettings.heldItemTooltips && highlight > 0 && hightlightStack != null)
        {
            String name = hightlightStack.getDisplayName();
            int opacity = highlight * 256 / 10;

            if (opacity > 255)
            {
                opacity = 255;
            }

            if (opacity > 0)
            {
                FontRenderer font = hightlightStack.getItem().getFontRenderer(hightlightStack);
                font = font == null ? mc.fontRenderer : font;

                if (font != null)
                {
                    int x1 = x;

                    if (SHConfig.quiverHotbarAlignLeft)
                    {
                        x1 += width + 5;
                    }
                    else
                    {
                        x1 -= 5 + font.getStringWidth(name);
                    }

                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                    font.drawStringWithShadow(name, x1, y + 6 + SHData.SELECTED_ARROW.get(mc.thePlayer) * 20, 0xFFFFFF | opacity << 24);
                    GL11.glDisable(GL11.GL_BLEND);
                }
            }
        }
    }

    @Override
    public void updateTick()
    {
        if (!mc.isGamePaused() && QuiverHelper.getEquippedQuiver(mc.thePlayer) != null)
        {
            quiver = QuiverHelper.getQuiverInventory(mc.thePlayer);
            ItemStack itemstack = quiver.getCurrentArrow();

            if (itemstack == null)
            {
                highlight = 0;
            }
            else if (hightlightStack != null && itemstack.getItem() == hightlightStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, hightlightStack) && (itemstack.isItemStackDamageable() || itemstack.getItemDamage() == hightlightStack.getItemDamage()))
            {
                if (highlight > 0)
                {
                    --highlight;
                }
            }
            else
            {
                highlight = 40;
            }

            hightlightStack = itemstack;
        }
    }
}
