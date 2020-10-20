package com.fiskmods.heroes.client.hud;

import java.util.StringJoiner;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.spell.KeySequence;
import com.fiskmods.heroes.common.spell.Spell;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class HUDElementSpellSequence extends HUDElement
{
    private static KeySequence.Sequencer sequencer;
    private static KeySequence.Key[] keys;

    private int timeout;

    public HUDElementSpellSequence(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public boolean isVisible(ElementType type)
    {
        return type == ElementType.BOSSHEALTH && sequencer != null;
    }

    @Override
    public void postRender(ElementType type, ScreenInfo screen, int mouseX, int mouseY, float partialTicks)
    {
        if (sequencer != null && keys != null)
        {
            StringJoiner sj = new StringJoiner(" > ");
            int i = sequencer.index;

            for (int j = 0; j < keys.length; ++j)
            {
                KeySequence.Key key = keys[j];
                sj.add((j == i ? EnumChatFormatting.GOLD : j < i ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.WHITE) + GameSettings.getKeyDisplayString(key.get().getKeyCode()));
            }

            String s = sj.toString();
            x -= mc.fontRenderer.getStringWidth(s) / 2;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(0, 0, 0, 0.2F);
            drawTexturedModalRect(x - 2, y - 2, 0, 0, mc.fontRenderer.getStringWidth(s) + 4, mc.fontRenderer.FONT_HEIGHT + 4);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            drawOutlinedText(s, x, y, -1, 1);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    public void drawOutlinedText(String s, int x, int y, int color, float opacity)
    {
        String s1 = s.replaceAll("\u00a7[0-9a-f]", "");
        int alpha = Math.max(MathHelper.ceiling_float_int(255 * opacity), 4) << 24;

        mc.fontRenderer.drawString(s1, x + 1, y, alpha);
        mc.fontRenderer.drawString(s1, x - 1, y, alpha);
        mc.fontRenderer.drawString(s1, x, y + 1, alpha);
        mc.fontRenderer.drawString(s1, x, y - 1, alpha);
        mc.fontRenderer.drawString(s, x, y, color | alpha);
    }

    @Override
    public void updateTick()
    {
        if (!mc.isGamePaused() && sequencer != null)
        {
            if (++timeout > 100 || mc.thePlayer.isDead || SHData.SHIELD_BLOCKING.get(mc.thePlayer))
            {
                SHData.SPELL_FRACTION.set(mc.thePlayer, 0F);
                sequencer = null;
                timeout = 0;
            }
        }
    }

    @Override
    public void keyPress()
    {
        if (sequencer != null)
        {
            if (sequencer.keyPress())
            {
                SHData.SPELL_FRACTION.set(mc.thePlayer, (sequencer.index + 1F) / keys.length);
            }

            timeout = 0;
        }
    }

    public static void startSequence(Spell spell)
    {
        sequencer = spell.keySequence.sequence(() -> result(spell, true), () -> result(spell, false));
        keys = spell.keySequence.keys;
    }

    private static void result(Spell spell, boolean success)
    {
        if (success)
        {
            spell.trigger(Minecraft.getMinecraft().thePlayer);
        }
        else
        {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("random.fizz"), 0.8F));
        }

        SHData.SPELL_FRACTION.set(Minecraft.getMinecraft().thePlayer, 0F);
        sequencer = null;
        keys = null;
    }
}
