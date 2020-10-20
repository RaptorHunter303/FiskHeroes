package com.fiskmods.heroes.common.proxy;

import java.util.List;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHReflection;
import com.fiskmods.heroes.client.SHResourceHandler;
import com.fiskmods.heroes.client.gui.GuiOverlay;
import com.fiskmods.heroes.client.gui.book.PageParserSH;
import com.fiskmods.heroes.client.json.hero.TextureGetter;
import com.fiskmods.heroes.client.keybinds.SHKeyBinds;
import com.fiskmods.heroes.client.keybinds.SHKeyHandler;
import com.fiskmods.heroes.client.render.arrow.ArrowRendererManager;
import com.fiskmods.heroes.client.render.block.RenderBlockDisplayStand;
import com.fiskmods.heroes.client.render.block.RenderBlockSCEternium;
import com.fiskmods.heroes.client.render.effect.EffectRenderHandler;
import com.fiskmods.heroes.client.render.entity.RenderBatarang;
import com.fiskmods.heroes.client.render.entity.RenderCactus;
import com.fiskmods.heroes.client.render.entity.RenderCactusSpike;
import com.fiskmods.heroes.client.render.entity.RenderEarthCrack;
import com.fiskmods.heroes.client.render.entity.RenderFireBlast;
import com.fiskmods.heroes.client.render.entity.RenderFreezeGrenade;
import com.fiskmods.heroes.client.render.entity.RenderGrapplingHook;
import com.fiskmods.heroes.client.render.entity.RenderGrapplingHookCable;
import com.fiskmods.heroes.client.render.entity.RenderIcicle;
import com.fiskmods.heroes.client.render.entity.RenderInvisible;
import com.fiskmods.heroes.client.render.entity.RenderIronMan;
import com.fiskmods.heroes.client.render.entity.RenderLaserBolt;
import com.fiskmods.heroes.client.render.entity.RenderLightningCast;
import com.fiskmods.heroes.client.render.entity.RenderRepulsorBlast;
import com.fiskmods.heroes.client.render.entity.RenderSmokePellet;
import com.fiskmods.heroes.client.render.entity.RenderSoundWave;
import com.fiskmods.heroes.client.render.entity.RenderSpeedBlur;
import com.fiskmods.heroes.client.render.entity.RenderSpellDuplicate;
import com.fiskmods.heroes.client.render.entity.RenderSpellWhip;
import com.fiskmods.heroes.client.render.entity.RenderThrowingStar;
import com.fiskmods.heroes.client.render.entity.RenderThrownShield;
import com.fiskmods.heroes.client.render.entity.RenderTrickArrow;
import com.fiskmods.heroes.client.render.entity.player.RenderDisplayMannequin;
import com.fiskmods.heroes.client.render.equipment.EquipmentRenderer;
import com.fiskmods.heroes.client.render.hero.HeroRendererManager;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectManager;
import com.fiskmods.heroes.client.render.item.RenderItemBlackCanarysTonfas;
import com.fiskmods.heroes.client.render.item.RenderItemBoStaff;
import com.fiskmods.heroes.client.render.item.RenderItemCapsShield;
import com.fiskmods.heroes.client.render.item.RenderItemCharged.ChargeType;
import com.fiskmods.heroes.client.render.item.RenderItemChronosRifle;
import com.fiskmods.heroes.client.render.item.RenderItemColdGun;
import com.fiskmods.heroes.client.render.item.RenderItemCompoundBow;
import com.fiskmods.heroes.client.render.item.RenderItemDeadpoolsSwords;
import com.fiskmods.heroes.client.render.item.RenderItemDisplayCase;
import com.fiskmods.heroes.client.render.item.RenderItemDisplayStand;
import com.fiskmods.heroes.client.render.item.RenderItemFlashRing;
import com.fiskmods.heroes.client.render.item.RenderItemGameboii;
import com.fiskmods.heroes.client.render.item.RenderItemGrapplingGun;
import com.fiskmods.heroes.client.render.item.RenderItemHeatGun;
import com.fiskmods.heroes.client.render.item.RenderItemMiniAtomSuit;
import com.fiskmods.heroes.client.render.item.RenderItemPrometheusSword;
import com.fiskmods.heroes.client.render.item.RenderItemQuiver;
import com.fiskmods.heroes.client.render.item.RenderItemRipHuntersGun;
import com.fiskmods.heroes.client.render.item.RenderItemTachyonDevice;
import com.fiskmods.heroes.client.render.item.RenderItemTachyonPrototype;
import com.fiskmods.heroes.client.render.item.RenderItemTreadmill;
import com.fiskmods.heroes.client.render.tile.RenderDisplayStand;
import com.fiskmods.heroes.client.render.tile.RenderParticleCore;
import com.fiskmods.heroes.client.render.tile.RenderTreadmill;
import com.fiskmods.heroes.client.sound.MovingSoundAbsorb;
import com.fiskmods.heroes.client.sound.MovingSoundFlight;
import com.fiskmods.heroes.client.sound.MovingSoundIceCharge;
import com.fiskmods.heroes.client.sound.MovingSoundSpodermen;
import com.fiskmods.heroes.common.achievement.SHAchievements;
import com.fiskmods.heroes.common.achievement.StatStringFormatAllArrows;
import com.fiskmods.heroes.common.achievement.StatStringFormatAllSuits;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.book.Book;
import com.fiskmods.heroes.common.book.widget.WidgetType;
import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.entity.EntityCactus;
import com.fiskmods.heroes.common.entity.EntityCactusSpike;
import com.fiskmods.heroes.common.entity.EntityCanaryCry;
import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;
import com.fiskmods.heroes.common.entity.EntityEarthCrack;
import com.fiskmods.heroes.common.entity.EntityEarthquake;
import com.fiskmods.heroes.common.entity.EntityFireBlast;
import com.fiskmods.heroes.common.entity.EntityGrapplingHookCable;
import com.fiskmods.heroes.common.entity.EntityIcicle;
import com.fiskmods.heroes.common.entity.EntityIronMan;
import com.fiskmods.heroes.common.entity.EntityLaserBolt;
import com.fiskmods.heroes.common.entity.EntityLightningCast;
import com.fiskmods.heroes.common.entity.EntityRepulsorBlast;
import com.fiskmods.heroes.common.entity.EntityShadowDome;
import com.fiskmods.heroes.common.entity.EntitySpeedBlur;
import com.fiskmods.heroes.common.entity.EntitySpellDuplicate;
import com.fiskmods.heroes.common.entity.EntitySpellWhip;
import com.fiskmods.heroes.common.entity.EntityThrownShield;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.entity.gadget.EntityBatarang;
import com.fiskmods.heroes.common.entity.gadget.EntityFreezeGrenade;
import com.fiskmods.heroes.common.entity.gadget.EntityGrapplingHook;
import com.fiskmods.heroes.common.entity.gadget.EntitySmokePellet;
import com.fiskmods.heroes.common.entity.gadget.EntityThrowingStar;
import com.fiskmods.heroes.common.equipment.EquipmentHandler;
import com.fiskmods.heroes.common.event.ClientEventHandler;
import com.fiskmods.heroes.common.event.ClientEventHandlerBG;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.recipe.ManualRecipeVialArrow;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.common.tileentity.TileEntityParticleCore;
import com.fiskmods.heroes.common.tileentity.TileEntityTreadmill;
import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.GameboiiCartridge;
import com.fiskmods.heroes.pack.HeroTextureMap;
import com.fiskmods.heroes.pack.JSHeroesEngine;
import com.fiskmods.heroes.pack.JSHeroesResources;
import com.fiskmods.heroes.util.SHClientUtils;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy
{
    private Minecraft mc = Minecraft.getMinecraft();

    public static GuiOverlay guiOverlay = new GuiOverlay();

    @Override
    public void preInit()
    {
        super.preInit();
        SHReflection.client();
        SHKeyBinds.register();

        Book.registerPageParser(PageParserSH.INSTANCE);
        WidgetType.registerManualRecipe(ManualRecipeVialArrow.INSTANCE);
        TextureGetter.registerFunctions();
        HeroEffectManager.register();

        registerEventHandler(JSHeroesResources.INSTANCE);
        registerEventHandler(ClientEventHandler.INSTANCE);
        registerEventHandler(HeroRendererManager.INSTANCE);
        registerEventHandler(EquipmentHandler.INSTANCE);
        registerEventHandler(SHKeyHandler.INSTANCE);
//        registerEventHandler(MoveClientHandler.INSTANCE); // TODO: 1.4 Combat
        registerEventHandler(guiOverlay);

        if (FiskHeroes.isBattlegearLoaded)
        {
            BattlegearUtils.RENDER_BUS.register(ClientEventHandlerBG.INSTANCE);
        }

        if (SHConfig.addServer)
        {
            new Thread(SHClientUtils::addServer).start();
        }
    }

    @Override
    public void init()
    {
        super.init();
        JSHeroesEngine.INSTANCE.init(Side.CLIENT, null);
        SHAchievements.ALL_SUITS.setStatStringFormatter(StatStringFormatAllSuits.INSTANCE);
        SHAchievements.ALL_ARROWS.setStatStringFormatter(StatStringFormatAllArrows.INSTANCE);

        ArrowRendererManager.register();
        EffectRenderHandler.register();
        EquipmentRenderer.register();
        SHResourceHandler.register();
        HeroTextureMap.register();

        registerEntityRenderers();
        registerItemRenderers();
        registerTileRenderers();

        for (GameboiiCartridge cartridge : GameboiiCartridge.values())
        {
            Gameboii.get(cartridge).register();
        }

        SHResourceHandler.INSTANCE.freezeLoading = false;
    }

    private void registerEntityRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityDisplayMannequin.class, new RenderDisplayMannequin());
        RenderingRegistry.registerEntityRenderingHandler(EntitySpeedBlur.class, new RenderSpeedBlur());
        RenderingRegistry.registerEntityRenderingHandler(EntityTrickArrow.class, new RenderTrickArrow());
        RenderingRegistry.registerEntityRenderingHandler(EntityGrapplingHookCable.class, new RenderGrapplingHookCable());
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownShield.class, new RenderThrownShield());
        RenderingRegistry.registerEntityRenderingHandler(EntityFireBlast.class, new RenderFireBlast());
        RenderingRegistry.registerEntityRenderingHandler(EntityEarthquake.class, new RenderInvisible());
        RenderingRegistry.registerEntityRenderingHandler(EntityShadowDome.class, new RenderInvisible());
        RenderingRegistry.registerEntityRenderingHandler(EntityIcicle.class, new RenderIcicle());
        RenderingRegistry.registerEntityRenderingHandler(EntityBatarang.class, new RenderBatarang());
        RenderingRegistry.registerEntityRenderingHandler(EntityFreezeGrenade.class, new RenderFreezeGrenade());
        RenderingRegistry.registerEntityRenderingHandler(EntitySmokePellet.class, new RenderSmokePellet());
        RenderingRegistry.registerEntityRenderingHandler(EntityThrowingStar.class, new RenderThrowingStar());
        RenderingRegistry.registerEntityRenderingHandler(EntityGrapplingHook.class, new RenderGrapplingHook());
        RenderingRegistry.registerEntityRenderingHandler(EntityIronMan.class, new RenderIronMan());
        RenderingRegistry.registerEntityRenderingHandler(EntityCactusSpike.class, new RenderCactusSpike());
        RenderingRegistry.registerEntityRenderingHandler(EntityCactus.class, new RenderCactus());
        RenderingRegistry.registerEntityRenderingHandler(EntityCanaryCry.class, new RenderSoundWave());
        RenderingRegistry.registerEntityRenderingHandler(EntityLaserBolt.class, new RenderLaserBolt());
        RenderingRegistry.registerEntityRenderingHandler(EntityLightningCast.class, new RenderLightningCast());
        RenderingRegistry.registerEntityRenderingHandler(EntityEarthCrack.class, new RenderEarthCrack());
        RenderingRegistry.registerEntityRenderingHandler(EntityRepulsorBlast.class, new RenderRepulsorBlast());
        RenderingRegistry.registerEntityRenderingHandler(EntitySpellDuplicate.class, new RenderSpellDuplicate());
        RenderingRegistry.registerEntityRenderingHandler(EntitySpellWhip.class, new RenderSpellWhip());
    }

    private void registerItemRenderers()
    {
        for (int i = 0; i < 4; ++i)
        {
            MinecraftForgeClient.registerItemRenderer(ModItems.heroArmor[i], ChargeType.TACHYON.wrap(null));
        }

        MinecraftForgeClient.registerItemRenderer(ModItems.flashRing, RenderItemFlashRing.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.compoundBow, RenderItemCompoundBow.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.quiver, RenderItemQuiver.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.captainAmericasShield, RenderItemCapsShield.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.deadpoolsSwords, RenderItemDeadpoolsSwords.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.blackCanarysTonfas, RenderItemBlackCanarysTonfas.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.prometheusSword, RenderItemPrometheusSword.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.boStaff, RenderItemBoStaff.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.coldGun, RenderItemColdGun.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.heatGun, RenderItemHeatGun.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.ripsGun, RenderItemRipHuntersGun.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.chronosRifle, RenderItemChronosRifle.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.grapplingGun, RenderItemGrapplingGun.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.miniATOMSuit, RenderItemMiniAtomSuit.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.tachyonPrototype, ChargeType.TACHYON.wrap(RenderItemTachyonPrototype.INSTANCE));
        MinecraftForgeClient.registerItemRenderer(ModItems.tachyonDevice, ChargeType.TACHYON.wrap(RenderItemTachyonDevice.INSTANCE));
        MinecraftForgeClient.registerItemRenderer(ModItems.subatomicBattery, ChargeType.SUBATOMIC.wrap(null));
        MinecraftForgeClient.registerItemRenderer(ModItems.displayCase, RenderItemDisplayCase.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(ModItems.gameboii, RenderItemGameboii.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.treadmill), RenderItemTreadmill.INSTANCE);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.displayStand), RenderItemDisplayStand.INSTANCE);
    }

    private void registerTileRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTreadmill.class, new RenderTreadmill());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDisplayStand.class, new RenderDisplayStand());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityParticleCore.class, new RenderParticleCore());

        RenderingRegistry.registerBlockHandler(RenderBlockDisplayStand.INSTANCE);
        RenderingRegistry.registerBlockHandler(RenderBlockSCEternium.INSTANCE);
    }

    @Override
    public EntityPlayer getPlayer()
    {
        return mc.thePlayer;
    }

    @Override
    public boolean isClientPlayer(Entity entity)
    {
        return entity == getPlayer();
    }

    @Override
    public float getRenderTick()
    {
        return ClientEventHandler.renderTick;
    }

    @Override
    public MovingObjectPosition getMouseOver(float partialTicks, float blockReachDistance)
    {
        if (mc.renderViewEntity != null)
        {
            if (mc.theWorld != null)
            {
                mc.pointedEntity = null;
                MovingObjectPosition objectMouseOver = mc.renderViewEntity.rayTrace(blockReachDistance, partialTicks);
                double d1 = blockReachDistance;
                Vec3 vec3 = mc.renderViewEntity.getPosition(partialTicks);

                if (objectMouseOver != null)
                {
                    d1 = objectMouseOver.hitVec.distanceTo(vec3);
                }

                Vec3 vec31 = mc.renderViewEntity.getLook(partialTicks);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
                Entity pointedEntity = null;
                float f1 = 1.0F;
                List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance).expand(f1, f1, f1));
                double d2 = d1;

                for (Object aList : list)
                {
                    Entity entity = (Entity) aList;

                    if (entity.canBeCollidedWith())
                    {
                        float f2 = entity.getCollisionBorderSize();
                        AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
                        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                        if (axisalignedbb.isVecInside(vec3))
                        {
                            if (0.0D < d2 || d2 == 0.0D)
                            {
                                pointedEntity = entity;
                                d2 = 0.0D;
                            }
                        }
                        else if (movingobjectposition != null)
                        {
                            double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                            if (d3 < d2 || d2 == 0.0D)
                            {
                                pointedEntity = entity;
                                d2 = d3;
                            }
                        }
                    }
                }

                if (pointedEntity != null && (d2 < d1 || objectMouseOver == null))
                {
                    objectMouseOver = new MovingObjectPosition(pointedEntity);
                }

                return objectMouseOver;
            }
        }

        return null;
    }

    @Override
    public void playSound(EntityLivingBase entity, String sound, float volume, float pitch, int... args)
    {
        if (entity == null)
        {
            entity = mc.thePlayer;
        }

        if ("gliding".equals(sound))
        {
            mc.getSoundHandler().playSound(new MovingSoundFlight(entity));
        }
        else if ("cryokinesis".equals(sound))
        {
            mc.getSoundHandler().playSound(new MovingSoundIceCharge(entity));
        }
        else if ("spodermen".equals(sound))
        {
            mc.getSoundHandler().playSound(new MovingSoundSpodermen(entity, volume, pitch));
        }
        else if ("absorb".equals(sound))
        {
            TileEntity tile = entity.worldObj.getTileEntity(args[0], args[1], args[2]);

            if (tile instanceof TileEntityParticleCore)
            {
                mc.getSoundHandler().playSound(new MovingSoundAbsorb((TileEntityParticleCore) tile));
            }
        }
    }
}
