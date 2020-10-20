package com.fiskmods.heroes.client.model;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer.Pass;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class ModelBipedMultiLayer extends ModelBiped
{
    private final ModelRenderer[][] layer2;

    public ModelRenderer[] bipedHeadwear = new ModelRenderer[2];
    public ModelRenderer[] bipedBodyL2 = new ModelRenderer[2];
    public ModelRenderer[] bipedRightArmL2 = new ModelRenderer[2];
    public ModelRenderer[] bipedLeftArmL2 = new ModelRenderer[2];
    public ModelRenderer[] bipedRightLegL2 = new ModelRenderer[2];
    public ModelRenderer[] bipedLeftLegL2 = new ModelRenderer[2];
    public ModelRenderer hatLayer;

    protected Minecraft mc = Minecraft.getMinecraft();
    public HeroRenderer renderer;
    public int armorSlot;

    public ModelBipedMultiLayer()
    {
        float scale = 0.05F;
        float scaleL2 = scale + 0.5F;
        float scaleL3 = scale / 2 + 0.5F;
        textureWidth = 64;
        textureHeight = 64;
        hatLayer = new ModelRenderer(this, 32, 0);
        hatLayer.setTextureSize(64, 32);
        hatLayer.setRotationPoint(0, 0, 0);
        hatLayer.addBox(-4, -8, -4, 8, 8, 8, scale / 2);

        bipedHead = new ModelRenderer(this, 0, 0);
        bipedHead.setRotationPoint(0, 0, 0);
        bipedHead.addBox(-4, -8, -4, 8, 8, 8, scale);
        bipedBody = new ModelRenderer(this, 16, 16);
        bipedBody.setRotationPoint(0, 0, 0);
        bipedBody.addBox(-4, 0, -2, 8, 12, 4, scale);
        bipedRightArm = new ModelRenderer(this, 40, 16);
        bipedRightArm.setRotationPoint(-5, 2, 0);
        bipedRightArm.addBox(-3, -2, -2, 4, 12, 4, scale / 2);
        setRotateAngle(bipedRightArm, 0, 0, 0.1F);
        bipedLeftArm = new ModelRenderer(this, 32, 48);
        bipedLeftArm.setRotationPoint(5, 2, -0);
        bipedLeftArm.addBox(-1, -2, -2, 4, 12, 4, scale / 2);
        setRotateAngle(bipedLeftArm, 0, 0, -0.1F);
        bipedRightLeg = new ModelRenderer(this, 0, 16);
        bipedRightLeg.setRotationPoint(-1.9F, 12, 0.1F);
        bipedRightLeg.addBox(-2, 0, -2, 4, 12, 4, scale);
        bipedLeftLeg = new ModelRenderer(this, 16, 48);
        bipedLeftLeg.setRotationPoint(1.9F, 12, 0.1F);
        bipedLeftLeg.addBox(-2, 0, -2, 4, 12, 4, scale);

        int[][] textureX = {{32, 16, 40, 48, 0, 0}, {0, 16, 40, 32, 0, 16}};
        int[][] textureY = {{0, 32, 32, 48, 32, 48}, {0, 16, 16, 48, 16, 48}};

        for (int i = 0; i < 2; ++i)
        {
            bipedHeadwear[i] = new ModelRenderer(this, textureX[i][0], textureY[i][0]);
            bipedHeadwear[i].addBox(-4, -8, -4, 8, 8, 8, scaleL2);
            bipedBodyL2[i] = new ModelRenderer(this, textureX[i][1], textureY[i][1]);
            bipedBodyL2[i].addBox(-4, 0, -2, 8, 12, 4, scaleL2);
            bipedRightArmL2[i] = new ModelRenderer(this, textureX[i][2], textureY[i][2]);
            bipedRightArmL2[i].addBox(-3, -2, -2, 4, 12, 4, scaleL3);
            bipedLeftArmL2[i] = new ModelRenderer(this, textureX[i][3], textureY[i][3]);
            bipedLeftArmL2[i].addBox(-1, -2, -2, 4, 12, 4, scaleL3);
            bipedRightLegL2[i] = new ModelRenderer(this, textureX[i][4], textureY[i][4]);
            bipedRightLegL2[i].addBox(-2, 0, -2, 4, 12, 4, scaleL2);
            bipedLeftLegL2[i] = new ModelRenderer(this, textureX[i][5], textureY[i][5]);
            bipedLeftLegL2[i].addBox(-2, 0, -2, 4, 12, 4, scaleL2);
        }

        layer2 = new ModelRenderer[][] {bipedHeadwear, bipedBodyL2, bipedRightArmL2, bipedLeftArmL2, bipedRightLegL2, bipedLeftLegL2};
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale)
    {
        setRotationAngles(f, f1, f2, f3, f4, scale, entity);
        ModelHelper.renderBipedPre(this, entity, f, f1, f2, f3, f4, scale);
        sync();

        int pass = Pass.detectPass();

        if (pass == Pass.BASE)
        {
            SHRenderHelper.setupRenderHero(false);
        }

        if (renderer.preRenderBody(entity, pass, f, f1, f2, f3, f4, scale))
        {
            renderBody(entity, pass, f, f1, f2, f3, f4, scale);
            renderer.postRenderBody(entity, pass, f, f1, f2, f3, f4, scale);
        }

        if (pass == Pass.BASE)
        {
            SHRenderHelper.setupRenderHero(true);
            ResourceLocation lights = renderer.getLightsTexture(entity, armorSlot);

            if (renderer.preRenderBody(entity, Pass.GLOW, f, f1, f2, f3, f4, scale))
            {
                if (lights != null)
                {
                    mc.getTextureManager().bindTexture(lights);
                    renderBody(entity, Pass.GLOW, f, f1, f2, f3, f4, scale);
                }

                renderer.postRenderBody(entity, Pass.GLOW, f, f1, f2, f3, f4, scale);
            }

            SHRenderHelper.finishRenderHero(true);
        }

        ModelHelper.renderBipedPost(this, entity, f, f1, f2, f3, f4, scale);
    }

    public void renderBody(Entity entity, int pass, float f, float f1, float f2, float f3, float f4, float scale)
    {
        boolean flag = pass == Pass.ENCHANTMENT;
        renderParts(entity, anim ->
        {
            renderPart(bipedHead, scale, anim);
            renderPart(bipedBody, scale, anim);
            renderPart(bipedRightArm, scale, anim);
            renderPart(bipedLeftArm, scale, anim);
            renderPart(bipedRightLeg, scale, anim);
            renderPart(bipedLeftLeg, scale, anim);

            for (ModelRenderer[] models : layer2)
            {
                renderPart(models[flag ? 1 : 0], scale, anim);
            }
        });
    }

    public void renderArm(EntityPlayer player, ItemStack itemstack, HeroIteration iter, int pass, float scale)
    {
        renderParts(player, anim ->
        {
            renderPart(bipedRightArm, scale, anim);
            renderPart(bipedRightArmL2[pass == Pass.ENCHANTMENT ? 1 : 0], scale, anim);
        });
    }

    public void renderParts(Entity entity, Consumer<Float> render)
    {
        if (entity == null)
        {
            render.accept(0F);
            return;
        }

        float alpha = -1;
        float anim = 0;

        if (entity instanceof EntityLivingBase && StatusEffect.has((EntityLivingBase) entity, StatEffect.TUTRIDIUM_POISON))
        {
            Function<Integer, Float> func = i -> new Random(100000 + (entity.ticksExisted + i) * 1000).nextFloat();
            anim = Math.abs(MathHelper.cos(SHRenderHelper.interpolate(func.apply(1), func.apply(0)) * 2));

            if ((alpha = SHRenderHelper.getAlpha()) > 0)
            {
                SHRenderHelper.setAlpha(alpha * (1 - anim / 2));
            }

            anim /= 10;
        }

        render.accept(anim);

        if (alpha > 0)
        {
            SHRenderHelper.setAlpha(alpha);
        }
    }

    public void renderParts(Entity entity, ModelRenderer anchor, float scale, Consumer<Float> render)
    {
        if (anchor.showModel && !anchor.isHidden)
        {
            renderParts(entity, anim -> renderPart(anchor, scale, anim, () -> render.accept(anim)));
        }
    }

    public void renderPart(ModelRenderer renderer, float scale, float anim)
    {
        renderPart(renderer, scale, anim, () -> renderer.render(scale));
    }

    public void renderPart(ModelRenderer anchor, float scale, float anim, Runnable render)
    {
        if (anim > 0)
        {
            anim += 1;
            GL11.glPushMatrix();
            GL11.glTranslatef(anchor.offsetX, anchor.offsetY, anchor.offsetZ);
            GL11.glTranslatef(anchor.rotationPointX * scale, anchor.rotationPointY * scale, anchor.rotationPointZ * scale);
            GL11.glScalef(anim, anim, anim);
            GL11.glTranslatef(-anchor.offsetX, -anchor.offsetY, -anchor.offsetZ);
            GL11.glTranslatef(-anchor.rotationPointX * scale, -anchor.rotationPointY * scale, -anchor.rotationPointZ * scale);
            render.run();
            GL11.glPopMatrix();
            return;
        }

        render.run();
    }

    public ItemStack getArmorStack(Entity entity)
    {
        return entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getEquipmentInSlot(4 - armorSlot) : null;
    }

    public void reset()
    {
        reset(this);
        sync();
    }

    public static void reset(ModelBiped model)
    {
        model.aimedBow = false;
        model.isChild = false;
        model.isRiding = false;
        model.isSneak = false;
        model.onGround = 0;
        model.heldItemLeft = 0;
        model.heldItemRight = 0;
        model.setRotationAngles(0, 0, 0, 0, 0, 0.0625F, null);
        setRotateAngle(model.bipedHead, 0, 0, 0);
        setRotateAngle(model.bipedHeadwear, 0, 0, 0);
        setRotateAngle(model.bipedBody, 0, 0, 0);
        setRotateAngle(model.bipedRightArm, 0, 0, 0);
        setRotateAngle(model.bipedLeftArm, 0, 0, 0);
        setRotateAngle(model.bipedRightLeg, 0, 0, 0);
        setRotateAngle(model.bipedLeftLeg, 0, 0, 0);
    }

    public void sync()
    {
        sync(bipedHead, bipedHeadwear);
        sync(bipedBody, bipedBodyL2);
        sync(bipedRightArm, bipedRightArmL2);
        sync(bipedLeftArm, bipedLeftArmL2);
        sync(bipedRightLeg, bipedRightLegL2);
        sync(bipedLeftLeg, bipedLeftLegL2);
    }

    public static void sync(ModelRenderer parent, ModelRenderer... children)
    {
        for (ModelRenderer child : children)
        {
            child.rotateAngleX = parent.rotateAngleX;
            child.rotateAngleY = parent.rotateAngleY;
            child.rotateAngleZ = parent.rotateAngleZ;

            child.rotationPointX = parent.rotationPointX;
            child.rotationPointY = parent.rotationPointY;
            child.rotationPointZ = parent.rotationPointZ;

            child.showModel = parent.showModel;
            child.isHidden = parent.isHidden;
            child.mirror = parent.mirror;

            child.offsetX = parent.offsetX;
            child.offsetY = parent.offsetY;
            child.offsetZ = parent.offsetZ;
        }
    }

    public static void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
