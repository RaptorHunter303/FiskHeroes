package com.fiskmods.heroes.client.render.equipment;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectChest;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.RewardHelper;
import com.fiskmods.heroes.util.RewardHelper.Ranking;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public enum ConquestBadgeRenderer implements EquipmentRenderer
{
    INSTANCE;

    @Override
    public boolean test(EntityPlayer player)
    {
        return !player.isInvisibleToPlayer(mc.thePlayer) && RewardHelper.hasConquestRanking(player);
    }

    @Override
    public float[] getOffset(EntityPlayer player, HeroIteration iter, ModelBiped model, float partialTicks)
    {
        Hero hero = SHHelper.getHeroFromArmor(player, 2);
        float f = 0;

        if (hero != null)
        {
            if (!hero.hasEnabledModifier(player, Ability.SHAPE_SHIFTING) || SHData.DISGUISE.get(player) == null)
            {
                f = 0.0375F;
            }
        }
        else if (player.getCurrentArmor(2) != null)
        {
            f = 0.0625F;
        }

        return new float[] {0.125F, 0.125F, -0.125F - f};
    }

    @Override
    public void render(EntityPlayer player, HeroIteration iter, ModelBiped model, RenderEquipmentEvent event, float partialTicks)
    {
        mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
        TextureUtil.func_152777_a(false, false, 1);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        model.bipedBody.postRender(0.0625F);

        final float scale = 0.125F;
        final float maxWidth = 4;
        float y = event.yOffset;

        iter = SHHelper.getHeroIterFromArmor(event.player, 2);
        HeroRenderer renderer = HeroRenderer.get(iter);
        Tessellator tessellator = Tessellator.instance;
        IIcon icon;

        float extrude = 0;
        float yOffset = 0;
        float z = event.zOffset;

        if (renderer != null && iter != null)
        {
            HeroEffectChest effect = renderer.getEffect(HeroEffectChest.class, player);

            if (effect != null)
            {
                extrude = effect.getExtrude() / 16;
                yOffset = effect.getYOffset() / 16;
                z -= 0.03F * extrude;
            }
        }

        for (Ranking ranking : Ranking.values())
        {
            icon = RewardHelper.conquestIcons[ranking.ordinal()];

            if (icon == null)
            {
                icon = ((TextureMap) mc.getTextureManager().getTexture(TextureMap.locationItemsTexture)).getAtlasSprite("missingno");
            }

            int num = RewardHelper.getConquestRanking(player, ranking) * 1;

            if (num > 0)
            {
                int o = ranking.ordinal();
                float w = (16 - o * o * 1.5F) * scale;
                float h = (9 - o) * scale / 16;
                int maxRow = Math.max(MathHelper.floor_float(maxWidth / w), 1);

                for (; num > 0; num -= maxRow, y += h)
                {
                    int qty = Math.min(num, maxRow);
                    float width = w * qty;
                    float z1 = z;

                    if (y >= yOffset && y <= yOffset + 6.5 / 16)
                    {
                        z1 -= MathHelper.sin((float) ((y - yOffset) * Math.PI * 16 / 6.5)) * extrude;
                    }

                    for (int i = 0; i < qty; ++i)
                    {
                        float x = event.xOffset + (w * i - (width - w) / 2 - 8 * scale) / 16F;

                        GL11.glPushMatrix();
                        GL11.glTranslatef(x, y, z1);
                        GL11.glScalef(-scale, -scale, scale);
                        GL11.glTranslatef(-1, -0.5F, 0);
                        GL11.glColor4f(1, 1, 1, 1);
                        ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
                        GL11.glPopMatrix();
                    }
                }
            }
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        TextureUtil.func_147945_b();
    }
}
