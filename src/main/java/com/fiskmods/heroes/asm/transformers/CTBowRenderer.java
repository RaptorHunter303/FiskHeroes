package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CTBowRenderer extends ClassTransformerMethodProcess
{
    public CTBowRenderer()
    {
        super("mods.battlegear2.client.renderer.BowRenderer", "renderEquippedBow", "renderEquippedBow", "(Ladd;Lsv;Z)V", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Z)V");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        int success = 0;

        for (int i = 0; i < method.instructions.size(); ++i)
        {
            AbstractInsnNode node = method.instructions.get(i);

            if (node instanceof IntInsnNode)
            {
                IntInsnNode intNode = (IntInsnNode) node;

                if (intNode.operand == 13 || intNode.operand == 18)
                {
                    ++success;
                    list.add(new VarInsnNode(ALOAD, 8));
                    list.add(node);
                    list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getBowIconTime", "(L" + varPlayer + ";I)I", false));
                    continue;
                }
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);

        return success >= 2;
    }
}
