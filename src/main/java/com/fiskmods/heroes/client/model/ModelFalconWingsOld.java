//package com.fiskmods.heroes.client.model;
//
//import org.lwjgl.opengl.GL11;
//
//import com.fiskmods.heroes.common.data.SHData;
//import com.fiskmods.heroes.helper.SHRenderHelper;
//
//import fiskfille.core.helper.FiskMath;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.client.model.ModelBase;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.Vec3;
//
//public class ModelFalconWings extends ModelBase
//{
//    public final ShapeRenderer[][] shapes;
//
//    public ModelFalconWings()
//    {
//        textureWidth = 32;
//        textureHeight = 16;
//        shapes = new ShapeRenderer[2][6];
//
//        for (int side = 0; side < shapes.length; ++side)
//        {
//            int prog = side == 0 ? 21 : 5;
//            int i = side * 2 - 1;
//
//            for (int segment = 0; segment < shapes[side].length; ++segment)
//            {
//                ShapeRenderer renderer = new ShapeRenderer(this);
//                int width = (segment < 2 ? 5 : 4) * i;
//                int y = segment == 0 ? -2 : 0;
//
//                // 5, 5, 4, 4, 4, 4
//
//                renderer.startBuilding(GL11.GL_QUADS | GL11.GL_LIGHTING);
//                renderer.addVertex(0, y + 8, 0, prog - width, 8 * side + 8);
//                renderer.addVertex(width, y + 8, 0, prog, 8 * side + 8);
//                renderer.addVertex(width, y, 0, prog, 8 * side);
//                renderer.addVertex(0, y, 0, prog - width, 8 * side);
//                renderer.build();
//                prog += width;
//
//                shapes[side][segment] = renderer;
//
//                if (segment > 0)
//                {
//                    shapes[side][segment - 1].addChild(renderer);
//                }
//            }
//        }
//
//        resetAngles();
//    }
//
//    public void render(int side, float scale)
//    {
//        shapes[side][0].render(scale);
//    }
//
//    public void resetAngles()
//    {
//        for (int side = 0; side < 2; ++side)
//        {
//            int i = side * 2 - 1;
//            shapes[side][0].setRotationPoint(2 * i, 2, 1.8F);
//            shapes[side][1].setRotationPoint(5 * i, -2, 0);
//            shapes[side][2].setRotationPoint(5 * i, 0, 0);
//            shapes[side][3].setRotationPoint(4 * i, 0, 0);
//            shapes[side][4].setRotationPoint(4 * i, 0, 0);
//            shapes[side][5].setRotationPoint(4 * i, 0, 0);
//            setRotateAngle(shapes[side][0], 0, 0, 0.7F * i);
//            setRotateAngle(shapes[side][1], 0, 0.0175F * i, 0.87F * i);
//            setRotateAngle(shapes[side][2], 0, 0.0175F * i, 0.99F * i);
//            setRotateAngle(shapes[side][3], 0, 0.0175F * i, 1.08F * i);
//            setRotateAngle(shapes[side][4], 0, 0.0175F * i, 0.50F * i);
//            setRotateAngle(shapes[side][5], 0, 0.0175F * i, 1.32F * i);
//        }
//    }
//
//    public void setAngles(Entity entity, float wings, float shield)
//    {
//        resetAngles();
//
//        if (entity instanceof EntityPlayer)
//        {
//            EntityPlayer player = (EntityPlayer) entity;
//            float f = wings * wings * 0.8F;
//            float f1 = -0.261799F;
//            float f2 = 1;
//
//            Vec3 motion = SHRenderHelper.getMotion(player);
//
//            if (motion.yCoord < 0)
//            {
//                motion = motion.normalize();
//                f2 = 1 - (float) Math.pow(-motion.yCoord, 1.5);
//            }
//
//            f1 = f2 * -1.570796F + (1 - f2) * f1;
//
//            for (int side = 0; side < 2; ++side)
//            {
//                int i = side * 2 - 1;
//                shapes[side][0].rotateAngleZ -= 0.7F * f * i;
//                shapes[side][1].rotateAngleZ -= 0.87F * f * i;
//                shapes[side][2].rotateAngleZ -= 0.99F * f * i;
//                shapes[side][3].rotateAngleZ -= 1.08F * f * i;
//                shapes[side][4].rotateAngleZ -= 0.50F * f * i;
//                shapes[side][5].rotateAngleZ -= 1.32F * f * i;
//
//                for (int j = 1; j < shapes[side].length; ++j)
//                {
//                    shapes[side][j].rotateAngleY -= 0.05F * f * i;
//                }
//
//                shapes[side][0].rotationPointX -= 0.5F * wings * i;
//                shapes[side][0].rotationPointZ += 0.25F * wings;
//                shapes[side][0].rotateAngleX -= 0.2F * wings;
//                shapes[side][0].rotateAngleY -= 0.2F * wings * i;
//                shapes[side][0].rotateAngleZ = ((float) Math.PI / 2 - (0.4F - f1) * wings) * i;
//            }
//        }
//
//        if (shield > 0)
//        {
//            shield = FiskMath.curve(shield);
//            float f = Math.max(1 - shield * 3, 0);
//            shapes[0][0].rotationPointX += 1.5F * shield;
//            shapes[0][0].rotateAngleZ += 2 * shield;
//
//            for (int j = 1; j < shapes[0].length; ++j)
//            {
//                shapes[0][j].rotateAngleZ *= f;
//            }
//
//            shield *= shield;
//            shapes[0][0].rotateAngleY += 0.1F * shield;
//            shapes[0][1].rotateAngleY -= 1.4F * shield;
//            shapes[0][2].rotateAngleY -= 1.3F * shield;
//            shapes[0][3].rotateAngleY -= 0.3F * shield;
//            shapes[0][4].rotateAngleY -= 0.2F * shield;
//        }
//    }
//
//    public void setRotateAngle(ShapeRenderer renderer, float x, float y, float z)
//    {
//        renderer.rotateAngleX = x;
//        renderer.rotateAngleY = y;
//        renderer.rotateAngleZ = z;
//    }
//}
