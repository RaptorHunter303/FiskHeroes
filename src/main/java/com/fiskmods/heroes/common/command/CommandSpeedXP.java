package com.fiskmods.heroes.common.command;

import java.util.List;

import com.fiskmods.heroes.common.data.DataManager;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandSpeedXP extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "speedxp";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "commands.speedxp.usage";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args)
    {
        if (args.length <= 0)
        {
            throw new WrongUsageException("commands.speedxp.usage");
        }
        else
        {
            String s = args[0];
            boolean level = s.endsWith("l") || s.endsWith("L");

            if (level && s.length() > 1)
            {
                s = s.substring(0, s.length() - 1);
            }

            int amount = parseInt(commandSender, s);
            boolean negative = amount < 0;

            if (negative)
            {
                amount *= -1;
            }

            EntityPlayerMP player;

            if (args.length > 1)
            {
                player = getPlayer(commandSender, args[1]);
            }
            else
            {
                player = getCommandSenderAsPlayer(commandSender);
            }

            if (level)
            {
                if (negative)
                {
                    DataManager.addSpeedExperienceLevel(player, -amount);
                    func_152373_a(commandSender, this, "commands.speedxp.success.negative.levels", amount, player.getCommandSenderName());
                }
                else
                {
                    DataManager.addSpeedExperienceLevel(player, amount);
                    func_152373_a(commandSender, this, "commands.speedxp.success.levels", amount, player.getCommandSenderName());
                }
            }
            else
            {
                if (negative)
                {
                    throw new WrongUsageException("commands.speedxp.failure.widthdrawXp");
                }

                DataManager.addSpeedExperience(player, amount);
                func_152373_a(commandSender, this, "commands.speedxp.success", amount, player.getCommandSenderName());
            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] args)
    {
        return args.length == 2 ? getListOfStringsMatchingLastWord(args, getAllUsernames()) : null;
    }

    protected String[] getAllUsernames()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 1;
    }
}
