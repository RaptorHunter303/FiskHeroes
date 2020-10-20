package com.fiskmods.heroes.common.interaction;

import java.util.function.BiPredicate;

import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.HeroModifier;
import com.fiskmods.heroes.util.SHHelper;
import com.google.common.collect.ImmutableList;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public abstract class InteractionBase extends Interaction
{
    protected final ImmutableList<InteractionType> reqTypes;

    protected BiPredicate<EntityPlayer, Hero> predicate = (e, h) -> h != null;

    public InteractionBase(InteractionType... types)
    {
        reqTypes = ImmutableList.<InteractionType> builder().add(types).build();
    }

    public InteractionBase()
    {
        this(InteractionType.RIGHT_CLICK_AIR);
    }

    public void setPredicate(BiPredicate<EntityPlayer, Hero> pred)
    {
        predicate = pred;
    }

    public void requireModifier(HeroModifier modifier)
    {
        predicate = predicate.and((e, h) -> h.hasEnabledModifier(e, modifier));
    }

    @Override
    public boolean listen(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (!reqTypes.contains(type) || !predicate.test(sender, SHHelper.getHero(sender)))
        {
            return false;
        }

        if (side.isClient() && sender == clientPlayer)
        {
            if (!clientRequirements(sender, type, x, y, z))
            {
                return false;
            }
        }

        return serverRequirements(sender, type, x, y, z);
    }

    public abstract boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z);

    public abstract boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z);
}
