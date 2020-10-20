package com.fiskmods.heroes.client.json.shape;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffect;
import com.google.gson.stream.JsonReader;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public enum ShapeFormatCircles implements IShapeFormat
{
    INSTANCE;

    @Override
    public float[] read(JsonReader in) throws IOException
    {
        return HeroEffect.readArray(in, new float[2], f -> f);
    }

    @Override
    public void render(JsonShape shape, ModelBipedMultiLayer model, Entity entity, ItemStack itemstack, int pass, float s, float mult, float ticks, float scale, boolean left)
    {
        Tessellator tessellator = Tessellator.instance;
        int i = 0;

        for (ShapeEntry entry : shape.shapes)
        {
            float sides = entry.data[0];
            float size = entry.data[1];
            float a = (float) Math.toRadians(shape.maxAngle / sides) * mult;
            Vec3 vec3 = Vec3.createVectorHelper(0, MathHelper.sin((ticks + i) / 5) / 30 * scale, size * scale);

//            vec3.rotateAroundY((float) Math.toRadians(data[2] * mult + 2 * f * MathHelper.sin(1 + i)));

            vec3.rotateAroundX(entry.rotation[0]);
            vec3.rotateAroundY(entry.rotation[1] + 3.14F * MathHelper.sin(1 + i) * (1 - mult));
            vec3.rotateAroundX(entry.rotation[2]);

            tessellator.startDrawing(GL11.GL_LINE_STRIP);

            for (int j = 0; j <= sides; ++j)
            {
                tessellator.addVertex(vec3.xCoord, vec3.yCoord, vec3.zCoord);
                vec3.rotateAroundY(a);
            }

            tessellator.draw();
            ++i;
        }
    }
}
