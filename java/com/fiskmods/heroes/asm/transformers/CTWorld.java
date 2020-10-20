package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTWorld extends ClassTransformerBase
{
    public CTWorld()
    {
        super("net.minecraft.world.World");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        boolean flag = false;

        for (MethodNode method : methods)
        {
            if (method.name.equals(SHTranslator.getMappedName("b", "getClosestVulnerablePlayerToEntity")) && method.desc.equals(SHTranslator.getMappedName("(Lsa;D)Lyz;", "(Lnet/minecraft/entity/Entity;D)Lnet/minecraft/entity/player/EntityPlayer;")))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof VarInsnNode)
                    {
                        VarInsnNode varNode = (VarInsnNode) node;

                        if (varNode.var == 0)
                        {
                            list.add(varNode);
                            list.add(new VarInsnNode(ALOAD, 1));
                            continue;
                        }
                    }

                    if (node instanceof MethodInsnNode)
                    {
                        list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getClosestVulnerablePlayer", SHTranslator.obfuscatedEnv ? "(Lsa;DDDD)Lyz;" : "(Lnet/minecraft/entity/Entity;DDDD)Lnet/minecraft/entity/player/EntityPlayer;", false));
                        continue;
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                flag = true;
            }
            else if (method.name.equals(SHTranslator.getMappedName("g", "getIndirectPowerLevelTo")) && method.desc.equals("(IIII)I"))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node.getOpcode() == IRETURN)
                    {
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 5));
                        list.add(new VarInsnNode(ILOAD, 1));
                        list.add(new VarInsnNode(ILOAD, 2));
                        list.add(new VarInsnNode(ILOAD, 3));
                        list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getIndirectPowerLevelTo", SHTranslator.obfuscatedEnv ? "(ILahb;Laji;III)I" : "(ILnet/minecraft/world/World;Lnet/minecraft/block/Block;III)I", false));
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                flag = true;
            }
            else if (method.name.equals(SHTranslator.getMappedName("v", "isBlockIndirectlyGettingPowered")) && method.desc.equals("(III)Z"))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node.getOpcode() == IRETURN)
                    {
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ILOAD, 1));
                        list.add(new VarInsnNode(ILOAD, 2));
                        list.add(new VarInsnNode(ILOAD, 3));
                        list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "isBlockIndirectlyGettingPowered", SHTranslator.obfuscatedEnv ? "(ZLahb;III)Z" : "(ZLnet/minecraft/world/World;III)Z", false));
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
