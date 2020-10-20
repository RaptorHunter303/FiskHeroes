package com.fiskmods.heroes.client.render.entity.player;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;
import com.fiskmods.heroes.util.TextureHelper;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Full, uncircumcised version of {@link RenderDisplayMannequin}. Saved for future reference.
 *
 * @author FiskFille
 */
@SideOnly(Side.CLIENT)
public class RenderDisplayMannequinFull extends RenderPlayer
{
    private static final Logger logger = LogManager.getLogger();

    protected int shouldRenderPass(EntityDisplayMannequin entity, int slot, float partialTicks)
    {
        ItemStack itemstack = entity.inventory.armorItemInSlot(3 - slot);

        RenderPlayerEvent.SetArmorModel event = new RenderPlayerEvent.SetArmorModel(entity, this, 3 - slot, partialTicks, itemstack);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.result != -1)
        {
            return event.result;
        }

        if (itemstack != null)
        {
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor)
            {
                ItemArmor itemarmor = (ItemArmor) item;
                bindTexture(RenderBiped.getArmorResource(entity, itemstack, slot, null));
                ModelBiped modelbiped = slot == 2 ? modelArmor : modelArmorChestplate;
                modelbiped.bipedHead.showModel = slot == 0;
                modelbiped.bipedHeadwear.showModel = slot == 0;
                modelbiped.bipedBody.showModel = slot == 1 || slot == 2;
                modelbiped.bipedRightArm.showModel = slot == 1;
                modelbiped.bipedLeftArm.showModel = slot == 1;
                modelbiped.bipedRightLeg.showModel = slot == 2 || slot == 3;
                modelbiped.bipedLeftLeg.showModel = slot == 2 || slot == 3;
                modelbiped = ForgeHooksClient.getArmorModel(entity, itemstack, slot, modelbiped);
                setRenderPassModel(modelbiped);
                modelbiped.onGround = mainModel.onGround;
                modelbiped.isRiding = mainModel.isRiding;
                modelbiped.isChild = mainModel.isChild;

                // Move outside if to allow for more then just CLOTH
                int j = itemarmor.getColor(itemstack);

                if (j != -1)
                {
                    float f1 = (j >> 16 & 255) / 255;
                    float f2 = (j >> 8 & 255) / 255;
                    float f3 = (j & 255) / 255;
                    GL11.glColor3f(f1, f2, f3);

                    if (itemstack.isItemEnchanted())
                    {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(1, 1, 1);

                if (itemstack.isItemEnchanted())
                {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    protected void bindArmorOverlayTexture(EntityDisplayMannequin entity, int slot, float partialTicks)
    {
        ItemStack itemstack = entity.inventory.armorItemInSlot(3 - slot);

        if (itemstack != null)
        {
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor)
            {
                bindTexture(RenderBiped.getArmorResource(entity, itemstack, slot, "overlay"));
                GL11.glColor3f(1, 1, 1);
            }
        }
    }

    public void doRender(EntityDisplayMannequin entity, double x, double y, double z, float f, float partialTicks)
    {
        if (MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Pre(entity, this, partialTicks)))
        {
            return;
        }

        GL11.glColor3f(1, 1, 1);
        ItemStack itemstack = entity.inventory.getCurrentItem();
        modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight = itemstack != null ? 1 : 0;

        if (itemstack != null && entity.getItemInUseCount() > 0)
        {
            EnumAction enumaction = itemstack.getItemUseAction();

            if (enumaction == EnumAction.block)
            {
                modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight = 3;
            }
            else if (enumaction == EnumAction.bow)
            {
                modelArmorChestplate.aimedBow = modelArmor.aimedBow = modelBipedMain.aimedBow = true;
            }
        }

        modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = entity.isSneaking();
        double d3 = y - entity.yOffset;

        if (entity.isSneaking() && !(entity instanceof EntityPlayerSP))
        {
            d3 -= 0.125D;
        }

        doRenderSuper(entity, x, d3, z, f, partialTicks);
        modelArmorChestplate.aimedBow = modelArmor.aimedBow = modelBipedMain.aimedBow = false;
        modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = false;
        modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight = 0;
        MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Post(entity, this, partialTicks));
    }

    private float interpolateRotation(float f, float f1, float f2)
    {
        float f3;

        for (f3 = f1 - f; f3 < -180; f3 += 360)
        {
            ;
        }

        while (f3 >= 180)
        {
            f3 -= 360;
        }

        return f + f2 * f3;
    }

    public void doRenderSuper(EntityLivingBase entity, double x, double y, double z, float f, float partialTicks)
    {
        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(entity, this, x, y, z)))
        {
            return;
        }

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        mainModel.onGround = renderSwingProgress(entity, partialTicks);

