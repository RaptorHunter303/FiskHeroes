package com.fiskmods.heroes.common.item;

import java.util.List;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.util.VectorHelper;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBoStaff extends ItemUntextured implements IPunchWeapon
{
    public ItemBoStaff()
    {
        setMaxStackSize(1);
        setMaxDamage(SHConstants.MAX_DMG_BO_STAFF);
    }

    @Override
    public boolean hitEntity(ItemStack itemstack, EntityLivingBase target, EntityLivingBase attacker)
    {
        float width = attacker.width * 2;
        Vec3 vec3 = VectorHelper.getOffsetCoords(attacker, 0, 0, -0.2F);
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(-width, 0, -width, width, attacker.height, width).offset(vec3.xCoord, attacker.boundingBox.minY, vec3.zCoord);
        List<EntityLivingBase> list = attacker.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, aabb, IEntitySelector.selectAnything);

        for (EntityLivingBase entity : list)
        {
            if (entity != attacker)
            {
                ItemStack stack = attacker.getHeldItem();

                if (attacker instanceof EntityPlayer && stack != null && stack.getItem().onLeftClickEntity(stack, (EntityPlayer) attacker, entity))
                {
                    continue;
                }

                if (entity.canAttackWithItem())
                {
                    if (!entity.hitByEntity(attacker))
                    {
                        float attackDamage = (float) attacker.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                        float livingModifier = EnchantmentHelper.getEnchantmentModifierLiving(attacker, entity);
                        int knockback = EnchantmentHelper.getKnockbackModifier(attacker, entity);

                        if (attacker.isSprinting())
                        {
                            ++knockback;
                        }

                        if (attackDamage > 0.0F || livingModifier > 0.0F)
                        {
                            boolean crit = attacker.fallDistance > 0.0F && !attacker.onGround && !attacker.isOnLadder() && !attacker.isInWater() && !attacker.isPotionActive(Potion.blindness) && attacker.ridingEntity == null;

                            if (crit && attackDamage > 0.0F)
                            {
                                attackDamage *= 1.5F;
                            }

                            attackDamage += livingModifier;

                            boolean onFire = false;
                            int fire = EnchantmentHelper.getFireAspectModifier(attacker);

                            if (fire > 0 && !entity.isBurning())
                            {
                                onFire = true;
                                entity.setFire(1);
                            }

                            boolean success = entity.attackEntityFrom(attacker instanceof EntityPlayer ? DamageSource.causePlayerDamage((EntityPlayer) attacker) : DamageSource.causeMobDamage(attacker), attackDamage);

                            if (success)
                            {
                                if (knockback > 0)
                                {
//                                  entity.addVelocity
//                                  (
//                                      -MathHelper.sin(attacker.rotationYaw * (float)Math.PI / 180.0F) * (float)knockback * 0.5F,
//                                      0.1D,
//                                      MathHelper.cos(attacker.rotationYaw * (float)Math.PI / 180.0F) * (float)knockback * 0.5F
//                                  );

                                    double d = knockback * 0.25F;
                                    double x = attacker.posX - entity.posX;
                                    double z = attacker.posZ - entity.posZ;

                                    while (Math.sqrt(x * x + z * z) > d)
                                    {
                                        double d1 = 0.95D;
                                        x *= d1;
                                        z *= d1;
                                    }

                                    while (Math.sqrt(x * x + z * z) < d)
                                    {
                                        double d1 = 1.05D;
                                        x *= d1;
                                        z *= d1;
                                    }

                                    entity.addVelocity(-x, 0.1D, -z);

                                    attacker.motionX *= 0.6D;
                                    attacker.motionZ *= 0.6D;
                                    attacker.setSprinting(false);
                                }

                                if (attacker instanceof EntityPlayer)
                                {
                                    EntityPlayer player = (EntityPlayer) attacker;

                                    if (crit)
                                    {
                                        player.onCriticalHit(entity);
                                    }

                                    if (livingModifier > 0.0F)
                                    {
                                        player.onEnchantmentCritical(entity);
                                    }

                                    if (attackDamage >= 18.0F)
                                    {
                                        player.triggerAchievement(AchievementList.overkill);
                                    }

                                    player.setLastAttacker(entity);
                                    EnchantmentHelper.func_151384_a(entity, player);
                                    EnchantmentHelper.func_151385_b(player, entity);

                                    player.addStat(StatList.damageDealtStat, Math.round(attackDamage * 10.0F));

                                    if (fire > 0)
                                    {
                                        entity.setFire(fire * 4);
                                    }

                                    player.addExhaustion(0.3F);
                                }
                            }
                            else if (onFire)
                            {
                                entity.extinguish();
                            }
                        }
                    }
                }
            }
        }

        itemstack.damageItem(1, attacker);
        return true;
    }

    @Override
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public int getItemEnchantability()
    {
        return 10;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemstack1, ItemStack itemstack2)
    {
        return itemstack2.getItem() == Items.iron_ingot || super.getIsRepairable(itemstack1, itemstack2);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase entity)
    {
        if (block.getBlockHardness(world, x, y, z) != 0.0D)
        {
            itemstack.damageItem(2, entity);
        }

        return true;
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack itemstack)
    {
        Multimap multimap = super.getAttributeModifiers(itemstack);
        multimap.removeAll(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", SHConstants.DMG_BO_STAFF, 0));
        return multimap;
    }
}
