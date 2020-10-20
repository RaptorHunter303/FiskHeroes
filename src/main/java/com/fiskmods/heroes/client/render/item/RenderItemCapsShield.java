package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.client.model.item.ModelCapsShield;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.event.ClientEventHandler;
import com.fiskmods.heroes.common.item.ItemCapsShield;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemCapsShield implements IItemRenderer
{
    INSTANCE;

    private static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/shield_cap.png");
    private static final ResourceLocation TEXTURE_STEALTH = new ResourceLocation(FiskHeroes.MODID, "textures/models/shield_cap_stealth.png");
    private static final ModelCapsShield MODEL = new ModelCapsShield();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return type == ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (data.length > 1 && data[1] instanceof EntityPlayer)
        {
            player = (EntityPlayer) data[1];
        }

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.6F, 0.45F, 0.2F);
            GL11.glRotatef(130, 0, 0, 1);
            GL11.glRotatef(-50, 1, 0, 0);
            GL11.glTranslatef(-0.1F, 0.5F, -0.1F);

            float f = player.getSwingProgress(ClientEventHandler.renderTick);
            f = MathHelper.sin((float) (f * Math.PI));

            GL11.glRotatef(10 * f, 0, 0, 1);
            GL11.glTranslatef(0.2F * f, 0.2F * f, 0.6F * f);
            GL11.glRotatef(-30 * f, 1, 0, 0);
            GL11.glRotatef(50 * f, 0, 0, 1);

            if (player.isUsingItem())
            {
                GL11.glRotatef(-40, 1, 0, 0);
                GL11.glRotatef(-90, 0, 1, 0);
                GL11.glRotatef(50, 0, 0, 1);
                GL11.glRotatef(15, 0, 1, 0);
                GL11.glTranslatef(0, 0.3F, -0.1F);
            }

            GL11.glRotatef(90, 0, 1, 0);
            render(item, false);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glPushMatrix();

            if (player.isUsingItem())
            {
                GL11.glRotatef(-95, 1, 0, 0);
                GL11.glRotatef(-15, 0, 1, 0);
                GL11.glRotatef(-40, 0, 0, 1);
                GL11.glTranslatef(0.275F, 0.225F, -0.1F);
            }
            else
            {
                GL11.glRotatef(12, 0, 1, 0);
                GL11.glRotatef(-75, 0, 0, 1);
                GL11.glRotatef(-77.5F, 0, 1, 0);
                GL11.glRotatef(8, 0, 0, 1);
                GL11.glTranslatef(-0.175F, 0.25F, -0.1625F);
            }

            float scale = 1.825F;
            GL11.glScalef(scale, scale, scale);
            render(item, !player.isUsingItem());
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(-2, 3, 0);
            GL11.glScalef(10, 10, 10);
            GL11.glTranslatef(1, 0.5F, 1);
            GL11.glScalef(1, 1, -1);
            GL11.glRotatef(210, 1, 0, 0);
            GL11.glRotatef(45, 0, 1, 0);
            GL11.glRotatef(-90, 0, 1, 0);
            GL11.glRotatef(45, 0, 1, 0);
            GL11.glRotatef(60, 1, 0, 0);

            float scale = 1.7F;
            GL11.glScalef(scale, -scale, -scale);
            render(item, false);
            GL11.glPopMatrix();

            double height = Cooldown.SHIELD_THROW.getProgress(player, 16);

            if (height > 0)
            {
                Tessellator tessellator = Tessellator.instance;
                float zLevel = RenderItem.getInstance().zLevel + 300;

                tessellator.startDrawingQuads();
                tessellator.addVertex(0, 16, zLevel);
                tessellator.addVertex(16, 16, zLevel);
                tessellator.addVertex(16, 16 - height, zLevel);
                tessellator.addVertex(0, 16 - height, zLevel);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(1, 1, 1, 0.6F);
                tessellator.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }
        else if (type == ItemRenderType.ENTITY)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(90, 1, 0, 0);
            GL11.glRotatef(-90, 0, 0, 1);
            GL11.glTranslatef(0, 0, -0.03F);

            GL11.glScalef(1, -1, -1);
            render(item, false);
            GL11.glPopMatrix();
        }
    }

    public static void render(ItemStack item, boolean straps)
    {
        render(straps, item != null && item.hasEffect(0), ItemCapsShield.isStealth(item));
    }

    public static void render(boolean straps, boolean enchanted, boolean stealth)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(stealth ? TEXTURE_STEALTH : TEXTURE);
        MODEL.render(straps);

        if (enchanted)
        {
            SHRenderHooks.renderEnchanted(() -> MODEL.render(straps));
        }
    }
}
