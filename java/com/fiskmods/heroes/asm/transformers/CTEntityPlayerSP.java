package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTEntityPlayerSP extends ClassTransformerBase
{
    public CTEntityPlayerSP()
    {
        super("net.minecraft.client.entity.EntityPlayerSP");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        boolean flag = false;

        for (MethodNode method : methods)
        {
            if (method.name.equals(SHTranslator.getMappedName("t", "getFOVMultiplier")) && method.desc.equals("()F"))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof MethodInsnNode)
                    {
                        MethodInsnNode methodNode = (MethodInsnNode) node;

                        if (methodNode.name.equals(SHTranslator.getMappedName("b", "getWalkSpeed")) && methodNode.desc.equals("()F"))
                        {
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "getScaledWalkSpeedForFOV", SHTranslator.getMappedName("(Lyw;Lblk;)F", "(Lnet/minecraft/entity/player/PlayerCapabilities;Lnet/minecraft/client/entity/EntityPlayerSP;)F"), false));
                            continue;
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                flag = true;
            }
            else if (method.name.equals(SHTranslator.getMappedName("e", "onLivingUpdate")) && method.desc.equals("()V"))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof LdcInsnNode)
                    {
                        LdcInsnNode ldcNode = (LdcInsnNode) node;

                        if (ldcNode.cst instanceof Float && (Float) ldcNode.cst == 0.2F)
                        {
                            boolean flag1 = false;

                            if (node.getPrevious() instanceof FieldInsnNode)
                            {
                                FieldInsnNode fieldNode = (FieldInsnNode) node.getPrevious();
                                flag1 = fieldNode.getOpcode() == GETFIELD && fieldNode.owner.equals(SHTranslator.getMappedName("blk", "net/minecraft/client/entity/EntityPlayerSP")) && fieldNode.name.equals(SHTranslator.getMappedName("V", "ySize")) && fieldNode.desc.equals("F");
                            }

                            if (node.getNext() instanceof FieldInsnNode)
                            {
                                FieldInsnNode fieldNode = (FieldInsnNode) node.getNext();
                                flag1 |= fieldNode.getOpcode() == PUTFIELD && fieldNode.owner.equals(SHTranslator.getMappedName("blk", "net/minecraft/client/entity/EntityPlayerSP")) && fieldNode.name.equals(SHTranslator.getMappedName("V", "ySize")) && fieldNode.desc.equals("F");
                            }

                            if (flag1)
                            {
                                list.add(new VarInsnNode(ALOAD, 0));
                                list.add(node);
                                list.add(new InsnNode(F2D));
                                list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getScaledSneakOffset", SHTranslator.getMappedName("(Lsa;D)D", "(Lnet/minecraft/entity/Entity;D)D"), false));
                                list.add(new InsnNode(D2F));
                                continue;
                            }
                        }
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
