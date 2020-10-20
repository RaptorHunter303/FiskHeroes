package com.fiskmods.heroes.client.render.equipment;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.RewardHelper;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.TextureHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public enum RewardGemRenderer implements EquipmentRenderer
{
    INSTANCE;

    @Override
    public boolean test(EntityPlayer player)
    {
        return !player.getHideCape() && !player.isInvisibleToPlayer(mc.thePlayer) && (RewardHelper.hasRewardClient(player) || RewardHelper.isConquestTop(player));
    }

    @Override
    public float[] getOffset(EntityPlayer player, HeroIteration iter, ModelBiped model, float partialTicks)
    {
        return new float[] {-0.5F, -0.25F, 0.084375F / 2};
    }

    @Override
    public void render(EntityPlayer player, HeroIteration iter, ModelBiped model, RenderEquipmentEvent event, float partialTicks)
    {
        float f = MathHelper.sin((player.ticksExisted + partialTicks) / 10) * 0.1F + 0.1F;
        float f1 = (player.ticksExisted + partialTicks) / 20 * (180F / (float) Math.PI);
        float yaw = partialTicks == 1 ? player.renderYawOffset : SHRenderHelper.interpolate(player.renderYawOffset, player.prevRenderYawOffset);
        Tessellator tessellator = Tessellator.instance;
        IIcon icon;

        float r = 0, g = 0, b = 0;

        if (RewardHelper.isConquestTop(player))
        {
            icon = RewardHelper.cactutIcon;
            r = 0.25F;
            g = 0.8F;
            b = 0.5F;
        }
        else if (RewardHelper.isCollaboratorClient(player))
        {
            icon = RewardHelper.collaboratorIcon;
            r = 0.25F;
            g = 0.5F;
            b = 0.8F;
        }
        else if (RewardHelper.isPatronClient(player))
        {
            icon = RewardHelper.patreonIcon;
            r = 0.8F;
            g = 0.5F;
            b = 0.25F;
        }
        else
        {
            icon = ModItems.tutridiumGem.getIconFromDamage(0);
        }

        if (icon == null)
        {
            icon = ((TextureMap) mc.getTextureManager().getTexture(TextureMap.locationItemsTexture)).getAtlasSprite("missingno");
        }

        mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
        TextureUtil.func_152777_a(false, false, 1);
        GL11.glTranslatef(0, f - 0.9F - (player != mc.thePlayer || iter != null && iter.hero.hasEnabledModifier(player, Ability.SHAPE_SHIFTING) && SHData.DISGUISE.get(player) != null ? 0.35F : 0), 0);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(-0.5F, -0.5F, 0.5F);
        GL11.glRotatef((player.ticksExisted + partialTicks) / 20 * (180F / (float) Math.PI) + yaw, 0, 1, 0);
        GL11.glTranslatef(event.xOffset, event.yOffset, event.zOffset);
        GL11.glColor4f(1, 1, 1, 1);
        ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);

        if (RewardHelper.inPatreonClubClient(player))
        {
            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            mc.getTextureManager().bindTexture(TextureHelper.RES_ITEM_GLINT);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(768, 1, 1, 0);
            float f7 = 0.76F;
            GL11.glColor4f(r * f7, g * f7, b * f7, 1);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            float f8 = 0.125F;
            GL11.glScalef(f8, f8, f8);
            float f9 = Minecraft.getSystemTime() % 3000L / 3000F * 8;
            GL11.glTranslatef(f9, 0, 0);
            GL11.glRotatef(-50, 0, 0, 1);
            ItemRenderer.renderItemIn2D(tessellator, 0, 0, 1, 1, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(f8, f8, f8);
            f9 = Minecraft.getSystemTime() % 4873L / 4873F * 8;
            GL11.glTranslatef(-f9, 0, 0);
            GL11.glRotatef(10, 0, 0, 1);
            ItemRenderer.renderItemIn2D(tessellator, 0, 0, 1, 1, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1, 1, 1, 1);
        TextureUtil.func_147945_b();
    }
}
