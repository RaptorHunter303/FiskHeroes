package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTEntityPlayer extends ClassTransformerBase
{
    public CTEntityPlayer()
    {
        super("net.minecraft.entity.player.EntityPlayer");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        boolean flag = false;

        for (MethodNode method : methods)
        {
            if (method.name.equals(SHTranslator.getMappedName("b", "getItemIcon")) && method.desc.equals(SHTranslator.getMappedName("(Ladd;I)Lrf;", "(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/util/IIcon;")))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof IntInsnNode)
                    {
                        IntInsnNode intNode = (IntInsnNode) node;

                        if (intNode.operand == 13 || intNode.operand == 18)
                        {
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(node);
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getBowIconTime", "(L" + varPlayer + ";I)I", false));
                            continue;
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                flag = true;
            }
            else if (method.name.equals(SHTranslator.getMappedName("bE", "getArmorVisibility")) && method.desc.equals("()F"))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node.getOpcode() == FRETURN)
                    {
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getArmorVisibilityMultiplier", "(L" + varPlayer + ";)F", false));
                        list.add(new InsnNode(FMUL));
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean processFields(List<FieldNode> fields)
    {
        return true;
    }
}
