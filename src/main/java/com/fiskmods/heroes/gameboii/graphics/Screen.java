package com.fiskmods.heroes.gameboii.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.input.Keyboard;

import com.fiskmods.heroes.common.Pair;
import com.fiskmods.heroes.gameboii.Gameboii;
import com.fiskmods.heroes.gameboii.batfish.Batfish;
import com.fiskmods.heroes.gameboii.batfish.BatfishGraphics;
import com.fiskmods.heroes.gameboii.batfish.BatfishSounds;
import com.fiskmods.heroes.util.SHFormatHelper;

public abstract class Screen
{
    private final Map<ConsoleButton, Pair<Supplier<String>, Runnable>> consoleButtons = new LinkedHashMap<>();
    private final List<AbstractButton> buttonList = new ArrayList<>();

    private int selectedId;

    protected GameboiiFont fontRenderer;
    protected int width;
    protected int height;

    public Screen()
    {
    }

    protected void addConsoleButton(ConsoleButton button, Supplier<String> name, Runnable action)
    {
        consoleButtons.put(button, Pair.of(name, action));
    }

    protected void addConsoleButton(ConsoleButton button, String name, Runnable action)
    {
        addConsoleButton(button, () -> name, action);
    }

    public void onOpenScreen()
    {
        fontRenderer = Batfish.INSTANCE.fontRenderer;
        width = Gameboii.getWidth();
        height = Gameboii.getHeight();

        consoleButtons.clear();
        buttonList.clear();
        initScreen();
    }

    public void initScreen()
    {
    }

    public void draw(Graphics2D g2d)
    {
        buttonList.forEach(button -> button.draw(g2d));

        if (!consoleButtons.isEmpty() && Gameboii.getScreen() == this)
        {
            g2d.setColor(new Color(0, 0, 0, 0.5F));

            int x = 0;
            int y = height - 60;
            g2d.fillRect(x, y, width, 40);

            x = width - 40;
            y += 27;
            g2d.setFont(GameboiiFont.BUTTON_TEXT);

            for (Map.Entry<ConsoleButton, Pair<Supplier<String>, Runnable>> e : consoleButtons.entrySet())
            {
                String s = e.getValue().getKey().get();
                x -= fontRenderer.getStringWidth(s);
                fontRenderer.drawString(s, x, y, 0xFFFFFF);

                s = e.getKey().name();
                x -= 22;
                Rectangle rect = new Rectangle(e.getKey().ordinal() % 2 * 26, e.getKey().ordinal() / 2 * 26, 26, 26);
                drawCenteredImage(g2d, BatfishGraphics.console_buttons, x, y - 8, 26, 26, rect);
                fontRenderer.drawStringWithShadow(s, x - fontRenderer.getStringWidth(s) / 2, y, 0xFFFFFF, 0);
                x -= 45;
            }
        }
    }

    public void keyTyped(char character, int key)
    {
        if (!buttonList.isEmpty())
        {
            if (key == Keyboard.KEY_DOWN || key == Keyboard.KEY_S)
            {
                cycleButtons(1);
                return;
            }
            else if (key == Keyboard.KEY_UP || key == Keyboard.KEY_W)
            {
                cycleButtons(-1);
                return;
            }
            else if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_SPACE)
            {
                pressButton();
                return;
            }

            if (buttonList.size() > 0)
            {
                buttonList.get(selectedId).keyInput(key);
            }
        }

