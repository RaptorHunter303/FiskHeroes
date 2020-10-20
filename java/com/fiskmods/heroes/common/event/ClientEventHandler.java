package com.fiskmods.heroes.common.event;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHReflection;
import com.fiskmods.heroes.asm.ASMHooks;
import com.fiskmods.heroes.asm.ASMHooksClient;
import com.fiskmods.heroes.client.SHPlayerControllerMP;
import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.client.SHResourceHandler;
import com.fiskmods.heroes.client.gui.GuiSuperheroesBook;
import com.fiskmods.heroes.client.keybinds.KeyBindTranslator;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.client.render.LightningData;
import com.fiskmods.heroes.client.render.entity.SHEntityRenderer;
import com.fiskmods.heroes.client.render.entity.player.RenderPlayerHand;
import com.fiskmods.heroes.client.render.equipment.EquipmentRenderer;
import com.fiskmods.heroes.client.render.equipment.QuiverRenderer;
import com.fiskmods.heroes.client.render.equipment.RenderEquipmentEvent;
import com.fiskmods.heroes.client.render.equipment.ShieldRenderer;
import com.fiskmods.heroes.client.render.equipment.TachyonDeviceRenderer;
import com.fiskmods.heroes.client.render.equipment.TachyonPrototypeRenderer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffect;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectChest;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectDeadpoolSheath;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectPrometheusSheath;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectTrail;
import com.fiskmods.heroes.client.render.item.RenderItemMiniAtomSuit;
import com.fiskmods.heroes.client.sound.PositionedSoundSarcastic;
import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.client.sound.SoundSH;
import com.fiskmods.heroes.common.Tickrate;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.data.DataManager;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.arrow.IArrowData;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.common.entity.EntityBookPlayer;
import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;
import com.fiskmods.heroes.common.entity.EntityRenderItemPlayer;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.hero.ItemHeroArmor;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.interaction.InteractionType;
import com.fiskmods.heroes.common.item.IDualItem;
import com.fiskmods.heroes.common.item.IScopeWeapon;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.network.MessageSwingOffhand;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.common.proxy.ClientProxy;
import com.fiskmods.heroes.common.world.ModDimensions;
import com.fiskmods.heroes.util.QuiverHelper;
import com.fiskmods.heroes.util.RewardHelper;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;
import com.fiskmods.heroes.util.TextureHelper;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent;

public enum ClientEventHandler
{
    INSTANCE;

    private Minecraft mc = Minecraft.getMinecraft();

    public RenderPlayerHand renderHandInstance;
    private EntityRenderer renderer, prevRenderer;
    private PlayerControllerMP playerController, prevPlayerController;

    public static float renderTick;
    public static boolean blockKeyPresses;

    private int rightClickCounter;
    private boolean prevRightSwingInProgress = false;
    private boolean prevLeftSwingInProgress = false;

    public double shadowDome;

