package com.fiskmods.heroes.common.book.widget;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fiskmods.heroes.common.book.Book;
import com.fiskmods.heroes.util.TextureHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;

public enum WidgetType
{
    UNKNOWN,
    ITEM(18, 18)
    {
        @Override
        public void initType(Widget widget)
        {
            List<ItemStack> list = new ArrayList<>();
            ItemStack itemstack = Book.parseItem((String) widget.type.handleObject(widget, this));

            if (itemstack != null)
            {
                Item item = itemstack.getItem();

                if (item.getHasSubtypes())
                {
                    List<Integer> addedMetas = new ArrayList<>();
                    List<ItemStack> list1 = new ArrayList<>();
                    item.getSubItems(item, item.getCreativeTab(), list1);

                    for (ItemStack itemstack1 : list1)
                    {
                        if (itemstack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
                        {
                            if (!addedMetas.contains(itemstack1.getItemDamage()))
                            {
                                list.add(itemstack1);
                                addedMetas.add(itemstack1.getItemDamage());
                            }
                        }
                        else if (itemstack.getItemDamage() == itemstack1.getItemDamage())
                        {
                            list.add(itemstack1);
                        }
                    }
                }
                else
                {
                    itemstack.setItemDamage(Math.max(itemstack.getItemDamage(), 0));
                    list.add(itemstack);
                }
            }

            widget.itemstacks = new ItemStack[][] {list.toArray(new ItemStack[list.size()])};
        }
    },
    RECIPE(113, 60)
    {
        @Override
        public void initType(Widget widget)
        {
            ItemStack itemstack = Book.parseItem((String) widget.type.handleObject(widget, this));

            if (itemstack != null)
            {
                if (RECIPE_CACHE.containsKey(itemstack))
                {
                    widget.itemstacks = RECIPE_CACHE.get(itemstack);
                }
                else
                {
                    List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
                    List<ItemStack[]> list = new ArrayList<>();

                    for (IRecipe recipe : recipes)
                    {
                        ItemStack output = recipe.getRecipeOutput();

                        if (output != null && output.getItem() == itemstack.getItem() && (itemstack.getItemDamage() == OreDictionary.WILDCARD_VALUE || output.getItemDamage() == itemstack.getItemDamage()))
                        {
                            ItemStack[] stacks = new ItemStack[10];

                            if (recipe instanceof ShapedRecipes)
                            {
                                ShapedRecipes shapedRecipe = (ShapedRecipes) recipe;

                                for (int i = 0; i < Math.min(stacks.length, shapedRecipe.recipeItems.length); ++i)
                                {
                                    stacks[i] = shapedRecipe.recipeItems[i];
                                }

                                stacks[9] = output;
                                list.add(stacks);
                            }
                            else if (recipe instanceof ShapelessRecipes)
                            {
                                ShapelessRecipes shapelessRecipe = (ShapelessRecipes) recipe;

                                for (int i = 0; i < Math.min(stacks.length, shapelessRecipe.recipeItems.size()); ++i)
                                {
                                    int j = STACK_ORDER[i][0];
                                    int k = STACK_ORDER[i][1];
                                    stacks[k * 3 + j] = (ItemStack) shapelessRecipe.recipeItems.get(i);
                                }

                                stacks[9] = output;
                                list.add(stacks);
                            }
                        }
                    }

                    for (IManualRecipe recipe : MANUAL_RECIPES)
                    {
                        ItemStack output = recipe.getRecipeOutput();

                        if (output != null && output.getItem() == itemstack.getItem() && (itemstack.getItemDamage() == OreDictionary.WILDCARD_VALUE || output.getItemDamage() == itemstack.getItemDamage()))
                        {
                            ItemStack[] stacks = new ItemStack[10];
                            recipe.putStacks(output, stacks);
                            stacks[9] = output;

                            list.add(stacks);
                            break;
                        }
                    }

                    widget.itemstacks = list.toArray(new ItemStack[list.size()][]);
                    RECIPE_CACHE.put(itemstack, widget.itemstacks);
                }
            }
        }
    },
    IMAGE(0, 0, false)
    {
        @Override
        public void initType(Widget widget)
        {
            TextureHelper.loadImage((String) widget.type.handleObject(widget, this));
        }

        @Override
        public Dimension getSize(Widget widget)
        {
            return TextureHelper.getImageSize((String) widget.type.handleObject(widget, this));
        }
    },
    TEXT(0, 0, false)
    {
        @Override
        public Dimension getSize(Widget widget)
        {
            return new Dimension(Minecraft.getMinecraft().fontRenderer.getStringWidth((String) widget.type.handleObject(widget, this)), Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
        }

        @Override
        public float scale(Widget widget, WidgetType type)
        {
            return 1;
        }
    },
    ITEMLINK(0, 0, false)
    {
        @Override
        public boolean handleType(WidgetType type)
        {
            return type == ITEM || type == TEXT;
        }

        @Override
        public Object handleObject(Widget widget, WidgetType type)
        {
            if (type == TEXT && widget.itemstacks.length > 0 && widget.itemstacks[0].length > 0)
            {
                ItemStack stack = widget.itemstacks[0][0];

                return String.format("<link=%s>%s</link>", stack.getUnlocalizedName(), stack.getDisplayName());
            }

            return widget.value;
        }

        @Override
        public Point offset(Widget widget, WidgetType type)
        {
            if (type == TEXT)
            {
                return new Point(Math.round(12 * widget.scale), Math.round(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2 * (widget.scale - 1)));
            }

            return OFFSET_ZERO;
        }

        @Override
        public float scale(Widget widget, WidgetType type)
        {
            if (type == ITEM)
            {
                return widget.scale * 0.5F;
            }

            return 1;
        }
    };

    private static final Point OFFSET_ZERO = new Point();

    private static final List<IManualRecipe> MANUAL_RECIPES = new ArrayList<>();
    public static final Map<ItemStack, ItemStack[][]> RECIPE_CACHE = new HashMap<>();
    public static final int[][] STACK_ORDER = new int[][] {{0, 0}, {1, 0}, {0, 1}, {1, 1}, {0, 2}, {1, 2}, {2, 0}, {2, 1}, {2, 2}};

    private Dimension size;
    public final boolean backgroundDefault;
    public final float scaleDefault;

    private WidgetType(int width, int height, boolean background, float scale)
    {
        size = new Dimension(width, height);
        backgroundDefault = background;
        scaleDefault = scale;
    }

    private WidgetType(int width, int height, boolean background)
    {
        this(width, height, background, 1);
    }

    private WidgetType(int width, int height, float scale)
    {
        this(width, height, true, scale);
    }

    private WidgetType(int width, int height)
    {
        this(width, height, 1);
    }

    private WidgetType()
    {
        this(0, 0);
    }

    public void initType(Widget widget)
    {
    }

    public Dimension getSize(Widget widget)
    {
        return size;
    }

    /**
     * @param type the WidgetType matched against
     * @return true if the specified WidgetType should be handled
     */
    public boolean handleType(WidgetType type)
    {
        return type == this;
    }

    /**
     * @param widget an instance of the Widget being handled
     * @param type the WidgetType matched against
     * @return an Object containing the required data
     */
    public Object handleObject(Widget widget, WidgetType type)
    {
        return widget.value;
    }

    /**
     * @param widget an instance of the Widget being handled
     * @param type the WidgetType matched against
     * @return the offset at which the Widget component should be rendered
     */
    public Point offset(Widget widget, WidgetType type)
    {
        return OFFSET_ZERO;
    }

    public float scale(Widget widget, WidgetType type)
    {
        return scaleDefault * widget.scale;
    }

    public static WidgetType get(String name)
    {
        WidgetType type = valueOf(name.toUpperCase(Locale.ROOT));

        if (type != null)
        {
            return type;
        }

        return UNKNOWN;
    }

    public static void registerManualRecipe(IManualRecipe recipe)
    {
        MANUAL_RECIPES.add(recipe);
    }
}
