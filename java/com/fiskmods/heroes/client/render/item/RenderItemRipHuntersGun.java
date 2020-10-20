package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelRipHuntersGun;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectEnergyBolt;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemRipHuntersGun implements IItemRenderer
{
    INSTANCE;

    private static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/rip_hunters_gun.png");
    private static final ResourceLocation TEXTURE_LIGHTS = new ResourceLocation(FiskHeroes.MODID, "textures/models/rip_hunters_gun_lights.png");
    private static final ModelRipHuntersGun MODEL = new ModelRipHuntersGun();

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
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        EntityPlayer player = null;

        if (data.length > 1 && data[1] instanceof EntityPlayer)
        {
            player = (EntityPlayer) data[1];
        }

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            float f = FiskMath.curve(SHData.AIMING_TIMER.interpolate(player));
            float f1 = SHData.RELOAD_TIMER.interpolate(player);
            float f2 = SHData.SCOPE_TIMER.interpolate(player);

            GL11.glPushMatrix();
            GL11.glTranslatef(1.2F - f * 0.2F - f1 * 0.05F, f * 0.3F + f2 * 0.1F, 0.25F - f * 0.5F - f2 * 0.665F);
            GL11.glRotatef(-10 + f * 5 + f2 * 5, 1, 0, 0);
            GL11.glRotatef(200 + f * 10 + f1 * 5, 0, 0, 1);
            GL11.glRotatef(115 - f * 20, 0, 1, 0);

            float scale = 0.7F;
            GL11.glScalef(scale, scale, scale);
            render(player);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.425F, 0.075F, -0.2F);
            GL11.glRotatef(12, 0, 1, 0);
            GL11.glRotatef(15, 0, 0, 1);
            GL11.glRotatef(-170, 1, 0, 0);

            float scale = 0.6F;
            GL11.glScalef(scale, scale, scale);
            render(player);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -0.5F, -0.5F);
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glRotatef(90, 0, 1, 0);

            float scale = 0.7F;
            GL11.glScalef(scale, scale, scale);
            render(player);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.ENTITY)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -0.05F, -0.3F);
            GL11.glRotatef(180, 1, 0, 0);

            float scale = 0.4F;
            GL11.glScalef(scale, scale, scale);
            render(player);
            GL11.glPopMatrix();
        }
    }

    public static void render(EntityPlayer player)
    {
        int color = 0x005EFF;

        if (player != null)
        {
            MODEL.shape50.rotateAngleZ = (float) Math.toRadians(30 * (1 + SHData.RELOAD_TIMER.interpolate(player) * 2));
        }
        else
        {
            MODEL.shape50.rotateAngleZ = 0.5235987755982988F;
            player = Minecraft.getMinecraft().thePlayer;
        }

        HeroIteration iter = SHHelper.getHeroIter(player);

        if (iter != null)
        {
            HeroRenderer renderer = HeroRenderer.get(iter);
            HeroEffectEnergyBolt effect = renderer.getEffect(HeroEffectEnergyBolt.class, player);

            if (effect != null)
            {
                color = effect.getColor();
            }
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        MODEL.render();
        GL11.glDisable(GL11.GL_LIGHTING);
        SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_LIGHTS);
        SHRenderHelper.setGlColor(SHRenderHelper.getColorFromHex(color));
        MODEL.render();
        GL11.glColor4f(1, 1, 1, 1);
        SHRenderHelper.resetLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
