package com.fiskmods.heroes.common.network;

import com.fiskmods.heroes.FiskHeroes;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class SHNetworkManager extends FiskNetworkHelper
{
    public static SimpleNetworkWrapper wrapper;

    public static void registerPackets()
    {
        wrapper = FiskNetworkHelper.getWrapper(FiskHeroes.MODID);

        registerMessage(MessageBroadcastState.class);
        registerMessage(MessagePlayerData.class);
        registerMessage(MessageInteraction.class);
        registerMessage(MessageTileTrigger.class);
        registerMessage(MessageSwingOffhand.class);
        registerMessage(MessageTriggerSpell.class);
//        registerMessage(MessageActivateMove.class); // TODO: 1.4 Combat

        registerMessage(MessagePlayerJoin.class, Side.CLIENT);
        registerMessage(MessageUpdateArmor.class, Side.CLIENT);
        registerMessage(MessageUpdateEffects.class, Side.CLIENT);
        registerMessage(MessageUpdateStepHeight.class, Side.CLIENT);
        registerMessage(MessageUpdateCollection.class, Side.CLIENT);
        registerMessage(MessageSyncHeroPacks.class, Side.CLIENT);
        registerMessage(MessageSyncRules.class, Side.CLIENT);
        registerMessage(MessageKnockback.class, Side.CLIENT);
        registerMessage(MessageAddArrowToEntity.class, Side.CLIENT);
        registerMessage(MessageSetTemperature.class, Side.CLIENT);
        registerMessage(MessageGrappleArrowCut.class, Side.CLIENT);
        registerMessage(MessageTeleport.class, Side.CLIENT);
        registerMessage(MessageApplyMotion.class, Side.CLIENT);
        registerMessage(MessageSpellWhip.class, Side.CLIENT);
        registerMessage(MessageGameboii.Load.class, Side.CLIENT);
        registerMessage(MessageForceSlow.class, Side.CLIENT);

        registerMessage(MessageUpdateBook.class, Side.SERVER);
        registerMessage(MessageThrowShield.class, Side.SERVER);
        registerMessage(MessageSuitIteration.class, Side.SERVER);
        registerMessage(MessageGameboii.Save.class, Side.SERVER);
        registerMessage(MessageGameboii.RequestStats.class, Side.SERVER);
        registerMessage(MessageBatfish.class, Side.SERVER);
    }
}
