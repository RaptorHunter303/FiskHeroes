package com.fiskmods.heroes.gameboii.batfish.level;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import com.fiskmods.heroes.common.network.MessageBatfish;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.IGameboiiSaveObject;
import com.fiskmods.heroes.gameboii.batfish.Batfish;
import com.fiskmods.heroes.gameboii.batfish.BatfishDialogue;
import com.fiskmods.heroes.gameboii.batfish.BatfishGraphics;
import com.fiskmods.heroes.gameboii.batfish.ScreenIngame;
import com.fiskmods.heroes.gameboii.batfish.level.PowerupObject.Powerup;
import com.fiskmods.heroes.gameboii.batfish.level.PowerupObject.Type;
import com.fiskmods.heroes.gameboii.engine.Dialogue;
import com.fiskmods.heroes.gameboii.graphics.Resource;
import com.fiskmods.heroes.gameboii.graphics.ResourceLoader;
import com.fiskmods.heroes.gameboii.graphics.Screen;
import com.fiskmods.heroes.gameboii.graphics.ScreenDialogue;
import com.fiskmods.heroes.gameboii.level.LivingLevelObject;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class BatfishPlayer extends LivingLevelObject implements IGameboiiSaveObject
{
    public boolean[] skinAvailable = new boolean[Skin.values().length];
    private boolean[] skinUnlocked = new boolean[Skin.values().length];
    private Skin skin;

    private List<Powerup> powerups = new ArrayList<>();

    public int ticksPlayed;
    public int totalCoins;
    public int currentCoins;
    public double highScore;

    public boolean onBossFloor;
    public boolean inDialogue;
    public int dialogueCooldown;

    public boolean bladeTutorial;
    public boolean bombTutorial;
    public boolean worldTutorial;

    public boolean gameBeaten;

    public BatfishPlayer()
    {
        super(0, 0, 0, 0, null);
        unlock(Skin.BATFISH);
        setSkin(Skin.BATFISH);

        for (int i = 0; i < skinAvailable.length; ++i)
        {
            skinAvailable[i] = i != Skin.SPODERMEN.ordinal();
        }
    }
    
    private void readSkins(ByteBuffer buf, int length)
    {
        for (int i = 0; i < length; ++i)
        {
            skinUnlocked[i] = buf.get() > 0;
        }

        for (int i = 0; i < length; ++i)
        {
            skinAvailable[i] = buf.get() > 0;
        }
    }

    @Override
    public void read(ByteBuffer buf, int protocol)
    {
        totalCoins = buf.getInt();
        Skin skin = Skin.values()[Math.min(Math.max(buf.get(), 0), Skin.values().length - 1)];

        if (protocol == 0)
        {
            readSkins(buf, 9);
        }
        else
        {
            readSkins(buf, skinUnlocked.length);
        }

        setSkin(skin);
        highScore = buf.getFloat();
        bladeTutorial = buf.get() > 0;
        bombTutorial = buf.get() > 0;
        worldTutorial = buf.get() > 0;

        gameBeaten = buf.get() > 0;
    }

    @Override
    public void write(ByteBuffer buf)
    {
        buf.putInt(totalCoins);
        buf.put((byte) skin.ordinal());

        for (boolean b : skinUnlocked)
        {
            buf.put((byte) (b ? 1 : 0));
        }

        for (boolean b : skinAvailable)
        {
            buf.put((byte) (b ? 1 : 0));
        }

        buf.putFloat((float) highScore);
        buf.put((byte) (bladeTutorial ? 1 : 0));
        buf.put((byte) (bombTutorial ? 1 : 0));
        buf.put((byte) (worldTutorial ? 1 : 0));

        buf.put((byte) (gameBeaten ? 1 : 0));
    }

    @Override
    public void draw(Graphics2D g2d, Screen screen, int x, int y, int screenWidth, int screenHeight, int scale)
    {
        int frameX = onBossFloor ? walkDelta > 0.01 ? 2 + (int) walkAmount % 2 : ticksExisted / 8 % 2 : 0;

        if (!onBossFloor)
        {
            int max = 4;

            for (int i = 0; i < max; ++i)
            {
                float f = (float) i / max;
                int x1 = (int) Math.round(motionX * 2 * f * scale);
                int y1 = (int) Math.round(motionY * 2 * f * scale);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f * 0.3F));
                drawBody(g2d, screen, x, y, scale, skin.getResource(), frameX, 0);
            }

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        }

        drawBody(g2d, screen, x, y, scale, skin.getResource(), frameX, 0);
    }

    @Override
    public void onLivingUpdate()
    {
        if (level != null)
        {
            if (onBossFloor)
            {
                skinAvailable[Skin.SPODERMEN.ordinal()] = true;
            }

            double boost = 1 + ticksPlayed / Batfish.DIFFICULTY_SPEED;
            double speed = 1 + (boost - 1) / 2;

            if (Batfish.INSTANCE.currentScreen instanceof ScreenIngame)
            {
                if (onBossFloor)
                {
                    if (!powerups.isEmpty())
                    {
                        powerups.clear();
                    }

                    if (onGround && Gameboii.keyUpPressed())
                    {
                        motionY += 10;
                    }

                    speed = 1;
                }
                else
                {
                    if (Batfish.INSTANCE.worldPowerup == 0)
                    {
                        motionY += 0.25 * boost;
                    }
                    else
                    {
                        motionY *= 0.8F;
                    }

                    if (Gameboii.keyUpPressed())
                    {
                        motionY += 2;
                    }
                }

                if (Gameboii.keyLeftPressed())
                {
                    motionX -= speed;
                }

                if (Gameboii.keyRightPressed())
                {
                    motionX += speed;
                }

                motionX *= Math.max(1 - speed * 0.05, 0.8);
            }

            boolean flag = hasPowerup(Type.BOOST) || hasPowerup(Type.SUPERBOOST);
            powerups.removeIf(t -> t.tick(this));
            ++ticksPlayed;

            if (flag && !hasPowerup(Type.BOOST) && !hasPowerup(Type.SUPERBOOST))
            {
                motionX = 0;
                motionY = 0.25 * boost;
            }

            if (dialogueCooldown > 0)
            {
                --dialogueCooldown;
            }
            else
            {
                inDialogue = false;
            }
        }
    }

    @Override
    public boolean hasGravity()
    {
        return onBossFloor;
    }

    public void reset()
    {
        totalCoins += currentCoins;
        currentCoins = 0;
        ticksPlayed = 0;
        powerups.clear();

        if (posY > highScore)
        {
            highScore = posY;
        }

        facing = false;
        walkAmount = 0;
        walkDelta = 0;

        dialogueCooldown = 0;
        inDialogue = false;
        onBossFloor = false;

        motionX = 0;
        motionY = 0;
        setPosition(0, 0);
        Batfish.INSTANCE.worldPowerup = 0;
        Gameboii.save();
    }

    public void setSkin(Skin newSkin)
    {
        double d = newSkin.height - height;
        setSize(newSkin.width, newSkin.height);
        setPosition(posX, posY + d);
        skin = newSkin;

        if (Batfish.INSTANCE.player != null)
        {
            Gameboii.save();
        }
    }

    public Skin getSkin()
    {
        return skin;
    }

    public boolean isSkinAvailable(Skin skin)
    {
        return skinAvailable[skin.ordinal()];
    }

    public boolean hasSkin(Skin skin)
    {
        return skinUnlocked[skin.ordinal()];
    }

    public boolean unlock(Skin skin)
    {
        if (!hasSkin(skin) && totalCoins >= skin.price)
        {
            skinUnlocked[skin.ordinal()] = true;
            totalCoins -= skin.price;

            if (Batfish.INSTANCE.player != null)
            {
                Gameboii.save();

                if (skin == Skin.SPODERMEN)
                {
                    SHNetworkManager.wrapper.sendToServer(new MessageBatfish(1));
                }
            }

            return true;
        }

        return false;
    }

    public void pickup(PowerupObject.Type type)
    {
        if (!bladeTutorial && type == PowerupObject.Type.BLADE)
        {
            Batfish.INSTANCE.player.startDialogue(BatfishDialogue.TUTORIAL_BLADE, true);
            bladeTutorial = true;
        }
        else if (!bombTutorial && type == PowerupObject.Type.BOMB)
        {
            Batfish.INSTANCE.player.startDialogue(BatfishDialogue.TUTORIAL_BOMB, true);
            bombTutorial = true;
        }
        else if (!worldTutorial && type == PowerupObject.Type.WORLD)
        {
            Batfish.INSTANCE.player.startDialogue(BatfishDialogue.TUTORIAL_WORLD, true);
            worldTutorial = true;
        }

        if (type.duration > 0)
        {
            Powerup powerup = getPowerup(type);

            if (powerup != null)
            {
                powerup.time += type.duration;
                return;
            }

            powerups.add(new Powerup(type));
        }
        else
        {
            type.consumer.accept(this);
        }

        if (type == PowerupObject.Type.WORLD)
        {
            Screen screen = Gameboii.getScreen();

            if (screen instanceof ScreenIngame)
            {
                screen.onOpenScreen();
            }
        }
    }

    public boolean hasPowerup(PowerupObject.Type type)
    {
        return Iterables.any(powerups, t -> t.type == type);
    }

    public Powerup getPowerup(PowerupObject.Type type)
    {
        return Iterables.find(powerups, t -> t.type == type, null);
    }

    public boolean isInvulnerable()
    {
        return hasPowerup(PowerupObject.Type.BOOST) || hasPowerup(PowerupObject.Type.SUPERBOOST);
    }

    public void startDialogue(Dialogue dialogue, boolean pause)
    {
        if (!inDialogue)
        {
            Gameboii.displayScreen(new ScreenDialogue(dialogue, pause));
            inDialogue = true;
        }

        dialogueCooldown = 2;
    }

    public enum Skin
    {
        BATFISH("Batfish", 8, 18, 0),
        FISK("FiskFille", 8, 17, 100),
        HAWK("Hawk-dude", 10, 16, 500),
        BUILDER("Panicked Builder", 8, 16, 1000, (l, s) -> l.loadGIF(s, 4 * 20, 20, 2)),
        KARLSSON("Karlsson", 6, 12, 1000),
        OBAMA("Barack Obama", 6, 14, 5000),
        VADER("Darth Vader", 8, 14, 7500),
        JESUS("Jesus Christ", 6, 14, 10000),
        SPODERMEN("Spodermen", 8, 15, 100000),
        MARC("Marctron", 8, 18, 750),
        ARNOLD("Arnold Schwarzenegger", 8, 17, 7500),
        PUTIN("Wide Putin", 8, 12, 5000);
        
        public static Skin[] SKINS;
        public int ordinal;

        public final String name;
        public final int price;

        public final int width;
        public final int height;

        public final BiFunction<ResourceLoader, String, Resource> resource;

        Skin(String name, int width, int height, int price, BiFunction<ResourceLoader, String, Resource> resource)
        {
            this.name = name;
            this.price = price;
            this.width = width;
            this.height = height;
            this.resource = resource;
        }

        Skin(String name, int width, int height, int price)
        {
            this(name, width, height, price, (l, s) -> l.load(s, 4 * 20, 20));
        }

        public Resource getResource()
        {
            return BatfishGraphics.player[ordinal()];
        }
        
        static
        {
            List<Skin> list = Lists.newArrayList(values());
            list.sort(Comparator.comparing(t -> t.price));
            
            SKINS = list.toArray(new Skin[0]);
            
            for (int i = 0; i < SKINS.length; ++i)
            {
                SKINS[i].ordinal = i;
            }
        }
    }
}
