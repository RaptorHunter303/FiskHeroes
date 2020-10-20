package com.fiskmods.heroes.common.event;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.SHReflection;
import com.fiskmods.heroes.asm.ASMHooks;
import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.achievement.SHAchievements;
import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.book.BookHandler;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.config.RuleHandler;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.Cooldowns;
import com.fiskmods.heroes.common.data.DataManager;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.SHEntityData;
import com.fiskmods.heroes.common.data.SHPlayerData;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.common.data.world.SHMapData;
import com.fiskmods.heroes.common.entity.EntityIronMan;
import com.fiskmods.heroes.common.entity.arrow.EntityGrappleArrow;
import com.fiskmods.heroes.common.entity.arrow.EntityVineArrow;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.entity.gadget.EntityGrapplingHook;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Hero.Permission;
import com.fiskmods.heroes.common.hero.Hero.Property;
import com.fiskmods.heroes.common.hero.Heroes;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.item.IDualItem;
import com.fiskmods.heroes.common.item.IPunchWeapon;
import com.fiskmods.heroes.common.item.IReloadWeapon;
import com.fiskmods.heroes.common.item.IScopeWeapon;
import com.fiskmods.heroes.common.item.ItemCapsShield;
import com.fiskmods.heroes.common.item.ItemTachyonDevice;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.network.MessageApplyMotion;
import com.fiskmods.heroes.common.network.MessageBroadcastState;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.common.world.ModDimensions;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.QuiverHelper;
import com.fiskmods.heroes.util.QuiverHelper.Quiver;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;
import com.fiskmods.heroes.util.SpeedsterHelper.SpeedBar;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;

public enum CommonEventHandler
{
    INSTANCE;

    private List<EntityPlayer> playersToSync = new ArrayList<>();
    private Random rand = new Random();

    private MinecraftServer server;
    public File saveDir;

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        World world = event.world;

