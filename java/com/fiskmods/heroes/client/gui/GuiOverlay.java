package com.fiskmods.heroes.client.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.lwjgl.opengl.GL11;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.client.hud.HUDBounds;
import com.fiskmods.heroes.client.hud.HUDElementFlashbang;
import com.fiskmods.heroes.client.hud.HUDElementLightsOut;
import com.fiskmods.heroes.client.hud.HUDElementQuiver;
import com.fiskmods.heroes.client.hud.HUDElementScope;
import com.fiskmods.heroes.client.hud.HUDElementSpellSequence;
import com.fiskmods.heroes.client.hud.HUDElementTreadmill;
import com.fiskmods.heroes.client.hud.HUDLayout;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.config.SHConfig;
import com.fiskmods.heroes.common.data.DataManager;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.data.effect.StatEffect;
import com.fiskmods.heroes.common.data.effect.StatusEffect;
import com.fiskmods.heroes.common.entity.attribute.SHAttributes;
import com.fiskmods.heroes.common.equipment.EnumEquipment;
import com.fiskmods.heroes.common.equipment.EquipmentHandler;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.common.hero.modifier.AbilityEquipment;
import com.fiskmods.heroes.common.hero.modifier.Weakness;
import com.fiskmods.heroes.common.predicate.SHPredicates;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;
import com.fiskmods.heroes.util.SpeedsterHelper;
import com.fiskmods.heroes.util.SpeedsterHelper.SpeedBar;
import com.fiskmods.heroes.util.TemperatureHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeHooks;

public class GuiOverlay extends HUDLayout
{
    public static final ResourceLocation BARS = new ResourceLocation(FiskHeroes.MODID, "textures/gui/bars.png");
    public static final ResourceLocation ICONS = new ResourceLocation(FiskHeroes.MODID, "textures/gui/icons.png");
    public static final ResourceLocation WIDGETS = new ResourceLocation(FiskHeroes.MODID, "textures/gui/widgets.png");

    protected int massWarningTimer;
    protected static final int MAX_MASS_WARNING_TIMER = 15;

    public GuiOverlay()
    {
        put(new HUDElementFlashbang(mc), HUDBounds.FULL_SCREEN);
        put(new HUDElementScope(mc), HUDBounds.FULL_SCREEN);

        put(new HUDElementTreadmill(mc), i -> i.w / 2, i -> 10);
        put(new HUDElementQuiver(mc), i -> SHConfig.quiverHotbarAlignLeft ? 0 : i.w - 22, i -> i.h / 2 - 51, i -> 22, i -> 102);
//        put(new HUDElementFocus(mc), i -> i.w / 2 - 10, i -> i.h / 2 + 20, i -> 84, i -> 13); // TODO: 1.4 Combat
        put(new HUDElementLightsOut(mc), i -> i.w / 2, i -> i.h / 2, i -> 60, i -> 60);
        put(new HUDElementSpellSequence(mc), i -> i.w / 2, i -> i.h / 2 - 20);
    }

