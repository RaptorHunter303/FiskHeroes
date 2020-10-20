//package com.fiskmods.heroes.common.data; // TODO: 1.4 Combat
//
//import com.fiskmods.heroes.common.predicate.SHPredicates;
//
//import net.minecraft.entity.Entity;
//
//public class SHDataCombat<T> extends SHData<T>
//{
//    public SHDataCombat(T defaultVal)
//    {
//        super(defaultVal, SHPredicates.HAS_MOVESET);
//    }
//
//    @Override
//    protected void onValueChanged(Entity entity, T value)
//    {
//        SHData.FOCUS.setWithoutNotify(entity, 0F);
//    }
//}
