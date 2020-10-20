//package com.fiskmods.heroes.client.gui;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.lwjgl.input.Keyboard;
//
//import com.fiskmods.heroes.common.tileentity.TileEntityRuleHandler;
//
//import cpw.mods.fml.relauncher.Side;
//import cpw.mods.fml.relauncher.SideOnly;
//import io.netty.buffer.Unpooled;
//import net.minecraft.client.gui.FontRenderer;
//import net.minecraft.client.gui.GuiButton;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.client.gui.GuiTextField;
//import net.minecraft.client.resources.I18n;
//import net.minecraft.command.server.CommandBlockLogic;
//import net.minecraft.network.PacketBuffer;
//import net.minecraft.network.play.client.C17PacketCustomPayload;
//
//@SideOnly(Side.CLIENT)
//public class GuiRuleHandler extends GuiScreen
//{
//    private static final Logger LOGGER = LogManager.getLogger();
//
//    private final TileEntityRuleHandler tileentity;
//
//    private GuiTextField nameField;
//    private GuiTextField outputField;
//
//    private GuiButton doneButton;
//    private GuiButton cancelButton;
//
//    public GuiRuleHandler(TileEntityRuleHandler tile)
//    {
//        tileentity = tile;
//    }
//
//    @Override
//    public void initGui()
//    {
//        Keyboard.enableRepeatEvents(true);
//        buttonList.clear();
//        buttonList.add(doneButton = new GuiButton(0, width / 2 - 4 - 150, height / 4 + 120 + 12, 150, 20, I18n.format("gui.done")));
//        buttonList.add(cancelButton = new GuiButton(1, width / 2 + 4, height / 4 + 120 + 12, 150, 20, I18n.format("gui.cancel")));
//        buttonList.add(cancelButton = new GuiButton(1, width / 2 + 4, height / 4 + 120 + 12, 50, 20, I18n.format("gui.cancel")));
//
//
//        nameField = new GuiTextField(fontRendererObj, width / 2 - 150, 50, 300, 20);
//        nameField.setMaxStringLength(32767);
//        nameField.setFocused(true);
//        nameField.setText(tileentity.func_145753_i());
//        outputField = new GuiTextField(fontRendererObj, width / 2 - 150, 135, 300, 20);
//        outputField.setMaxStringLength(32767);
//        outputField.setEnabled(false);
//        outputField.setText(tileentity.func_145753_i());
//
//        if (tileentity.func_145749_h() != null)
//        {
//            outputField.setText(tileentity.func_145749_h().getUnformattedText());
//        }
//
//        doneButton.enabled = nameField.getText().trim().length() > 0;
//    }
//
//    @Override
//    public void updateScreen()
//    {
//        nameField.updateCursorCounter();
//    }
//
//    @Override
//    public void onGuiClosed()
//    {
//        Keyboard.enableRepeatEvents(false);
//    }
//
//    @Override
//    protected void actionPerformed(GuiButton button)
//    {
//        if (button.enabled)
//        {
//            if (button.id == 1)
//            {
//                mc.displayGuiScreen((GuiScreen) null);
//            }
//            else if (button.id == 0)
//            {
//                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
//
//                try
//                {
//                    packetbuffer.writeByte(tileentity.func_145751_f());
//                    tileentity.func_145757_a(packetbuffer);
//                    packetbuffer.writeStringToBuffer(nameField.getText());
//                    mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|AdvCdm", packetbuffer));
//                }
//                catch (Exception exception)
//                {
//                    LOGGER.error("Couldn\'t send command block info", exception);
//                }
//                finally
//                {
//                    packetbuffer.release();
//                }
//
//                mc.displayGuiScreen(null);
//            }
//        }
//    }
//
//    @Override
//    protected void keyTyped(char c, int key)
//    {
//        nameField.textboxKeyTyped(c, key);
//        outputField.textboxKeyTyped(c, key);
//        doneButton.enabled = nameField.getText().trim().length() > 0;
//
//        if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER)
//        {
//            actionPerformed(doneButton);
//        }
//        else if (key == Keyboard.KEY_ESCAPE)
//        {
//            actionPerformed(cancelButton);
//        }
//    }
//
//    @Override
//    protected void mouseClicked(int mouseX, int mouseY, int button)
//    {
//        super.mouseClicked(mouseX, mouseY, button);
//        nameField.mouseClicked(mouseX, mouseY, button);
//        outputField.mouseClicked(mouseX, mouseY, button);
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks)
//    {
//        drawDefaultBackground();
//        drawCenteredString(fontRendererObj, I18n.format("advMode.setCommand"), width / 2, 20, 16777215);
//        drawString(fontRendererObj, I18n.format("advMode.command"), width / 2 - 150, 37, 10526880);
//        nameField.drawTextBox();
//        byte b0 = 75;
//        byte b1 = 0;
//        FontRenderer fontrenderer = fontRendererObj;
//        String s = I18n.format("advMode.nearestPlayer");
//        int i1 = width / 2 - 150;
//        int l = b1 + 1;
//        drawString(fontrenderer, s, i1, b0 + b1 * fontRendererObj.FONT_HEIGHT, 10526880);
//        drawString(fontRendererObj, I18n.format("advMode.randomPlayer"), width / 2 - 150, b0 + l++ * fontRendererObj.FONT_HEIGHT, 10526880);
//        drawString(fontRendererObj, I18n.format("advMode.allPlayers"), width / 2 - 150, b0 + l++ * fontRendererObj.FONT_HEIGHT, 10526880);
//
//        if (outputField.getText().length() > 0)
//        {
//            int k = b0 + l * fontRendererObj.FONT_HEIGHT + 20;
//            drawString(fontRendererObj, I18n.format("advMode.previousOutput"), width / 2 - 150, k, 10526880);
//            outputField.drawTextBox();
//        }
//
//        super.drawScreen(mouseX, mouseY, partialTicks);
//    }
//}
