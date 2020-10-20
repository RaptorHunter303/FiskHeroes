package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.ASMHelper;
import com.fiskmods.heroes.asm.SHTranslator;
import com.fiskmods.heroes.common.entity.arrow.EntityTrickArrow;

import net.minecraft.entity.projectile.EntityArrow;

public class CTBlockButton extends ClassTransformerBase
{
    public CTBlockButton()
    {
        super("net.minecraft.block.BlockButton");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        boolean flag = false;

        for (MethodNode method : methods)
        {
            if (method.name.equals(SHTranslator.getMappedName("a", "onEntityCollidedWithBlock")) && method.desc.equals(SHTranslator.getMappedName("(Lahb;IIILsa;)V", "(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V")) || method.name.equals(SHTranslator.getMappedName("a", "updateTick")) && method.desc.equals(SHTranslator.getMappedName("(Lahb;IIILjava/util/Random;)V", "(Lnet/minecraft/world/World;IIILjava/util/Random;)V")))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof FieldInsnNode)
                    {
                        FieldInsnNode fieldNode = (FieldInsnNode) node;

                        if (fieldNode.getOpcode() == GETFIELD && fieldNode.name.equals(SHTranslator.getMappedName("a", "field_150047_a")) && fieldNode.desc.equals("Z"))
                        {
                            list.add(new InsnNode(ICONST_1));
                            continue;
                        }
                    }

                    if (node.getNext() instanceof FieldInsnNode)
                    {
                        FieldInsnNode fieldNode = (FieldInsnNode) node.getNext();

                        if (fieldNode.getOpcode() == GETFIELD && fieldNode.name.equals(SHTranslator.getMappedName("a", "field_150047_a")) && fieldNode.desc.equals("Z"))
                        {
                            continue;
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                flag = true;
            }
            else if (method.name.equals(SHTranslator.getMappedName("n", "func_150046_n")) && method.desc.equals(SHTranslator.getMappedName("(Lahb;III)V", "(Lnet/minecraft/world/World;III)V")))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof LdcInsnNode)
                    {
                        LdcInsnNode ldcNode = (LdcInsnNode) node;

                        if (ldcNode.cst instanceof Type && ldcNode.cst.equals(Type.getType(EntityArrow.class)))
                        {
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, SHTranslator.getMappedName("anx", "net/minecraft/block/BlockButton"), SHTranslator.getMappedName("a", "field_150047_a"), "Z"));
                            list.add(new LdcInsnNode(Type.getType(EntityArrow.class)));
                            list.add(new LdcInsnNode(Type.getType(EntityTrickArrow.class)));
                            list.add(ASMHelper.conditional());
                            list.add(ASMHelper.cast("java/lang/Class"));
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
