package com.fiskmods.heroes.common.hero;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.SHConstants;
import com.fiskmods.heroes.client.model.ModelBipedMultiLayer;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.common.BackupMap;
import com.fiskmods.heroes.common.book.widget.IItemListEntry;
import com.fiskmods.heroes.common.entity.attribute.AttributeWrapper;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.Weakness;
import com.fiskmods.heroes.common.item.IDualItem;
import com.fiskmods.heroes.common.item.ITachyonCharged;
import com.fiskmods.heroes.common.item.ItemTachyonDevice;
import com.fiskmods.heroes.common.item.ModItems;
import com.fiskmods.heroes.pack.HeroTextureMap;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemHeroArmor extends ItemArmor implements ISpecialArmor, ITachyonCharged, IItemListEntry
{
    public static final String TAG_HERO = "HeroType";
    public static final String TAG_ID_TEMP = "Temp";
    public static boolean hideStats = false;

    @SideOnly(Side.CLIENT)
    private Map<String, IIcon> icons;
    BackupMap<String, String> registry = new BackupMap<>();

    public ItemHeroArmor(int armorPiece)
    {
        super(ModItems.MATERIAL_SUPERHERO, 4, armorPiece);
        setMaxDamage(1024);
        setHasSubtypes(true);
    }

    public String getArmorType(ItemStack itemstack)
    {
        HeroIteration iter = get(itemstack);

        if (iter != null)
        {
            String s = iter.getArmorOverride(armorType);

            if (!StringUtils.isNullOrEmpty(s))
            {
                return s;
            }

            return registry.getOrDefault(Hero.getNameForHero(iter.hero), "Unknown");
        }

        return "Unknown";
    }

    @Override
    public int getTachyonMaxCharge(ItemStack itemstack)
    {
        return SHConstants.MAX_CHARGE_ARMOR;
    }

    @Override
    public boolean isTachyonBattery(ItemStack itemstack)
    {
        return false;
    }

    @Override
    public boolean renderTachyonBar(ItemStack itemstack)
    {
        return ItemTachyonDevice.getCharge(itemstack) > 0;
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemstack)
    {
        HeroIteration iter = get(itemstack);

        if (iter == null)
        {
            return super.getItemStackDisplayName(itemstack);
        }

        String name = iter.hero.getLocalizedName();
        String key = "item.superhero_armor.name";

        if (name.contains("/") && itemstack != null)
        {
            name = name.substring(0, name.indexOf("/"));
        }

        List<String> args = Lists.newArrayList(name, StatCollector.translateToLocal(getArmorType(itemstack)));

        if (iter.hero.getVersion() != null)
        {
            key += ".version";
            args.add(StatCollector.translateToLocal(iter.hero.getVersion()));
        }

        if (name.toLowerCase(Locale.ROOT).endsWith("s"))
        {
            key += ".alt";
        }

        return StatCollector.translateToLocalFormatted(key, args.toArray()).trim();
    }

    @Override
    public boolean getIsRepairable(ItemStack itemstack1, ItemStack itemstack2)
    {
        return itemstack2.getItem() == ModItems.tutridiumGem || super.getIsRepairable(itemstack1, itemstack2);
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot)
    {
        return new ArmorProperties(0, 1F / 100, armor.getMaxDamage() + 1 - armor.getItemDamage());
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot)
    {
        return 0;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
    {
        if (SHHelper.canArmorBlock(entity, source))
        {
            HeroIteration iter = get(stack);
            AttributeWrapper wrapper;

            if (iter != null && ((wrapper = SHAttributes.getAttribute(entity, iter.hero, SHAttributes.DAMAGE_REDUCTION)) == null || wrapper.getValue(1) <= 1))
            {
                return;
            }

            stack.damageItem((int) Math.max(damage / 4.0F, 1.0F), entity);
        }
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        HeroIteration iter = get(stack);

        if (iter != null)
        {
            ResourceLocation location = HeroRenderer.get(iter).getTexture(stack, entity, slot);

            if (location != null)
            {
                return location.toString();
            }
        }

        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
    {
        HeroIteration iter = get(itemstack);

        if (iter != null)
        {
            String s = iter.hero.getLocalizedName();

            if (s.contains("/"))
            {
                String name = s.substring(s.indexOf("/") + 1);
                list.add(EnumChatFormatting.WHITE + name);
            }

            if (!iter.isDefault())
            {
                list.add(EnumChatFormatting.DARK_PURPLE + iter.getLocalizedIterName());
            }

            Set<Ability> abilities = iter.hero.getAbilities();
            Set<Weakness> weaknesses = iter.hero.getWeaknesses();
            list.add(I18n.format("tooltip.tier", iter.hero.getTier().tier));

            if (renderTachyonBar(itemstack))
            {
                list.add(ItemTachyonDevice.getChargeForDisplay(itemstack));
                list.add("");
            }

            if (!hideStats)
            {
                if (abilities.size() > 0 || weaknesses.size() > 0 || !getAttributeModifiers(itemstack).isEmpty())
                {
                    list.add(I18n.format("tooltip.fullSet"));
                }

                if (advanced)
                {
                    list.add(EnumChatFormatting.DARK_GRAY + iter.getName());
                }

                if (abilities.size() > 0 || weaknesses.size() > 0)
                {
                    list.add("");
                }

                for (Ability ability : abilities)
                {
                    list.add(EnumChatFormatting.GREEN + "* " + ability.getLocalizedName());
                }

                for (Weakness weakness : weaknesses)
                {
                    list.add(EnumChatFormatting.YELLOW + "* " + weakness.getLocalizedName());
                }

                if (itemstack.isItemEnchanted())
                {
                    list.add("");
                }
            }
            else
            {
                if (advanced)
                {
                    list.add(EnumChatFormatting.DARK_GRAY + iter.getName());
                }

                list.add(EnumChatFormatting.DARK_GRAY + EnumChatFormatting.ITALIC.toString() + StatCollector.translateToLocal("tooltip.moreInfo"));
            }
        }
        else if (advanced)
        {
            list.add(EnumChatFormatting.DARK_GRAY + (itemstack.hasTagCompound() ? itemstack.getTagCompound().getString(TAG_HERO) : null));
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (String s : registry.keySet())
        {
            Hero hero = Hero.getHeroFromName(s);

            if (hero != null && !hero.isHidden())
            {
                if (tab == null)
                {
                    for (HeroIteration iter : hero.getIterations())
                    {
                        ItemStack stack = create(item, iter, true);

                        if (stack != null)
                        {
                            list.add(stack);
                        }
                    }
                }
                else
                {
                    ItemStack stack = create(item, hero, true);

                    if (stack != null)
                    {
                        list.add(stack);
                    }
                }
            }
        }
    }

    @Override
    public final Multimap getAttributeModifiers(ItemStack stack)
    {
        if (hideStats)
        {
            return HashMultimap.create();
        }

        Multimap multimap = super.getAttributeModifiers(stack);
        HeroIteration iter = get(stack);

        if (iter != null)
        {
            iter.hero.getAttributeModifiers(null, (attr, amount, operation) ->
            {
                multimap.put(attr.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, attr.getAttributeUnlocalizedName(), amount, operation));
            });
        }

        return multimap;
    }

    @Override
    public void getListItems(Item item, CreativeTabs tab, List list)
    {
        getSubItems(item, tab, list);
    }

    @Override
    public String getPageLink(ItemStack itemstack)
    {
        return get(itemstack).hero.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack itemstack, int slot)
    {
        HeroRenderer renderer = HeroRenderer.get(get(itemstack));
        ModelBipedMultiLayer model = renderer.model;

        model.armorSlot = slot;
        model.renderer = renderer;
        renderer.setupRenderLayers(model, slot);

        if (entity != null)
        {
            ItemStack heldItem = entity.getHeldItem();

            model.isSneak = entity.isSneaking();
            model.isRiding = entity.isRiding();
            model.isChild = entity.isChild();
            model.heldItemRight = heldItem != null ? 1 : 0;
            model.heldItemLeft = heldItem != null && heldItem.getItem() instanceof IDualItem ? 1 : 0;

            if (entity instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) entity;
                boolean usingItem = heldItem != null && player.getItemInUseDuration() > 0;

                model.aimedBow = usingItem && heldItem.getItemUseAction() == EnumAction.bow;
                model.heldItemRight = usingItem && heldItem.getItemUseAction() == EnumAction.block ? 3 : model.heldItemRight;
                model.heldItemLeft = usingItem && heldItem.getItem() instanceof IDualItem && heldItem.getItemUseAction() == EnumAction.block ? 3 : model.heldItemLeft;

                if (FiskHeroes.isBattlegearLoaded)
                {
                    ItemStack offhand = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();

                    if (offhand != null)
                    {
                        model.heldItemLeft = 1;

                        if (player.getItemInUseCount() > 0 && player.getItemInUse() == offhand)
                        {
                            EnumAction action = offhand.getItemUseAction();

                            if (action == EnumAction.block)
                            {
                                model.heldItemLeft = 3;
                            }
                            else if (action == EnumAction.bow)
                            {
                                model.aimedBow = true;
                            }

                            heldItem = player.inventory.getCurrentItem();
                            model.heldItemRight = heldItem != null ? 1 : 0;
                        }
                        else if (((IBattlePlayer) player).isBlockingWithShield())
                        {
                            model.heldItemLeft = 3;
                        }
                    }
                }
            }
            else
            {
                model.aimedBow = false;
            }
        }

        return model;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack)
    {
        HeroIteration iter = get(stack);

        if (iter != null)
        {
            return icons.get(iter.getName());
        }

        return super.getIconIndex(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass)
    {
        return getIconIndex(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber()
    {
        return HeroTextureMap.ID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        icons = new HashMap<>();
        itemIcon = iconRegister.registerIcon(getIconString());

        for (Map.Entry<String, String> e : registry.entrySet())
        {
            Hero hero = Hero.getHeroFromName(e.getKey());

            if (hero != null)
            {
                for (HeroIteration iter : hero.getIterations())
                {
                    HeroRenderer renderer = HeroRenderer.get(iter);
                    String s = renderer.getItemIcons()[armorType];

                    if (s != null)
                    {
                        ResourceLocation name = iter.getRegistryName();
                        icons.put(iter.getName(), iconRegister.registerIcon(name.getResourceDomain() + ":" + String.format(s, name.getResourcePath())));
                    }
                }
            }
        }
    }

    public boolean register(Hero hero, String armorType)
    {
        return registry.put(Hero.getNameForHero(hero), armorType) == null;
    }

    public static ItemStack create(Item item, HeroIteration iter, boolean creative)
    {
        if (!(item instanceof ItemHeroArmor))
        {
            return null;
        }
        else if (!iter.hero.hasPieceOfSet(((ItemHeroArmor) item).armorType))
        {
            return null;
        }

        return set(new ItemStack(item), iter, creative);
    }

    public static ItemStack create(Item item, Hero hero, boolean creative)
    {
        return create(item, hero.getDefault(), creative);
    }

    public static HeroIteration get(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            if (stack.getTagCompound().hasKey(TAG_ID_TEMP, NBT.TAG_ANY_NUMERIC))
            {
                String s = HeroIteration.getName(stack.getTagCompound().getInteger(TAG_ID_TEMP));
                stack.getTagCompound().removeTag(TAG_ID_TEMP);

                if (!StringUtils.isNullOrEmpty(s))
                {
                    stack.getTagCompound().setString(TAG_HERO, s);
                    return HeroIteration.lookup(s);
                }
            }
            else if (stack.getTagCompound().hasKey(TAG_HERO, NBT.TAG_STRING))
            {
                return HeroIteration.lookup(stack.getTagCompound().getString(TAG_HERO));
            }
        }

        return null;
    }

    public static ItemStack set(ItemStack stack, HeroIteration iter, boolean creative)
    {
        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (creative)
        {
            stack.getTagCompound().setInteger(TAG_ID_TEMP, HeroIteration.indexOf(iter.getName()));
        }
        else
        {
            stack.getTagCompound().setString(TAG_HERO, iter.getName());
        }

        return stack;
    }
}
