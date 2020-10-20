package com.fiskmods.heroes.common.registry;

import com.google.common.reflect.TypeToken;

import net.minecraft.util.ResourceLocation;

public class FiskRegistryEntry<T>
{
    public final FiskDelegate<T> delegate = new FiskDelegate(this, getClass());

    private ResourceLocation registryName = null;
    private TypeToken<T> token = new TypeToken<T>(getClass())
    {
    };

    public final T setRegistryName(String name)
    {
        if (getRegistryName() != null)
        {
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());
        }

        delegate.setName(name);
        registryName = new ResourceLocation(name);

        return (T) this;
    }

    public final T setRegistryName(ResourceLocation name)
    {
        return setRegistryName(name.toString());
    }

    public final T setRegistryName(String domain, String name)
    {
        return setRegistryName(domain + ":" + name);
    }

    public final ResourceLocation getRegistryName()
    {
        return registryName;
    }

    public final String getDomain()
    {
        return registryName.getResourceDomain();
    }

    public final Class<T> getRegistryType()
    {
        return (Class<T>) token.getRawType();
    }

    @Override
    public String toString()
    {
        return delegate.name();
    }
}
