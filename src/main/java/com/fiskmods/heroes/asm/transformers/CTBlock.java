package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CTBlock extends ClassTransformerMethodProcess
{
    public CTBlock()
    {
        super("net.minecraft.block.Block", "a", "shouldSideBeRendered", "(Lahl;IIII)Z", "(Lnet/minecraft/world/IBlockAccess;IIII)Z");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        boolean success = false;

        for (AbstractInsnNode node : method.instructions.toArray())
        {
//            if (!success && node instanceof LineNumberNode)
//            {
//                success = true;
//                list.add(new VarInsnNode(ALOAD, 0));
//                list.add(new VarInsnNode(ALOAD, 1));
//                list.add(new VarInsnNode(ILOAD, 2));
//                list.add(new VarInsnNode(ILOAD, 3));
//                list.add(new VarInsnNode(ILOAD, 4));
//                list.add(new VarInsnNode(ILOAD, 5));
//                list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "shouldSideBeRendered", SHTranslator.getMappedName("(Laji;Lahl;IIII)Z", "(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Z"), false));
//            }

            if (node.getOpcode() == IRETURN)
            {
                success = true;
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new VarInsnNode(ILOAD, 3));
                list.add(new VarInsnNode(ILOAD, 4));
                list.add(new VarInsnNode(ILOAD, 5));
                list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "shouldSideBeRendered", "(ZL" + varBlock + ";L" + varIBlockAccess + ";IIII)Z", false));
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);

        return success;
    }
}
