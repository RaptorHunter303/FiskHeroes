package com.fiskmods.heroes.client.gui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.gui.GuiStringList.IGuiStringList;
import com.fiskmods.heroes.common.container.ContainerSuitFabricator;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.Heroes;
import com.fiskmods.heroes.common.tileentity.TileEntityCosmicFabricator;
import com.fiskmods.heroes.common.tileentity.TileEntitySuitFabricator;
import com.fiskmods.heroes.util.FabricatorHelper;
import com.fiskmods.heroes.util.SHComparators;
import com.fiskmods.heroes.util.SHFormatHelper;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.base.Objects;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiSuitFabricator extends GuiContainer implements IGuiStringList
{
    private static final ResourceLocation TEXTURES = new ResourceLocation(FiskHeroes.MODID, "textures/gui/container/suit_fabricator.png");

    private final TileEntitySuitFabricator tile;

    private GuiStringList heroList;
    private List<Hero> heroes;

    protected GuiSuitFabricator(Container container, InventoryPlayer inventoryPlayer, TileEntitySuitFabricator tileentity)
    {
        super(container);
        tile = tileentity;
        ySize = 210;
    }

    public GuiSuitFabricator(InventoryPlayer inventoryPlayer, TileEntitySuitFabricator tileentity)
    {
        this(new ContainerSuitFabricator(inventoryPlayer, tileentity), inventoryPlayer, tileentity);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        heroes = Hero.REGISTRY.getValues().stream().filter(this::hasHero).sorted(SHComparators.fabricator(FabricatorHelper.getMaxTier(mc.thePlayer))).collect(Collectors.toList());
        heroList = new GuiStringList<>(this, heroes, guiLeft + 15, guiTop + 18, 106, 70).setFormat(t -> SHFormatHelper.formatHero(t.getDefault()), t -> t.getTier().tier > FabricatorHelper.getMaxTier(mc.thePlayer) ? 0x666666 : -1);
    }

    public boolean hasHero(Hero hero)
    {
        return hero.isCosmic() == isCosmic() && (!hero.isHidden() || hero == Heroes.spodermen && SHHelper.hasSpodermenAccess(mc.thePlayer));
    }

    public boolean isCosmic()
    {
        return tile instanceof TileEntityCosmicFabricator;
    }

    @Override
    public void elementClicked(int index, boolean doubleClick)
    {
        if (index >= 0 && index < heroes.size())
        {
            tile.sendToServer(buf ->
            {
                ByteBufUtils.writeUTF8String(buf, heroes.get(index).getName());
            });
        }
    }

    @Override
    public boolean isSelected(int index)
    {
        return Objects.equal(heroes.get(index), tile.getHero());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = I18n.format(tile.getInventoryName());
        fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 94, 4210752);

        Hero hero = tile.getHero();
        int energy = FabricatorHelper.getTotalEnergy(tile);

        if (hero != null)
        {
            int cost = FabricatorHelper.getMaterialCost(hero);

            if (cost > 0)
            {
//                int k = 8453920;
                String s1 = I18n.format("gui.suit_fabricator.cost", SHFormatHelper.formatNumber(cost));
                int k = isCosmic() ? 0xFF69FF : 0x7CBBE2;

                if (energy < cost)
                {
//                    k = 16736352;
                    k = 0xFF6060;
                }

                int color = -16777216 | (k & 16579836) >> 2 | k & -16777216;
                int x = xSize - 8 - fontRendererObj.getStringWidth(s1);
                int y = ySize - 94;

                if (fontRendererObj.getUnicodeFlag())
                {
                    drawRect(x - 3, y - 2, xSize - 7, y + 10, -16777216);
                    drawRect(x - 2, y - 1, xSize - 8, y + 9, -12895429);
                }
                else
                {
                    fontRendererObj.drawString(s1, x, y + 1, color);
                    fontRendererObj.drawString(s1, x + 1, y, color);
                    fontRendererObj.drawString(s1, x + 1, y + 1, color);
                }

                fontRendererObj.drawString(s1, x, y, k);
            }
        }

        boolean unicode = fontRendererObj.getUnicodeFlag();
        String s1 = SHFormatHelper.formatNumber(energy);
        fontRendererObj.setUnicodeFlag(true);
        fontRendererObj.drawString(s1, xSize - 8 - fontRendererObj.getStringWidth(s1), ySize - 104, 4210752);
        fontRendererObj.setUnicodeFlag(unicode);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURES);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        heroList.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (heroList.hoveredIndex >= 0 && heroList.hoveredIndex < heroes.size())
        {
            Hero hero = heroes.get(heroList.hoveredIndex);

            if (hero != null && FabricatorHelper.getMaxTier(mc.thePlayer) < hero.getTier().tier)
            {
                drawHoveringText(Arrays.asList(EnumChatFormatting.RED + I18n.format("gui.suit_fabricator.locked.line1"), EnumChatFormatting.RED + I18n.format("gui.suit_fabricator.locked.line2", hero.getTier().tier - 1)), mouseX, mouseY, fontRendererObj);
            }
        }
    }
}
