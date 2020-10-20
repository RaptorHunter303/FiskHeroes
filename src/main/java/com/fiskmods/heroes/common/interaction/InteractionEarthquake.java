package com.fiskmods.heroes.common.interaction;

import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.entity.EntityEarthquake;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class InteractionEarthquake extends InteractionBase
{
    public static final String KEY_EARTHQUAKE = "EARTHQUAKE";

    public InteractionEarthquake()
    {
        super(InteractionType.RIGHT_CLICK_BLOCK);
        requireModifier(Ability.GEOKINESIS);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return true;
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return Cooldown.EARTHQUAKE.available(player) && SHHelper.getHero(player).isKeyPressed(player, KEY_EARTHQUAKE);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            EntityEarthquake entity = new EntityEarthquake(sender.worldObj, sender);
            entity.posX = x + 0.5F;
            entity.posY = y + 0.5F;
            entity.posZ = z + 0.5F;

            sender.worldObj.spawnEntityInWorld(entity);
        }
        else if (sender == clientPlayer)
        {
            sender.swingItem();
            Cooldown.EARTHQUAKE.set(sender);
        }
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
