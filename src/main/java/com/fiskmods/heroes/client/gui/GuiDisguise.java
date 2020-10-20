package com.fiskmods.heroes.client.gui;

import com.fiskmods.heroes.common.data.SHData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiDisguise extends GuiPickName
{
    public GuiDisguise()
    {
        super(I18n.format("gui.disguise"));
    }

    @Override
    public void onDone(String finalName)
    {
        if (finalName != null && !finalName.isEmpty())
        {
            SHData.SHAPE_SHIFT_TIMER.set(mc.thePlayer, 1.0F);
            SHData.SHAPE_SHIFTING_FROM.set(mc.thePlayer, SHData.DISGUISE.get(mc.thePlayer));
            SHData.SHAPE_SHIFTING_TO.set(mc.thePlayer, finalName);

            if (GuiPickName.recentlyUsed.contains(finalName))
            {
                GuiPickName.recentlyUsed.remove(finalName);
            }

            GuiPickName.recentlyUsed.add(finalName);

            if (GuiPickName.recentlyUsed.size() > 10)
            {
                GuiPickName.recentlyUsed.remove(0);
            }
        }
    }
}
