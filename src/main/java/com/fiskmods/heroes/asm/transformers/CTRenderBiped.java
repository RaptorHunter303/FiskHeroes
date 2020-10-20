package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTRenderBiped extends ClassTransformerBase
{
    public CTRenderBiped()
    {
        super("net.minecraft.client.renderer.entity.RenderBiped");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        boolean flag = false;

        for (MethodNode method : methods)
        {
            if (method.name.equals(SHTranslator.getMappedName("a", "doRender")) && method.desc.equals(SHTranslator.getMappedName("(Lsw;DDDFF)V", "(Lnet/minecraft/entity/EntityLiving;DDDFF)V")))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof LdcInsnNode)
                    {
                        LdcInsnNode ldcNode = (LdcInsnNode) node;

                        if (ldcNode.cst instanceof Double && (Double) ldcNode.cst == 0.125D)
                        {
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(node);
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getScaledSneakOffset", SHTranslator.getMappedName("(Lsa;D)D", "(Lnet/minecraft/entity/Entity;D)D"), false));
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
