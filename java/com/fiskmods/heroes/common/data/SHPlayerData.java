package com.fiskmods.heroes.common.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fiskmods.heroes.common.achievement.SHAchievements;
import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.network.MessageUpdateCollection;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;

public class SHPlayerData implements IExtendedEntityProperties
{
    public static final String IDENTIFIER = "FiskHeroesPlayer";

    public Map<SHData, Object> data = new HashMap<>();
    public Map<Hero, Byte> heroCollection = new HashMap<>();
    public Map<ArrowType, Integer> arrowCollection = new HashMap<>();

    public boolean arrowsNeedUpdate;
    public int maxTier;

    public Set<Hero> heroesCollected = new HashSet<>();
    public EntityPlayer player;

    public static SHPlayerData getData(EntityPlayer player)
    {
        return (SHPlayerData) player.getExtendedProperties(IDENTIFIER);
    }

    public void onUpdate()
    {
        if (SHData.SCALE.get(player) <= 0)
        {
            SHData.SCALE.setWithoutNotify(player, 1F);
        }

        if (!player.worldObj.isRemote)
        {
            if (arrowsNeedUpdate)
            {
                SHNetworkManager.wrapper.sendTo(new MessageUpdateCollection(player), (EntityPlayerMP) player);
                arrowsNeedUpdate = false;
            }

            if (player instanceof EntityPlayerMP && !SHData.SPODERMEN.get(player) && ((EntityPlayerMP) player).func_147099_x().hasAchievementUnlocked(SHAchievements.SPODERMEN))
            {
                SHData.SPODERMEN.set(player, true);
            }
        }

        if (player.ticksExisted % 40 == 0 && !Hero.REGISTRY.getKeys().isEmpty())
        {
            List<Hero> heroes = Lists.newArrayList(Hero.REGISTRY);
            boolean flag = false;

            for (ItemStack itemstack : player.inventory.armorInventory)
            {
                flag |= doItemCheck(itemstack);
            }

            for (ItemStack itemstack : player.inventory.mainInventory)
            {
                flag |= doItemCheck(itemstack);
            }

            if (flag && !heroCollection.isEmpty() || maxTier == 0)
            {
                heroesCollected.clear();
                int tier = 0;

                for (Hero hero : heroes)
                {
                    if (SHHelper.hasCollectedHero(player, hero))
                    {
                        tier = Math.max(tier, hero.getTier().tier);

                        if (!hero.isHidden())
                        {
                            heroesCollected.add(hero);
                        }
                    }
                }

                for (int i = 0; i < SHAchievements.TIERS.length; ++i)
                {
                    if (tier > i)
                    {
                        player.triggerAchievement(SHAchievements.TIERS[i]);
                    }
                }

                maxTier = Math.max(tier + 1, 1);
            }

            if (flag)
            {
                for (Hero hero : heroes)
                {
                    if (!hero.isHidden() && !heroesCollected.contains(hero))
                    {
                        flag = false;
                        break;
                    }
                }

                if (flag)
                {
                    player.triggerAchievement(SHAchievements.ALL_SUITS);
                }
            }
        }
    }

    public boolean doItemCheck(ItemStack itemstack)
    {
        Hero hero = SHHelper.getHeroFromArmor(itemstack);

        if (hero != null)
        {
            int print = 1 << ((ItemArmor) itemstack.getItem()).armorType;
            Byte b = heroCollection.getOrDefault(hero, (byte) 0);

            if ((b & print) != print)
            {
                heroCollection.put(hero, (byte) (b | print));
                return true;
            }
        }

        return false;
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt)
    {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound suits = new NBTTagCompound();
        NBTTagCompound arrows = new NBTTagCompound();

        compound.setBoolean("Saved", true);
        SHData.writeToNBT(compound, data);

        for (Map.Entry<Hero, Byte> e : heroCollection.entrySet())
        {
            suits.setByte(Hero.getNameForHero(e.getKey()), e.getValue());
        }

        for (Map.Entry<ArrowType, Integer> e : arrowCollection.entrySet())
        {
            arrows.setByte(ArrowType.getNameForArrow(e.getKey()), e.getValue().byteValue());
        }

        compound.setTag("HeroCollection", suits);
        compound.setTag("ArrowCollection", arrows);
        nbt.setTag(IDENTIFIER, compound);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt)
    {
        NBTTagCompound compound = nbt.getCompoundTag(IDENTIFIER);

        if (compound.getBoolean("Saved"))
        {
            data = SHData.readFromNBT(compound, data);

            if (compound.hasKey("HeroCollection", NBT.TAG_COMPOUND))
            {
                NBTTagCompound tag = compound.getCompoundTag("HeroCollection");

                for (String s : (Set<String>) tag.func_150296_c())
                {
                    Hero hero = Hero.getHeroFromName(s);

                    if (hero != null)
                    {
                        heroCollection.put(hero, tag.getByte(s));
                    }
                }
            }

            if (compound.hasKey("ArrowCollection", NBT.TAG_COMPOUND))
            {
                NBTTagCompound tag = compound.getCompoundTag("ArrowCollection");

                for (String s : (Set<String>) tag.func_150296_c())
                {
                    ArrowType arrow = ArrowType.getArrowFromName(s);

                    if (arrow != null)
                    {
                        arrowCollection.put(arrow, (int) tag.getByte(s));
                    }
                }
            }
        }
    }

    @Override
    public void init(Entity entity, World world)
    {
        if (entity instanceof EntityPlayer)
        {
            player = (EntityPlayer) entity;

            if (player.getAttributeMap().getAttributeInstanceByName(SHAttributes.STEP_HEIGHT.getAttributeUnlocalizedName()) == null)
            {
                player.getAttributeMap().registerAttribute(SHAttributes.STEP_HEIGHT);
            }
        }
    }

    public int speedXpBarCap()
    {
        return 20 + SHData.SPEED_EXPERIENCE_LEVEL.get(player) * 6;
    }

    public void copy(SHPlayerData props)
    {
        data = props.data;
        heroCollection = props.heroCollection;
        maxTier = props.maxTier;
    }

    public <T> void putData(SHData<T> type, T value)
    {
        data.put(type, value);
    }

    public <T> T getData(SHData<T> type)
    {
        if (data.containsKey(type))
        {
            return (T) data.get(type);
        }
        else if (!type.defaultValue.canEqual())
        {
            putData(type, type.getDefault());

            return getData(type);
        }

        return type.getDefault();
    }
}
