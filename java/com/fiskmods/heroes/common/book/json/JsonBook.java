package com.fiskmods.heroes.common.book.json;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fiskmods.heroes.FiskHeroes;

public class JsonBook
{
    public List<JsonPage> pages = new ArrayList<>();
    public List<String> chapters = new ArrayList<>();
    public String background;

    public List<File> getChapters(File bookFile)
    {
        List<File> files = new ArrayList<>();

        for (String path : chapters)
        {
            try
            {
                File chapterFile = new File(bookFile.getParentFile(), path);

                if (chapterFile != null)
                {
                    files.add(chapterFile);
                }
            }
            catch (Exception e)
            {
                FiskHeroes.LOGGER.warn("{} doesn't appear to have a parent directory. This shouldn't be possible!", bookFile.getName());
                e.printStackTrace();
            }
        }

        return files;
    }
}
