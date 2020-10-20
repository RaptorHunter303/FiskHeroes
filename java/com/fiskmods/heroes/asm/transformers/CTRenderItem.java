package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTRenderItem extends ClassTransformerMethodProcess
{
    public CTRenderItem()
    {
        super("net.minecraft.client.renderer.entity.RenderItem", "renderDroppedItem", "renderDroppedItem", "(Lxk;Lrf;IFFFFI)V", "(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/IIcon;IFFFFI)V");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        boolean success = false;

        for (AbstractInsnNode node : method.instructions.toArray())
        {
            if (node instanceof MethodInsnNode)
            {
                MethodInsnNode methodNode = (MethodInsnNode) node;

                if (methodNode.name.equals(SHTranslator.getMappedName("a", "bindTexture")) && methodNode.desc.equals("(L" + varResourceLocation + ";)V"))
                {
                    success = true;
                    list.add(node);
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new VarInsnNode(ALOAD, 1));
                    list.add(new MethodInsnNode(INVOKEVIRTUAL, SHTranslator.getMappedName("bny", "net/minecraft/client/renderer/entity/RenderItem"), SHTranslator.getMappedName("b", "bindEntityTexture"), "(L" + varEntity + ";)V", false));
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
