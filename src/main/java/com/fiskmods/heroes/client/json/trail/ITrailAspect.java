package com.fiskmods.heroes.client.json.trail;

import com.fiskmods.heroes.client.SHResourceHandler;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.util.SHRenderHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public interface ITrailAspect
{
    ITrailAspect DEFAULT = new ITrailAspect()
    {
        @Override
        public Integer getColor()
        {
            return -1;
        }

        @Override
        public Integer getColor(EntityLivingBase entity)
        {
            return getColor();
        }

        @Override
        public Vec3 getVecColor()
        {
            return SHRenderHelper.WHITE;
        }

        @Override
        public Vec3 getVecColor(EntityLivingBase entity)
        {
            return getVecColor();
        }

        @Override
        public boolean isLightning()
        {
            return true;
        }
    };

    Integer getColor();

    Integer getColor(EntityLivingBase entity);

    Vec3 getVecColor();

    Vec3 getVecColor(EntityLivingBase entity);

    boolean isLightning();

    class V9Wrapper<T extends ITrailAspect> implements ITrailAspect
    {
        private final T parent;

        public V9Wrapper(T aspect)
        {
            parent = aspect;
        }

        public T get()
        {
            return parent;
        }

        @Override
        public Integer getColor()
        {
            return parent.getColor();
        }

        @Override
        public Integer getColor(EntityLivingBase entity)
        {
            if (isLightning())
            {
                ITrailAspect v9 = SHResourceHandler.getV9Trail().getTrailLightning();

                if (SHData.isTracking(entity))
                {
                    float f = SHData.VEL9_CONVERT.interpolate(entity);

                    if (f == 1)
                    {
                        return v9.getColor();
                    }
                    else if (f == 0)
                    {
                        return getColor();
                    }

                    return SHRenderHelper.getHex(SHRenderHelper.fade(getColor(), v9.getColor(), f));
                }
                else if (StatusEffect.has(entity, StatEffect.VELOCITY_9))
                {
                    return v9.getColor();
                }
            }

            return getColor();
        }

        @Override
        public Vec3 getVecColor()
        {
            return parent.getVecColor();
        }

        @Override
        public Vec3 getVecColor(EntityLivingBase entity)
        {
            if (isLightning())
            {
                ITrailAspect v9 = SHResourceHandler.getV9Trail().getTrailLightning();

                if (SHData.isTracking(entity))
                {
                    return SHRenderHelper.fade(getVecColor(), v9.getVecColor(), SHData.VEL9_CONVERT.interpolate(entity));
                }
                else if (StatusEffect.has(entity, StatEffect.VELOCITY_9))
                {
                    return v9.getVecColor();
                }
            }

            return getVecColor();
        }

        @Override
        public boolean isLightning()
        {
            return parent.isLightning();
        }
    }
}
