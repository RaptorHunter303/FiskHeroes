package com.fiskmods.heroes.common.block;

import java.util.Random;

import com.fiskmods.heroes.client.sound.SHSounds;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.tileentity.TileEntityParticleCore;
import com.fiskmods.heroes.common.world.ModDimensions;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSubatomicCore extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockSubatomicCore()
    {
        super(Material.rock);
    }

    @Override
    public int getRenderColor(int metadata)
    {
        return CoreType.get(metadata).color;
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        int color = getRenderColor(metadata);

        if (world.getTileEntity(x, y, z) instanceof TileEntityParticleCore)
        {
            TileEntityParticleCore tile = (TileEntityParticleCore) world.getTileEntity(x, y, z);

            switch (tile.getType())
            {
            case BLACK:
                return color;
            case WHITE:
                return SHRenderHelper.getHex(SHRenderHelper.fade(color, CoreType.BLACK.color, -tile.gravity));
            default:
                return SHRenderHelper.getHex(SHRenderHelper.fade(CoreType.WHITE.color, color, tile.gravity / tile.getType().gravity));
            }
        }

        return color;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return CoreType.get(world.getBlockMetadata(x, y, z)).canCollide ? super.getCollisionBoundingBoxFromPool(world, x, y, z) : null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        CoreType type = CoreType.get(world.getBlockMetadata(x, y, z));

        if (world.getTileEntity(x, y, z) instanceof TileEntityParticleCore)
        {
            TileEntityParticleCore tile = (TileEntityParticleCore) world.getTileEntity(x, y, z);
            ItemStack heldItem = player.getHeldItem();

            if (player.canPlayerEdit(x, y, z, side, heldItem) && tile.draining == tile.prevDraining && !world.isRemote)
            {
                ItemStack itemstack = tile.getStackInSlot(side);

                if (heldItem == null && itemstack != null || heldItem != null && tile.canInsertItem(side, heldItem, side))
                {
                    player.setCurrentItemOrArmor(0, itemstack);
                    tile.setInventorySlotContents(side, heldItem);

                    if (heldItem != null)
                    {
                        tile.lastInteraction = player.getCommandSenderName();
                    }

                    tile.markBlockForUpdate();

                    if (!world.isRemote)
                    {
                        world.playSoundAtEntity(player, (heldItem == null ? SHSounds.ITEM_BATTERY_DETACH : SHSounds.ITEM_BATTERY_ATTACH).toString(), 1, 1);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
        if (entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            CoreType type = CoreType.get(world.getBlockMetadata(x, y, z));

            switch (type)
            {
            case STABLE:
                if (player.dimension != ModDimensions.QUANTUM_REALM_ID)
                {
                    world.setBlockToAir(x, y, z);
                }

                SHHelper.setInQuantumRealm(player);
                break;
            case BLACK:
                player.attackEntityFrom(ModDamageSources.BLACK_HOLE, Float.MAX_VALUE);
                break;
            default:
                break;
            }
        }
        else if (entity instanceof EntityLivingBase)
        {
            ((EntityLivingBase) entity).attackEntityFrom(ModDamageSources.BLACK_HOLE, Float.MAX_VALUE);
        }
        else
        {
            entity.setDead();
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata)
    {
        FiskServerUtils.dropItems(world, x, y, z);
        world.func_147453_f(x, y, z, block);

        super.breakBlock(world, x, y, z, block, metadata);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityParticleCore();
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return icons[meta == 0 ? 0 : 1];
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        icons = new IIcon[2];
        icons[0] = iconRegister.registerIcon(getTextureName());
        icons[1] = iconRegister.registerIcon(getTextureName() + "_unstable");
    }

    public static enum CoreType
    {
        STABLE(0, -1, 1, false),
        YELLOW(10, 0xFFFF00, 0.75F, true),
        ORANGE(8, 0xFFAE00, 1, true),
        RED(10, 0xFF0000, 1.25F, true),
        DARK_RED(5, 0x7F0000, 1.5F, true),
        BLACK(1, 0x282828, 2, false),
        WHITE(0, -1, 0, true);

        public final int weight;
        public final int color;
        public final float gravity;
        public final boolean canCollide;

        private CoreType(int weight, int color, float gravity, boolean collision)
        {
            this.weight = weight;
            this.color = color;
            this.gravity = gravity;
            canCollide = collision;
        }

        public static CoreType getRandom(Random rand)
        {
            double totalWeight = 0;

            for (CoreType type : values())
            {
                totalWeight += type.weight;
            }

            double random = rand.nextDouble() * totalWeight;

            for (CoreType type : values())
            {
                random -= type.weight;

                if (random <= 0)
                {
                    return type;
                }
            }

            return STABLE;
        }

        public static CoreType get(int metadata)
        {
            if (metadata >= 0 && metadata < values().length)
            {
                return values()[metadata];
            }

            return STABLE;
        }
    }
}
