//package com.fiskmods.heroes.common.interaction;
//
//import com.fiskmods.heroes.common.config.Rule;
//import com.fiskmods.heroes.common.damagesource.ModDamageSources;
//import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
//import com.fiskmods.heroes.common.data.SHData;
//import com.fiskmods.heroes.common.hero.modifier.Ability;
//import com.fiskmods.heroes.common.interaction.EnumInteraction.InteractionType;
//import com.fiskmods.heroes.helper.SHHelper;
//
//import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
//import cpw.mods.fml.relauncher.Side;
//import fiskfille.core.helper.VectorHelper;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.MovingObjectPosition;
//
//public class InteractionTelekinesis extends InteractionBase
//{
//    public InteractionTelekinesis()
//    {
//        requireModifier(Ability.TELEKINESIS);
//    }
//
//    @Override
//    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
//    {
//        return !SHData.AIMING.get(player) && player.getHeldItem() == null && !player.isSneaking();
//    }
//
//    @Override
//    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
//    {
//        return Cooldown.TELEKINESIS.available(player);
//    }
//
//    @Override
//    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
//    {
//        if (side.isServer())
//        {
//            double range = Rule.RANGE_TELEKINESIS_DMG.get(sender);
//            MovingObjectPosition rayTrace = SHHelper.rayTrace(sender, range, 2, 1);
//
//            if (rayTrace == null)
//            {
//                rayTrace = new MovingObjectPosition(0, 0, 0, 0, VectorHelper.getOffsetCoords(sender, 0, 0, range), false);
//            }
//
//            if (rayTrace != null && rayTrace.hitVec != null && rayTrace.entityHit != null)
//            {
//                rayTrace.entityHit.attackEntityFrom(ModDamageSources.causeLightningDamage(sender), Rule.DMG_TELEKINESIS.get(rayTrace.entityHit));
//            }
//        }
//        else if (sender == clientPlayer)
//        {
//            Cooldown.TELEKINESIS.set(sender);
//        }
//    }
//
//    @Override
//    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
//    {
//        return TARGET_NONE;
//    }
//}
