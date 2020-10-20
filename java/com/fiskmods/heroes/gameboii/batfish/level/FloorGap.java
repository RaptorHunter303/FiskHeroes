package com.fiskmods.heroes.gameboii.batfish.level;

import java.awt.Graphics2D;

import com.fiskmods.heroes.gameboii.batfish.Batfish;
import com.fiskmods.heroes.gameboii.batfish.BatfishGraphics;
import com.fiskmods.heroes.gameboii.graphics.Screen;
import com.fiskmods.heroes.gameboii.level.LevelObject;

public class FloorGap extends LevelObject
{
    private final int depthPlane;

    public final boolean isBossFloor;
    public final boolean isWood;

    public FloorGap(double x, double y, int plane, boolean wood, boolean boss)
    {
        super(x + 13, y - (boss ? 2 : 0), 26, boss ? 4 : 6);
        depthPlane = plane;
        isBossFloor = boss;
        isWood = wood;
    }

    @Override
    public void draw(Graphics2D g2d, Screen screen, int x, int y, int screenWidth, int screenHeight, int scale)
    {
        int i = depthPlane > 0 ? 0 : 1;
        screen.drawImage(g2d, BatfishGraphics.floors[isWood ? 1 : 0], x, y - (isBossFloor ? 2 : 0) * scale, width * scale, 6 * scale, 0, i * 6, 26, i * 6 + 6);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (isBossFloor && Batfish.INSTANCE.player.boundingBox.minY >= boundingBox.minY)
        {
            Batfish.INSTANCE.player.onBossFloor = true;
        }
    }

    @Override
    public void onCollideWith(LevelObject obj)
    {
        if (obj instanceof BatfishPlayer && canCollideWith(obj))
        {
            BatfishPlayer player = (BatfishPlayer) obj;
            player.motionY -= obj.boundingBox.minY - boundingBox.maxY;
            player.onBossFloor = true;
        }
    }

    @Override
    public boolean canCollideWith(LevelObject obj)
    {
        return isBossFloor && obj.boundingBox.minY >= boundingBox.minY;
    }

    @Override
    public int depthPlane()
    {
        return depthPlane;
    }
}
