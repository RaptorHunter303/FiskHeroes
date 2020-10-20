package com.fiskmods.heroes.gameboii.engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fiskmods.heroes.gameboii.graphics.AbstractFormattedText;
import com.fiskmods.heroes.gameboii.graphics.DynamicFormattedText;
import com.fiskmods.heroes.gameboii.graphics.FormattedText;

public class DialogueBuilder
{
    private static final Pattern PATTERN = Pattern.compile("<h>(.+?)<\\/h>");

    private final Color highlight;

    public DialogueBuilder(Color color)
    {
        highlight = color;
    }

    private AbstractFormattedText format(String s, Color c, Supplier<Object[]> args)
    {
        if (args != null)
        {
            return new DynamicFormattedText(() -> String.format(s, args.get()), c);
        }

        return new FormattedText(s, c);
    }

    public Dialogue build(DialogueSpeaker speaker, Dialogue prev, String in, Supplier<Object[]> args)
    {
        List<AbstractFormattedText> list = new ArrayList<>();
        String[] astring = in.split("\\\n");

        for (String line : astring)
        {
            Matcher m = PATTERN.matcher(line);
            AbstractFormattedText text = null;
            int lastEnd = 0;

            while (m.find())
            {
                String s = line.substring(lastEnd, m.start());

                if (text == null)
                {
                    text = format(s, null, args);
                }
                else
                {
                    text.add(format(s, null, args));
                }

                text.add(format(m.group(1), highlight, args));
                lastEnd = m.end();
            }

            String s = line.substring(lastEnd);

            if (!s.isEmpty())
            {
                if (text == null)
                {
                    text = format(s, null, args);
                }
                else
                {
                    text.add(format(s, null, args));
                }
            }

            if (text != null)
            {
                list.add(text);
            }
        }

        return new Dialogue(speaker, prev, list.toArray(new AbstractFormattedText[0]));
    }

    public Dialogue build(DialogueSpeaker speaker, Dialogue prev, String in)
    {
        return build(speaker, prev, in, null);
    }
}
