package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTFoodStats extends ClassTransformerMethodProcess
{
    public CTFoodStats()
    {
        super("net.minecraft.util.FoodStats", "a", "onUpdate", "(Lyz;)V", "(Lnet/minecraft/entity/player/EntityPlayer;)V");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        boolean healed = false;

        for (AbstractInsnNode node : method.instructions.toArray())
        {
            if (node.getNext() instanceof MethodInsnNode)
            {
                MethodInsnNode methodNode = (MethodInsnNode) node.getNext();

                if (methodNode.getOpcode() == INVOKEVIRTUAL && methodNode.owner.equals(SHTranslator.getMappedName("zr", "net/minecraft/util/FoodStats")) && methodNode.name.equals(SHTranslator.getMappedName("a", "addExhaustion")) && methodNode.desc.equals("(F)V"))
                {
                    list.add(node);
                    list.add(new LdcInsnNode(80.0F));
                    list.add(new VarInsnNode(ALOAD, 1));
                    list.add(new IntInsnNode(BIPUSH, 80));
                    list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getHealRate", "(L" + varPlayer + ";I)I", false));
                    list.add(new InsnNode(I2F));
                    list.add(new InsnNode(FDIV));
                    list.add(new InsnNode(FDIV));
                    continue;
                }
            }

            if (node instanceof MethodInsnNode)
            {
                MethodInsnNode methodNode = (MethodInsnNode) node;

                if (methodNode.getOpcode() == INVOKEVIRTUAL && methodNode.owner.equals(varPlayer) && methodNode.name.equals(SHTranslator.getMappedName("f", "heal")) && methodNode.desc.equals("(F)V"))
                {
                    healed = true;
                }
            }

            if (node instanceof IntInsnNode)
            {
                IntInsnNode intNode = (IntInsnNode) node;

                if (intNode.getOpcode() == BIPUSH && intNode.operand == 80)
                {
                    if (!healed)
                    {
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(intNode);
                        list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getHealRate", "(L" + varPlayer + ";I)I", false));
                        continue;
                    }
                }
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);

        return true;
    }
}
