package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTRenderPlayer extends ClassTransformerBase
{
    protected static String varRenderPlayer;
    protected static String varACP;

    public CTRenderPlayer()
    {
        super("net.minecraft.client.renderer.entity.RenderPlayer");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        int success = 0;

        for (MethodNode method : methods)
        {
            if (method.name.equals(SHTranslator.getMappedName("a", "renderLivingAt")) && method.desc.equals("(L" + varLivingBase + ";DDD)V"))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof MethodInsnNode)
                    {
                        MethodInsnNode methodNode = (MethodInsnNode) node;

                        if (methodNode.getOpcode() == INVOKEVIRTUAL && methodNode.desc.equals("(L" + varACP + ";DDD)V"))
                        {
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "applyPlayerRenderTranslation", "(L" + varRenderPlayer + ";L" + varACP + ";DDD)V", false));
                            continue;
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                ++success;
            }
            else if (method.name.equals(SHTranslator.getMappedName("a", "doRender")) && method.desc.equals("(L" + varACP + ";DDDFF)V"))
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
                            list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKS, "getScaledSneakOffset", "(L" + varEntity + ";D)D", false));
                            continue;
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                ++success;
            }
            else if (method.name.equals(SHTranslator.getMappedName("a", "rotateCorpse")) && method.desc.equals("(L" + varACP + ";FFF)V"))
            {
                InsnList list = new InsnList();

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node instanceof MethodInsnNode)
                    {
                        MethodInsnNode methodNode = (MethodInsnNode) node;

                        if (node.getOpcode() == INVOKESPECIAL && methodNode.name.equals(SHTranslator.getMappedName("a", "rotateCorpse")) && methodNode.desc.equals("(L" + varLivingBase + ";FFF)V"))
                        {
                            list.add(node);
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new VarInsnNode(FLOAD, 4));
                            list.add(new MethodInsnNode(INVOKESTATIC, MODELHELPER, "rotateCorpse", "(L" + varPlayer + ";F)V", false));
                            continue;
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                ++success;
            }
            else if (method.name.equals(SHTranslator.getMappedName("a", "renderFirstPersonArm")) && method.desc.equals("(L" + varPlayer + ";)V"))
            {
                InsnList list = new InsnList();
                boolean flag = true;

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (flag && node instanceof LineNumberNode)
                    {
                        LabelNode label = new LabelNode();
                        flag = false;

                        list.add(node);
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, ASMHOOKSCLIENT, "renderFirstPersonArm", "(L" + varRenderPlayer + ";L" + varPlayer + ";)Z", false));
                        list.add(new JumpInsnNode(IFEQ, label));
                        list.add(new InsnNode(RETURN));
                        list.add(label);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        continue;
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                ++success;
            }
        }

        return success == 4;
    }

    @Override
    public boolean processFields(List<FieldNode> fields)
    {
        return true;
    }

    @Override
    public void setupMappings()
    {
        super.setupMappings();
        varRenderPlayer = SHTranslator.getMappedName("bop", "net/minecraft/client/renderer/entity/RenderPlayer");
        varACP = SHTranslator.getMappedName("blg", "net/minecraft/client/entity/AbstractClientPlayer");
    }
}
