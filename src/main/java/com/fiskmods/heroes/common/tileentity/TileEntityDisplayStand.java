package com.fiskmods.heroes.common.tileentity;

import java.util.LinkedList;
import java.util.UUID;

import com.fiskmods.heroes.client.render.LightningData;
import com.fiskmods.heroes.client.render.effect.EffectRenderHandler;
import com.fiskmods.heroes.client.render.effect.EffectTrail;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectTrail;
import com.fiskmods.heroes.common.DimensionalCoords;
import com.fiskmods.heroes.common.block.BlockDisplayStand;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.entity.EntityDisplayMannequin;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.item.IEquipmentItem;
import com.fiskmods.heroes.common.item.ItemDisplayCase;
import com.fiskmods.heroes.common.item.ItemDisplayCase.DisplayCase;
import com.fiskmods.heroes.common.item.ItemTachyonDevice;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.SHTileHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.FakePlayerFactory;

public class TileEntityDisplayStand extends TileEntityContainer implements IMultiTile
{
    public static final int SLOT_CASING = 5;

    private DisplayCase prevCasing;
    private GameProfile displayProfile;
    private int displayColor;

    private UUID owner;

    public boolean isRedstonePowered;
    public boolean fixHatLayer;

    private final GameProfile internalProfile;
    public EntityPlayer fakePlayer;

    public TileEntityDisplayStand()
    {
        internalProfile = new GameProfile(UUID.randomUUID(), String.format("DisplayStand[%s]", new DimensionalCoords(this)));
    }

