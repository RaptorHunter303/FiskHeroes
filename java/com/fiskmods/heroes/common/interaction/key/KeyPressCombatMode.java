//package com.fiskmods.heroes.common.interaction.key; // TODO: 1.4 Combat
//
//import com.fiskmods.heroes.common.data.SHData;
//import com.fiskmods.heroes.common.hero.Hero;
//import com.fiskmods.heroes.common.interaction.EnumInteraction.InteractionType;
//import com.fiskmods.heroes.common.keybinds.SHKeyBinding;
//import com.fiskmods.heroes.common.keybinds.SHKeyBinds;
//import com.fiskmods.heroes.common.move.MoveCommonHandler;
//
//import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
//import cpw.mods.fml.relauncher.Side;
//import cpw.mods.fml.relauncher.SideOnly;
//import net.minecraft.entity.player.EntityPlayer;
//
//public class KeyPressCombatMode extends KeyPressBase
//{
//    public KeyPressCombatMode()
//    {
//        super(null);
//    }
//
//    @Override
//    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
//    {
//        return MoveCommonHandler.hasMoveSet(player);
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public SHKeyBinding getKey(EntityPlayer player, Hero hero)
//    {
//        return SHKeyBinds.COMBAT_MODE;
//    }
//
//    @Override
//    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
//    {
//        SHData.COMBAT_MODE.set(sender, !SHData.COMBAT_MODE.get(sender));
//    }
//
//    @Override
//    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
//    {
//        return TARGET_NONE;
//    }
//}
