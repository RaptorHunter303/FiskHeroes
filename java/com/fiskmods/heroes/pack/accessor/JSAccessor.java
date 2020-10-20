package com.fiskmods.heroes.pack.accessor;

public interface JSAccessor<T extends JSAccessor<T>>
{
    boolean matches(T t);
}
