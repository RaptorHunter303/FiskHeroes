package com.fiskmods.heroes.gameboii.graphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.util.function.Consumer;

public class FilteredLevelCanvas implements BufferedImageOp
{
    public final BufferedImage image;
    public final Graphics2D graphics;

    private final Consumer<int[]> filterFunc;

    public FilteredLevelCanvas(int width, int height, Consumer<int[]> filter)
    {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = (Graphics2D) image.getGraphics();
        filterFunc = filter;
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM)
    {
        if (destCM == null)
        {
            destCM = src.getColorModel();
        }

        return new BufferedImage(destCM, destCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), destCM.isAlphaPremultiplied(), null);
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst)
    {
        if (dst == null)
        {
            dst = createCompatibleDestImage(src, null);
        }

        int width = src.getWidth();
        int height = src.getHeight();

        int[] pixels = new int[width * height];
        getPixels(src, 0, 0, width, height, pixels);
        filterFunc.accept(pixels);
        setPixels(dst, 0, 0, width, height, pixels);

        return dst;
    }

    @Override
    public Rectangle2D getBounds2D(BufferedImage src)
    {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    @Override
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt)
    {
        return (Point2D) srcPt.clone();
    }

    @Override
    public RenderingHints getRenderingHints()
    {
        return null;
    }

    public static int[] getPixels(BufferedImage img, int x, int y, int w, int h, int[] pixels)
    {
        if (w == 0 || h == 0)
        {
            return new int[0];
        }

        if (pixels == null)
        {
            pixels = new int[w * h];
        }
        else if (pixels.length < w * h)
        {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
        }

        int imageType = img.getType();

        if (imageType == BufferedImage.TYPE_INT_ARGB || imageType == BufferedImage.TYPE_INT_RGB)
        {
            return (int[]) img.getRaster().getDataElements(x, y, w, h, pixels);
        }

        return img.getRGB(x, y, w, h, pixels, 0, w);
    }

    public static void setPixels(BufferedImage img, int x, int y, int w, int h, int[] pixels)
    {
        if (pixels == null || w == 0 || h == 0)
        {
            return;
        }
        else if (pixels.length < w * h)
        {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
        }

        int imageType = img.getType();

        if (imageType == BufferedImage.TYPE_INT_ARGB || imageType == BufferedImage.TYPE_INT_RGB)
        {
            img.getRaster().setDataElements(x, y, w, h, pixels);
        }
        else
        {
            img.setRGB(x, y, w, h, pixels, 0, w);
        }
    }
}
