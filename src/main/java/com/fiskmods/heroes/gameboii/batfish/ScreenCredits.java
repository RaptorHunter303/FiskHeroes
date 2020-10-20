package com.fiskmods.heroes.gameboii.batfish;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.fiskmods.heroes.common.network.MessageBatfish;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.graphics.GameboiiFont;
import com.fiskmods.heroes.gameboii.graphics.Screen;

public class ScreenCredits extends Screen
{
    private final Map<String, String[]> credits = new LinkedHashMap<>();
    private final Screen prevScreen;
    private int ticks;

    public ScreenCredits(Screen screen)
    {
        Gameboii.playSound(BatfishSounds.WHISTLE, 1, 1);
        prevScreen = screen;

        credits.put("Coding", new String[] {"FiskFille"});
        credits.put("Story", new String[] {"FiskFille", "Marctron"});
        credits.put("Texturing", new String[] {"FiskFille", "Marctron"});
        credits.put("Music", new String[] {"Kevin MacLeod"});
        credits.put("Storyboarding", new String[] {"FiskFille", "Paint.NET"});
        credits.put("Sound design", new String[] {"Not in my house"});
        credits.put("Costume design", new String[] {"Joe Mama"});
        credits.put("Set design", new String[] {"Bob the Builder"});
        credits.put("Camera operator", new String[] {"Long-Neck Johnsson"});
        credits.put("Good use of time by", new String[] {"FiskFille"});

        Set<String> cast = new HashSet<>();
        Random rand = new Random();

        while (cast.size() < 20)
        {
            cast.add("Construction worker #" + (1 + rand.nextInt(100)));
        }

        credits.put("Cast", cast.toArray(new String[0]));
        credits.put("These aren't even", new String[] {"Credits A. Nymore"});
        credits.put("Wasting your time", new String[] {"FiskFille", "Thi S. Game"});
        credits.put("Based on characters by", new String[] {"FiskFille", "Marvel Comics", "Lucasfilm Ltd.", "Astrid Lindgren", "God, I guess?"});
    }

    @Override
    public void update()
    {
        if (Gameboii.keyUpPressed())
        {
            ticks += 10;
        }
        else
        {
            ++ticks;
        }

        if (ticks > 2500)
        {
            Gameboii.displayScreen(new ScreenMainMenu());
            Batfish.INSTANCE.titleThemeTicks = 0;
        }
        else if (ticks > 50 && Batfish.INSTANCE.player.ticksPlayed > 0)
        {
            Batfish.INSTANCE.player.gameBeaten = true;
            Batfish.INSTANCE.player.reset();
            SHNetworkManager.wrapper.sendToServer(new MessageBatfish(0));
        }
    }

    @Override
    public void draw(Graphics2D g2d)
    {
        if (prevScreen != null)
        {
            prevScreen.draw(g2d);
        }

        float w = Math.min(Math.max(ticks - 10, 0) / 40F, 1);
        int spacing = 10;
        int y = height + 100 - ticks;

        g2d.setColor(new Color(0, 0, 0, w));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.WHITE);
        g2d.setFont(GameboiiFont.GAME_OVER);
        String s = "Batfish";

        g2d.drawString(s, width / 2 - fontRenderer.getStringWidth(s) / 2, y);
        g2d.setFont(GameboiiFont.BUTTON_TEXT);
        y += 20;

        for (Map.Entry<String, String[]> e : credits.entrySet())
        {
            y += 40;
            g2d.drawString(e.getKey(), width / 2 - fontRenderer.getStringWidth(e.getKey()) - spacing, y);

            for (String s1 : e.getValue())
            {
                g2d.drawString(s1, width / 2 + spacing, y);
                y += 30;
            }
        }

//        g2d.drawString("" + ticks, 20, 40);
        w = Math.min(Math.max(ticks - 2300, 0) / 60F, 1);
        w -= Math.min(Math.max(ticks - 2400, 0) / 60F, 1);

        s = "Batfish will return";
        g2d.setFont(GameboiiFont.GAME_OVER);
        g2d.setColor(new Color(1, 1, 1, w));
        g2d.drawString(s, width / 2 - fontRenderer.getStringWidth(s) / 2, height / 2);
    }
}
