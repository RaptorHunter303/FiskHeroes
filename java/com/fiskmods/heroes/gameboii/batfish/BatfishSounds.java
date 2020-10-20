package com.fiskmods.heroes.gameboii.batfish;

import static com.fiskmods.heroes.gameboii.engine.GameboiiSound.Category.*;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.gameboii.engine.GameboiiSound;

public class BatfishSounds
{
    public static final GameboiiSound CLICK = new GameboiiSound(SHSounds.BATFISH_CLICK.toString(), EFFECT);
    public static final GameboiiSound COIN = new GameboiiSound(SHSounds.BATFISH_COIN.toString(), EFFECT);
    public static final GameboiiSound DEATH = new GameboiiSound(SHSounds.BATFISH_DEATH.toString(), EFFECT);
    public static final GameboiiSound EXPLODE = new GameboiiSound(SHSounds.BATFISH_EXPLODE.toString(), EFFECT);
    public static final GameboiiSound POP = new GameboiiSound(SHSounds.BATFISH_POP.toString(), EFFECT);
    public static final GameboiiSound SCREAM = new GameboiiSound(SHSounds.BATFISH_SCREAM.toString(), EFFECT);
    public static final GameboiiSound WHISTLE = new GameboiiSound(SHSounds.BATFISH_WHISTLE.toString(), EFFECT);
    public static final GameboiiSound WOODBREAK = new GameboiiSound(SHSounds.BATFISH_WOODBREAK.toString(), EFFECT);
    public static final GameboiiSound WORLD = new GameboiiSound(SHSounds.BATFISH_WORLD.toString(), EFFECT);

    public static final GameboiiSound TITLE = new GameboiiSound(SHSounds.BATFISH_TITLE.toString(), MUSIC);
}
