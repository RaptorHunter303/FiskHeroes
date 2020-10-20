package com.fiskmods.heroes.pack;

import com.fiskmods.heroes.FiskHeroes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

/**
 * Split from TextureMap for independent runtime reloading of Hero item textures
 */
@SideOnly(Side.CLIENT)
public class HeroTextureMap extends TextureMap
{
    public static final ResourceLocation LOCATION = new ResourceLocation("textures/atlas/fiskheroes.png");
    public static final int ID = FiskHeroes.MODID.hashCode();

    private static HeroTextureMap instance;

    private HeroTextureMap()
    {
        super(ID, "textures/items/heroes", true);
        instance = this;
    }

    public static HeroTextureMap getInstance()
    {
        return instance;
    }

    public static void register()
    {
        JSHeroesEngine.LOGGER.info("Loading Hero Items Texture");
        Minecraft.getMinecraft().getTextureManager().loadTextureMap(LOCATION, new HeroTextureMap());
    }
}
