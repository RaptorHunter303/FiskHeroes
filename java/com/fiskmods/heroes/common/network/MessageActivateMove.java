//package com.fiskmods.heroes.common.network; // TODO: 1.4 Combat
//
//import com.fiskmods.heroes.common.data.SHData;
//import com.fiskmods.heroes.common.hero.Hero;
//import com.fiskmods.heroes.common.move.Move.MoveActivation;
//import com.fiskmods.heroes.common.move.MoveCommonHandler;
//import com.fiskmods.heroes.common.move.MoveEntry;
//import com.fiskmods.heroes.helper.SHHelper;
//import com.google.common.collect.ImmutableMap;
//
//import fiskfille.core.network.AbstractMessage;
//import io.netty.buffer.ByteBuf;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.util.MovingObjectPosition;
//import net.minecraft.util.MovingObjectPosition.MovingObjectType;
//import net.minecraft.util.Vec3;
//
//public class MessageActivateMove extends AbstractMessage<MessageActivateMove>
//{
//    private int id;
//    private int mouseOverId;
//
//    private MovingObjectPosition mouseOver;
//    private MoveActivation activation;
//
//    public MessageActivateMove()
//    {
//    }
//
//    public MessageActivateMove(EntityLivingBase entity, MovingObjectPosition mop, MoveActivation moveActivation)
//    {
//        id = entity.getEntityId();
//        activation = moveActivation;
//        mouseOver = mop;
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf)
//    {
//        id = buf.readInt();
//        activation = MoveActivation.values()[buf.readByte()];
//        byte b = buf.readByte();
//
//        if (b >= 0)
//        {
//            MovingObjectType type = MovingObjectType.values()[Math.min(b, MovingObjectType.values().length - 1)];
//
//            switch (type)
//            {
//            case ENTITY:
//                mouseOver = new MovingObjectPosition(null, null);
//                mouseOverId = buf.readInt();
//                break;
//            default:
//                mouseOver = new MovingObjectPosition(buf.readInt(), buf.readInt(), buf.readInt(), buf.readByte(), Vec3.createVectorHelper(0, 0, 0), type == MovingObjectType.BLOCK);
//                mouseOverId = -1;
//                break;
//            }
//
//            if (buf.readBoolean())
//            {
//                mouseOver.hitVec = Vec3.createVectorHelper(buf.readDouble(), buf.readDouble(), buf.readDouble());
//            }
//        }
//        else
//        {
//            mouseOver = null;
//            mouseOverId = -1;
//        }
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf)
//    {
//        buf.writeInt(id);
//        buf.writeByte(activation.ordinal());
//
//        if (mouseOver != null)
//        {
//            buf.writeByte(mouseOver.typeOfHit.ordinal());
//
//            switch (mouseOver.typeOfHit)
//            {
//            case ENTITY:
//                buf.writeInt(mouseOver.entityHit != null ? mouseOver.entityHit.getEntityId() : -1);
//                break;
//            default:
//                buf.writeInt(mouseOver.blockX);
//                buf.writeInt(mouseOver.blockY);
//                buf.writeInt(mouseOver.blockZ);
//                buf.writeByte(mouseOver.sideHit);
//                break;
//            }
//
//            buf.writeBoolean(mouseOver.hitVec != null);
//
//            if (mouseOver.hitVec != null)
//            {
//                buf.writeDouble(mouseOver.hitVec.xCoord);
//                buf.writeDouble(mouseOver.hitVec.yCoord);
//                buf.writeDouble(mouseOver.hitVec.zCoord);
//            }
//        }
//        else
//        {
//            buf.writeByte(-1);
//        }
//    }
//
//    @Override
//    public void receive() throws MessageException
//    {
//        EntityLivingBase entity = getEntity(EntityLivingBase.class, id);
//
//        if (context.side.isClient() && entity == getPlayer())
//        {
//            return;
//        }
//
//        Hero hero = SHHelper.getHero(entity);
//        MoveEntry e = MoveCommonHandler.getMove(entity, hero);
//
//        if (e != null)
//        {
//            float focus = SHData.FOCUS.get(entity);
//            ImmutableMap<String, Number> modifiers = e.getParent().getModifiers(e.move, focus);
//
//            if (mouseOver != null && mouseOverId >= 0)
//            {
//                mouseOver.entityHit = getEntity(Entity.class, mouseOverId);
//            }
//
//            if (modifiers != null && e.move.onActivated(entity, hero, mouseOver, activation, modifiers, focus) && (focus == 0 || SHData.FOCUS.setWithoutNotify(entity, 0F)) && context.side.isServer())
//            {
//                SHNetworkManager.wrapper.sendToDimension(this, entity.dimension);
//            }
//        }
//    }
//}
