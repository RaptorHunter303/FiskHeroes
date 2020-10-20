package com.fiskmods.heroes.client.model.item;

import com.fiskmods.heroes.client.model.ShapeRenderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.util.MathHelper;

public class ModelRetractableShield extends ModelBase
{
    public final int segments, center, width;

    private final ShapeRenderer[] spine;
    private final ShapeRenderer[][][] sides;

    public ModelRetractableShield(int sideSegments, int spineLength, int spineCenter)
    {
        segments = sideSegments;
        center = spineCenter - 1;
        width = segments * 4 + 2;

        spine = new ShapeRenderer[spineLength];
        sides = new ShapeRenderer[spineLength][2][segments];
        textureWidth = 32;
        textureHeight = 16;

        while (textureWidth < width * 2)
        {
            textureWidth *= 2;
        }

        while (textureHeight < spineLength * 2)
        {
            textureHeight *= 2;
        }

        for (int i = 0; i < spine.length; ++i)
        {
            int y = i < center ? -2 : 0;
            spine[i] = newShape(-1, y, 2, 2, segments * 2, i * 2);

            if (i > center)
            {
                spine[i - 1].addChild(spine[i]);
            }
            else if (i > 0)
            {
                spine[i].addChild(spine[i - 1]);
            }

            for (int side = 0; side < 2; ++side)
            {
                ShapeRenderer prev = spine[i];
                int j = side * 2 - 1;

                for (int segment = 0; segment < segments; ++segment)
                {
                    ShapeRenderer shape = newShape(-side * 2, y, 2, 2, segments * 2 + j * (segment + 1) * 2, i * 2);
                    prev.addChild(shape);
                    prev = shape;

                    sides[i][side][segment] = shape;
                }
            }
        }
    }

    private ShapeRenderer newShape(int x, int y, int w, int h, int u, int v)
    {
        ShapeRenderer shape = new ShapeRenderer(this);
        shape.startBuildingQuads();
        shape.addVertex(0, y, x + w, u, v);
        shape.addVertex(0, y + h, x + w, u, v + h);
        shape.addVertex(0, y + h, x, u + w, v + h);
        shape.addVertex(0, y, x, u + w, v);
        u += width;
        shape.addVertex(0, y + h, x, u + w, v + h);
        shape.addVertex(0, y + h, x + w, u, v + h);
        shape.addVertex(0, y, x + w, u, v);
        shape.addVertex(0, y, x, u + w, v);
        shape.build();

        return shape;
    }

    public void render(float mult, float curveX, float curveZ, float scale)
    {
        setAngles(mult, curveX / (spine.length - 2), curveZ / segments);
        spine[center].render(scale);
    }

    public void setAngles(float mult, float curveX, float curveZ)
    {
        int max = Math.max(spine.length - center, center);

        for (int i = 0; i < spine.length; ++i)
        {
            float f = MathHelper.clamp_float(i < center ? mult * max - center + i + 1 : mult * max - i + center, 0, 1);
            float f1 = 1 - f;

            if (i != center)
            {
                int j = i < center ? -1 : 1;
                spine[i].rotationPointY = 2 * j * f + (i == center - 1 ? 2 : 0);
                spine[i].rotateAngleX = 0;

                if (i != center - 1)
                {
                    spine[i].rotateAngleZ = -curveX * j * f;
                }
            }

            for (int side = 0; side < 2; ++side)
            {
                int j = side * 2 - 1;

                for (int segment = 0; segment < segments; ++segment)
                {
                    ShapeRenderer shape = sides[i][side][segment];
//                  shape.rotationPointZ = -(2 * f - (segment == 0 ? 1 : 0)) * j;
                    shape.rotationPointZ = (segment == 0 ? j : 0) - 2 * j * f;
                    shape.rotationPointX = 0.1F * f1;
                    shape.rotateAngleY = -curveZ * j * f;
                }
            }
        }
    }
}