    @Override
    public void updateEntity()
    {
        DisplayCase casing = getCasing();

        if (prevCasing != casing && (prevCasing == null || casing == null) || !prevCasing.equals(casing))
        {
            prevCasing = casing;
            markDirty();
        }

        if (worldObj.isRemote)
        {
            if (SHTileHelper.getTileBase(this) == this)
            {
                clientTick();
            }
        }
        else
        {
            if (getBlockMetadata() >= 8 && getBlockType() != ModBlocks.displayStandTop)
            {
                BlockDisplayStand.migrate(worldObj, xCoord, yCoord, zCoord, ModBlocks.displayStandTop);
            }

            fakePlayer = FakePlayerFactory.get((WorldServer) worldObj, internalProfile);
        }

        if (fakePlayer != null)
        {
            ++fakePlayer.ticksExisted;

            for (int i = 0; i < 4; ++i)
            {
                fakePlayer.setCurrentItemOrArmor(4 - i, getStackInSlot(i));
            }

            fakePlayer.setCurrentItemOrArmor(0, getStackInSlot(6));

            if (!worldObj.isRemote && SHTileHelper.getTileBase(this) == this)
            {
                ItemStack equipped = getStackInSlot(4);

                if (equipped != null && equipped.getItem() == ModItems.tachyonPrototype && fakePlayer.ticksExisted % 20 == 0 && SpeedsterHelper.isSpeedster(fakePlayer))
                {
                    int charge = ItemTachyonDevice.getCharge(equipped);
                    boolean shouldDrain = false;

                    if (charge > 0)
                    {
                        for (int i = 0; i < 4; ++i)
                        {
                            ItemStack stack = getStackInSlot(i);

                            if (stack != null)
                            {
                                if (ItemTachyonDevice.setCharge(stack, ItemTachyonDevice.getCharge(stack) + 1))
                                {
                                    shouldDrain = true;
                                }
                            }
                        }
                    }

                    if (shouldDrain)
                    {
                        ItemTachyonDevice.setCharge(equipped, --charge);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void clientTick()
    {
        if (fakePlayer == null)
        {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc != null && mc.playerController != null && getWorldObj() != null)
            {
                fakePlayer = new EntityDisplayMannequin(this, mc);
            }
        }
        else
        {
            if (!HeroEffectTrail.lightningData.containsKey(fakePlayer))
            {
                HeroEffectTrail.lightningData.put(fakePlayer, new LinkedList());
            }
            else
            {
                LinkedList<LightningData> list = HeroEffectTrail.lightningData.get(fakePlayer);

                for (int i = 0; i < list.size(); ++i)
                {
                    list.get(i).onUpdate(fakePlayer, worldObj);
                }
            }

            fixHatLayer = false;

            try
            {
                for (int slot = 0; slot < 4; ++slot)
                {
                    HeroIteration iter = SHHelper.getHeroIterFromArmor(fakePlayer, 3 - slot);

                    if (iter != null)
                    {
                        HeroRenderer renderer = HeroRenderer.get(iter);

                        if (renderer.fixHatLayer(fakePlayer, slot))
                        {
                            fixHatLayer = true;
                            break;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            ItemStack equipped = getStackInSlot(4);

            if (equipped != null && equipped.getItem() == ModItems.tachyonPrototype && ItemTachyonDevice.getCharge(equipped) > 0 && SpeedsterHelper.isSpeedster(fakePlayer) && SpeedsterHelper.getTachyonCharge(fakePlayer) < 1)
            {
                SHRenderHelper.doLightningAura(fakePlayer, SpeedsterHelper.getTrailLightning(fakePlayer).getVecColor(fakePlayer), 16, 1, 0.1F);

                if (!Iterables.any(EffectRenderHandler.get(fakePlayer), e -> e.effect == EffectTrail.INSTANCE))
                {
                    EffectRenderHandler.add(fakePlayer, EffectTrail.INSTANCE, -1);
                }
            }

            fakePlayer.onUpdate();
        }
    }

    @Override
    public void invalidate()
    {
        super.invalidate();

        if (fakePlayer != null)
        {
            fakePlayer.setDead();
        }
    }

    public DisplayCase getCasing()
    {
        return ItemDisplayCase.getCasing(getStackInSlot(SLOT_CASING));
    }

    public boolean setColor(int color)
    {
        if (displayColor != color)
        {
            displayColor = color;
            return true;
        }

        return false;
    }

    public int getColor()
    {
        return displayColor;
    }

    public GameProfile getUsername()
    {
        return displayProfile;
    }

    public void setUsername(GameProfile profile)
    {
        displayProfile = profile;

        if (!worldObj.isRemote)
        {
            validateUsername();
        }
    }

    private void validateUsername()
    {
        if (displayProfile != null && !StringUtils.isNullOrEmpty(displayProfile.getName()))
        {
            if (!displayProfile.isComplete() || !displayProfile.getProperties().containsKey("textures"))
            {
                GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152655_a(displayProfile.getName());

                if (profile != null)
                {
                    Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);

                    if (property == null)
                    {
                        profile = MinecraftServer.getServer().func_147130_as().fillProfileProperties(profile, true);
                    }

                    displayProfile = profile;
                    markDirty();
                }
            }
        }
    }

    public UUID getOwner()
    {
        return owner;
    }

    public void setOwner(EntityLivingBase entity)
    {
        if (entity instanceof EntityPlayer)
        {
            GameProfile profile = ((EntityPlayer) entity).getGameProfile();

            if (profile != null && profile.getId() != null)
            {
                owner = profile.getId();
            }
        }
        else if (entity != null)
        {
            owner = entity.getUniqueID();
        }
    }

    public boolean isOwner(EntityLivingBase entity)
    {
        if (owner == null)
        {
            return false;
        }

        if (entity instanceof EntityPlayer)
        {
            GameProfile profile = ((EntityPlayer) entity).getGameProfile();

            if (profile != null && profile.getId() != null)
            {
                return owner.equals(profile.getId());
            }
        }
        else if (entity != null)
        {
            return owner.equals(entity.getUniqueID());
        }

        return false;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1).expand(1, 1, 1);
    }

    @Override
    public int getSizeInventory()
    {
        return 7;
    }

    @Override
    public String getInventoryName()
    {
        return hasCustomInventoryName() ? displayProfile.getName() : "gui.display_stand";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return displayProfile != null && displayProfile.isComplete();
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        displayColor = nbt.getByte("Color");
        isRedstonePowered = nbt.getBoolean("Powered");

        if (nbt.hasKey("Username", NBT.TAG_COMPOUND))
        {
            displayProfile = NBTUtil.func_152459_a(nbt.getCompoundTag("Username"));
        }

        if (nbt.hasKey("Owner", NBT.TAG_COMPOUND))
        {
            NBTTagCompound compound = nbt.getCompoundTag("Owner");

            if (compound.hasKey("UUIDMost", NBT.TAG_LONG) && compound.hasKey("UUIDLeast", NBT.TAG_LONG))
            {
                owner = new UUID(compound.getLong("UUIDMost"), compound.getLong("UUIDLeast"));
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setByte("Color", (byte) displayColor);
        nbt.setBoolean("Powered", isRedstonePowered);

        if (displayProfile != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            NBTUtil.func_152460_a(tag, displayProfile);
            nbt.setTag("Username", tag);
        }

        if (owner != null)
        {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setLong("UUIDMost", owner.getMostSignificantBits());
            compound.setLong("UUIDLeast", owner.getLeastSignificantBits());
            nbt.setTag("Owner", compound);
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (getBlockMetadata() >= 8 || slot != 6 && getStackInSlot(slot) != null)
        {
            return false;
        }

        switch (slot)
        {
        case 4:
            return stack.getItem() instanceof IEquipmentItem && ((IEquipmentItem) stack.getItem()).canEquip(stack, this);
        case SLOT_CASING:
            return stack.getItem() == ModItems.displayCase;
        default:
            return slot == 6 || stack.getItem().isValidArmor(stack, slot, fakePlayer);
        }
    }

    @Override
    public int[] getBaseOffsets(int metadata)
    {
        return new int[] {0, -metadata / 8, 0};
    }
}
