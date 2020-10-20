package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.json.cloud.ParticleColor;
import com.fiskmods.heroes.client.particle.EntitySHBreachFX;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectTeleportation;
import com.fiskmods.heroes.common.DimensionalCoords;
import com.fiskmods.heroes.common.Vec3Container;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.NBTHelper;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public class MessageTeleport extends AbstractMessage<MessageTeleport>
{
    private int id;
    private DimensionalCoords dest;

    public MessageTeleport()
    {
    }

    public MessageTeleport(EntityLivingBase entity, DimensionalCoords coords)
    {
        id = entity.getEntityId();
        dest = coords != null ? coords : new DimensionalCoords();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        dest = NBTHelper.fromBytes(buf, DimensionalCoords.class);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        NBTHelper.toBytes(buf, dest);
    }

    @Override
    public void receive() throws MessageException
    {
        EntityLivingBase user = getEntity(EntityLivingBase.class, id);
        HeroIteration iter = SHHelper.getHeroIter(user);

        if (iter != null)
        {
            HeroRenderer renderer = HeroRenderer.get(iter);
            HeroEffectTeleportation effect = renderer.getEffect(HeroEffectTeleportation.class, user);

            if (effect != null && effect.getParticles() != null && effect.getParticles().color != null)
            {
                doParticles(user, effect.getParticles().color, Vec3Container.wrap(user));
                doParticles(user, effect.getParticles().color, new Vec3Container(() -> dest.posX + 0.5, () -> dest.posY + user.height / 2.0, () -> dest.posZ + 0.5));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void doParticles(EntityLivingBase user, ParticleColor[] color, Vec3Container pos)
    {
        Vec3 offset = Vec3.createVectorHelper(0, 1.5F, 0);
        float pitch = user == FiskHeroes.proxy.getPlayer() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 ? 5 : 10;
        float yaw = pitch * 2;
        float scale = SHData.SCALE.get(user);

        for (int i = 0; i < pitch; ++i)
        {
            offset.rotateAroundX((float) Math.toRadians(180 / pitch));
            Vec3 off = VectorHelper.copy(offset);

            for (int j = 0; j < yaw; ++j)
            {
                off.rotateAroundY((float) Math.toRadians(360 / yaw));

                Vec3 v = Vec3.createVectorHelper(off.xCoord * user.width, off.yCoord * user.height / 2, off.zCoord * user.width);
                Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySHBreachFX(user.worldObj, pos, color, v.xCoord, v.yCoord, v.zCoord, scale));
            }
        }
    }
}
