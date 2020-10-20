package com.fiskmods.heroes.common;

import net.minecraft.item.ItemStack;

public interface IOffhandRender
{
    ItemStack getItemToRenderSH();

    void setItemToRenderSH(ItemStack itemstack);

    float getEquippedProgressSH();

    void setEquippedProgressSH(float progress);

    float getPrevEquippedProgressSH();

    void setPrevEquippedProgressSH(float progress);
}
