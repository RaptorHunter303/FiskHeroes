package com.fiskmods.heroes.gameboii.batfish.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fiskmods.heroes.gameboii.batfish.Batfish;
import com.fiskmods.heroes.gameboii.level.Level;

public class BatfishLevel extends Level
{
    public final List<BatfishSection> sections = new ArrayList<>();

    public BatfishLevel(Random random)
    {
        super(random);
    }

    public BatfishLevel(long seed)
    {
        super(seed);
    }

    public BatfishLevel()
    {
        super();
    }

    @Override
    public BatfishLevel reset()
    {
        super.reset();
        sections.forEach(t -> t.removeFrom(this));

        new BatfishSection(rand, 0, true).addTo(this);
        new BatfishSection(rand, -1, false).addTo(this);
        addObject(Batfish.INSTANCE.player);

        return this;
    }

    @Override
    public void onUpdate()
    {
        for (BatfishSection section : new ArrayList<>(sections))
        {
            section.onUpdate();
        }

        sections.removeIf(BatfishSection::isEmpty);
        super.onUpdate();
    }
}
