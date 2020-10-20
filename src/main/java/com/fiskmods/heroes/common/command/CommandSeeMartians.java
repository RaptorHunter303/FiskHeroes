package com.fiskmods.heroes.common.command;

import java.util.List;

import com.fiskmods.heroes.common.data.SHData;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandSeeMartians extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "seemartians";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "commands.seemartians.usage";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args)
    {
        if (args.length > 0)
        {
            EntityPlayerMP player = args.length >= 2 ? getPlayer(commandSender, args[1]) : getCommandSenderAsPlayer(commandSender);
            boolean flag = parseBoolean(commandSender, args[0]);

            SHData.PENETRATE_MARTIAN_INVIS.set(player, flag);

            if (player != commandSender)
            {
                func_152374_a(commandSender, this, 1, "commands.seemartians.success.other", player.getCommandSenderName(), flag);
            }
            else
            {
                func_152374_a(commandSender, this, 1, "commands.seemartians.success.self", flag);
            }
        }
        else
        {
            throw new WrongUsageException("commands.seemartians.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] args)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "true", "false") : args.length == 2 ? getListOfStringsMatchingLastWord(args, getListOfPlayerUsernames()) : null;
    }

    protected String[] getListOfPlayerUsernames()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 1;
    }
}
