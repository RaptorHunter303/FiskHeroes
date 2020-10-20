package com.fiskmods.heroes.gameboii.batfish;

import java.awt.Color;
import java.awt.Graphics2D;

import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.graphics.GameboiiFont;
import com.fiskmods.heroes.gameboii.graphics.Screen;

public class ScreenLoading extends Screen
{
    private int topText;
    private int ticks;

    @Override
    public void update()
    {
        ++ticks;

        if (ticks > 150)
        {
            Gameboii.displayScreen(new ScreenMainMenu());
            Batfish.INSTANCE.titleThemeTicks = 0;
        }
        else if (ticks > 20)
        {
            if (ticks < 80)
            {
                ++topText;
            }
            else if (ticks == 90)
            {
                Gameboii.playSound(BatfishSounds.COIN, 1, 0.5F);
            }
        }
        else if (ticks == 20)
        {
            Gameboii.playSound(BatfishSounds.SCREAM, 1, 1);
        }
    }

    @Override
    public void draw(Graphics2D g2d)
    {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        if (topText > 0)
        {
            g2d.setFont(GameboiiFont.GAME_OVER);
            int x = width / 2;
            int y = height / 4;

            String s = "";
            float w = 1 - Math.min(Math.max(ticks - 80, 0) / 10F, 1);

            if (w < 255)
            {
                int i = 0;
                g2d.setColor(new Color(0, 0, 0, w));

                while (true)
                {
                    if (i >= topText || fontRenderer.getStringWidth(s + "A") > width)
                    {
                        g2d.drawString(s, x - fontRenderer.getStringWidth(s) / 2, y += 40);
                        s = "";

                        if (i >= topText)
                        {
                            break;
                        }
                    }

                    s += "A";
                    ++i;
                }
            }

            w = Math.min(Math.max(ticks - 90, 0) / 20F, 1);
            s = "STUDIOS";

            g2d.setColor(new Color(1, 0, 0, w));
            g2d.drawString(s, x - fontRenderer.getStringWidth(s) / 2, height / 2);
        }
    }
}
