package com.fiskmods.heroes.common.interaction;

import com.fiskmods.heroes.common.equipment.EnumEquipment;
import com.fiskmods.heroes.common.equipment.EquipmentHandler;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityEquipment;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class InteractionEquipment extends InteractionBase
{
    public InteractionEquipment()
    {
        super(InteractionType.RIGHT_CLICK_AIR, InteractionType.RIGHT_CLICK_BLOCK);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        if (player.getHeldItem() != null || SHHelper.getUtilityBelt(player).type == EnumEquipment.Type.NONE)
        {
            return false;
        }

        Hero hero = SHHelper.getHero(player);

        if (hero != null)
        {
            for (Ability ability : hero.getAbilities())
            {
                if (ability instanceof AbilityEquipment)
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return EquipmentHandler.useCooldown[SHHelper.getUtilityBelt(player).ordinal()] == 0;
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            EnumEquipment equipment = SHHelper.getUtilityBelt(sender);
            Entity entity;

            if (!sender.worldObj.isRemote && (entity = equipment.createEntity(sender.worldObj, sender)) != null)
            {
                sender.worldObj.spawnEntityInWorld(entity);
            }
        }
        else if (sender == clientPlayer)
        {
            EnumEquipment equipment = SHHelper.getUtilityBelt(sender);
            int i = equipment.ordinal();

            if (++EquipmentHandler.timesUsed[i] >= equipment.maxUses)
            {
                EquipmentHandler.useCooldown[i] = equipment.useCooldown;
            }

            sender.swingItem();
        }
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
