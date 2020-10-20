package com.fiskmods.heroes.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.config.SyncedConfig;

import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

public class GuiSHModConfig extends GuiConfig
{
    public GuiSHModConfig(GuiScreen parent)
    {
        super(parent, getConfigElements(), FiskHeroes.MODID, false, false, "Fisk's Superheroes Configuration");
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> elements = new ArrayList<>();

        elements.add(categoryElement(SyncedConfig.CATEGORY_SERVER, "Server", "server"));
        elements.add(categoryElement(SHConfig.CATEGORY_CLIENT, "Client", "client"));
        elements.add(categoryElement(SHConfig.CATEGORY_GENERAL, "General", "general"));

        return elements;
    }

    private static IConfigElement categoryElement(String category, String name, String tooltipKey)
    {
        return new DummyConfigElement.DummyCategoryElement(name, tooltipKey, new ConfigElement(SHConfig.configFile.getCategory(category.toLowerCase(Locale.ROOT))).getChildElements());
    }
}
