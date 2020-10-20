package com.fiskmods.heroes.client.render.entity.player;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectArmAnimation;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectEnergyProj;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.event.ClientEventHandler;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerHand extends RenderPlayer
{
    public static boolean rendering;

    public RenderPlayer parent;

    @Override
    public void renderFirstPersonArm(EntityPlayer player)
    {
        float aiming = SHData.AIMING_TIMER.interpolate(player);
        float blocking = SHData.SHIELD_BLOCKING_TIMER.interpolate(player);
        float recoil = 1 - Math.min((1 - SHData.RELOAD_TIMER.interpolate(player)) * 4, 1);

        if (aiming > 0)
        {
            GL11.glTranslatef(-0.35F * aiming, 0, 0.15F * aiming);
            GL11.glRotatef(-27 * aiming, 0, 1, 0);
            GL11.glRotatef(-20 * aiming, 1, 0, 0);
            GL11.glRotatef(-30 * aiming, 0, 0, 1);
        }

        if (blocking > 0)
        {
            GL11.glTranslatef(-0.6F * blocking, -0.2F * blocking, 0);
            GL11.glRotatef(70 * blocking, 0, 1, 0);
            GL11.glRotatef(-80 * blocking, 0, 0, 1);
        }

        if (recoil > 0)
        {
            GL11.glTranslatef(0, -recoil * 0.1F, 0);
        }

        boolean renderDefaultArm = true;
        ItemStack itemstack = player.getCurrentArmor(2);
        HeroIteration iter = null;

        if (itemstack != null)
        {
            iter = SHHelper.getHeroIterFromArmor(player, 2);

            if (iter != null)
            {
                HeroRenderer renderer = HeroRenderer.get(iter);
                float shooting = SHData.SHOOTING_TIMER.interpolate(player);

                renderDefaultArm = renderer.shouldRenderDefaultModel(player, iter, false);

                if (shooting > 0)
                {
                    if (iter.hero.hasEnabledModifier(player, Ability.HEAT_VISION))
                    {
                        GL11.glTranslatef(0, -shooting, 0);
                    }

                    if (iter.hero.hasEnabledModifier(player, Ability.ENERGY_PROJECTION))
                    {
                        HeroEffectEnergyProj effect = renderer.getEffect(HeroEffectEnergyProj.class, player);

                        if (effect != null && effect.shouldUseHands() && player.swingProgress == 0)
                        {
                            GL11.glTranslatef(-0.4F * shooting, -0.3F * shooting, -0.15F * shooting);
                            GL11.glRotatef(-20 * shooting, 1, 0, 0);
                            GL11.glRotatef(-35 * shooting, 0, 0, 1);
                        }
                    }
                }

                HeroEffectArmAnimation armAnim = renderer.getEffect(HeroEffectArmAnimation.class, player);

                if (armAnim != null)
                {
                    float t = armAnim.swordPose(player, itemstack);

                    if (t > 0)
                    {
                        t = FiskMath.curve(t);
                        GL11.glRotatef(53 * t, 0, 1, 0);
                        GL11.glRotatef(20 * t, 1, 0, 0);
                        GL11.glRotatef(-20 * t, 0, 0, 1);
                        GL11.glTranslatef(0, -0.2F * t, -0.3F * t);
                    }
                }
            }
        }

        ModelRenderer arm = modelBipedMain.bipedRightArm;
        arm.rotateAngleX = 0;
        arm.rotateAngleY = 0;
        arm.rotateAngleZ = 0;

        if (renderDefaultArm)
        {
            super.renderFirstPersonArm(player);
        }

        if (iter != null)
        {
            HeroRenderer renderer = HeroRenderer.get(iter);
            ResourceLocation texture = renderer.getTexture(itemstack, player, 1);

            if (texture != null)
            {
                bindTexture(texture);
            }

            ModelBipedMultiLayer.sync(arm, renderer.model.bipedRightArm);
            ModelBipedMultiLayer.sync(arm, renderer.model.bipedRightArmL2);
            renderer.model.onGround = 0;

            GL11.glColor4f(1, 1, 1, 1);
            GL11.glDisable(GL11.GL_CULL_FACE);
            SHRenderHelper.setupRenderHero(false);

            if (renderer.preRenderArm(player, itemstack, iter, Pass.BASE))
            {
                renderer.model.renderArm(player, itemstack, iter, Pass.BASE, 0.0625F);
                renderer.postRenderArm(player, itemstack, iter, Pass.BASE);
            }

            ResourceLocation lights = renderer.getLightsTexture(player, 1);
            SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
            GL11.glDisable(GL11.GL_LIGHTING);

            if (renderer.preRenderArm(player, itemstack, iter, Pass.GLOW))
            {
                if (lights != null)
                {
                    bindTexture(lights);
                    renderer.model.renderArm(player, itemstack, iter, Pass.GLOW, 0.0625F);
                }

                renderer.postRenderArm(player, itemstack, iter, Pass.GLOW);
            }

            SHRenderHelper.finishRenderHero(true);
            GL11.glEnable(GL11.GL_CULL_FACE);

            if (renderer.preRenderArm(player, itemstack, iter, Pass.ENCHANTMENT))
            {
                float ticks = player.ticksExisted + ClientEventHandler.renderTick;
                int renderPass = shouldRenderPass(player, 1, ClientEventHandler.renderTick);

                if ((renderPass & 15) == 15)
                {
                    HeroIteration iter1 = iter;
                    SHRenderHooks.renderEnchanted(() ->
                    {
                        renderer.model.renderArm(player, itemstack, iter1, Pass.ENCHANTMENT, 0.0625F);
                        renderer.postRenderArm(player, itemstack, iter1, Pass.ENCHANTMENT);
                    });
                }
            }
        }
    }

    @Override
    protected void bindTexture(ResourceLocation location)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
    }

    public void setParent(RenderPlayer render)
    {
        parent = render;
    }
}
