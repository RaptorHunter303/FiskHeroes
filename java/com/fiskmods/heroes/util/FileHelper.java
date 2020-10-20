package com.fiskmods.heroes.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.util.StringUtils;

public class FileHelper
{
    public static URLConnection createConnection(String url) throws MalformedURLException, IOException
    {
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        return connection;
    }

    public static boolean isURL(String path)
    {
        return path.startsWith("http://") || path.startsWith("https://") || path.startsWith("www.");
    }

    public static String flatten(List<String> list)
    {
        String s = "";

        for (String s1 : list)
        {
            s += s1 + "\n";
        }

        return s;
    }

    public static String compactify(BufferedReader reader) throws IOException
    {
        StringBuilder out = new StringBuilder();
        String s;

        while ((s = reader.readLine()) != null)
        {
            s = stripComments(s).trim();

            if (!s.isEmpty())
            {
                out.append(s);
            }
        }

        return out.toString();
    }

    public static String stripComments(String code)
    {
        if (code.contains("//"))
        {
            boolean bal1 = false;
            boolean bal2 = false;

            l: for (int i = 0; i < code.length(); ++i)
            {
                switch (code.charAt(i))
                {
                case '"':
                    bal1 = bal2 ? bal1 : !bal1;
                    break;
                case '\'':
                    bal2 = bal1 ? bal2 : !bal2;
                    break;
                case '/':
                    if (!bal1 && !bal2 && i + 1 < code.length() && code.charAt(i + 1) == '/')
                    {
                        code = code.substring(0, i);
                        break l;
                    }
                    break;
                default:
                    break;
                }
            }
        }

        return code;
    }

    public static byte[] compress(String s, Charset charset) throws IOException
    {
        if (StringUtils.isNullOrEmpty(s))
        {
            return new byte[0];
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);

        gzip.write(s.getBytes(charset));
        gzip.close();

        return out.toByteArray();
    }

    public static String decompress(byte[] bytes, Charset charset) throws IOException
    {
        GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bytes));
        BufferedReader reader = new BufferedReader(new InputStreamReader(gzip, charset));
        StringBuilder out = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null)
        {
            out.append(line);
        }

        return out.toString();
    }
}
