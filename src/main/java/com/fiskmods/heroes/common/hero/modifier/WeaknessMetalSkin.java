package com.fiskmods.heroes.common.hero.modifier;

import java.util.function.Function;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.SHData;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

public class WeaknessMetalSkin extends Weakness
{
    public static final int WATER_COOLING_TICKS = 30;
    public static final int AIR_COOLING_TICKS = 300;

    public static final int COOLDOWN_TICKS = 200;

    @Override
    public void onUpdateGlobal(EntityLivingBase entity, Hero hero, Phase phase, boolean hasWeakness)
    {
        if (phase == Phase.END)
        {
            float prevHeat = SHData.METAL_HEAT.get(entity);

            if (hasWeakness)
            {
                AxisAlignedBB aabb = entity.boundingBox.contract(0.001, 0.001, 0.001);

                if (entity.isInWater())
                {
                    if (SHData.METAL_HEAT.get(entity) > 0)
                    {
                        if (entity.ticksExisted % 2 == 0)
                        {
                            entity.playSound("random.fizz", 1, 0.8F + 0.6F * (1 - SHData.METAL_HEAT.get(entity)) + (entity.getRNG().nextFloat() - entity.getRNG().nextFloat()) * 0.4F);
                        }

                        SHData.METAL_HEAT.incrWithoutNotify(entity, -1F / WeaknessMetalSkin.WATER_COOLING_TICKS);

                        for (int i = 0; i < 3; ++i)
                        {
                            entity.worldObj.spawnParticle("largesmoke", entity.posX + (entity.getRNG().nextDouble() - 0.5D) * entity.width, entity.boundingBox.minY + entity.getRNG().nextDouble() * entity.height, entity.posZ + (entity.getRNG().nextDouble() - 0.5D) * entity.width, 0, 0, 0);
                        }
                    }
                }
                else if (entity.worldObj.func_147470_e(aabb) && entity.worldObj.isAnyLiquid(aabb))
                {
                    HeatSource.LAVA.applyHeat(entity);
                }
            }

            if (SHData.METAL_HEAT.get(entity) >= 1 && SHData.METAL_HEAT_COOLDOWN.get(entity) <= 0)
            {
                SHData.METAL_HEAT_COOLDOWN.setWithoutNotify(entity, WeaknessMetalSkin.COOLDOWN_TICKS);
            }

            if (SHData.METAL_HEAT_COOLDOWN.get(entity) > 0)
            {
                SHData.METAL_HEAT_COOLDOWN.incrWithoutNotify(entity, -1);
            }

            if (prevHeat == SHData.METAL_HEAT.get(entity))
            {
                if (SHData.METAL_HEAT.get(entity) > 0 && SHData.METAL_HEAT_COOLDOWN.get(entity) <= 0)
                {
                    SHData.METAL_HEAT.incrWithoutNotify(entity, -1F / WeaknessMetalSkin.AIR_COOLING_TICKS);
                }
            }

            SHData.METAL_HEAT.clampWithoutNotify(entity, 0.0F, 1.0F);
        }
    }

    private static void applyHeat(EntityLivingBase entity, HeatSource source, boolean withNotify)
    {
        if (SHHelper.hasEnabledModifier(entity, Weakness.METAL_SKIN))
        {
            if (SHData.METAL_HEAT.get(entity) < 1)
            {
                SHData.METAL_HEAT.incrWithoutNotify(entity, 1F / source.apply(entity));
            }
        }
    }

    public enum HeatSource implements Function<Entity, Integer>
    {
        LAVA(Rule.TICKS_METALSKIN_LAVA),
        HEAT_VISION(Rule.TICKS_METALSKIN_HEATVISION),
        ENERGY_PROJECTION(Rule.TICKS_METALSKIN_HEATVISION),
        FLAME_BLAST(Rule.TICKS_METALSKIN_FLAMEBLAST),
        HEAT_GUN(Rule.TICKS_METALSKIN_HEATGUN),
        BLAZE_ARROW(Rule.TICKS_METALSKIN_BLAZEARROW);

        private final Function<Entity, Integer> ticks;

        private HeatSource(Function<Entity, Integer> func)
        {
            ticks = func;
        }

        private HeatSource(Rule<Integer> rule)
        {
            this(rule::get);
        }

        public void applyHeat(EntityLivingBase entity)
        {
            WeaknessMetalSkin.applyHeat(entity, this, false);
        }

        public void applyHeatWithNotify(EntityLivingBase entity)
        {
            WeaknessMetalSkin.applyHeat(entity, this, true);
        }

        public void applyHeat(Entity entity)
        {
            if (entity instanceof EntityLivingBase)
            {
                applyHeat((EntityLivingBase) entity);
            }
        }

        @Override
        public Integer apply(Entity t)
        {
            return ticks.apply(t);
        }
    }
}
