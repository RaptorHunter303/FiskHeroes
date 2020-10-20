package com.fiskmods.heroes.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;

public class TextureHelper
{
    public static final ResourceLocation NULL_TEXTURE = new ResourceLocation(FiskHeroes.MODID, "textures/null.png");
    public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static final ResourceLocation MISSING_TEXTURE = new ResourceLocation("missingno");

    private static Minecraft mc = Minecraft.getMinecraft();
    private static Map<String, ResourceLocation> cachedImages = new HashMap<>();
    private static Map<String, Dimension> cachedImageDimensions = new HashMap<>();

    private static final List<String> PROFILE_LOOKUP = new ArrayList<>();
    private static final Map<String, ResourceLocation> SKIN_CACHE = new HashMap<>();
    private static final Map<String, ResourceLocation> CAPE_CACHE = new HashMap<>();

    public static void lookupProfileTextures(final String username)
    {
        if (username != null && username.length() > 0 && !PROFILE_LOOKUP.contains(username))
        {
            GameProfile profile = FiskServerUtils.lookupProfile(username);

            if (profile != null)
            {
                SkinManager skinmanager = Minecraft.getMinecraft().func_152342_ad();
                skinmanager.func_152790_a(profile, (type, location) ->
                {
                    switch (type)
                    {
                    case CAPE:
                        CAPE_CACHE.put(username, location);
                        break;
                    case SKIN:
                        SKIN_CACHE.put(username, location);
                        break;
                    }
                }, true);
                PROFILE_LOOKUP.add(username);
            }
        }
    }

    public static ResourceLocation getSkin(String username)
    {
        lookupProfileTextures(username);
        ResourceLocation location = SKIN_CACHE.get(username);

        if (location == null)
        {
            return AbstractClientPlayer.locationStevePng;
        }

        return location;
    }

    public static ResourceLocation getCape(String username)
    {
        lookupProfileTextures(username);
        return CAPE_CACHE.get(username);
    }

