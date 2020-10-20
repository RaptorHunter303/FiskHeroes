package com.fiskmods.heroes.util;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fiskmods.heroes.SHReflection;
import com.fiskmods.heroes.asm.ASMHooks;
import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.client.render.Lightning;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.HeroRendererMartianManhunter;
import com.fiskmods.heroes.common.BlockStack;
import com.fiskmods.heroes.common.DimensionalCoords;
import com.fiskmods.heroes.common.achievement.SHAchievements;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.SHPlayerData;
import com.fiskmods.heroes.common.entity.EntityEarthCrack;
import com.fiskmods.heroes.common.entity.EntitySpellDuplicate;
import com.fiskmods.heroes.common.entity.arrow.EntityGrappleArrow;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.equipment.EnumEquipment;
import com.fiskmods.heroes.common.event.ArmorTracker;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Hero.Property;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.hero.ItemHeroArmor;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityEquipment;
import com.fiskmods.heroes.common.hero.modifier.AbilitySizeManipulation;
import com.fiskmods.heroes.common.hero.modifier.AbilitySpellcasting;
import com.fiskmods.heroes.common.hero.modifier.HeroModifier;
import com.fiskmods.heroes.common.item.IDualItem;
import com.fiskmods.heroes.common.item.IPunchWeapon;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.network.MessageKnockback;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.common.spell.Spell;
import com.fiskmods.heroes.common.world.ModDimensions;
import com.fiskmods.heroes.common.world.TeleporterQuantumRealm;
import com.google.gson.Gson;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class SHHelper
{
    public static Map<EntityLivingBase, Hero> prevHero = new HashMap<>();

    public static byte getHeroSignature(ItemStack[] itemstacks)
    {
        HeroIteration prev = null;
        byte b = 0;

        for (int i = 0; i < itemstacks.length; ++i)
        {
            HeroIteration iter = getHeroIterFromArmor(itemstacks[i]);

            if (iter != null)
            {
                if (prev != null && prev != iter)
                {
                    return 0;
                }

                b |= 1 << i;
                prev = iter;
            }
            else if (itemstacks[i] != null)
            {
                return 0;
            }
        }

        return prev != null ? b : 0;
    }

    public static ItemStack[] getEquipment(EntityLivingBase entity)
    {
        return new ItemStack[] {entity.getEquipmentInSlot(4), entity.getEquipmentInSlot(3), entity.getEquipmentInSlot(2), entity.getEquipmentInSlot(1)};
    }

    public static ItemStack[] getEquipment(IInventory inventory, int slotOffset)
    {
        return new ItemStack[] {inventory.getStackInSlot(slotOffset), inventory.getStackInSlot(slotOffset + 1), inventory.getStackInSlot(slotOffset + 2), inventory.getStackInSlot(slotOffset + 3)};
    }

    public static Hero getHeroFromArmor(ItemStack itemstack)
    {
        HeroIteration iter = getHeroIterFromArmor(itemstack);
        return iter != null ? iter.hero : null;
    }

    public static HeroIteration getHeroIterFromArmor(ItemStack itemstack)
    {
        if (itemstack != null && itemstack.getItem() instanceof ItemHeroArmor)
        {
            return ItemHeroArmor.get(itemstack);
        }

        return null;
    }

    public static boolean isHeroArmor(ItemStack stack, Hero hero, int armorSlot)
    {
        return getHeroFromArmor(stack) == hero && ((ItemHeroArmor) stack.getItem()).armorType == armorSlot;
    }

    public static boolean isHeroArmor(ItemStack stack, HeroIteration iter, int armorSlot)
    {
        return getHeroIterFromArmor(stack) == iter && ((ItemHeroArmor) stack.getItem()).armorType == armorSlot;
    }

    public static Hero getHero(ItemStack[] itemstacks)
    {
        Hero hero = getHeroFromArmor(FiskServerUtils.nonNull(itemstacks));
        return hero != null && hero.getArmorSignature() == getHeroSignature(itemstacks) ? hero : null;
    }

    public static HeroIteration getHeroIter(ItemStack[] itemstacks)
    {
        HeroIteration iter = getHeroIterFromArmor(FiskServerUtils.nonNull(itemstacks));
        return iter != null && iter.hero.getArmorSignature() == getHeroSignature(itemstacks) ? iter : null;
    }

    public static Hero getHeroFromArmor(EntityLivingBase entity, int slot)
    {
        return getHeroFromArmor(entity.getEquipmentInSlot(slot + 1));
    }

    public static HeroIteration getHeroIterFromArmor(EntityLivingBase entity, int slot)
    {
        return getHeroIterFromArmor(entity.getEquipmentInSlot(slot + 1));
    }

    public static Hero getHero(EntityLivingBase entity)
    {
        return getHero(getEquipment(entity));
    }

    public static Hero getHero(Entity entity)
    {
        return entity instanceof EntityLivingBase ? getHero((EntityLivingBase) entity) : null;
    }

    public static HeroIteration getHeroIter(EntityLivingBase entity)
    {
        return getHeroIter(getEquipment(entity));
    }

    public static Hero getPrevHero(EntityLivingBase entity)
    {
        return prevHero.get(entity);
    }

    public static boolean isHero(ItemStack[] itemstacks)
    {
        return getHero(itemstacks) != null;
    }

    public static boolean isHero(EntityLivingBase entity)
    {
        return isHero(getEquipment(entity));
    }

    public static boolean isHero(Entity entity)
    {
        return entity instanceof EntityLivingBase && isHero((EntityLivingBase) entity);
    }

    public static boolean hasModifier(EntityLivingBase entity, HeroModifier modifier)
    {
        Hero hero = getHero(entity);
        return hero != null && hero.hasModifier(modifier);
    }

    public static boolean hasEnabledModifier(EntityLivingBase entity, HeroModifier modifier)
    {
        Hero hero = getHero(entity);
        return hero != null && hero.hasEnabledModifier(entity, modifier);
    }

    public static boolean hasProperty(EntityLivingBase entity, Property property)
    {
        Hero hero = getHero(entity);
        return hero != null && hero.hasProperty(entity, property);
    }

    public static boolean hasPermission(EntityLivingBase entity, String permission)
    {
        Hero hero = getHero(entity);
        return hero != null && hero.hasPermission(entity, permission);
    }

    public static void doSmokeExplosion(World world, double x, double y, double z)
    {
        for (int i = 0; i < 300; ++i)
        {
            float spread = 0.3F;
            double motX = world.rand.nextFloat() * 2 - 1;
            double motY = world.rand.nextFloat() * 2 - 1;
            double motZ = world.rand.nextFloat() * 2 - 1;
            SHParticleType.THICK_SMOKE.spawn(x, y, z, motX * spread, motY * spread, motZ * spread);
        }
    }

    public static int getShieldBlocking(EntityLivingBase entity)
    {
        if (SHData.SHIELD_BLOCKING.get(entity))
        {
            return 1;
        }

        ItemStack heldItem = entity.getHeldItem();
        return entity instanceof EntityPlayer && heldItem != null && heldItem.getItem() == ModItems.captainAmericasShield && ((EntityPlayer) entity).isUsingItem() ? 2 : 0;
    }

    public static boolean canShieldBlock(EntityLivingBase entity, DamageSource source, int blockingType)
    {
        if (blockingType > 0)
        {
            if (blockingType > 1 && source == DamageSource.fall && entity.rotationPitch > 60)
            {
                return true;
            }

            Entity src = source.getSourceOfDamage();

            if (src != null && !source.isUnblockable())
            {
                return FiskServerUtils.isEntityLookingAt(entity, src, 0.75);
            }
        }

        return false;
    }

    public static boolean canArmorBlock(EntityLivingBase entity, DamageSource source)
    {
        return getDamageMult(entity, source) > 0;
    }

    public static float getDamageMult(EntityLivingBase entity, DamageSource source)
    {
        if (ArmorTracker.isTracking(entity))
        {
            float f = SHAttributes.getArmorProtection(entity);

            if (!"indirectMagic".equals(source.damageType))
            {
                if (source == DamageSource.onFire || source == DamageSource.lava || source == DamageSource.magic)
                {
                    f *= 0.5F;
                }
                else if (source.isUnblockable())
                {
                    return 1;
                }
            }

            return 1 - f;
        }

        return 1;
    }

    public static void knockbackWithoutNotify(EntityLivingBase entity, Entity attacker, float amount)
    {
        if (attacker != null)
        {
            double d1 = attacker.posX - entity.posX;
            double d0;

            for (d0 = attacker.posZ - entity.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
            {
                d1 = (Math.random() - Math.random()) * 0.01D;
            }

            entity.attackedAtYaw = (float) (Math.atan2(d0, d1) * 180.0D / Math.PI) - entity.rotationYaw;
            entity.knockBack(attacker, 0, d1 * amount, d0 * amount);
            entity.motionX *= amount;
            entity.motionZ *= amount;
        }
    }

    public static void knockback(EntityLivingBase entity, Entity attacker, float amount)
    {
        if (!entity.worldObj.isRemote)
        {
            SHNetworkManager.wrapper.sendToDimension(new MessageKnockback(entity, attacker, amount), entity.dimension);
        }

        knockbackWithoutNotify(entity, attacker, amount);
    }

    public static float getDefaultScale(EntityPlayer player)
    {
        Hero hero = getHero(player);

        if (hero != null)
        {
            float defScale = hero.getDefaultScale(player);

            if (hero.hasEnabledModifier(player, Ability.SHAPE_SHIFTING))
            {
                float f = SHData.SHAPE_SHIFT_TIMER.get(player);
                String disguise = SHData.DISGUISE.get(player);
                String from = SHData.SHAPE_SHIFTING_FROM.get(player);
                String to = SHData.SHAPE_SHIFTING_TO.get(player);

                if (StringUtils.isNullOrEmpty(from) && StringUtils.isNullOrEmpty(to) && StringUtils.isNullOrEmpty(disguise)) // Normal
                {
                    return defScale;
                }
                else if (!StringUtils.isNullOrEmpty(from) && !StringUtils.isNullOrEmpty(to)) // Disguise -> disguise
                {
                    return 1;
                }
                else if (!StringUtils.isNullOrEmpty(from) && StringUtils.isNullOrEmpty(to)) // Disguise -> normal
                {
                    f = 1 - f;
                }

                return FiskServerUtils.interpolate(1, defScale, f); // Normal -> disguise
            }

            return defScale;
        }

        return 1;
    }

    public static Lightning createLightning(Vec3 color, long seed, float length, float scale)
    {
        Random rand = new Random();

        if (seed != 0)
        {
            rand.setSeed(seed);
        }

        Lightning lightning = new Lightning(rand.nextFloat() * length, scale, color).setRotation(rand.nextFloat() * 360, rand.nextFloat() * 360, rand.nextFloat() * 360);
        branchLightning(lightning, rand, length, scale, 0);

        return lightning;
    }

    private static void branchLightning(Lightning parent, Random rand, float length, float scale, int branch)
    {
        Lightning lightning = new Lightning(rand.nextFloat() * length, scale).setRotation(rand.nextFloat() * rand.nextFloat() * 90, rand.nextFloat() * rand.nextFloat() * 90, rand.nextFloat() * rand.nextFloat() * 90);
        parent.addChild(lightning);

        if (branch < 10)
        {
            if (rand.nextDouble() < 1 - branch * 0.1)
            {
                branchLightning(lightning, rand, length, scale, ++branch);
            }
        }

        if (rand.nextDouble() < 0.1)
        {
            branchLightning(parent, rand, length, scale, 7);
        }
    }

    public static EnumEquipment getUtilityBelt(EntityPlayer player)
    {
        Hero hero = getHero(player);

        if (hero != null)
        {
            for (Ability ability : hero.getAbilities())
            {
                if (ability instanceof AbilityEquipment)
                {
                    AbilityEquipment abilityEquipment = (AbilityEquipment) ability;
                    int index = SHData.UTILITY_BELT_TYPE.get(player);

                    if (index < 0)
                    {
                        break;
                    }

                    return abilityEquipment.get()[MathHelper.clamp_int(index, 0, abilityEquipment.get().length - 1)];
                }
            }
        }

        return EnumEquipment.FISTS;
    }

    public static boolean shouldOverrideScale(EntityPlayer player, float scale)
    {
        Hero hero = getHero(player);
        float widthScale = scale;
        float heightScale = scale;

        if (hero == null && getPrevHero(player) != null || !SHData.GLIDING.get(player) && SHData.PREV_GLIDING.get(player) || !SHData.SHADOWFORM.get(player) && SHData.PREV_SHADOWFORM.get(player))
        {
            return true;
        }

        if (SHData.GLIDING.get(player))
        {
            heightScale *= 0.6F / 1.8F;
        }
        else if (SHData.SHADOWFORM.get(player))
        {
            heightScale *= 0.2F / 1.8F;
        }

        return !player.isEntityAlive() || (hero != null || getPrevHero(player) != null || SHData.GLIDING.get(player) || SHData.PREV_GLIDING.get(player) || SHData.SHADOWFORM.get(player) || SHData.PREV_SHADOWFORM.get(player)) && (heightScale != player.height / 1.8F || widthScale != player.width / 0.6F);
    }

    public static boolean shouldOverrideReachDistance(EntityPlayer player)
    {
        return ASMHooks.getModifiedEntityScale(player) > 1;
    }

    public static float getReachDistance(EntityPlayer player, double dist)
    {
        if (SHData.isTracking(player))
        {
            float scale = SHData.SCALE.get(player);
            return (float) (scale > 1 ? Math.pow(scale, 1.0 / 3) * dist : dist);
        }

        return (float) dist;
//        return (float) (Math.max((ASMHooks.getModifiedEntityScale(player) - 1) * 0.5 + 1, 1) * dist);
    }

    public static void swingOffhandItem(EntityPlayer player)
    {
        ItemStack stack = player.getHeldItem();

        if (stack != null && stack.getItem() != null)
        {
            Item item = stack.getItem();

            if (item instanceof IDualItem && ((IDualItem) item).onEntitySwingOffHand(player, stack))
            {
                return;
            }
        }

        if (!SHData.IS_SWING_IN_PROGRESS.get(player) || SHData.SWING_PROGRESS_INT.get(player) >= SHReflection.getArmSwingAnimationEnd(player) / 2 || SHData.SWING_PROGRESS_INT.get(player) < 0)
        {
            SHData.SWING_PROGRESS_INT.setWithoutNotify(player, (byte) -1);
            SHData.IS_SWING_IN_PROGRESS.setWithoutNotify(player, true);
        }
    }

    public static void setInQuantumRealm(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP && !player.worldObj.isRemote)
        {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            DimensionalCoords coords = SHData.QR_ORIGIN.get(player);

            SHData.QR_ORIGIN.set(player, new DimensionalCoords(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.boundingBox.minY), MathHelper.floor_double(player.posZ), player.dimension));

            if (player.dimension != ModDimensions.QUANTUM_REALM_ID)
            {
                if (coords.equals(new DimensionalCoords()) || coords.dimension != ModDimensions.QUANTUM_REALM_ID)
                {
                    coords.posX = MathHelper.getRandomIntegerInRange(player.getRNG(), -SHConfig.get().qrSpread, SHConfig.get().qrSpread);
                    coords.posY = MathHelper.getRandomIntegerInRange(player.getRNG(), 0, player.worldObj.getHeight());
                    coords.posZ = MathHelper.getRandomIntegerInRange(player.getRNG(), -SHConfig.get().qrSpread, SHConfig.get().qrSpread);
                    coords.dimension = ModDimensions.QUANTUM_REALM_ID;
                }

                SHData.SCALE.set(player, getDefaultScale(player));
                SHData.QR_TIMER.set(player, 0.0F);

                float f = 10;
                playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, ModDimensions.QUANTUM_REALM_ID, new TeleporterQuantumRealm(playerMP.mcServer.worldServerForDimension(ModDimensions.QUANTUM_REALM_ID)));
                playerMP.playerNetServerHandler.setPlayerLocation(coords.posX + +(Math.random() * 2 - 1) * f, coords.posY + +(Math.random() * 2 - 1) * f, coords.posZ + +(Math.random() * 2 - 1) * f, player.rotationYaw, player.rotationPitch);

                player.triggerAchievement(SHAchievements.QUANTUM_REALM);
            }
            else
            {
                if (coords.equals(new DimensionalCoords()) || coords.dimension == ModDimensions.QUANTUM_REALM_ID)
                {
                    player.travelToDimension(0);
                }
                else
                {
                    playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, coords.dimension, new TeleporterQuantumRealm(playerMP.mcServer.worldServerForDimension(coords.dimension)));
                    playerMP.playerNetServerHandler.setPlayerLocation(coords.posX, coords.posY, coords.posZ, player.rotationYaw, player.rotationPitch);
                }

                Hero hero = getHero(player);

                if (hero != null && hero.getFuncBoolean(player, AbilitySizeManipulation.FUNC_INSTANT, false))
                {
                    SHData.SCALE.set(player, hero.getFuncFloat(player, AbilitySizeManipulation.FUNC_MIN_SIZE, 1));
                    SHData.GROWING.set(player, true);
                    SHData.SHRINKING.set(player, false);
                }
            }
        }
    }

    public static int getSideStandingOn(EntityPlayer player)
    {
        return 1;
    }

    public static float getStepDownHeight(EntityPlayer player)
    {
        return SpeedsterHelper.canPlayerRun(player) ? 5 : 0;
    }

    public static MovingObjectPosition rayTrace(EntityLivingBase entity, double range, int flags, float partialTicks)
    {
        return rayTrace(entity, range, 0, flags, partialTicks);
    }

    /**
     * Flags: 1 = Stop at non-collidable blocks 2 = Stop at liquids 4 = ?
     */
    public static MovingObjectPosition rayTrace(EntityLivingBase entity, double range, float borderSize, int flags, float partialTicks)
    {
        Vec3 look = entity.getLook(partialTicks);
        Vec3 src = VectorHelper.getPosition(entity, partialTicks).addVector(0, VectorHelper.getOffset(entity), 0);
        Vec3 dest = VectorHelper.add(src, VectorHelper.multiply(look, range));

        MovingObjectPosition rayTrace = entity.worldObj.func_147447_a(VectorHelper.copy(src), VectorHelper.copy(dest), (flags & 1) == 1, (flags & 2) == 2, (flags & 4) == 4);
        Entity pointedEntity = null;
        Vec3 hitVec = null;

        double length = rayTrace != null ? rayTrace.hitVec.distanceTo(src) : range;
        double newLength = length;

        for (Entity target : (List<Entity>) entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range).expand(1, 1, 1)))
        {
            if (target.canBeCollidedWith())
            {
                float border = target.getCollisionBorderSize() + borderSize;
                AxisAlignedBB aabb = target.boundingBox.expand(border, border, border);
                MovingObjectPosition mop = aabb.calculateIntercept(src, dest);

                if (aabb.isVecInside(src))
                {
                    if (0 < newLength || newLength == 0)
                    {
                        pointedEntity = target;
                        hitVec = mop == null ? src : mop.hitVec;
                        newLength = 0;
                    }
                }
                else if (mop != null)
                {
                    double distance = src.distanceTo(mop.hitVec);

                    if (distance < newLength || newLength == 0)
                    {
                        if (target == entity.ridingEntity && !target.canRiderInteract())
                        {
                            if (newLength == 0)
                            {
                                pointedEntity = target;
                                hitVec = mop.hitVec;
                            }
                        }
                        else
                        {
                            pointedEntity = target;
                            hitVec = mop.hitVec;
                            newLength = distance;
                        }
                    }
                }
            }
        }

        if (pointedEntity != null && (newLength < length || rayTrace == null))
        {
            rayTrace = new MovingObjectPosition(pointedEntity, hitVec);
        }

        if (rayTrace != null)
        {
            rayTrace.hitInfo = newLength;
        }

        return rayTrace;
    }

    public static BlockStack getHeatVisionResult(Block block, int metadata)
    {
        return BlockStack.fromItemStack(FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(block, 1, metadata == -1 ? OreDictionary.WILDCARD_VALUE : metadata)));
    }

    public static boolean hasCollectedHero(EntityPlayer player, Hero hero)
    {
        return hero != null && hero.getArmorSignature() == SHPlayerData.getData(player).heroCollection.getOrDefault(hero, (byte) 0);
    }

    public static boolean canPlayerSeeMartianInvis(EntityPlayer clientPlayer)
    {
        return SHData.PENETRATE_MARTIAN_INVIS.get(clientPlayer);
    }

    public static float getInvisibility(EntityPlayer player, EntityPlayer clientPlayer)
    {
        float f = SHData.INVISIBILITY_TIMER.interpolate(player);
        float invis = 1;

        if (canPlayerSeeMartianInvis(clientPlayer))
        {
            invis = 0.4F;
        }
        else if (HeroRenderer.get(getHeroIter(player)) instanceof HeroRendererMartianManhunter)
        {
            double motX = player.prevPosX - player.posX;
            double motY = player.prevPosY - player.posY;
            double motZ = player.prevPosZ - player.posZ;
            float f1 = (float) MathHelper.clamp_double(Math.sqrt(motX * motX + motY * motY + motZ * motZ) / 10, 0, 1);

            invis = MathHelper.clamp_float(f1, 0, 0.2F);
        }
        else
        {
            return f > 0 ? 0 : 1;
        }

        return FiskServerUtils.interpolate(1, invis, f);
    }

    public static boolean ignite(Entity entity, int seconds)
    {
        if (seconds > 0 && (entity instanceof EntityLivingBase || entity instanceof EntityGrappleArrow))
        {
            entity.setFire(seconds);
            return true;
        }

        return false;
    }

    public static void melt(World world, int x, int y, int z, int flags)
    {
        if (!world.isRemote)
        {
            Block block = world.getBlock(x, y, z);

            if ((flags & 1) == 1 && block == ModBlocks.iceLayer)
            {
                world.setBlockToAir(x, y, z);
            }
            else if ((flags & 2) == 2 && block == ModBlocks.frostedIce)
            {
                block.updateTick(world, x, y, z, world.rand);
            }
            else if ((flags & 4) == 4 && block == Blocks.ice)
            {
                if (world.provider.isHellWorld)
                {
                    world.setBlockToAir(x, y, z);
                }
                else
                {
                    world.setBlock(x, y, z, Blocks.water);
                }
            }
            else if ((flags & 8) == 8)
            {
                BlockStack stack = getHeatVisionResult(block, world.getBlockMetadata(x, y, z));

                if (stack != null)
                {
                    world.setBlock(x, y, z, stack.block, stack.metadata, 3);
                }
            }
        }
    }

    public static boolean isHoldingSword(EntityLivingBase entity)
    {
        ItemStack heldItem = entity.getHeldItem();
        return heldItem != null && (heldItem.getItem() instanceof ItemSword || heldItem.getItem() instanceof ItemAxe) && !(heldItem.getItem() instanceof IPunchWeapon);
    }

    public static void incr(SHData<Float> data, Entity entity, float ticksIncr, float ticksDecr, boolean incr, boolean decr)
    {
        float f = data.get(entity);

        if (incr && f < 1)
        {
            data.incrWithoutNotify(entity, 1 / ticksIncr);
        }
        else if (decr && f > 0)
        {
            data.incrWithoutNotify(entity, -1 / ticksDecr);
        }

        data.clampWithoutNotify(entity, 0.0F, 1.0F);
    }

    public static void incr(SHData<Float> data, Entity entity, float ticks, boolean incr, boolean decr)
    {
        incr(data, entity, ticks, ticks, incr, decr);
    }

    public static void incr(SHData<Float> data, Entity entity, float ticksIncr, float ticksDecr, boolean condition)
    {
        incr(data, entity, ticksIncr, ticksDecr, condition, !condition);
    }

    public static void incr(SHData<Float> data, Entity entity, float ticks, boolean condition)
    {
        incr(data, entity, ticks, ticks, condition);
    }

    public static void setShadowForm(Entity entity, boolean flag, boolean notify)
    {
        if (notify)
        {
            SHData.SHADOWFORM.set(entity, flag);
        }
        else
        {
            SHData.SHADOWFORM.setWithoutNotify(entity, flag);
        }

        if (flag)
        {
            entity.motionY += 0.55;
        }
    }

    public static boolean isProtectedEternium(Block block)
    {
        return block == ModBlocks.eterniumOre || block == ModBlocks.eterniumStone || block == ModBlocks.eterniumBlock || block == ModBlocks.superchargedEternium || block == ModBlocks.nexusBricks || block == ModBlocks.nexusBrickStairs || block == ModBlocks.nexusBrickSlab || block == ModBlocks.nexusBrickDoubleSlab;
    }

    public static boolean isPoisonEternium(Block block)
    {
        return block == ModBlocks.eterniumOre || block == ModBlocks.eterniumBlock || block == ModBlocks.superchargedEternium;
    }

    public static boolean isPoisonEternium(Item item)
    {
        return item == ModItems.eterniumShard || isPoisonEternium(Block.getBlockFromItem(item));
    }

    public static boolean hasLeftClickKey(EntityLivingBase entity, Hero hero)
    {
        if (hero != null && hero.hasKeyBinding(-1))
        {
            Set<String> keys = hero.getKeyBindsMatching(-1);

            for (String key : keys)
            {
                if (hero.isKeyBindEnabled(entity, key) && (key != "AIM" || hero.getFuncBoolean(entity, "canAim", true)))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isEarthCrackTarget(EntityLivingBase entity)
    {
        for (Object obj : entity.worldObj.loadedEntityList)
        {
            if (obj instanceof EntityEarthCrack && ((EntityEarthCrack) obj).target == entity)
            {
                return true;
            }
        }

        return false;
    }

    public static Collection<Spell> getSpells(EntityLivingBase entity, Hero hero)
    {
        Object spells = hero.getFuncObject(entity, AbilitySpellcasting.FUNC_SPELLS);

        if (spells == null)
        {
            return Spell.REGISTRY.getValues();
        }

        Gson gson = new Gson();
        Collection c = gson.fromJson(gson.toJson(spells), Map.class).values();
        return ((Stream<String>) c.stream().map(String::valueOf)).map(Spell::getSpellFromName).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static EntityLivingBase filterDuplicate(EntityLivingBase entity)
    {
        EntityLivingBase owner;
        return entity instanceof EntitySpellDuplicate && (owner = ((EntitySpellDuplicate) entity).getOwner()) != null ? owner : entity;
    }
    
    public static final Calendar TIME = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
    public static final Random RANDOM = new Random();

    public static boolean hasSpodermenAccess(Entity entity)
    {
        if (entity == null || SHData.SPODERMEN.get(entity))
        {
            TIME.setTime(new Date());
            RANDOM.setSeed((TIME.get(5) * 31 + TIME.get(11) * (TIME.get(2) + 1)) * 2400 * 365);
            return RANDOM.nextInt(14) == 0;
        }
        
        return false;
    }
}
