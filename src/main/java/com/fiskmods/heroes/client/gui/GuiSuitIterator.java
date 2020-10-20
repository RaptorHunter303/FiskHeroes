package com.fiskmods.heroes.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.gui.GuiStringList.IGuiStringList;
import com.fiskmods.heroes.client.render.item.RenderItemMiniAtomSuit;
import com.fiskmods.heroes.common.container.ContainerSuitIterator;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.network.MessageSuitIteration;
import com.fiskmods.heroes.common.network.SHNetworkManager;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class GuiSuitIterator extends GuiContainer implements IGuiStringList, ICrafting
{
    private static final ResourceLocation guiTextures = new ResourceLocation(FiskHeroes.MODID, "textures/gui/container/suit_iterator.png");

    private final ContainerSuitIterator container;
    private final List<HeroIteration> iterations = new ArrayList<>();
    private GuiStringList iterationList;

    public GuiSuitIterator(InventoryPlayer inventory, World world, int x, int y, int z)
    {
        super(new ContainerSuitIterator(inventory, world, x, y, z));
        container = (ContainerSuitIterator) inventorySlots;
        ySize = 188;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        iterationList = new GuiStringList<>(this, iterations, guiLeft + 109, guiTop + 18, 52, 70).setFormat(HeroIteration::getLocalizedIterName, t -> -1);
        inventorySlots.removeCraftingFromCrafters(this);
        inventorySlots.addCraftingToCrafters(this);

        iterations.clear();
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        inventorySlots.removeCraftingFromCrafters(this);
    }

    @Override
    public void elementClicked(int index, boolean doubleClick)
    {
        HeroIteration iter = iterations.get(index);
        updateIteration(iter != null ? iter.getId() : -1);
    }

    @Override
    public boolean isSelected(int index)
    {
        HeroIteration iter = iterations.get(index);
        return iter != null && iter.getId() == container.iterationId;
    }

    private void updateIterations()
    {
        HeroIteration iter = SHHelper.getHeroIter(SHHelper.getEquipment(container.input, 0));

        if (iter == null)
        {
            iterations.clear();
        }
        else if (iterations.isEmpty())
        {
            iter.hero.getIterations().stream().sorted().forEach(iterations::add);
        }
    }

    private void updateIteration(int id)
    {
        container.updateIteration(id);
        SHNetworkManager.wrapper.sendToServer(new MessageSuitIteration(mc.thePlayer, id));
    }

    @Override
    public void sendContainerAndContentsToPlayer(Container container, List list)
    {
        updateIterations();
    }

    @Override
    public void sendSlotContents(Container container, int slot, ItemStack itemstack)
    {
        updateIterations();
    }

    @Override
    public void sendProgressBarUpdate(Container container, int id, int value)
    {
        if (id == 0)
        {
            updateIterations();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = I18n.format("gui.suit_iterator");
        fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 94, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(guiTextures);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        HeroIteration iter = SHHelper.getHeroIter(SHHelper.getEquipment(container.input, 0));

        if (iter != null)
        {
            EntityPlayer player = RenderItemMiniAtomSuit.fakePlayer;

            if (player != null)
            {
                ItemStack[] stacks = iter.createArmorStacks();
                int x = guiLeft + 80;
                int y = guiTop + 85;
                float scale = 30;

                for (int i = 0; i < stacks.length; ++i)
                {
                    player.inventory.armorInventory[i] = stacks[3 - i];
                }

                GL11.glEnable(GL11.GL_COLOR_MATERIAL);
                GL11.glPushMatrix();
                GL11.glTranslatef(x, y, scale);
                GL11.glScalef(-scale, scale, scale);
                GL11.glRotatef(180, 0, 0, 1);
                RenderHelper.enableStandardItemLighting();
                GL11.glTranslatef(0, player.yOffset + 0.075F, 10);
                GL11.glRotatef(10, 1, 0, 0);
                GL11.glRotatef((mc.thePlayer.ticksExisted + partialTicks) * 2, 0, 1, 0);
                RenderManager.instance.playerViewY = 180;
                RenderManager.instance.renderEntityWithPosYaw(player, 0, 0, 0, 0, 1);
                GL11.glPopMatrix();
                RenderHelper.disableStandardItemLighting();
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            }
        }

        iterationList.drawScreen(mouseX, mouseY, partialTicks);
    }
}
