package com.fiskmods.heroes.client.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.SHRenderHooks;
import com.fiskmods.heroes.client.gui.book.PageCharacter;
import com.fiskmods.heroes.common.book.Book;
import com.fiskmods.heroes.common.book.Chapter;
import com.fiskmods.heroes.common.book.Clickable;
import com.fiskmods.heroes.common.book.ClickablePageLink;
import com.fiskmods.heroes.common.book.Page;
import com.fiskmods.heroes.common.book.PageItemList;
import com.fiskmods.heroes.common.book.json.JsonClickable;
import com.fiskmods.heroes.common.book.widget.IItemListEntry;
import com.fiskmods.heroes.common.book.widget.Widget;
import com.fiskmods.heroes.common.book.widget.WidgetType;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Hero.Property;
import com.fiskmods.heroes.common.hero.Hero.Toggle;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.HeroModifier;
import com.fiskmods.heroes.common.hero.modifier.Weakness;
import com.fiskmods.heroes.common.network.MessageUpdateBook;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.util.FiskServerUtils;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.TextureHelper;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants.NBT;

@SideOnly(Side.CLIENT)
public class GuiSuperheroesBook extends GuiScreen
{
    public static final ResourceLocation BOOK_TEXTURES = new ResourceLocation(FiskHeroes.MODID, "textures/gui/metahuman_log.png");
    public static final float CHAPTER_TEXT_SCALE = 1.5F;
    public static final int BOOK_WIDTH = 278;
    public static final int BOOK_HEIGHT = 180;

    public static EntityClientPlayerMP fakePlayer;

    public final Set<Integer> bookmarks = new TreeSet<>();
    public final ItemStack bookStack;
    public Book book;
    public int currPage;

    private GuiButtonNextPage buttonNextPage;
    private GuiButtonNextPage buttonPreviousPage;
    private GuiButton buttonDone;

    private boolean hasInit = false;
    private boolean leftMouseDown;
    private boolean rightMouseDown;
    private boolean chestOpen;

    public GuiSuperheroesBook(EntityPlayer player, ItemStack itemstack, Book book)
    {
        bookStack = itemstack;
        this.book = book;
    }

    @Override
    public void initGui()
    {
        int x = (width - BOOK_WIDTH) / 2;
        int y = 3;

        buttonList.add(buttonDone = new GuiButton(0, width / 2 - 100, 19 + BOOK_HEIGHT, 200, 20, I18n.format("gui.done")));
        buttonList.add(buttonNextPage = new GuiButtonNextPage(1, x + BOOK_WIDTH - 46, y + 153, true, this));
        buttonList.add(buttonPreviousPage = new GuiButtonNextPage(2, x + 22, y + 153, false, this));

        if (bookStack.hasTagCompound() && !hasInit)
        {
            readFromNBT(bookStack.getTagCompound());
            hasInit = true;
        }

        updateButtons();
    }

    public void updateButtons()
    {
        buttonNextPage.visible = currPage < book.getTotalPagePairs() - 1;
        buttonPreviousPage.visible = currPage > 0;
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                mc.displayGuiScreen(null);
            }
            else if (button.id == 1)
            {
                if (currPage < book.getTotalPagePairs() - 1)
                {
                    ++currPage;
                }
            }
            else if (button.id == 2)
            {
                if (currPage > 0)
                {
                    --currPage;
                }
            }

