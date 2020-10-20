package com.fiskmods.heroes.common;

public class Pair<K, V>
{
    private final K key;
    private final V value;

    private Pair(K k, V v)
    {
        key = k;
        value = v;
    }

    public K getKey()
    {
        return key;
    }

    public V getValue()
    {
        return value;
    }

    public static <K, V> Pair<K, V> of(K key, V value)
    {
        return new Pair(key, value);
    }

    @Override
    public String toString()
    {
        return String.format("Pair[k=%s,v=%s]", key, value);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (key == null ? 0 : key.hashCode());
        result = prime * result + (value == null ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        Pair other = (Pair) obj;

        if (key == null)
        {
            if (other.key != null)
            {
                return false;
            }
        }
        else if (!key.equals(other.key))
        {
            return false;
        }

        if (value == null)
        {
            if (other.value != null)
            {
                return false;
            }
        }
        else if (!value.equals(other.value))
        {
            return false;
        }

        return true;
    }
}
