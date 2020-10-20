package com.fiskmods.heroes.common.command;

import com.fiskmods.heroes.common.data.world.SHMapData;
import com.fiskmods.heroes.common.network.MessageForceSlow;
import com.fiskmods.heroes.common.network.SHNetworkManager;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class CommandForceSlow extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "forceslow";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "commands.forceslow.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length <= 0 || args.length > 1)
        {
            throw new WrongUsageException("commands.forceslow.usage");
        }
        else
        {
            World world = sender.getEntityWorld();
            SHMapData data = SHMapData.get(world);
            float amount = (float) parseDoubleBounded(sender, args[0], 0, 1);
            int percent = Math.round(amount * 100);

            boolean flag = percent > 0;
            amount = percent * 0.01F;

            if (data.forceSlow != amount)
            {
                data.forceSlow = amount;
                SHNetworkManager.wrapper.sendToAll(new MessageForceSlow(amount));

                IChatComponent component = new ChatComponentTranslation("commands.forceslow.success." + (flag ? "on" : "off")).setChatStyle(new ChatStyle().setColor(flag ? EnumChatFormatting.GREEN : EnumChatFormatting.RED));
                MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("commands.forceslow.success", component, Math.round(amount * 100)).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
            }
        }
    }
}
