package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.item.ModelChronosRifle;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.Constants.NBT;

public enum RenderItemChronosRifle implements IItemRenderer
{
    INSTANCE;

    private static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/chronos_rifle.png");
    private static final ResourceLocation TEXTURE_LIGHTS = new ResourceLocation(FiskHeroes.MODID, "textures/models/chronos_rifle_lights.png");
    private static final ModelChronosRifle MODEL = new ModelChronosRifle();

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
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data)
    {
        EntityLivingBase entity = null;

        if (data.length > 1 && data[1] instanceof EntityLivingBase)
        {
            entity = (EntityLivingBase) data[1];
        }

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            float f = FiskMath.curve(SHData.AIMING_TIMER.interpolate(entity));
            float f1 = SHData.RELOAD_TIMER.interpolate(entity);
            float f2 = SHData.SCOPE_TIMER.interpolate(entity);
            float f3 = Math.max(f2 * 1.25F - 0.25F, 0) * f;
            float f4 = Math.min(f3 * 1.25F, 1) * f;
            float recoil = 1 - Math.min((1 - f1) * 4, 1);

            GL11.glPushMatrix();
            GL11.glTranslatef(0.6F - f * 0.05F - recoil * 0.3F - f3 * 1.525F, 0.2F + f * 0.1F - recoil * 0.1F - f3 * 0.2F, -0.5F + f * 0.1F - f4 * 0.6925F - recoil * f2 * 0.0325F);
            GL11.glRotatef(-10 + f * 5 + f4 * 6, 1, 0, 0);
            GL11.glRotatef(200 + f * (5 + recoil * (f2 - 1) * 5) - f4 * 10, 0, 0, 1);
            GL11.glRotatef(50 + f * 50 - f4 * 5, 0, 1, 0);
            GL11.glTranslatef(0, 0, f2 * 0.3F);
            render(stack);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            float f = FiskMath.curve(SHData.AIMING_TIMER.interpolate(entity));
            float f1 = SHRenderHelper.interpolate(entity.rotationPitch, entity.prevRotationPitch) / 90;
            boolean flag = entity instanceof EntityDisplayMannequin;

            if (flag)
            {
                f = f1 = 0;
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0.35F, 0.2F, flag ? -0.25F : -0.2F);
            GL11.glRotatef(flag ? 12 : 40 * (1 - f), 0, 1, 0);
            GL11.glRotatef(15, 0, 0, 1);
            GL11.glRotatef(flag ? -190 : -195 - f * 15 + Math.max(f1 - 0.4F, 0) * 60, 1, 0, 0);

            float scale = 1.2F;
            GL11.glScalef(scale, scale, scale);
            render(stack);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.1F, -0.5F, -0.5F);
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glRotatef(90, 0, 1, 0);

            float scale = 0.9F;
            GL11.glScalef(scale, scale, scale);
            render(stack);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.ENTITY)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -0.05F, -0.5F);
            GL11.glRotatef(180, 1, 0, 0);
            render(stack);
            GL11.glPopMatrix();
        }
    }

    public static void render(ItemStack stack)
    {
        ResourceLocation texture = TEXTURE;

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fisktag", NBT.TAG_COMPOUND))
        {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("fisktag");
            MODEL.shape25.isHidden = tag.hasKey("Scope", NBT.TAG_ANY_NUMERIC) && !tag.getBoolean("Scope");

            if (tag.hasKey("Texture", NBT.TAG_STRING))
            {
                texture = new ResourceLocation(tag.getString("Texture"));
            }
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        MODEL.render();
        GL11.glDisable(GL11.GL_LIGHTING);
        SHRenderHelper.setLighting(SHRenderHelper.FULLBRIGHT);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_LIGHTS);
        MODEL.render();
        SHRenderHelper.resetLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);

        MODEL.shape25.isHidden = false;
    }
}
