//package com.fiskmods.heroes.common.hero.modifier;
//
//import java.util.List;
//
//import com.fiskmods.heroes.common.config.Rule;
//import com.fiskmods.heroes.common.damagesource.ModDamageSources;
//import com.fiskmods.heroes.common.data.SHData;
//import com.fiskmods.heroes.common.hero.Hero;
//
//import cpw.mods.fml.common.gameevent.TickEvent.Phase;
//import fiskfille.core.helper.VectorHelper;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.util.AxisAlignedBB;
//import net.minecraft.util.MovingObjectPosition;
//import net.minecraft.util.Vec3;
//
//public class AbilityTelekinesis extends Ability
//{
//    public static final String KEY_TELEKINESIS = "TELEKINESIS";
//
//    public AbilityTelekinesis(int tier)
//    {
//        super(tier);
//    }
//
//    @Override
//    public void onUpdate(EntityLivingBase entity, Hero hero, Phase phase)
//    {
//        if (phase == Phase.END)
//        {
//            if (SHData.AIMING.get(entity))
//            {
//                float range = Rule.RANGE_TELEKINESIS_STUN.get(entity);
//                float border = Rule.RADIUS_TELEKINESIS_STUN.get(entity);
//
//                Vec3 look = entity.getLook(1);
//                Vec3 src = VectorHelper.getPosition(entity, 1).addVector(0, VectorHelper.getOffset(entity), 0);
//                Vec3 dest = VectorHelper.add(src, VectorHelper.multiply(look, range));
//
//                MovingObjectPosition rayTrace = entity.worldObj.func_147447_a(VectorHelper.copy(src), VectorHelper.copy(dest), false, true, true);
//                Entity pointedEntity = null;
//                Vec3 hitVec = null;
//
//                double length = rayTrace != null ? rayTrace.hitVec.distanceTo(src) : range;
//                double newLength = length;
//
//                for (Entity target : (List<Entity>) entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range).expand(1, 1, 1)))
//                {
//                    if (target.canBeCollidedWith())
//                    {
//                        AxisAlignedBB aabb = target.boundingBox.expand(border, border, border);
//                        MovingObjectPosition mop = aabb.calculateIntercept(src, dest);
//
//                        if (aabb.isVecInside(src))
//                        {
//                            if (0 < newLength || newLength == 0)
//                            {
//                                pointedEntity = target;
//                                hitVec = mop == null ? src : mop.hitVec;
//                                newLength = 0;
//                            }
//                        }
//                        else if (mop != null)
//                        {
//                            double distance = src.distanceTo(mop.hitVec);
//
//                            if (distance < newLength || newLength == 0)
//                            {
//                                if (target == entity.ridingEntity && !target.canRiderInteract())
//                                {
//                                    if (newLength == 0)
//                                    {
//                                        pointedEntity = target;
//                                        hitVec = mop.hitVec;
//                                    }
//                                }
//                                else
//                                {
//                                    pointedEntity = target;
//                                    hitVec = mop.hitVec;
//                                    newLength = distance;
//                                }
//                            }
//                        }
//                    }
//                }
//
//                if (pointedEntity != null && (newLength < length || rayTrace == null))
//                {
//                    rayTrace = new MovingObjectPosition(pointedEntity, hitVec);
//                }
//
//                if (rayTrace != null)
//                {
//                    rayTrace.hitInfo = newLength;
//
//                    if (rayTrace.entityHit != null)
//                    {
//                        double d = 0.1;
//                        rayTrace.entityHit.motionX *= d;
//                        rayTrace.entityHit.motionY -= 0.2;
//                        rayTrace.entityHit.motionZ *= d;
//                        rayTrace.entityHit.attackEntityFrom(ModDamageSources.causeLightningDamage(entity), Rule.DMG_TELEKINESIS.get(rayTrace.entityHit));
//                    }
//                }
//            }
//
////            if (entity.worldObj.isRemote && FiskHeroes.proxy.isClientPlayer(entity))
////            {
////                SHData.AIMING.set(entity, hero.isKeyPressed(KEY_TELEKINESIS));
////            }
//        }
//    }
//}
