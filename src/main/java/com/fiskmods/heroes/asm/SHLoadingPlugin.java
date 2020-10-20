package com.fiskmods.heroes.asm;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.fiskmods.heroes.asm.transformers.CTBlock;
import com.fiskmods.heroes.asm.transformers.CTBlockButton;
import com.fiskmods.heroes.asm.transformers.CTBlockLiquid;
import com.fiskmods.heroes.asm.transformers.CTBlockPane;
import com.fiskmods.heroes.asm.transformers.CTBlockPressurePlate;
import com.fiskmods.heroes.asm.transformers.CTBowRenderer;
import com.fiskmods.heroes.asm.transformers.CTEffectRenderer;
import com.fiskmods.heroes.asm.transformers.CTEntity;
import com.fiskmods.heroes.asm.transformers.CTEntityLivingBase;
import com.fiskmods.heroes.asm.transformers.CTEntityOtherPlayerMP;
import com.fiskmods.heroes.asm.transformers.CTEntityPlayer;
import com.fiskmods.heroes.asm.transformers.CTEntityPlayerSP;
import com.fiskmods.heroes.asm.transformers.CTEntityRenderer;
import com.fiskmods.heroes.asm.transformers.CTFoodStats;
import com.fiskmods.heroes.asm.transformers.CTGuiInventory;
import com.fiskmods.heroes.asm.transformers.CTItemBow;
import com.fiskmods.heroes.asm.transformers.CTItemRenderer;
import com.fiskmods.heroes.asm.transformers.CTItemStack;
import com.fiskmods.heroes.asm.transformers.CTMinecraftServer;
import com.fiskmods.heroes.asm.transformers.CTModelBiped;
import com.fiskmods.heroes.asm.transformers.CTNetHandlerPlayServer;
import com.fiskmods.heroes.asm.transformers.CTRenderBiped;
import com.fiskmods.heroes.asm.transformers.CTRenderGlobal;
import com.fiskmods.heroes.asm.transformers.CTRenderItem;
import com.fiskmods.heroes.asm.transformers.CTRenderPlayer;
import com.fiskmods.heroes.asm.transformers.CTWorld;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@MCVersion("1.7.10")
@TransformerExclusions("com.fiskmods.heroes.asm")
public class SHLoadingPlugin implements IFMLLoadingPlugin
{
    private static final String[] SERVER_TRANSFORMERS = new String[] {CTBlockButton.class.getName(), CTBlockLiquid.class.getName(), CTBlockPane.class.getName(), CTBlockPressurePlate.class.getName(), CTEntity.class.getName(), CTEntityLivingBase.class.getName(), CTEntityPlayer.class.getName(), CTFoodStats.class.getName(), CTItemBow.class.getName(), CTItemStack.class.getName(), CTMinecraftServer.class.getName(), CTWorld.class.getName(), CTNetHandlerPlayServer.class.getName()};

    public static File source;

    public List<String> setupTransformers(List<String> list)
    {
        if (FMLLaunchHandler.side().isClient())
        {
            list.add(CTBlock.class.getName());
            list.add(CTBowRenderer.class.getName());
            list.add(CTEntityOtherPlayerMP.class.getName());
            list.add(CTEntityPlayerSP.class.getName());
            list.add(CTEntityRenderer.class.getName());
            list.add(CTEffectRenderer.class.getName());
            list.add(CTGuiInventory.class.getName());
            list.add(CTItemRenderer.class.getName());
            list.add(CTModelBiped.class.getName());
            list.add(CTRenderBiped.class.getName());
            list.add(CTRenderGlobal.class.getName());
            list.add(CTRenderItem.class.getName());
            list.add(CTRenderPlayer.class.getName());
        }

        return list;
    }

    @Override
    public String[] getASMTransformerClass()
    {
        List<String> list = setupTransformers(Lists.newArrayList(SERVER_TRANSFORMERS));
        return list.toArray(new String[list.size()]);
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }

    @Override
    public String getModContainerClass()
    {
        return "com.fiskmods.heroes.FiskHeroesCore";
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        SHTranslator.obfuscatedEnv = Boolean.class.cast(data.get("runtimeDeobfuscationEnabled"));
        source = (File) data.get("coremodLocation");

        if (source == null)
        {
            source = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        }
    }
}
