package com.fiskmods.heroes.common.entity.attribute;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fiskmods.heroes.asm.ASMHooks;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.network.MessageUpdateStepHeight;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class SHAttributes
{
    private static final Map<String, ArmorAttribute> REGISTRY = new HashMap<>();

    private static final UUID UUID_HEALTH_MOD = UUID.fromString("8c1729f2-e903-4fc7-94d5-7691c4196643");
    private static final UUID UUID_SPEEDSTER_MOD = UUID.fromString("388b19ef-9685-4881-be63-04bc862dca2d");
    private static final UUID UUID_TREADMILL_MOD = UUID.fromString("4534d51c-b886-4911-96aa-5cdb35e03cf4");
    private static final UUID UUID_SCALE_MOD = UUID.fromString("0f8e4b88-608f-4a2c-8371-ab8b4a587fdf");

    public static final ArmorAttribute ARROW_DAMAGE = new ArmorAttribute("arrowDamage", true);
    public static final ArmorAttribute ATTACK_DAMAGE = new ArmorAttribute("attackDamage", true);
    public static final ArmorAttribute BASE_SPEED = new ArmorAttribute("baseSpeed", true);
    public static final ArmorAttribute BASE_SPEED_LEVELS = new ArmorAttribute("baseSpeedLevels", true);
    public static final ArmorAttribute BOW_DRAWBACK = new ArmorAttribute("bowDrawback", false);
    public static final ArmorAttribute DAMAGE_REDUCTION = new ArmorAttribute("damageReduction", true);
    public static final ArmorAttribute FALL_RESISTANCE = new ArmorAttribute("fallResistance", false);
    public static final ArmorAttribute JUMP_HEIGHT = new ArmorAttribute("jumpHeight", true);
    public static final ArmorAttribute KNOCKBACK = new ArmorAttribute("knockback", true);
    public static final ArmorAttribute MAX_HEALTH = new ArmorAttribute("maxHealth", true);
    public static final ArmorAttribute PUNCH_DAMAGE = new ArmorAttribute("punchDamage", true);
    public static final ArmorAttribute SPRINT_SPEED = new ArmorAttribute("sprintSpeed", true);
    public static final ArmorAttribute STEP_HEIGHT = new AttributeStepHeight("stepHeight", true);
    public static final ArmorAttribute SWORD_DAMAGE = new ArmorAttribute("swordDamage", true);

    public static AttributeWrapper getAttribute(EntityLivingBase entity, ArmorAttribute attribute)
    {
        return getAttribute(entity, SHHelper.getHero(entity), attribute);
    }

    public static AttributeWrapper getAttribute(EntityLivingBase entity, Hero hero, ArmorAttribute attribute)
    {
        if (hero != null)
        {
            AttributeWrapper wrapper = new AttributeWrapper(attribute);
            hero.getAttributeModifiers(entity, (attr, amount, operation) ->
            {
                if (attr == attribute)
                {
                    wrapper.apply(amount, operation);
                }
            });

            return wrapper.isEmpty() ? null : wrapper;
        }

        return null;
    }

    public static float getArmorProtection(EntityLivingBase entity)
    {
        return getArmorProtection(entity, SHHelper.getEquipment(entity), Rule.DURABILITY_SCALED_PROT.get(entity));
    }

    public static float getArmorProtection(EntityLivingBase entity, ItemStack[] itemstacks, boolean durabilityScaled)
    {
        Hero hero = SHHelper.getHero(itemstacks);
        AttributeWrapper wrapper = getAttribute(entity, hero, DAMAGE_REDUCTION);

        if (wrapper != null)
        {
            float amount = wrapper.getValue(1) - 1;

            if (durabilityScaled)
            {
                float durability = 0;

                for (ItemStack itemstack : itemstacks)
                {
                    if (itemstack != null)
                    {
                        durability += (float) (itemstack.getMaxDamage() - itemstack.getItemDamage()) / itemstack.getMaxDamage();
                    }
                }

                amount *= durability / hero.getPiecesToSet();
            }

            return amount;
        }

        return 0;
    }

    public static double getModifier(EntityLivingBase entity, ArmorAttribute attribute, double baseValue)
    {
        return getModifier(entity, SHHelper.getHero(entity), attribute, baseValue);
    }

    public static double getModifier(EntityLivingBase entity, Hero hero, ArmorAttribute attribute, double baseValue)
    {
        AttributeWrapper wrapper = getAttribute(entity, hero, attribute);

        if (wrapper != null)
        {
            double result = wrapper.getValue(baseValue);

            return attribute.isAdditive() ? result : baseValue * 2 - result;
        }

        return baseValue;
    }

    public static float getModifier(EntityLivingBase entity, ArmorAttribute attribute, float baseValue)
    {
        return getModifier(entity, SHHelper.getHero(entity), attribute, baseValue);
    }

    public static float getModifier(EntityLivingBase entity, Hero hero, ArmorAttribute attribute, float baseValue)
    {
        return (float) getModifier(entity, hero, attribute, (double) baseValue);
    }

    public static void applyModifiers(EntityPlayer player)
    {
        IAttributeInstance speedAttribute = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        IAttributeInstance healthAttribute = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        IAttributeInstance stepAttribute = player.getEntityAttribute(STEP_HEIGHT);
        float prevStepHeight = player.stepHeight;
        float stepHeight = 0.5F;
        float scale;

        if ((scale = SHData.SCALE.get(player)) > 1)
        {
            applyModifier(healthAttribute, UUID_HEALTH_MOD, Math.pow(scale, 1.0 / 3) - 1, 1);
            stepHeight = player.stepHeight = scale * 0.5F;
        }
        else if (removeModifier(healthAttribute, UUID_HEALTH_MOD))
        {
            stepHeight = player.stepHeight = 0.5F;
        }

        if (SpeedsterHelper.canPlayerRun(player))
        {
            applyModifier(speedAttribute, UUID_SPEEDSTER_MOD, SHData.SPEED.get(player) < 0 ? 0 : FiskMath.KMPH * (SpeedsterHelper.getPlayerTopSpeed(player) - 20), 0);
            stepHeight = player.stepHeight = 1.0F;
        }
        else if (removeModifier(speedAttribute, UUID_SPEEDSTER_MOD))
        {
            stepHeight = player.stepHeight = 0.5F;
        }

        if (SpeedsterHelper.isOnTreadmill(player))
        {
            applyModifier(speedAttribute, UUID_TREADMILL_MOD, -1, 1);
        }
        else
        {
            removeModifier(speedAttribute, UUID_TREADMILL_MOD);
        }

        float strengthScale = ASMHooks.getStrengthScale(player);

        if (strengthScale != 1)
        {
            applyModifier(speedAttribute, UUID_SCALE_MOD, strengthScale - 1, 1);
        }
        else
        {
            removeModifier(speedAttribute, UUID_SCALE_MOD);
        }

        IAttributeInstance[] instances = {speedAttribute, speedAttribute, stepAttribute, healthAttribute};
        ArmorAttribute[] attributes = {BASE_SPEED, SPRINT_SPEED, STEP_HEIGHT, MAX_HEALTH};

        for (int i = 0; i < attributes.length; ++i)
        {
            ArmorAttribute attribute = attributes[i];
            List<UUID> list = new ArrayList<>();

            if (attribute != SPRINT_SPEED || player.isSprinting())
            {
                AttributeWrapper wrapper = getAttribute(player, attribute);

                if (wrapper != null)
                {
                    List<AttributePair> modifiers = wrapper.getModifiers();

                    for (AttributePair mod : modifiers)
                    {
                        UUID uuid = attribute.createUUID(player, mod);
                        applyModifier(instances[i], uuid, mod.amount, mod.operation);
                        list.add(uuid);

                        if (attribute == STEP_HEIGHT)
                        {
                            player.stepHeight = wrapper.getValue(stepHeight);
                        }
                    }
                }
            }

            attribute.clean(player, instances[i], list);
        }

        if (player.stepHeight != prevStepHeight)
        {
            SHNetworkManager.wrapper.sendTo(new MessageUpdateStepHeight(player, player.stepHeight), (EntityPlayerMP) player);
        }
    }

    public static boolean applyModifier(IAttributeInstance instance, UUID uuid, double amount, int operation)
    {
        AttributeModifier modifier = instance.getModifier(uuid);

        if (modifier == null)
        {
            instance.applyModifier(new AttributeModifier(uuid, uuid.toString(), amount, operation).setSaved(false));
            return true;
        }
        else if ((modifier.getAmount() != amount || modifier.getOperation() != operation) && removeModifier(instance, uuid))
        {
            return applyModifier(instance, uuid, amount, operation);
        }

        return false;
    }

    public static boolean removeModifier(IAttributeInstance instance, UUID uuid)
    {
        AttributeModifier modifier = instance.getModifier(uuid);

        if (modifier != null)
        {
            instance.removeModifier(modifier);
            return true;
        }

        return false;
    }

    public static ArmorAttribute getAttribute(String key)
    {
        return REGISTRY.get(key);
    }

    static
    {
        for (Field field : SHAttributes.class.getFields())
        {
            if (field.getType() == ArmorAttribute.class)
            {
                try
                {
                    REGISTRY.put(field.getName(), (ArmorAttribute) field.get(null));
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
