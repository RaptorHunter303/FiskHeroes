package com.fiskmods.heroes.common.command;

import java.util.List;

import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

public class CommandStatEffect extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "stateffect";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.stateffect.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.stateffect.usage");
        }
        else
        {
            EntityPlayerMP player = getPlayer(sender, args[0]);

            if (args[1].equals("clear"))
            {
                if (StatusEffect.get(player).isEmpty())
                {
                    throw new CommandException("commands.stateffect.failure.notActive.all", player.getCommandSenderName());
                }

                StatusEffect.clear(player);
                func_152373_a(sender, this, "commands.stateffect.success.removed.all", player.getCommandSenderName());
            }
            else
            {
                StatEffect effect = StatEffect.REGISTRY.lookup(args[1]);
                int seconds = 30;
                int amplifier = 0;

                if (effect == null)
                {
                    throw new NumberInvalidException("commands.stateffect.notFound", args[1]);
                }

                if (args.length >= 3)
                {
                    seconds = parseIntBounded(sender, args[2], 0, 1000000);
                }

                if (args.length >= 4)
                {
                    amplifier = parseIntBounded(sender, args[3], 0, 255);
                }

                if (seconds == 0)
                {
                    if (!StatusEffect.has(player, effect))
                    {
                        throw new CommandException("commands.stateffect.failure.notActive", new ChatComponentTranslation(effect.getUnlocalizedName()), player.getCommandSenderName());
                    }

                    StatusEffect.clear(player, effect);
                    func_152373_a(sender, this, "commands.stateffect.success.removed", new ChatComponentTranslation(effect.getUnlocalizedName()), player.getCommandSenderName());
                }
                else
                {
                    StatusEffect.add(player, effect, seconds * 20, amplifier);
                    func_152373_a(sender, this, "commands.stateffect.success", new ChatComponentTranslation(effect.getUnlocalizedName()), amplifier, player.getCommandSenderName(), seconds);
                }
            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        switch (args.length)
        {
        case 1:
            return getListOfStringsMatchingLastWord(args, getAllUsernames());
        case 2:
            return getListOfStringsFromIterableMatchingLastWord(args, StatEffect.REGISTRY.getKeys());
        default:
            return null;
        }
    }

    protected String[] getAllUsernames()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
