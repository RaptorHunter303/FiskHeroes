package com.fiskmods.heroes.client.render;

import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.client.json.trail.ITrailAspect;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Lightning
{
    public List<Lightning> children = new ArrayList<>();
    public Lightning parent;

    public float scale;
    public float length;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public Vec3 lightningColor;

    public Lightning(float length, float scale)
    {
        this(length, scale, ITrailAspect.DEFAULT.getVecColor());
    }

    public Lightning(float length, float scale, Vec3 color)
    {
        this.scale = scale;
        this.length = length;
        lightningColor = color;
    }

    public void onUpdate(World world)
    {
        // if (parent == null)
        // {
        // Lightning lightning = SHHelper.createLightning(lightningType, 0, length).setScale(scale);
        // children.clear();
        // children.addAll(lightning.children);
        // scale = lightning.scale;
        // rotateAngleX = lightning.rotateAngleX;
        // rotateAngleY = lightning.rotateAngleY;
        // rotateAngleZ = lightning.rotateAngleZ;
        // }
    }

    public Lightning addChild(Lightning child)
    {
        child.parent = this;
        child.scale = scale;
        child.lightningColor = lightningColor;
        children.add(child);
        return this;
    }

    public Lightning setRotation(float rotX, float rotY, float rotZ)
    {
        rotateAngleX = rotX;
        rotateAngleY = rotY;
        rotateAngleZ = rotZ;
        return this;
    }
}
