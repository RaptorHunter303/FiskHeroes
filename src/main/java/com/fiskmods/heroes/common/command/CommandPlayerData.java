package com.fiskmods.heroes.common.command;

//package fiskfille.core.command;
//
//import java.util.List;
//import java.util.Locale;
//import java.util.function.Predicate;
//
//import com.fiskmods.heroes.common.command.CommandSuit;
//import com.fiskmods.heroes.common.data.SHData;
//import com.fiskmods.heroes.common.data.SHData.ClassType;
//import com.fiskmods.heroes.common.hero.Hero;
//import com.fiskmods.heroes.common.predicate.SHPredicates;
//
//import fiskfille.core.DimensionalCoords;
//import fiskfille.core.helper.NBTHelper;
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.command.WrongUsageException;
//import net.minecraft.item.ItemStack;
//import net.minecraft.server.MinecraftServer;
//
//public class CommandPlayerData extends CommandBase
//{
//    @Override
//    public String getCommandName()
//    {
//        return "playerdata";
//    }
//
//    @Override
//    public int getRequiredPermissionLevel()
//    {
//        return 3;
//    }
//
//    @Override
//    public String getCommandUsage(ICommandSender sender)
//    {
//        return "commands.playerdata.usage";
//    }
//
//    @Override
//    public void processCommand(ICommandSender sender, String[] args)
//    {
//        if (args.length < 2)
//        {
//            throw new WrongUsageException("commands.playerdata.usage");
//        }
//        else
//        {
//            System.out.println("Test");
//        }
//    }
//
//    @Override
//    public List addTabCompletionOptions(ICommandSender sender, String[] args)
//    {
//        switch (args.length)
//        {
//        case 1:
//            return getListOfStringsMatchingLastWord(args, getPlayers());
//        case 2:
//            return getListOfStringsMatchingLastWord(args, Mode.NAMES);
//        case 3:
//        {
//            Mode mode = Mode.get(args[1]);
//            Predicate<SHData<?>> p = t -> t.isCommandAccessible();
//
//            if (mode != null)
//            {
//                p = p.and(SHPredicates.isModeApplicable(mode));
//            }
//
//            return getListOfStringsFromIterableMatchingLastWord(args, SHData.REGISTRY.getKeys(p));
//        }
//        default:
//        {
//            Mode mode = Mode.get(args[1]);
//
//            if (mode != null && args.length - 4 < mode.argTypes.length)
//            {
//                // /playerdata FiskFille set fiskheroes:prev_hero Test2
//
//                switch (mode.argTypes[args.length - 4])
//                {
//                case VALUE:
//                {
//                    SHData<?> data = SHData.getDataFromName(args[2]);
//
//                    if (data != null && SHPredicates.isModeApplicable(mode).test(data))
//                    {
//                        return getListOfStringsFromIterableMatchingLastWord(args, data.getTabCompletions());
//                    }
//
//                    break;
//                }
//                case BOOLEAN:
//                    return getListOfStringsMatchingLastWord(args, "true", "false");
//                }
//            }
//
//            return null;
//        }
//        }
//    }
//
//    protected String[] getPlayers()
//    {
//        return MinecraftServer.getServer().getAllUsernames();
//    }
//
//    @Override
//    public boolean isUsernameIndex(String[] args, int index)
//    {
//        return index == 0;
//    }
//
//    public static <T> T fromString(ClassType type, ICommandSender sender, String[] args, int index)
//    {
//        String s = args[index];
//
//        if (type.getType() == Integer.class)
//        {
//            return (T) Integer.valueOf(parseInt(sender, s));
//        }
//        else if (type.getType() == Double.class)
//        {
//            return (T) Double.valueOf(parseDouble(sender, s));
//        }
//        else if (type.getType() == Float.class)
//        {
//            return (T) Float.valueOf((float) parseDouble(sender, s));
//        }
//        else if (type.getType() == Boolean.class)
//        {
//            return (T) Boolean.valueOf(parseBoolean(sender, s));
//        }
//        else if (type.getType() == String.class)
//        {
//            return (T) func_147176_a(sender, args, index, true).getFormattedText();
//        }
//        else if (type.getType() == ItemStack.class)
//        {
//            return (T) ItemStack.loadItemStackFromNBT(NBTHelper.fromJson(func_147178_a(sender, args, index).getUnformattedText()));
//        }
//        else if (type.getType() == DimensionalCoords.class)
//        {
//            return (T) NBTHelper.readFromNBT(NBTHelper.fromJson(func_147178_a(sender, args, index).getUnformattedText()), new ClassType(DimensionalCoords.class));
//        }
//        else if (type.getType() == Hero.class)
//        {
//            return (T) CommandSuit.getHeroByText(s);
//        }
//
//        return null;
//    }
//
//    public enum Mode
//    {
//        SET(new ArgType[] {ArgType.VALUE})
//        {
//            @Override
//            public void execute(ICommandSender sender, String[] args)
//            {
//            }
//        },
//        RESET
//        {
//            @Override
//            public void execute(ICommandSender sender, String[] args)
//            {
//            }
//        },
//        TOGGLE(Boolean.class)
//        {
//            @Override
//            public void execute(ICommandSender sender, String[] args)
//            {
//            }
//        },
//        FREEZE(new ArgType[] {ArgType.BOOLEAN})
//        {
//            @Override
//            public void execute(ICommandSender sender, String[] args)
//            {
//            }
//        },
//        INCR(new ArgType[] {ArgType.VALUE}, Integer.class, Float.class, Double.class, String.class)
//        {
//            @Override
//            public void execute(ICommandSender sender, String[] args)
//            {
//            }
//        },
//        MULT(new ArgType[] {ArgType.VALUE}, Integer.class, Float.class, Double.class)
//        {
//            @Override
//            public void execute(ICommandSender sender, String[] args)
//            {
//            }
//        },
//        DIV(new ArgType[] {ArgType.VALUE}, Integer.class, Float.class, Double.class)
//        {
//            @Override
//            public void execute(ICommandSender sender, String[] args)
//            {
//            }
//        },
//        CLAMP(new ArgType[] {ArgType.VALUE, ArgType.VALUE}, Integer.class, Float.class, Double.class)
//        {
//            @Override
//            public void execute(ICommandSender sender, String[] args)
//            {
//            }
//        };
//
//        private static final String[] NAMES = new String[values().length];
//
//        public final Class[] applicable;
//        private final ArgType[] argTypes;
//
//        private Mode(ArgType[] args, Class... classes)
//        {
//            argTypes = args;
//            applicable = classes == null || classes.length == 0 ? new Class[] {Object.class} : classes;
//        }
//
//        private Mode(Class... classes)
//        {
//            this(new ArgType[0], classes);
//        }
//
//        public void execute(ICommandSender sender, String[] args)
//        {
//        }
//
//        public static Mode get(String s)
//        {
//            for (Mode mode : values())
//            {
//                if (s.equals(mode.name().toLowerCase(Locale.ROOT)))
//                {
//                    return mode;
//                }
//            }
//
//            return null;
//        }
//
//        static
//        {
//            for (int i = 0; i < NAMES.length; ++i)
//            {
//                NAMES[i] = values()[i].name().toLowerCase(Locale.ROOT);
//            }
//        }
//    }
//
//    private enum ArgType
//    {
//        VALUE,
//        BOOLEAN
//    }
//}
