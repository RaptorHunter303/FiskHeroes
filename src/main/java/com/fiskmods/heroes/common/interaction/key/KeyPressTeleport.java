package com.fiskmods.heroes.common.interaction.key;

import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.common.DimensionalCoords;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityTeleportation;
import com.fiskmods.heroes.common.interaction.InteractionType;
import com.fiskmods.heroes.common.network.MessageTeleport;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

public class KeyPressTeleport extends KeyPressBase
{
    public KeyPressTeleport()
    {
        requireModifier(Ability.TELEPORTATION);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return SHData.TELEPORT_DELAY.get(player) <= 0 && !SHHelper.isEarthCrackTarget(player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public KeyBinding getKey(EntityPlayer player, Hero hero)
    {
        return hero.getKey(player, AbilityTeleportation.KEY_TELEPORT);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            double range = Rule.RANGE_TELEPORT.getHero(sender);
            Vec3 src = VectorHelper.getOffsetCoords(sender, 0, 0, 0);
            Vec3 dst = VectorHelper.getOffsetCoords(sender, 0, 0, range);

            MovingObjectPosition mop = sender.worldObj.func_147447_a(src, dst, false, true, false);

            if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
            {
                x = mop.blockX;
                y = mop.blockY + 1;
                z = mop.blockZ;

                float w = sender.width / 2;
                boolean flag = false;
                AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(-w, 0, -w, w, sender.height, w).offset(0.5, 0, 0.5);

                if (sender.worldObj.checkBlockCollision(aabb.getOffsetBoundingBox(x, y, z)))
                {
                    flag = true;

                    if (mop.sideHit == 0)
                    {
                        flag = sender.worldObj.checkBlockCollision(aabb.getOffsetBoundingBox(x, y -= sender.height + 1, z));
                    }
                    else
                    {
                        while (y < sender.worldObj.getActualHeight() && sender.worldObj.checkBlockCollision(aabb.getOffsetBoundingBox(x, ++y, z)))
                        {
                        }

                        flag = false;

//                        ForgeDirection dir = ForgeDirection.getOrientation(mop.sideHit);
//                        flag = sender.worldObj.checkBlockCollision(aabb.getOffsetBoundingBox(x += dir.offsetX, y -= 1, z += dir.offsetZ));
                    }
                }

                DimensionalCoords coords = new DimensionalCoords(x, y, z, 0);

                if (flag)
                {
                    coords = null;
                }
                else
                {
                    SHNetworkManager.wrapper.sendToDimension(new MessageTeleport(sender, coords), sender.dimension);
                }

                SHData.TELEPORT_DEST.setWithoutNotify(sender, coords);
            }

            SHData.TELEPORT_DELAY.set(sender, SHConstants.TICKS_TELEPORT_DELAY);
        }
    }

    @Override
    public boolean syncWithServer()
    {
        return true;
    }
}
