package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTEntityLivingBase extends ClassTransformerBase
{
    public CTEntityLivingBase()
    {
        super("net.minecraft.entity.EntityLivingBase");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        int success = 0;

        for (MethodNode method : methods)
        {
            if (method.name.equals(SHTranslator.getMappedName("aO", "updatePotionEffects")) && method.desc.equals("()V"))
            {
                InsnList list = new InsnList();
                boolean flag = false;

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof LdcInsnNode)
                    {
                        LdcInsnNode ldcNode = (LdcInsnNode) node;

                        if (ldcNode.cst instanceof String && (ldcNode.cst.equals("mobSpell") || ldcNode.cst.equals("mobSpellAmbient")))
                        {
                            flag = true;
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(node);
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getPotionParticleName", "(L" + varEntity + ";Ljava/lang/String;)Ljava/lang/String;", false));
                            continue;
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);

                if (flag)
                {
                    ++success;
                }
            }
            else if (method.name.equals(SHTranslator.getMappedName("j", "getArmSwingAnimationEnd")) && method.desc.equals("()I"))
            {
                InsnList list = new InsnList();
                boolean flag = false;

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node.getOpcode() == IRETURN)
                    {
                        flag = true;
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getArmSwingAnimationEnd", "(IL" + varLivingBase + ";)I", false));
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);

                if (flag)
                {
                    ++success;
                }
            }
            else if (method.name.equals(SHTranslator.getMappedName("e", "moveEntityWithHeading")) && method.desc.equals("(FF)V"))
            {
                InsnList list = new InsnList();
                int count = 0;

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (i == 0)
                    {
                        ++count;
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(FLOAD, 1));
                        list.add(new VarInsnNode(FLOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "moveEntityWithHeading", "(L" + varLivingBase + ";FF)Z", false));

                        LabelNode l7 = new LabelNode();
                        list.add(new JumpInsnNode(IFEQ, l7));
                        list.add(new InsnNode(RETURN));
                        list.add(l7);
                    }

                    if (node instanceof LdcInsnNode)
                    {
                        LdcInsnNode ldcNode = (LdcInsnNode) node;

                        if (ldcNode.cst instanceof Float && (Float) ldcNode.cst == 4.0F)
                        {
                            ++count;
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getModifiedEntityScale", "(L" + varEntity + ";)F", false));
                            list.add(new InsnNode(FDIV));
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);

                if (count >= 2)
                {
                    ++success;
                }
            }
        }

        return success >= 3;
    }

    @Override
    public boolean processFields(List<FieldNode> fields)
    {
        return true;
    }
}
