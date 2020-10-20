package com.fiskmods.heroes.gameboii.batfish.level;

import static com.fiskmods.heroes.gameboii.batfish.level.BatfishPlayer.Skin.*;

import java.awt.Graphics2D;
import java.util.Random;

import com.fiskmods.heroes.gameboii.batfish.Batfish;
import com.fiskmods.heroes.gameboii.batfish.BatfishGraphics;
import com.fiskmods.heroes.gameboii.graphics.Screen;
import com.fiskmods.heroes.gameboii.level.LevelObject;
import com.fiskmods.heroes.gameboii.level.LivingLevelObject;

public class BuilderObject extends LivingLevelObject
{
    public boolean panic;

    public BuilderObject(double x, double y, Random rand)
    {
        super(x, y, BUILDER.width, BUILDER.height, rand);
    }

    @Override
    public void draw(Graphics2D g2d, Screen screen, int x, int y, int screenWidth, int screenHeight, int scale)
    {
        int srcX = (panic ? 2 + ticksExisted / 2 % 2 : ticksExisted / 8 % 2) * width;
        int srcY = facing ? height : 0;

        screen.drawImage(g2d, BatfishGraphics.builder, x, y, width * scale, height * scale, srcX, srcY, srcX + width, srcY + height);
    }

    @Override
    public void onUpdate()
    {
        if (Batfish.INSTANCE.worldPowerup > 0)
        {
            prevPosX = posX;
            prevPosY = posY;
        }
        else
        {
            super.onUpdate();
        }
    }

    @Override
    public void onLivingUpdate()
    {
        if (level != null && Batfish.INSTANCE.player.posY > posY - 20)
        {
            panic = true;
        }

        if (panic)
        {
            if (posX == prevPosX)
            {
                facing = !facing;
            }

            motionX += 1 * (facing ? -1 : 1);
        }
    }

    @Override
    public boolean canCollideWith(LevelObject obj)
    {
        return !(obj instanceof BuilderObject || obj instanceof BatfishPlayer);
    }
}
