package com.fiskmods.heroes.client.render.item;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.client.model.item.ModelTonfa;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.event.ClientEventHandler;
import com.fiskmods.heroes.util.SHEnumHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public enum RenderItemBlackCanarysTonfas implements IItemRenderer
{
    INSTANCE;

    private static final ResourceLocation TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/models/tonfa.png");
    private static final ModelTonfa MODEL = new ModelTonfa();

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
        float f = type == SHEnumHelper.EQUIPPED_FIRST_PERSON_OFFHAND || type == SHEnumHelper.EQUIPPED_OFFHAND ? -1 : 1;

        if (data.length > 1 && data[1] instanceof EntityPlayer)
        {
            player = (EntityPlayer) data[1];
        }

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == SHEnumHelper.EQUIPPED_FIRST_PERSON_OFFHAND)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(85, 0, 1, 0);
            GL11.glRotatef(150, 1, 0, 0);
            GL11.glRotatef(-2, 0, 0, 1);
            GL11.glTranslatef(0.05F, 0.1F, -0.75F);
            GL11.glRotatef(-5, 1, 0, 0);

            GL11.glRotatef(-180 * f, 0, 1, 0);

            if (player != null)
            {
                if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
                {
                    float f1 = player.getSwingProgress(ClientEventHandler.renderTick);

                    if (!player.isSwingInProgress && f1 == 0)
                    {
                        f1 = 1;
                    }

                    f1 = Math.min(f1 * 1.5F, 1);

                    if (SHData.RIGHT_TONFA_STATE.get(player))
                    {
                        GL11.glRotatef(-180 * f1, 0, 1, 0);
                    }
                    else
                    {
                        GL11.glRotatef(-180 * f1 + 180, 0, 1, 0);
                    }
                }
                else
                {
                    float f1 = SHRenderHooks.getSwingProgress(player, ClientEventHandler.renderTick);

                    if (!SHData.IS_SWING_IN_PROGRESS.get(player) && f1 == 0)
                    {
                        f1 = 1;
                    }

                    f1 = Math.min(f1 * 1.5F, 1);

                    if (SHData.LEFT_TONFA_STATE.get(player))
                    {
                        GL11.glRotatef(180 * f1, 0, 1, 0);
                    }
                    else
                    {
                        GL11.glRotatef(180 * f1 + 180, 0, 1, 0);
                    }
                }
            }

            float scale = 1.4F;
            GL11.glScalef(scale, scale, scale);
            render(item);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.EQUIPPED || type == SHEnumHelper.EQUIPPED_OFFHAND)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(85, 0, 1, 0);
            GL11.glRotatef(142, 1, 0, 0);
            GL11.glRotatef(-2, 0, 0, 1);
            GL11.glTranslatef(0.05F, 0.4F, -0.725F);

            if (type == SHEnumHelper.EQUIPPED_OFFHAND)
            {
                GL11.glTranslatef(0.03F, 0.0F, 0.0F);
            }

            GL11.glRotatef(-180 * f, 0, 1, 0);

            if (player != null)
            {
                if (type == ItemRenderType.EQUIPPED)
                {
                    float f1 = player.getSwingProgress(ClientEventHandler.renderTick);

                    if (!player.isSwingInProgress && f1 == 0)
                    {
                        f1 = 1;
                    }

                    f1 = Math.min(f1 * 1.5F, 1);

                    if (SHData.RIGHT_TONFA_STATE.get(player))
                    {
                        GL11.glRotatef(180 * f1, 0, 1, 0);
                    }
                    else
                    {
                        GL11.glRotatef(180 * f1 + 180, 0, 1, 0);
                    }
                }
                else
                {
                    float f1 = SHData.SWING_PROGRESS.interpolate(player);

                    if (!SHData.IS_SWING_IN_PROGRESS.get(player) && f1 == 0)
                    {
                        f1 = 1;
                    }

                    f1 = Math.min(f1 * 1.5F, 1);

                    if (SHData.LEFT_TONFA_STATE.get(player))
                    {
                        GL11.glRotatef(-180 * f1, 0, 1, 0);
                    }
                    else
                    {
                        GL11.glRotatef(-180 * f1 + 180, 0, 1, 0);
                    }
                }
            }

            float scale = 1.2F;
            GL11.glScalef(scale, scale, scale);
            render(item);
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glPushMatrix();
            GL11.glRotatef(-90, 0, 1, 0);
            GL11.glTranslatef(0, 0, -0.2F);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.4F, 0, 0);

            float scale = 1.7F;
            GL11.glScalef(scale, -scale, -scale);
            render(item);
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(-0.4F, 0, 0);
            GL11.glScalef(scale, -scale, -scale);
            render(item);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
        else if (type == ItemRenderType.ENTITY)
        {
            float scale = 1.8F;

            GL11.glPushMatrix();
            GL11.glRotatef(-90, 0, 1, 0);
            GL11.glTranslatef(0.25F, 0, -0.2F);
            GL11.glScalef(scale, -scale, -scale);
            render(item);
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glRotatef(-90, 0, 1, 0);
            GL11.glTranslatef(-0.25F, 0, -0.2F);
            GL11.glScalef(scale, -scale, -scale);
            render(item);
            GL11.glPopMatrix();
        }
    }

    public static void render(ItemStack item)
    {
        render(item != null && item.hasEffect(0));
    }

    public static void render(boolean enchanted)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        MODEL.render();

        if (enchanted)
        {
            SHRenderHooks.renderEnchanted(MODEL::render);
        }
    }
}