    @Override
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event)
    {
        super.onRenderGameOverlayPost(event);
        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();

        if (event.type == ElementType.HOTBAR)
        {
            renderIcons(event, width, height, mc.thePlayer);
            boolean flag = mc.playerController.shouldDrawHUD();

            if (flag)
            {
                renderArmorProtection(event, width, height, mc.thePlayer);
            }

            renderCryokinesis(event, width, height, mc.thePlayer, flag);
            renderShrinkCooldown(event, width, height, mc.thePlayer, flag);
            renderCooldown(Ability.STEEL_TRANSFORMATION, 172, 0, SHData.STEEL_COOLDOWN.get(mc.thePlayer), width, height, mc.thePlayer, flag);
            renderCooldown(Ability.UMBRAKINESIS, 0, 10, SHData.SHADOWFORM_COOLDOWN.get(mc.thePlayer), width, height, mc.thePlayer, flag);
            renderCooldown(Ability.ABSOLUTE_INTANGIBILITY, 81, 10, SHData.INTANGIBILITY_COOLDOWN.get(mc.thePlayer), width, height, mc.thePlayer, flag);
            renderCooldown(SHPredicates.heroPred(t -> t.getKeyBinding(Ability.KEY_TRANSFORM) > 0), 162, 10, SHData.TRANSFORM_COOLDOWN.get(mc.thePlayer), width, height, mc.thePlayer, flag);

            if (flag)
            {
                renderTemperature(event, width, height, mc.thePlayer);
            }
        }
    }

    public void renderIcons(RenderGameOverlayEvent.Post event, int width, int height, EntityPlayer player)
    {
        GL11.glColor4f(1, 1, 1, 1);
        Hero hero = SHHelper.getHero(player);
        List<Ability> activeAbilities = new ArrayList<>();

        int x = SHConfig.hudAlignLeft ? 0 : width;
        int y = SHConfig.hudAlignTop ? 0 : height;

        if (hero != null)
        {
            for (Ability ability : hero.getAbilities())
            {
                if (ability.renderIcon(player))
                {
                    activeAbilities.add(ability);
                }
            }

            mc.getTextureManager().bindTexture(ICONS);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            int size = activeAbilities.size();

            if (size > 0)
            {
                y += SHConfig.hudAlignTop ? 0 : -22;
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(0, 0, 0, 1);
                drawTexturedModalRect(x - (SHConfig.hudAlignLeft ? 0 : size * 22 + 1), y - (SHConfig.hudAlignTop ? 0 : 1), 0, 0, size * 22 + 1, 23);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
            else
            {
                y += SHConfig.hudAlignTop ? 5 : -5;
            }

            for (int i = 0; i < size; ++i)
            {
                Ability ability = activeAbilities.get(i);
                int x1 = x + (SHConfig.hudAlignLeft ? (size - 1) * 22 : -22) - i * 22;
                int y1 = y;

                GL11.glColor4f(1, 1, 1, 1);
                mc.getTextureManager().bindTexture(ICONS);
                drawTexturedModalRect(x1, y1, size > 2 ? i == size - 1 ? 22 : i == 0 ? 66 : 44 : size == 2 ? i == 0 ? 66 : 22 : 0, 234, 22, 22);

                if (ability instanceof AbilityEquipment)
                {
                    EnumEquipment type = SHHelper.getUtilityBelt(player);

                    if (type != EnumEquipment.FISTS)
                    {
                        float f = EquipmentHandler.getCooldownCompletion(type);
                        int progress = (int) (f * 18);

                        GL11.glColor4f(0, 0, 0, 1);
                        drawTexturedModalRect(x1 + 2, y1 + 2, 18 + type.ordinal() * 18, 18, 18, 18);
                        GL11.glColor4f(1, 1, 1, 1);

                        if (progress > 0)
                        {
                            drawTexturedModalRect(x1 + 2, y1 + 2, 0, 18, progress, 18);
                            drawTexturedModalRect(x1 + 2, y1 + 2, 18 + type.ordinal() * 18, 18, progress, 18);
                        }

                        int usesLeft = type.maxUses - EquipmentHandler.timesUsed[type.ordinal()];
                        drawString(mc.fontRenderer, (usesLeft <= 0 ? EnumChatFormatting.RED : "") + "" + usesLeft, x1 + 21 - mc.fontRenderer.getStringWidth(usesLeft + ""), y1 + 22 - mc.fontRenderer.FONT_HEIGHT, -1);
                    }
                }
                else
                {
                    boolean flag = ability.isActive(player);

                    if (!flag)
                    {
                        GL11.glColor4f(1, 0.25F, 0.25F, 1);
                    }

                    drawTexturedModalRect(x1 + 2, y1 + 2, ability.getX(), ability.getY(), 18, 18);

                    if (!flag)
                    {
                        GL11.glColor4f(1, 1, 1, 1);
                        drawTexturedModalRect(x1 + 2, y1 + 2, 0, 0, 18, 18);
                    }
                }
            }

            if (size > 0)
            {
                y += SHConfig.hudAlignTop ? 23 + 5 : -5;
            }

            if (activeAbilities.contains(Ability.SUPER_SPEED) && SHData.SPEED.get(player) >= 0)
            {
                List<SpeedBar> list = SpeedsterHelper.getSpeedSettingList(player);
                Map<SpeedBar, Integer> map = new LinkedHashMap<>();

                for (SpeedBar bar : SpeedBar.values())
                {
                    map.put(bar, SpeedsterHelper.getMaxSpeedSetting(player, bar));
                }

                int cap = 10;
                int rows = 0;
                int index = 0;
                int column = 0;

                int y1 = y;
                int x1 = x + (SHConfig.hudAlignLeft ? 2 : -2);
                int maxWidth = 0;

                for (Map.Entry<SpeedBar, Integer> e : map.entrySet())
                {
                    int bars = e.getValue();

                    if (bars > 0)
                    {
                        maxWidth = Math.max(maxWidth, 3 + Math.min(bars, cap - 1) * 4);
                        rows += Math.max(bars / cap, 1);
                    }
                }

                for (Map.Entry<SpeedBar, Integer> e : map.entrySet())
                {
                    int bars = e.getValue();

                    if (bars >= 0)
                    {
                        for (int i = 0; i <= bars; ++i)
                        {
                            if (column == 0)
                            {
                                GL11.glColor4f(1, 1, 1, 1);
                                drawBox(x1, y - (SHConfig.hudAlignTop ? 0 : (rows + 1) * 4), 3 + Math.min(bars - i, cap - 1) * 4);
                            }

                            drawBar(x1 - (SHConfig.hudAlignLeft ? -column * 4 - 1 : (column + 1) * 4), y + 1 - (SHConfig.hudAlignTop ? 0 : (rows + 1) * 4), e.getKey().getColor(player), index <= SHData.SPEED.get(player));
                            ++index;

                            if (++column >= cap && i < bars)
                            {
                                y += 4;
                                column = 0;
                            }
                        }

                        y += 4;
                        column = 0;
                    }
                }

                String s = SHData.SPEED.get(player) + 1 + "";
                drawString(mc.fontRenderer, s, x - (SHConfig.hudAlignLeft ? -maxWidth - 8 : maxWidth + 8 + mc.fontRenderer.getStringWidth(s)), !SHConfig.hudAlignTop ? Math.min(y1 - (rows + 1) * 2, y1 + 4) - mc.fontRenderer.FONT_HEIGHT / 2 : Math.max(y1 - mc.fontRenderer.FONT_HEIGHT / 2 + (rows + 1) * 2, y1) - 1, -1);

                if (rows == 1)
                {
                    y += SHConfig.hudAlignTop ? 4 : -4;
                }

                y += SHConfig.hudAlignTop ? 8 : y1 - y - (rows + 1) * 4 - 8;
            }
        }
        else
        {
            y += SHConfig.hudAlignTop ? 5 : -5;
        }

        if (SpeedsterHelper.canPlayerRun(player))
        {
            y -= SHConfig.hudAlignTop ? 0 : mc.fontRenderer.FONT_HEIGHT;

            int kmph = (int) Math.round(DataManager.getVelocity(player));
            float multiplier = kmph / SpeedsterHelper.getPlayerTopSpeed(player);
            int barWidth = Math.min((int) (100 * multiplier), 100);

            String s = SHConfig.useMiles ? Math.round(kmph * 0.621371192F) + " mph" : kmph + " km/h";
            drawString(mc.fontRenderer, s, SHConfig.hudAlignLeft ? 4 : width - 4 - mc.fontRenderer.getStringWidth(s), y, -1);

            y += SHConfig.hudAlignTop ? mc.fontRenderer.FONT_HEIGHT + 3 : -3;
            Vec3 color = SpeedsterHelper.getTrailLightning(player).getVecColor(player);
            float shadeFactor = 0.25F;

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            SHRenderHelper.setGlColor(VectorHelper.multiply(color, shadeFactor));
            drawTexturedModalRect(SHConfig.hudAlignLeft ? 4 : width - 4 - barWidth + 1, y - (SHConfig.hudAlignTop ? 0 : 3) + 1, 0, 0, barWidth, 2);
            SHRenderHelper.setGlColor(color);
            drawTexturedModalRect(SHConfig.hudAlignLeft ? 4 : width - 4 - barWidth, y - (SHConfig.hudAlignTop ? 0 : 3), 0, 0, barWidth, 2);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1, 1, 1, 1);
            y += SHConfig.hudAlignTop ? 8 : -8;
        }

        int cooldown = EquipmentHandler.fade;
        int max = EquipmentHandler.FADE_MAX;

        if (cooldown > 0)
        {
            mc.getTextureManager().bindTexture(ICONS);
            int x1 = width / 2 - 18;
            int y1 = height / 2;
            float alpha = (float) (cooldown > max / 2 ? max : cooldown * 2) / max;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1, 1, 1, alpha);

            GL11.glPushMatrix();
            GL11.glTranslatef(-x1, -y1, 0);
            GL11.glScalef(2, 2, 2);
            GL11.glTranslatef(x1, y1, 0);
            drawTexturedModalRect(0, 0, 18 + SHHelper.getUtilityBelt(player).ordinal() * 18, 18, 18, 18);
            GL11.glPopMatrix();
            GL11.glColor4f(1, 1, 1, 1);
        }

        boolean prevUnicodeFlag = mc.fontRenderer.getUnicodeFlag();
        mc.fontRenderer.setUnicodeFlag(true);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

        for (StatusEffect status : StatusEffect.get(player))
        {
            StatEffect e = status.effect;

            if (status.duration < 0)
            {
                continue;
            }

            int left = x - (SHConfig.hudAlignLeft ? -3 : 24 + 3);
            int top = y - (SHConfig.hudAlignTop ? 0 : 24);

            mc.getTextureManager().bindTexture(ICONS);
            drawTexturedModalRect(left, top, 0, 138, 24, 24);

            if (e != null)
            {
                if (e.hasStatusIcon())
                {
                    int index = e.getStatusIconIndex();
                    int i = status.duration;

                    GL11.glColor4f(1, 1, 1, i > 100 || i == 0 ? 1 : 0.5F + MathHelper.sin((i - event.partialTicks) * (float) Math.PI * 0.125F) * 0.5F);
                    drawTexturedModalRect(left + 3, top + 3, index % 8 * 18, 162 + index / 8 * 18, 18, 18);
                    GL11.glColor4f(1, 1, 1, 1);
                }

                String s = e.getFormattedString(status);
                String s1 = e.getDurationString(status);
                drawString(mc.fontRenderer, s, left + (SHConfig.hudAlignLeft ? 24 + 5 : -mc.fontRenderer.getStringWidth(s) - 5), top + 3, -1);
                drawString(mc.fontRenderer, s1, left + (SHConfig.hudAlignLeft ? 24 + 5 : -mc.fontRenderer.getStringWidth(s1) - 5), top + mc.fontRenderer.FONT_HEIGHT + 2, -1);
            }

            y += SHConfig.hudAlignTop ? 26 : -26;
        }

        mc.fontRenderer.setUnicodeFlag(prevUnicodeFlag);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_BLEND);