    public static void loadImage(String path)
    {
        if (!path.isEmpty())
        {
            try
            {
                if (!cachedImages.containsKey(path))
                {
                    BufferedImage image = null;

                    if (FileHelper.isURL(path))
                    {
                        image = ImageIO.read(FileHelper.createConnection(path).getInputStream());
                    }
                    else
                    {
                        InputStream imageResource = FiskHeroes.class.getResourceAsStream("/" + path);

                        if (imageResource != null)
                        {
                            image = ImageIO.read(imageResource);
                        }
                    }

                    if (image != null)
                    {
                        cachedImages.put(path, mc.getTextureManager().getDynamicTextureLocation("image", new DynamicTexture(image)));
                        cachedImageDimensions.put(path, new Dimension(image.getWidth(), image.getHeight()));
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void drawImage(String path, int x, int y, float zLevel, Dimension maxSize)
    {
        drawImage(path, x, y, zLevel, 1, maxSize);
    }

    public static void drawImage(String path, int x, int y, float zLevel, double scale, Dimension maxSize)
    {
        if (!cachedImages.containsKey(path))
        {
            loadImage(path);
        }

        if (cachedImages.containsKey(path))
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(cachedImages.get(path));

            Dimension dim = new Dimension(cachedImageDimensions.get(path));

            if (maxSize != null)
            {
                double scaleX = (double) dim.width / (maxSize.width < 0 ? dim.width : maxSize.width);
                double scaleY = (double) dim.height / (maxSize.height < 0 ? dim.height : maxSize.height);

                if (scaleX > 1 || scaleY > 1)
                {
                    scale = 1.0D / Math.max(scaleX, scaleY);
                }
            }

            dim.width *= scale;
            dim.height *= scale;

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(x, y + dim.height, zLevel, 0, 1);
            tessellator.addVertexWithUV(x + dim.width, y + dim.height, zLevel, 1, 1);
            tessellator.addVertexWithUV(x + dim.width, y, zLevel, 1, 0);
            tessellator.addVertexWithUV(x, y, zLevel, 0, 0);
            tessellator.draw();
        }
    }

    public static Dimension getImageSize(String path)
    {
        if (cachedImageDimensions.containsKey(path))
        {
            return cachedImageDimensions.get(path);
        }

        return new Dimension();
    }

    public static int getImageWidth(String path)
    {
        return getImageSize(path).width;
    }

    public static int getImageHeight(String path)
    {
        return getImageSize(path).height;
    }

    public static boolean isBoundTexture(ResourceLocation resourceLocation)
    {
        ITextureObject texture = mc.getTextureManager().getTexture(resourceLocation);

        return texture != null && texture.getGlTextureId() == GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
    }

    public static ThreadDownloadImageData getDownloadImage(ResourceLocation resourceLocation, String url)
    {
        return getDownloadImage(resourceLocation, url, new ImageBufferSimple());
    }

    public static ThreadDownloadImageData getDownloadImage(ResourceLocation resourceLocation, String url, IImageBuffer buffer)
    {
        ITextureObject texture = mc.getTextureManager().getTexture(resourceLocation);

        if (texture == null)
        {
            texture = new ThreadDownloadImageData(null, url, null, buffer);
            mc.getTextureManager().loadTexture(resourceLocation, texture);
        }

        return (ThreadDownloadImageData) texture;
    }

    public static ITextureObject modifyTexture(ResourceLocation in, ResourceLocation out, ResourceLocation other, TextureOperation operation)
    {
        TextureManager manager = mc.getTextureManager();
        ITextureObject otherTex = manager.getTexture(other);

        if (otherTex == null)
        {
            otherTex = new SimpleTexture(other);
            manager.loadTexture(other, otherTex);
        }

        return new ModifiedTexture(in, other, operation);
    }

    public static ITextureObject modifyTexture(ResourceLocation in, ResourceLocation other, TextureOperation operation)
    {
        return modifyTexture(in, in, other, operation);
    }

    public static class ModifiedTexture extends SimpleTexture
    {
        private final ResourceLocation otherLocation;
        private final TextureOperation textureOp;

        public ModifiedTexture(ResourceLocation texture, ResourceLocation other, TextureOperation operation)
        {
            super(texture);
            otherLocation = other;
            textureOp = operation;
        }

        @Override
        public void loadTexture(IResourceManager manager) throws IOException
        {
            deleteGlTexture();
            InputStream textureStream = null;
            InputStream otherStream = null;

            try
            {
                BufferedImage texture = ImageIO.read(textureStream = manager.getResource(textureLocation).getInputStream());
                BufferedImage other = ImageIO.read(otherStream = manager.getResource(otherLocation).getInputStream());
                int width = Math.max(texture.getWidth(), other.getWidth());
                int height = Math.max(texture.getHeight(), other.getHeight());

                texture = createCopy(texture, width, height);
                other = createCopy(other, width, height);

                int[] textureData = ((DataBufferInt) texture.getRaster().getDataBuffer()).getData();
                int[] otherData = ((DataBufferInt) other.getRaster().getDataBuffer()).getData();

                for (int x = 0; x < width; ++x)
                {
                    for (int y = 0; y < height; ++y)
                    {
                        int i = x + y * width;
                        textureData[i] = textureOp.apply(textureData[i], otherData[i]);
                    }
                }

                TextureUtil.uploadTextureImageAllocate(getGlTextureId(), texture, false, false);
            }
            finally
            {
                if (textureStream != null)
                {
                    textureStream.close();
                }

                if (otherStream != null)
                {
                    otherStream.close();
                }
            }
        }

        private BufferedImage createCopy(BufferedImage image, int width, int height)
        {
            BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = copy.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(image, 0, 0, width, height, null);
            g.dispose();

            return copy;
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ImageBufferSimple implements IImageBuffer
    {
        @Override
        public BufferedImage parseUserSkin(BufferedImage image)
        {
            return image;
        }

        @Override
        public void func_152634_a()
        {
        }
    }

    public static enum TextureOperation implements BiFunction<Integer, Integer, Integer>
    {
        XOR((src, other) -> (other >> 24 & 255) > 0 ? 0 : src),
        AND((src, other) -> src & 0xFF000000 | src & 0xFFFFFF | other & 0xFFFFFF),
        OR((src, other) -> src | other);

        private final BiFunction<Integer, Integer, Integer> function;

        private TextureOperation(BiFunction<Integer, Integer, Integer> func)
        {
            function = func;
        }

        @Override
        public Integer apply(Integer source, Integer other)
        {
            return function.apply(source, other);
        }
    }
}
