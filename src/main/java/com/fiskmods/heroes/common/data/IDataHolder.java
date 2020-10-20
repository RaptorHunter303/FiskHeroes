package com.fiskmods.heroes.common.data;

public interface IDataHolder
{
    <T> void set(SHData<T> data, T value);

    <T> T get(SHData<T> data);
}
