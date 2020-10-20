package com.fiskmods.heroes.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.json.hero.JsonHeroRenderer;
import com.fiskmods.heroes.client.model.ModelSombrero;
import com.fiskmods.heroes.common.entity.EntityCactus;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class RenderCactus extends RenderLiving
{
    public ResourceLocation texture = new ResourceLocation(FiskHeroes.MODID, JsonHeroRenderer.TEXTURE_DIR + "senor_cactus_sombrero.png");
    public ModelSombrero model = new ModelSombrero();

    public RenderCactus()
    {
        super(null, 0.0F);
    }

    public void doRender(EntityCactus entity, double x, double y, double z, float f, float partialTicks)
    {
        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(entity, this, x, y, z)))
        {
            return;
        }

        bindEntityTexture(entity);
        GL11.glPushMatrix();

        try
        {
            float f2 = interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f3 = interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f4;

            if (entity.isRiding() && entity.ridingEntity instanceof EntityLivingBase)
            {
                EntityLivingBase entitylivingbase1 = (EntityLivingBase) entity.ridingEntity;
                f2 = interpolateRotation(entitylivingbase1.prevRenderYawOffset, entitylivingbase1.renderYawOffset, partialTicks);
                f4 = MathHelper.wrapAngleTo180_float(f3 - f2);

                if (f4 < -85.0F)
                {
                    f4 = -85.0F;
                }

                if (f4 >= 85.0F)
                {
                    f4 = 85.0F;
                }

                f2 = f3 - f4;

                if (f4 * f4 > 2500.0F)
                {
                    f2 += f4 * 0.2F;
                }
            }

            float f13 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            renderLivingAt(entity, x, y + 0.5F, z);
            f4 = handleRotationFloat(entity, partialTicks);
            rotateCorpse(entity, f4, f2, partialTicks);

            if (!entity.isInvisible())
            {
                renderModel(entity);
            }
            else if (!entity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer))
            {
                boolean inventoryTint = field_147909_c.useInventoryTint;
                field_147909_c.useInventoryTint = false;
                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 0.15F);
                GL11.glDepthMask(false);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
                renderModel(entity);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                GL11.glPopMatrix();
                GL11.glDepthMask(true);
                field_147909_c.useInventoryTint = inventoryTint;
            }
            else
            {
                GL11.glTranslatef(0, entity.getCactusSize(), 0);
            }

            if (entity.isDonatorSummoned())
            {
                bindTexture(texture);
                float scale = 1.75F;
                GL11.glScalef(scale, -scale, -scale);
                GL11.glTranslatef(0, 0.0625F * 5, 0);
                model.render(0.0625F);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        GL11.glPopMatrix();
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(entity, this, x, y, z));
    }

    public void renderModel(EntityCactus entity)
    {
        for (int i = 0; i < entity.getCactusSize(); ++i)
        {
            field_147909_c.renderBlockAsItem(Blocks.cactus, 0, 1.0F);
            GL11.glTranslatef(0, 1, 0);
        }
    }

    private float interpolateRotation(float p_77034_1_, float p_77034_2_, float p_77034_3_)
    {
        float f3;

        for (f3 = p_77034_2_ - p_77034_1_; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return p_77034_1_ + p_77034_3_ * f3;
    }

    protected void rotateCorpse(EntityCactus entity, float x, float y, float z)
    {
        super.rotateCorpse(entity, x, y, z);

        if (entity.limbSwingAmount >= 0.01D)
        {
            float f3 = 13.0F;
            float f4 = entity.limbSwing - entity.limbSwingAmount * (1.0F - z) + 6.0F;
            float f5 = (Math.abs(f4 % f3 - f3 * 0.5F) - f3 * 0.25F) / (f3 * 0.25F);
            GL11.glRotatef(6.5F * f5, 0.0F, 0.0F, 1.0F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return TextureMap.locationBlocksTexture;
    }

    @Override
    protected void rotateCorpse(EntityLivingBase entity, float x, float y, float z)
    {
        rotateCorpse((EntityCactus) entity, x, y, z);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        doRender((EntityCactus) entity, x, y, z, f, partialTicks);
    }
}
