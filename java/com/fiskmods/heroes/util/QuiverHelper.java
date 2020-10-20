package com.fiskmods.heroes.util;

import java.util.HashMap;
import java.util.Map;

import com.fiskmods.heroes.common.Pair;
import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.container.InventoryQuiver;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.item.ItemTrickArrow;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.NBTHelper.INBTSaveAdapter;
import com.fiskmods.heroes.util.NBTHelper.INBTSavedObject;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraft.nbt.NBTTagLong;

public class QuiverHelper
{
    public static byte locateEquippedQuiver(EntityPlayer player)
    {
        InventoryPlayer inventory = player.inventory;
        byte slot = -1;

        for (byte b = 0; b < inventory.mainInventory.length; ++b)
        {
            ItemStack itemstack = inventory.mainInventory[b];

            if (itemstack != null && itemstack.getItem() == ModItems.quiver)
            {
                if (slot < 0)
                {
                    slot = b;
                }

                InventoryQuiver quiver = new InventoryQuiver(player, b);

                if (quiver.getFirstItemStack() != null)
                {
                    return b;
                }
            }
        }

        return slot;
    }

    public static Quiver getQuiver(Entity entity)
    {
        return SHData.EQUIPPED_QUIVER.get(entity);
    }

    public static boolean hasQuiver(Entity entity)
    {
        return getQuiver(entity) != null;
    }

    public static ItemStack getEquippedQuiver(EntityPlayer player)
    {
        return FiskServerUtils.getStackInSlot(player, SHData.EQUIPPED_QUIVER_SLOT.get(player));
    }

    public static InventoryQuiver getQuiverInventory(EntityPlayer player)
    {
        return new InventoryQuiver(player, getEquippedQuiver(player));
    }

    public static ItemStack getArrowToFire(EntityPlayer player)
    {
        InventoryQuiver inventory = getQuiverInventory(player);

        if (inventory != null)
        {
            int slot = SHData.SELECTED_ARROW.get(player);

            if (inventory.getStackInSlot(slot) != null)
            {
                return inventory.getStackInSlot(slot);
            }

            return inventory.getFirstItemStack();
        }

        return null;
    }

    public static ArrowType getArrowTypeToFire(EntityPlayer player)
    {
        Quiver quiver = getQuiver(player);
        return quiver != null ? quiver.getType(quiver.getActiveSlot(player)) : null;
    }

    public static class Quiver implements INBTSavedObject<Quiver>
    {
        private final Map<Integer, Pair<ArrowType, Integer>> map = new HashMap<>();

        private int metadata;
        private boolean enchanted;

        public Quiver(long code)
        {
            setBinaryCode(code);
        }

        public Quiver(EntityPlayer player, ItemStack quiver)
        {
            InventoryQuiver inventory = new InventoryQuiver(player, quiver);

            for (int slot = 0; slot < inventory.getSizeInventory(); ++slot)
            {
                ItemStack arrow = inventory.getStackInSlot(slot);

                if (arrow != null)
                {
                    map.put(slot, Pair.of(ItemTrickArrow.getType(arrow), arrow.stackSize / 21 + 1));
                }
            }

            setMetadata(quiver.getItemDamage());
            setEnchanted(quiver.isItemEnchanted());
        }

        public long getBinaryCode()
        {
            long code = metadata & 0x7F;

            if (enchanted)
            {
                code |= 0x80;
            }

            for (int slot = 4; slot >= 0; --slot)
            {
                Pair<ArrowType, Integer> p = get(slot);
                code <<= 9;
                code |= ArrowType.getIdFromArrow(p.getKey()) & 0x3F;
                code |= (p.getValue() & 7) << 6;
            }

            return code;
        }

        public void setBinaryCode(long code)
        {
            map.clear();

            if (code != 0)
            {
                for (int slot = 0; slot < 5; ++slot)
                {
                    int i = (int) (code & 0x1FF);
                    map.put(slot, Pair.of(ArrowType.getArrowById(i & 0x3F), i >> 6 & 7));
                    code >>= 9;
                }

                metadata = (int) (code & 0x7F);
                enchanted = (code & 0x80) == 0x80;
            }
            else
            {
                metadata = 0;
                enchanted = false;
            }
        }

        public ArrowType getType(int slot)
        {
            return get(slot).getKey();
        }

        public void setType(int slot, ArrowType type)
        {
            map.put(slot, Pair.of(type == null ? ArrowType.getArrowById(0) : type, getAmount(slot)));
        }

        public int getAmount(int slot)
        {
            return get(slot).getValue();
        }

        public void setAmount(int slot, int amount)
        {
            map.put(slot, Pair.of(getType(slot), amount));
        }

        public int getMetadata()
        {
            return metadata;
        }

        public boolean isEnchanted()
        {
            return enchanted;
        }

        public void setMetadata(int meta)
        {
            metadata = meta & 0x7F;
        }

        public void setEnchanted(boolean isEnchanted)
        {
            enchanted = isEnchanted;
        }

        public int getFirstSlot()
        {
            for (int slot = 0; slot < 5; ++slot)
            {
                if (!isEmpty(slot))
                {
                    return slot;
                }
            }

            return 0;
        }

        public int getActiveSlot(Entity entity)
        {
            int slot = SHData.SELECTED_ARROW.get(entity);

            if (!isEmpty(slot))
            {
                return slot;
            }

            return getFirstSlot();
        }

        public boolean isEmpty(int slot)
        {
            return getAmount(slot) <= 0;
        }

        public boolean isEmpty()
        {
            for (int slot = 0; slot < 5; ++slot)
            {
                if (!isEmpty(slot))
                {
                    return false;
                }
            }

            return true;
        }

        public Pair<ArrowType, Integer> get(int slot)
        {
            Pair<ArrowType, Integer> p = map.get(slot);

            if (p == null)
            {
                map.put(slot, p = Pair.of(ArrowType.getArrowById(0), 0));
            }

            return p;
        }

        @Override
        public NBTBase writeToNBT()
        {
            return new NBTTagLong(getBinaryCode());
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeLong(getBinaryCode());
        }

        @Override
        public int hashCode()
        {
            return map.hashCode();
        }

        @Override
        public String toString()
        {
            return String.format("Quiver[meta=%s, inv=%s]", metadata, map);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof Quiver)
            {
                return getBinaryCode() == ((Quiver) obj).getBinaryCode();
            }

            return false;
        }

        static
        {
            NBTHelper.registerAdapter(Quiver.class, new INBTSaveAdapter<Quiver>()
            {
                @Override
                public Quiver readFromNBT(NBTBase tag)
                {
                    if (tag instanceof NBTPrimitive)
                    {
                        return new Quiver(((NBTPrimitive) tag).func_150291_c());
                    }

                    return null;
                }

                @Override
                public Quiver fromBytes(ByteBuf buf)
                {
                    return new Quiver(buf.readLong());
                }
            });
        }
    }
}
