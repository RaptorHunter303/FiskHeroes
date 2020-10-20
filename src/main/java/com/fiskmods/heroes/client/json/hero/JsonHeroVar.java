package com.fiskmods.heroes.client.json.hero;

import java.util.function.BiFunction;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.SHDataInterp;
import com.fiskmods.heroes.pack.JSHeroesEngine;
import com.fiskmods.heroes.pack.accessor.JSEntityAccessor;
import com.fiskmods.heroes.pack.accessor.JSItemAccessor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class JsonHeroVar
{
    private final String key;
    private BiFunction<Entity, ItemStack, Object> interpreter;

    public JsonHeroVar(String key)
    {
        this.key = key;
    }

    public Object get(Entity entity, ItemStack stack)
    {
        if (interpreter == null)
        {
            interpreter = JsonHeroVar.createInterpreter(key, false);
        }

        return interpreter.apply(entity, stack);
    }

    public boolean test(Entity entity, ItemStack stack)
    {
        Object obj = get(entity, stack);
        return obj instanceof Boolean ? ((Boolean) obj).booleanValue() : "true".equals(String.valueOf(obj));
    }

    public static BiFunction<Entity, ItemStack, Object> createInterpreter(String key, boolean interpolate)
    {
        if (key.matches("^\\w+?:\\w+$"))
        {
            SHData<?> data = SHData.REGISTRY.getObject(key);

            if (interpolate && data instanceof SHDataInterp<?>)
            {
                SHDataInterp<?> data1 = (SHDataInterp<?>) data;
                return (entity, stack) -> data1.interpolate(entity);
            }
            else if (data != null)
            {
                return (entity, stack) -> data.get(entity);
            }
            else
            {
                return (entity, stack) -> null;
            }
        }
        else
        {
            return (entity, stack) -> entity instanceof EntityLivingBase ? JSHeroesEngine.INSTANCE.evalSilently(key, "entity, item", JSEntityAccessor.wrap((EntityLivingBase) entity), JSItemAccessor.wrap(stack)) : null;
        }
    }
}
