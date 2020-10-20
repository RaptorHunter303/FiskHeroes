package com.fiskmods.heroes.client.json.shape;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffect;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.google.gson.stream.JsonReader;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public enum ShapeFormatLines implements IShapeFormat
{
    INSTANCE;

    @Override
    public float[] read(JsonReader in) throws IOException
    {
        return HeroEffect.readArray(in, f -> f);
    }

    @Override
    public void render(JsonShape shape, ModelBipedMultiLayer model, Entity entity, ItemStack itemstack, int pass, float s, float mult, float ticks, float scale, boolean left)
    {
        Tessellator tessellator = Tessellator.instance;
        float f1 = mult / 2;
        int i = 0;

        for (ShapeEntry entry : shape.shapes)
        {
            tessellator.startDrawing(GL11.GL_LINE_STRIP);
            float f = (float) (entry.data.length * 360 / Math.PI);

            for (int j = 0; j < entry.data.length - 2; j += 3)
            {
                tessellator.addVertex(FiskServerUtils.interpolate(MathHelper.sin(i * f) * f1, entry.data[j], mult), FiskServerUtils.interpolate(MathHelper.cos(i * f) * f1, entry.data[j + 1], mult), entry.data[j + 2] * mult);
                ++i;
            }

            tessellator.draw();
        }
    }
}
