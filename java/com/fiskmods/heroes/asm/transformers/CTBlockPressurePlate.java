package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTBlockPressurePlate extends ClassTransformerMethodProcess
{
    public CTBlockPressurePlate()
    {
        super("net.minecraft.block.BlockPressurePlate", "e", "func_150065_e", "(Lahb;III)I", "(Lnet/minecraft/world/World;III)I");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        String type = "";

        boolean success = false;

        for (AbstractInsnNode node : method.instructions.toArray())
        {
            if (node instanceof FieldInsnNode)
            {
                FieldInsnNode fieldNode = (FieldInsnNode) node;

                if (fieldNode.owner.equals(SHTranslator.getMappedName("amv", "net/minecraft/block/BlockPressurePlate$Sensitivity")) && fieldNode.desc.equals(SHTranslator.getMappedName("Lamv;", "Lnet/minecraft/block/BlockPressurePlate$Sensitivity;")))
                {
                    type = fieldNode.name;
                }
            }

            if (node instanceof MethodInsnNode)
            {
                MethodInsnNode methodNode = (MethodInsnNode) node;

                if (type.equals(SHTranslator.getMappedName("b", "mobs")) && methodNode.name.equals(SHTranslator.getMappedName("a", "getEntitiesWithinAABB")) && methodNode.desc.equals("(Ljava/lang/Class;L" + varAABB + ";)Ljava/util/List;"))
                {
                    success = true;
                    list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getAllowedPressureEntitiesWithinAABB", "(L" + varWorld + ";Ljava/lang/Class;L" + varAABB + ";)Ljava/util/List;", false));
                    continue;
                }
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);

        return success;
    }
}
