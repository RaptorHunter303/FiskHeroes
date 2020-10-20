package com.fiskmods.heroes.common.spell;

import java.util.List;

import com.fiskmods.heroes.client.particle.EntitySHSpellWaveFX;
import com.fiskmods.heroes.client.render.hero.HeroRenderer;
import com.fiskmods.heroes.client.render.hero.effect.HeroEffectSpellcasting;
import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.damagesource.ModDamageSources;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.entity.EntitySpellDuplicate;
import com.fiskmods.heroes.common.hero.HeroIteration;
import com.fiskmods.heroes.common.interaction.Interaction;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.SHRenderHelper;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class SpellAtmospheric extends Spell
{
    public SpellAtmospheric()
    {
        super(Cooldown.SPELL_ATMOSPHERIC, "awds", true);
    }

    @Override
    public void onTrigger(EntityLivingBase caster)
    {
        if (!caster.worldObj.isRemote)
        {
            double range = 64;
            Vec3 look = caster.getLook(1);
            AxisAlignedBB bounds = caster.boundingBox.addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range).expand(1, 1, 1);
            List<Entity> list = caster.worldObj.getEntitiesWithinAABBExcludingEntity(caster, bounds, IEntitySelector.selectAnything);
            EntityLivingBase realCaster = SHHelper.filterDuplicate(caster);
            Float dmg = null;

            for (Entity entity : list)
            {
                if (canTarget(caster, entity))
                {
                    Vec3 vec3 = look.normalize();
                    Vec3 vec31 = Vec3.createVectorHelper(entity.posX - caster.posX, entity.boundingBox.minY + entity.getEyeHeight() - (caster.posY + caster.getEyeHeight()), entity.posZ - caster.posZ);
                    double d0 = vec31.lengthVector();
                    double d1 = vec3.dotProduct(vec31.normalize());

                    if (d1 > 1 - 0.9 / d0 && caster.canEntityBeSeen(entity))
                    {
                        double d = Math.pow(entity.getDistanceToEntity(caster), 1.0 / 4) / 2;

                        if (d < 1.2)
                        {
                            if (entity instanceof EntityLivingBase)
                            {
                                if (dmg == null)
                                {
                                    dmg = Rule.DMG_SPELL_ATMOSPHERIC.get(realCaster);
                                }
                                
                                entity.hurtResistantTime = 0;
                                entity.attackEntityFrom(ModDamageSources.MAGIC.apply(realCaster), (float) (dmg / d));
                            }

                            entity.motionX += look.xCoord / d;
                            entity.motionY += look.yCoord / d;
                            entity.motionZ += look.zCoord / d;
                        }
                    }
                }
            }
        }
        else
        {
            spawnParticle(caster);
        }
    }

    private boolean canTarget(EntityLivingBase caster, Entity entity)
    {
        return !(entity instanceof EntitySpellDuplicate || caster instanceof EntitySpellDuplicate && ((EntitySpellDuplicate) caster).isOwner(entity));
    }

    @SideOnly(Side.CLIENT)
    private void spawnParticle(EntityLivingBase caster)
    {
        HeroIteration iter = SHHelper.getHeroIter(caster);
        int hex = 0xFF8000;

        if (iter != null)
        {
            HeroRenderer renderer = HeroRenderer.get(iter);
            HeroEffectSpellcasting effect = renderer.getEffect(HeroEffectSpellcasting.class, caster);

            if (effect != null)
            {
                hex = effect.getAtmosphereColor(hex);
            }
        }

        Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySHSpellWaveFX(caster.worldObj, caster.posX, caster.posY + caster.getEyeHeight(), caster.posZ, caster.getLookVec(), SHRenderHelper.hexToRGB(hex)));
    }

    @Override
    public TargetPoint getTargetPoint(EntityLivingBase caster)
    {
        return Interaction.TARGET_ALL;
    }
}
