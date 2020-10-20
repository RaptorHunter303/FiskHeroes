package com.fiskmods.heroes.client.render.effect;

import com.fiskmods.heroes.FiskHeroes;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public interface Effect
{
    Minecraft mc = Minecraft.getMinecraft();

    void doRender(Entry e, Entity anchor, boolean isClientPlayer, boolean isFirstPerson, float partialTicks);

    default void onUpdate(Entry e, Entity anchor)
    {
    }

    class Entry
    {
        public final Effect effect;
        public final int maxTime;
        public int time;

        private boolean delete;

        public Entry(int ticks, Effect e)
        {
            maxTime = ticks;
            effect = e;
        }

        boolean tick(Entity anchor)
        {
            effect.onUpdate(this, anchor);
            return delete || ++time > maxTime && !isIndefinite();
        }

        public void markForDeletion()
        {
            delete = true;
        }

        public float progress()
        {
            return isIndefinite() ? 1 : (time + FiskHeroes.proxy.getRenderTick()) / maxTime;
        }

        public boolean isIndefinite()
        {
            return maxTime < 0;
        }
    }
}
