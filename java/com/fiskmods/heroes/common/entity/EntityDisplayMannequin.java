package com.fiskmods.heroes.common.entity;

import com.fiskmods.heroes.common.data.IDataHolder;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.item.ItemCapsShield;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.util.QuiverHelper.Quiver;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInputFromOptions;

@SideOnly(Side.CLIENT)
public class EntityDisplayMannequin extends EntityClientPlayerMP implements IDataHolder
{
    public final TileEntityDisplayStand displayStand;
    private int maskOpenTimer;

    private float maskOpenAnim;
    private float prevMaskOpenAnim;

    public EntityDisplayMannequin(TileEntityDisplayStand tile, Minecraft mc)
    {
        super(mc, tile.getWorldObj(), mc.getSession(), mc.getNetHandler(), new StatFileWriter());
        movementInput = new MovementInputFromOptions(mc.gameSettings);
        displayStand = tile;
        width = 0.6F;
        height = 1.8F;
        yOffset = 0;
        capabilities.isFlying = true;
        rotationYawHead = 0;
        setLocationAndAngles(tile.xCoord + 0.5F, tile.yCoord, tile.zCoord + 0.5F, 0, 0);
        setDead();
    }

    @Override
    public void onUpdate()
    {
        prevMaskOpenAnim = maskOpenAnim;

        if (displayStand.isRedstonePowered)
        {
            if (maskOpenTimer < 5)
            {
                ++maskOpenTimer;
            }

            if (maskOpenAnim < 1)
            {
                maskOpenAnim += 1F / 5;
            }
        }
        else
        {
            if (maskOpenTimer > 0)
            {
                --maskOpenTimer;
            }

            if (maskOpenAnim > 0)
            {
                maskOpenAnim -= 1F / 5;
            }
        }

        maskOpenAnim = MathHelper.clamp_float(maskOpenAnim, 0, 1);
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean isInvisible()
    {
        return true;
    }

    @Override
    public <T> void set(SHData<T> data, T value)
    {
    }

    @Override
    public <T> T get(SHData<T> data)
    {
        if (data == SHData.MASK_OPEN)
        {
            return (T) Boolean.valueOf(displayStand.isRedstonePowered);
        }
        else if (data == SHData.MASK_OPEN_TIMER)
        {
            return (T) Byte.valueOf((byte) maskOpenTimer);
        }
        else if (data == SHData.MASK_OPEN_TIMER2)
        {
            return (T) Float.valueOf(maskOpenAnim);
        }
        else if (data == SHData.MASK_OPEN_TIMER2.getPrevData())
        {
            return (T) Float.valueOf(prevMaskOpenAnim);
        }

        ItemStack stack = displayStand.getStackInSlot(4);

        if (stack != null)
        {
            if (data == SHData.EQUIPPED_QUIVER && stack.getItem() == ModItems.quiver)
            {
                return (T) new Quiver(this, stack);
            }
            else if (data == SHData.EQUIPPED_QUIVER_SLOT && stack.getItem() == ModItems.quiver || data == SHData.EQUIPPED_TACHYON_DEVICE_SLOT && stack.getItem() == ModItems.tachyonDevice)
            {
                return (T) Byte.valueOf((byte) 4);
            }
            else if (data == SHData.HAS_CPT_AMERICAS_SHIELD && stack.getItem() == ModItems.captainAmericasShield)
            {
                byte b = (byte) (stack.isItemEnchanted() ? 2 : 1);

                if (ItemCapsShield.isStealth(stack))
                {
                    b |= 4;
                }

                return (T) Byte.valueOf(b);
            }
            else if (data == SHData.HAS_DEADPOOLS_SWORDS && stack.getItem() == ModItems.deadpoolsSwords || data == SHData.HAS_PROMETHEUS_SWORD && stack.getItem() == ModItems.prometheusSword)
            {
                return (T) Byte.valueOf((byte) (stack.isItemEnchanted() ? 2 : 1));
            }
        }

        return data.getDefault();
    }
}
