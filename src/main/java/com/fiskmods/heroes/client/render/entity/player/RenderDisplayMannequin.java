package com.fiskmods.heroes.client.render.entity.player;

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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class RenderDisplayMannequin extends RenderPlayer
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final int PASS_ENCHANT_GLINT = 0xF;
    public static final int PASS_OVERLAY = 0x10;

    protected void doRender(EntityDisplayMannequin entity, double x, double y, double z, float f, float partialTicks)
    {
        if (MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Pre(entity, this, partialTicks)))
        {
            return;
        }

        modelArmorChestplate.aimedBow = modelArmor.aimedBow = modelBipedMain.aimedBow = false;
        modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = false;
        modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight = 0;

        GL11.glColor3f(1, 1, 1);
        doRenderSuper(entity, x, y - entity.yOffset, z, f, partialTicks);
        MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Post(entity, this, partialTicks));
    }

    protected void doRenderSuper(EntityDisplayMannequin entity, double x, double y, double z, float f, float partialTicks)
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

        mainModel.isRiding = false;

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
            float ticks = handleRotationFloat(entity, partialTicks);
            renderLivingAt(entity, x, y, z);
            rotateCorpse(entity, ticks, 0, partialTicks);

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(-1, -1, 1);
            preRenderCallback(entity, partialTicks);
            GL11.glTranslatef(0, -24 * 0.0625F - 0.0078125F, 0);
            mainModel.setLivingAnimations(entity, 0, 0, partialTicks);
            renderModel(entity, 0, 0, ticks, 0, 0, 0.0625F);

            for (int slot = 0; slot < 4; ++slot)
            {
                int pass = shouldRenderPass(entity, slot, partialTicks);

                if (pass > 0)
                {
                    renderPassModel.setLivingAnimations(entity, 0, 0, partialTicks);
                    renderPassModel.render(entity, 0, 0, ticks, 0, 0, 0.0625F);

                    if ((pass & 0xF0) == PASS_OVERLAY)
                    {
                        func_82408_c(entity, slot, partialTicks);
                        renderPassModel.render(entity, 0, 0, ticks, 0, 0, 0.0625F);
                    }

                    if ((pass & PASS_ENCHANT_GLINT) == PASS_ENCHANT_GLINT)
                    {
                        float scale1 = 1F / 3;
                        bindTexture(TextureHelper.RES_ITEM_GLINT);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glDepthFunc(GL11.GL_EQUAL);
                        GL11.glDepthMask(false);

                        for (int layer = 0; layer < 2; ++layer)
                        {
                            GL11.glDisable(GL11.GL_LIGHTING);
                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                            GL11.glMatrixMode(GL11.GL_TEXTURE);
                            GL11.glLoadIdentity();
                            GL11.glScalef(scale1, scale1, scale1);
                            GL11.glRotatef(30 - layer * 60, 0, 0, 1);
                            GL11.glTranslatef(0, ticks * (0.001F + layer * 0.003F) * 20, 0);
                            GL11.glMatrixMode(GL11.GL_MODELVIEW);
                            GL11.glColor4f(0.38F, 0.19F, 0.608F, 1);
                            renderPassModel.render(entity, 0, 0, ticks, 0, 0, 0.0625F);
                        }

                        GL11.glColor4f(1, 1, 1, 1);
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glDepthMask(true);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        GL11.glDepthFunc(GL11.GL_LEQUAL);
                    }

                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                }
            }

            GL11.glDepthMask(true);
            renderEquippedItems(entity, partialTicks);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
        catch (Exception e)
        {
            LOGGER.error("Couldn\'t render entity", e);
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();

        passSpecialRender(entity, x, y, z);
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(entity, this, x, y, z));
    }

    protected void renderEquippedItems(EntityDisplayMannequin entity, float partialTicks)
    {
        RenderPlayerEvent.Specials.Pre event = new RenderPlayerEvent.Specials.Pre(entity, this, partialTicks);

        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return;
        }

        GL11.glColor3f(1, 1, 1);
        ItemStack helmet = entity.inventory.armorItemInSlot(3);

        if (helmet != null && event.renderHelmet)
        {
            GL11.glPushMatrix();
            modelBipedMain.bipedHead.postRender(0.0625F);

            if (helmet.getItem() instanceof ItemBlock)
            {
                IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(helmet, ItemRenderType.EQUIPPED);
                boolean is3D = renderer != null && renderer.shouldUseRenderHelper(ItemRenderType.EQUIPPED, helmet, ItemRendererHelper.BLOCK_3D);

                if (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(helmet.getItem()).getRenderType()))
                {
                    float scale = 0.625F;
                    GL11.glTranslatef(0, -0.25F, 0);
                    GL11.glRotatef(90, 0, 1, 0);
                    GL11.glScalef(scale, -scale, -scale);
                }

                renderManager.itemRenderer.renderItem(entity, helmet, 0);
            }
            else if (helmet.getItem() == Items.skull)
            {
                float scale = 1.0625F;
                GL11.glScalef(scale, -scale, -scale);
                GameProfile profile = null;

                if (helmet.hasTagCompound())
                {
                    NBTTagCompound tag = helmet.getTagCompound();

                    if (tag.hasKey("SkullOwner", 10))
                    {
                        profile = NBTUtil.func_152459_a(tag.getCompoundTag("SkullOwner"));
                    }
                    else if (tag.hasKey("SkullOwner", 8) && !StringUtils.isNullOrEmpty(tag.getString("SkullOwner")))
                    {
                        profile = new GameProfile(null, tag.getString("SkullOwner"));
                    }
                }

                TileEntitySkullRenderer.field_147536_b.func_152674_a(-0.5F, 0, -0.5F, 1, 180, helmet.getItemDamage(), profile);
            }

            GL11.glPopMatrix();
        }

        ItemStack heldItem = entity.inventory.getCurrentItem();

        if (heldItem != null && event.renderItem)
        {
            GL11.glPushMatrix();
            modelBipedMain.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
            EnumAction action = null;

            if (entity.getItemInUseCount() > 0)
            {
                action = heldItem.getItemUseAction();
            }

            IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(heldItem, ItemRenderType.EQUIPPED);
            boolean is3D = renderer != null && renderer.shouldUseRenderHelper(ItemRenderType.EQUIPPED, heldItem, ItemRendererHelper.BLOCK_3D);

            if (is3D || heldItem.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(heldItem.getItem()).getRenderType()))
            {
                float scale = 0.5F;
                GL11.glTranslatef(0, 0.1875F, -0.3125F);
                scale *= 0.75F;
                GL11.glRotatef(20, 1, 0, 0);
                GL11.glRotatef(45, 0, 1, 0);
                GL11.glScalef(-scale, -scale, scale);
            }
            else if (heldItem.getItem() == Items.bow)
            {
                float scale = 0.625F;
                GL11.glTranslatef(0, 0.125F, 0.3125F);
                GL11.glRotatef(-20, 0, 1, 0);
                GL11.glScalef(scale, -scale, scale);
                GL11.glRotatef(-100, 1, 0, 0);
                GL11.glRotatef(45, 0, 1, 0);
            }
            else if (heldItem.getItem().isFull3D())
            {
                float scale = 0.625F;

                if (heldItem.getItem().shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180, 0, 0, 1);
                    GL11.glTranslatef(0, -0.125F, 0);
                }

                if (entity.getItemInUseCount() > 0 && action == EnumAction.block)
                {
                    GL11.glTranslatef(0.05F, 0, -0.1F);
                    GL11.glRotatef(-50, 0, 1, 0);
                    GL11.glRotatef(-10, 1, 0, 0);
                    GL11.glRotatef(-60, 0, 0, 1);
                }

                GL11.glTranslatef(0, 0.1875F, 0);
                GL11.glScalef(scale, -scale, scale);
                GL11.glRotatef(-100, 1, 0, 0);
                GL11.glRotatef(45, 0, 1, 0);
            }
            else
            {
                float scale = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(scale, scale, scale);
                GL11.glRotatef(60, 0, 0, 1);
                GL11.glRotatef(-90, 1, 0, 0);
                GL11.glRotatef(20, 0, 0, 1);
            }

            if (heldItem.getItem().requiresMultipleRenderPasses())
            {
                for (int pass = 0; pass < heldItem.getItem().getRenderPasses(heldItem.getItemDamage()); ++pass)
                {
                    int color = heldItem.getItem().getColorFromItemStack(heldItem, pass);
                    float r = (color >> 16 & 255) / 255;
                    float g = (color >> 8 & 255) / 255;
                    float b = (color & 255) / 255;

                    GL11.glColor4f(r, g, b, 1);
                    renderManager.itemRenderer.renderItem(entity, heldItem, pass);
                }
            }
            else
            {
                int color = heldItem.getItem().getColorFromItemStack(heldItem, 0);
                float r = (color >> 16 & 255) / 255;
                float g = (color >> 8 & 255) / 255;
                float b = (color & 255) / 255;

                GL11.glColor4f(r, g, b, 1);
                renderManager.itemRenderer.renderItem(entity, heldItem, 0);
            }

            GL11.glPopMatrix();
        }

        MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Specials.Post(entity, this, partialTicks));
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

        func_147906_a(entity, text, x, y, z, 64);
    }

    protected void renderLivingAt(EntityDisplayMannequin entity, double x, double y, double z)
    {
        GL11.glTranslatef((float) x, (float) y, (float) z);
    }

    protected void rotateCorpse(EntityDisplayMannequin entity, float f, float f1, float partialTicks)
    {
        GL11.glRotatef(180 - f1, 0, 1, 0);
        String s = EnumChatFormatting.getTextWithoutFormattingCodes(entity.getCommandSenderName());

        if ((s.equals("Dinnerbone") || s.equals("Grumm")) && !(entity instanceof EntityPlayer && ((EntityPlayer) entity).getHideCape()))
        {
            GL11.glTranslatef(0, entity.height + 0.1F, 0);
            GL11.glRotatef(180, 0, 0, 1);
        }
    }

    @Override
    protected void func_96449_a(EntityLivingBase entity, double x, double y, double z, String text, float yOffset, double dist)
    {
        renderNametag((EntityDisplayMannequin) entity, x, y, z, text, yOffset, dist);
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
    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks)
    {
        doRender((EntityDisplayMannequin) entity, x, y, z, f, partialTicks);
    }
}