    private ClientEventHandler()
    {
        renderHandInstance = new RenderPlayerHand();
        renderHandInstance.setRenderManager(RenderManager.instance);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equals(FiskHeroes.MODID))
        {
            SHConfig.load(SHConfig.configFile);
            SHConfig.configFile.save();
        }
    }

    @SubscribeEvent
    public void onInitGuiPost(InitGuiEvent.Post event)
    {
        if (event.gui instanceof GuiMainMenu)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            String text = null;
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            if (month == Calendar.NOVEMBER && day == 5)
            {
                text = "Happy birthday, Fisk!";
            }
            else if (month == Calendar.JANUARY && day == 26)
            {
                text = "Happy birthday, Megan!";
            }
            else if (month == Calendar.MAY && day == 30)
            {
                text = "Happy birthday, Gaby!";
            }
            else
            {
                String format = "Happy %s, m'dude!";
                int year = calendar.get(Calendar.YEAR);

                if (month == Calendar.MARCH && day == 14)
                {
                    text = String.format(format, year - 2002);
                }
                else if (month == Calendar.JANUARY && day == 9)
                {
                    text = String.format(format, year - 2002);
                }
                else if (month == Calendar.SEPTEMBER && day == 13)
                {
                    text = String.format(format, year - 2001);
                }
            }

            if (text != null)
            {
                SHReflection.splashTextField.set(event.gui, text);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        EntityPlayer player = event.entityPlayer;
        int x = event.x;
        int y = event.y;
        int z = event.z;

        if (SHData.SHIELD.get(player) || SHData.SHADOWFORM.get(player))
        {
            event.setCanceled(true);
            return;
        }

        ItemStack heldItem = player.getHeldItem();

        if (event.action == Action.RIGHT_CLICK_AIR)
        {
            if (heldItem != null && heldItem.getItem() instanceof IDualItem)
            {
                if (rightClickCounter <= 0 && (mc.objectMouseOver == null || !(mc.objectMouseOver.entityHit instanceof EntityHanging)))
                {
                    SHHelper.swingOffhandItem(player);
                    SHNetworkManager.wrapper.sendToServer(new MessageSwingOffhand(player));

                    if (mc.objectMouseOver == null)
                    {
                        LogManager.getLogger().error("Null returned as \'hitResult\', mc shouldn\'t happen!");

                        if (mc.playerController.isNotCreative())
                        {
                            rightClickCounter = 10;
                        }
                    }
                    else
                    {
                        switch (mc.objectMouseOver.typeOfHit.ordinal())
                        {
                        case 2:
                            mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
                            break;
                        case 1:
                            int x1 = mc.objectMouseOver.blockX;
                            int y1 = mc.objectMouseOver.blockY;
                            int z1 = mc.objectMouseOver.blockZ;

                            if (mc.theWorld.getBlock(x1, y1, z1).getMaterial() == Material.air)
                            {
                                if (mc.playerController.isNotCreative())
                                {
                                    rightClickCounter = 10;
                                }
                            }
                            else
                            {
                                mc.playerController.clickBlock(x1, y1, z1, mc.objectMouseOver.sideHit);
                            }
                        }
                    }
                }

                event.setCanceled(true);
                return;
            }

            x = MathHelper.floor_double(player.posX);
            y = MathHelper.floor_double(player.boundingBox.minY);
            z = MathHelper.floor_double(player.posZ);
        }
        else if (event.action == Action.LEFT_CLICK_BLOCK && SHHelper.hasLeftClickKey(player, SHHelper.getHero(player)))
        {
            event.setCanceled(true);
            return;
        }

        if (InteractionType.get(event.action).interact(player, x, y, z, true))
        {
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        EntityPlayer player = event.player;

        if (player.worldObj.isRemote)
        {
            HeroIteration iter = SHHelper.getHeroIter(player);
            HeroRenderer renderer = HeroRenderer.get(iter);
            boolean flag = true;

            if (renderer != null && iter != null)
            {
                for (HeroEffect effect : renderer.getEffects())
                {
                    effect.onClientTick(player, iter, event.phase);

                    if (effect instanceof HeroEffectTrail && ((HeroEffectTrail) effect).shouldTick(player, event.phase))
                    {
                        flag = false;
                    }
                }
            }

            if (flag && StatusEffect.has(player, StatEffect.VELOCITY_9))
            {
                HeroEffectTrail.tick(player, SHResourceHandler.getV9Trail(), event.phase, SpeedsterHelper.canPlayerRun(player));
            }

            if (event.phase == Phase.END)
            {
                if (player.capabilities.isCreativeMode && mc.playerController.isNotCreative())
                {
                    mc.playerController.setGameType(GameType.CREATIVE);
                }

                if (player == mc.thePlayer)
                {
                    if (iter != null && iter.hero.getFuncBoolean(player, Ability.FUNC_CAN_AIM, false))
                    {
                        SHData.AIMING.set(player, iter.hero.isKeyPressed(player, Ability.KEY_AIM));
                    }
                    else
                    {
                        SHData.AIMING.set(player, false);
                    }

                    if (rightClickCounter > 0)
                    {
                        --rightClickCounter;
                    }

                    ItemStack itemstack = player.getHeldItem();

                    if (prevRightSwingInProgress != player.isSwingInProgress || prevLeftSwingInProgress != SHData.IS_SWING_IN_PROGRESS.get(player))
                    {
                        if (itemstack != null && itemstack.getItem() instanceof IDualItem)
                        {
                            IDualItem dualItem = (IDualItem) itemstack.getItem();

                            if (prevRightSwingInProgress)
                            {
                                dualItem.onSwingEnd(player, itemstack, true);
                            }

                            if (prevLeftSwingInProgress)
                            {
                                dualItem.onSwingEnd(player, itemstack, false);
                            }
                        }

                        prevRightSwingInProgress = player.isSwingInProgress;
                        prevLeftSwingInProgress = SHData.IS_SWING_IN_PROGRESS.get(player);
                    }
                }
            }
            else
            {
                LinkedList<LightningData> list = HeroEffectTrail.lightningData.computeIfAbsent(player, k -> new LinkedList<>());

                for (int i = 0; i < list.size(); ++i)
                {
                    LightningData data = list.get(i);
                    data.onUpdate(player, player.worldObj);
                }

                if (player == mc.thePlayer)
                {
                    SHData.EQUIPPED_QUIVER_SLOT.set(player, QuiverHelper.locateEquippedQuiver(player));
                    SHData.EQUIPPED_TACHYON_DEVICE_SLOT.set(player, SpeedsterHelper.locateEquippedTachyonDevice(player));

                    if (SHHelper.hasEnabledModifier(player, Ability.PROPELLED_FLIGHT))
                    {
                        SHData.FLIGHT_ANIMATION.set(player, (byte) (player.onGround || player.capabilities.isFlying ? 0 : player.moveForward < 0 ? -1 : 1));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onSound(PlaySoundEvent17 event)
    {
        if (mc.theWorld != null && mc.thePlayer != null)
        {
            ISound sound = event.sound;

            if (sound.getClass() == PositionedSoundRecord.class) // We don't want to replace any custom moving/positioned sounds
            {
                float pitchMultiplier = Tickrate.CLIENT_TICKRATE / 20F;

                if (sound.getPositionedSoundLocation().toString().equals(SHSounds.RANDOM_BATFISH.toString()))
                {
                    int x = MathHelper.floor_float(sound.getXPosF());
                    int y = MathHelper.floor_float(sound.getYPosF());
                    int z = MathHelper.floor_float(sound.getZPosF());
                    TileEntity tile = mc.theWorld.getTileEntity(x, y, z);

                    if (tile instanceof TileEntitySign)
                    {
                        TileEntitySign sign = (TileEntitySign) tile;

                        for (String s : sign.signText)
                        {
                            if (s.matches("bAtFiSh|BaTfIsH"))
                            {
                                event.result = new PositionedSoundSarcastic(sound.getPositionedSoundLocation(), sound.getVolume(), sound.getPitch() * pitchMultiplier, sound.canRepeat(), sound.getRepeatDelay(), sound.getAttenuationType(), sound.getXPosF(), sound.getYPosF(), sound.getZPosF());
                                return;
                            }
                        }
                    }
                }

                if (Tickrate.CLIENT_TICKRATE != 20)
                {
                    event.result = new SoundSH(sound.getPositionedSoundLocation(), sound.getVolume(), sound.getPitch() * pitchMultiplier, sound.canRepeat(), sound.getRepeatDelay(), sound.getAttenuationType(), sound.getXPosF(), sound.getYPosF(), sound.getZPosF());
                }
            }
        }
    }

    @SubscribeEvent
    public void onFOVUpdate(FOVUpdateEvent event)
    {
        EntityPlayer player = event.entity;
        ItemStack heldItem = player.getHeldItem();

        if (SpeedsterHelper.isOnTreadmill(player))
        {
            event.newfov += 0.5F;
        }

        if (SHData.GLIDING.get(player) && mc.gameSettings.keyBindForward.getIsKeyPressed() && SHHelper.hasEnabledModifier(player, Ability.GLIDING_FLIGHT))
        {
            event.newfov += 0.2F;
        }

        if (player.isUsingItem() && player.getItemInUse().getItem() == ModItems.compoundBow)
        {
            int i = player.getItemInUseDuration();
            float f1 = i / SHAttributes.BOW_DRAWBACK.get(player, 30);

            if (f1 > 1.0F)
            {
                f1 = 1.0F;
            }
            else
            {
                f1 *= f1;
            }

            event.newfov *= 1.0F - f1 * 0.15F;
        }

        if (player.isUsingItem() && player.getItemInUse().getItem() == Items.bow)
        {
            int i = player.getItemInUseDuration();
            float f1 = (float) i / 20;

            if (f1 > 1.0F)
            {
                f1 = 1.0F;
            }
            else
            {
                f1 *= f1;
            }

            event.newfov /= 1.0F - f1 * 0.15F;

            f1 = i / ASMHooks.getDrawbackTime(player);

            if (f1 > 1.0F)
            {
                f1 = 1.0F;
            }
            else
            {
                f1 *= f1;
            }

            event.newfov *= 1.0F - f1 * 0.15F;
        }

        if (mc.gameSettings.thirdPersonView == 0 && heldItem != null && heldItem.getItem() instanceof IScopeWeapon && ((IScopeWeapon) heldItem.getItem()).canUseScope(heldItem))
        {
            float f = ((IScopeWeapon) heldItem.getItem()).isProperScope() ? 0.8F : 0.4F;
            f -= 0.1F * SHData.RELOAD_TIMER.interpolate(player);

            event.newfov *= 1 - SHData.SCOPE_TIMER.interpolate(player) * f * Math.abs(MathHelper.cos(player.getSwingProgress(renderTick) * (float) Math.PI));
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (mc.thePlayer == null || mc.thePlayer.dimension == event.world.provider.dimensionId)
        {
            playerController = null;
        }
    }

    @SubscribeEvent
    public void onDisconnectFromServer(ClientDisconnectionFromServerEvent event)
    {
        SHConfig.load(SHConfig.configFile);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase == Phase.END)
        {
            ClientProxy.guiOverlay.updateTick();
            EntityPlayer player = mc.thePlayer;

            if (player != null)
            {
                if (playerController == null)
                {
                    playerController = new SHPlayerControllerMP(mc, mc.getNetHandler());
                }

                if (SHHelper.shouldOverrideReachDistance(player))
                {
                    if (mc.playerController != playerController)
                    {
                        prevPlayerController = mc.playerController;
                        mc.playerController = playerController;
                    }
                }
                else if (prevPlayerController != null && mc.playerController != prevPlayerController)
                {
                    mc.playerController = playerController;
                }

                if (!mc.isGamePaused())
                {
                    if (player.dimension == ModDimensions.QUANTUM_REALM_ID)
                    {
                        Random rand = new Random();
                        double spread = 200;

                        for (int i = 0; i < 30; ++i)
                        {
                            SHParticleType.QUANTUM_PARTICLE.spawn(player.posX + (rand.nextDouble() - 0.5D) * spread - spread / 4 * 0.2F, player.posY + (rand.nextDouble() - 0.5D) * spread, player.posZ + (rand.nextDouble() - 0.5D) * spread, 0.2F, 0, 0);
                        }
                    }

                    if (shadowDome > 0)
                    {
                        shadowDome -= 1F / 20;
                    }
                }

                blockKeyPresses = SHHelper.isHero(player) && StatusEffect.has(player, StatEffect.TUTRIDIUM_POISON);
            }
            else
            {
                blockKeyPresses = false;
            }

            if (mc.theWorld == null || mc.isGamePaused())
            {
                Tickrate.updateClientTickrate(20);

                if (mc.gameSettings.renderDistanceChunks > 16)
                {
                    mc.gameSettings.renderDistanceChunks = 16;
                }
            }

            if (GuiSuperheroesBook.fakePlayer == null)
            {
                if (mc != null && mc.playerController != null && mc.theWorld != null)
                {
                    GuiSuperheroesBook.fakePlayer = new EntityBookPlayer(mc);
                }
            }
            else
            {
                GuiSuperheroesBook.fakePlayer.onUpdate();
            }

            if (RenderItemMiniAtomSuit.fakePlayer == null)
            {
                if (mc != null && mc.playerController != null && mc.theWorld != null)
                {
                    RenderItemMiniAtomSuit.fakePlayer = new EntityRenderItemPlayer(mc);
                }
            }
            else
            {
                RenderItemMiniAtomSuit.fakePlayer.onUpdate();
            }
        }
        else if (mc.theWorld != null)
        {
            for (EntityPlayer player : (List<EntityPlayer>) mc.theWorld.playerEntities)
            {
                SHRenderHelper.updatePrevMotion(player);
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;
        HeroIteration iter = SHHelper.getHeroIter(player);
        World world = player.worldObj;

        if (SHData.INVISIBLE.get(player) && SHHelper.getInvisibility(player, mc.thePlayer) <= 0 || SHData.SHADOWFORM_TIMER.get(player) >= 1 && !SHHelper.canPlayerSeeMartianInvis(mc.thePlayer))
        {
            event.setCanceled(true);
            return;
        }

        if (iter != null)
        {
            HeroRenderer renderer = HeroRenderer.get(iter);

            if (!renderer.shouldRenderDefaultModel(player, iter, true))
            {
                for (ModelBiped model : new ModelBiped[] {event.renderer.modelArmorChestplate, event.renderer.modelArmor, event.renderer.modelBipedMain})
                {
                    model.bipedHead.offsetY = 256;
                    model.bipedHeadwear.offsetY = 256;
                    model.bipedBody.offsetY = 256;
                    model.bipedRightArm.offsetY = 256;
                    model.bipedLeftArm.offsetY = 256;
                    model.bipedRightLeg.offsetY = 256;
                    model.bipedLeftLeg.offsetY = 256;
                }
            }
        }

        for (int slot = 0; slot < 4; ++slot)
        {
            HeroIteration iter1 = SHHelper.getHeroIterFromArmor(player, 3 - slot);

            if (iter1 != null)
            {
                HeroRenderer renderer = HeroRenderer.get(iter1);

                if (renderer.fixHatLayer(player, slot))
                {
                    for (ModelBiped model : new ModelBiped[] {event.renderer.modelArmorChestplate, event.renderer.modelArmor, event.renderer.modelBipedMain})
                    {
                        model.bipedHeadwear.offsetY = 256;
                    }

                    break;
                }
            }
        }

        for (ModelBiped model : new ModelBiped[] {event.renderer.modelArmorChestplate, event.renderer.modelArmor, event.renderer.modelBipedMain})
        {
            ItemStack itemstack = player.getHeldItem();

            if (itemstack != null && itemstack.getItem() instanceof IDualItem)
            {
                model.heldItemLeft = 1;

                if (player.getItemInUseCount() > 0 && player.getItemInUse() == itemstack)
                {
                    EnumAction enumaction = itemstack.getItemUseAction();

                    if (enumaction == EnumAction.block)
                    {
                        model.heldItemLeft = 3;
                    }
                    else if (enumaction == EnumAction.bow)
                    {
                        model.aimedBow = true;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event)
    {
        EntityPlayer player = event.entityPlayer;

        for (ModelBiped model : new ModelBiped[] {event.renderer.modelArmorChestplate, event.renderer.modelArmor, event.renderer.modelBipedMain})
        {
            model.bipedHead.offsetY = 0;
            model.bipedHeadwear.offsetY = 0;
            model.bipedBody.offsetY = 0;
            model.bipedRightArm.offsetY = 0;
            model.bipedLeftArm.offsetY = 0;
            model.bipedRightLeg.offsetY = 0;
            model.bipedLeftLeg.offsetY = 0;
            model.heldItemLeft = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderPlayerSpecialsPre(RenderPlayerEvent.Specials.Pre event)
    {
        AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;
        ModelBiped model = event.renderer.modelBipedMain;
        HeroIteration iter = SHHelper.getHeroIter(player);

        if ((SHData.INVISIBLE.get(player) || SHData.SHADOWFORM_TIMER.get(player) >= 1) && !SHHelper.canPlayerSeeMartianInvis(mc.thePlayer))
        {
            event.setCanceled(true);
            return;
        }

        boolean prevRenderCape = event.renderCape;

        if (SHHelper.getHeroFromArmor(player, 2) != null)
        {
            event.renderCape = false;
        }

        if (player instanceof EntityDisplayMannequin || player instanceof EntityRenderItemPlayer)
        {
            for (ModelBiped model1 : new ModelBiped[] {event.renderer.modelArmorChestplate, event.renderer.modelArmor, event.renderer.modelBipedMain})
            {
                for (ModelRenderer modelRenderer : (List<ModelRenderer>) model1.boxList)
                {
                    modelRenderer.rotateAngleX = 0;
                    modelRenderer.rotateAngleY = 0;
                    modelRenderer.rotateAngleZ = 0;
                }
            }
        }

        setupPlayerRotation(player);

        Random random = new Random((long) player.getEntityId() + 1);
        GL11.glColor4f(1, 1, 1, 1);

        if (!player.isInvisible())
        {
            for (int slot = 0; slot < 4; ++slot)
            {
                HeroIteration iter1 = SHHelper.getHeroIterFromArmor(player, 3 - slot);

                if (iter1 != null)
                {
                    HeroRenderer renderer = HeroRenderer.get(iter1);

                    if (renderer.fixHatLayer(player, slot))
                    {
                        ModelRenderer hatLayer = renderer.model.hatLayer;
                        boolean flag = model.bipedHeadwear.showModel;
                        boolean flag1 = model.bipedHeadwear.isHidden;

                        hatLayer.showModel = true;
                        hatLayer.isHidden = false;
                        ModelBipedMultiLayer.sync(model.bipedHead, hatLayer);
                        hatLayer.showModel = flag;
                        hatLayer.isHidden = flag1;

                        mc.getTextureManager().bindTexture(player.getLocationSkin());
                        hatLayer.render(0.0625F);
                        break;
                    }
                }
            }
        }

        for (IArrowData data : DataManager.getArrowsInEntity(player))
        {
            if (data != null)
            {
                EntityTrickArrow arrow = data.getEntity(player.worldObj);

                if (arrow != null)
                {
                    GL11.glPushMatrix();
                    ModelRenderer modelrenderer = model.getRandomModelBox(random);
                    ModelBox modelbox = (ModelBox) modelrenderer.cubeList.get(random.nextInt(modelrenderer.cubeList.size()));
                    modelrenderer.postRender(0.0625F);
                    float f1 = random.nextFloat();
                    float f2 = random.nextFloat();
                    float f3 = random.nextFloat();
                    float f4 = (modelbox.posX1 + (modelbox.posX2 - modelbox.posX1) * f1) / 16.0F;
                    float f5 = (modelbox.posY1 + (modelbox.posY2 - modelbox.posY1) * f2) / 16.0F;
                    float f6 = (modelbox.posZ1 + (modelbox.posZ2 - modelbox.posZ1) * f3) / 16.0F;
                    GL11.glTranslatef(f4, f5, f6);
                    f1 = f1 * 2.0F - 1.0F;
                    f2 = f2 * 2.0F - 1.0F;
                    f3 = f3 * 2.0F - 1.0F;
                    f1 *= -1.0F;
                    f2 *= -1.0F;
                    f3 *= -1.0F;
                    float f7 = MathHelper.sqrt_float(f1 * f1 + f3 * f3);
                    arrow.prevRotationYaw = arrow.rotationYaw = (float) (Math.atan2(f1, f3) * 180.0D / Math.PI);
                    arrow.prevRotationPitch = arrow.rotationPitch = (float) (Math.atan2(f2, f7) * 180.0D / Math.PI);
                    double d0 = 0.0D;
                    double d1 = 0.0D;
                    double d2 = 0.0D;
                    float f8 = 0.0F;
                    RenderManager.instance.renderEntityWithPosYaw(arrow, d0, d1, d2, f8, 85 + event.partialRenderTick);
                    GL11.glPopMatrix();
                }
            }
        }

        EquipmentRenderer.render(player, iter, model, event.partialRenderTick);

        if (iter != null && iter.hero.hasEnabledModifier(player, Ability.SHAPE_SHIFTING))
        {
            String disguise = SHData.DISGUISE.get(player);

            if (disguise != null)
            {
                ResourceLocation cape = TextureHelper.getCape(disguise);

                if (prevRenderCape && cape != null && !player.isInvisible() && !player.getHideCape())
                {
                    float t = SHData.SHAPE_SHIFT_TIMER.get(player);
                    t = 1 - (t > 0.5F ? 1 - t : t) * 2;
                    GL11.glColor4f(1, 1, 1, t);

                    mc.getTextureManager().bindTexture(cape);
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
                    GL11.glTranslatef(0.0F, 0.0F, 0.125F);
                    double d3 = player.field_71091_bM + (player.field_71094_bP - player.field_71091_bM) * event.partialRenderTick - (player.prevPosX + (player.posX - player.prevPosX) * event.partialRenderTick);
                    double d4 = player.field_71096_bN + (player.field_71095_bQ - player.field_71096_bN) * event.partialRenderTick - (player.prevPosY + (player.posY - player.prevPosY) * event.partialRenderTick);
                    double d0 = player.field_71097_bO + (player.field_71085_bR - player.field_71097_bO) * event.partialRenderTick - (player.prevPosZ + (player.posZ - player.prevPosZ) * event.partialRenderTick);
                    float f4 = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * event.partialRenderTick;
                    double d1 = MathHelper.sin(f4 * (float) Math.PI / 180.0F);
                    double d2 = -MathHelper.cos(f4 * (float) Math.PI / 180.0F);
                    float f5 = (float) d4 * 10.0F;

                    if (f5 < -6.0F)
                    {
                        f5 = -6.0F;
                    }

                    if (f5 > 32.0F)
                    {
                        f5 = 32.0F;
                    }

                    float f6 = (float) (d3 * d1 + d0 * d2) * 100.0F;
                    float f7 = (float) (d3 * d2 - d0 * d1) * 100.0F;

                    if (f6 < 0.0F)
                    {
                        f6 = 0.0F;
                    }

                    float f8 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * event.partialRenderTick;
                    f5 += MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * event.partialRenderTick) * 6.0F) * 32.0F * f8;

                    if (player.isSneaking())
                    {
                        f5 += 25.0F;
                    }

                    GL11.glRotatef(6.0F + f6 / 2.0F + f5, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(f7 / 2.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-f7 / 2.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    model.renderCloak(0.0625F);
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderPlayerSpecialsPost(RenderPlayerEvent.Specials.Post event)
    {
        SHRenderHooks.renderItemIn3rdPerson(event.entityPlayer, event.renderer.modelBipedMain, event.partialRenderTick);
    }

    @SubscribeEvent
    public void onRenderEquipment(RenderEquipmentEvent event)
    {
        if (event.renderer == TachyonDeviceRenderer.INSTANCE || event.renderer == TachyonPrototypeRenderer.INSTANCE)
        {
            HeroIteration iter = SHHelper.getHeroIterFromArmor(event.player, 2);
            HeroRenderer renderer = HeroRenderer.get(iter);

            if (renderer != null && iter != null)
            {
                HeroEffectChest effect = renderer.getEffect(HeroEffectChest.class, event.player);

                if (effect != null)
                {
                    event.zOffset -= effect.getExtrude() / 16 * 0.25F;
                    event.yOffset += (effect.getYOffset() - 1) / 16;
                }
            }
        }
        else if (event.renderer == ShieldRenderer.INSTANCE)
        {
            if (QuiverRenderer.INSTANCE.test(event.player))
            {
                event.xOffset += 0.05F;
                event.yOffset -= 0.18F + (event.player.getCurrentArmor(2) == null ? 0.01F : 0);
                event.zOffset += 0.05F;
            }
            else
            {
                for (int i = 0; i < 4; ++i)
                {
                    HeroIteration iter = SHHelper.getHeroIterFromArmor(event.player, i);

                    if (iter != null)
                    {
                        HeroRenderer renderer = HeroRenderer.get(iter);

                        if (renderer.hasEffect(HeroEffectDeadpoolSheath.class) || renderer.hasEffect(HeroEffectPrometheusSheath.class))
                        {
                            event.yOffset -= 0.0225F;
                            break;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderLivingSpecialsPre(RenderLivingEvent.Specials.Pre event)
    {
        if (event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;
            Hero hero = SHHelper.getHero(player);

            if (hero != null && hero.hasEnabledModifier(player, Ability.SHAPE_SHIFTING))
            {
                String disguise = SHData.DISGUISE.get(player);

                if (disguise != null)
                {
                    Render rend = RenderManager.instance.getEntityRenderObject(player);
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

                    if (rend instanceof RenderPlayer && Minecraft.isGuiEnabled() && player.riddenByEntity == null && !player.isInvisibleToPlayer(mc.thePlayer) && SHHelper.getInvisibility(player, mc.thePlayer) > 0.2F)
                    {
                        double dist = player.getDistanceSqToEntity(mc.thePlayer);
                        float range = player.isSneaking() ? RendererLivingEntity.NAME_TAG_RANGE_SNEAK : RendererLivingEntity.NAME_TAG_RANGE;

                        if (dist < range * range)
                        {
                            boolean flag = SHHelper.canPlayerSeeMartianInvis(mc.thePlayer);
                            double yOffset = player == mc.thePlayer ? -1.62F * (SHData.SCALE.interpolate(player) - 1) : 0;
                            float f = 0.016666668F * 1.6F;

                            if (player == mc.thePlayer)
                            {
                                dist += 100;
                            }

                            if (flag)
                            {
                                disguise = EnumChatFormatting.GRAY + disguise;
                            }

                            SHReflection.renderNametag((RenderPlayer) rend, player, event.x, event.y + yOffset, event.z, disguise, f, dist);

                            if (flag)
                            {
                                float f1 = rend.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * f;

                                if (dist < 100 && player.getWorldScoreboard().func_96539_a(2) != null)
                                {
                                    f1 *= 2;
                                }

                                dist += 100;
                                yOffset += f1;
                                SHReflection.renderNametag((RenderPlayer) rend, player, event.x, event.y + yOffset, event.z, player.func_145748_c_().getFormattedText(), f, dist);
                            }
                        }
                    }

                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event)
    {
        EntityPlayer player = event.player;
        Hero hero = SHHelper.getHero(player);

        if (hero != null)
        {
            if (SHData.INTANGIBLE.get(player) && event.overlayType == RenderBlockOverlayEvent.OverlayType.BLOCK)
            {
                if (hero.hasEnabledModifier(player, Ability.INTANGIBILITY) && Ability.INTANGIBILITY.isActive(player) || hero.hasEnabledModifier(player, Ability.ABSOLUTE_INTANGIBILITY) && Ability.ABSOLUTE_INTANGIBILITY.isActive(player))
                {
                    event.setCanceled(true);
                }
            }

            if (event.overlayType == RenderBlockOverlayEvent.OverlayType.FIRE && hero.hasEnabledModifier(player, Ability.FIRE_IMMUNITY) && Ability.FIRE_IMMUNITY.isActive(player))
            {
                event.setCanceled(true);
            }
        }
    }

    public static void setupPlayerRotation(EntityLivingBase entity)
    {
        byte b = SHData.FLIGHT_ANIMATION.get(entity);

        if (b != 0)
        {
            double d = Math.min(Math.sqrt((entity.prevPosX - entity.posX) * (entity.prevPosX - entity.posX) + (entity.prevPosZ - entity.posZ) * (entity.prevPosZ - entity.posZ)), 1) * b;
            GL11.glRotated(60 * d, 1, 0, 0); // TODO: Rework flight animation
        }
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event)
    {
        if (event.map.getTextureType() == 1)
        {
            RewardHelper.registerIcons(event.map);
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event)
    {
        ItemStack itemstack = event.itemStack;
        List<String> tooltip = event.toolTip;

        if (itemstack.getItem() instanceof ItemHeroArmor && !GuiScreen.isShiftKeyDown() && !ItemHeroArmor.hideStats)
        {
            ItemHeroArmor.hideStats = true;
            tooltip.clear();
            tooltip.addAll(itemstack.getTooltip(event.entityPlayer, event.showAdvancedItemTooltips));
            ItemHeroArmor.hideStats = false;
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        renderTick = event.renderTickTime;

        if (mc.theWorld != null)
        {
            if (event.phase == TickEvent.Phase.START)
            {
                ASMHooksClient.onRenderTick();

                if (SHRenderHelper.shouldOverrideThirdPersonDistance(mc.thePlayer))
                {
                    float thirdPersonDistance = 4.0F;

                    if (SHHelper.isHero(mc.thePlayer) && SHHelper.getPrevHero(mc.thePlayer) != null)
                    {
                        thirdPersonDistance *= SHData.SCALE.interpolate(mc.thePlayer);
                    }

                    SHReflection.thirdPersonDistanceField.set(mc.entityRenderer, thirdPersonDistance);
                }

                if (SHRenderHelper.shouldOverrideView(mc.thePlayer) || SHHelper.shouldOverrideReachDistance(mc.thePlayer))
                {
                    if (renderer == null)
                    {
                        renderer = new SHEntityRenderer(mc);
                    }

                    if (mc.entityRenderer != renderer)
                    {
                        prevRenderer = mc.entityRenderer;
                        mc.entityRenderer = renderer;
                    }
                }
                else if (prevRenderer != null && mc.entityRenderer == renderer)
                {
                    mc.entityRenderer = prevRenderer;
                }

                KeyBindTranslator.translate(mc.thePlayer);
            }
        }
    }

    @SubscribeEvent
    public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event)
    {
        if (shadowDome > 0)
        {
            float r = Rule.RADIUS_SHADOWDOME.get(mc.thePlayer);
            float f = (float) Math.min(shadowDome * 10, 1);
            float f1 = r + (event.farPlaneDistance - r) * (1 - f);

            GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);

            if (SHHelper.hasEnabledModifier(mc.thePlayer, Ability.UMBRAKINESIS))
            {
                GL11.glFogf(GL11.GL_FOG_START, f1 * 0.9F);
                GL11.glFogf(GL11.GL_FOG_END, f1);
            }
            else
            {
                GL11.glFogf(GL11.GL_FOG_START, 0);
                GL11.glFogf(GL11.GL_FOG_END, f1 * (0.2F + (float) shadowDome * 0.8F));
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GL11.glFogi(34138, 34139);
            }
        }
    }

    @SubscribeEvent
    public void onRenderFogColors(EntityViewRenderEvent.FogColors event)
    {
        if (shadowDome > 0)
        {
            double d = 1 - Math.min(shadowDome * 10, 1);
            event.red *= d;
            event.green *= d;
            event.blue *= d;
        }
    }
}
