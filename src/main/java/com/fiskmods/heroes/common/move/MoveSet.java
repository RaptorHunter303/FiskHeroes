package com.fiskmods.heroes.common.move;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.fiskmods.heroes.common.JsonTypeDeserializer;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class MoveSet implements Iterable<MoveEntry>
{
    public static final GsonBuilder GSON_FACTORY = new GsonBuilder().registerTypeAdapter(MoveSet.class, new MoveSet.Adapter());

    private final ImmutableMap<Move, MoveEntry> moves;
    private final ImmutableMap<Move, Set<Double>> markers;

    private final ImmutableMap<Move, Map<Double, ImmutableMap<String, Number>>> mappings;

    public final int color;

    private MoveSet(Map<Move, MoveEntry> moves, int color)
    {
        Map<Move, Map<Double, ImmutableMap<String, Number>>> mappings = new HashMap<>();
        Map<Move, Set<Double>> markers = new HashMap<>();

        for (MoveEntry entry : moves.values())
        {
            entry.parent = this;

            for (Map.Entry<Double, Map<String, Number>> e : entry.modifiers.entrySet())
            {
                mappings.computeIfAbsent(entry.move, k -> new HashMap<>()).put(e.getKey(), ImmutableMap.copyOf(e.getValue()));
                markers.computeIfAbsent(entry.move, k -> new HashSet<>()).add(e.getKey());
            }
        }

        this.moves = ImmutableMap.copyOf(moves);
        this.markers = ImmutableMap.copyOf(markers);
        this.mappings = ImmutableMap.copyOf(mappings);
        this.color = color;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public ImmutableCollection<MoveEntry> getMoves()
    {
        return moves.values();
    }

    public MoveEntry getMove(Move move)
    {
        return moves.get(move);
    }

    public MoveEntry getMove(int index)
    {
        if (moves.isEmpty())
        {
            return null;
        }

        return Iterables.get(getMoves(), Math.abs(index % moves.size()));
    }

    public int getMoveIndex(Move move)
    {
        return moves.containsKey(move) ? Iterables.indexOf(getMoves(), move::equals) : -1;
    }

    public ImmutableSet<Double> getMarkers(Move move)
    {
        return markers.containsKey(move) ? ImmutableSet.copyOf(markers.get(move)) : ImmutableSet.of();
    }

    public ImmutableMap<String, Number> getModifiers(Move move, double focus)
    {
        if (!moves.containsKey(move))
        {
            return null;
        }

        return mappings.get(move).entrySet().stream().filter(e -> e.getKey() <= focus).sorted(Comparator.comparing(Map.Entry<Double, ?>::getKey).reversed()).map(Map.Entry::getValue).findFirst().orElse(null);
    }

    public Number getModifier(Move move, String key, double focus)
    {
        return getModifiers(move, focus).getOrDefault(key, 0);
    }

    public int size()
    {
        return moves.size();
    }

    @Override
    public Iterator<MoveEntry> iterator()
    {
        return getMoves().iterator();
    }

    @Override
    public String toString()
    {
        return String.format("MoveSet[color=%s, moves=%s]", color, moves.values());
    }

    public static class Builder
    {
        private Map<Move, MoveEntry> moves = new HashMap<>();

        public Builder add(Move move, Consumer<MoveWrapper> c, String icon)
        {
            MoveEntry container = new MoveEntry(move, icon, new HashMap<>());
            c.accept(container::put);

            moves.put(move, container);
            return this;
        }

        public MoveSet build(int color)
        {
            return new MoveSet(moves, color);
        }
    }

    public static class Adapter extends JsonTypeDeserializer<MoveSet>
    {
        @Override
        public MoveSet deserialize(JsonReader in) throws IOException
        {
            in.beginObject();
            String name = "";

            Map<Move, MoveEntry> moves = new HashMap<>();
            int color = 0xFFFFFF;

            while (in.hasNext())
            {
                JsonToken next = in.peek();

                if (next == JsonToken.NAME)
                {
                    name = in.nextName();
                }
                else if (name.equals("color") && next == JsonToken.STRING)
                {
                    color = Integer.decode(in.nextString());
                }
                else if (name.equals("moves") && next == JsonToken.BEGIN_ARRAY)
                {
                    in.beginArray();

                    while (in.hasNext())
                    {
                        if (in.peek() == JsonToken.BEGIN_OBJECT)
                        {
                            in.beginObject();
                            Move move = null;
                            Map modifiers = null;
                            String icon = null;

                            while (in.hasNext())
                            {
                                next = in.peek();

                                if (next == JsonToken.NAME)
                                {
                                    name = in.nextName();
                                }
                                else if (name.equals("id") && next == JsonToken.STRING)
                                {
                                    move = Move.getMoveFromName(in.nextString());
                                }
                                else if (name.equals("icon") && next == JsonToken.STRING)
                                {
                                    icon = in.nextString();
                                }
                                else if (name.equals("modifiers") && next == JsonToken.BEGIN_OBJECT)
                                {
                                    modifiers = new HashMap<>();
                                    in.beginObject();

                                    while (in.hasNext())
                                    {
                                        if (in.peek() == JsonToken.NAME)
                                        {
                                            double d = Double.parseDouble(in.nextName());

                                            if (in.peek() == JsonToken.BEGIN_OBJECT)
                                            {
                                                Map<String, Number> map = new HashMap<>();
                                                in.beginObject();

                                                while (in.hasNext())
                                                {
                                                    if (in.peek() == JsonToken.NAME)
                                                    {
                                                        name = in.nextName();

                                                        if (in.peek() == JsonToken.NUMBER)
                                                        {
                                                            map.put(name, in.nextDouble());
                                                            continue;
                                                        }
                                                    }

                                                    in.skipValue();
                                                }

                                                modifiers.put(d, map);
                                                in.endObject();
                                                continue;
                                            }
                                        }

                                        in.skipValue();
                                    }

                                    in.endObject();
                                }
                                else
                                {
                                    in.skipValue();
                                }
                            }

                            if (move != null && modifiers != null)
                            {
                                String path = "minecraft:textures/items/fish.png";

                                if (!StringUtils.isNullOrEmpty(icon))
                                {
                                    ResourceLocation loc = new ResourceLocation(icon);
                                    path = loc.getResourceDomain() + ":textures/icons/" + loc.getResourcePath() + ".png";
                                }

                                moves.put(move, new MoveEntry(move, path, modifiers));
                            }

                            in.endObject();
                        }
                        else
                        {
                            in.skipValue();
                        }
                    }

                    in.endArray();
                }
                else
                {
                    in.skipValue();
                }
            }

            in.endObject();

            return new MoveSet(moves, color);
        }
    }
}
