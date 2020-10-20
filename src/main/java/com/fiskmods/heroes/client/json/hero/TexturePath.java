package com.fiskmods.heroes.client.json.hero;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class TexturePath
{
    private static final Pattern PATTERN = Pattern.compile("<(\\w+)>?");

    protected final String key;
    protected final String path;

    protected TexturePath(ResourceVarTx tx, String s)
    {
        Matcher m = PATTERN.matcher(s);
        key = s;

        while (m.find())
        {
            int i = tx.indexOf(m.group(1));

            if (i >= 0)
            {
                s = s.replace(m.group(), "%" + i + "$s");
            }
        }

        path = format(s);
    }

    public void load(TextureManager manager, ResourceVarTx tx, Map<String, String> perm)
    {
        tx.load(manager, new ResourceLocation(formatPath(perm.values().toArray())));
    }

    public String formatPath(Object... args)
    {
        if (args != null && args.length > 0)
        {
            return String.format(path, args);
        }

        return path;
    }

    public static TexturePath of(ResourceVarTx tx, String s)
    {
        if (tx == null || StringUtils.isNullOrEmpty(s))
        {
            return null;
        }

        return new TexturePath(tx, s);
    }

    public static String format(String s)
    {
        int i = s.indexOf(':');

        if (i >= 0)
        {
            return s.substring(0, i + 1) + JsonHeroRenderer.TEXTURE_DIR + s.substring(i + 1, s.length()) + ".png";
        }

        return JsonHeroRenderer.TEXTURE_DIR + s + ".png";
    }

    @Override
    public String toString()
    {
        return "{\"" + key + "\" = \"" + path + "\"}";
    }
}
