package com.fiskmods.heroes.common.interaction;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityLaserBolt;
import com.fiskmods.heroes.common.entity.EntityLaserBolt.Type;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class InteractionEnergyBlast extends InteractionBase
{
    public InteractionEnergyBlast()
    {
        requireModifier(Ability.ENERGY_BLAST);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return SHData.AIMING.get(player);
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return Cooldown.ENERGY_BLAST.available(player);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            double frontOffset = 0.6;
            double sideOffset = -0.3;
            Vec3 src = VectorHelper.getOffsetCoords(sender, sideOffset, -0.3, frontOffset);
            EntityLaserBolt entity = new EntityLaserBolt(sender.worldObj, sender, Type.SUIT, SHHelper.getHeroIter(sender), false);
            entity.setPosition(src.xCoord, src.yCoord, src.zCoord);

            sender.worldObj.spawnEntityInWorld(entity);
            sender.worldObj.playSoundAtEntity(sender, SHSounds.ITEM_CHRONOSRIFLE_SHOOT.toString(), 1, 1.0F / (sender.getRNG().nextFloat() * 0.3F + 0.7F));
        }
        else if (sender == clientPlayer)
        {
            Cooldown.ENERGY_BLAST.set(sender);
            SHData.RELOAD_TIMER.setWithoutNotify(sender, 1.0F);
        }
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
