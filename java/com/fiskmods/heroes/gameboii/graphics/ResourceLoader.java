package com.fiskmods.heroes.gameboii.graphics;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import com.fiskmods.heroes.FiskHeroes;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public enum ResourceLoader implements IResourceManager
{
    INSTANCE;

    private static final BufferedImage MISSING_IMAGE = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

    private IResourceManager resourceManager;

    static
    {
        int i = -16777216;
        int j = -524040;

        MISSING_IMAGE.setRGB(0, 0, i);
        MISSING_IMAGE.setRGB(1, 0, j);
        MISSING_IMAGE.setRGB(0, 1, j);
        MISSING_IMAGE.setRGB(1, 1, i);
    }

    public void reload(IResourceManager manager, IResourceListener listener)
    {
        resourceManager = manager;
        listener.reload(this);
    }

    @Override
    public Set getResourceDomains()
    {
        return resourceManager.getResourceDomains();
    }

    @Override
    public IResource getResource(ResourceLocation location) throws IOException
    {
        return resourceManager.getResource(location);
    }

    @Override
    public List getAllResources(ResourceLocation location) throws IOException
    {
        return resourceManager.getAllResources(location);
    }

    public Resource load(String path, int width, int height)
    {
        try
        {
            IResource resource = resourceManager.getResource(new ResourceLocation(FiskHeroes.MODID, path));

            if (resource != null)
            {
                BufferedImage image = ImageIO.read(resource.getInputStream());
                return new Resource(width, height, () -> image);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        AffineTransform tx = AffineTransform.getScaleInstance(width / 2.0, height / 2.0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        return new Resource(width, height, () -> op.filter(MISSING_IMAGE, null));
    }

    public Resource loadGIF(String path, int width, int height, int delay)
    {
        Resource resource = load(path, width, height);
        BufferedImage image = resource.get();

        if (image == MISSING_IMAGE)
        {
            return resource;
        }

        BufferedImage[] data = getAnimationData(image, width, height);
        return new Resource(width, height, data, data.length, delay);
    }

    public ResourceAnimated loadAnimation(String path, int width, int height)
    {
        Resource resource = load(path, width, height);
        BufferedImage image = resource.get();

        if (image == MISSING_IMAGE)
        {
            return new ResourceAnimated(width, height, new BufferedImage[] {image});
        }

        return new ResourceAnimated(width, height, getAnimationData(image, width, height));
    }

    private BufferedImage[] getAnimationData(BufferedImage image, int width, int height)
    {
        float scale = (float) image.getWidth() / width;
        BufferedImage[] data = new BufferedImage[MathHelper.floor_float(image.getHeight() / height / scale)];

        for (int i = 0; i < data.length; ++i)
        {
            data[i] = image.getSubimage(0, MathHelper.floor_float(i * height * scale), MathHelper.floor_float(image.getWidth() * scale), MathHelper.floor_float(height * scale));
        }

        return data;
    }
}
