package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.equipment.EnumEquipment;
import com.fiskmods.heroes.common.equipment.EquipmentHandler;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityEquipment;
import com.fiskmods.heroes.common.interaction.InteractionType;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public class KeyPressEquipmentReset extends KeyPressBase
{
    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
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
    @SideOnly(Side.CLIENT)
    public KeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return hero.getKey(player, AbilityEquipment.KEY_RESET);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        byte b = SHData.UTILITY_BELT_TYPE.get(sender);
        SHData.PREV_UTILITY_BELT_TYPE.clampWithoutNotify(sender, (byte) 0, (byte) EnumEquipment.values().length);

        if (b < 0)
        {
            SHData.UTILITY_BELT_TYPE.setWithoutNotify(sender, SHData.PREV_UTILITY_BELT_TYPE.get(sender));
        }
        else
        {
            SHData.PREV_UTILITY_BELT_TYPE.setWithoutNotify(sender, b);
            SHData.UTILITY_BELT_TYPE.setWithoutNotify(sender, (byte) -1);
        }

        if (side.isClient() && sender == clientPlayer)
        {
            sender.playSound(SHSounds.ABILITY_EQUIPMENT_SWITCH.toString(), 1, 1);
            EquipmentHandler.fade = EquipmentHandler.FADE_MAX;
        }
    }

    @Override
    public boolean syncWithServer()
    {
        return true;
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
