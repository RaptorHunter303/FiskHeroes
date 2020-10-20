package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTBlockPane extends ClassTransformerMethodProcess
{
    public CTBlockPane()
    {
        super("net.minecraft.block.BlockPane", "a", "addCollisionBoxesToList", "(Lahb;IIILazt;Ljava/util/List;Lsa;)V", "(Lnet/minecraft/world/World;IIILnet/minecraft/util/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;)V");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        boolean success = false;

        for (AbstractInsnNode node : method.instructions.toArray())
        {
            if (!success && node instanceof LineNumberNode)
            {
                LabelNode label = new LabelNode();
                list.add(node);
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 7));
                list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "ignorePaneCollisionBox", "(L" + SHTranslator.getMappedName("aoa", "net/minecraft/block/BlockPane") + ";L" + varEntity + ";)Z", false));
                list.add(new JumpInsnNode(IFEQ, label));
                list.add(new InsnNode(RETURN));
                list.add(label);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                success = true;
                continue;
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);

        return success;
    }
}
