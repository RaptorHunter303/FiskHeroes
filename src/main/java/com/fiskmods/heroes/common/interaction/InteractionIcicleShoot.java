package com.fiskmods.heroes.common.interaction;

import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.entity.EntityIcicle;
import com.fiskmods.heroes.common.hero.modifier.Ability;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class InteractionIcicleShoot extends InteractionBase
{
    public InteractionIcicleShoot()
    {
        requireModifier(Ability.CRYOKINESIS);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return !SHData.CRYO_CHARGING.get(player) && player.getHeldItem() == null && !player.isSneaking();
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return Cooldown.ICICLES.available(player);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            float f = SHData.CRYO_CHARGE.get(sender);
            float spread = 0.5F;

            for (int i = 0; i <= (int) (f * 5); ++i)
            {
                EntityIcicle entity = new EntityIcicle(sender.worldObj, sender);

                if (i > 0)
                {
                    entity.motionX += (Math.random() - 0.5D) * spread;
                    entity.motionY += (Math.random() - 0.5D) * spread;
                    entity.motionZ += (Math.random() - 0.5D) * spread;
                }

                sender.worldObj.spawnEntityInWorld(entity);
            }

            SHData.CRYO_CHARGE.set(sender, 0.0F);
        }
        else if (sender == clientPlayer)
        {
            sender.swingItem();
            Cooldown.ICICLES.set(sender);
        }
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
