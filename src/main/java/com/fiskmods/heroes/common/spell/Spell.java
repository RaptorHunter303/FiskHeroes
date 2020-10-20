package com.fiskmods.heroes.common.spell;

import java.lang.reflect.Field;
import java.util.Locale;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.interaction.Interaction;
import com.fiskmods.heroes.common.network.MessageTriggerSpell;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.common.registry.FiskRegistryEntry;
import com.fiskmods.heroes.common.registry.FiskSimpleRegistry;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StatCollector;

public class Spell extends FiskRegistryEntry<Spell>
{
    public static final FiskSimpleRegistry<Spell> REGISTRY = new FiskSimpleRegistry(FiskHeroes.MODID, null);

    public static void register(String key, Spell value)
    {
        REGISTRY.putObject(key, value);
    }

    public static Spell getSpellFromName(String key)
    {
        return REGISTRY.getObject(key);
    }

    public static String getNameForSpell(Spell value)
    {
        return REGISTRY.getNameForObject(value);
    }

    public static final Spell EARTH_SWALLOWING = new SpellEarthSwallow();
    public static final Spell ATMOSPHERIC = new SpellAtmospheric();
    public static final Spell DUPLICATION = new SpellDuplication();
    public static final Spell WHIP = new SpellWhip();

    public final KeySequence keySequence;
    public final Cooldown spellCooldown;

    public final boolean canDuplicatesUse;

    public Spell(Cooldown cooldown, String sequence, boolean duplicate)
    {
        keySequence = new KeySequence(sequence);
        canDuplicatesUse = duplicate;
        spellCooldown = cooldown;
    }

    public String getUnlocalizedName()
    {
        return "spell." + getName().replace(':', '.');
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(getUnlocalizedName() + ".name");
    }

    public boolean canTrigger(EntityLivingBase caster)
    {
        return true;
    }

    public void trigger(EntityPlayer player)
    {
        if (!player.worldObj.isRemote)
        {
            TargetPoint target = getTargetPoint(player);

            if (target == null)
            {
                SHNetworkManager.wrapper.sendToDimension(new MessageTriggerSpell(player, this), player.dimension);
            }
            else if (target.range > 0)
            {
                SHNetworkManager.wrapper.sendToAllAround(new MessageTriggerSpell(player, this), target);
            }
            else if (player instanceof EntityPlayerMP)
            {
                SHNetworkManager.wrapper.sendTo(new MessageTriggerSpell(player, this), (EntityPlayerMP) player);
            }
        }
        else if (spellCooldown.available(player))
        {
            SHNetworkManager.wrapper.sendToServer(new MessageTriggerSpell(player, this));
        }
    }

    public void onTrigger(EntityLivingBase caster)
    {
    }

    public boolean shouldSync(EntityLivingBase caster)
    {
        TargetPoint target = getTargetPoint(caster);
        return target == null || target.range > 0;
    }

    public TargetPoint getTargetPoint(EntityLivingBase caster)
    {
        return Interaction.TARGET_NONE;
    }

    public boolean renderIcon(EntityPlayer player)
    {
        return false;
    }

    public int getX()
    {
        return 0;
    }

    public int getY()
    {
        return 0;
    }

    public String getName()
    {
        return delegate.name();
    }

    @Override
    public String toString()
    {
        return "Spell[" + getName() + "]";
    }

    static
    {
        for (Field field : Spell.class.getFields())
        {
            if (field.getType() == Spell.class)
            {
                try
                {
                    register(field.getName().toLowerCase(Locale.ROOT), (Spell) field.get(null));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
