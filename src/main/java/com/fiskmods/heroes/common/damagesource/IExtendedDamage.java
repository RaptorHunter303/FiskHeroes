package com.fiskmods.heroes.common.damagesource;

import net.minecraft.util.DamageSource;

public interface IExtendedDamage
{
    DamageSource with(DamageType... types);

    int getFlags();

    public enum DamageType
    {
        COLD,
        ENERGY,
        SOUND,
        CACTUS
        {
            @Override
            public boolean isPresent(DamageSource source)
            {
                return source == DamageSource.CACTUS || super.isPresent(source);
            }
        },
        SHURIKEN,
        INDIRECT;

        public final int flag = 1 << ordinal();

        public boolean isPresent(DamageSource source)
        {
            return source instanceof IExtendedDamage && (((IExtendedDamage) source).getFlags() & flag) == flag;
        }

        public static int with(int flags, DamageType... types)
        {
            for (DamageType type : types)
            {
                flags |= type.flag;
            }

            return flags;
        }
    }
}
