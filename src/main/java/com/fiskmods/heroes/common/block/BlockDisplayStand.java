package com.fiskmods.heroes.common.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.render.block.RenderBlockDisplayStand;
import com.fiskmods.heroes.common.BlockStack;
import com.fiskmods.heroes.common.DimensionalCoords;
import com.fiskmods.heroes.common.item.IEquipmentItem;
import com.fiskmods.heroes.common.item.ItemDisplayCase.DisplayCase;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.network.MessageUpdateArmor;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.common.tileentity.TileEntityDisplayStand;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHTileHelper;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class BlockDisplayStand extends BlockContainer
{
    private final boolean topHalf;

    public static BlockStack renderingBlock;
    private static boolean migrating;

    public BlockDisplayStand(boolean top)
    {
        super(Material.rock);
        topHalf = top;

        setHardness(0.5F);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        if (renderingBlock == null || renderingBlock.block instanceof BlockDisplayStand)
        {
            Map<List<AxisAlignedBB>, BlockStack> map = getExtraBounds(world, x, y, z);
            int light = 0;

            for (Map.Entry<List<AxisAlignedBB>, BlockStack> e : map.entrySet())
            {
                if (e.getValue() != null)
                {
                    light = Math.max(light, e.getValue().block.getLightValue());
                }
            }

            return light;
        }

        return renderingBlock.block.getLightValue();
    }

    @Override
    public float getAmbientOcclusionLightValue()
    {
        if (renderingBlock != null && !(renderingBlock.block instanceof BlockDisplayStand))
        {
            return renderingBlock.block.getAmbientOcclusionLightValue();
        }

        return super.getAmbientOcclusionLightValue();
    }

    @Override
    public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z)
    {
        if (renderingBlock != null && !(renderingBlock.block instanceof BlockDisplayStand))
        {
            return renderingBlock.block.getMixedBrightnessForBlock(world, x, y, z);
        }

        return super.getMixedBrightnessForBlock(world, x, y, z);
    }

    @Override
    public int getBlockColor()
    {
        if (renderingBlock != null && !(renderingBlock.block instanceof BlockDisplayStand))
        {
            return renderingBlock.block.getBlockColor();
        }

        return super.getBlockColor();
    }

    @Override
    public int getRenderColor(int metadata)
    {
        if (renderingBlock != null && !(renderingBlock.block instanceof BlockDisplayStand))
        {
            return renderingBlock.block.getRenderColor(renderingBlock.metadata);
        }

        return super.getRenderColor(metadata);
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z)
    {
        if (renderingBlock != null && !(renderingBlock.block instanceof BlockDisplayStand))
        {
            return renderingBlock.block.colorMultiplier(world, x, y, z);
        }

        return super.colorMultiplier(world, x, y, z);
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return RenderBlockDisplayStand.RENDER_ID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return topHalf ? 1 : 0;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBounds(world.getBlockMetadata(x, y, z));
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        setBounds(world.getBlockMetadata(x, y, z));
    }

    public void setBounds(int metadata)
    {
        float f = 0.0625F;

        if (metadata < 8)
        {
            setBlockBounds(0, 0, 0, 1, 2, 1);
        }
        else
        {
            setBlockBounds(0, -1, 0, 1, 1, 1);
        }
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
        setBlockBounds(0, 0, 0, 1, 0.0625F, 1);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
    {
        TileEntityDisplayStand tile = getTile(world, x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);
        float f = 0.0625F;
        float f1 = 0;

        if (tile != null && !tile.getCasing().hasBaseplate())
        {
            f1 = f;
        }

        if (metadata == 0 || metadata == 4)
        {
            addBox(f * 4, 0, f * 6, 1 - f * 4, f * 12 - f1, 1 - f * 6, x, y, z, aabb, list);
            addBox(f * 0.5F, f * 12 - f1, f * 6, 1 - f * 0.5F, f * 23.625F - f1, 1 - f * 6, x, y, z, aabb, list);
            addBox(f * 4, f * 23.625F - f1, f * 4, 1 - f * 4, f * 31.125F - f1, 1 - f * 4, x, y, z, aabb, list);
        }
        else if (metadata == 2 || metadata == 6)
        {
            addBox(f * 6, 0, f * 4, 1 - f * 6, f * 12 - f1, 1 - f * 4, x, y, z, aabb, list);
            addBox(f * 6, f * 12 - f1, f * 0.5F, 1 - f * 6, f * 23.625F - f1, 1 - f * 0.5F, x, y, z, aabb, list);
            addBox(f * 4, f * 23.625F - f1, f * 4, 1 - f * 4, f * 31.125F - f1, 1 - f * 4, x, y, z, aabb, list);
        }
        else if (metadata < 8)
        {
            addBox(f * 4, f, f * 4 - f1, 1 - f * 4, f * 31.125F - f1, 1 - f * 4, x, y, z, aabb, list);
        }

        Map<List<AxisAlignedBB>, BlockStack> map = getExtraBounds(world, x, y, z);

        for (Map.Entry<List<AxisAlignedBB>, BlockStack> e : map.entrySet())
        {
            BlockStack stack = e.getValue();

            if (stack == null || stack.block.getMaterial() == Material.air || stack.block.getCollisionBoundingBoxFromPool(world, x, y, z) == null)
            {
                continue;
            }

            for (AxisAlignedBB aabb1 : e.getKey())
            {
                float f2 = 0.001F;
                aabb1 = aabb1.contract(f2, f2, f2);
                addBox(aabb1.minX, aabb1.minY, aabb1.minZ, aabb1.maxX, aabb1.maxY, aabb1.maxZ, x, y, z, aabb, list);
            }
        }
    }

    public void addBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int x, int y, int z, AxisAlignedBB aabb, List list)
    {
        AxisAlignedBB aabb1 = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ).offset(x, y, z);

        if (aabb1.intersectsWith(aabb))
        {
            list.add(aabb1);
        }
    }

    public static TileEntityDisplayStand getTile(IBlockAccess world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        TileEntity tile = world.getTileEntity(x, y - (metadata < 8 ? 0 : 1), z);

        if (tile instanceof TileEntityDisplayStand)
        {
            return (TileEntityDisplayStand) tile;
        }

        return null;
    }

    public static Map<List<AxisAlignedBB>, BlockStack> getExtraBounds(IBlockAccess world, int x, int y, int z)
    {
        TileEntityDisplayStand tile = getTile(world, x, y, z);

        if (tile != null)
        {
            return tile.getCasing().getBounds(world.getBlockMetadata(x, y, z));
        }

        return new HashMap<>();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (SHTileHelper.getTileBase(world.getTileEntity(x, y, z)) instanceof TileEntityDisplayStand)
        {
            TileEntityDisplayStand tile = (TileEntityDisplayStand) SHTileHelper.getTileBase(world.getTileEntity(x, y, z));
            ItemStack heldItem = player.getHeldItem();

            x = tile.xCoord;
            y = tile.yCoord;
            z = tile.zCoord;

            if (!player.canPlayerEdit(x, y, z, side, heldItem))
            {
                return false;
            }

            if (heldItem != null && heldItem.getItem() == ModItems.displayCase)
            {
                if (!world.isRemote)
                {
                    ItemStack displayCase = tile.getStackInSlot(5);

                    if (ItemStack.areItemStackTagsEqual(heldItem, displayCase))
                    {
                        tile.setInventorySlotContents(5, null);

                        if (!player.capabilities.isCreativeMode && !player.inventory.addItemStackToInventory(displayCase))
                        {
                            player.dropPlayerItemWithRandomChoice(displayCase, false);
                        }
                    }
                    else
                    {
                        ItemStack itemstack1 = heldItem.copy();
                        itemstack1.stackSize = 1;

                        tile.setInventorySlotContents(5, itemstack1);

                        if (!player.capabilities.isCreativeMode)
                        {
                            if (--heldItem.stackSize <= 0)
                            {
                                player.setCurrentItemOrArmor(0, displayCase);
                            }
                            else if (!player.inventory.addItemStackToInventory(displayCase))
                            {
                                player.dropPlayerItemWithRandomChoice(displayCase, false);
                            }
                        }
                    }

                    onNeighborBlockChange(world, x, y, z, this);
                    tile.markDirty();
                }
            }
            else if (heldItem != null && heldItem.getItem() == Items.dye)
            {
                if (!world.isRemote && tile.setColor(15 - heldItem.getItemDamage()))
                {
                    tile.markDirty();

                    if (!player.capabilities.isCreativeMode && --heldItem.stackSize <= 0)
                    {
                        player.setCurrentItemOrArmor(0, null);
                    }
                }
            }
            else
            {
                if (!player.isSneaking())
                {
                    player.openGui(FiskHeroes.MODID, 2, world, x, y, z);
                }
                else if (!world.isRemote)
                {
                    boolean dirty = false;

                    for (int i = 0; i < 4; ++i)
                    {
                        ItemStack itemstack1 = player.getCurrentArmor(i);
                        ItemStack itemstack2 = tile.getStackInSlot(3 - i);

                        if (!ItemStack.areItemStacksEqual(itemstack1, itemstack2))
                        {
                            player.setCurrentItemOrArmor(1 + i, ItemStack.copyItemStack(itemstack2));
                            tile.setInventorySlotContents(3 - i, ItemStack.copyItemStack(itemstack1));
                            dirty = true;
                        }
                    }

                    if (dirty)
                    {
                        SHNetworkManager.wrapper.sendTo(new MessageUpdateArmor(player), (EntityPlayerMP) player);
                        tile.markDirty();
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player)
    {
        super.onBlockClicked(world, x, y, z, player);

        if (!world.isRemote && SHTileHelper.getTileBase(world.getTileEntity(x, y, z)) instanceof TileEntityDisplayStand)
        {
            TileEntityDisplayStand tile = (TileEntityDisplayStand) SHTileHelper.getTileBase(world.getTileEntity(x, y, z));
            ItemStack heldItem = player.getHeldItem();

            x = tile.xCoord;
            y = tile.yCoord;
            z = tile.zCoord;

            if (!player.canPlayerEdit(x, y, z, 0, heldItem))
            {
                return;
            }

            if (player.isSneaking())
            {
                if (!tile.isOwner(player))
                {
                    player.addChatMessage(new ChatComponentTranslation("message.displayStand.notOwner").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
                    return;
                }

                ItemStack displayEquipped = tile.getStackInSlot(4);
                ItemStack displayHeld = tile.getStackInSlot(6);

                if (heldItem == null && displayHeld == null || heldItem != null && heldItem.getItem() instanceof IEquipmentItem && ((IEquipmentItem) heldItem.getItem()).canEquip(heldItem, tile) && (displayEquipped == null || displayHeld != null))
                {
                    if (!ItemStack.areItemStacksEqual(displayEquipped, heldItem))
                    {
                        player.setCurrentItemOrArmor(0, ItemStack.copyItemStack(displayEquipped));
                        tile.setInventorySlotContents(4, ItemStack.copyItemStack(heldItem));
                        tile.markDirty();
                    }
                }
                else
                {
                    if (!ItemStack.areItemStacksEqual(displayHeld, heldItem))
                    {
                        player.setCurrentItemOrArmor(0, ItemStack.copyItemStack(displayHeld));
                        tile.setInventorySlotContents(6, ItemStack.copyItemStack(heldItem));
                        tile.markDirty();
                    }
                }
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata)
    {
        if (!migrating)
        {
            FiskServerUtils.dropItems(world, x, y, z);
            world.func_147453_f(x, y, z, block);
        }

        super.breakBlock(world, x, y, z, block, metadata);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if (!migrating)
        {
            int metadata = world.getBlockMetadata(x, y, z);

            if (metadata >= 8)
            {
                if (world.getBlock(x, y - 1, z) != ModBlocks.displayStand)
                {
                    world.setBlockToAir(x, y, z);
                    breakBlock(world, x, y, z, this, metadata);
                }
            }
            else if (!(world.getBlock(x, y + 1, z) instanceof BlockDisplayStand))
            {
                world.setBlockToAir(x, y, z);
                breakBlock(world, x, y, z, this, metadata);
            }

            onReceiveUpdate(world, x, y, z);
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        onReceiveUpdate(world, x, y, z);
    }

    public void onReceiveUpdate(World world, int x, int y, int z)
    {
        TileEntity tileentity = world.getTileEntity(x, y, z);

        if (tileentity instanceof TileEntityDisplayStand && SHTileHelper.getTileBase(tileentity) == tileentity)
        {
            TileEntityDisplayStand tile = (TileEntityDisplayStand) tileentity;

            if (!world.isRemote)
            {
                boolean flag = world.isBlockIndirectlyGettingPowered(tile.xCoord, tile.yCoord, tile.zCoord);

                if (tile.isRedstonePowered != flag)
                {
                    tile.isRedstonePowered = flag;
                    tile.markBlockForUpdate();
                }
            }
        }
    }

    public static void migrate(World world, int x, int y, int z, Block newBlock)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        TileEntity tile = world.getTileEntity(x, y, z);
        migrating = true;
        FiskHeroes.LOGGER.info("Migrating pre-1.1 Display Stand at {}", new DimensionalCoords(tile));
        world.setBlock(x, y, z, newBlock);
        migrating = false;
        world.setBlockMetadataWithNotify(x, y, z, metadata, 2);

        if (tile != null)
        {
            tile.validate();
            world.setTileEntity(x, y, z, tile);
            tile.updateContainingBlockInfo();
        }
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return y < world.getHeight() - 1 && super.canPlaceBlockAt(world, x, y, z) && super.canPlaceBlockAt(world, x, y + 1, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack)
    {
        int rotation = MathHelper.floor_double(entity.rotationYaw * 8F / 360F + 4.5) & 7;

        migrating = true;
        placeAt(world, x, y, z, ModBlocks.displayStand, entity, itemstack, rotation);
        placeAt(world, x, y + 1, z, ModBlocks.displayStandTop, entity, itemstack, rotation + 8);
        migrating = false;
    }

    private void placeAt(World world, int x, int y, int z, Block block, EntityLivingBase entity, ItemStack itemstack, int metadata)
    {
        if (world.setBlock(x, y, z, block, metadata, 2))
        {
            TileEntityDisplayStand tile = getTile(world, x, y, z);

            if (tile != null)
            {
                tile.setColor(itemstack.getItemDamage());
                GameProfile profile = null;

                if (itemstack.hasTagCompound())
                {
                    NBTTagCompound nbt = itemstack.getTagCompound();

                    if (nbt.hasKey("Username", NBT.TAG_COMPOUND))
                    {
                        profile = NBTUtil.func_152459_a(nbt.getCompoundTag("Username"));
                    }
                    else if (nbt.hasKey("Username", NBT.TAG_STRING) && nbt.getString("Username").length() > 0)
                    {
                        profile = new GameProfile(null, nbt.getString("Username"));
                    }
                }

                tile.setUsername(profile);
                tile.setOwner(entity);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z)
    {
        return Item.getItemFromBlock(ModBlocks.displayStand);
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z)
    {
        TileEntityDisplayStand tile = getTile(world, x, y, z);

        if (tile != null)
        {
            return tile.getColor();
        }

        return 0;
    }

    public void writeToItemStack(TileEntityDisplayStand tile, ItemStack itemstack)
    {
        itemstack.setItemDamage(tile.getColor());

        if (tile.hasCustomInventoryName())
        {
            if (!itemstack.hasTagCompound())
            {
                itemstack.setTagCompound(new NBTTagCompound());
            }

            NBTTagCompound compound = new NBTTagCompound();
            NBTUtil.func_152460_a(compound, tile.getUsername());

            itemstack.getTagCompound().setTag("Username", compound);
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        ItemStack itemstack = super.getPickBlock(target, world, x, y, z, player);
        TileEntityDisplayStand tile = getTile(world, x, y, z);

        if (tile != null)
        {
            writeToItemStack(tile, itemstack);
        }

        return itemstack;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
    {
        int metadata = world.getBlockMetadata(x, y, z);

        if (!world.isRemote && (player == null || !player.capabilities.isCreativeMode))
        {
            ItemStack itemstack = new ItemStack(ModBlocks.displayStand, 1, damageDropped(metadata));
            TileEntityDisplayStand tile = getTile(world, x, y, z);

            if (tile != null)
            {
                writeToItemStack(tile, itemstack);
            }

            float f = 0.7F;
            double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
            double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
            double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemstack);

            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }

        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 0;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
        Map<List<AxisAlignedBB>, BlockStack> map = getExtraBounds(world, x, y, z);

        for (Map.Entry<List<AxisAlignedBB>, BlockStack> e : map.entrySet())
        {
            if (e.getValue() == null)
            {
                continue;
            }

            BlockStack stack = e.getValue();
            AxisAlignedBB aabb1 = entity.boundingBox.addCoord(-x, -y, -z);

            for (AxisAlignedBB aabb : e.getKey())
            {
                if (aabb.intersectsWith(aabb1))
                {
                    stack.block.onEntityCollidedWithBlock(world, x, y, z, entity);
                    break;
                }
            }
        }

        super.onEntityCollidedWithBlock(world, x, y, z, entity);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity)
    {
        Map<List<AxisAlignedBB>, BlockStack> map = getExtraBounds(world, x, y, z);

        for (Map.Entry<List<AxisAlignedBB>, BlockStack> e : map.entrySet())
        {
            if (e.getValue() == null)
            {
                continue;
            }

            BlockStack stack = e.getValue();
            AxisAlignedBB aabb1 = entity.boundingBox.addCoord(-x, -y, -z);

            aabb1.maxY = aabb1.minY;
            aabb1 = aabb1.expand(0, 0.001F, 0);

            for (AxisAlignedBB aabb : e.getKey())
            {
                if (aabb.intersectsWith(aabb1))
                {
                    stack.block.onEntityWalking(world, x, y, z, entity);
                    break;
                }
            }
        }

        super.onEntityWalking(world, x, y, z, entity);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityDisplayStand tile = getTile(world, x, y, z);

        if (tile != null)
        {
            DisplayCase casing = tile.getCasing();

            if (side == 0 || side == 1)
            {
                BlockStack stack = side == 0 ? casing.bottom : casing.top;

                if (stack != null)
                {
                    return stack.block.getIcon(side, stack.metadata);
                }
            }
            else if (tile.getStackInSlot(5) != null)
            {
                int metadata = world.getBlockMetadata(x, y, z);
                int rotation = metadata % 8;
                int[] sides = {3, 4, 2, 5};
                boolean front = side == sides[(rotation / 2 + 0) % 4] || rotation % 2 == 1 && side == sides[(rotation / 2 + 1) % 4];

                BlockStack stack = front ? casing.front : casing.walls;
                stack = stack == null ? casing.walls : stack;

                if (stack != null)
                {
                    return stack.block.getIcon(side, stack.metadata);
                }
            }
        }

        return side == 0 ? Blocks.stone_slab.getIcon(side, 0) : Blocks.wool.getIcon(side, 0);
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        return Blocks.stone_slab.getIcon(side, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
        for (int i = 0; i < ItemDye.field_150921_b.length; ++i)
        {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityDisplayStand();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
    }
}