        if (renderPassModel != null)
        {
            renderPassModel.onGround = mainModel.onGround;
        }

        mainModel.isRiding = entity.isRiding();

        if (renderPassModel != null)
        {
            renderPassModel.isRiding = mainModel.isRiding;
        }

        mainModel.isChild = entity.isChild();

        if (renderPassModel != null)
        {
            renderPassModel.isChild = mainModel.isChild;
        }

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

                if (f4 < -85)
                {
                    f4 = -85;
                }

                if (f4 >= 85)
                {
                    f4 = 85;
                }

                f2 = f3 - f4;

                if (f4 * f4 > 2500)
                {
                    f2 += f4 * 0.2F;
                }
            }

            float f13 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            renderLivingAt(entity, x, y, z);
            f4 = handleRotationFloat(entity, partialTicks);
            rotateCorpse(entity, f4, f2, partialTicks);
            float f5 = 0.0625F;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(-1, -1, 1);
            preRenderCallback(entity, partialTicks);
            GL11.glTranslatef(0, -24 * f5 - 0.0078125F, 0);
            float f6 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            float f7 = entity.limbSwing - entity.limbSwingAmount * (1 - partialTicks);

            if (entity.isChild())
            {
                f7 *= 3;
            }

            if (f6 > 1)
            {
                f6 = 1;
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            mainModel.setLivingAnimations(entity, f7, f6, partialTicks);
            renderModel(entity, f7, f6, f4, f3 - f2, f13, f5);
            int j;
            float f8;
            float f9;
            float f10;

            for (int i = 0; i < 4; ++i)
            {
                j = shouldRenderPass(entity, i, partialTicks);

                if (j > 0)
                {
                    renderPassModel.setLivingAnimations(entity, f7, f6, partialTicks);
                    renderPassModel.render(entity, f7, f6, f4, f3 - f2, f13, f5);

                    if ((j & 240) == 16)
                    {
                        func_82408_c(entity, i, partialTicks);
                        renderPassModel.render(entity, f7, f6, f4, f3 - f2, f13, f5);
                    }

                    if ((j & 15) == 15)
                    {
                        f8 = entity.ticksExisted + partialTicks;
                        bindTexture(TextureHelper.RES_ITEM_GLINT);
                        GL11.glEnable(GL11.GL_BLEND);
                        f9 = 0.5F;
                        GL11.glColor4f(f9, f9, f9, 1);
                        GL11.glDepthFunc(GL11.GL_EQUAL);
                        GL11.glDepthMask(false);

                        for (int k = 0; k < 2; ++k)
                        {
                            GL11.glDisable(GL11.GL_LIGHTING);
                            f10 = 0.76F;
                            GL11.glColor4f(0.5F * f10, 0.25F * f10, 0.8F * f10, 1);
                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                            GL11.glMatrixMode(GL11.GL_TEXTURE);
                            GL11.glLoadIdentity();
                            float f11 = f8 * (0.001F + k * 0.003F) * 20;
                            float f12 = 0.33333334F;
                            GL11.glScalef(f12, f12, f12);
                            GL11.glRotatef(30 - k * 60, 0, 0, 1);
                            GL11.glTranslatef(0, f11, 0);
                            GL11.glMatrixMode(GL11.GL_MODELVIEW);
                            renderPassModel.render(entity, f7, f6, f4, f3 - f2, f13, f5);
                        }

                        GL11.glColor4f(1, 1, 1, 1);
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glDepthMask(true);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glDepthFunc(GL11.GL_LEQUAL);
                    }

                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                }
            }

            GL11.glDepthMask(true);
            renderEquippedItems(entity, partialTicks);
            float f14 = entity.getBrightness(partialTicks);
            j = getColorMultiplier(entity, f14, partialTicks);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

            if ((j >> 24 & 255) > 0 || entity.hurtTime > 0 || entity.deathTime > 0)
            {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDepthFunc(GL11.GL_EQUAL);

                if (entity.hurtTime > 0 || entity.deathTime > 0)
                {
                    GL11.glColor4f(f14, 0, 0, 0.4F);
                    mainModel.render(entity, f7, f6, f4, f3 - f2, f13, f5);

                    for (int l = 0; l < 4; ++l)
                    {
                        if (inheritRenderPass(entity, l, partialTicks) >= 0)
                        {
                            GL11.glColor4f(f14, 0, 0, 0.4F);
                            renderPassModel.render(entity, f7, f6, f4, f3 - f2, f13, f5);
                        }
                    }
                }

                if ((j >> 24 & 255) > 0)
                {
                    f8 = (j >> 16 & 255) / 255;
                    f9 = (j >> 8 & 255) / 255;
                    float f15 = (j & 255) / 255;
                    f10 = (j >> 24 & 255) / 255;
                    GL11.glColor4f(f8, f9, f15, f10);
                    mainModel.render(entity, f7, f6, f4, f3 - f2, f13, f5);

                    for (int i1 = 0; i1 < 4; ++i1)
                    {
                        if (inheritRenderPass(entity, i1, partialTicks) >= 0)
                        {
                            GL11.glColor4f(f8, f9, f15, f10);
                            renderPassModel.render(entity, f7, f6, f4, f3 - f2, f13, f5);
                        }
                    }
                }

                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
        catch (Exception exception)
        {
            logger.error("Couldn\'t render entity", exception);
        }

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
        passSpecialRender(entity, x, y, z);
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(entity, this, x, y, z));
    }

    protected ResourceLocation getEntityTexture(EntityDisplayMannequin entity)
    {
        return entity.getLocationSkin();
    }

    protected void renderEquippedItems(EntityDisplayMannequin entity, float partialTicks)
    {
        RenderPlayerEvent.Specials.Pre event = new RenderPlayerEvent.Specials.Pre(entity, this, partialTicks);

        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return;
        }

        GL11.glColor3f(1, 1, 1);
        // super.renderEquippedItems(player, partialTicks); // EMPTY
        super.renderArrowsStuckInEntity(entity, partialTicks);
        ItemStack itemstack = entity.inventory.armorItemInSlot(3);

        if (itemstack != null && event.renderHelmet)
        {
            GL11.glPushMatrix();
            modelBipedMain.bipedHead.postRender(0.0625F);
            float f1;

            if (itemstack.getItem() instanceof ItemBlock)
            {
                IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, ItemRenderType.EQUIPPED);
                boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(ItemRenderType.EQUIPPED, itemstack, ItemRendererHelper.BLOCK_3D);

                if (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))
                {
                    f1 = 0.625F;
                    GL11.glTranslatef(0, -0.25F, 0);
                    GL11.glRotatef(90, 0, 1, 0);
                    GL11.glScalef(f1, -f1, -f1);
                }

                renderManager.itemRenderer.renderItem(entity, itemstack, 0);
            }
            else if (itemstack.getItem() == Items.skull)
            {
                f1 = 1.0625F;
                GL11.glScalef(f1, -f1, -f1);
                GameProfile gameprofile = null;

                if (itemstack.hasTagCompound())
                {
                    NBTTagCompound nbttagcompound = itemstack.getTagCompound();

                    if (nbttagcompound.hasKey("SkullOwner", 10))
                    {
                        gameprofile = NBTUtil.func_152459_a(nbttagcompound.getCompoundTag("SkullOwner"));
                    }
                    else if (nbttagcompound.hasKey("SkullOwner", 8) && !StringUtils.isNullOrEmpty(nbttagcompound.getString("SkullOwner")))
                    {
                        gameprofile = new GameProfile((UUID) null, nbttagcompound.getString("SkullOwner"));
                    }
                }

                TileEntitySkullRenderer.field_147536_b.func_152674_a(-0.5F, 0, -0.5F, 1, 180, itemstack.getItemDamage(), gameprofile);
            }

            GL11.glPopMatrix();
        }

        float f2;

        if (entity.getCommandSenderName().equals("deadmau5") && entity.func_152123_o())
        {
            bindTexture(entity.getLocationSkin());

            for (int j = 0; j < 2; ++j)
            {
                float f9 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - (entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialTicks);
                float f10 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                GL11.glPushMatrix();
                GL11.glRotatef(f9, 0, 1, 0);
                GL11.glRotatef(f10, 1, 0, 0);
                GL11.glTranslatef(0.375F * (j * 2 - 1), 0, 0);
                GL11.glTranslatef(0, -0.375F, 0);
                GL11.glRotatef(-f10, 1, 0, 0);
                GL11.glRotatef(-f9, 0, 1, 0);
                f2 = 1.3333334F;
                GL11.glScalef(f2, f2, f2);
                modelBipedMain.renderEars(0.0625F);
                GL11.glPopMatrix();
            }
        }

        boolean flag = entity.func_152122_n();
        flag = event.renderCape && flag;
        float f4;

        if (flag && !entity.isInvisible() && !entity.getHideCape())
        {
            bindTexture(entity.getLocationCape());
            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, 0.125F);
            double d3 = entity.field_71091_bM + (entity.field_71094_bP - entity.field_71091_bM) * partialTicks - (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks);
            double d4 = entity.field_71096_bN + (entity.field_71095_bQ - entity.field_71096_bN) * partialTicks - (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks);
            double d0 = entity.field_71097_bO + (entity.field_71085_bR - entity.field_71097_bO) * partialTicks - (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks);
            f4 = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialTicks;
            double d1 = MathHelper.sin(f4 * (float) Math.PI / 180);
            double d2 = -MathHelper.cos(f4 * (float) Math.PI / 180);
            float f5 = (float) d4 * 10;

            if (f5 < -6)
            {
                f5 = -6;
            }

            if (f5 > 32)
            {
                f5 = 32;
            }

            float f6 = (float) (d3 * d1 + d0 * d2) * 100;
            float f7 = (float) (d3 * d2 - d0 * d1) * 100;

            if (f6 < 0)
            {
                f6 = 0;
            }

            float f8 = entity.prevCameraYaw + (entity.cameraYaw - entity.prevCameraYaw) * partialTicks;
            f5 += MathHelper.sin((entity.prevDistanceWalkedModified + (entity.distanceWalkedModified - entity.prevDistanceWalkedModified) * partialTicks) * 6) * 32 * f8;

            if (entity.isSneaking())
            {
                f5 += 25;
            }

            GL11.glRotatef(6 + f6 / 2 + f5, 1, 0, 0);
            GL11.glRotatef(f7 / 2, 0, 0, 1);
            GL11.glRotatef(-f7 / 2, 0, 1, 0);
            GL11.glRotatef(180, 0, 1, 0);
            modelBipedMain.renderCloak(0.0625F);
            GL11.glPopMatrix();
        }

        ItemStack itemstack1 = entity.inventory.getCurrentItem();

        if (itemstack1 != null && event.renderItem)
        {
            GL11.glPushMatrix();
            modelBipedMain.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

            if (entity.fishEntity != null)
            {
                itemstack1 = new ItemStack(Items.stick);
            }

            EnumAction enumaction = null;

            if (entity.getItemInUseCount() > 0)
            {
                enumaction = itemstack1.getItemUseAction();
            }

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack1, ItemRenderType.EQUIPPED);
            boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(ItemRenderType.EQUIPPED, itemstack1, ItemRendererHelper.BLOCK_3D);

            if (is3D || itemstack1.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack1.getItem()).getRenderType()))
            {
                f2 = 0.5F;
                GL11.glTranslatef(0, 0.1875F, -0.3125F);
                f2 *= 0.75F;
                GL11.glRotatef(20, 1, 0, 0);
                GL11.glRotatef(45, 0, 1, 0);
                GL11.glScalef(-f2, -f2, f2);
            }
            else if (itemstack1.getItem() == Items.bow)
            {
                f2 = 0.625F;
                GL11.glTranslatef(0, 0.125F, 0.3125F);
                GL11.glRotatef(-20, 0, 1, 0);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100, 1, 0, 0);
                GL11.glRotatef(45, 0, 1, 0);
            }
            else if (itemstack1.getItem().isFull3D())
            {
                f2 = 0.625F;

                if (itemstack1.getItem().shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180, 0, 0, 1);
                    GL11.glTranslatef(0, -0.125F, 0);
                }

                if (entity.getItemInUseCount() > 0 && enumaction == EnumAction.block)
                {
                    GL11.glTranslatef(0.05F, 0, -0.1F);
                    GL11.glRotatef(-50, 0, 1, 0);
                    GL11.glRotatef(-10, 1, 0, 0);
                    GL11.glRotatef(-60, 0, 0, 1);
                }

                GL11.glTranslatef(0, 0.1875F, 0);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100, 1, 0, 0);
                GL11.glRotatef(45, 0, 1, 0);
            }
            else
            {
                f2 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(f2, f2, f2);
                GL11.glRotatef(60, 0, 0, 1);
                GL11.glRotatef(-90, 1, 0, 0);
                GL11.glRotatef(20, 0, 0, 1);
            }

            float f3;
            int k;
            float f12;

            if (itemstack1.getItem().requiresMultipleRenderPasses())
            {
                for (k = 0; k < itemstack1.getItem().getRenderPasses(itemstack1.getItemDamage()); ++k)
                {
                    int i = itemstack1.getItem().getColorFromItemStack(itemstack1, k);
                    f12 = (i >> 16 & 255) / 255;
                    f3 = (i >> 8 & 255) / 255;
                    f4 = (i & 255) / 255;
                    GL11.glColor4f(f12, f3, f4, 1);
                    renderManager.itemRenderer.renderItem(entity, itemstack1, k);
                }
            }
            else
            {
                k = itemstack1.getItem().getColorFromItemStack(itemstack1, 0);
                float f11 = (k >> 16 & 255) / 255;
                f12 = (k >> 8 & 255) / 255;
                f3 = (k & 255) / 255;
                GL11.glColor4f(f11, f12, f3, 1);
                renderManager.itemRenderer.renderItem(entity, itemstack1, 0);
            }

            GL11.glPopMatrix();
        }

        MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Specials.Post(entity, this, partialTicks));
    }

    protected void preRenderCallback(EntityDisplayMannequin entity, float partialTicks)
    {
        float f1 = 0.9375F;
        GL11.glScalef(f1, f1, f1);
    }

    protected void renderNametag(EntityDisplayMannequin entity, double x, double y, double z, String text, float yOffset, double dist)
    {
        if (dist < 100.0)
        {
            Scoreboard scoreboard = entity.getWorldScoreboard();
            ScoreObjective scoreobjective = scoreboard.func_96539_a(2);

            if (scoreobjective != null)
            {
                Score score = scoreboard.func_96529_a(entity.getCommandSenderName(), scoreobjective);

                if (entity.isPlayerSleeping())
                {
                    func_147906_a(entity, score.getScorePoints() + " " + scoreobjective.getDisplayName(), x, y - 1.5D, z, 64);
                }
                else
                {
                    func_147906_a(entity, score.getScorePoints() + " " + scoreobjective.getDisplayName(), x, y, z, 64);
                }

                y += getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * yOffset;
            }
        }

        if (entity.isPlayerSleeping())
        {
            func_147906_a(entity, text, x, y - 1.5D, z, 64);
        }
        else
        {
            func_147906_a(entity, text, x, y, z, 64);
        }
    }

    @Override
    public void renderFirstPersonArm(EntityPlayer player)
    {
        float f = 1;
        GL11.glColor3f(f, f, f);
        modelBipedMain.onGround = 0;
        modelBipedMain.setRotationAngles(0, 0, 0, 0, 0, 0.0625F, player);
        modelBipedMain.bipedRightArm.render(0.0625F);
    }

    protected void renderLivingAt(EntityDisplayMannequin entity, double x, double y, double z)
    {
        if (entity.isEntityAlive() && entity.isPlayerSleeping())
        {
            GL11.glTranslatef((float) x + entity.field_71079_bU, (float) y + entity.field_71082_cx, (float) z + entity.field_71089_bV);
        }
        else
        {
            GL11.glTranslatef((float) x, (float) y, (float) z);
        }
    }

    protected void rotateCorpse(EntityDisplayMannequin entity, float f, float f1, float partialTicks)
    {
        if (entity.isEntityAlive() && entity.isPlayerSleeping())
        {
            GL11.glRotatef(entity.getBedOrientationInDegrees(), 0, 1, 0);
            GL11.glRotatef(getDeathMaxRotation(entity), 0, 0, 1);
            GL11.glRotatef(270, 0, 1, 0);
        }
        else
        {
            GL11.glRotatef(180 - f1, 0, 1, 0);

            if (entity.deathTime > 0)
            {
                float f3 = (entity.deathTime + partialTicks - 1) / 20 * 1.6F;
                f3 = MathHelper.sqrt_float(f3);

                if (f3 > 1)
                {
                    f3 = 1;
                }

                GL11.glRotatef(f3 * getDeathMaxRotation(entity), 0, 0, 1);
            }
            else
            {
                String s = EnumChatFormatting.getTextWithoutFormattingCodes(entity.getCommandSenderName());

                if ((s.equals("Dinnerbone") || s.equals("Grumm")) && (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).getHideCape()))
                {
                    GL11.glTranslatef(0, entity.height + 0.1F, 0);
                    GL11.glRotatef(180, 0, 0, 1);
                }
            }
        }
    }

    @Override
    protected void func_96449_a(EntityLivingBase entity, double x, double y, double z, String text, float yOffset, double dist)
    {
        renderNametag((EntityDisplayMannequin) entity, x, y, z, text, yOffset, dist);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTicks)
    {
        preRenderCallback((EntityDisplayMannequin) entity, partialTicks);
    }

    @Override
    protected void func_82408_c(EntityLivingBase entity, int slot, float partialTicks)
    {
        bindArmorOverlayTexture((EntityDisplayMannequin) entity, slot, partialTicks);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int slot, float partialTicks)
    {
        return shouldRenderPass((EntityDisplayMannequin) entity, slot, partialTicks);
    }

    @Override
    protected void renderEquippedItems(EntityLivingBase entity, float partialTicks)
    {
        renderEquippedItems((EntityDisplayMannequin) entity, partialTicks);
    }

    @Override
    protected void rotateCorpse(EntityLivingBase entity, float f, float f1, float partialTicks)
    {
        rotateCorpse((EntityDisplayMannequin) entity, f, f1, partialTicks);
    }

    @Override
    protected void renderLivingAt(EntityLivingBase entity, double x, double y, double z)
    {
        renderLivingAt((EntityDisplayMannequin) entity, x, y, z);
    }

    @Override
    public void doRender(AbstractClientPlayer player, double x, double y, double z, float f, float partialTicks)
    {
        doRender((EntityDisplayMannequin) player, x, y, z, f, partialTicks);
    }

    @Override
    public void doRender(EntityLivingBase entity, double x, double y, double z, float f, float partialTicks)
    {
        doRender((EntityDisplayMannequin) entity, x, y, z, f, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return getEntityTexture((EntityDisplayMannequin) entity);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        doRender((EntityDisplayMannequin) entity, x, y, z, f, partialTicks);
    }
}
