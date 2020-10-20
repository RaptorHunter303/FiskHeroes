package com.fiskmods.heroes.gameboii.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.fiskmods.heroes.gameboii.engine.BoundingBox;

public abstract class Level
{
    public final List<LevelObject> objects = new ArrayList<>();
    private final List<LevelObject> newObjects = new ArrayList<>();

    public final Random rand;

    public Level(Random random)
    {
        rand = random;
    }

    public Level(long seed)
    {
        this(new Random(seed));
    }

    public Level()
    {
        this(new Random());
    }

    public Level reset()
    {
        objects.forEach(LevelObject::destroy);
        newObjects.clear();
        return this;
    }

    public void addObject(LevelObject obj)
    {
        obj.level = this;
        newObjects.add(obj);
    }

    public void onUpdate()
    {
        objects.removeIf(LevelObject::tick);
        objects.addAll(newObjects);
        newObjects.clear();
    }

    public List<BoundingBox> getCollidingBoundingBoxes(LevelObject obj, BoundingBox box)
    {
        return objects.stream().filter(t -> t != obj && t.canCollideWith(obj) && obj.canCollideWith(t)).map(t -> t.boundingBox).filter(t -> t.intersectsWith(box)).collect(Collectors.toList());
    }

    public List<LevelObject> getCollidingObjects(LevelObject obj, BoundingBox box)
    {
        return objects.stream().filter(t -> t != obj && t.boundingBox.intersectsWith(box) && t.canCollideWith(obj) && obj.canCollideWith(t)).collect(Collectors.toList());
    }

    public List<LevelObject> getIntersectingObjects(LevelObject obj, BoundingBox box)
    {
        return objects.stream().filter(t -> t != obj && t.boundingBox.intersectsWith(box)).collect(Collectors.toList());
    }
}
