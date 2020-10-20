package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CTGuiInventory extends ClassTransformerMethodProcess
{
    public CTGuiInventory()
    {
        super("net.minecraft.client.gui.inventory.GuiInventory", "a", "func_147046_a", "(IIIFFLsv;)V", "(IIIFFLnet/minecraft/entity/EntityLivingBase;)V");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        boolean flag = false;

        for (AbstractInsnNode node : method.instructions.toArray())
        {
            if (node.getOpcode() == I2F && (node.getPrevious() instanceof VarInsnNode && ((VarInsnNode) node.getPrevious()).var == 2 && node.getPrevious().getOpcode() == ILOAD || node.getPrevious() != null && node.getPrevious().getPrevious() instanceof VarInsnNode && ((VarInsnNode) node.getPrevious().getPrevious()).var == 2 && node.getPrevious().getPrevious().getOpcode() == ILOAD))
            {
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "getInventoryPlayerScale", "(IL" + varLivingBase + ";)F", false));
                continue;
            }

            if (node.getOpcode() == I2F && node.getPrevious() instanceof VarInsnNode && ((VarInsnNode) node.getPrevious()).var == 1 && node.getPrevious().getOpcode() == ILOAD)
            {
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new VarInsnNode(ALOAD, 5));
                list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "getInventoryPlayerOffset", "(IIL" + varLivingBase + ";)F", false));
                continue;
            }

            if (node instanceof MethodInsnNode)
            {
                MethodInsnNode methodNode = (MethodInsnNode) node;

                if (methodNode.owner.equals("org/lwjgl/opengl/GL11") && methodNode.name.equals("glScalef") && methodNode.desc.equals("(FFF)V"))
                {
                    list.add(node);
                    list.add(new InsnNode(FCONST_0));
                    list.add(new VarInsnNode(ILOAD, 1));
                    list.add(new VarInsnNode(ILOAD, 2));
                    list.add(new VarInsnNode(ALOAD, 5));
                    list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "getInventoryPlayerOffsetPost", "(IIL" + varLivingBase + ";)F", false));
                    list.add(new InsnNode(FCONST_0));
                    list.add(new MethodInsnNode(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glTranslatef", "(FFF)V", false));
                    continue;
                }
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);

        return true;
    }
}
