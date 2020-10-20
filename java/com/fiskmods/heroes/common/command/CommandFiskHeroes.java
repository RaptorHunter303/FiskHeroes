package com.fiskmods.heroes.common.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.config.RuleHandler;
import com.fiskmods.heroes.common.event.CommonEventHandler;
import com.fiskmods.heroes.common.world.structure.StructureGenerator;
import com.fiskmods.heroes.common.world.structure.StructureLocator;
import com.fiskmods.heroes.common.world.structure.StructureType;
import com.fiskmods.heroes.pack.HeroPack.HeroPackException;
import com.fiskmods.heroes.pack.JSHeroesEngine;
import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;

public class CommandFiskHeroes extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "fiskheroes";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.fiskheroes.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length > 0)
        {
            if ("reload".equals(args[0]))
            {
                Thread thread = new Thread(() ->
                {
                    try
                    {
                        Set<ReloadArgument> set = new HashSet<>();

                        for (int i = 1; i < args.length; ++i)
                        {
                            if ("withResources".equals(args[i]))
                            {
                                set.add(ReloadArgument.TEXTURES);
                                break;
                            }

                            for (ReloadArgument arg : ReloadArgument.values())
                            {
                                if (arg.key.equals(args[i]))
                                {
                                    set.add(arg);
                                    break;
                                }
                            }
                        }

                        func_152373_a(sender, this, "commands.fiskheroes.reload.success", JSHeroesEngine.INSTANCE.reload(set.contains(ReloadArgument.TEXTURES)));
                    }
                    catch (HeroPackException e)
                    {
                        IChatComponent message = new ChatComponentTranslation("commands.fiskheroes.reload.error", e.getMessage());
                        message.getChatStyle().setColor(EnumChatFormatting.RED);

                        sender.addChatMessage(message);
                        e.printStackTrace();
                    }
                }, "Server HeroPack reloader");

                func_152373_a(sender, this, "commands.fiskheroes.reload.start");
                thread.start();
                return;
            }
            else if ("rules".equals(args[0]))
            {
                if (args.length > 1)
                {
                    RuleMode mode = RuleMode.get(args[1]);

                    if (mode != null)
                    {
                        if (args.length - 2 == mode.argTypes.length)
                        {
                            if (mode.argTypes.length > 0 && mode.argTypes[0] == ArgType.RULE)
                            {
                                Rule rule = Rule.getFromName(args[2]);

                                if (rule != null && !mode.valueType.applicable.test(rule.getType()))
                                {
                                    throw new WrongUsageException("commands.fiskheroes.rules.unsupported." + mode.valueType.name().toLowerCase(Locale.ROOT), mode);
                                }
                            }

                            try
                            {
                                mode.execute(this, sender, args);
                                return;
                            }
                            catch (RuntimeException e)
                            {
                                throw e;
                            }
                            catch (Exception e)
                            {
                                throw new RuntimeException(e);
                            }
                        }

                        throw new WrongUsageException("commands.fiskheroes.rules." + args[1] + ".usage");
                    }
                }

                throw new WrongUsageException("commands.fiskheroes.rules.usage");
            }
            else if ("structure".equals(args[0]))
            {
                if (args.length > 1)
                {
                    String arg = args[1];

                    if (arg.equals("locate") ? args.length == 3 || args.length == 4 : args.length == 3 || args.length == 5)
                    {
                        StructureType type = null;

                        for (StructureType type1 : StructureType.values())
                        {
                            if (type1.key.equals(args[2]))
                            {
                                type = type1;
                                break;
                            }
                        }

                        boolean targetAll = arg.equals("locate") && args[2].equals("*");
                        int x = sender.getPlayerCoordinates().posX;
                        int z = sender.getPlayerCoordinates().posZ;

                        if (!arg.equals("locate") && args.length == 5)
                        {
                            x = MathHelper.floor_double(func_110666_a(sender, x, args[3]));
                            z = MathHelper.floor_double(func_110666_a(sender, z, args[4]));
                        }

                        if (type == null && !targetAll)
                        {
                            throw new CommandException("commands.fiskheroes.structure.unknown", args[2]);
                        }

                        World world = sender.getEntityWorld();

                        if (arg.equals("locate"))
                        {
                            WorldInfo info = world.getWorldInfo();

                            if (info.getTerrainType() == WorldType.FLAT || !info.isMapFeaturesEnabled() || !world.provider.isSurfaceWorld())
                            {
                                throw new CommandException("commands.fiskheroes.structure.locate.doesNotGenerate", args[2]);
                            }

                            int range = Integer.MAX_VALUE;

                            if (args.length == 4)
                            {
                                range = parseIntWithMin(sender, args[3], 0);
                            }

                            StructureLocator.locate(this, sender, type, targetAll, range, x, z, 10000);
                            return;
                        }
                        else if (arg.equals("generate"))
                        {
                            if (!world.blockExists(x, 64, z))
                            {
                                throw new CommandException("commands.fiskheroes.structure.generate.outOfWorld", arg);
                            }

                            try
                            {
                                StructureGenerator.generateStructure(world, x, z, type);
                                func_152373_a(sender, this, "commands.fiskheroes.structure.generate.success", type.key, x, z);

                                return;
                            }
                            catch (Exception e)
                            {
                                throw new CommandException("commands.fiskheroes.structure.generate.failure");
                            }
                        }
                    }

                    if (arg.equals("locate") || arg.equals("generate"))
                    {
                        throw new WrongUsageException(String.format("commands.fiskheroes.structure.%s.usage", arg));
                    }
                }

                throw new WrongUsageException("commands.fiskheroes.structure.usage");
            }
        }

        throw new WrongUsageException("commands.fiskheroes.usage");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "reload", "rules", "structure");
        }
        else if (args[0].equals("reload") && args.length > 1)
        {
            return getListOfStringsMatchingLastWord(args, ReloadArgument.NAMES);
        }
        else if (args[0].equals("rules"))
        {
            switch (args.length)
            {
            case 2:
                return getListOfStringsMatchingLastWord(args, RuleMode.NAMES);
            default:
                RuleMode mode = RuleMode.get(args[1]);

                if (mode != null && mode.argTypes.length > args.length - 3)
                {
                    ArgType type = mode.argTypes[args.length - 3];

                    switch (type)
                    {
                    case VALUE:
                        Rule rule = Rule.getFromName(args[2]);
                        return rule != null && rule.ofType(Boolean.class) && mode.valueType.applicable.test(rule.getType()) ? getListOfStringsMatchingLastWord(args, "true", "false") : null;
                    case RULE:
                        return getListOfStringsFromIterableMatchingLastWord(args, Rule.REGISTRY.getKeys(r -> mode.valueType.applicable.test(r.getType())));
                    case RULESET:
                        return getListOfStringsFromIterableMatchingLastWord(args, RuleHandler.getKeys());
                    default:
                        return null;
                    }
                }

                return null;
            }
        }
        else if (args[0].equals("structure"))
        {
            if (args.length == 2)
            {
                return getListOfStringsMatchingLastWord(args, "locate", "generate");
            }
            else if (args.length == 3)
            {
                return getListOfStringsMatchingLastWord(args, StructureType.NAMES);
            }
        }

        return null;
    }

    private enum ReloadArgument
    {
        TEXTURES("--resources");

        private static final String[] NAMES;

        private final String key;

        ReloadArgument(String key)
        {
            this.key = key;
        }

        static
        {
            NAMES = new String[values().length];

            for (int i = 0; i < NAMES.length; ++i)
            {
                NAMES[i] = values()[i].key;
            }
        }
    }

    private enum RuleMode implements ICommandExecutor
    {
        SAVE(ValueType.ALL, (command, sender, args) ->
        {
            String key = args[2].toLowerCase(Locale.ROOT);

            try
            {
                RuleHandler.INSTANCE.save(sender.getEntityWorld(), CommonEventHandler.INSTANCE.saveDir, key);
                func_152373_a(sender, command, "commands.fiskheroes.rules.save.success", key + ".dat");
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new CommandException("commands.fiskheroes.rules.save.failure", key + ".dat", e.getMessage());
            }
        }, ArgType.STRING),
        LOAD(ValueType.ALL, (command, sender, args) ->
        {
            String key = args[2].toLowerCase(Locale.ROOT);

            if (RuleHandler.INSTANCE.load(key))
            {
                func_152373_a(sender, command, "commands.fiskheroes.rules.load.success", key);
            }
            else
            {
                throw new CommandException("commands.fiskheroes.rules.load.unknown", key);
            }
        }, ArgType.RULESET),
        PRINT(ValueType.ALL, (IRuleOrAllExecutor) (command, sender, args, r) ->
        {
            IChatComponent message;

            if (r != null)
            {
                IChatComponent key = new ChatComponentText(Rule.getNameFor(r));
                IChatComponent value = new ChatComponentText(String.valueOf(RuleHandler.getGlobal().get(r)));
                message = new ChatComponentTranslation("commands.fiskheroes.rules.print.entry", key, value);

                key.getChatStyle().setColor(EnumChatFormatting.GREEN);
                value.getChatStyle().setColor(EnumChatFormatting.YELLOW);
                message.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
                sender.addChatMessage(message);
                return;
            }

            message = new ChatComponentTranslation("commands.fiskheroes.rules.print.header");
            message.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
            sender.addChatMessage(message);

            List<Rule> rules = Lists.newArrayList(Rule.REGISTRY);
            rules.sort((o1, o2) -> o1.delegate.name().compareTo(o2.delegate.name()));

            for (Rule rule : rules)
            {
                IChatComponent key = new ChatComponentText(Rule.getNameFor(rule));
                IChatComponent value = new ChatComponentText(String.valueOf(RuleHandler.getGlobal().get(rule)));
                message = new ChatComponentTranslation("commands.fiskheroes.rules.print.entry", key, value);

                key.getChatStyle().setColor(EnumChatFormatting.GREEN);
                value.getChatStyle().setColor(EnumChatFormatting.YELLOW);
                message.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
                sender.addChatMessage(message);
            }
        }, ArgType.RULE),
        SET(ValueType.ALL, (IRuleExecutor) (command, sender, args, rule) ->
        {
            func_152373_a(sender, command, "commands.fiskheroes.rules.set.success", Rule.getNameFor(rule), RuleHandler.getGlobal().put(rule, rule.parse(sender, args[3])));
        }, ArgType.RULE, ArgType.VALUE),
        RESET(ValueType.ALL, (IRuleOrAllExecutor) (command, sender, args, r) ->
        {
            if (r != null)
            {
                func_152373_a(sender, command, "commands.fiskheroes.rules.reset.success", Rule.getNameFor(r), RuleHandler.getGlobal().put(r, r.defaultValue));
                return;
            }

            for (Rule rule : Rule.REGISTRY)
            {
                RuleHandler.getGlobal().put(rule, rule.defaultValue);
            }

            func_152373_a(sender, command, "commands.fiskheroes.rules.reset.all.success");
        }, ArgType.RULE),
        TOGGLE(ValueType.BOOLEAN, (IRuleExecutor) (command, sender, args, rule) ->
        {
            func_152373_a(sender, command, "commands.fiskheroes.rules.set.success", Rule.getNameFor(rule), RuleHandler.getGlobal().put(rule, !((Boolean) RuleHandler.getGlobal().get(rule))));
        }, ArgType.RULE),
        ADD(ValueType.NUMERIC, (IRuleExecutor) (command, sender, args, rule) ->
        {
            func_152373_a(sender, command, "commands.fiskheroes.rules.set.success", Rule.getNameFor(rule), RuleHandler.getGlobal().put(rule, rule.add(RuleHandler.getGlobal().get(rule), rule.parse(sender, args[3]))));
        }, ArgType.RULE, ArgType.VALUE),
        MULT(ValueType.NUMERIC, (IRuleExecutor) (command, sender, args, rule) ->
        {
            func_152373_a(sender, command, "commands.fiskheroes.rules.set.success", Rule.getNameFor(rule), RuleHandler.getGlobal().put(rule, rule.mult(RuleHandler.getGlobal().get(rule), rule.parse(sender, args[3]))));
        }, ArgType.RULE, ArgType.VALUE),
        CLAMP(ValueType.NUMERIC, (IRuleExecutor) (command, sender, args, rule) ->
        {
            func_152373_a(sender, command, "commands.fiskheroes.rules.set.success", Rule.getNameFor(rule), RuleHandler.getGlobal().put(rule, rule.clamp(RuleHandler.getGlobal().get(rule), rule.parse(sender, args[3]), rule.parse(sender, args[4]))));
        }, ArgType.RULE, ArgType.VALUE, ArgType.VALUE);

        private static final String[] NAMES;

        private final ICommandExecutor executor;
        private final ValueType valueType;
        private final ArgType[] argTypes;

        private RuleMode(ValueType type, ICommandExecutor e, ArgType... args)
        {
            valueType = type;
            argTypes = args;
            executor = e;
        }

        @Override
        public void execute(ICommand command, ICommandSender sender, String[] args) throws Exception
        {
            executor.execute(command, sender, args);
        }

        public static RuleMode get(String s)
        {
            for (RuleMode mode : values())
            {
                if (s.equals(mode.name().toLowerCase(Locale.ROOT)))
                {
                    return mode;
                }
            }

            return null;
        }

        static
        {
            NAMES = new String[values().length];

            for (int i = 0; i < NAMES.length; ++i)
            {
                NAMES[i] = values()[i].name().toLowerCase(Locale.ROOT);
            }
        }
    }

    private enum ValueType
    {
        ALL(c -> true),
        BOOLEAN(Boolean.class),
        NUMERIC(Integer.class, Float.class, Double.class);

        private final Predicate<Class> applicable;

        private ValueType(Predicate<Class> p)
        {
            applicable = p;
        }

        private ValueType(Class... classes)
        {
            this(c -> Arrays.asList(classes).contains(c));
        }
    }

    private enum ArgType
    {
        VALUE,
        RULE,
        RULESET,
        STRING;
    }

    private interface ICommandExecutor
    {
        void execute(ICommand command, ICommandSender sender, String[] args) throws Exception;
    }

    private interface IRuleExecutor extends ICommandExecutor
    {
        void execute(ICommand command, ICommandSender sender, String[] args, Rule rule) throws Exception;

        @Override
        default void execute(ICommand command, ICommandSender sender, String[] args) throws Exception
        {
            Rule rule = Rule.getFromName(args[2]);

            if (rule == null)
            {
                throw new CommandException("commands.fiskheroes.rules.unknown", args[2]);
            }

            execute(command, sender, args, rule);
        }
    }

    private interface IRuleOrAllExecutor extends IRuleExecutor
    {
        @Override
        default void execute(ICommand command, ICommandSender sender, String[] args) throws Exception
        {
            Rule rule = Rule.getFromName(args[2]);

            if (rule != null || "*".equals(args[2]))
            {
                execute(command, sender, args, rule);
                return;
            }

            throw new CommandException("commands.fiskheroes.rules.unknown", args[2]);
        }
    }
}
