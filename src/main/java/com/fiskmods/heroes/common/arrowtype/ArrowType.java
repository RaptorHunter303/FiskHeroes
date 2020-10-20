package com.fiskmods.heroes.common.arrowtype;

import java.util.List;
import java.util.function.BiFunction;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.common.book.widget.IItemListEntry;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.arrow.DefaultArrowData;
import com.fiskmods.heroes.common.data.arrow.IArrowData;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.item.ItemTrickArrow;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.common.registry.FiskRegistryEntry;
import com.fiskmods.heroes.common.registry.FiskRegistryNumerical;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ArrowType<T extends EntityTrickArrow> extends FiskRegistryEntry<ArrowType<T>> implements IItemListEntry
{
    public static final FiskRegistryNumerical<ArrowType<EntityTrickArrow>> REGISTRY = new FiskRegistryNumerical<>(FiskHeroes.MODID, "normal_arrow");

    public static void register(int id, String key, ArrowType value)
    {
        REGISTRY.addObject(id, key, value);
    }

    public static ArrowType getArrowFromName(String key)
    {
        return REGISTRY.getObject(key);
    }

    public static String getNameForArrow(ArrowType value)
    {
        return REGISTRY.getNameForObject(value);
    }

    public static int getIdFromArrow(ArrowType value)
    {
        return value == null ? 0 : REGISTRY.getIDForObject(value);
    }

    public static ArrowType getArrowById(int id)
    {
        return REGISTRY.getObjectById(id);
    }

    public final Class<T> entity;

    private BiFunction<ArrowType<T>, T, IArrowData> dataFactory = DefaultArrowData::new;
    private T dummyEntity;

    private float damageMultiplier = 1F;
    private float velocityFactor = 1.5F;
    private float gravityFactor = 0.05F;

    @SideOnly(Side.CLIENT)
    public IIcon icon;

    public ArrowType(Class<T> clazz)
    {
        entity = clazz;
    }

    public BiFunction<ArrowType<T>, T, IArrowData> getDataFactory()
    {
        return dataFactory;
    }

    public ArrowType setDataFactory(BiFunction<ArrowType<T>, T, IArrowData> func)
    {
        dataFactory = func != null ? func : (t, e) -> null;
        return this;
    }

    public ArrowType setDefaultDataFactory(ArrowType type)
    {
        return setDataFactory((t, e) -> new DefaultArrowData(type, e));
    }

    public float getDamageMultiplier()
    {
        return damageMultiplier;
    }

    public float getVelocityFactor()
    {
        return velocityFactor;
    }

    public float getGravityFactor()
    {
        return gravityFactor;
    }

    public ArrowType setDamage(float multiplier)
    {
        damageMultiplier = multiplier;
        return this;
    }

    public ArrowType setVelocity(float factor)
    {
        velocityFactor = factor;
        return this;
    }

    public ArrowType setGravity(float factor)
    {
        gravityFactor = factor;
        return this;
    }

    public T newInstance(World world)
    {
        try
        {
            return entity.getConstructor(World.class).newInstance(world);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public T newInstance(World world, double x, double y, double z)
    {
        try
        {
            return entity.getConstructor(World.class, double.class, double.class, double.class).newInstance(world, x, y, z);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public T newInstance(World world, EntityLivingBase shooter, float speed, boolean horizontal)
    {
        try
        {
            return entity.getConstructor(World.class, EntityLivingBase.class, float.class, boolean.class).newInstance(world, shooter, speed, horizontal);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public T shoot(World world, EntityLivingBase shooter, IBlockSource source, ItemStack bow, ItemStack arrow, float f)
    {
        if (!world.isRemote)
        {
            T entity = null;

            if (shooter != null)
            {
                entity = newInstance(world, shooter, f * Rule.VELMULT_ARROW.getHero(shooter), SHData.HORIZONTAL_BOW.get(shooter));
            }
            else if (source != null)
            {
                IPosition pos = BlockDispenser.func_149939_a(source);
                entity = newInstance(world, pos.getX(), pos.getY(), pos.getZ());
            }

            if (entity != null)
            {
                arrow = arrow.copy();
                arrow.stackSize = 1;

                onShoot(shooter, source, entity, bow, arrow, f);

                if (source != null)
                {
                    EnumFacing facing = BlockDispenser.func_149937_b(source.getBlockMetadata());
                    entity.setThrowableHeading(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ(), 4 * entity.getVelocityFactor(), 2);
                }

                return entity;
            }
        }

        return null;
    }

    public void onShoot(EntityLivingBase shooter, IBlockSource source, T entity, ItemStack bow, ItemStack arrow, float f)
    {
        entity.setArrowItem(arrow);
        entity.setArrowId(getIdFromArrow(this));

        if (shooter != null)
        {
            HeroIteration iter = SHHelper.getHeroIter(shooter);

            entity.setHero(iter != null ? iter.getName() : null);
            entity.setDamage(SHAttributes.ARROW_DAMAGE.get(shooter, entity.getDamage() * Rule.DMGMULT_ARROW.get(shooter, iter)));

            if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            {
                entity.canBePickedUp = 2;
            }
        }
        else if (source != null)
        {
            entity.setDamage(entity.getDamage() * 2);
            entity.canBePickedUp = 1;
        }

        if (arrow.hasTagCompound() && arrow.getTagCompound().getBoolean(ItemTrickArrow.NO_ENTITY))
        {
            entity.setNoEntity();
        }

        if (f == 1)
        {
            entity.setIsCritical(true);
        }

        int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, bow);
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, bow);

        if (power > 0)
        {
            entity.setDamage(entity.getDamage() + power * 0.5 + 0.5);
        }

        if (punch > 0)
        {
            entity.setKnockbackStrength(punch);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, bow) > 0)
        {
            entity.setFire(100);
        }

        entity.worldObj.spawnEntityInWorld(entity);
    }

    public T getDummyEntity(EntityLivingBase shooter)
    {
        if (dummyEntity == null)
        {
            dummyEntity = newInstance(shooter.worldObj, 0, 0, 0);
            dummyEntity.setArrowId(getIdFromArrow(this));
            dummyEntity.setArrowItem(makeItem());
        }

        HeroIteration iter = SHHelper.getHeroIter(shooter);
        dummyEntity.setHero(iter != null ? iter.getName() : null);
        dummyEntity.ticksExisted = shooter.ticksExisted;
        dummyEntity.worldObj = shooter.worldObj;

        return dummyEntity;
    }

    public ItemStack makeItem(int stackSize)
    {
        return new ItemStack(ModItems.trickArrow, stackSize, getIdFromArrow(this));
    }

    public ItemStack makeItem()
    {
        return makeItem(1);
    }

    public boolean matches(ItemStack stack)
    {
        return stack.getItem() == ModItems.trickArrow && stack.getItemDamage() == getIdFromArrow(this);
    }

    public boolean canDispense(IBlockSource blockSource, ItemStack itemstack)
    {
        return true;
    }

    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(makeItem());
    }

    public String getTextureName()
    {
        return getDomain() + ":arrows/" + getRegistryName().getResourcePath() + "_arrow";
    }

    public ItemStack[] onEaten(ItemStack itemstack, World world, EntityPlayer player)
    {
        return new ItemStack[0];
    }

    public boolean isEdible(ItemStack itemstack, EntityPlayer player)
    {
        return false;
    }

    public int getHealAmount(ItemStack itemstack)
    {
        return 0;
    }

    public float getSaturationModifier(ItemStack itemstack)
    {
        return 0;
    }

    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.none;
    }

    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 32;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag)
    {
        ItemStack itemstack1 = ItemTrickArrow.getItem(itemstack);

        if (itemstack1 != null)
        {
            itemstack1.getItem().addInformation(itemstack1, player, list, flag);
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack itemstack, int pass)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getItemIcon(int damage, int pass)
    {
        return icon;
    }

    @SideOnly(Side.CLIENT)
    public int getRenderPasses(int metadata)
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public int getColor(ItemStack itemstack, int pass)
    {
        return -1;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        icon = iconRegister.registerIcon(getTextureName());
    }

    @Override
    public void getListItems(Item item, CreativeTabs tab, List list)
    {
        list.add(makeItem());
    }

    @Override
    public String getPageLink(ItemStack itemstack)
    {
        return itemstack.getUnlocalizedName();
    }
}
