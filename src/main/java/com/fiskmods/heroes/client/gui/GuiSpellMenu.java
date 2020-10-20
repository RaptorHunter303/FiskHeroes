package com.fiskmods.heroes.client.gui;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.client.hud.HUDElementSpellSequence;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectSpellcasting;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.hero.modifier.AbilitySpellcasting;
import com.fiskmods.heroes.common.spell.Spell;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

@SideOnly(Side.CLIENT)
public class GuiSpellMenu extends GuiRadialMenu<Spell>
{
    public GuiSpellMenu(Iterable<Spell> iter)
    {
        super(iter);
    }

    @Override
    public KeyBinding getKeyBinding()
    {
        Hero hero = SHHelper.getHero(mc.thePlayer);
        return hero != null ? hero.getKey(mc.thePlayer, AbilitySpellcasting.KEY_MENU) : null;
    }

    @Override
    public void render(int index, Spell value, int xPos, int yPos, boolean selected, int mouseX, int mouseY, float partialTicks)
    {
        float f = (value.spellCooldown.get(mc.thePlayer) - partialTicks) / value.spellCooldown.getMax(mc.thePlayer, SHHelper.getHero(mc.thePlayer));

        if (f > 0)
        {
            int x = width / 2;
            int y = height / 2;
            float slice = 360F / slots.size();
            float offset = slice * (index - (1 + f) / 2);
            float[] r = {0, getRadius(index, value, selected, partialTicks)};
            r[0] = r[1] * 0.95F;

            GL11.glColor4f(1, 1, 1, 0.8F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBegin(GL11.GL_QUADS);

            for (int i = 0; i < slice; ++i)
            {
                float rad0 = (float) Math.toRadians(offset + f * i);
                float rad1 = (float) Math.toRadians(offset + f * (i + 1));
                GL11.glVertex2f(x + MathHelper.cos(rad1) * r[0], y + MathHelper.sin(rad1) * r[0]);
                GL11.glVertex2f(x + MathHelper.cos(rad1) * r[1], y + MathHelper.sin(rad1) * r[1]);
                GL11.glVertex2f(x + MathHelper.cos(rad0) * r[1], y + MathHelper.sin(rad0) * r[1]);
                GL11.glVertex2f(x + MathHelper.cos(rad0) * r[0], y + MathHelper.sin(rad0) * r[0]);
            }

            GL11.glEnd();
            GL11.glColor4f(0, 0, 0, 0.8F);
            GL11.glBegin(GL11.GL_LINE_STRIP);

            for (int i = 0; i < slice; ++i)
            {
                float rad0 = (float) Math.toRadians(offset + f * i);
                float rad1 = (float) Math.toRadians(offset + f * (i + 1));
                GL11.glVertex2f(x + MathHelper.cos(rad0) * r[0], y + MathHelper.sin(rad0) * r[0]);
                GL11.glVertex2f(x + MathHelper.cos(rad1) * r[0], y + MathHelper.sin(rad1) * r[0]);
            }

            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1, 1, 1, 1);
        }
    }

    @Override
    public void postRender(int index, Spell value, int xPos, int yPos, boolean selected, int mouseX, int mouseY, float partialTicks)
    {
        String s = value.getLocalizedName();
        boolean flag = !value.spellCooldown.available(mc.thePlayer);

        if (flag)
        {
            s = EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + s;
        }

        int x = width / 2;
        int y = height / 2;
        int x1 = xPos - 4;
        int y1 = yPos;

        if (x1 < x)
        {
            x1 -= fontRendererObj.getStringWidth(s) - 8;
        }

        if (y1 < y)
        {
            y1 -= 9;
        }

        drawString(fontRendererObj, s, x1, y1, -1);

        if (flag)
        {
            x1 = (int) ((xPos - x) * 0.6 + x);
            y1 = (int) ((yPos - y) * 0.6 + y);

            s = EnumChatFormatting.GRAY + StringUtils.ticksToElapsedTime(value.spellCooldown.get(mc.thePlayer));
            drawCenteredString(fontRendererObj, s, x1, y1 - fontRendererObj.FONT_HEIGHT / 2, -1);
        }
    }

    @Override
    public void onSelected(int index, Spell value, boolean mouseClicked)
    {
        if (mouseClicked && value.spellCooldown.available(mc.thePlayer))
        {
            mc.displayGuiScreen(null);
            mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
            HUDElementSpellSequence.startSequence(value);
        }
    }

    @Override
    public int getColor(int index, Spell value, boolean selected)
    {
        return value.spellCooldown.available(mc.thePlayer) ? selected ? getSpellColor() : super.getColor(index, value, selected) : 0x515151;
    }

    private int getSpellColor()
    {
        HeroIteration iter = SHHelper.getHeroIter(mc.thePlayer);
        int hex = 0xFF8000;

        if (iter != null)
        {
            HeroRenderer renderer = HeroRenderer.get(iter);
            HeroEffectSpellcasting effect = renderer.getEffect(HeroEffectSpellcasting.class, mc.thePlayer);

            if (effect != null)
            {
                hex = effect.getGenericColor(hex);
            }
        }

        return hex;
    }
}
