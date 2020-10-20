package com.fiskmods.heroes.client.render.arrow;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.render.entity.RenderGrapplingHook;
import com.fiskmods.heroes.common.entity.arrow.EntityDetonatorArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.util.SHRenderHelper;

public class ArrowRendererDetonator extends ArrowRendererExplosive
{
    public ArrowRendererDetonator()
    {
        model.arrowHeadBase.showModel = false;
    }

    @Override
    public void render(EntityTrickArrow arrow, double x, double y, double z, float partialTicks, boolean inQuiver)
    {
        super.render(arrow, x, y, z, partialTicks, inQuiver);

        float scale = 0.75F;
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(0, -1, 0);

        if (!inQuiver)
        {
            float f = 1;

            if (arrow instanceof EntityDetonatorArrow)
            {
                EntityDetonatorArrow entity = (EntityDetonatorArrow) arrow;
                f = SHRenderHelper.interpolate(entity.grappleTimer, entity.prevGrappleTimer);
            }

            mc.getTextureManager().bindTexture(RenderGrapplingHook.TEXTURE);
            RenderGrapplingHook.MODEL.render(f);
        }
    }
}
