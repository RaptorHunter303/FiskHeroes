package com.fiskmods.heroes.common.tileentity;

import java.util.List;

import com.fiskmods.heroes.common.achievement.SHAchievements;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Heroes;
import com.fiskmods.heroes.common.network.MessageTileTrigger.ITileDataCallback;
import com.fiskmods.heroes.util.FabricatorHelper;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntitySuitFabricator extends TileEntityContainer implements ITileDataCallback
{
    public final IInventory output = new InventoryBasic("Result", true, 4);

    protected Hero selectedHero;
    public int energy;

    @Override
    public void updateEntity()
    {
        if (selectedHero == Heroes.spodermen && !SHHelper.hasSpodermenAccess(null))
        {
            selectedHero = null;
        }
        
        int energy = FabricatorHelper.getTotalEnergy(this);
        boolean flag = selectedHero != null && energy >= FabricatorHelper.getMaterialCost(selectedHero);

        for (int slot = 0; slot < 4; ++slot)
        {
            output.setInventorySlotContents(slot, flag ? selectedHero.getDefault().createArmor(slot) : null);
        }

        if (worldObj.getTotalWorldTime() % 80L == 0L)
        {
            if (energy > 9000)
            {
                float radius = 6;
                AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(radius, radius, radius);
                List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);

                for (EntityPlayer player : list)
                {
                    player.triggerAchievement(SHAchievements.OVER_9000);
                }
            }
        }
    }

    public Hero getHero()
    {
        return selectedHero;
    }

    @Override
    public int getSizeInventory()
    {
        return 6;
    }

    @Override
    public String getInventoryName()
    {
        return "gui.suit_fabricator";
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        energy = nbt.getInteger("MaterialEnergy");

        if (nbt.hasKey("Selected"))
        {
            selectedHero = Hero.getHeroFromName(nbt.getString("Selected"));
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setInteger("MaterialEnergy", energy);

        if (selectedHero != null)
        {
            nbt.setString("Selected", selectedHero.getName());
        }
    }

    @Override
    public void receive(EntityPlayer sender, ByteBuf buf)
    {
        Hero hero = Hero.REGISTRY.getObject(ByteBufUtils.readUTF8String(buf));

        if (hero != null && !hero.isCosmic() && (!hero.isHidden() || hero == Heroes.spodermen && SHHelper.hasSpodermenAccess(sender)) && FabricatorHelper.getMaxTier(sender) >= hero.getTier().tier)
        {
            selectedHero = hero;
        }
    }
}
