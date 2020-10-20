package com.fiskmods.heroes.common.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.hero.Heroes;
import com.fiskmods.heroes.common.hero.Tier;
import com.fiskmods.heroes.common.predicate.HeroSelector;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.NBTHelper;
import com.fiskmods.heroes.util.SHFormatHelper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.Constants.NBT;

public class CommandSuit extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "suit";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.suit.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1 || args.length > 2)
        {
            throw new WrongUsageException("commands.suit.usage");
        }
        else
        {
            EntityPlayerMP player = args.length >= 2 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
            NBTTagCompound compound = new NBTTagCompound();
            String tagString = "{}";

            if (args[0].startsWith("{"))
            {
                tagString = func_147178_a(sender, new String[] {args[0]}, 0).getUnformattedText();

                try
                {
                    NBTBase nbtbase = JsonToNBT.func_150315_a(tagString);

                    if (!(nbtbase instanceof NBTTagCompound))
                    {
                        func_152373_a(sender, this, "commands.suit.tagError", "Not a valid tag");
                        return;
                    }

                    compound = (NBTTagCompound) nbtbase;

                    if (!compound.hasKey("Id") && !compound.hasKey("Random", NBT.TAG_COMPOUND))
                    {
                        func_152373_a(sender, this, "commands.suit.tagError", "Missing required tag 'Id'");
                        return;
                    }
                }
                catch (NBTException nbtexception)
                {
                    func_152373_a(sender, this, "commands.suit.tagError", nbtexception.getMessage());
                    return;
                }
            }
            else
            {
                compound.setString("Id", args[0]);
            }

            String s = compound.getString("Id");
            Random rand = new Random();
            boolean iterate = false;

            if (compound.hasKey("Seed"))
            {
                rand.setSeed(compound.getLong("Seed"));
            }

            if (compound.hasKey("Iterate", NBT.TAG_ANY_NUMERIC))
            {
                iterate = compound.getBoolean("Iterate");
            }

            if (compound.hasKey("Id", NBT.TAG_LIST))
            {
                NBTTagList list = compound.getTagList("Id", NBT.TAG_COMPOUND);
                Map<String, Integer> map = new HashMap<>();

                for (int i = 0; i < list.tagCount(); ++i)
                {
                    NBTTagCompound tag = list.getCompoundTagAt(i);

                    if (tag.hasKey("Id"))
                    {
                        map.put(tag.getString("Id"), Math.max(tag.getInteger("Weight"), 1));
                    }
                }

                if (!map.isEmpty())
                {
                    s = FiskMath.getWeighted(rand, map);
                }
            }

            if (compound.hasKey("Random", NBT.TAG_COMPOUND))
            {
                NBTTagCompound randTag = compound.getCompoundTag("Random");
                Iterator<Hero> iter = Hero.REGISTRY.getValues().stream().filter(HeroSelector.selector(randTag).and(t -> !t.isHidden())).iterator();

                if (!iter.hasNext())
                {
                    throw new NumberInvalidException("commands.suit.notFound.criteria", tagString);
                }

                if (randTag.hasKey("TierBias", NBT.TAG_ANY_NUMERIC))
                {
                    Map<Hero, Float> map = new HashMap<>();
                    float bias = randTag.getFloat("TierBias");

                    for (Hero hero; iter.hasNext() && (hero = iter.next()) != null;)
                    {
                        map.put(hero, 1 + (Tier.MAX - hero.getTier().tier) * bias);
                    }

                    s = Hero.getNameForHero(FiskMath.getWeighted(rand, map));
                }
                else
                {
                    List<Hero> list = Lists.newArrayList(iter);
                    s = Hero.getNameForHero(list.get(rand.nextInt(list.size())));
                }
            }

            HeroIteration iter = getHeroByText(s);

            if (iter.hero == Heroes.spodermen)
            {
                throw new CommandException("Nice try mate, but you're gonna have to try a bit harder than that if you want to get the %s suit. :P", iter.hero.getLocalizedName());
            }

            Collection<HeroIteration> c = iter.hero.getIterations();

            if (iterate && !c.isEmpty())
            {
                iter = Iterables.get(c, rand.nextInt(c.size()));
            }

            NBTTagCompound tag = compound.getCompoundTag("NBT");
            boolean equip = compound.getBoolean("Equip");
            boolean force = compound.getBoolean("Force");
            float durability = 1;

            if (compound.hasKey("Durability"))
            {
                durability = MathHelper.clamp_float(compound.getFloat("Durability"), 0, 1);
            }

            for (int i = 0; i < 4; ++i)
            {
                ItemStack itemstack = iter.createArmor(i);

                if (itemstack != null)
                {
                    if (durability < 1)
                    {
                        itemstack.setItemDamage(Math.round((1 - durability) * itemstack.getMaxDamage()));
                    }

                    itemstack.setTagCompound(NBTHelper.merge(itemstack.getTagCompound(), tag));
                }

                if (equip && (itemstack != null || force))
                {
                    int slot = 4 - i;

                    if (player.getEquipmentInSlot(slot) == null || force)
                    {
                        player.setCurrentItemOrArmor(slot, itemstack);
                        continue;
                    }
                }

                if (itemstack == null)
                {
                    continue;
                }

                EntityItem entityitem = player.dropPlayerItemWithRandomChoice(itemstack, false);

                if (entityitem != null)
                {
                    entityitem.delayBeforeCanPickup = 0;
                    entityitem.func_145797_a(player.getCommandSenderName());
                }
            }

            byte equipment = compound.getByte("Equipment");

            if (equipment != 0)
            {
                List<ItemStack> stacks = iter.hero.getEquipmentStacks();

                for (int i = 0; i < stacks.size(); ++i)
                {
                    ItemStack itemstack = stacks.get(i);

                    if (equipment >= 0 && i >= equipment)
                    {
                        break;
                    }

                    if (i == 0 && (player.getEquipmentInSlot(0) == null || force))
                    {
                        player.setCurrentItemOrArmor(0, itemstack);
                        continue;
                    }

                    EntityItem entityitem = player.dropPlayerItemWithRandomChoice(itemstack.copy(), false);

                    if (entityitem != null)
                    {
                        entityitem.delayBeforeCanPickup = 0;
                        entityitem.func_145797_a(player.getCommandSenderName());
                    }
                }
            }

            func_152373_a(sender, this, "commands.suit.success", SHFormatHelper.formatHero(iter), player.getCommandSenderName());
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        switch (args.length)
        {
        case 1:
            return getListOfStringsFromIterableMatchingLastWord(args, HeroIteration.getKeys());
        case 2:
            return getListOfStringsMatchingLastWord(args, getPlayers());
        default:
            return null;
        }
    }

    protected String[] getPlayers()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 1;
    }

    public static HeroIteration getHeroByText(String key)
    {
        HeroIteration iter = HeroIteration.get(key);

        if (iter != null)
        {
            return iter;
        }

        throw new NumberInvalidException("commands.suit.notFound", key);
    }
}
