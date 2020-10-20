package com.fiskmods.heroes.asm;

public class SHTranslator
{
    public static boolean obfuscatedEnv;

    public static String getMappedClassName(String className)
    {
        return "net/minecraft/" + className.replace(".", "/");
    }

    public static String getMappedName(String name, String devName)
    {
        return obfuscatedEnv ? name : devName;
    }
}
