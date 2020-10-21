package com.fiskmods.heroes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.nbt.ListNBT;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.world.gen.feature.OreFeature;

public class SHReflection
{
    // Client
    private static GenericMethod<Object, Void> renderNametagMethod;
    private static GenericMethod<Object, Void> clearResourcesMethod;

    public static GenericField<Object, String> splashTextField;
    public static GenericField<Object, Map> mapTextureObjectsField;
    public static GenericField<Object, Float> thirdPersonDistanceField;
    public static GenericField<Object, List> defaultResourcePacksField;

    // Common
    private static GenericMethod<Entity, Void> setSizeMethod;
    private static GenericMethod<LivingEntity, Integer> getArmSwingAnimationEndMethod;
    private static GenericMethod<Entity, Boolean> canTriggerWalkingMethod;
    private static GenericMethod<CreeperEntity, Void> creeperExplodeMethod;

    public static GenericField<Entity, Integer> nextStepDistanceField;
    public static GenericField<ListNBT, List> tagListField;
    public static GenericField<OreFeature, Block> genMineableOreField;
    public static GenericField<OreFeature, Integer> genMineableMetaField;
    public static GenericField<OreFeature, Integer> genMineableNumField;
    public static GenericField<OreFeature, Block> genMineableStoneField;


    public static void client()
    {
        renderNametagMethod = MethodBuilder.in(PlayerRenderer.class, void.class).with(LivingEntity.class, double.class, double.class, double.class, String.class, float.class, double.class).find("func_96449_a");
        clearResourcesMethod = MethodBuilder.in(SimpleReloadableResourceManager.class, void.class).with().find("func_110543_a", "clearResources");

        splashTextField = new GenericField(MainMenuScreen.class, String.class, "field_73975_c", "splashText");
        mapTextureObjectsField = new GenericField(TextureManager.class, Map.class, "field_110585_a", "mapTextureObjects");
        thirdPersonDistanceField = new GenericField(EntityRenderer.class, float.class, "field_78490_B", "thirdPersonDistance");
        defaultResourcePacksField = new GenericField(Minecraft.class, List.class, "field_110449_ao", "defaultResourcePacks");
    }

    public static void common()
    {
        setSizeMethod = MethodBuilder.in(Entity.class, void.class).with(float.class, float.class).find("func_70105_a", "setSize");
        getArmSwingAnimationEndMethod = MethodBuilder.in(LivingEntity.class, int.class).with().find("func_82166_i", "getArmSwingAnimationEnd");
        canTriggerWalkingMethod = MethodBuilder.in(Entity.class, boolean.class).with().find("func_70041_e_", "canTriggerWalking");
        creeperExplodeMethod = MethodBuilder.in(CreeperEntity.class, void.class).with().find("func_146077_cc");

        nextStepDistanceField = new GenericField(Entity.class, int.class, "field_70150_b", "nextStepDistance");
        tagListField = new GenericField(ListNBT.class, List.class, "field_74747_a", "tagList");
        genMineableOreField = new GenericField(OreFeature.class, Block.class, "field_150519_a");
        genMineableMetaField = new GenericField(OreFeature.class, int.class, "mineableBlockMeta");
        genMineableNumField = new GenericField(OreFeature.class, int.class, "field_76541_b", "numberOfBlocks");
        genMineableStoneField = new GenericField(OreFeature.class, Block.class, "field_150518_c");
    }

    public static void renderNametag(PlayerRenderer instance, LivingEntity entity, double x, double y, double z, String username, float p_96449_9_, double p_96449_10_)
    {
        renderNametagMethod.invoke(instance, entity, x, y, z, username, p_96449_9_, p_96449_10_);
    }

    public static void clearResources(SimpleReloadableResourceManager instance)
    {
        clearResourcesMethod.invoke(instance);
    }

    public static void setSize(Entity instance, float f, float f1)
    {
        setSizeMethod.invoke(instance, f, f1);
    }

    public static int getArmSwingAnimationEnd(LivingEntity instance)
    {
        return getArmSwingAnimationEndMethod.invoke(instance);
    }

    public static boolean canTriggerWalking(Entity instance)
    {
        return canTriggerWalkingMethod.invoke(instance);
    }

    public static void creeperExplode(CreeperEntity instance)
    {
        creeperExplodeMethod.invoke(instance);
    }

    public static class GenericField<C, T>
    {
        private final Field theField;

        public GenericField(Class<C> parent, Class<T> type, String... names)
        {
            for (String name : names)
            {
                for (Field field : parent.getDeclaredFields())
                {
                    if (field.getName().equals(name) && field.getType() == type)
                    {
                        field.setAccessible(true);
                        theField = field;
                        return;
                    }
                }
            }

            throw new RuntimeException(String.format("Unable to locate field of type %s in %s: %s", type.getName(), parent.getName(), Arrays.asList(names)));
        }

        public T get(C instance)
        {
            try
            {
                return (T) theField.get(instance);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        public T getStatic()
        {
            return get(null);
        }

        public void set(C instance, T value)
        {
            try
            {
                theField.set(instance, value);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        public void setStatic(T value)
        {
            set(null, value);
        }
    }

    public static class MethodBuilder<C, T>
    {
        private final Class<C> parentClass;
        private final Class<T> returnType;

        private Class[] parameters;

        private MethodBuilder(Class<C> parent, Class<T> type)
        {
            parentClass = parent;
            returnType = type;
        }

        public static <C, T> MethodBuilder in(Class<C> parent, Class<T> type)
        {
            return new MethodBuilder(parent, type);
        }

        public MethodBuilder with(Class... params)
        {
            parameters = params;
            return this;
        }

        public GenericMethod find(String... names)
        {
            List<String> list = Arrays.asList(names);
            String print = getParameterPrint(parameters);

            for (Method method : parentClass.getDeclaredMethods())
            {
                if (method.getReturnType() == returnType && list.contains(method.getName()) && print.equals(getParameterPrint(method.getParameterTypes())))
                {
                    method.setAccessible(true);
                    return new GenericMethod(method);
                }
            }

            throw new RuntimeException(String.format("Unable to locate method of type %s in %s: %s", returnType.getName(), parentClass.getName(), list));
        }

        public static String getParameterPrint(Class[] params)
        {
            if (params != null)
            {
                StringBuilder sb = new StringBuilder();

                for (Class c : params)
                {
                    sb.append(c.getCanonicalName()).append(";");
                }

                return sb.toString();
            }

            return "";
        }
    }

    public static class GenericMethod<C, T>
    {
        private final Method theMethod;

        public GenericMethod(Method method)
        {
            theMethod = method;
        }

        public T invoke(C instance, Object... args)
        {
            try
            {
                return (T) theMethod.invoke(instance, args);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        public T invokeStatic(Object... args)
        {
            return invoke(null, args);
        }
    }
}
