package com.fiskmods.heroes.client.json.hero;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fiskmods.heroes.common.Pair;
import com.fiskmods.heroes.util.TextureHelper;
import com.fiskmods.heroes.util.TextureHelper.TextureOperation;
import com.google.common.collect.ImmutableList;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class TexturePathModifier extends TexturePath
{
    public final ResourceVarTx tx;
    public final TexturePath pathIn;

    public final ImmutableList<Pair<TextureOperation, String>> transforms;

    public TexturePathModifier(ResourceVarTx tx, String in, String out, List<Pair<TextureOperation, String>> transforms)
    {
        super(tx, out);
        this.tx = tx;
        pathIn = TexturePath.of(tx, in);
        this.transforms = transforms != null ? ImmutableList.copyOf(transforms) : null;
    }

    public TexturePathModifier(ResourceVarTx tx, String in)
    {
        this(tx, in, null, null);
    }

    @Override
    public void load(TextureManager manager, ResourceVarTx tx, Map<String, String> perm)
    {
        Object[] args = perm.values().toArray();
        ResourceLocation out = new ResourceLocation(formatPath(args));

        if (pathIn != null && transforms != null)
        {
            ResourceLocation in = new ResourceLocation(pathIn.formatPath(args));

            for (Pair<TextureOperation, String> p : transforms)
            {
                ResourceLocation other = new ResourceLocation(TexturePath.of(tx, p.getValue()).formatPath(args));
                manager.loadTexture(out, TextureHelper.modifyTexture(in, out, other, p.getKey()));
                tx.resources.add(other);
            }

            tx.load(manager, in);
        }

        tx.load(manager, out);
    }

    public static Function<ResourceVarTx, TexturePath> read(JsonReader in) throws IOException
    {
        String name = "";
        in.beginObject();

        List<Pair<TextureOperation, String>> transforms = null;
        String textureIn = null;
        String textureOut = null;

        while (in.hasNext())
        {
            JsonToken next = in.peek();

            if (next == JsonToken.NAME)
            {
                name = in.nextName();
            }
            else if (name.equals("in") && next == JsonToken.STRING)
            {
                textureIn = in.nextString();
            }
            else if (name.equals("out") && next == JsonToken.STRING)
            {
                textureOut = in.nextString();
            }
            else if (name.equals("transform") && next == JsonToken.BEGIN_ARRAY)
            {
                transforms = new LinkedList<>();
                in.beginArray();

                while (in.hasNext())
                {
                    if (in.peek() == JsonToken.BEGIN_OBJECT)
                    {
                        TextureOperation operation = null;
                        String suppliedTexture = null;
                        in.beginObject();

                        while (in.hasNext())
                        {
                            next = in.peek();

                            if (next == JsonToken.NAME)
                            {
                                name = in.nextName();
                                continue;
                            }
                            else if (next == JsonToken.STRING)
                            {
                                if (name.equals("operation"))
                                {
                                    try
                                    {
                                        operation = TextureOperation.valueOf(in.nextString());
                                    }
                                    catch (Exception e)
                                    {
                                    }

                                    continue;
                                }
                                else if (name.equals("supply"))
                                {
                                    suppliedTexture = in.nextString();
                                    continue;
                                }
                            }

                            in.skipValue();
                        }

                        if (operation != null && !StringUtils.isNullOrEmpty(suppliedTexture))
                        {
                            transforms.add(Pair.of(operation, suppliedTexture));
                        }

                        in.endObject();
                        continue;
                    }

                    in.skipValue();
                }

                in.endArray();
            }
            else
            {
                in.skipValue();
            }
        }

        in.endObject();

        if (!StringUtils.isNullOrEmpty(textureIn) && !StringUtils.isNullOrEmpty(textureOut) && transforms != null && !transforms.isEmpty())
        {
            final List list = transforms;
            final String texIn = textureIn;
            final String texOut = textureOut;

            return tx -> new TexturePathModifier(tx, texIn, texOut, list); // Create only after getters in ResourceTx have been read
        }

        return null;
    }
}
