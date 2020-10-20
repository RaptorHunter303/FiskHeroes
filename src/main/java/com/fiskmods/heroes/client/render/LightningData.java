package com.fiskmods.heroes.client.render;

import com.fiskmods.heroes.client.render.hero.effect.HeroEffectTrail;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class LightningData
{
    public final Lightning lightning;
    public final Vec3 pos;

    public int progress;

    public LightningData(Lightning lightning, double x, double y, double z)
    {
        this.lightning = lightning;
        pos = Vec3.createVectorHelper(x, y, z);
    }

    public void onUpdate(EntityPlayer player, World world)
    {
        lightning.onUpdate(world);

        if (++progress > 4)
        {
            HeroEffectTrail.getLightningData(player).remove(this);
            return;
        }
    }
}