            updateButtons();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button)
    {
        super.mouseClicked(mouseX, mouseY, button);
        int currPage1 = (currPage + 1) * 2 - 1;
        int currPage2 = (currPage + 1) * 2;

        for (Page page : book.pages)
        {
            if (page.pageNumber == currPage1 || page.pageNumber == currPage2)
            {
                if (page.clickables != null && button == 0)
                {
                    for (Clickable clickable : page.clickables)
                    {
                        for (Rectangle rect : clickable.bounds)
                        {
                            if (rect.contains(mouseX, mouseY))
                            {
                                mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1));
                                clickable.execute(button);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void onGuiClosed()
    {
        if (mc.thePlayer != null)
        {
            if (!bookStack.hasTagCompound())
            {
                bookStack.setTagCompound(new NBTTagCompound());
            }

            writeToNBT(bookStack.getTagCompound());
            SHNetworkManager.wrapper.sendToServer(new MessageUpdateBook(bookStack.getTagCompound()));
        }
    }

    public void readFromNBT(NBTTagCompound tag)
    {
        currPage = MathHelper.clamp_int(tag.getInteger("Page"), 0, book.getTotalPagePairs());

        if (tag.hasKey("Bookmarks", NBT.TAG_LIST))
        {
            NBTTagList list = tag.getTagList("Bookmarks", NBT.TAG_STRING);

            for (int i = 0; i < list.tagCount(); ++i)
            {
                Page page = book.getPageFromId(list.getStringTagAt(i));

                if (page != null)
                {
                    bookmarks.add((page.pageNumber - 1) / 2);
                }
            }
        }
        else if (tag.hasKey("Bookmarks", NBT.TAG_INT_ARRAY))
        {
            int[] aint = tag.getIntArray("Bookmarks");

            for (int pagePair : aint)
            {
                if (pagePair < book.getTotalPagePairs())
                {
                    bookmarks.add(pagePair);
                }
            }

            tag.removeTag("Bookmarks");
        }
    }

    public void writeToNBT(NBTTagCompound tag)
    {
        tag.setInteger("Page", currPage);

        if (!bookmarks.isEmpty())
        {
            NBTTagList list = new NBTTagList();

            for (int pagePair : bookmarks)
            {
                list.appendTag(new NBTTagString(book.getPageAt(pagePair * 2 + 1).pageId));
            }

            tag.setTag("Bookmarks", list);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(book.getBackground());

        int x = (width - BOOK_WIDTH) / 2;
        int y = 3;
        int currPage1 = (currPage + 1) * 2 - 1;
        int currPage2 = (currPage + 1) * 2;
        final boolean debug = false;
        ItemStack hoverItem = null;
        List<String> hoverText = null;

        boolean unicode = fontRendererObj.getUnicodeFlag();
        boolean leftClick = Mouse.isButtonDown(0) && !leftMouseDown;
        boolean rightClick = Mouse.isButtonDown(1) && !rightMouseDown;
        boolean mousePressed = false;

        drawTexturedModalRect(x, y, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, 512, 256);
        fontRendererObj.setUnicodeFlag(true);
        fontRendererObj.drawString(currPage1 + "", x + 16, y + 10, 0);
        fontRendererObj.drawString(currPage2 + "", x - 16 + BOOK_WIDTH - fontRendererObj.getStringWidth(currPage2 + ""), y + 10, 0);

        for (Page page : book.pages)
        {
            page.clickables.clear();

            if (page.pageNumber == currPage1 || page.pageNumber == currPage2)
            {
                String[] header = page.getHeader();
                String text = page instanceof PageCharacter ? "" : formatText(page.text);

                boolean isChapter = page instanceof Chapter;
                int left = (page.pageNumber == currPage1 ? x + 16 : x + 146) + (isChapter ? 15 : 0);
                int top = isChapter ? y + 45 : y + 25;

                if (!StringUtils.isNullOrEmpty(header[0]))
                {
                    String s = EnumChatFormatting.BOLD + formatText(header[0]);
                    fontRendererObj.drawString(s, left + 58 - fontRendererObj.getStringWidth(s) / 2, top, 0);
                    top += 15;

                    if (!StringUtils.isNullOrEmpty(header[1]))
                    {
                        top += 5;
                        s = formatText(header[1]);
                        fontRendererObj.drawString(s, left + 58 - fontRendererObj.getStringWidth(s) / 2, top - 12, 0);
                        top += 5;
                    }
                }

                int pageWidth = isChapter ? (int) (book.getPageWidth() / CHAPTER_TEXT_SCALE) - 20 : book.getPageWidth();
                int pageHeight = BOOK_HEIGHT + Math.min(top - y, -29) - top + y;

                List<EnumChatFormatting> lineColors = new ArrayList<>();
                List<Rectangle> widgetBounds = new ArrayList<>();

                if (!isChapter)
                {
                    if (debug)
                    {
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glColor4f(1, 1, 0, 1);
                        drawTexturedModalRect(left, top, 0, 0, pageWidth, pageHeight);
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                    }

                    if (page instanceof PageCharacter)
                    {
                        PageCharacter pageCharacter = (PageCharacter) page;
                        Hero hero = pageCharacter.hero;

                        if (hero != null)
                        {
                            if (!pageCharacter.stats)
                            {
                                List<ItemStack> armor = new ArrayList<>(Arrays.asList(hero.getDefault().createArmorStacks()));
                                List equipment = hero.getEquipment();
                                int i = 0;

                                if (hero.getVersion() != null)
                                {
                                    String s = EnumChatFormatting.ITALIC + I18n.format(hero.getVersion());
                                    fontRendererObj.drawString(s, left + 58 - fontRendererObj.getStringWidth(s) / 2, y + 15, 0);
                                }

                                if (!equipment.isEmpty())
                                {
                                    armor.add(new ItemStack(Blocks.chest));

                                    Rectangle rect = new Rectangle(left + 8 + 18, 49 + (armor.size() - equipment.size()) * 18, 18, equipment.size() * 18);
                                    chestOpen = new Rectangle(left + 8, 49 + (armor.size() - 1) * 18, 18, 18).contains(mouseX, mouseY) || chestOpen && rect.contains(mouseX, mouseY);

                                    if (chestOpen)
                                    {
                                        GL11.glColor4f(1, 1, 1, 1);
                                        mc.getTextureManager().bindTexture(BOOK_TEXTURES);

                                        for (int j = 0; j < equipment.size(); ++j)
                                        {
                                            drawTexturedModalRect(rect.x, rect.y + j * 18, 28, 206, 18, 18, 512, 256);
                                        }
                                    }
                                }

                                SHRenderHelper.setupRenderItemIntoGUI();

                                for (ItemStack itemstack : armor)
                                {
                                    if (itemstack != null)
                                    {
                                        SHRenderHooks.renderItemIntoGUI(left + 9, 50 + i * 18, itemstack);

                                        if (itemstack.getItem() == Item.getItemFromBlock(Blocks.chest) && chestOpen)
                                        {
                                            for (int j = equipment.size() - 1; j >= 0; --j)
                                            {
                                                ItemStack itemstack1 = FiskServerUtils.getStackFrom(equipment.get(j));

                                                if (itemstack1 != null)
                                                {
                                                    Rectangle rect = new Rectangle(left + 8 + 18, 49 + (i - j) * 18, 18, 18);
                                                    page.clickables.add(new ClickablePageLink(this, getPageForItem(itemstack1)).setBounds(rect));

                                                    if (rect.contains(mouseX, mouseY))
                                                    {
                                                        hoverItem = itemstack1;
                                                    }

                                                    SHRenderHooks.renderItemIntoGUI(rect.x + 1, rect.y + 1, itemstack1);
                                                }
                                            }
                                        }
                                    }

                                    ++i;
                                }

                                SHRenderHelper.finishRenderItemIntoGUI();

                                if (fakePlayer != null)
                                {
                                    for (i = 0; i < Math.min(armor.size(), 4); ++i)
                                    {
                                        fakePlayer.setCurrentItemOrArmor(4 - i, armor.get(i));
                                    }

                                    Rectangle rect = new Rectangle(left + pageWidth, top - 5, 80, 0);
                                    rect.height = pageHeight + 10;
                                    rect.x -= rect.width;

                                    if (debug)
                                    {
                                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                                        GL11.glColor4f(0, 1, 0.5F, 1);
                                        drawTexturedModalRect(rect.x, rect.y, 0, 0, rect.width, rect.height);
                                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                                    }

                                    float scale = 50;
                                    int x1 = rect.x + rect.width / 2;
                                    int y1 = rect.y + rect.height;

                                    GL11.glEnable(GL11.GL_COLOR_MATERIAL);
                                    GL11.glPushMatrix();
                                    GL11.glTranslatef(x1, y1, scale);
                                    GL11.glScalef(-scale, scale, scale);
                                    GL11.glRotatef(180, 0, 0, 1);
                                    RenderHelper.enableStandardItemLighting();
                                    GL11.glTranslatef(0, fakePlayer.yOffset + 0.075F, 10);
                                    GL11.glRotatef(10, 1, 0, 0);
                                    GL11.glRotatef((fakePlayer.ticksExisted + partialTicks) * 2, 0, 1, 0);
                                    RenderManager.instance.playerViewY = 180;
                                    SHRenderHelper.startGlScissor(rect.x, rect.y, rect.width, rect.height);
                                    RenderManager.instance.renderEntityWithPosYaw(fakePlayer, 0.0D, 0.0D, 0.0D, 0, 1);
                                    SHRenderHelper.endGlScissor();
                                    GL11.glPopMatrix();
                                    RenderHelper.disableStandardItemLighting();
                                    GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                                    OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                                    OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
                                }
                            }
                            else
                            {
                                List<String> list = new ArrayList<>();
                                Set<HeroModifier> modifiers = hero.getEnabledModifiers(fakePlayer);

                                list.add(I18n.format("tooltip.tier", hero.getTier().tier));

                                if (!modifiers.isEmpty())
                                {
                                    list.add("");
                                }

                                modifiers.stream().filter(Ability.class::isInstance).forEach(t -> list.add(EnumChatFormatting.DARK_GREEN + String.format("* <link=%s?c=DARK_GREEN>%s</link>", t.getUnlocalizedName(), t.getLocalizedName())));
                                modifiers.stream().filter(Weakness.class::isInstance).forEach(t -> list.add(EnumChatFormatting.GOLD + String.format("* <link=%s?c=GOLD>%s</link>", t.getUnlocalizedName(), t.getLocalizedName())));

                                if (!modifiers.isEmpty())
                                {
                                    list.add("");
                                }

                                page.text = "";
                                hero.getAttributeModifiers(fakePlayer, (attribute, amount, operation) ->
                                {
                                    if (amount != 0 && list.size() <= 1)
                                    {
                                        list.add("");
                                    }

                                    if (operation == 1 || operation == 2)
                                    {
                                        amount *= 100;
                                    }

                                    if (amount > 0)
                                    {
                                        list.add(EnumChatFormatting.BLUE + I18n.format("attribute.modifier.plus." + operation, ItemStack.field_111284_a.format(amount), I18n.format("attribute.name." + attribute.getAttributeUnlocalizedName())));
                                    }
                                    else if (amount < 0)
                                    {
                                        amount *= -1;
                                        list.add(EnumChatFormatting.RED + I18n.format("attribute.modifier.take." + operation, ItemStack.field_111284_a.format(amount), I18n.format("attribute.name." + attribute.getAttributeUnlocalizedName())));
                                    }
                                });

                                for (String s : list)
                                {
                                    boolean flag = false;

                                    for (EnumChatFormatting format : EnumChatFormatting.values())
                                    {
                                        if (s.startsWith(format.toString()))
                                        {
                                            s = s.substring(2);
                                            lineColors.add(format);
                                            flag = true;
                                            break;
                                        }
                                    }

                                    if (!flag)
                                    {
                                        lineColors.add(EnumChatFormatting.RESET);
                                    }

                                    page.text += s + "\n";
                                }

                                text = formatText(page.text);
                                mc.getTextureManager().bindTexture(book.getBackground());
                                GL11.glEnable(GL11.GL_BLEND);

                                List<PropertyButton> buttons = new ArrayList<>();
                                int index = 0;

                                for (Property p : Property.values())
                                {
                                    if (hero.hasProperty(fakePlayer, p))
                                    {
                                        PropertyButton b = new PropertyButton(p.name().toLowerCase(Locale.ROOT), p.iconIndex, p.dataHook);
                                        buttons.add(b);

                                        if (p == Property.MASK_TOGGLE)
                                        {
                                            b.onPress = t -> hero.onToggleMask(fakePlayer, t);
                                        }
                                    }
                                }

                                for (Toggle t : Toggle.values())
                                {
                                    if (t.dataHook.canSet.test(fakePlayer) && hero.isKeyBindEnabled(fakePlayer, t.keybind))
                                    {
                                        buttons.add(new PropertyButton("toggle." + t.name().toLowerCase(Locale.ROOT), t.iconIndex, t.dataHook, t.cooldown));
                                    }
                                }

                                buttons.sort(Comparator.comparing(t -> t.dataHook == null ? 1 : 0));

                                for (PropertyButton button : buttons)
                                {
                                    Rectangle rect = new Rectangle(left, top, 13, 13);
                                    rect.x += pageWidth - rect.width;
                                    rect.y += index * (rect.height + 2);

                                    boolean hover = (hoverText == null || hoverText.isEmpty()) && new Rectangle(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2).contains(mouseX, mouseY);
                                    boolean pressed = button.dataHook != null && button.dataHook.get(fakePlayer);

                                    if (hover)
                                    {
                                        int i = -1;

                                        if (button.cooldown != null && (i = button.cooldown.apply(fakePlayer, hero)) >= 0)
                                        {
                                            hoverText = Arrays.asList(I18n.format("gui.metahuman_log.hero." + button.name), StringUtils.ticksToElapsedTime(i));
                                        }
                                        else
                                        {
                                            hoverText = Arrays.asList(I18n.format("gui.metahuman_log.hero." + button.name));
                                        }

                                        if (leftClick && button.dataHook != null)
                                        {
                                            boolean state = !button.dataHook.get(fakePlayer);

                                            if (button.onPress != null)
                                            {
                                                button.onPress.accept(state);
                                            }

                                            button.dataHook.setWithoutNotify(fakePlayer, state);
                                            mousePressed = true;
                                        }
                                    }

                                    GL11.glColor4f(1, 1, 1, 1);
                                    drawTexturedModalRect(rect.x, rect.y, 278 + (hover ? rect.width : 0), (button.dataHook != null ? 44 : 18) + (pressed ? rect.height : 0), rect.width, rect.height, 512, 256);

                                    if (pressed)
                                    {
                                        GL11.glColor4f(1, 1, 1, 0.5F);
                                    }

                                    drawTexturedModalRect(rect.x, rect.y, 304 + button.iconX * rect.width, 18 + button.iconY * rect.height, rect.width, rect.height, 512, 256);
                                    ++index;
                                }

                                GL11.glDisable(GL11.GL_BLEND);
                            }
                        }
                    }
                    else if (page instanceof PageItemList)
                    {
                        PageItemList itemList = (PageItemList) page;
                        itemList.widgets.clear();

                        int itemX = 0;
                        int itemY = 0;
                        int maxItemColumns = pageWidth / 18 - 1;
                        int maxItemRows = pageHeight / 18 - 1;

                        List<ItemStack> list = Lists.newArrayList(itemList.itemList);
                        ItemStack[] aitemstack = new ItemStack[(list.size() / 42 + 1) * 42];

                        for (int i = 0; i < aitemstack.length; ++i)
                        {
                            if (i < list.size())
                            {
                                aitemstack[i] = list.get(i);
                            }
                        }

                        for (int index = 0; index < aitemstack.length; ++index)
                        {
                            ItemStack itemstack = aitemstack[index];

                            if (index < itemList.startIndex)
                            {
                                continue;
                            }

                            Rectangle rect = new Rectangle(left + itemX * 18 + (pageWidth - 6 * 18) / 2, top + itemY * 18, 18, 18);
                            Widget widget = new Widget(WidgetType.ITEM, null, rect.x - left, rect.y - top, 1, true);

                            if (itemstack != null)
                            {
                                String transferId = getPageForItem(itemstack);
                                page.clickables.add(new ClickablePageLink(this, transferId).setBounds(rect));

                                if (rect.contains(mouseX, mouseY))
                                {
                                    hoverItem = itemstack;
                                }
                            }

                            widget.itemstacks = new ItemStack[][] {{itemstack}};
                            page.widgets.add(widget);

                            if (itemX < maxItemColumns)
                            {
                                ++itemX;
                            }
                            else
                            {
                                itemX = 0;
                                ++itemY;

                                if (itemY > maxItemRows)
                                {
                                    break;
                                }
                            }
                        }
                    }

                    updateWidgetBounds(left, top, pageWidth, page.widgets, widgetBounds);
                    drawParsedText(text, left, top, pageWidth, widgetBounds, page.clickables, lineColors);
                }
                else
                {
                    String s2 = EnumChatFormatting.BOLD + I18n.format("book.metahumanLog.chapter", book.chapters.indexOf(page) + 1);
                    fontRendererObj.drawString(s2, left, top, 0);

                    GL11.glPushMatrix();
                    GL11.glTranslatef(left, top + 20, 0);
                    GL11.glScalef(CHAPTER_TEXT_SCALE, CHAPTER_TEXT_SCALE, CHAPTER_TEXT_SCALE);
                    fontRendererObj.drawSplitString(EnumChatFormatting.BOLD + text, 0, 0, pageWidth, 0);
                    GL11.glPopMatrix();

                    updateWidgetBounds(left, top, pageWidth, page.widgets, widgetBounds);
                }

                if (debug)
                {
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glColor4f(0, 0, 0, 1);

                    for (Rectangle rect : widgetBounds)
                    {
                        drawTexturedModalRect(rect.x, rect.y, 0, 0, rect.width, rect.height);
                    }

                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

                for (Widget widget : page.widgets) // We need to render the image and text widgets separately because they bind new textures
                {
                    if (widget.type.handleType(WidgetType.IMAGE))
                    {
                        Point pos = offset(widget, WidgetType.IMAGE, left, top, pageWidth);
                        TextureHelper.drawImage((String) widget.type.handleObject(widget, WidgetType.IMAGE), pos.x, pos.y, zLevel, widget.getScale(WidgetType.IMAGE), new Dimension(pageWidth - (pos.x - left), pageHeight - (pos.y - top)));
                    }

                    if (widget.type.handleType(WidgetType.TEXT))
                    {
                        Point pos = offset(widget, WidgetType.TEXT, left, top, pageWidth);
                        float scale = widget.getScale(WidgetType.TEXT);

                        GL11.glPushMatrix();
                        GL11.glTranslatef(pos.x, pos.y, 0);
                        GL11.glScalef(scale, scale, scale);
                        GL11.glTranslatef(-pos.x, -pos.y, 0);
                        drawParsedText(formatText((String) widget.type.handleObject(widget, WidgetType.TEXT)), pos.x, pos.y, pageWidth, widgetBounds, page.clickables, new ArrayList<EnumChatFormatting>());
                        GL11.glPopMatrix();
                    }
                }

                mc.getTextureManager().bindTexture(book.getBackground());
                fontRendererObj.setUnicodeFlag(unicode); // Re-enable for things like item stack numbers
                GL11.glColor4f(1, 1, 1, 1);

                for (Widget widget : page.widgets) // Render widget background, if enabled
                {
                    if (widget.background)
                    {
                        GL11.glPushMatrix();

                        if (widget.type.handleType(WidgetType.ITEM))
                        {
                            Point pos = offset(widget, WidgetType.ITEM, left, top, pageWidth);
                            float scale = widget.getScale(WidgetType.ITEM);

                            GL11.glTranslatef(pos.x, pos.y, 0);
                            GL11.glScalef(scale, scale, scale);
                            drawTexturedModalRect(0, 0, 28, 206, 18, 18, 512, 256);
                        }

                        if (widget.type.handleType(WidgetType.RECIPE))
                        {
                            Point pos = offset(widget, WidgetType.RECIPE, left, top, pageWidth);
                            float scale = widget.getScale(WidgetType.RECIPE);

                            GL11.glTranslatef(pos.x, pos.y, 0);
                            GL11.glScalef(scale, scale, scale);
                            drawTexturedModalRect(0, 0, 46, 180, 113, 60, 512, 256);
                        }

                        GL11.glPopMatrix();
                    }
                }

                SHRenderHelper.setupRenderItemIntoGUI();

                for (Widget widget : page.widgets) // Render widget foreground. (Usually items)
                {
                    GL11.glPushMatrix();

                    if (widget.itemstacks != null)
                    {
                        if (widget.type.handleType(WidgetType.ITEM) && widget.itemstacks.length > 0 && widget.itemstacks[0].length > 0)
                        {
                            Point pos = offset(widget, WidgetType.ITEM, left, top, pageWidth);
                            float scale = widget.getScale(WidgetType.ITEM);

                            ItemStack[] stacks = widget.itemstacks[0];
                            ItemStack itemstack = stacks[0];

                            if (stacks.length > 1)
                            {
                                int cycle = mc.thePlayer.ticksExisted / 20;
                                itemstack = stacks[cycle % stacks.length];
                            }

                            GL11.glTranslatef(pos.x, pos.y, 0);
                            GL11.glScalef(scale, scale, scale);
                            SHRenderHooks.renderItemIntoGUI(1, 1, itemstack);
                        }

                        if (widget.type.handleType(WidgetType.RECIPE) && widget.itemstacks.length > 0 && widget.itemstacks[0].length > 9)
                        {
                            Point pos = offset(widget, WidgetType.RECIPE, left, top, pageWidth);
                            float scale = widget.getScale(WidgetType.RECIPE);

                            GL11.glTranslatef(pos.x, pos.y, 0);
                            GL11.glScalef(scale, scale, scale);

                            ItemStack[] stacks = widget.itemstacks[0];

                            if (stacks.length > 1)
                            {
                                int cycle = mc.thePlayer.ticksExisted / 20;
                                stacks = widget.itemstacks[cycle % widget.itemstacks.length];
                            }

                            for (int i = 0; i < 9; ++i)
                            {
                                ItemStack itemstack = stacks[i];

                                if (itemstack != null)
                                {
                                    Rectangle rect = new Rectangle(3 + i % 3 * 18 + pos.x, 3 + i / 3 * 18 + top + widget.y, 18, 18);
                                    SHRenderHooks.renderItemIntoGUI(rect.x - pos.x + 1, rect.y - (top + widget.y) + 1, itemstack);

                                    String transferId = getPageForItem(itemstack);

                                    if (book.getPageFromId(transferId) != null)
                                    {
                                        page.clickables.add(new ClickablePageLink(this, transferId).setBounds(rect).setColor(EnumChatFormatting.BLUE));
                                    }

                                    if (rect.contains(mouseX, mouseY))
                                    {
                                        hoverItem = itemstack;
                                    }
                                }
                            }

                            Rectangle rect = new Rectangle(91, 21, 18, 18);

                            if (rect.contains(mouseX - pos.x, mouseY - (top + widget.y)))
                            {
                                hoverItem = stacks[9];
                            }

                            SHRenderHooks.renderItemIntoGUI(rect.x + 1, rect.y + 1, stacks[9]);
                        }
                    }

                    GL11.glPopMatrix();
                }

                for (JsonClickable jsonClickable : page.jsonClickables) // Add all clickables to the page, with the right offsets. We don't need to do this until after widget rendering.
                {
                    Clickable clickable = new ClickablePageLink(this, jsonClickable.getLink()).setColor(jsonClickable.getColor());

                    for (Rectangle rect : jsonClickable.getBounds())
                    {
                        Rectangle rect1 = new Rectangle(getOffset(jsonClickable, left, top, pageWidth));
                        rect1.setSize(rect.width, rect.height);

                        clickable.bounds.add(rect1);
                    }

                    page.clickables.add(clickable);
                }

                fontRendererObj.setUnicodeFlag(true);
                SHRenderHelper.finishRenderItemIntoGUI();
            }

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(0, 0, 0, 1);

            for (Clickable clickable : page.clickables)
            {
                for (Rectangle rect : clickable.bounds)
                {
                    if (rect.contains(mouseX, mouseY))
                    {
                        fontRendererObj.drawString(clickable.color.toString(), 0, 0, 0); // We need to trick the FontRenderer to set the gui color for us, since we can't get a hex value from EnumChatFormatting
                        zLevel += 100;

                        for (Rectangle rect1 : clickable.bounds)
                        {
                            drawTexturedModalRect(rect1.x, rect1.y + rect1.height - 1, 0, 0, rect1.width, 1);
                        }

                        zLevel -= 100;
                        break;
                    }
                }
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        mc.getTextureManager().bindTexture(book.getBackground());
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1, 1, 1, 1);

        Rectangle rect = new Rectangle(x + 8, y + 27, 22, 8);
        rect.x -= rect.width;

        if (rect.contains(mouseX, mouseY))
        {
            hoverText = getBookmarkText(0);

            if (leftClick)
            {
                currPage = 0;
                updateButtons();
                mousePressed = true;
            }
        }

        drawTexturedModalRect(rect.x, rect.y, 278, rect.contains(mouseX, mouseY) ? rect.height : 0, rect.width, rect.height, 512, 256);
        rect = new Rectangle(x + 139, y + 172, 131, 8);

        int x1 = 0;
        int y1 = rect.y;
        int spacing = 3;
        int index = 0;

        List<Integer> list = new ArrayList<>(bookmarks);
        list.add(-1);

        while (x1 - 3 <= rect.x)
        {
            if (x1 != 0)
            {
                --spacing;
            }

            x1 = rect.x + rect.width / 2 - (8 + spacing) * list.size() / 2;
        }

        for (int pageNum : list)
        {
            boolean hover = hoverText == null || hoverText.isEmpty();
            boolean createNew = index == bookmarks.size();

            if (!createNew || currPage > 0 && !bookmarks.contains(currPage))
            {
                Rectangle rect1 = new Rectangle(x1, y1, 8 + (createNew || !(currPage > 0 && !bookmarks.contains(currPage)) && index == bookmarks.size() - 1 ? Math.max(spacing, 0) : spacing), 18);
                hover &= rect1.contains(mouseX, mouseY);

                if (hover)
                {
                    if (createNew)
                    {
                        hoverText = Arrays.asList(I18n.format("gui.metahuman_log.bookmark.new"));

                        if (leftClick)
                        {
                            bookmarks.add(currPage);
                            mousePressed = true;
                        }
                    }
                    else
                    {
                        hoverText = getBookmarkText(pageNum);

                        if (leftClick)
                        {
                            currPage = pageNum;
                            updateButtons();
                            mousePressed = true;
                        }
                        else if (rightClick)
                        {
                            bookmarks.remove(pageNum);
                            mousePressed = true;
                            continue;
                        }
                    }
                }

                float shade = hover || spacing > 0 ? 1 : 1 - (1 - (float) index / bookmarks.size()) * MathHelper.clamp_float(0.25F - (float) spacing / 8, 0, 0.5F);
                GL11.glColor4f(shade, shade, shade, 1);

                zLevel += hover ? 100 : 0;
                drawTexturedModalRect(x1, y1, 300 + (hover ? 8 : 0) + (createNew ? 16 : 0), 0, 8, 18, 512, 256);
                zLevel -= hover ? 100 : 0;
                x1 += 8 + spacing;
            }

            ++index;
        }

        GL11.glDisable(GL11.GL_BLEND);
        fontRendererObj.setUnicodeFlag(unicode);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (hoverText != null && !hoverText.isEmpty())
        {
            drawHoveringText(hoverText, mouseX, mouseY, fontRendererObj);
        }
        else if (hoverItem != null)
        {
            renderToolTip(hoverItem, mouseX, mouseY);
        }

        if (mousePressed)
        {
            mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1));
        }

        leftMouseDown = Mouse.isButtonDown(0);
        rightMouseDown = Mouse.isButtonDown(1);
    }

    public String getPageForItem(ItemStack itemstack)
    {
        if (itemstack.getItem() instanceof IItemListEntry)
        {
            return ((IItemListEntry) itemstack.getItem()).getPageLink(itemstack);
        }

        return itemstack.getUnlocalizedName();
    }

    public List<String> getBookmarkText(int pageIndex)
    {
        List<String> hoverText = new ArrayList<>();
        Page prevPage = null;

        for (int i = 0; i < 2; ++i)
        {
            if (pageIndex * 2 + i < book.pages.size())
            {
                Page page = book.pages.get(pageIndex * 2 + i);

                if (prevPage == null || !page.pageId.equals(prevPage.pageId))
                {
                    String s = page.getSummaryTitle();

                    if (!StringUtils.isNullOrEmpty(s))
                    {
                        hoverText.add(EnumChatFormatting.GRAY + I18n.format("gui.metahuman_log.bookmark.page.long", EnumChatFormatting.WHITE + s + EnumChatFormatting.GRAY, page.pageNumber));
                    }
                    else if (prevPage == null)
                    {
                        hoverText.add(I18n.format("gui.metahuman_log.bookmark.page", page.pageNumber));
                        break;
                    }
                }

                prevPage = page;
            }
        }

        return hoverText;
    }

    public boolean intersects(Rectangle rect, List<Rectangle> list)
    {
        for (Rectangle rect1 : list)
        {
            if (rect.intersects(rect1))
            {
                return true;
            }
        }

        return false;
    }

    public Point offset(Widget widget, WidgetType type, int left, int top, int pageWidth)
    {
        Point pos = widget.alignment.offset(widget, left, top, pageWidth);
        Point pos1 = widget.type.offset(widget, type);
        pos.translate(pos1.x, pos1.y);

        return pos;
    }

    public Point getOffset(JsonClickable jsonClickable, int left, int top, int pageWidth)
    {
        String alignment = jsonClickable.getAlignment();
        Rectangle bounds = new Rectangle(-1, -1);

        for (Rectangle rect : jsonClickable.getBounds())
        {
            bounds.add(rect);
        }

        if (alignment.equals("right"))
        {
            bounds.x *= -1;
            bounds.x += left + pageWidth - bounds.width;
        }
        else if (alignment.equals("center"))
        {
            bounds.x += left + (pageWidth - bounds.width) / 2;
        }
        else
        {
            bounds.x += left;
        }

        return new Point(bounds.x, bounds.y + top);
    }

    public String formatText(String text)
    {
        text = text.replace("\\n", "\n");
        text = text.replaceAll("&([a-f0-9rklmnor])", "\u00A7$1");

        if (text.contains("<") && text.contains(">"))
        {
            for (KeyBinding keyBind : mc.gameSettings.keyBindings)
            {
                String s = String.format("<%s>", keyBind.getKeyDescription());

                if (text.contains(s))
                {
                    text = text.replace(s, GameSettings.getKeyDisplayString(keyBind.getKeyCode()));
                }
            }
        }

        return text;
    }

    public void drawParsedText(String text, int left, int top, int pageWidth, List<Rectangle> widgetBounds, List<Clickable> clickables, List<EnumChatFormatting> lineColors)
    {
        String newText = text;
        String linkStart = "<link="; // FIXME: Links flicker when more than one of them occupy the same row
        String linkEnd = "</link>";

        Map<Integer[], String> links = new HashMap<>();
        Map<Integer[], EnumChatFormatting> colors = new HashMap<>();

        while (newText.contains(linkStart) && newText.contains(linkEnd))
        {
            int index = newText.indexOf(linkStart);
            int startIndex = index;
            int endIndex = index;
            int totalIndex = index;

            int indexColorStart = -1;
            EnumChatFormatting color = null;
            String transferId = "";

            for (int i = index + linkStart.length(); i < newText.length(); ++i)
            {
                String s = newText.substring(i);

                if (s.startsWith(">"))
                {
                    transferId = newText.substring(index + linkStart.length(), i);
                    startIndex = i + 1;

                    if (indexColorStart >= 0)
                    {
                        String colorName = newText.substring(indexColorStart + 3, i);

                        for (EnumChatFormatting format : EnumChatFormatting.values())
                        {
                            if (format.name().equals(colorName))
                            {
                                color = format;
                                break;
                            }
                        }

                        transferId = transferId.substring(0, transferId.length() - (colorName.length() + 3));
                    }

                    break;
                }
                else if (s.startsWith("?c="))
                {
                    indexColorStart = i;
                }
            }

            for (int i = startIndex; i < newText.length(); ++i)
            {
                if (newText.substring(i).startsWith(linkEnd))
                {
                    endIndex = i;
                    totalIndex = i + linkEnd.length();
                    break;
                }
            }

            String s = newText.substring(startIndex, endIndex);
            Integer[] aint = new Integer[] {index, index + s.length()};

            links.put(aint, transferId);
            colors.put(aint, color);
            newText = newText.substring(0, index) + s + newText.substring(totalIndex, newText.length());
        }

        List<String> lines = new ArrayList<>();
        List<String> list = new ArrayList<>();
        int lineNum = -1;

        for (String s : newText.replace("\n", " \n").split("\n"))
        {
            if (s.endsWith(" "))
            {
                s = s.substring(0, s.length() - 1);
            }

            list.add(s);
        }

        for (String s : list)
        {
            int index = 0;
            ++lineNum;

            if (s.isEmpty())
            {
                lines.add(s);
            }

            while (index < s.length() && lineNum < 30)
            {
                Rectangle rect = new Rectangle(left, top + lineNum * fontRendererObj.FONT_HEIGHT, 1, fontRendererObj.FONT_HEIGHT);

                while (!intersects(rect, widgetBounds) && rect.width < pageWidth)
                {
                    ++rect.width;
                }

                if (rect.width < 5)
                {
                    ++lineNum;
                    continue;
                }

                String s1 = s.substring(index);
                List<String> list1 = fontRendererObj.listFormattedStringToWidth(s1, rect.width < pageWidth ? rect.width - 3 : rect.width);

                if (!list1.isEmpty())
                {
                    String lineText = list1.get(0);
                    index += lineText.length();

                    if (lineText.startsWith(" ") && !lineText.startsWith("  "))
                    {
                        lineText = lineText.substring(1);
                    }

                    lines.add(lineText);
                    ++lineNum;
                }
            }
        }

        Map<Integer[], Clickable> map = new HashMap<>();
        int column = 0;
        int yPos = top;

        for (Iterator<String> iter = lines.iterator(); iter.hasNext(); yPos += fontRendererObj.FONT_HEIGHT)
        {
            String line = iter.next();
            String line1 = line;

            for (Map.Entry<Integer[], String> e : links.entrySet())
            {
                int startIndex = MathHelper.clamp_int(e.getKey()[0] - column, 0, line.length());
                int endIndex = MathHelper.clamp_int(e.getKey()[1] - column, 0, line.length());

                if (endIndex - startIndex > 0)
                {
                    Clickable clickable = map.get(e.getKey());

                    if (clickable == null)
                    {
                        EnumChatFormatting color = colors.get(e.getKey());
                        clickable = new ClickablePageLink(this, e.getValue());

                        if (color != null)
                        {
                            clickable.setColor(color);
                        }

                        map.put(e.getKey(), clickable);
                        clickables.add(clickable);
                    }

                    clickable.bounds.add(new Rectangle(left + fontRendererObj.getStringWidth(line.substring(0, startIndex)), yPos, fontRendererObj.getStringWidth(line.substring(startIndex, endIndex)), fontRendererObj.FONT_HEIGHT));
                    line1 = line.substring(0, startIndex) + clickable.color + line.substring(startIndex, endIndex) + EnumChatFormatting.RESET + line.substring(endIndex);
                }
            }

            String prefix = "";

            if (lines.indexOf(line) < lineColors.size())
            {
                prefix += lineColors.get(lines.indexOf(line));
            }

            fontRendererObj.drawString(prefix + line1, left, yPos, 0);
            column += line.length() + 1;
        }
    }

    public void updateWidgetBounds(int left, int top, int pageWidth, List<Widget> widgets, List<Rectangle> bounds)
    {
        bounds.clear();

        for (Widget widget : widgets) // Keep track of all widgets that have a background
        {
            if (widget.background)
            {
                List<Rectangle> list = widget.getBounds(left, top, pageWidth);
                Point offset = widget.alignment.offset(widget, left, top, pageWidth);

                for (Rectangle rect : list)
                {
                    rect.translate(offset.x, offset.y);
                    bounds.add(rect);
                }
            }
        }
    }

    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int texWidth, int texHeight)
    {
        float f = 1F / texWidth;
        float f1 = 1F / texHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, zLevel, u * f, (v + height) * f1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, (u + width) * f, (v + height) * f1);
        tessellator.addVertexWithUV(x + width, y, zLevel, (u + width) * f, v * f1);
        tessellator.addVertexWithUV(x, y, zLevel, u * f, v * f1);
        tessellator.draw();
    }

    private class PropertyButton
    {
        public final BiFunction<EntityLivingBase, Hero, Integer> cooldown;
        public final SHData<Boolean> dataHook;
        public Consumer<Boolean> onPress;

        public final String name;
        public final int iconX, iconY;

        public PropertyButton(String s, int iconIndex, SHData<Boolean> data, BiFunction<EntityLivingBase, Hero, Integer> f)
        {
            name = s;
            iconX = iconIndex % 5;
            iconY = iconIndex / 5;
            dataHook = data;
            cooldown = f;
        }

        public PropertyButton(String s, int iconIndex, SHData<Boolean> data)
        {
            this(s, iconIndex, data, (t, u) -> -1);
        }
    }
}
