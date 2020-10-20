package com.fiskmods.heroes.client.gui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import com.fiskmods.heroes.common.container.ContainerDisplayStand;
import com.fiskmods.heroes.common.container.ContainerQuiver;
import com.fiskmods.heroes.common.container.ContainerSuitFabricator;
import com.fiskmods.heroes.common.container.ContainerSuitIterator;
import com.fiskmods.heroes.common.container.InventoryQuiver;
import com.fiskmods.heroes.common.item.ItemCactusJournal;
import com.fiskmods.heroes.common.item.ItemMetahumanLog;
import com.fiskmods.heroes.common.network.MessageUpdateBook;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.common.tileentity.TileEntitySuitFabricator;
import com.fiskmods.heroes.gameboii.GameboiiCartridge;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SHGuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileentity = world.getTileEntity(x, y, z);

        switch (id)
        {
        case 0:
            return new ContainerQuiver(player.inventory, new InventoryQuiver(player, x));
        case 2:
            return new ContainerDisplayStand(player.inventory, (TileEntityDisplayStand) tileentity);
        case 3:
            return new ContainerSuitFabricator(player.inventory, (TileEntitySuitFabricator) tileentity);
        case 6:
            return new ContainerSuitIterator(player.inventory, world, x, y, z);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int id, final EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileentity = world.getTileEntity(x, y, z);
        ItemStack heldItem = player.getHeldItem();

        switch (id)
        {
        case 0:
            return new GuiQuiver(player.inventory, new InventoryQuiver(player, x));
        case 1:
            return new GuiDisguise();
        case 2:
            return new GuiDisplayStand(player.inventory, (TileEntityDisplayStand) tileentity);
        case 3:
            return new GuiSuitFabricator(player.inventory, (TileEntitySuitFabricator) tileentity);
        case 4:
            switch (x)
            {
            case 0:
                return new GuiSuperheroesBook(player, heldItem, ItemMetahumanLog.getBook(heldItem));
            case 2:
                return new GuiSuperheroesBook(player, heldItem, ItemCactusJournal.getBook(heldItem));
            case 1:
                switch (y)
                {
                case 1:
                    return new GuiDebugBook(player, heldItem);
                case 0:
                    new Thread(() ->
                    {
                        try
                        {
                            Thread.sleep(500);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }

                        FileDialog dialog = new FileDialog((Frame) null, "Select File", FileDialog.LOAD);
                        dialog.setFile("*.json");
                        dialog.setVisible(true);

                        String dir = dialog.getDirectory();
                        String name = dialog.getFile();

                        if (dir != null && name != null)
                        {
                            File file = new File(dir, name);

                            if (file != null && file.isFile() && file.exists())
                            {
                                if (!heldItem.hasTagCompound())
                                {
                                    heldItem.setTagCompound(new NBTTagCompound());
                                }

                                heldItem.getTagCompound().setString("Path", file.getPath());
                                SHNetworkManager.wrapper.sendToServer(new MessageUpdateBook(heldItem.getTagCompound()));
                            }
                        }
                    }).start();

                    Minecraft mc = Minecraft.getMinecraft();

                    if (mc.isSingleplayer() && !mc.getIntegratedServer().getPublic())
                    {
                        mc.getSoundHandler().pauseSounds();
                    }

                    return new GuiIngameMenu();
                }
                break;
            }
        case 5:
            return new GuiGameboii(GameboiiCartridge.get(x));
        case 6:
            return new GuiSuitIterator(player.inventory, world, x, y, z);
        }

        return null;
    }
}
