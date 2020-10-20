package com.fiskmods.heroes.client.render.tile;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.model.tile.ModelMannequin;
import com.fiskmods.heroes.common.event.ClientEventHandler;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemDye;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderDisplayStand extends TileEntitySpecialRenderer
{
    private static final ResourceLocation GLOW = new ResourceLocation(FiskHeroes.MODID, "textures/models/tachyon_device_glow.png");

    private final ModelBiped model1 = new ModelMannequin(0.5F, false);
    private final ModelBiped model2 = new ModelMannequin(0.5F, true);
    private final ModelBiped model3 = new ModelMannequin(0.025F, true);
    private final ResourceLocation[] textures;

    public RenderDisplayStand()
    {
        textures = new ResourceLocation[ItemDye.field_150921_b.length];
        int j = textures.length - 1;

        for (int i = 0; i < textures.length; ++i)
        {
            textures[i] = new ResourceLocation(FiskHeroes.MODID, String.format("textures/models/tiles/display_mannequin_%s.png", ItemDye.field_150921_b[MathHelper.clamp_int(j - i, 0, j)]));
        }
    }

    public void render(TileEntityDisplayStand tile, double x, double y, double z, float partialTicks)
    {
        int metadata = 0;

        if (tile.getWorldObj() != null)
        {
            metadata = tile.getBlockMetadata();
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1F, -1F, -1F);
        GL11.glRotatef(metadata * 45, 0, 1, 0);

        if (metadata < 8)
        {
            if (tile.hasCustomInventoryName())
            {
                ResourceLocation location = AbstractClientPlayer.locationStevePng;

                if (tile.getUsername() != null)
                {
                    Minecraft mc = Minecraft.getMinecraft();
                    Map map = mc.func_152342_ad().func_152788_a(tile.getUsername());

                    if (map.containsKey(Type.SKIN))
                    {
                        location = mc.func_152342_ad().func_152792_a((MinecraftProfileTexture) map.get(Type.SKIN), Type.SKIN);
                    }
                }

                bindTexture(location);
            }
            else
            {
                bindTexture(textures[MathHelper.clamp_int(tile.getColor(), 0, ItemDye.field_150921_b.length - 1)]);
            }

            if (tile.getCasing().hasBaseplate())
            {
                GL11.glTranslatef(0, -0.0625F, 0);
            }

            try
            {
                ModelBiped model = tile.hasCustomInventoryName() ? tile.fixHatLayer ? model3 : model2 : model1;
                float scale = 0.9375F;

                GL11.glPushMatrix();
                GL11.glScalef(scale, scale, scale);
                GL11.glTranslatef(0, 0.0625F * 1.475F, 0);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glEnable(GL11.GL_BLEND);
                model.isChild = false;
                model.render(tile.fakePlayer, 0, 0, 0, 0, 0, 0.0625F);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();

                if (tile.fakePlayer != null && FiskServerUtils.nonNull(tile.getItemStacks()) != null)
                {
                    GL11.glPushMatrix();
                    GL11.glRotatef(180, 1, 0, 0);
                    GL11.glTranslatef(0, -1.5F, 0);
                    RenderManager.instance.renderEntityWithPosYaw(tile.fakePlayer, 0, 0, 0, 0, ClientEventHandler.renderTick);
                    GL11.glPopMatrix();
                }
            }
            catch (Exception e)
            {
            }
        }

        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks)
    {
        render((TileEntityDisplayStand) tile, x, y, z, partialTicks);
    }
}
