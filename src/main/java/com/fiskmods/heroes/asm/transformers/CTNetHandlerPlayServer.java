package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTNetHandlerPlayServer extends ClassTransformerMethodProcess
{
    public CTNetHandlerPlayServer()
    {
        super("net.minecraft.network.NetHandlerPlayServer", "a", "processUseEntity", "(Lja;)V", "(Lnet/minecraft/network/play/client/C02PacketUseEntity;)V");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        boolean success = false;

        for (AbstractInsnNode node : method.instructions.toArray())
        {
            if (node instanceof VarInsnNode && ((VarInsnNode) node).var == 5 && node.getOpcode() == DLOAD)
            {
                success = true;
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(node);
                list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getReachDistanceSq", "(L" + SHTranslator.getMappedName("nh", "net/minecraft/network/NetHandlerPlayServer") + ";D)D", false));
                continue;
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);

        return success;
    }
}
