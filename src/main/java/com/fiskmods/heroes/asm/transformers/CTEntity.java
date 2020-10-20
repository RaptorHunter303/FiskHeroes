package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.ASMHelper;
import com.fiskmods.heroes.asm.SHTranslator;

public class CTEntity extends ClassTransformerBase
{
    public CTEntity()
    {
        super("net.minecraft.entity.Entity");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        boolean flag = false;

        for (MethodNode method : methods)
        {
            if (method.name.equals(SHTranslator.getMappedName("c", "getBrightnessForRender")) && method.desc.equals("(F)I"))
            {
                InsnList list = new InsnList();
                int startIndex = -1;
                int endIndex = -1;

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (i + 9 < method.instructions.size())
                    {
                        AbstractInsnNode endNode = method.instructions.get(i + 9);

                        if (endNode instanceof VarInsnNode && ((VarInsnNode) endNode).var == 6 && endNode.getOpcode() == ISTORE)
                        {
                            startIndex = i;
                            endIndex = i + 9;
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "getBrightnessForRender", "(L" + varEntity + ";)I", false));
                        }
                    }

                    if (i >= startIndex && i < endIndex)
                    {
                        continue;
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                flag = true;
            }
            else if (method.name.equals(SHTranslator.getMappedName("d", "moveEntity")) && method.desc.equals("(DDD)V"))
            {
                InsnList list = new InsnList();
                boolean flag1 = false;
                boolean flag2 = false;
                boolean removeNext = false;

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (removeNext)
                    {
                        removeNext = false;
                        continue;
                    }

                    if (node.getOpcode() == ICONST_1 && node.getNext() != null && node.getNext().getOpcode() == IADD && node.getNext().getNext() instanceof FieldInsnNode)
                    {
                        FieldInsnNode fieldNode = (FieldInsnNode) node.getNext().getNext();

                        if (fieldNode.name.equals(SHTranslator.getMappedName("d", "nextStepDistance")) && fieldNode.desc.equals("I") && fieldNode.getOpcode() == PUTFIELD)
                        {
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(node);
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getStepDistance", "(L" + varEntity + ";I)F", false));
                            list.add(new InsnNode(F2I));
                            continue;
                        }
                    }

                    if (node instanceof MethodInsnNode)
                    {
                        MethodInsnNode methodNode = (MethodInsnNode) node;

                        if (!flag1)
                        {
                            if (methodNode.owner.equals(varAABB) && methodNode.name.equals(SHTranslator.getMappedName("d", "offset")) && methodNode.desc.equals("(DDD)L" + varAABB + ";"))
                            {
                                flag1 = true;
                                removeNext = true;
                                list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "noClipCollide", "(L" + varEntity + ";DDD)V", false));
                                list.add(new InsnNode(RETURN));
                                continue;
                            }
                        }
                    }

                    if (node instanceof FieldInsnNode)
                    {
                        FieldInsnNode fieldNode = (FieldInsnNode) node;

                        if (!flag2)
                        {
                            if (fieldNode.getOpcode() == GETFIELD && fieldNode.name.equals(SHTranslator.getMappedName("C", "boundingBox")) && fieldNode.desc.equals(SHTranslator.getMappedName("Lazt;", "Lnet/minecraft/util/AxisAlignedBB;")))
                            {
                                flag2 = true;
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
            else if (method.name.equals(SHTranslator.getMappedName("C", "onEntityUpdate")) && method.desc.equals("()V"))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof MethodInsnNode)
                    {
                        MethodInsnNode methodNode = (MethodInsnNode) node;

                        if (methodNode.name.equals(SHTranslator.getMappedName("ao", "isSprinting")) && methodNode.desc.equals("()Z"))
                        {
                            list.add(node);
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "shouldProduceSprintParticles", "(L" + varEntity + ";)Z", false));
                            list.add(ASMHelper.and());
                            continue;
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                flag = true;
            }
            else if (method.name.equals(SHTranslator.getMappedName("Y", "getShadowSize")) && method.desc.equals("()F"))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof FieldInsnNode)
                    {
                        FieldInsnNode fieldNode = (FieldInsnNode) node;

                        if (fieldNode.name.equals(SHTranslator.getMappedName("N", "height")) && fieldNode.desc.equals("F"))
                        {
                            list.add(node);
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getEntityScale", "(L" + varEntity + ";)F", false));
                            list.add(new InsnNode(FMUL));
                            continue;
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
