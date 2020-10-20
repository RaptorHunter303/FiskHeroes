package com.fiskmods.heroes.gameboii.batfish.level;

import java.awt.Graphics2D;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.batfish.BatfishGraphics;
import com.fiskmods.heroes.gameboii.batfish.BatfishSounds;
import com.fiskmods.heroes.gameboii.graphics.Screen;
import com.fiskmods.heroes.gameboii.level.Level;
import com.fiskmods.heroes.gameboii.level.LevelObject;

public class PowerupObject extends LevelObject
{
    public final Type type;

    public PowerupObject(double x, double y, Random rand)
    {
        super(x, y, 8, 8);
        type = Type.values()[rand.nextInt(Type.values().length)];
    }

    @Override
    public void draw(Graphics2D g2d, Screen screen, int x, int y, int screenWidth, int screenHeight, int scale)
    {
        int srcY = type.ordinal() * height;
        screen.drawImage(g2d, BatfishGraphics.powerups, x, y, width * scale, height * scale, 0, srcY, width, srcY + height);
    }

    @Override
    public void onCollideWith(LevelObject obj)
    {
        if (obj instanceof BatfishPlayer && !isDead)
        {
            ((BatfishPlayer) obj).pickup(type);
            Gameboii.playSound(BatfishSounds.POP, 1, 1);
            destroy();
        }
    }

    @Override
    public boolean canCollideWith(LevelObject obj)
    {
        return false;
    }

    public static void doBoost(BatfishPlayer player, float speed)
    {
        Level level = player.level;

        if (level != null)
        {
            Comparator<LevelObject> c = Comparator.<LevelObject, Double> comparing(t -> t.posY - player.posY);
            player.motionY += speed;

            for (LevelObject obj : level.objects.stream().filter(t -> t instanceof FloorGap).sorted(c).collect(Collectors.toList()))
            {
                if (player.posY <= obj.boundingBox.minY)
                {
                    player.motionX *= 0.5;
                    player.motionX += (obj.posX - player.posX) / Math.max((obj.boundingBox.minY - player.posY) / player.motionY, 1);
                    break;
                }
            }
        }
    }

    public enum Type
    {
        BOOST(UseType.TIMED, 100, p -> PowerupObject.doBoost(p, 2)),
        SUPERBOOST(UseType.TIMED, 100, p -> PowerupObject.doBoost(p, 4)),
        STOP(UseType.INSTANT, 0, p ->
        {
            p.ticksPlayed = 0;
        }),
        BLADE(UseType.STACKABLE, 1, p ->
        {
        }),
        BOMB(UseType.STACKABLE, 1, p ->
        {
        }),
        WORLD(UseType.STACKABLE, 1, p ->
        {
        });

        public final UseType useType;
        public final int duration;

        public final Consumer<BatfishPlayer> consumer;

        Type(UseType useType, int duration, Consumer<BatfishPlayer> consumer)
        {
            this.useType = useType;
            this.duration = duration;
            this.consumer = consumer;
        }
    }

    public enum UseType
    {
        TIMED,
        INSTANT,
        STACKABLE;
    }

    public static class Powerup
    {
        public final Type type;
        public int time;

        public Powerup(Type type, int time)
        {
            this.type = type;
            this.time = time;
        }

        public Powerup(Type type)
        {
            this(type, type.duration);
        }

        public boolean tick(BatfishPlayer player)
        {
            type.consumer.accept(player);

            if (type.useType == UseType.STACKABLE)
            {
                return time <= 0;
            }

            return --time < 0;
        }
    }
}
