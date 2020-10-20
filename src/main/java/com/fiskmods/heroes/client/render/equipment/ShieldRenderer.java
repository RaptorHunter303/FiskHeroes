package com.fiskmods.heroes.client.render.equipment;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.render.item.RenderItemCapsShield;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.SHHelper;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public enum ShieldRenderer implements EquipmentRenderer
{
    INSTANCE;

    @Override
    public boolean test(EntityPlayer player)
    {
        ItemStack stack = player.getHeldItem();
        return SHData.HAS_CPT_AMERICAS_SHIELD.get(player) > 0 && (stack == null || stack.getItem() != ModItems.captainAmericasShield || player instanceof EntityDisplayMannequin);
    }

    @Override
    public float[] getOffset(EntityPlayer player, HeroIteration iter, ModelBiped model, float partialTicks)
    {
        return new float[] {0, -0.15F - (SHHelper.getHeroFromArmor(player, 2) != null ? 0.025F : player.getCurrentArmor(2) != null ? 0.05F : 0), -0.375F};
    }

    @Override
    public void render(EntityPlayer player, HeroIteration iter, ModelBiped model, RenderEquipmentEvent event, float partialTicks)
    {
        model.bipedBody.postRender(0.0625F);
        GL11.glRotatef(-90, 1, 0, 0);
        GL11.glRotatef(180, 0, 1, 0);
        GL11.glTranslatef(event.xOffset, event.yOffset, event.zOffset);

        byte b = SHData.HAS_CPT_AMERICAS_SHIELD.get(player);
        RenderItemCapsShield.render(false, (b & 2) == 2, (b & 4) == 4);
    }
}
