package com.fiskmods.heroes.common.item;

import java.util.List;
import java.util.function.Predicate;

import com.fiskmods.heroes.common.book.widget.IItemListEntry;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHFormatHelper;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.collect.Iterables;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemFlashRing extends ItemUntextured implements IItemListEntry
{
    private static final Predicate<Hero> PREDICATE = t -> Iterables.any(t.getEquipmentStacks(), t1 -> t1.getItem() == ModItems.flashRing);

    public ItemFlashRing()
    {
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
    {
        HeroIteration iter = getContainedHero(itemstack);

        if (iter != null)
        {
            list.add(SHFormatHelper.formatHero(iter));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
//        if (!world.isRemote)
        {
            if (itemstack.hasTagCompound())
            {
                if (itemstack.getTagCompound().hasKey("Suit", NBT.TAG_STRING))
                {
                    HeroIteration iter = HeroIteration.lookup(itemstack.getTagCompound().getString("Suit"));

                    if (iter != null)
                    {
                        setContainedArmor(itemstack, iter.createArmorStacks());
                    }

                    itemstack.getTagCompound().removeTag("Suit");
                }

                if (itemstack.getTagCompound().getBoolean("Dispensed"))
                {
                    itemstack.getTagCompound().removeTag("Items");
                }

                if (itemstack.getTagCompound().hasKey("Dispensed"))
                {
                    itemstack.getTagCompound().removeTag("Dispensed");
                }
            }

            ItemStack[] equipment = SHHelper.getEquipment(player);
            HeroIteration iter = SHHelper.getHeroIter(equipment);

            if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Items", NBT.TAG_LIST))
            {
                ItemStack[] armorFromNBT = getArmorFromNBT(itemstack);

                if (iter != null && PREDICATE.test(iter.hero))
                {
                    setContainedArmor(itemstack, equipment);

                    if (armorFromNBT != null)
                    {
                        for (int i = 0; i < 4; ++i)
                        {
                            player.setCurrentItemOrArmor(4 - i, armorFromNBT[i]);
                        }
                    }
                }
                else
                {
                    if (armorFromNBT != null)
                    {
                        for (int i = 0; i < 4; ++i)
                        {
                            swapArmor(player, armorFromNBT[i], 3 - i);
                        }
                    }

                    itemstack.getTagCompound().removeTag("Items");
                }
            }
            else if (iter != null && PREDICATE.test(iter.hero))
            {
                setContainedArmor(itemstack, equipment);

                for (int i = 1; i <= 4; ++i)
                {
                    player.setCurrentItemOrArmor(i, null);
                }
            }

            if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasNoTags())
            {
                itemstack.setTagCompound(null);
            }
        }

        return itemstack;
    }

    public void swapArmor(EntityPlayer player, ItemStack itemstack, int slot)
    {
        if (itemstack != null)
        {
            ItemStack armor = player.getCurrentArmor(slot);

            if (armor != null && !player.inventory.addItemStackToInventory(armor))
            {
                player.entityDropItem(armor, 0);
            }

            player.setCurrentItemOrArmor(slot + 1, itemstack);
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item));
        Hero.REGISTRY.getValues().stream().filter(PREDICATE.and(t -> !t.isHidden())).sorted().forEach(t ->
        {
            if (tab == null)
            {
                t.getIterations().stream().sorted().forEach(t1 -> list.add(create(t1)));
            }
            else
            {
                list.add(create(t.getDefault()));
            }
        });
    }

    @Override
    public void getListItems(Item item, CreativeTabs tab, List list)
    {
        super.getSubItems(item, tab, list);
    }

    @Override
    public String getPageLink(ItemStack itemstack)
    {
        return itemstack.getUnlocalizedName();
    }

    public static void setContainedArmor(ItemStack itemstack, ItemStack... armor)
    {
        NBTTagList itemsList = new NBTTagList();

        for (int i = 0; i < armor.length; ++i)
        {
            if (armor[i] != null)
            {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                itemsList.appendTag(armor[i].writeToNBT(itemTag));
            }
        }

        if (!itemstack.hasTagCompound())
        {
            itemstack.setTagCompound(new NBTTagCompound());
        }

        itemstack.getTagCompound().setTag("Items", itemsList);
    }

    public static ItemStack[] getArmorFromNBT(ItemStack itemstack)
    {
        if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Items"))
        {
            NBTTagList nbtItems = itemstack.getTagCompound().getTagList("Items", NBT.TAG_COMPOUND);
            ItemStack[] items = new ItemStack[4];

            for (int i = 0; i < nbtItems.tagCount(); ++i)
            {
                NBTTagCompound item = nbtItems.getCompoundTagAt(i);
                byte slot = item.getByte("Slot");

                if (slot >= 0 && slot < items.length)
                {
                    items[slot] = ItemStack.loadItemStackFromNBT(item);
                }
            }

            return items;
        }

        return null;
    }

    public static HeroIteration getContainedHero(ItemStack itemstack)
    {
        if (itemstack.hasTagCompound())
        {
            if (itemstack.getTagCompound().hasKey("Items", NBT.TAG_LIST) && !itemstack.getTagCompound().getBoolean("Dispensed"))
            {
                return SHHelper.getHeroIterFromArmor(FiskServerUtils.nonNull(getArmorFromNBT(itemstack)));
            }
            else if (itemstack.getTagCompound().hasKey("Suit", NBT.TAG_STRING))
            {
                return HeroIteration.lookup(itemstack.getTagCompound().getString("Suit"));
            }
        }

        return null;
    }

    public static ItemStack create(Item item, HeroIteration iter)
    {
        ItemStack itemstack = new ItemStack(item);
        itemstack.setTagCompound(new NBTTagCompound());
        itemstack.getTagCompound().setString("Suit", iter.toString());

        return itemstack;
    }

    public static ItemStack create(HeroIteration iter)
    {
        return create(ModItems.flashRing, iter);
    }
}