//        if (hero != null) TODO: Implement properly
//        {
//            int i = 0;
//
//            for (SHKeyBinding k : SHKeyBinds.KEY_MAPPINGS.values())
//            {
//                if (!k.keyDescription.equals(k.defaultKeyDescription))
//                {
//                    mc.fontRenderer.drawString(String.format("%s: %s", Keyboard.getKeyName(k.getKeyCode()), I18n.format(k.keyDescription)), 20, 20 + i * 10, -1);
//                    ++i;
//                }
//            }
//        }
    }

    public void drawBar(int x, int y, Vec3 color, boolean filled)
    {
        float contrast = filled ? 0.3F : 0.4F;
        float lightness = filled ? 1 : 0.25F;

        GL11.glColor4d((color.xCoord + contrast) * lightness, (color.yCoord + contrast) * lightness, (color.zCoord + contrast) * lightness, 1);
        drawTexturedModalRect(x, y, 91, 254, 3, 2);

        GL11.glColor4d(color.xCoord * lightness, color.yCoord * lightness, color.zCoord * lightness, 1);
        drawTexturedModalRect(x, y, 94, 254, 3, 2);

        GL11.glColor4d((color.xCoord - contrast) * lightness, (color.yCoord - contrast) * lightness, (color.zCoord - contrast) * lightness, 1);
        drawTexturedModalRect(x, y, 97, 254, 3, 2);
    }

    public void drawBox(int x, int y, int width)
    {
        int u = 89;
        int v = 252;
        int height = 4;

        if (!SHConfig.hudAlignLeft)
        {
            x -= width + 2;
        }

        drawTexturedModalRect(x, y, 88, 252, 1, 4);
        drawTexturedModalRect(x + width + 1, y, 90, 252, 1, 4);

        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 1, y + height, zLevel, u * f, (v + height) * f);
        tessellator.addVertexWithUV(x + 1 + width, y + height, zLevel, u * f, (v + height) * f);
        tessellator.addVertexWithUV(x + 1 + width, y, zLevel, u * f, v * f);
        tessellator.addVertexWithUV(x + 1, y, zLevel, u * f, v * f);
        tessellator.draw();
    }

    public void drawLightning(int x, int y, Vec3 color)
    {
        float contrast = 0.4F;

        SHRenderHelper.setGlColor(color.addVector(contrast, contrast, contrast));
        drawTexturedModalRect(x, y, 0, 223, 11, 11);
        SHRenderHelper.setGlColor(color);
        drawTexturedModalRect(x, y, 11, 223, 11, 11);
        SHRenderHelper.setGlColor(color.addVector(-contrast, -contrast, -contrast));
        drawTexturedModalRect(x, y, 22, 223, 11, 11);
    }

    public void renderArmorProtection(RenderGameOverlayEvent.Post event, int width, int height, EntityPlayer player)
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1, 1, 1, 1);
        Hero hero = SHHelper.getHero(player);
        float amount = SHAttributes.getArmorProtection(player);

        if (amount > 0 || hero != null)
        {
            int barWidth = Math.round(amount * 79);
            int left = width / 2 - 91;
            int top = height - GuiIngameForge.left_height;

            if (ForgeHooks.getTotalArmorValue(mc.thePlayer) == 0)
            {
                top += 10;
            }

            mc.getTextureManager().bindTexture(ICONS);
            drawTexturedModalRect(left, top, 0, 50, 81, 9);

            if (barWidth > 0)
            {
                int barWidth1 = 0;

                if (hero != null && hero.hasEnabledModifier(player, Weakness.METAL_SKIN) && hero.hasEnabledModifier(player, Ability.FIRE_IMMUNITY))
                {
                    barWidth1 = barWidth - Math.round(SHData.METAL_HEAT.interpolate(player) * barWidth);

                    if (!Ability.FIRE_IMMUNITY.isActive(player))
                    {
                        barWidth1 = 0;
                    }

                    if (barWidth1 > 0)
                    {
                        barWidth -= barWidth1;
                    }
                }

                if (barWidth > 0)
                {
                    drawTexturedModalRect(left + 1 + barWidth1, top + 1, 81 + barWidth1, 50, barWidth, 7);
                }

                if (barWidth1 > 0)
                {
                    drawTexturedModalRect(left + 1, top + 1, 160, 50, barWidth1, 7);
                }
            }

            GuiIngameForge.left_height += 10;
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    public void renderCryokinesis(RenderGameOverlayEvent.Post event, int width, int height, EntityPlayer player, boolean flag)
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1, 1, 1, 1);
        Hero hero = SHHelper.getHero(player);
        float amount = SHData.CRYO_CHARGE.get(player);

        if (hero != null && hero.hasEnabledModifier(player, Ability.CRYOKINESIS))
        {
            mc.getTextureManager().bindTexture(ICONS);

            if (flag)
            {
                int left = width / 2 - 91;
                int top = height - GuiIngameForge.left_height;

                if (ForgeHooks.getTotalArmorValue(mc.thePlayer) == 0)
                {
                    top += 10;
                }

                drawTexturedModalRect(left, top, 0, 59, 81, 9);
                drawTexturedModalRect(left + 1, top + 1, 81, 59, (int) (79 * amount), 7);
                GuiIngameForge.left_height += 10;
            }
            else
            {
                int left = width / 2 - 41;
                int top = height - GuiIngameForge.left_height + 6;

                drawTexturedModalRect(left, top, 0, 59, 81, 9);
                drawTexturedModalRect(left + 1, top + 1, 81, 59, (int) (79 * amount), 7);
                GuiIngameForge.left_height += 10;
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    public void renderShrinkCooldown(RenderGameOverlayEvent.Post event, int width, int height, EntityPlayer player, boolean flag)
    {
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        float t = SHData.QR_TIMER.get(player);

        if (t > 0)
        {
            mc.getTextureManager().bindTexture(BARS);

            if (flag)
            {
                int left = width / 2 - 91;
                int top = height - GuiIngameForge.left_height;

                if (ForgeHooks.getTotalArmorValue(mc.thePlayer) == 0)
                {
                    top += 14;
                }

                drawTexturedModalRect(left, top, 91, 0, 81, 5);

                GL11.glColor4f(t < 0.5F ? t * 2 : 1, t >= 0.5F ? 1 - (t - 0.5F) * 2 : 1, 0, 1);
                drawTexturedModalRect(left, top, 91, 5, (int) (81 * (1 - t)), 5);
                GuiIngameForge.left_height += 6;
            }
            else
            {
                int left = width / 2 - 41;
                int top = height - GuiIngameForge.left_height + 10;

                drawTexturedModalRect(left, top, 91, 0, 81, 5);

                GL11.glColor4f(t < 0.5F ? t * 2 : 1, t >= 0.5F ? 1 - (t - 0.5F) * 2 : 1, 0, 1);
                drawTexturedModalRect(left, top, 91, 5, (int) (81 * (1 - t)), 5);
                GuiIngameForge.left_height += 6;
            }
        }

        if (massWarningTimer > 0)
        {
            float f = massWarningTimer;
            f = f > MAX_MASS_WARNING_TIMER / 2 ? MAX_MASS_WARNING_TIMER - f : f;
            float opacity = MathHelper.clamp_float(f / MAX_MASS_WARNING_TIMER * 1.75F, 0, 1);
            int color = 0xFF0000 | Math.round(255 * opacity) << 24;
            float x = width / 2;
            float y = height / 2 - 60;
            float scale = 4;

            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, 0);
            GL11.glScalef(scale, scale, scale);
            drawCenteredString(mc.fontRenderer, I18n.format("gui.shrink_warning.line1"), 0, 0, color);
            GL11.glTranslatef(-x, -y, 0);
            GL11.glPopMatrix();

            x = width / 2;
            y = height / 2 - 20;
            scale = 2;
            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, 0);
            GL11.glScalef(scale, scale, scale);
            drawCenteredString(mc.fontRenderer, I18n.format("gui.shrink_warning.line2"), 0, 0, color);
            GL11.glTranslatef(-x, -y, 0);
            GL11.glPopMatrix();
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void renderCooldown(Predicate<Entity> p, int u, int v, float t, int width, int height, EntityPlayer player, boolean flag)
    {
        if (t > 0 && p.test(player))
        {
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            mc.getTextureManager().bindTexture(BARS);

            if (flag)
            {
                int left = width / 2 - 91;
                int top = height - GuiIngameForge.left_height;

                if (ForgeHooks.getTotalArmorValue(mc.thePlayer) == 0)
                {
                    top += 14;
                }

                drawTexturedModalRect(left, top, u, v, 81, 5);
                drawTexturedModalRect(left, top, u, v + 5, (int) (81 * (1 - t)), 5);
                GuiIngameForge.left_height += 6;
            }
            else
            {
                int left = width / 2 - 41;
                int top = height - GuiIngameForge.left_height + 10;

                drawTexturedModalRect(left, top, u, v, 81, 5);
                drawTexturedModalRect(left, top, u, v + 5, (int) (81 * (1 - t)), 5);
                GuiIngameForge.left_height += 6;
            }

            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    public void renderTemperature(RenderGameOverlayEvent.Post event, int width, int height, EntityPlayer player)
    {
        GL11.glEnable(GL11.GL_BLEND);
        int amount = TemperatureHelper.getTemperatureForGui();
        boolean cold = false;

        if (amount < 0)
        {
            amount *= -1;
            cold = true;
        }

        if (amount > 0)
        {
            mc.getTextureManager().bindTexture(ICONS);

            int left = width / 2 - 91;
            int top = height - GuiIngameForge.left_height;

            if (ForgeHooks.getTotalArmorValue(mc.thePlayer) == 0)
            {
                top += 10;
            }

            for (int i = 0; i < amount; ++i)
            {
                drawTexturedModalRect(left + i * 12, top, cold ? 0 : 9, 88, 9, 9);
            }

            GuiIngameForge.left_height += 10;
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    public void updateTick()
    {
        if (mc.thePlayer != null && !mc.isGamePaused())
        {
            if (SHData.QR_TIMER.get(mc.thePlayer) > 0.8F && Rule.ALLOW_QR.get(mc.thePlayer) && massWarningTimer == 0)
            {
                massWarningTimer = MAX_MASS_WARNING_TIMER;
            }

            if (massWarningTimer > 0)
            {
                --massWarningTimer;
            }
        }
    }
}
