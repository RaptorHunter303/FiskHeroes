package com.fiskmods.heroes.client.render.arrow;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.util.ResourceLocation;

public class ArrowRendererTipped extends ArrowRenderer
{
    public final ResourceLocation texture;
    public final boolean hasGlow;

    public ArrowRendererTipped(String domain, String path, boolean glow)
    {
        texture = new ResourceLocation(domain, path);
        hasGlow = glow;
    }

    public ArrowRendererTipped(String key, boolean glow)
    {
        this(FiskHeroes.MODID, String.format("textures/models/arrow_head_%s.png", key), glow);
    }

    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        setupModel(false);
        super.render(arrow, x, y, z, partialTicks, inQuiver);

        setupModel(true);
        mc.getTextureManager().bindTexture(texture);

        if (hasGlow)
        {
            SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
            model.render(0.0625F);
            SHRenderHelper.resetLighting();
        }
        else
        {
            model.render(0.0625F);
        }
    }

    private void setupModel(boolean head)
    {
        model.arrowHeadBase.showModel = head;
        model.featherMain1.showModel = !head;
        model.featherMain2.showModel = !head;
        model.featherMain3.showModel = !head;
        model.stick.showModel = !head;
    }
}
