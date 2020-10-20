package com.fiskmods.heroes.gameboii.batfish;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.gameboii.batfish.level.BatfishPlayer.Skin;
import com.fiskmods.heroes.gameboii.batfish.level.PowerupObject;
import com.fiskmods.heroes.gameboii.graphics.IResourceListener;
import com.fiskmods.heroes.gameboii.graphics.Resource;
import com.fiskmods.heroes.gameboii.graphics.ResourceLoader;

import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public enum BatfishGraphics implements IResourceListener
{
    INSTANCE;

    public static Resource logo;
    public static Resource game_over;

    public static Resource buttons;
    public static Resource stone;
    public static Resource coin;
    public static Resource console_buttons;
    public static Resource support_beam;
    public static Resource builder;
    public static Resource powerups;
    public static Resource robo_spodermen;
    public static Resource spodermen_mask;
    public static Resource old_spice;
    public static Resource warning_tape;

    public static Resource[] player;
    public static Resource[] floors;

    public static int[] sky_gradient;

    @Override
    public void reload(ResourceLoader loader)
    {
        logo = loader.loadGIF("textures/batfish/logo.png", 109, 21, 2);
        game_over = loader.load("textures/batfish/game_over.png", 113, 20);

        buttons = loader.load("textures/batfish/button.png", 200, 60);
        stone = loader.load("textures/batfish/stone.png", 16, 16);
        coin = loader.loadGIF("textures/batfish/coin.png", 8, 8, 4);
        console_buttons = loader.load("textures/batfish/console_buttons.png", 52, 52);
        support_beam = loader.load("textures/batfish/support_beam.png", 10, 16);
        builder = loader.load("textures/batfish/builder.png", 32, 32);
        powerups = loader.load("textures/batfish/powerups.png", 8, 8 * PowerupObject.Type.values().length);
        robo_spodermen = loader.load("textures/batfish/player/robo_spodermen.png", 4 * 20, 2 * 20);
        spodermen_mask = loader.load("textures/batfish/spodermen_mask.png", 3, 4);
        old_spice = loader.load("textures/batfish/old_spice.png", 18, 28);
        warning_tape = loader.load("textures/batfish/warning_tape.png", 26, 16);

        floors = new Resource[] {loader.load("textures/batfish/floor.png", 26, 18), loader.load("textures/batfish/floor_wood.png", 26, 18)};
        player = new Resource[Skin.values().length];

        for (Skin skin : Skin.values())
        {
            player[skin.ordinal()] = skin.resource.apply(loader, "textures/batfish/player/" + skin.name().toLowerCase(Locale.ROOT) + ".png");
        }

        try
        {
            sky_gradient = null;
            IResource resource = loader.getResource(new ResourceLocation(FiskHeroes.MODID, "textures/batfish/sky_gradient.png"));

            if (resource != null)
            {
                BufferedImage image = ImageIO.read(resource.getInputStream());
                sky_gradient = new int[image.getWidth()];

                for (int i = 0; i < sky_gradient.length; ++i)
                {
                    sky_gradient[i] = image.getRGB(i, 0);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (sky_gradient == null)
        {
            sky_gradient = new int[] {0x7AADFF};
        }
    }
}