        if (!world.isRemote)
        {
            if (MinecraftServer.getServer() != server)
            {
                FiskHeroes.LOGGER.info("Loading server data");
                server = MinecraftServer.getServer();
                saveDir = new File(new File(DimensionManager.getCurrentSaveRootDirectory(), "fiskutils").getAbsolutePath().replace("\\.\\", "\\"));

                BookHandler.INSTANCE.load(saveDir);
                RuleHandler.INSTANCE.load(saveDir);
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event)
    {
        World world = event.world;

        if (event.phase == Phase.START)
        {
            if (world.provider.dimensionId == ModDimensions.QUANTUM_REALM_ID)
            {
                for (Entity entity : (List<Entity>) world.loadedEntityList)
                {
                    if (entity.posY < 0)
                    {
                        entity.posY = world.getHeight();
                    }
                    else if (entity.posY > world.getHeight())
                    {
                        entity.posY = 0;
                    }
                }
            }

            SHMapData.get(world).onUpdate(world);
        }
        else if (event.side.isServer())
        {
            RuleHandler.INSTANCE.tick();
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.entity;

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;

            if (!entity.worldObj.isRemote)
            {
                playersToSync.add(player);
            }
        }
    }

    @SubscribeEvent
    public void onStartTracking(StartTracking event)
    {
        EntityPlayer player = event.entityPlayer;

        if (player != null && !player.worldObj.isRemote)
        {
            if (event.target instanceof EntityPlayer)
            {
                EntityPlayer beingTracked = (EntityPlayer) event.target;

                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                EntityPlayerMP beingTrackedMP = (EntityPlayerMP) beingTracked;

                SHNetworkManager.wrapper.sendTo(new MessageBroadcastState(player), beingTrackedMP);
                SHNetworkManager.wrapper.sendTo(new MessageBroadcastState(beingTracked), playerMP);
            }
        }
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event)
    {
        EntityPlayer player = event.entityPlayer;
        ItemStack itemstack = player.getHeldItem();

        if (SHData.SHIELD.get(player) || SHData.SHADOWFORM.get(player) || SHData.INTANGIBLE.get(player) && SHHelper.hasEnabledModifier(player, Ability.ABSOLUTE_INTANGIBILITY) && Ability.ABSOLUTE_INTANGIBILITY.isActive(player))
        {
            event.setCanceled(true);
            return;
        }

        if (itemstack != null && itemstack.getItem() instanceof IDualItem)
        {
            if (!(event.target instanceof EntityHanging))
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityConstruct(EntityConstructing event)
    {
        if (event.entity instanceof EntityLivingBase)
        {
            event.entity.registerExtendedProperties(SHEntityData.IDENTIFIER, new SHEntityData());
        }

        if (event.entity instanceof EntityPlayer)
        {
            event.entity.registerExtendedProperties(SHPlayerData.IDENTIFIER, new SHPlayerData());
        }
    }

    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone event)
    {
        SHPlayerData.getData(event.entityPlayer).copy(SHPlayerData.getData(event.original));
        SHEntityData.getData(event.entityPlayer).copy(SHEntityData.getData(event.original));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        Hero hero = SHHelper.getHero(player);

        if (event.phase == TickEvent.Phase.END)
        {
            short damaged = SHData.TIME_SINCE_DAMAGED.get(player);

            if (damaged < Short.MAX_VALUE)
            {
                SHData.TIME_SINCE_DAMAGED.setWithoutNotify(player, ++damaged);
            }

            updateArmSwingProgress(player);

            if (!player.worldObj.isRemote)
            {
                ItemStack quiver = QuiverHelper.getEquippedQuiver(player);
                ItemStack tachyonDevice = SpeedsterHelper.getEquippedTachyonDevice(player);

                SHData.EQUIPPED_QUIVER.set(player, quiver != null ? new Quiver(player, quiver) : null);
                SHData.TACHYON_DEVICE_ON.set(player, tachyonDevice != null && ItemTachyonDevice.getCharge(tachyonDevice) > 0);
                SHData.HAS_CPT_AMERICAS_SHIELD.set(player, getHasItem(player, ModItems.captainAmericasShield).byteValue());
                SHData.HAS_DEADPOOLS_SWORDS.set(player, getHasItem(player, ModItems.deadpoolsSwords).byteValue());
                SHData.HAS_PROMETHEUS_SWORD.set(player, getHasItem(player, ModItems.prometheusSword).byteValue());

                if (quiver != null)
                {
                    SHData.CURRENT_ARROW.set(player, QuiverHelper.getArrowToFire(player));
                }

                if (SHData.SUPERHERO_LANDING.get(player))
                {
                    if (player.getHealth() > 0 && player.getHealth() < 1)
                    {
                        player.triggerAchievement(SHAchievements.LAND_DEADPOOL);
                    }

                    SHData.SUPERHERO_LANDING.setWithoutNotify(player, false);
                }
            }

            Cooldowns.getInstance(player).tick();
            StatusEffect effectVelocity9 = StatusEffect.get(player, StatEffect.VELOCITY_9);
            ItemStack heldItem = player.getHeldItem();

            int maxSpeed = SpeedsterHelper.getMaxSpeedSetting(player);

            if (SHData.SPEED.get(player) < 0)
            {
                SHData.SPEED.setWithoutNotify(player, (byte) maxSpeed);
            }

            if (SHData.GLIDING.get(player))
            {
                SHData.TICKS_GLIDING.incrWithoutNotify(player, 1);
            }

            if (SHData.RELOAD_TIMER.get(player) > 0)
            {
                int time = 10;

                if (heldItem != null && heldItem.getItem() instanceof IReloadWeapon)
                {
                    time = ((IReloadWeapon) heldItem.getItem()).getReloadTime(heldItem, player, hero);
                }

                SHData.RELOAD_TIMER.incrWithoutNotify(player, -1F / time);
            }

            SHData.RELOAD_TIMER.clampWithoutNotify(player, 0.0F, 1.0F);

            if (SpeedsterHelper.hasTachyonDevice(player))
            {
                SHHelper.incr(SHData.TACHYON_CHARGE, player, SHConstants.TICKS_TACHYON_LOAD, SHData.TACHYON_DEVICE_ON.get(player) && SpeedsterHelper.getTachyonCharge(player) == 0);
            }
            else
            {
                SHData.TACHYON_CHARGE.setWithoutNotify(player, 0.0F);
            }

            if (effectVelocity9 != null && !SpeedsterHelper.isSpeedster(player))
            {
                SHData.SPEEDING.setWithoutNotify(player, true);

                if (SHData.SPEED.get(player) < maxSpeed)
                {
                    SHData.SPEED.setWithoutNotify(player, (byte) maxSpeed);
                }
            }

            SHHelper.incr(SHData.VEL9_CONVERT, player, SHConstants.FADE_VELOCITY9, effectVelocity9 != null);
            SHHelper.incr(SHData.SCOPE_TIMER, player, SHConstants.TICKS_SCOPE, player.isSneaking() && heldItem != null && heldItem.getItem() instanceof IScopeWeapon && ((IScopeWeapon) heldItem.getItem()).canUseScope(heldItem) && SHData.AIMING_TIMER.get(player) >= 1);
            SHHelper.incr(SHData.HORIZONTAL_BOW_TIMER, player, SHConstants.TICKS_HORIZONTAL_BOW, SHData.HORIZONTAL_BOW.get(player));
            SHHelper.incr(SHData.HAT_TIP, player, SHConstants.ANIMATION_HAT_TIP, false);

            for (Entity entity : (List<Entity>) player.worldObj.loadedEntityList)
            {
                if (entity instanceof EntityGrappleArrow)
                {
                    EntityGrappleArrow arrow = (EntityGrappleArrow) entity;

                    if (arrow.getShooter() == player && !arrow.getIsCableCut())
                    {
                        if (heldItem == null || heldItem.getItem() != ModItems.compoundBow || heldItem == null || player.swingProgressInt == 1)
                        {
                            arrow.setIsCableCut(true);
                            player.worldObj.playSoundAtEntity(player, SHSounds.ENTITY_ARROW_GRAPPLE_DISCONNECT.toString(), 1.0F, 0.8F);
                        }

                        if (arrow.inGround && !(arrow instanceof EntityVineArrow && ((EntityVineArrow) arrow).getIsSnake()))
                        {
                            if (player.motionY >= 0)
                            {
                                player.fallDistance = 0;
                            }

                            double x = (arrow.posX - player.posX) / 40.0;
                            double y = (arrow.posY - player.posY) / 40.0;
                            double z = (arrow.posZ - player.posZ) / 40.0;
                            double cap = 0.3;
                            double d = 0.95;

                            while (x > cap || y > cap || z > cap || x < -cap || y < -cap || z < -cap)
                            {
                                x *= d;
                                y *= d;
                                z *= d;
                            }

                            player.motionX += x;
                            player.motionY += y;
                            player.motionZ += z;
                        }
                    }
                }
                else if (entity instanceof EntityGrapplingHook)
                {
                    EntityGrapplingHook hook = (EntityGrapplingHook) entity;

                    if (hook.getShooter() == player)
                    {
                        if (heldItem == null || heldItem.getItem() != ModItems.grapplingGun || player.swingProgressInt == 1 || !SHHelper.hasPermission(player, Permission.USE_GRAPPLING_GUN))
                        {
                            hook.setDead();
                        }

                        if (hook.inGround)
                        {
                            if (player.motionY >= 0)
                            {
                                player.fallDistance = 0;
                            }

                            double x = (hook.posX - player.posX) / 40.0;
                            double y = (hook.posY - player.posY) / 40.0;
                            double z = (hook.posZ - player.posZ) / 40.0;
                            double cap = 0.2;
                            double d = 0.95;

                            while (x > cap || y > cap || z > cap || x < -cap || y < -cap || z < -cap)
                            {
                                x *= d;
                                y *= d;
                                z *= d;
                            }

                            player.motionX += x;
                            player.motionY += y;
                            player.motionZ += z;
                        }
                    }
                }
            }

            if (SpeedsterHelper.hasSuperSpeed(player))
            {
                if (SHData.SPEED.get(player) > maxSpeed)
                {
                    SHData.SPEED.setWithoutNotify(player, (byte) Math.max((byte) maxSpeed, 0));
                }

                if (SHData.SPEEDING.get(player))
                {
                    if (!player.worldObj.isRemote)
                    {
                        if (player.ticksExisted % 40 == 0 && SHData.SPEED.get(player) > SpeedsterHelper.getFilteredMaxSpeedSetting(player, SpeedBar.TACHYON))
                        {
                            float f = SpeedsterHelper.getTachyonCharge(player);

                            if (f > 0)
                            {
                                for (int i = 0; i < 4; ++i)
                                {
                                    ItemStack itemstack1 = player.getCurrentArmor(i);

                                    if (itemstack1 != null)
                                    {
                                        int charge = itemstack1.getTagCompound().getInteger("TachyonCharge");

                                        if (charge > 0)
                                        {
                                            itemstack1.getTagCompound().setInteger("TachyonCharge", --charge);
                                        }
                                    }
                                }
                            }
                            else if (SHData.TACHYON_DEVICE_ON.get(player) && SHData.TACHYON_CHARGE.get(player) >= 1)
                            {
                                ItemStack itemstack1 = SpeedsterHelper.getEquippedTachyonDevice(player);
                                ItemTachyonDevice.setCharge(itemstack1, ItemTachyonDevice.getCharge(itemstack1) - 1);
                            }
                        }
                    }
                }

                if (maxSpeed >= 19)
                {
                    player.triggerAchievement(SHAchievements.KMPH_1000);
                }

                if (SHData.SPEED_EXPERIENCE_LEVEL.get(player) >= 30)
                {
                    player.triggerAchievement(SHAchievements.MAX_SPEED);
                }
            }

            if (SpeedsterHelper.isOnTreadmill(player))
            {
                if (SHData.TREADMILL_DECREASING.get(player))
                {
                    SHData.TREADMILL_LIMB_FACTOR.setWithoutNotify(player, SHData.TREADMILL_LIMB_FACTOR.get(player) * 0.9F);
                }
                else
                {
                    SHData.TREADMILL_LIMB_FACTOR.incrWithoutNotify(player, 0.05F);
                }

                SHData.TREADMILL_LIMB_FACTOR.clampWithoutNotify(player, 0.0F, 1.0F);
                SHData.TREADMILL_LIMB_PROGRESS.incrWithoutNotify(player, SHData.TREADMILL_LIMB_FACTOR.get(player));
            }

            if (hero != null)
            {
                handleMaskOpen(player, hero);

                SHHelper.incr(SHData.MASK_OPEN_TIMER2, player, 5, SHData.MASK_OPEN.get(player));
                SHHelper.incr(SHData.AIMING_TIMER, player, SHConstants.TICKS_AIMING, SHData.AIMING.get(player));
                SHHelper.incr(SHData.AIMED_TIMER, player, SHConstants.TICKS_AIMED, SHData.AIMING_TIMER.get(player) >= 1);
                SHHelper.incr(SHData.SHOOTING_TIMER, player, SHConstants.TICKS_SHOOTING, SHData.SHOOTING.get(player));
                SHHelper.incr(SHData.STEEL_TIMER, player, SHConstants.TICKS_STEEL_CONVERT, SHData.STEELED.get(player));
                SHHelper.incr(SHData.TRANSFORM_TIMER, player, SHConstants.TICKS_TRANSFORMATION, SHData.TRANSFORMED.get(player));

                Hero.updateModifiers(player, hero, event.phase);
            }

            float scale = SHData.SCALE.get(player);
            float defScale = SHHelper.getDefaultScale(player);
            boolean flag = true;

            if (hero == null || !hero.hasEnabledModifier(player, Ability.SIZE_MANIPULATION))
            {
                double width = player.width / scale * defScale;
                double height = player.height / scale * defScale;

                AxisAlignedBB aabb = player.boundingBox;
                aabb = AxisAlignedBB.getBoundingBox(aabb.minX, aabb.minY, aabb.minZ, aabb.minX + width, aabb.minY + height, aabb.minZ + width);

                if (player.worldObj.getCollidingBoundingBoxes(player, aabb).isEmpty())
                {
                    SHData.SCALE.setWithoutNotify(player, scale = defScale);
                }
                else
                {
                    flag = false;
                }
            }

            if (SHHelper.shouldOverrideScale(player, scale))
            {
                float widthScale = scale;
                float heightScale = scale;

                if (hero == null && SHHelper.getPrevHero(player) != null || !SHData.GLIDING.get(player) && SHData.PREV_GLIDING.get(player) || !SHData.SHADOWFORM.get(player) && SHData.PREV_SHADOWFORM.get(player))
                {
                    widthScale = heightScale = 1;
                }

                if (SHData.GLIDING.get(player))
                {
                    float f = MathHelper.clamp_float(SHData.TICKS_GLIDING.get(player), 0, 4) / 4;
                    heightScale *= 0.6F / 1.8F * f + (1 - f);
                }
                else
                {
                    float f = SHData.SHADOWFORM_TIMER.get(player);
                    widthScale *= 0.2F / 0.6F * f + (1 - f);
                    heightScale *= 0.2F / 1.8F * f + (1 - f);
                }

                SHReflection.setSize(player, 0.6F * widthScale, 1.8F * heightScale);
                player.eyeHeight = (heightScale - 1) * 1.62F + player.getDefaultEyeHeight();
            }

            // int face = SHHelper.getSideStandingOn(player);
            //
            // if (face != 1)
            // {
            // player.motionY = 0;
            //
            // if (face == 2)
            // {
            //// player.motionZ += 0.2F; TODO: Wall-running?
            // }
            //
            // float scale1 = 0.15F / 1.62F;
            // SHReflection.setSize(player, 0.6F * scale1, 1.8F * scale1);
            // player.eyeHeight = (scale1 - 1) * 1.62F + player.getDefaultEyeHeight() * scale1 - player.getDefaultEyeHeight() * (scale1 - 1);
            // }

            if (!SHData.GLIDING.get(player))
            {
                SHData.TICKS_SINCE_GLIDING.incrWithoutNotify(player, 1);
            }

            int qrTicks = Rule.TICKS_QRTIMER.get(player, hero);
            int steelTicks = Rule.TICKS_STEELTIMER.get(player, hero);
            int shadowTicks = Rule.TICKS_SHADOWFORMTIMER.get(player, hero);
            int intangibleTicks = Rule.TICKS_INTANGIBLETIMER.get(player, hero);
            int transformTicks = -1;
            float transformRegen = 1;

            if (hero != null)
            {
                transformTicks = hero.getFuncInt(player, Ability.FUNC_TRANSFORM_TICKS, -1);
                transformRegen = hero.getFuncFloat(player, Ability.FUNC_TRANSFORM_REGEN, 1);
                SHData.PREV_TRANSFORM_MAX.setWithoutNotify(player, transformTicks);
            }
            else
            {
                transformTicks = SHData.PREV_TRANSFORM_MAX.get(player);
            }

            if (qrTicks < 0)
            {
                SHData.QR_TIMER.setWithoutNotify(player, 0F);
            }
            else
            {
                SHHelper.incr(SHData.QR_TIMER, player, qrTicks, flag && scale < defScale && player.dimension != ModDimensions.QUANTUM_REALM_ID);

                if (player.dimension != ModDimensions.QUANTUM_REALM_ID && SHData.QR_TIMER.get(player) >= 1)
                {
                    if (Rule.ALLOW_QR.get(player, hero))
                    {
                        SHHelper.setInQuantumRealm(player);
                    }
                    else
                    {
                        SHData.SHRINKING.setWithoutNotify(player, false);
                        SHData.GROWING.setWithoutNotify(player, true);
                    }
                }
            }

            if (steelTicks < 0)
            {
                SHData.STEEL_COOLDOWN.setWithoutNotify(player, 0F);
            }
            else
            {
                SHHelper.incr(SHData.STEEL_COOLDOWN, player, steelTicks, SHData.STEELED.get(player));

                if (SHData.STEEL_COOLDOWN.get(player) >= 1)
                {
                    SHData.STEELED.setWithoutNotify(player, false);
                }
            }

            if (shadowTicks < 0)
            {
                SHData.SHADOWFORM_COOLDOWN.setWithoutNotify(player, 0F);
            }
            else
            {
                SHHelper.incr(SHData.SHADOWFORM_COOLDOWN, player, shadowTicks, SHData.SHADOWFORM.get(player));

                if (SHData.SHADOWFORM_COOLDOWN.get(player) >= 1)
                {
                    SHHelper.setShadowForm(player, false, false);
                }
            }

            if (intangibleTicks < 0)
            {
                SHData.INTANGIBILITY_COOLDOWN.setWithoutNotify(player, 0F);
            }
            else
            {
                SHHelper.incr(SHData.INTANGIBILITY_COOLDOWN, player, intangibleTicks, hero != null && SHData.INTANGIBLE.get(player) && hero.hasEnabledModifier(player, Ability.ABSOLUTE_INTANGIBILITY) && Ability.ABSOLUTE_INTANGIBILITY.isActive(player));

                if (SHData.INTANGIBILITY_COOLDOWN.get(player) >= 1)
                {
                    SHData.INTANGIBLE.setWithoutNotify(player, false);
                }
            }

            if (transformTicks < 0)
            {
                SHData.TRANSFORM_COOLDOWN.setWithoutNotify(player, 0F);
            }
            else
            {
                SHHelper.incr(SHData.TRANSFORM_COOLDOWN, player, transformTicks * transformRegen, transformTicks, SHData.TRANSFORMED.get(player));

                if (SHData.TRANSFORM_COOLDOWN.get(player) >= 1)
                {
                    SHData.TRANSFORMED.setWithoutNotify(player, false);
                }
            }
        }
        else
        {
            SHData.onUpdate(player);
            SHData.PREV_GLIDING.setWithoutNotify(player, SHData.GLIDING.get(player));
            SHData.PREV_SHADOWFORM.setWithoutNotify(player, SHData.SHADOWFORM.get(player));

            if (player instanceof EntityPlayerMP)
            {
                EntityPlayerMP entityplayermp = (EntityPlayerMP) player;

                if (SHHelper.shouldOverrideReachDistance(player))
                {
                    float originalDistance = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
                    entityplayermp.theItemInWorldManager.setBlockReachDistance(SHHelper.getReachDistance(player, originalDistance));
                }
            }

            if (!player.worldObj.isRemote)
            {
                SHAttributes.applyModifiers(player);
            }

            Hero.updateModifiers(player, hero, event.phase);

            if (SHHelper.getStepDownHeight(player) > 0)
            {
                if (!player.onGround && SHData.PREV_ON_GROUND.get(player) && player.motionY <= 0)
                {
                    int x = MathHelper.floor_double(player.posX);
                    int z = MathHelper.floor_double(player.posZ);
                    float y = MathHelper.floor_double(player.boundingBox.minY);

                    if (player.worldObj.blockExists(x, 0, z))
                    {
                        while (player.worldObj.isAirBlock(x, (int) y, z) && y > 0)
                        {
                            y -= 1;
                        }

                        Block block = player.worldObj.getBlock(x, (int) y, z);

                        if (block.getMaterial() != Material.air && block.isCollidable())
                        {
                            y += block.getBlockBoundsMaxY();
                        }

                        double amount = player.boundingBox.minY - y;

                        if (amount <= SHHelper.getStepDownHeight(player))
                        {
                            player.motionY -= amount;
                        }
                    }
                }
            }

            SHData.PREV_ON_GROUND.setWithoutNotify(player, player.onGround);
        }
    }

    private void handleMaskOpen(EntityPlayer player, Hero hero)
    {
        byte maskOpenTimer = SHData.MASK_OPEN_TIMER.get(player);
        boolean isMaskOpen = SHData.MASK_OPEN.get(player);

        if (maskOpenTimer < 5 && isMaskOpen)
        {
            SHData.MASK_OPEN_TIMER.incrWithoutNotify(player, (byte) 1);
        }
        else if (maskOpenTimer > 0 && !isMaskOpen)
        {
            SHData.MASK_OPEN_TIMER.incrWithoutNotify(player, (byte) -1);
        }

        byte suitOpenTimer = SHData.SUIT_OPEN_TIMER.get(player);
        boolean isSuitOpen = SHData.SUIT_OPEN.get(player);

        if (maskOpenTimer == 0)
        {
            if (suitOpenTimer < 5 && isSuitOpen)
            {
                SHData.SUIT_OPEN_TIMER.incrWithoutNotify(player, (byte) 1);
            }
            else if (suitOpenTimer > 0 && !isSuitOpen)
            {
                SHData.SUIT_OPEN_TIMER.incrWithoutNotify(player, (byte) -1);
            }
        }
        else if (isSuitOpen)
        {
            SHData.MASK_OPEN.setWithoutNotify(player, false);
        }

        if (hero.hasEnabledModifier(player, Ability.SENTRY_MODE) && suitOpenTimer == 5 && isSuitOpen)
        {
            if (Rule.ALLOW_SENTRYMODE.get(player) && player.canPlayerEdit(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ), 0, player.getHeldItem()))
            {
                if (!player.worldObj.isRemote)
                {
                    EntityIronMan entity = new EntityIronMan(player.worldObj);
                    entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                    entity.renderYawOffset = entity.prevRenderYawOffset = player.renderYawOffset;
                    entity.rotationYawHead = entity.prevRotationYawHead = player.rotationYawHead;
                    entity.rotationYaw = entity.prevRotationYaw = player.rotationYaw;
                    entity.ticksExisted = player.ticksExisted;

                    entity.setOwner(player.getUniqueID().toString());
                    entity.set(SHData.HOVERING, SHData.HOVERING.get(player) || !player.onGround);

                    for (int i = 1; i <= 4; ++i)
                    {
                        entity.setCurrentItemOrArmor(i, player.getEquipmentInSlot(i));
                        player.setCurrentItemOrArmor(i, null);
                    }

                    player.worldObj.spawnEntityInWorld(entity);
                    entity.onSpawnWithEgg(null);
                }

                Vec3 vec3 = VectorHelper.getFrontCoordsRenderYawOffset(player, 0.25F, false);
                player.motionX = player.posX - vec3.xCoord;
                player.motionY = player.posY - vec3.yCoord;
                player.motionZ = player.posZ - vec3.zCoord;
            }

            SHData.SUIT_OPEN.setWithoutNotify(player, false);
        }
    }

    private Number getHasItem(EntityPlayer player, Item item)
    {
        for (ItemStack stack : player.inventory.mainInventory)
        {
            if (stack != null && stack.getItem() == item)
            {
                int i = stack.isItemEnchanted() ? 2 : 1;

                if (item == ModItems.captainAmericasShield && ItemCapsShield.isStealth(stack))
                {
                    return i | 4;
                }

                return i;
            }
        }

        return 0;
    }

    @SubscribeEvent
    public void onPlayerBreakBlock(PlayerEvent.BreakSpeed event)
    {
        EntityPlayer player = event.entityPlayer;

        if (SHHelper.hasLeftClickKey(player, SHHelper.getHero(player)))
        {
            event.setCanceled(true);
            return;
        }

        if (SpeedsterHelper.canPlayerRun(player))
        {
            event.newSpeed *= 2;
        }

        event.newSpeed *= ASMHooks.getStrengthScale(player);
    }

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event)
    {
        EntityPlayer player = event.getPlayer();
        ItemStack itemstack = player.getHeldItem();

        if (player.capabilities.isCreativeMode)
        {
            if (itemstack != null && itemstack.getItem() instanceof IPunchWeapon || SHData.SHIELD.get(player) || SHData.BLADE.get(player))
            {
                event.setCanceled(true);
            }
        }
        else if ((itemstack == null || itemstack.getItem() != ModItems.tutridiumPickaxe) && SHHelper.isProtectedEternium(event.block))
        {
            double x = event.x + 0.5;
            double y = event.y + 0.5;
            double z = event.z + 0.5;

            if (player instanceof EntityPlayerMP)
            {
                double dx = x - player.posX;
                double dy = y - player.boundingBox.minY;
                double dz = z - player.posZ;
                double d = -MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);

                d /= 2;
                d *= player.getDistance(x, y, z);
                SHNetworkManager.wrapper.sendTo(new MessageApplyMotion(dx / d, dy / d, dz / d), (EntityPlayerMP) player);
            }

            player.addChatMessage(new ChatComponentTranslation("message.eternium.needTutridium." + (1 + rand.nextInt(4))).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            event.world.createExplosion(null, x, y, z, 2, false);
            event.setCanceled(true);
        }
    }

