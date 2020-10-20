package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CTEntityOtherPlayerMP extends ClassTransformerMethodProcess
{
    public CTEntityOtherPlayerMP()
    {
        super("net.minecraft.client.entity.EntityOtherPlayerMP", "h", "onUpdate", "()V", "()V");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        boolean success = false;

        for (int i = 0; i < method.instructions.size(); ++i)
        {
            AbstractInsnNode node = method.instructions.get(i);

            if (node instanceof LdcInsnNode)
            {
                LdcInsnNode ldcNode = (LdcInsnNode) node;

                if (ldcNode.cst instanceof Float && (Float) ldcNode.cst == 4.0F)
                {
                    success = true;
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getModifiedEntityScale", "(L" + varEntity + ";)F", false));
                    list.add(new InsnNode(FDIV));
                }
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);

        return success;
    }
}
