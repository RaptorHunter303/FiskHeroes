package com.fiskmods.heroes.client.model;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ModelFalconWings extends ModelBase
{
    public final ShapeRenderer[][][] shapes;

    public ModelFalconWings()
    {
        textureWidth = 32;
        textureHeight = 16;
        shapes = new ShapeRenderer[2][8][2];

        for (int side = 0; side < 2; ++side)
        {
            int prog = side == 0 ? 26 : 0;
            int i = side * 2 - 1;

            for (int j = 0; j < shapes[side].length; ++j)
            {
                ShapeRenderer top = new ShapeRenderer(this);
                ShapeRenderer bottom = new ShapeRenderer(this);
                int width = (j == 0 || j == 7 ? 4 : 3) * i;
                int y = j == 0 ? -2 : 0;

                prog += width;
                top.startBuilding(GL11.GL_QUADS | GL11.GL_LIGHTING);
                top.addVertex(0, y + 4, 0, prog - width, 8 * side + 4);
                top.addVertex(width, y + 4, 0, prog, 8 * side + 4);
                top.addVertex(width, y, 0, prog, 8 * side);
                top.addVertex(0, y, 0, prog - width, 8 * side);
                top.build();
                bottom.startBuilding(GL11.GL_QUADS | GL11.GL_LIGHTING);
                bottom.addVertex(0, 4, 0, prog, 8 * side + 8);
                bottom.addVertex(-width, 4, 0, prog - width, 8 * side + 8);
                bottom.addVertex(-width, 0, 0, prog - width, 8 * side + 4);
                bottom.addVertex(0, 0, 0, prog, 8 * side + 4);
                bottom.build();

                top.addChild(bottom);
                shapes[side][j][0] = top;
                shapes[side][j][1] = bottom;

                if (j > 0)
                {
                    shapes[side][j - 1][0].addChild(top);
                }
            }
        }

        resetAngles();
    }

    public void render(int side1, float s, float scale)
    {
        ShapeRenderer shape = shapes[side1][0][0];
        GL11.glPushMatrix();
        GL11.glTranslatef(shape.offsetX, shape.offsetY, shape.offsetZ);
        GL11.glTranslatef(shape.rotationPointX * scale, shape.rotationPointY * scale, shape.rotationPointZ * scale);
        GL11.glScaled(s, s, s);
        GL11.glTranslatef(-shape.offsetX, -shape.offsetY, -shape.offsetZ);
        GL11.glTranslatef(-shape.rotationPointX * scale, -shape.rotationPointY * scale, -shape.rotationPointZ * scale);
        shape.render(scale);
        GL11.glPopMatrix();
    }

    public void resetAngles()
    {
        for (int side = 0; side < 2; ++side)
        {
            int i = side * 2 - 1;
            setRotateAngle(shapes[side][0][0], 0, 0, 1.5707963267948966F * i);
            setRotateAngle(shapes[side][1][0], 0, 0, 0.6283185307179586F * i);
            setRotateAngle(shapes[side][2][0], 0, 0, 1.0821041362364843F * i);
            setRotateAngle(shapes[side][3][0], 0, 0, 1.1693705988362009F * i);
            setRotateAngle(shapes[side][4][0], 0, 0, 1.0122909661567112F * i);
            setRotateAngle(shapes[side][5][0], 0, 0, 1.1868238913561440F * i);
            setRotateAngle(shapes[side][6][0], 0, 0, 1.2740903539558606F * i);
            setRotateAngle(shapes[side][7][0], 0, 0, 1.2566370614359172F * i);

            for (int j = 0; j < shapes[side].length; ++j)
            {
                setRotateAngle(shapes[side][j][1], 0, 0, 0);
                shapes[side][j][0].rotateAngleX -= 0.01F;
                shapes[side][j][1].rotateAngleX += 0.1F;

                if (j > 0)
                {
                    shapes[side][j][0].setRotationPoint(3 * i, 0, 0);
                    shapes[side][j][1].setRotationPoint(3 * i, 0, 0);
                }
            }

            shapes[side][0][0].setRotationPoint(2 * i, 2, 1.8F);
            shapes[side][0][1].setRotationPoint(4 * i, -2, 0);
            shapes[side][1][0].setRotationPoint(4 * i, -2, 0);
            shapes[side][7][1].rotationPointX += i;
        }
    }

    public void setAngles(Entity entity, float wings, float shield)
    {
        resetAngles();

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            float f = 1 - wings * wings;
            float f1 = -0.261799F;
            float f2 = 1;

            Vec3 motion = SHRenderHelper.getMotion(player);

            if (motion.yCoord < 0)
            {
                motion = motion.normalize();
                f2 = 1 - (float) Math.pow(-motion.yCoord, 1.5);
            }

            f1 = f2 * -1.570796F + (1 - f2) * f1;

            for (int side = 0; side < 2; ++side)
            {
                int i = side * 2 - 1;

                for (int j = 0; j < shapes[side].length; ++j)
                {
                    shapes[side][j][0].rotateAngleX *= f;
                    shapes[side][j][0].rotateAngleZ *= f;

                    float f3 = 1 - (float) j / shapes[side].length;
                    shapes[side][j][1].rotationPointY += 4 * (1 - f);
                    shapes[side][j][0].rotateAngleY -= 0.075F * MathHelper.sin((float) (Math.PI / 2 * f3)) * (1 - f) * i;
                    shapes[side][j][0].rotateAngleZ += 0.1F * MathHelper.sin((float) (Math.PI / 2 * f3)) * (1 - f) * i;
                }

                ShapeRenderer shape = shapes[side][0][0];
                shape.rotationPointX += 1.5F * wings * i;
                shape.rotationPointZ += 1.5F * wings;
                shape.rotateAngleX -= 0.2F * wings;
                shape.rotateAngleY -= (0.1F * wings + 0.2F * MathHelper.sin((float) (wings * Math.PI))) * i;
                shape.rotateAngleZ = ((float) Math.PI / 2 - (0.4F - f1) * wings) * i;
            }
        }

        if (shield > 0)
        {
            shield = FiskMath.curve(shield);
            float f = 1 - shield;

            for (int j = 0; j < shapes[0].length; ++j)
            {
                shapes[0][j][0].rotateAngleZ *= f;
                shapes[0][j][0].rotateAngleX *= f;
                shapes[0][j][1].rotateAngleX *= f;
                shapes[0][j][1].rotationPointY += ((j == 0 ? 2 : 4) - shapes[0][j][1].rotationPointY) * (1 - f);
            }

            ShapeRenderer shape = shapes[0][0][0];
            shape.rotationPointX -= 0.5F * shield;
            shape.rotationPointY += 1F * shield;
            shape.rotationPointZ += 1F * shield;
            shape.rotateAngleZ += 0.3F * shield;
            shape.rotateAngleX -= 0.1F * shield;
            shape.rotateAngleY += 0.1F * shield + 0.8F * MathHelper.sin((float) (shield * Math.PI));
            shapes[0][1][0].rotateAngleY -= 1.2F * shield;
            shapes[0][2][0].rotateAngleY -= 1.1F * shield;
            shapes[0][3][0].rotateAngleY -= 0.4F * shield;
            shapes[0][4][0].rotateAngleY -= 0.3F * shield;
            shapes[0][5][0].rotateAngleY -= 0.1F * shield;
            shapes[0][6][0].rotateAngleY -= 0.1F * shield;
            shapes[0][7][0].rotateAngleY -= 0.1F * shield;
        }
    }

    public void setRotateAngle(ShapeRenderer renderer, float x, float y, float z)
    {
        renderer.rotateAngleX = x;
        renderer.rotateAngleY = y;
        renderer.rotateAngleZ = z;
    }
}