    protected void onSwitchArmor(EntityLivingBase entity, Hero from, Hero to)
    {
        if (from != null)
        {
            from.onRemoved(entity);
        }

        SHData.SHOOTING.set(entity, false);
    }

    protected void updateArmSwingProgress(EntityPlayer player)
    {
        int i = SHReflection.getArmSwingAnimationEnd(player);

        if (SHData.IS_SWING_IN_PROGRESS.get(player))
        {
            SHData.SWING_PROGRESS_INT.incrWithoutNotify(player, (byte) 1);

            if (SHData.SWING_PROGRESS_INT.get(player) >= i)
            {
                SHData.SWING_PROGRESS_INT.setWithoutNotify(player, (byte) 0);
                SHData.IS_SWING_IN_PROGRESS.setWithoutNotify(player, false);
            }
        }
        else
        {
            SHData.SWING_PROGRESS_INT.setWithoutNotify(player, (byte) 0);
        }

        SHData.SWING_PROGRESS.setWithoutNotify(player, (float) SHData.SWING_PROGRESS_INT.get(player) / i);

        if (player.swingProgressInt == 1 && player.swingProgressInt == SHData.SWING_PROGRESS_INT.get(player))
        {
            double radius = 1.5D;
            AxisAlignedBB aabb = player.boundingBox.expand(radius, 0.0D, radius);
            List<Entity> list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, aabb);

            for (Entity entity : list)
            {
                if (entity instanceof EntityLivingBase && player.canEntityBeSeen(entity))
                {
                    entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0F);
                    SHHelper.knockbackWithoutNotify((EntityLivingBase) entity, player, 0.5F);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event)
    {
        EntityLivingBase entity = event.entityLiving;
        SHEntityData data = SHEntityData.getData(entity);

        data.onUpdate();

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;

            if (!player.worldObj.isRemote)
            {
                if (playersToSync.size() > 0 && playersToSync.contains(player))
                {
                    DataManager.updatePlayerWithServerInfo(player);
                    playersToSync.remove(player);
                }
            }

            SHPlayerData.getData(player).onUpdate();
        }

        StatusEffect statusV9 = null;
        boolean flag = false;

        for (int i = 0; i < data.activeEffects.size(); ++i)
        {
            StatusEffect status = data.activeEffects.get(i);

            if (status.effect == StatEffect.SPEED_SICKNESS && StatusEffect.has(entity, StatEffect.VELOCITY_9))
            {
                data.activeEffects.remove(i);
                flag = true;
                continue;
            }

            if (!status.isMaxDuration() && --status.duration <= 0)
            {
                data.activeEffects.remove(i);
                flag = true;

                if (status.effect == StatEffect.VELOCITY_9)
                {
                    statusV9 = status;

                    if (!SpeedsterHelper.hasSuperSpeed(entity))
                    {
                        SHData.SPEEDING.setWithoutNotify(entity, false);
                    }
                }
            }
        }

        if (flag)
        {
            if (statusV9 != null)
            {
                StatusEffect.add(entity, StatEffect.SPEED_SICKNESS, Short.MAX_VALUE, statusV9.amplifier);
            }

            Collections.sort(data.activeEffects);
        }

        if (entity.dimension == ModDimensions.QUANTUM_REALM_ID)
        {
            entity.fallDistance = 0;

            if (entity instanceof EntityPlayerMP)
            {
                EntityPlayerMP player = (EntityPlayerMP) entity;

                if (entity.posY < 0)
                {
                    player.playerNetServerHandler.setPlayerLocation(player.posX, entity.worldObj.getHeight(), player.posZ, player.rotationYaw, player.rotationPitch);
                }
                else if (entity.posY > entity.worldObj.getHeight())
                {
                    player.playerNetServerHandler.setPlayerLocation(player.posX, 0, player.posZ, player.rotationYaw, player.rotationPitch);
                }
            }
            else
            {
                if (entity.posY < 0)
                {
                    entity.posY = entity.worldObj.getHeight();
                }
                else if (entity.posY > entity.worldObj.getHeight())
                {
                    entity.posY = 0;
                }
            }

            if (!(entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying))
            {
                entity.motionY += 0.075F;
            }

            Hero hero = null;
            flag = true;

            if (ArmorTracker.isTracking(entity))
            {
                hero = SHHelper.getHero(entity);
                flag = hero == null || !hero.hasProperty(entity, Property.BREATHE_SPACE);
            }

            if (flag && !entity.worldObj.isRemote)
            {
                entity.attackEntityFrom(ModDamageSources.SUFFOCATE, Rule.DMG_QR_SUFFOCATE.get(entity, hero));
            }
        }

        if (ArmorTracker.isTracking(entity))
        {
            Hero hero = SHHelper.getHero(entity);
            Hero prevHero = SHData.PREV_HERO.get(entity);

            if (hero != null && hero.hasProperty(entity, Property.BREATHE_UNDERWATER))
            {
                entity.setAir(300);
            }

            if (prevHero != hero)
            {
                onSwitchArmor(entity, prevHero, hero);
                SHData.PREV_HERO.setWithoutNotify(entity, hero);
            }

            SHHelper.prevHero.put(entity, prevHero);
            SHData.PREV_AIMING.setWithoutNotify(entity, SHData.AIMING.get(entity));

            if (entity.getHealth() > entity.getMaxHealth())
            {
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        EntityLivingBase entity = event.entityLiving;
        EntityLivingBase attacker = null;

        if (event.source.getEntity() instanceof EntityLivingBase)
        {
            attacker = (EntityLivingBase) event.source.getEntity();
        }

        if (attacker instanceof EntityPlayer && ArmorTracker.isTracking(entity) && ArmorTracker.isTracking(attacker))
        {
            if (SHHelper.getHero(entity) == Heroes.deadpool_xmen && SHHelper.getHero(attacker) == Heroes.captain_america)
            {
                ((EntityPlayer) attacker).triggerAchievement(SHAchievements.KILL_DEADPOOL);
            }
        }

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;

            if (event.source == DamageSource.fall || event.source == ModDamageSources.FLY_INTO_WALL)
            {
                if (SHData.TICKS_SINCE_GLIDING.get(player) < 10)
                {
                    player.triggerAchievement(SHAchievements.FACEPLANT);
                }
            }

            SHData.onDeath(player);
            SHEntityData.getData(player).activeEffects.clear();
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event)
    {
        EntityLivingBase attacker = null;

        if (event.source.getEntity() instanceof EntityLivingBase)
        {
            attacker = (EntityLivingBase) event.source.getEntity();

            if (ArmorTracker.isTracking(attacker))
            {
                boolean flag = false;

                if (SHHelper.hasEnabledModifier(attacker, Ability.RETRACTABLE_SHIELD) && SHData.SHIELD_BLOCKING.get(attacker))
                {
                    flag = true;
                }
                else if (SHHelper.hasEnabledModifier(attacker, Ability.UMBRAKINESIS) && SHData.SHADOWFORM.get(attacker) || SHData.INTANGIBLE.get(attacker) && SHHelper.hasEnabledModifier(attacker, Ability.ABSOLUTE_INTANGIBILITY) && Ability.ABSOLUTE_INTANGIBILITY.isActive(attacker))
                {
                    flag = true;
                }
                else if (SHHelper.hasLeftClickKey(attacker, SHHelper.getHero(attacker)))
                {
                    flag = true;
                }

                if (flag && FiskServerUtils.isMeleeDamage(event.source))
                {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        EntityLivingBase entity = event.entityLiving;

        if (ArmorTracker.isTracking(entity))
        {
            Hero hero = SHHelper.getHero(entity);

            if (hero != null && !hero.canTakeDamage(entity, attacker, event.source, event.ammount))
            {
                event.setCanceled(true);
                return;
            }
        }

        if (SHHelper.canShieldBlock(entity, event.source, SHHelper.getShieldBlocking(entity)))
        {
            if (entity.hurtResistantTime == 0)
            {
                ItemStack heldItem = entity.getHeldItem();

                if (heldItem != null && heldItem.getItem() == ModItems.captainAmericasShield)
                {
                    heldItem.damageItem(1 + MathHelper.floor_double(event.ammount), entity);
                }

                if (event.source == DamageSource.fall || FiskServerUtils.isMeleeDamage(event.source) || event.source.isExplosion())
                {
                    entity.hurtResistantTime = 7;

                    if (attacker != null)
                    {
                        SHHelper.knockback(entity, attacker, Math.min(event.ammount, 20) / 5);
                    }

                    if (!event.source.isExplosion())
                    {
                        entity.worldObj.playSoundAtEntity(entity, SHSounds.ITEM_SHIELD_HIT.toString(), 0.6F, 0.8F + 0.1F * rand.nextFloat() - Math.min(event.ammount, 10) / 50);
                    }
                }
            }

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        EntityLivingBase attacker = null;

        if (event.source.getEntity() instanceof EntityLivingBase)
        {
            attacker = (EntityLivingBase) event.source.getEntity();
        }

        EntityLivingBase entity = event.entityLiving;
        Hero hero = null;

        float mult = SHHelper.getDamageMult(entity, event.source);
        float originalAmount = event.ammount;

        if (ArmorTracker.isTracking(entity))
        {
            hero = SHHelper.getHero(entity);

            if (hero != null)
            {
                event.ammount = hero.damageTaken(entity, attacker, event.source, event.ammount, originalAmount);
            }
        }

        if (attacker != null && ArmorTracker.isTracking(attacker))
        {
            Hero hero1 = SHHelper.getHero(attacker);

            if (FiskServerUtils.isMeleeDamage(event.source))
            {
                int i = 0;

                if (entity instanceof EntityLivingBase)
                {
                    i += EnchantmentHelper.getKnockbackModifier(attacker, entity);
                }

                if (attacker.isSprinting())
                {
                    ++i;
                }

                float knockback = SHAttributes.KNOCKBACK.get(attacker, hero1, i);

                if (knockback > 0)
                {
                    entity.addVelocity(-MathHelper.sin(attacker.rotationYaw * (float) Math.PI / 180F) * knockback * 0.5, 0, MathHelper.cos(attacker.rotationYaw * (float) Math.PI / 180F) * knockback * 0.5);
                }

                event.ammount = SHAttributes.ATTACK_DAMAGE.get(attacker, hero1, event.ammount);

                if (SHHelper.isHoldingSword(attacker))
                {
                    event.ammount = SHAttributes.SWORD_DAMAGE.get(attacker, hero1, event.ammount);
                }
                else
                {
                    event.ammount = SHAttributes.PUNCH_DAMAGE.get(attacker, hero1, event.ammount);
                }

                if (attacker instanceof EntityPlayer && SpeedsterHelper.hasSuperSpeed(attacker))
                {
                    entity.hurtResistantTime = 0;
                }
            }

            if (hero1 != null)
            {
                event.ammount = hero1.damageDealt(attacker, entity, event.source, event.ammount, originalAmount);
            }
        }

        if (mult > 0)
        {
            if (hero != null)
            {
                mult = 1 - hero.damageReduction(entity, event.source, 1 - mult);
            }

            event.ammount *= mult;
        }

        if (event.ammount > 0)
        {
            SHData.TIME_SINCE_DAMAGED.set(entity, (short) 0);

            if (SHData.LIGHTSOUT.get(entity))
            {
                SHData.LIGHTSOUT_TIMER.set(entity, 0F);
            }
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingJumpEvent event)
    {
        EntityLivingBase entity = event.entityLiving;

        if (ArmorTracker.isTracking(entity))
        {
            Hero hero = SHHelper.getHero(entity);
            float scale = ASMHooks.getStrengthScale(entity);

            if (hero != null && entity.isSprinting() && hero.hasEnabledModifier(entity, Ability.LEAPING))
            {
                entity.moveFlying(entity.moveStrafing, entity.moveForward, 1.5F * scale);
                entity.motionY += 0.2F * scale;
            }

            entity.motionY += 0.15 * (SHAttributes.JUMP_HEIGHT.get(entity, hero, 1) - 1);
            entity.motionY += 0.185 * (scale - 1);
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event)
    {
        EntityLivingBase entity = event.entityLiving;

        if (ArmorTracker.isTracking(entity))
        {
            Hero hero = SHHelper.getHero(entity);

            if (hero != null && (hero.hasEnabledModifier(entity, Ability.PROPELLED_FLIGHT) || hero.hasEnabledModifier(entity, Ability.FLIGHT)))
            {
                event.setCanceled(true);
                return;
            }

            event.distance = SHAttributes.FALL_RESISTANCE.get(entity, hero, event.distance);
            float scale = SHData.SCALE.get(entity);

            if (scale > 1)
            {
                event.distance /= scale;
            }

            if (!entity.worldObj.isRemote && event.distance > 0 && entity instanceof EntityPlayer && hero == Heroes.deadpool_xmen)
            {
                SHData.SUPERHERO_LANDING.setWithoutNotify(entity, true);
            }
        }
    }

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event)
    {
        List<ChunkPosition> blocks = event.getAffectedBlocks();
        World world = event.world;

        for (int i = 0; i < blocks.size(); ++i)
        {
            ChunkPosition pos = blocks.get(i);
            int x = pos.chunkPosX;
            int y = pos.chunkPosY;
            int z = pos.chunkPosZ;

            if (world.getBlock(x, y, z) == ModBlocks.iceLayer)
            {
                ForgeDirection dir = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z)).getOpposite();
                x += dir.offsetX;
                y += dir.offsetY;
                z += dir.offsetZ;

                if (!world.isAirBlock(x, y, z) && world.getBlock(x, y, z).getBlockHardness(world, x, y, z) >= 0)
                {
                    blocks.add(new ChunkPosition(x, y, z));
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemCrafted(ItemCraftedEvent event)
    {
        EntityPlayer player = event.player;
        ItemStack stack = event.crafting;

        if (stack.getItem() == Item.getItemFromBlock(ModBlocks.suitFabricator))
        {
            player.triggerAchievement(SHAchievements.SUIT_FABRICATOR);
        }
        else if (stack.getItem() == Item.getItemFromBlock(ModBlocks.suitIterator))
        {
            player.triggerAchievement(SHAchievements.SUIT_ITERATOR);
        }
        else if (stack.getItem() == Item.getItemFromBlock(ModBlocks.cosmicFabricator))
        {
            player.triggerAchievement(SHAchievements.COSMIC_FABRICATOR);
        }
        else if (stack.getItem() == ModItems.gameboii)
        {
            IInventory inventory = event.craftMatrix;
            boolean flag = true;

            for (int i = 0; i < inventory.getSizeInventory(); ++i)
            {
                ItemStack stack1 = inventory.getStackInSlot(i);

                if (stack1 != null && (stack1.getItem() == ModItems.gameboiiCartridge || stack1.getItem() == ModItems.gameboii))
                {
                    flag = false;
                    break;
                }
            }

            if (flag)
            {
                player.triggerAchievement(SHAchievements.GAMEBOII);
            }
        }
        else if (stack.getItem() == ModItems.tachyonPrototype || stack.getItem() == ModItems.tachyonDevice)
        {
            IInventory inventory = event.craftMatrix;
            boolean flag = true;

            for (int i = 0; i < inventory.getSizeInventory(); ++i)
            {
                ItemStack stack1 = inventory.getStackInSlot(i);

                if (stack1 != null && stack1.getItem() == Item.getItemFromBlock(ModBlocks.tachyonicParticleShell))
                {
                    flag = false;
                    break;
                }
            }

            if (flag)
            {
                player.triggerAchievement(SHAchievements.TACHYONS);
            }
        }
        else if (stack.getItem() == ModItems.displayCase)
        {
            IInventory inventory = event.craftMatrix;

            for (int i = 0; i < inventory.getSizeInventory(); ++i)
            {
                ItemStack stack1 = inventory.getStackInSlot(i);

                if (stack1 != null && stack1.getItem() == ModItems.displayCase)
                {
                    player.triggerAchievement(SHAchievements.DISPLAY_CASE);
                    break;
                }
            }
        }
        else if (stack.getItem() == ModItems.trickArrow && player instanceof EntityPlayerMP && !((EntityPlayerMP) player).func_147099_x().hasAchievementUnlocked(SHAchievements.ALL_ARROWS))
        {
            ArrowType arrow = ArrowType.getArrowById(stack.getItemDamage());

            if (arrow != null)
            {
                Map<ArrowType, Integer> map = SHPlayerData.getData(player).arrowCollection;
                int i = Math.min(map.getOrDefault(arrow, 0) + stack.stackSize, 32);

                map.put(arrow, i);
                SHPlayerData.getData(player).arrowsNeedUpdate = true;

                if (i >= 16 && DataManager.getArrowsCollected(player) >= DataManager.serverArrows)
                {
                    player.triggerAchievement(SHAchievements.ALL_ARROWS);
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event)
    {
        EntityPlayer player = event.player;

        if (event.message.equalsIgnoreCase("m'lady") && SHHelper.getHero(player) == Heroes.senor_cactus)
        {
            SHData.HAT_TIP.set(player, 1.0F);
        }
    }

    @SubscribeEvent
    public void onEnderTeleport(EnderTeleportEvent event)
    {
        if (SHHelper.isEarthCrackTarget(event.entityLiving))
        {
            event.setCanceled(true);
        }
    }
}
