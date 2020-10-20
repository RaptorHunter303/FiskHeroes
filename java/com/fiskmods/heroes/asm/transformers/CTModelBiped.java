package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTModelBiped extends ClassTransformerBase
{
    public CTModelBiped()
    {
        super("net.minecraft.client.model.ModelBiped");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        boolean flag = false;

        for (MethodNode method : methods)
        {
            if (method.name.equals(SHTranslator.getMappedName("a", "render")) && method.desc.equals(SHTranslator.getMappedName("(Lsa;FFFFFF)V", "(Lnet/minecraft/entity/Entity;FFFFFF)V")))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof MethodInsnNode)
                    {
                        MethodInsnNode methodNode = (MethodInsnNode) node;

                        if (methodNode.name.equals(SHTranslator.getMappedName("a", "setRotationAngles")) && methodNode.desc.equals(SHTranslator.getMappedName("(FFFFFFLsa;)V", "(FFFFFFLnet/minecraft/entity/Entity;)V")))
                        {
                            list.add(node);
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new VarInsnNode(FLOAD, 2));
                            list.add(new VarInsnNode(FLOAD, 3));
                            list.add(new VarInsnNode(FLOAD, 4));
                            list.add(new VarInsnNode(FLOAD, 5));
                            list.add(new VarInsnNode(FLOAD, 6));
                            list.add(new VarInsnNode(FLOAD, 7));
                            list.add(new MethodInsnNode(INVOKESTATIC, MODELHELPER, "renderBipedPre", SHTranslator.getMappedName("(Lbhm;Lsa;FFFFFF)V", "(Lnet/minecraft/client/model/ModelBiped;Lnet/minecraft/entity/Entity;FFFFFF)V"), false));
                            continue;
                        }
                    }

                    if (node.getOpcode() == RETURN)
                    {
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(FLOAD, 2));
                        list.add(new VarInsnNode(FLOAD, 3));
                        list.add(new VarInsnNode(FLOAD, 4));
                        list.add(new VarInsnNode(FLOAD, 5));
                        list.add(new VarInsnNode(FLOAD, 6));
                        list.add(new VarInsnNode(FLOAD, 7));
                        list.add(new MethodInsnNode(INVOKESTATIC, MODELHELPER, "renderBipedPost", SHTranslator.getMappedName("(Lbhm;Lsa;FFFFFF)V", "(Lnet/minecraft/client/model/ModelBiped;Lnet/minecraft/entity/Entity;FFFFFF)V"), false));
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                flag = true;
            }
            else if (method.name.equals(SHTranslator.getMappedName("a", "setRotationAngles")) && method.desc.equals(SHTranslator.getMappedName("(FFFFFFLsa;)V", "(FFFFFFLnet/minecraft/entity/Entity;)V")))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof VarInsnNode)
                    {
                        VarInsnNode varNode = (VarInsnNode) node;

                        if (varNode.getOpcode() == FLOAD)
                        {
                            if (varNode.var == 1)
                            {
                                list.add(new VarInsnNode(ALOAD, 0));
                                list.add(new VarInsnNode(FLOAD, 1));
                                list.add(new VarInsnNode(FLOAD, 2));
                                list.add(new VarInsnNode(FLOAD, 3));
                                list.add(new VarInsnNode(FLOAD, 4));
                                list.add(new VarInsnNode(FLOAD, 5));
                                list.add(new VarInsnNode(FLOAD, 6));
                                list.add(new VarInsnNode(ALOAD, 7));
                                list.add(new MethodInsnNode(INVOKESTATIC, MODELHELPER, "getLimbSwingSpeed", SHTranslator.getMappedName("(Lbhm;FFFFFFLsa;)F", "(Lnet/minecraft/client/model/ModelBiped;FFFFFFLnet/minecraft/entity/Entity;)F"), false));
                                continue;
                            }
                            else if (varNode.var == 2)
                            {
                                list.add(new VarInsnNode(ALOAD, 0));
                                list.add(new VarInsnNode(FLOAD, 1));
                                list.add(new VarInsnNode(FLOAD, 2));
                                list.add(new VarInsnNode(FLOAD, 3));
                                list.add(new VarInsnNode(FLOAD, 4));
                                list.add(new VarInsnNode(FLOAD, 5));
                                list.add(new VarInsnNode(FLOAD, 6));
                                list.add(new VarInsnNode(ALOAD, 7));
                                list.add(new MethodInsnNode(INVOKESTATIC, MODELHELPER, "getLimbSwingDegree", SHTranslator.getMappedName("(Lbhm;FFFFFFLsa;)F", "(Lnet/minecraft/client/model/ModelBiped;FFFFFFLnet/minecraft/entity/Entity;)F"), false));
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
