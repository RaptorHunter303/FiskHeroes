package com.fiskmods.heroes.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.ASMHelper;
import com.fiskmods.heroes.asm.SHTranslator;

public class CTRenderGlobal extends ClassTransformerMethodProcess
{
    public CTRenderGlobal()
    {
        super("net.minecraft.client.renderer.RenderGlobal", "b", "doSpawnParticle", "(Ljava/lang/String;DDDDDD)Lbkm;", "(Ljava/lang/String;DDDDDD)Lnet/minecraft/client/particle/EntityFX;");
    }

    @Override
    public boolean processMethod(MethodNode method)
    {
        InsnList list = new InsnList();
        boolean removeNext = false;

        for (int i = 0; i < method.instructions.size(); ++i)
        {
            AbstractInsnNode node = method.instructions.get(i);

            if (removeNext)
            {
                removeNext = false;
                continue;
            }

            if (node instanceof LdcInsnNode)
            {
                LdcInsnNode ldcNode = (LdcInsnNode) node;

                if (ldcNode.cst instanceof String)
                {
                    if (ldcNode.cst.equals("mobSpell"))
                    {
                        removeNext = true;
                        list.add(node);
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false));

                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new LdcInsnNode("mobSpellAmbient"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false));
                        list.add(ASMHelper.not());

                        list.add(ASMHelper.and());
                        continue;
                    }
                    else if (ldcNode.cst.equals("mobSpellAmbient"))
                    {
                        removeNext = true;
                        list.add(node);
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false));
                        continue;
                    }
                }
            }

            if (node.getOpcode() == INVOKESPECIAL)
            {
                if (node instanceof MethodInsnNode)
                {
                    MethodInsnNode methodNode = (MethodInsnNode) node;

                    if (methodNode.owner.equals(SHTranslator.getMappedName("bkx", "net/minecraft/client/particle/EntitySpellParticleFX")) && methodNode.name.equals("<init>") && methodNode.desc.equals(SHTranslator.getMappedName("(Lahb;DDDDDD)V", "(Lnet/minecraft/world/World;DDDDDD)V")))
                    {
                        list.add(node);
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "getPotionParticleScale", "(Ljava/lang/String;)F", false));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, SHTranslator.getMappedName("bkm", "net/minecraft/client/particle/EntityFX"), SHTranslator.getMappedName("f", "multipleParticleScaleBy"), SHTranslator.getMappedName("(F)Lbkm;", "(F)Lnet/minecraft/client/particle/EntityFX;"), false));
                        continue;
                    }
                }
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);

        return true;
    }
}
