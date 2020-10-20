package com.fiskmods.heroes.client.json.shape;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.Vertex;
import net.minecraftforge.client.model.obj.WavefrontObject;

public class OBJtoShapeConverter
{
    public static void main(String[] args) throws Exception
    {
        WavefrontObject obj = new WavefrontObject(null, new FileInputStream(new File("C:\\Users\\fiskr\\Desktop\\untitled.obj")));
        BufferedWriter w = new BufferedWriter(new FileWriter(new File("C:\\Users\\fiskr\\Desktop\\output.txt")));

        for (GroupObject o : obj.groupObjects)
        {
            for (Face f : o.faces)
            {
                w.append("    [\n");

                for (int i = 0; i < f.vertices.length; ++i)
                {
                    Vertex v = f.vertices[i];
                    String s = String.format("      %s, %s, %s,\n", v.x, v.y, v.z);
                    w.append(s);

                    if (i == f.vertices.length - 1)
                    {
                        v = f.vertices[0];
                        w.append(String.format("      %s, %s, %s\n", v.x, v.y, v.z));
                    }
                }

                w.append("    ],\n");
            }
        }

        w.close();
    }
}
