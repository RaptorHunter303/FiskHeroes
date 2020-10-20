//package com.fiskmods.heroes.common.move; // TODO: 1.4 Combat
//
//import org.lwjgl.input.Mouse;
//
//import com.fiskmods.heroes.common.data.SHData;
//import com.fiskmods.heroes.common.hero.Hero;
//import com.fiskmods.heroes.common.move.Move.MouseAction;
//import com.fiskmods.heroes.common.move.Move.MoveActivation;
//import com.fiskmods.heroes.common.move.Move.MoveSensitivity;
//import com.fiskmods.heroes.common.network.MessageActivateMove;
//import com.fiskmods.heroes.common.network.SHNetworkManager;
//import com.fiskmods.heroes.helper.SHHelper;
//import com.google.common.collect.ImmutableMap;
//
//import cpw.mods.fml.common.FMLCommonHandler;
//import cpw.mods.fml.common.eventhandler.SubscribeEvent;
//import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
//import cpw.mods.fml.common.gameevent.TickEvent.Phase;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.settings.KeyBinding;
//import net.minecraft.util.MathHelper;
//import net.minecraftforge.client.event.MouseEvent;
//
//public enum MoveClientHandler
//{
//    INSTANCE;
//
//    private Minecraft mc = Minecraft.getMinecraft();
//    private MoveEntry moveEntry;
//
//    private boolean usingMove;
//    private int clickTimer;
//
//    private long systemTime;
//
//    public static final int FADE_MAX = 25;
//    public int fade;
//
//    @SubscribeEvent
//    public void onMouse(MouseEvent event)
//    {
//        if (mc.currentScreen == null && SHData.COMBAT_MODE.get(mc.thePlayer))
//        {
//            Hero hero = SHHelper.getHero(mc.thePlayer);
//
//            if (!MoveCommonHandler.hasMoveSet(mc.thePlayer, hero))
//            {
//                return;
//            }
//
//            int i = Mouse.getEventButton();
//            KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());
//
//            if (Mouse.getEventButtonState())
//            {
//                KeyBinding.onTick(i - 100);
//            }
//
//            long l = Minecraft.getSystemTime() - systemTime;
//
//            if (l <= 200L)
//            {
//                int j = Mouse.getEventDWheel();
//                int size = hero.getMoveSet().size();
//
//                if (j != 0 && size > 1)
//                {
//                    j = MathHelper.clamp_int(j, -1, 1) + SHData.COMBAT_INDEX.get(mc.thePlayer);
//
//                    if (j >= size)
//                    {
//                        j = 0;
//                    }
//                    else if (j < 0)
//                    {
//                        j = size - 1;
//                    }
//
//                    SHData.COMBAT_INDEX.set(mc.thePlayer, (byte) j);
//                    fade = FADE_MAX;
//                }
//            }
//
//            FMLCommonHandler.instance().fireMouseInput();
//            event.setCanceled(true);
//        }
//    }
//
//    @SubscribeEvent
//    public void onClientTick(ClientTickEvent event)
//    {
//        if (event.phase == Phase.START)
//        {
//            if (!mc.isGamePaused())
//            {
//                if (clickTimer > 0)
//                {
//                    --clickTimer;
//                }
//
//                if (mc.currentScreen == null && !mc.thePlayer.isUsingItem())
//                {
//                    moveEntry = MoveCommonHandler.getMove(mc.thePlayer);
//
//                    if (mc.gameSettings.keyBindAttack.getIsKeyPressed())
//                    {
//                        activate(MouseAction.LEFT_CLICK);
//                    }
//                    else if (mc.gameSettings.keyBindUseItem.getIsKeyPressed())
//                    {
//                        activate(MouseAction.RIGHT_CLICK);
//                    }
//                    else
//                    {
//                        usingMove = false;
//                    }
//                }
//                else
//                {
//                    moveEntry = null;
//                    usingMove = false;
//                }
//            }
//        }
//        else
//        {
//            systemTime = Minecraft.getSystemTime();
//        }
//    }
//
//    private void activate(MoveActivation activation)
//    {
//        if (clickTimer <= 0 && !usingMove && moveEntry != null && moveEntry.move.action.test(activation.button, activation.sensitivity))
//        {
//            float focus = SHData.FOCUS.get(mc.thePlayer);
//            ImmutableMap<String, Number> modifiers = moveEntry.getParent().getModifiers(moveEntry.move, focus);
//
//            if (modifiers != null && moveEntry.move.onActivated(mc.thePlayer, SHHelper.getHero(mc.thePlayer), mc.objectMouseOver, activation, modifiers, focus))
//            {
//                SHData.FOCUS.setWithoutNotify(mc.thePlayer, 0F);
//                SHNetworkManager.wrapper.sendToServer(new MessageActivateMove(mc.thePlayer, mc.objectMouseOver, activation));
//            }
//
//            clickTimer = 4;
//        }
//
//        usingMove = true;
//    }
//
//    private void activate(MouseAction action)
//    {
//        int sensitivity = MoveSensitivity.AIR;
//
//        if (mc.objectMouseOver != null)
//        {
//            switch (mc.objectMouseOver.typeOfHit)
//            {
//            case BLOCK:
//                sensitivity = MoveSensitivity.BLOCK;
//                break;
//            case ENTITY:
//                sensitivity = MoveSensitivity.ENTITY;
//                break;
//            default:
//                break;
//            }
//        }
//
//        activate(MoveActivation.get(action, sensitivity));
//    }
//}
