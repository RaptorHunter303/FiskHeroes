package com.fiskmods.heroes.client.json.trail;

import java.io.IOException;

import com.google.gson.stream.JsonReader;

public class JsonConstant<T>
{
    public final Class<T> type;
    public final String name;
    public final T defVal;

    private String key;
    private T value;

    public JsonConstant(String s, Class<T> c, T def)
    {
        name = s;
        type = c;
        defVal = def;
    }

    public T get()
    {
        if (value == null)
        {
            return defVal;
        }

        return value;
    }

    public JsonConstant<T> set(Object obj)
    {
        if (obj != null)
        {
            if (obj instanceof String)
            {
                if (type == Integer.class)
                {
                    obj = Integer.decode(String.valueOf(obj));
                }
                else if (type == Long.class)
                {
                    obj = Long.decode(String.valueOf(obj));
                }
                else if (Number.class.isAssignableFrom(type))
                {
                    obj = Double.valueOf(String.valueOf(obj));
                }
                else if (type == Boolean.class)
                {
                    obj = Boolean.valueOf(String.valueOf(obj));
                }
            }

            if (Number.class.isAssignableFrom(type) && !type.isAssignableFrom(obj.getClass()))
            {
                Number num = (Number) obj;

                if (type == Integer.class)
                {
                    obj = num.intValue();
                }
                else if (type == Long.class)
                {
                    obj = num.longValue();
                }
                else if (type == Byte.class)
                {
                    obj = num.byteValue();
                }
                else if (type == Short.class)
                {
                    obj = num.shortValue();
                }
                else if (type == Float.class)
                {
                    obj = num.floatValue();
                }
            }

            if (!type.isInstance(obj))
            {
                throw new ClassCastException(String.format("Field %s of type %s cannot be cast to %s!", name, type.getName(), obj.getClass().getName()));
            }
        }

        value = (T) obj;
        return this;
    }

    public void load(String k)
    {
        key = k;
    }

    public void inherit(JsonConstant<T> parent)
    {
        if (key == null && value == null)
        {
            key = parent.key;
            set(parent.get());
        }
    }

    public void postInit(JsonTrail trail) throws IOException
    {
        if (key != null)
        {
            if (!trail.constants.containsKey(key))
            {
                throw new IOException(String.format("Unresolved variable '%s'", key));
            }

            set(trail.constants.get(key));
        }
    }

    public void read(JsonReader in) throws IOException
    {
        switch (in.peek())
        {
        case BOOLEAN:
            set(in.nextBoolean());
            break;
        case NUMBER:
            if (Number.class.isAssignableFrom(type))
            {
                set(in.nextDouble());
            }
            else
            {
                in.skipValue();
            }

            break;
        case STRING:
            String s = in.nextString();

            if (type == String.class)
            {
                set(s);
            }
            else if (s.startsWith("@"))
            {
                load(s.substring(1));
            }
            else if (type == Integer.class || type == Long.class || type == Boolean.class)
            {
                set(s);
            }

            break;
        default:
            in.skipValue();
            break;
        }
    }
}
