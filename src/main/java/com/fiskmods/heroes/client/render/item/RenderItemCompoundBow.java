package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.client.model.item.ModelCompoundBow;
import com.fiskmods.heroes.client.render.arrow.ArrowRenderer;
import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.event.ClientEventHandler;
import com.fiskmods.heroes.common.item.ItemCompoundBow;
import com.fiskmods.heroes.util.QuiverHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemCompoundBow implements IItemRenderer
{
    INSTANCE;

    private static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/compound_bow.png");
    private static final ModelCompoundBow MODEL = new ModelCompoundBow();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        EntityPlayer player = null;
        float drawback = 0;
        float horizontal = 0;

        if (data.length > 1 && data[1] instanceof Entity)
        {
            if (data[1] instanceof EntityPlayer)
            {
                player = (EntityPlayer) data[1];

                if (player.isUsingItem())
                {
                    drawback = Math.min((player.getItemInUseDuration() + ClientEventHandler.renderTick) / SHAttributes.BOW_DRAWBACK.get(player, 30), 1);
                }
            }

            horizontal = SHData.HORIZONTAL_BOW_TIMER.interpolate((Entity) data[1]);
        }

        drawback = MathHelper.sin(drawback * 1.5F) / 1.5F;

        float arches = drawback * 1;
        float[] wheel1 = {0, 0.15F, 0.17F};
        float[] end1 = {0, 0.91F - 0.55F * arches, 0.625F + MathHelper.sin(1.075F + arches) - 0.9F};
        float[] wheel2 = {0, -1.63125F, 0.05F};
        float[] end2 = {0, -2.3F + 0.55F * arches, 0.625F + MathHelper.sin(1.075F + arches) - 0.9F};
        float prevLineWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
        GL11.glLineWidth(2);

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.75F, 0.5F, 0.0F);
            GL11.glRotatef(-5, 1, 0, 0);
            GL11.glRotatef(230, 0, 0, 1);
            GL11.glRotatef(100, 0, 1, 0);
            GL11.glRotatef(-5, 1, 0, 0);

            float scale = 0.375F;
            GL11.glScalef(scale, scale, scale);

            if (horizontal > 0)
            {
                GL11.glTranslatef(-0.2F * horizontal, -0.4F * horizontal, -0.1F * horizontal);
                GL11.glRotatef(70 * horizontal, 0, 0, 1);
                GL11.glRotatef(-10 * horizontal, 0, 1, 0);

                if (player.isUsingItem())
                {
                    GL11.glTranslatef(0.2F * horizontal, 0.3F * horizontal, -0.2F * horizontal);
                    GL11.glRotatef(20 * horizontal, 0, 0, 1);
                    GL11.glRotatef(0 * horizontal, 0, 1, 0);
                    GL11.glRotatef(-10 * horizontal, 1, 0, 0);
                }
            }

            drawCables(item, drawback, wheel1, end1, wheel2, end2);
            renderBow(item, arches);
            renderArrow(player, drawback, end1, end2);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glRotatef(140, 1, 0, 0);
            GL11.glTranslatef(-0.035F, 0.4F, -0.68F);
            GL11.glRotatef(180, 1, 0, 0);

            float scale = 0.4F;
            GL11.glScalef(-scale, -scale, -scale);

            if (horizontal > 0)
            {
                GL11.glTranslatef(-0.3F * horizontal, -0.1F * horizontal, -0.0F * horizontal);
                GL11.glRotatef(50 * horizontal, 0, 0, 1);
            }

            drawCables(item, drawback, wheel1, end1, wheel2, end2);
            renderBow(item, arches);
            renderArrow(player, drawback, end1, end2);
            GL11.glPopMatrix();
        }

        GL11.glLineWidth(prevLineWidth);
    }

    public static void renderBow(ItemStack item, float arches)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        MODEL.archBottom1.rotateAngleX = 0.1308996938995747F - arches;
        MODEL.archTop1.rotateAngleX = 0.18151424220741028F - arches;
        MODEL.render();

        if (item != null && item.hasEffect(0))
        {
            SHRenderHooks.renderEnchanted(MODEL::render);
        }
    }

    public static void renderArrow(EntityPlayer player, float drawback, float[] end1, float[] end2)
    {
        if (player != null && player.isUsingItem())
        {
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glRotatef(90, 0, 0, 1);
            GL11.glTranslatef((end1[1] + end2[1]) / 2 - 0.15F, (end1[2] + end2[2]) / 2 + drawback * 1.3F, 0);

            float scale = 2.3F;
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0, -0.0625F * 9, 0);

            ArrowType type = QuiverHelper.getArrowTypeToFire(player);

            if (type != null)
            {
                EntityTrickArrow entity = type.getDummyEntity(player);
                ItemStack itemstack = SHData.CURRENT_ARROW.get(player);

                if (itemstack != null)
                {
                    entity.setArrowItem(itemstack);
                }

                ArrowRenderer.get(type).render(entity, 0, 0, 0, ClientEventHandler.renderTick, false);
            }
        }
    }

    public static void drawCables(ItemStack item, float drawback, float[] wheel1, float[] end1, float[] wheel2, float[] end2)
    {
        if (!ItemCompoundBow.isBroken(item))
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glColor4f(0, 0, 0, 1);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawing(3);
            tessellator.addVertex(wheel1[0], wheel1[1], wheel1[2]);
            tessellator.addVertex(end1[0], end1[1], end1[2]);
            tessellator.draw();
            tessellator.startDrawing(3);
            tessellator.addVertex(wheel2[0], wheel2[1], wheel2[2]);
            tessellator.addVertex(end2[0], end2[1], end2[2]);
            tessellator.draw();
            tessellator.startDrawing(3);
            tessellator.addVertex(end1[0], end1[1], end1[2]);
            tessellator.addVertex((end1[0] + end2[0]) / 2, (end1[1] + end2[1]) / 2 - 0.15F, (end1[2] + end2[2]) / 2 + drawback * 1.3F);
            tessellator.addVertex(end2[0], end2[1], end2[2]);
            tessellator.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1, 1, 1, 1);
        }
    }
}
