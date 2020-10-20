package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class CTMinecraftServer extends ClassTransformerMethodProcess
{
    public CTMinecraftServer()
    {
        super("net.minecraft.server.MinecraftServer", "run", "run", "()V", "()V");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        boolean success = false;

        for (AbstractInsnNode node : method.instructions.toArray())
        {
            if (node instanceof LdcInsnNode)
            {
                LdcInsnNode ldcNode = (LdcInsnNode) node;

                if (ldcNode.cst instanceof Long && (Long) ldcNode.cst == 50L)
                {
                    success = true;
                    list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getMiliSecondsPerTick", "()J", false));
                    continue;
                }
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);

        return success;
    }
}
