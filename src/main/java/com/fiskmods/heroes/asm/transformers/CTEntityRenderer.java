package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTEntityRenderer extends ClassTransformerBase
{
    public CTEntityRenderer()
    {
        super("net.minecraft.client.renderer.EntityRenderer");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        boolean flag = false;

        for (MethodNode method : methods)
        {
            if (method.name.equals(SHTranslator.getMappedName("g", "setupViewBobbing")) && method.desc.equals("(F)V"))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof FieldInsnNode)
                    {
                        FieldInsnNode fieldNode = (FieldInsnNode) node;

                        if ((fieldNode.name.equals(SHTranslator.getMappedName("P", "distanceWalkedModified")) || fieldNode.name.equals(SHTranslator.getMappedName("O", "prevDistanceWalkedModified"))) && fieldNode.desc.equals("F"))
                        {
                            list.add(node);
                            list.add(new VarInsnNode(ALOAD, 2));
                            list.add(new VarInsnNode(FLOAD, 1));
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "getDistanceWalked", "(FL" + varEntity + ";F)F", false));
                            continue;
                        }
                    }

                    if (node instanceof LdcInsnNode)
                    {
                        LdcInsnNode ldcNode = (LdcInsnNode) node;

                        if (ldcNode.cst instanceof Float && (Float) ldcNode.cst == 0.5F)
                        {
                            list.add(node);
                            list.add(new VarInsnNode(ALOAD, 2));
                            list.add(new VarInsnNode(FLOAD, 1));
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "getViewBobbing", "(FL" + varEntity + ";F)F", false));
                            continue;
                        }
                    }

                    if (node instanceof MethodInsnNode)
                    {
                        MethodInsnNode methodNode = (MethodInsnNode) node;

                        if (methodNode.name.equals("abs") && methodNode.desc.equals("(F)F") && methodNode.owner.equals("java/lang/Math") && node.getNext() instanceof InsnNode && node.getNext().getOpcode() == FNEG)
                        {
                            list.add(node);
                            list.add(new VarInsnNode(ALOAD, 2));
                            list.add(new VarInsnNode(FLOAD, 1));
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "getViewBobbing", "(FL" + varEntity + ";F)F", false));
                            continue;
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                flag = true;
            }
            else if (method.name.equals(SHTranslator.getMappedName("a", "setupCameraTransform")) && method.desc.equals("(FI)V"))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof MethodInsnNode)
                    {
                        MethodInsnNode methodNode = (MethodInsnNode) node;

                        if (methodNode.name.equals(SHTranslator.getMappedName("h", "orientCamera")) && methodNode.desc.equals("(F)V"))
                        {
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "orientCameraPre", SHTranslator.getMappedName("(Lblt;F)V", "(Lnet/minecraft/client/renderer/EntityRenderer;F)V"), false));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new VarInsnNode(FLOAD, 1));
                            list.add(node);
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new VarInsnNode(FLOAD, 1));
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "orientCameraPost", SHTranslator.getMappedName("(Lblt;F)V", "(Lnet/minecraft/client/renderer/EntityRenderer;F)V"), false));
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
