package com.fiskmods.heroes.common.proxy;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHReflection;
import com.fiskmods.heroes.client.gui.SHGuiHandler;
import com.fiskmods.heroes.common.achievement.SHAchievements;
import com.fiskmods.heroes.common.arrowtype.ArrowTypeManager;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.enchantment.SHEnchantments;
import com.fiskmods.heroes.common.entity.SHEntities;
import com.fiskmods.heroes.common.event.CommonEventHandler;
import com.fiskmods.heroes.common.event.TickrateHandler;
import com.fiskmods.heroes.common.interaction.InteractionHandler;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.common.predicate.SHPredicates;
import com.fiskmods.heroes.common.recipe.SHRecipes;
import com.fiskmods.heroes.common.world.ModDimensions;
import com.fiskmods.heroes.common.world.gen.ModChestGen;
import com.fiskmods.heroes.common.world.gen.SHStructures;
import com.fiskmods.heroes.common.world.gen.SHWorldGen;
import com.fiskmods.heroes.pack.JSHeroesEngine;
import com.fiskmods.heroes.util.RewardHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy
{
    public void preInit()
    {
        Rule.init();
        SHData.init();
        SHReflection.common();
        SHNetworkManager.registerPackets();

        ArrowTypeManager.register();
        ModDimensions.register();
        StatEffect.register();

        registerEventHandler(JSHeroesEngine.INSTANCE);
        registerEventHandler(TickrateHandler.INSTANCE);
        registerEventHandler(CommonEventHandler.INSTANCE);
//        registerEventHandler(MoveCommonHandler.INSTANCE); // TODO: 1.4 Combat
        NetworkRegistry.INSTANCE.registerGuiHandler(FiskHeroes.MODID, new SHGuiHandler());

        new Thread(RewardHelper::fetchStats).start();
    }

    public void init()
    {
        ModItems.register();
        ModBlocks.register();
        ModChestGen.register();

        SHRecipes.register();
        SHEntities.register();
        SHAchievements.register();
        SHEnchantments.register();
        SHPredicates.register();
        SHStructures.register();
        SHWorldGen.register();

        InteractionHandler.register();
    }

    public void registerEventHandler(Object obj)
    {
        MinecraftForge.EVENT_BUS.register(obj);
        FMLCommonHandler.instance().bus().register(obj);
    }

    public EntityPlayer getPlayer()
    {
        return null;
    }

    public boolean isClientPlayer(Entity entity)
    {
        return false;
    }

    public float getRenderTick()
    {
        return 0;
    }

    public MovingObjectPosition getMouseOver(float renderTick, float blockReachDistance)
    {
        return null;
    }

    public void playSound(EntityLivingBase entity, String sound, float volume, float pitch, int... args)
    {
    }
}
