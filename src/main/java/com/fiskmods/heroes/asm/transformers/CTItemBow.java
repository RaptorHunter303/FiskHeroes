package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CTItemBow extends ClassTransformerMethodProcess
{
    public CTItemBow()
    {
        super("net.minecraft.item.ItemBow", "a", "onPlayerStoppedUsing", "(Ladd;Lahb;Lyz;I)V", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;I)V");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        boolean success = false;

        for (AbstractInsnNode node : method.instructions.toArray())
        {
            if (node instanceof LdcInsnNode)
            {
                LdcInsnNode ldcNode = (LdcInsnNode) node;

                if (ldcNode.cst instanceof Float && (Float) ldcNode.cst == 20.0F)
                {
                    success = true;
                    list.add(new VarInsnNode(ALOAD, 3));
                    list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getDrawbackTime", "(L" + varPlayer + ";)F", false));
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
