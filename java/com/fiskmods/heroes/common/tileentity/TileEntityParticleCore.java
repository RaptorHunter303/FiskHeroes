package com.fiskmods.heroes.common.tileentity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.particle.SHParticleType;
import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.DimensionalCoords;
import com.fiskmods.heroes.common.achievement.SHAchievements;
import com.fiskmods.heroes.common.block.BlockSubatomicCore.CoreType;
import com.fiskmods.heroes.common.block.ModBlocks;
import com.fiskmods.heroes.common.item.ItemSubatomicBattery;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityParticleCore extends TileEntityContainer implements ISidedInventory
{
    public List<DimensionalCoords> blocks;
    public int ticks;
    public int radius;
    public float gravity;

    public float animationTicks;
    public float prevAnimationTicks;
    public float draining;
    public float prevDraining;

    public String lastInteraction;

    @Override
    public void updateEntity()
    {
        List<Entity> list = worldObj.getEntitiesWithinAABB(Entity.class, getEffectiveBounds());
        CoreType type = getType();

        prevDraining = draining;
        ++ticks;

        if (getBlockType() == ModBlocks.subatomicParticleCore)
        {
            if (type != CoreType.STABLE)
            {
                if (type != CoreType.BLACK && gravity <= -1)
                {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, CoreType.BLACK.ordinal(), 2);
                    setGravity(CoreType.BLACK.gravity);
                    type = getType();

                    worldObj.createExplosion(null, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, radius, true);

                    for (int i = 0; i < getSizeInventory(); ++i)
                    {
                        setInventorySlotContents(i, null);
                    }

                    if (lastInteraction != null)
                    {
                        EntityPlayer player = worldObj.getPlayerEntityByName(lastInteraction);

                        if (player != null)
                        {
                            player.triggerAchievement(SHAchievements.BLACK_HOLE);
                        }
                    }
                }
                else if (type != CoreType.WHITE && gravity <= 0)
                {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, CoreType.WHITE.ordinal(), 2);
                    type = getType();

                    worldObj.playSound(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "random.fizz", 1, 0.8F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.4F, false);
                    double spread = 1.5;

                    for (int i = 0; i < 200; ++i)
                    {
                        SHParticleType.SUBATOMIC_CHARGE.spawn(xCoord + 0.5 + (worldObj.rand.nextDouble() - 0.5) * spread, yCoord + 0.5 + (worldObj.rand.nextDouble() - 0.5) * spread, zCoord + 0.5 + (worldObj.rand.nextDouble() - 0.5) * spread, 0, 0, 0);
                    }
                }
            }

            if (radius > 0)
            {
                if (type != CoreType.BLACK)
                {
                    boolean flag = false;

                    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
                    {
                        ItemStack stack = getStackInSlot(dir.ordinal());

                        if (stack != null && stack.getItem() instanceof ItemSubatomicBattery && ItemSubatomicBattery.getCharge(stack) < 1)
                        {
                            flag = true;

                            if (!worldObj.isRemote)
                            {
                                stack.setItemDamage(stack.getItemDamage() + 1);
                                gravity -= 1F / SHConstants.MAX_SUBATOMIC_RECHARGE;
                            }
                            else
                            {
                                double x = xCoord + 0.5 + dir.offsetX * 0.5;
                                double y = yCoord + 0.5 + dir.offsetY * 0.5;
                                double z = zCoord + 0.5 + dir.offsetZ * 0.5;
                                double spread = radius * gravity;
                                double motion = 0.25 * gravity;

                                for (int i = 0; i < MathHelper.ceiling_float_int(Math.abs(gravity) * radius); ++i)
                                {
                                    double x1 = x + (worldObj.rand.nextDouble() - 0.5) * spread;
                                    double y1 = y + (worldObj.rand.nextDouble() - 0.5) * spread;
                                    double z1 = z + (worldObj.rand.nextDouble() - 0.5) * spread;
                                    Vec3 mot = Vec3.createVectorHelper(x1 - x, y1 - y, z1 - z).normalize();
                                    SHParticleType.SUBATOMIC_CHARGE.spawn(x1, y1, z1, -mot.xCoord * motion, -mot.yCoord * motion, -mot.zCoord * motion);
                                }
                            }
                        }
                    }

                    if (flag && draining < 1)
                    {
                        draining += 1F / 20;
                    }
                    else if (!flag && draining > 0)
                    {
                        draining -= 1F / 10;
                    }

                    draining = MathHelper.clamp_float(draining, 0, 1);

                    if (flag)
                    {
                        if (draining == 1 && prevDraining < 1 && worldObj.isRemote)
                        {
                            FiskHeroes.proxy.playSound(null, "absorb", 0, 0, xCoord, yCoord, zCoord);
                        }
                        else if (draining > 0 && prevDraining == 0 && !worldObj.isRemote)
                        {
                            worldObj.playSoundEffect(xCoord, yCoord, zCoord, SHSounds.ITEM_BATTERY_ON.toString(), 1, 1);
                        }

                        markBlockForUpdate();
                    }
//                    else if (prevDraining == 1 && draining < 1 && !worldObj.isRemote)
//                    {
//                        worldObj.playSoundEffect(xCoord, yCoord, zCoord, SHSounds.ITEM_BATTERY_OFF.toString(), 1, 1);
//                    }
                }

                if (gravity > 0)
                {
                    if (type == CoreType.BLACK)
                    {
                        Vec3 center = Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);

                        if (blocks == null || ticks % 20 == 1)
                        {
                            blocks = new ArrayList<>();
                            int size = radius + 1;

                            for (int i = -size; i <= size; ++i)
                            {
                                for (int j = -size; j <= size; ++j)
                                {
                                    for (int k = -size; k <= size; ++k)
                                    {
                                        double layer = center.distanceTo(center.addVector(i, j, k));

                                        if (layer <= size + 2)
                                        {
                                            Block block = worldObj.getBlock(xCoord + i, yCoord + j, zCoord + k);

                                            if (block == Blocks.air || !(block.getBlockHardness(worldObj, xCoord + i, yCoord + j, zCoord + k) >= 0 || block instanceof BlockLiquid))
                                            {
                                                continue;
                                            }

                                            blocks.add(new DimensionalCoords(xCoord + i, yCoord + j, zCoord + k, 0));
                                        }
                                    }
                                }
                            }

                            blocks.sort(Comparator.comparingDouble(t -> Vec3.createVectorHelper(t.posX + 0.5, t.posY + 0.5, t.posZ + 0.5).distanceTo(center)));
                        }

                        if (!worldObj.isRemote && blocks != null && !blocks.isEmpty() && !list.isEmpty())
                        {
                            DimensionalCoords coords = blocks.get(0);
                            int j = MathHelper.ceiling_double_int(Math.pow(Vec3.createVectorHelper(coords.posX + 0.5, coords.posY + 0.5, coords.posZ + 0.5).distanceTo(center), 3) * radius / 10);

                            for (int i = 0; i < j && !blocks.isEmpty() && worldObj.loadedEntityList.size() < 1000; ++i)
                            {
                                coords = blocks.get(0);

                                EntityFallingBlock entity = new EntityFallingBlock(worldObj, coords.posX + 0.5, coords.posY + 0.5, coords.posZ + 0.5, worldObj.getBlock(coords.posX, coords.posY, coords.posZ), worldObj.getBlockMetadata(coords.posX, coords.posY, coords.posZ));
                                entity.motionY += 0.5F;
                                entity.field_145812_b = -100;
                                entity.field_145813_c = false;

                                worldObj.spawnEntityInWorld(entity);
                                worldObj.setBlock(coords.posX, coords.posY, coords.posZ, Blocks.air);
                                blocks.remove(coords);
                            }
                        }
                    }
                }
            }
        }

        for (Entity entity : list)
        {
            double dist = entity.getDistance(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);

            if (dist < radius * 3)
            {
                double divider = 25 * dist / 2 / gravity;
                entity.motionX -= (entity.posX - xCoord - 0.5) / divider;
                entity.motionY -= (entity.posY - yCoord - 0.5) / divider;
                entity.motionZ -= (entity.posZ - zCoord - 0.5) / divider;
            }

            if (dist <= Math.min(radius / 5, 1) && entity instanceof EntityFallingBlock)
            {
                entity.setDead();
            }
        }

        prevAnimationTicks = animationTicks;
        animationTicks += gravity;
    }

    public CoreType getType()
    {
        return CoreType.get(getBlockMetadata());
    }

    public AxisAlignedBB getEffectiveBounds()
    {
        float range = radius * 3 + 0.0625F;
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(range, range, range);
    }

    public void setRange(int range)
    {
        radius = range;
        markBlockForUpdate();
    }

    public void setGravity(float gravity)
    {
        this.gravity = gravity;
        markBlockForUpdate();
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        radius = nbt.getByte("Range");
        gravity = nbt.hasKey("Gravity", NBT.TAG_ANY_NUMERIC) ? nbt.getFloat("Gravity") : 1;
        draining = nbt.getFloat("Draining");

        if (nbt.hasKey("LastInteraction", NBT.TAG_STRING))
        {
            lastInteraction = nbt.getString("LastInteraction");
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setByte("Range", (byte) radius);
        nbt.setFloat("Gravity", gravity);
        nbt.setFloat("Draining", draining);

        if (lastInteraction != null)
        {
            nbt.setString("LastInteraction", lastInteraction);
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return getEffectiveBounds();
    }

    @Override
    public double getMaxRenderDistanceSquared()
    {
        double dist = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16;
        return dist * dist;
    }

    @Override
    public int getSizeInventory()
    {
        return ForgeDirection.VALID_DIRECTIONS.length;
    }

    @Override
    public String getInventoryName()
    {
        return "Particle Core";
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return new int[] {side};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, int side)
    {
        CoreType type = getType();
        return type != CoreType.STABLE && type.canCollide && item.getItem() instanceof ItemSubatomicBattery;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, int side)
    {
        return true;
    }
}
