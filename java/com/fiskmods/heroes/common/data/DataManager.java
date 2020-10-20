package com.fiskmods.heroes.common.data;

import java.util.Map;
import java.util.Set;

import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.data.arrow.IArrowData;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.network.MessageAddArrowToEntity;
import com.fiskmods.heroes.common.network.MessagePlayerJoin;
import com.fiskmods.heroes.common.network.SHNetworkManager;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;

public class DataManager
{
    public static int serverArrows = ArrowType.REGISTRY.getKeys().size();

    public static double getVelocity(EntityPlayer player)
    {
        if (player.ticksExisted == 0)
        {
            player.lastTickPosX = player.posX;
            player.lastTickPosY = player.posY;
            player.lastTickPosZ = player.posZ;
        }

        double diffX = player.posX - player.lastTickPosX;
        double diffY = player.posY - player.lastTickPosY;
        double diffZ = player.posZ - player.lastTickPosZ;
        double blocksMoved = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);

        return blocksMoved / 50 * 60 * 60;
    }

    public static Set<IArrowData> getArrowsInEntity(EntityLivingBase entity)
    {
        return SHEntityData.getData(entity).arrowsInEntity;
    }

    public static void addArrowToEntity(EntityLivingBase entity, ArrowType<EntityTrickArrow> type, EntityTrickArrow arrow)
    {
        IArrowData data = type.getDataFactory().apply(type, arrow);

        if (data != null)
        {
            getArrowsInEntity(entity).add(data);

            if (!entity.worldObj.isRemote)
            {
                SHNetworkManager.wrapper.sendToDimension(new MessageAddArrowToEntity(entity, arrow), arrow.dimension);
            }
        }

//        EntityTrickArrow arrow1 = type.newInstance(worldObj, posX, posY, posZ);
//
//        if (arrow1 != null)
//        {
//            arrow1.setEntityId(getEntityId());
//            arrow1.setArrowItem(getArrowItem());
//            arrow1.setArrowId(getArrowId());
//            arrow1.setHero(getHero());
//            arrow1.shootingEntity = shootingEntity;
//            arrow1.canBePickedUp = canBePickedUp;
//            arrow1.noEntity = noEntity;
//
//            DataManager.getArrowsInEntity(entityHit).add(new ArrowInEntityData(arrow1));
//            SHNetworkManager.wrapper.sendToDimension(new MessageAddArrowToEntity(entityHit, this), dimension);
//        }
    }

    public static void addSpeedExperience(EntityPlayer player, int amount)
    {
        byte speedExperienceLevel = SHData.SPEED_EXPERIENCE_LEVEL.get(player);
        int speedExperienceTotal = SHData.SPEED_EXPERIENCE_TOTAL.get(player);
        float speedExperience = SHData.SPEED_EXPERIENCE_BAR.get(player);
        int j = Integer.MAX_VALUE - speedExperienceTotal;

        if (amount > j)
        {
            amount = j;
        }

        if (speedExperienceLevel < 30)
        {
            speedExperience += (float) amount / (float) SHPlayerData.getData(player).speedXpBarCap();

            for (speedExperienceTotal += amount; speedExperience >= 1.0F; speedExperience /= SHPlayerData.getData(player).speedXpBarCap())
            {
                speedExperience = (speedExperience - 1.0F) * SHPlayerData.getData(player).speedXpBarCap();
                addSpeedExperienceLevel(player, 1);
            }
        }

        SHData.SPEED_EXPERIENCE_LEVEL.clamp(player, (byte) 0, (byte) 30);
        SHData.SPEED_EXPERIENCE_TOTAL.set(player, Math.max(speedExperienceTotal, 0));
        SHData.SPEED_EXPERIENCE_BAR.set(player, speedExperience);
    }

    public static void addSpeedExperienceLevel(EntityPlayer player, int amount)
    {
        byte speedExperienceLevel = SHData.SPEED_EXPERIENCE_LEVEL.get(player);
        int speedExperienceTotal = SHData.SPEED_EXPERIENCE_TOTAL.get(player);
        float speedExperience = SHData.SPEED_EXPERIENCE_BAR.get(player);

        if (speedExperienceLevel < 30 || amount < 0)
        {
            speedExperienceLevel += amount;

            if (speedExperienceLevel < 0)
            {
                speedExperienceLevel = 0;
                speedExperience = 0.0F;
                speedExperienceTotal = 0;
            }

            if (amount > 0 && SHData.SPEED_LEVEL_UP_COOLDOWN.get(player) < player.ticksExisted - 20.0F)
            {
                float f = speedExperienceLevel > 30 ? 1.0F : speedExperienceLevel / 30.0F;
                player.worldObj.playSoundAtEntity(player, "random.levelup", f * 0.75F, 1.0F);
                SHData.SPEED_LEVEL_UP_COOLDOWN.setWithoutNotify(player, player.ticksExisted);
            }
        }

        SHData.SPEED_EXPERIENCE_LEVEL.set(player, (byte) MathHelper.clamp_int(speedExperienceLevel, 0, 30));
        SHData.SPEED_EXPERIENCE_TOTAL.set(player, Math.max(speedExperienceTotal, 0));
        SHData.SPEED_EXPERIENCE_BAR.set(player, speedExperience);
    }

    public static void setHeroCollection(EntityPlayer player, Map<Hero, Byte> map)
    {
        SHPlayerData.getData(player).heroCollection = map;
        SHPlayerData.getData(player).maxTier = 0;
    }

    public static void setArrowCollection(EntityPlayer player, Map<ArrowType, Integer> map)
    {
        SHPlayerData.getData(player).arrowCollection = map;
    }

    public static int getArrowsCollected(EntityPlayer player)
    {
        return (int) SHPlayerData.getData(player).arrowCollection.entrySet().stream().filter(e -> e.getValue() >= 32).count();
    }

    public static void updatePlayerWithServerInfo(EntityPlayer player)
    {
        if (!player.worldObj.isRemote)
        {
            SHNetworkManager.wrapper.sendTo(new MessagePlayerJoin(player), (EntityPlayerMP) player);
        }
    }
}