        for (Map.Entry<ConsoleButton, Pair<Supplier<String>, Runnable>> e : consoleButtons.entrySet())
        {
            if (key == e.getKey().keyCode)
            {
                e.getValue().getValue().run();
                Gameboii.playSound(BatfishSounds.CLICK, 1, 1);
                return;
            }
        }
    }

    protected void cycleButtons(int offset)
    {
        selectedId += offset;
        int size = buttonList.size();

        while (selectedId < 0)
        {
            selectedId += size;
        }

        while (selectedId >= size)
        {
            selectedId -= size;
        }
    }

    protected void pressButton()
    {
        if (buttonList.size() > 0)
        {
            buttonList.get(selectedId).onClicked();
        }

        Gameboii.playSound(BatfishSounds.CLICK, 1, 1);
    }

    public void update()
    {
    }

    public void drawDefaultBackground(Graphics2D g2d)
    {
        final int size = 48;

        for (int x = 0; x < width / size; ++x)
        {
            for (int y = 0; y < height / size; ++y)
            {
                drawImage(g2d, BatfishGraphics.stone, size * x, size * y, size, size);
            }
        }
    }

    public void drawCoinCount(Graphics2D g2d, int x, int y, int coins, boolean center)
    {
        g2d.setFont(GameboiiFont.DEFAULT);
        String s = SHFormatHelper.formatNumber(coins);

        if (center)
        {
            x -= (25 + 2 + fontRenderer.getStringWidth(s)) / 2;
            y -= 16;
        }

//        fontRenderer.drawStringWithShadow(s, x + 25, y + 9, 0xE89000, 0);
//        fontRenderer.drawStringWithShadow(s, x + 25 + 2, y + 9 + 2, 0xE8BB00, 0);
//        drawCenteredImage(g2d, BatfishGraphics.coin, x, y, 32, 32);

        int color = 0xF8AD20;
        int color0 = 0xFFE649;
        int color1 = 0x9D6100;

        // fontRenderer.drawStringWithShadow(s, width / 2 - 155 + 2, 160 + 2, color1, 0);
        fontRenderer.drawStringWithShadow(s, x + 25, y + 11, 0xFFCD21, 0x3F2700, 2);
        drawCenteredImage(g2d, BatfishGraphics.coin, x, y, 32, 32);
    }

    public void drawImage(Graphics2D g2d, Resource resource, int x, int y)
    {
        drawImage(g2d, resource, x, y, resource.getWidth(), resource.getHeight());
    }

    public void drawCenteredImage(Graphics2D g2d, Resource resource, int x, int y)
    {
        drawImage(g2d, resource, x - resource.getWidth() / 2, y - resource.getHeight() / 2);
    }

    public void drawImage(Graphics2D g2d, Resource resource, int x, int y, int width, int height)
    {
        g2d.drawImage(resource.get(), x, y, width, height, null);
    }

    public void drawCenteredImage(Graphics2D g2d, Resource resource, int x, int y, int width, int height)
    {
        drawImage(g2d, resource, x - width / 2, y - height / 2, width, height);
    }

    public void drawImage(Graphics2D g2d, Resource resource, int x, int y, int width, int height, int srcX1, int srcY1, int srcX2, int srcY2)
    {
        g2d.drawImage(resource.get(), x, y, width + x, height + y, srcX1, srcY1, srcX2, srcY2, null);
    }

    public void drawImage(Graphics2D g2d, Resource resource, int x, int y, int width, int height, Rectangle src)
    {
        drawImage(g2d, resource, x, y, width, height, (int) src.getMinX(), (int) src.getMinY(), (int) src.getMaxX(), (int) src.getMaxY());
    }

    public void drawCenteredImage(Graphics2D g2d, Resource resource, int x, int y, int width, int height, int srcX1, int srcY1, int srcX2, int srcY2)
    {
        drawImage(g2d, resource, x - width / 2, y - height / 2, width, height, srcX1, srcY1, srcX2, srcY2);
    }

    public void drawCenteredImage(Graphics2D g2d, Resource resource, int x, int y, int width, int height, Rectangle src)
    {
        drawCenteredImage(g2d, resource, x, y, width, height, (int) src.getMinX(), (int) src.getMinY(), (int) src.getMaxX(), (int) src.getMaxY());
    }

    public enum ConsoleButton
    {
        Z(Keyboard.KEY_Z),
        X(Keyboard.KEY_X),
        C(Keyboard.KEY_C),
        V(Keyboard.KEY_V);

        public final int keyCode;

        ConsoleButton(int key)
        {
            keyCode = key;
        }
    }

    protected abstract class AbstractButton
    {
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public final int id;

        public AbstractButton(int x, int y, int width, int height)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            id = buttonList.size();
            buttonList.add(this);
        }

        public void keyInput(int key)
        {
        }

        public void onClicked()
        {
        }

        public abstract void draw(Graphics2D g2d);
    }

    protected class Button extends AbstractButton
    {
        public final String text;
        private final Runnable runnable;

        public Button(int x, int y, int width, int height, String buttonText, Runnable onClick)
        {
            super(x, y, width, height);
            text = buttonText;
            runnable = onClick;
        }

        @Override
        public void onClicked()
        {
            runnable.run();
        }

        @Override
        public void draw(Graphics2D g2d)
        {
            int i = id == selectedId ? 1 : 0;
            drawImage(g2d, BatfishGraphics.buttons, x, y, width, height, 0, i * 20, 200, (i + 1) * 20);

            g2d.setFont(GameboiiFont.BUTTON_TEXT);
            g2d.setColor(id == selectedId ? Color.YELLOW : Color.WHITE);
            g2d.drawString(text, x + width / 2 - fontRenderer.getStringWidth(text) / 2, y + 28);
        }
    }

    protected class Slider extends AbstractButton
    {
        private final String name;
        private final Supplier<Float> getter;
        private final Consumer<Float> setter;

        public Slider(int x, int y, int width, int height, String name, Supplier<Float> get, Consumer<Float> set)
        {
            super(x, y, width, height);
            this.name = name;
            getter = get;
            setter = set;
        }

        @Override
        public void keyInput(int key)
        {
            float incr = Gameboii.keyShiftPressed() ? 0.01F : 0.1F;
            float f = getter.get();

            if (f > 0 && (key == Keyboard.KEY_LEFT || key == Keyboard.KEY_A))
            {
                setter.accept(Math.max(f - incr, 0));
            }
            else if (f < 1 && (key == Keyboard.KEY_RIGHT || key == Keyboard.KEY_D))
            {
                setter.accept(Math.min(f + incr, 1));
            }
        }

        @Override
        public void draw(Graphics2D g2d)
        {
            Color color1 = Color.DARK_GRAY, color2 = Color.LIGHT_GRAY;
            Paint paint = g2d.getPaint();
            float f = getter.get();

            if (id == selectedId)
            {
                color1 = new Color(0x0A446B);
                color2 = new Color(0x96D5FF);
            }

            int w = 10, i = id == selectedId ? 1 : 0;
            int x1 = x + 2, y1 = y + 2, x2 = x + w + (int) ((width - w * 2) * f), y2 = y + height - 2;

            drawImage(g2d, BatfishGraphics.buttons, x, y, width, height, 0, 40, 200, 60);
//            g2d.setPaint(new GradientPaint(x1, y1, color1, x2, y2, color2));
//            g2d.fillRect(x1, y1, x2 - x1, y2 - y1);
//            g2d.setPaint(paint);
            drawImage(g2d, BatfishGraphics.buttons, x2 - w, y, w, height, 0, i * 20, w / 2, (i + 1) * 20);
            drawImage(g2d, BatfishGraphics.buttons, x2, y, w, height, 200 - w / 2, i * 20, 200, (i + 1) * 20);

            String text = String.format("%s: %d%%", name, Math.round(f * 100));
            g2d.setFont(GameboiiFont.BUTTON_TEXT);
            g2d.setColor(id == selectedId ? Color.YELLOW : Color.WHITE);
            g2d.drawString(text, x + width / 2 - fontRenderer.getStringWidth(text) / 2, y + 28);
        }
    }
}
