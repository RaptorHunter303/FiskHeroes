package com.fiskmods.heroes.client.json.trail;

import java.io.IOException;

import com.fiskmods.heroes.client.SHResourceHandler;
import com.fiskmods.heroes.util.TextureHelper;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class JsonTrailParticles extends JsonConstantContainer
{
    private final JsonConstant<String> texture;
    private final JsonConstant<Float> motion;
    private final JsonConstant<Float> speed;
    private final JsonConstant<Integer> fade;
    private final JsonConstant<Integer> density;
    private final JsonConstant<Float> differ;
    private final JsonConstant<Float> opacity;
    private final JsonConstant<Float> scale;

    private ResourceLocation textureLocation;

    public JsonTrailParticles()
    {
        texture = init("texture", String.class, "");
        motion = init("motion", Float.class, 0F);
        speed = init("speed", Float.class, 1F);
        fade = init("fade", Integer.class, 10);
        density = init("density", Integer.class, 6);
        differ = init("differ", Float.class, 0.435F);
        opacity = init("opacity", Float.class, 1F);
        scale = init("scale", Float.class, 1F);
    }

    public void load(JsonTrail json, IResourceManager manager, TextureManager textureManager) throws IOException
    {
        if (StringUtils.isNullOrEmpty(texture.get()))
        {
            textureLocation = TextureHelper.MISSING_TEXTURE;
        }
        else
        {
            SHResourceHandler.listen(textureLocation = new ResourceLocation(texture.get()));
        }

        textureManager.bindTexture(textureLocation);
    }

    public ResourceLocation getTexture()
    {
        return textureLocation;
    }

    public Float getMotion()
    {
        return motion.get();
    }

    public Float getSpeed()
    {
        return speed.get();
    }

    public Integer getFade()
    {
        return fade.get();
    }

    public Integer getDensity()
    {
        return density.get();
    }

    public Float getDiffer()
    {
        return differ.get();
    }

    public Float getOpacity()
    {
        return opacity.get();
    }

    public Float getScale()
    {
        return scale.get();
    }
}
