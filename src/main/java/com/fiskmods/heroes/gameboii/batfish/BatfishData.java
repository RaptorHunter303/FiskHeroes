package com.fiskmods.heroes.gameboii.batfish;

//package com.fiskmods.heroes.batfish;
//
//import com.fiskmods.heroes.batfish.level.BatfishPlayer.Skin;
//
//public class BatfishData
//{
//    public static int totalCoins = 0;
//    public static int currentCoins = 0;
//
//    public static boolean[] skinUnlocked = new boolean[Skin.values().length];
//
//    public static boolean hasSkin(Skin skin)
//    {
//        return skinUnlocked[skin.ordinal()];
//    }
//
//    public static boolean unlock(Skin skin)
//    {
//        if (!hasSkin(skin) && totalCoins >= skin.price)
//        {
//            skinUnlocked[skin.ordinal()] = true;
//            totalCoins -= skin.price;
//            return true;
//        }
//
//        return false;
//    }
//
//    static
//    {
//        unlock(Skin.BATFISH);
//    }
//}
