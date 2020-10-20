package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTItemStack extends ClassTransformerMethodProcess
{
    public CTItemStack()
    {
        super("net.minecraft.item.ItemStack", "c", "readFromNBT", "(Ldh;)V", "(Lnet/minecraft/nbt/NBTTagCompound;)V");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        boolean success = false;

        for (AbstractInsnNode node : method.instructions.toArray())
        {
            if (node.getOpcode() == RETURN)
            {
                success = true;
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "readFromNBT", SHTranslator.getMappedName("(Ladd;Ldh;)V", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/nbt/NBTTagCompound;)V"), false));
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);

        return success;
    }
}
