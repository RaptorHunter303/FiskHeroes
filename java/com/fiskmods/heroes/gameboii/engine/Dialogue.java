package com.fiskmods.heroes.gameboii.engine;

import java.util.function.Consumer;

import com.fiskmods.heroes.gameboii.graphics.AbstractFormattedText;
import com.fiskmods.heroes.gameboii.graphics.ScreenDialogue;

public class Dialogue
{
    public final Dialogue prev;
    public final DialogueSpeaker speaker;
    public final AbstractFormattedText[] text;

    private Dialogue next;
    private Consumer<ScreenDialogue> action;

    public Dialogue(DialogueSpeaker speaker, Dialogue prev, AbstractFormattedText... text)
    {
        this.speaker = speaker;
        this.prev = prev;
        this.text = text;

        if (prev != null)
        {
            prev.next = this;
        }
    }

    public Dialogue next()
    {
        return next;
    }

    public Dialogue setAction(Consumer<ScreenDialogue> c)
    {
        action = c;
        return this;
    }

    public void executeAction(ScreenDialogue screen)
    {
        if (action != null)
        {
            action.accept(screen);
        }
    }
}
