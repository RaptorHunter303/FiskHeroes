package com.fiskmods.heroes.gameboii;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.TaskTimer;
import com.fiskmods.heroes.gameboii.engine.GameboiiSoundHandler;
import com.fiskmods.heroes.gameboii.graphics.GameboiiFont;
import com.fiskmods.heroes.gameboii.graphics.IResourceListener;
import com.fiskmods.heroes.gameboii.graphics.ResourceLoader;
import com.fiskmods.heroes.gameboii.graphics.Screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public abstract class Abstract2DGame implements IGameboiiGame, IGameboiiSaveObject, IResourceManagerReloadListener
{
    protected final Minecraft mc = Minecraft.getMinecraft();

    protected BufferedImage canvas;
    protected DynamicTexture canvasTexture;
    protected ResourceLocation canvasLocation;
    protected Graphics2D graphics;

    public GameboiiFont fontRenderer;
    public IResourceListener resourceListener;

    public final GameboiiSoundHandler sounds = new GameboiiSoundHandler();
    public final int saveAllocation;
    public final int saveVersion;

    public Screen currentScreen;

    public Abstract2DGame(IResourceListener resources, int allocation, int version)
    {
        resourceListener = resources;
        saveAllocation = allocation;
        saveVersion = version;
    }

    @Override
    public void register()
    {
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(this);
    }

    @Override
    public void onResourceManagerReload(IResourceManager manager)
    {
        ResourceLoader.INSTANCE.reload(manager, resourceListener);
    }

    @Override
    public void init(int width, int height)
    {
        TaskTimer.start("Bootup");
        TaskTimer.start("Pre-init");
        preInit(width, height);
        TaskTimer.stop("Pre-init");
        TaskTimer.start("Init screen");

        if (canvas == null || canvas.getWidth() != width || canvas.getHeight() != height)
        {
            canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            graphics = (Graphics2D) canvas.getGraphics();

            fontRenderer = new GameboiiFont(graphics);
            canvasTexture = new DynamicTexture(canvas);
            canvasLocation = mc.getTextureManager().getDynamicTextureLocation("gameboii", canvasTexture);

            if (currentScreen != null)
            {
                currentScreen.onOpenScreen();
            }
        }

        TaskTimer.stop("Init screen");
        TaskTimer.start("Post-init");
        TaskTimer.start("Post-init screen");

        if (currentScreen == null)
        {
            displayScreen(null);
        }

        TaskTimer.stop("Post-init screen");
        postInit(width, height);
        TaskTimer.stop("Post-init");
        TaskTimer.start("Calibrate screen");
        draw(1);
        TaskTimer.stop("Calibrate screen");
        TaskTimer.stop("Bootup");
    }

    public abstract void preInit(int width, int height);

    public abstract void postInit(int width, int height);

    @Override
    public void readSaveData(byte[] data)
    {
        ByteBuffer buf = ByteBuffer.wrap(data);
        int protocol = buf.get() & 0xFF;

        sounds.read(buf, protocol);
        read(buf, protocol);
    }

    @Override
    public byte[] writeSaveData()
    {
        ByteBuffer buf = ByteBuffer.allocate(saveAllocation);
        buf.put((byte) saveVersion);

        sounds.write(buf);
        write(buf);

        byte[] data = new byte[buf.position()];
        ((ByteBuffer) buf.rewind()).get(data);
        return data;
    }

    @Override
    public void keyTyped(char character, int key)
    {
        if (currentScreen != null)
        {
            currentScreen.keyTyped(character, key);
        }
    }

    @Override
    public void draw(float partialTicks)
    {
        if (canvasLocation != null && canvas != null)
        {
            Tessellator tessellator = Tessellator.instance;
            float h = (float) canvas.getHeight() / canvas.getWidth();

            if (canvasTexture != null)
            {
                if (currentScreen != null)
                {
                    try
                    {
                        currentScreen.draw(graphics);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                canvas.getRGB(0, 0, canvas.getWidth(), canvas.getHeight(), canvasTexture.getTextureData(), 0, canvas.getWidth());
                canvasTexture.updateDynamicTexture();
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(canvasLocation);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(0, h, 0, 0, 1);
            tessellator.addVertexWithUV(1, h, 0, 1, 1);
            tessellator.addVertexWithUV(1, 0, 0, 1, 0);
            tessellator.addVertexWithUV(0, 0, 0, 0, 0);
            tessellator.draw();
        }
    }

    @Override
    public void tick()
    {
        if (currentScreen != null)
        {
            currentScreen.update();
        }
    }

    @Override
    public void quit()
    {
        canvas = null;
        canvasTexture = null;
        canvasLocation = null;
        graphics = null;

        fontRenderer = null;
        currentScreen = null;
    }

    @Override
    public int getWidth()
    {
        return canvas != null ? canvas.getWidth() : 0;
    }

    @Override
    public int getHeight()
    {
        return canvas != null ? canvas.getHeight() : 0;
    }

    @Override
    public Screen getScreen()
    {
        return currentScreen;
    }

    @Override
    public void displayScreen(Screen screen)
    {
        if (screen == null)
        {
            screen = displayMenuScreen();
        }

        currentScreen = screen;
        screen.onOpenScreen();
    }

    public abstract Screen displayMenuScreen();

    @Override
    public GameboiiSoundHandler getSoundHandler()
    {
        return sounds;
    }
}
