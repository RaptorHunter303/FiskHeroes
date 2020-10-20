package com.fiskmods.heroes.gameboii.batfish.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.batfish.Batfish;
import com.fiskmods.heroes.gameboii.batfish.ScreenIngame;
import com.fiskmods.heroes.gameboii.level.Level;
import com.fiskmods.heroes.gameboii.level.LevelObject;

import net.minecraft.util.MathHelper;

public class BatfishSection
{
    public static final int HEIGHT = 16 * 10;

    private final List<LevelObject> objects = new ArrayList<>();
    private boolean generator;
    private boolean prepareBoss;

    public final Random random;
    public final int yCoord;

    public BatfishSection(Random rand, int index, boolean flag)
    {
        int x = Gameboii.getWidth() / ScreenIngame.SCALE / 2 - 10;
        int y = index * HEIGHT;

        yCoord = y;
        random = rand;
        generator = flag;
        prepareBoss = yCoord >= Batfish.SPACE_ALTITUDE * 9 && (!flag || rand.nextInt(1) == 0);
        objects.add(new SupportBeam(-x, y));
        objects.add(new SupportBeam(x, y));

        if (index > 0)
        {
            int w = Gameboii.getWidth() / ScreenIngame.SCALE / 2;
            int padding = 20;
            int x1 = w - 15;
            int i = 13;

            int left = -x1;
            int right = x1;
            int mid = MathHelper.getRandomIntegerInRange(rand, left + padding, right - padding);
            boolean wood = rand.nextBoolean();
            boolean hasPowerup = rand.nextInt(10) == 0;

            objects.add(new FloorObject(left, y, mid - left - i, wood, prepareBoss));
            objects.add(new FloorObject(mid + i, y, right - mid - i, wood, prepareBoss));
            objects.add(new FloorGap(mid - i, y, -1, wood, prepareBoss));
            objects.add(new FloorGap(mid - i, y, 1, wood, prepareBoss));

            if (!prepareBoss)
            {
                objects.add(new WarningTape(mid - i, y));
            }

            if (prepareBoss)
            {
                generator = false;
                objects.add(new SpodermenObject(getSafePosX(rand, left + 4, right - 4, mid, i), y, rand));
                objects.add(new SupportBeam(-x, y + HEIGHT));
                objects.add(new SupportBeam(x, y + HEIGHT));
            }
            else
            {
                float builders = rand.nextFloat() * rand.nextFloat() * 3;

                for (int j = 0; j <= builders; ++j)
                {
                    objects.add(new BuilderObject(getSafePosX(rand, left + 4, right - 4, mid, i), y, rand));
                }

                x1 = 0;
                y -= 16 + rand.nextInt(8) * 8;

                for (int j = 0; j < 3; ++j)
                {
                    for (i = -2; i <= 2; ++i)
                    {
                        hasPowerup = spawnCoin(x1 + i * 8, y - 8 * (j + Math.abs(i)), rand, hasPowerup);
                    }
                }
            }
        }
    }

    private int getSafePosX(Random rand, int min, int max, int mid, int midR)
    {
        int x = mid;

        while (x > mid - midR && x < mid + midR)
        {
            x = MathHelper.getRandomIntegerInRange(rand, min, max);
        }

        return x;
    }

    private boolean spawnCoin(int x, int y, Random rand, boolean hasPowerup)
    {
        if (hasPowerup && rand.nextInt(5) == 0)
        {
            objects.add(new PowerupObject(x, y, rand));
            return false;
        }

        objects.add(new CoinObject(x, y));
        return hasPowerup;
    }

    public void onUpdate()
    {
        BatfishPlayer p = Batfish.INSTANCE.player;
        int h = Gameboii.getHeight();
        int minY = ScreenIngame.getScreenPosY(h, p.posY, yCoord);

        if (minY >= 0)
        {
            if (generator)
            {
                new BatfishSection(random, yCoord / HEIGHT + 1, !prepareBoss).addTo((BatfishLevel) p.level);
                generator = false;
            }
        }

        if (minY > h * 1.5 && !prepareBoss)
        {
            removeFrom(p.level);
        }
    }

    public void addTo(BatfishLevel level)
    {
        objects.forEach(level::addObject);
        level.sections.add(this);
    }

    public void removeFrom(Level level)
    {
        objects.forEach(LevelObject::destroy);
        objects.clear();
    }

    public boolean isEmpty()
    {
        return objects.isEmpty();
    }
}
