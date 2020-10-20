package com.fiskmods.heroes.gameboii.batfish.level;

import java.awt.Graphics2D;

import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.batfish.BatfishGraphics;
import com.fiskmods.heroes.gameboii.batfish.BatfishSounds;
import com.fiskmods.heroes.gameboii.batfish.ScreenGameOver;
import com.fiskmods.heroes.gameboii.batfish.level.PowerupObject.Type;
import com.fiskmods.heroes.gameboii.graphics.Screen;
import com.fiskmods.heroes.gameboii.level.LevelObject;

public class FloorObject extends LevelObject
{
    public final boolean isBossFloor;
    public final boolean isWood;

    public FloorObject(double x, double y, int width, boolean wood, boolean boss)
    {
        super(x + width / 2, y, width, 6);
        isBossFloor = boss;
        isWood = wood;
    }

    @Override
    public void draw(Graphics2D g2d, Screen screen, int x, int y, int screenWidth, int screenHeight, int scale)
    {
        for (int i = 0; i < width; i += 26)
        {
            int w = Math.min(26, width - i);
            screen.drawImage(g2d, BatfishGraphics.floors[isWood ? 1 : 0], x + i * scale, y, w * scale, 6 * scale, 0, 12, w, 18);
        }
    }

    @Override
    public void onCollideWith(LevelObject obj)
    {
        if (obj instanceof BatfishPlayer && obj.boundingBox.maxY <= boundingBox.minY)
        {
            BatfishPlayer player = (BatfishPlayer) obj;

            if (!player.isInvulnerable())
            {
                boolean blade = player.hasPowerup(Type.BLADE);
                boolean bomb = player.hasPowerup(Type.BOMB);

                if (!isBossFloor && (blade && isWood || bomb))
                {
                    if (blade && isWood)
                    {
                        Gameboii.playSound(BatfishSounds.WOODBREAK, 1, 1);
                        --player.getPowerup(Type.BLADE).time;
                    }
                    else
                    {
                        Gameboii.playSound(BatfishSounds.EXPLODE, 1, 1);
                        --player.getPowerup(Type.BOMB).time;
                    }

                    destroy();
                    double left = posX - width / 2;
                    double right = posX + width / 2;
                    double mid = obj.posX;
                    int i = 13;

                    level.addObject(new FloorObject(left, posY, (int) Math.ceil(mid - left - i), isWood, isBossFloor));
                    level.addObject(new FloorObject(mid + i, posY, (int) Math.ceil(right - mid - i), isWood, isBossFloor));
                    level.addObject(new FloorGap(mid - i, posY, -1, isWood, isBossFloor));
                    level.addObject(new FloorGap(mid - i, posY, 1, isWood, isBossFloor));
                }
                else
                {
                    Gameboii.displayScreen(new ScreenGameOver(player.posY, player.currentCoins));
                    player.reset();
                }
            }
        }
    }

    @Override
    public boolean canCollideWith(LevelObject obj)
    {
        return true;
    }

    @Override
    public int depthPlane()
    {
        return 1;
    }
}
