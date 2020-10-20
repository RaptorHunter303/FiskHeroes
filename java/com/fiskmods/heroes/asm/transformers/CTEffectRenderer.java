package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;
import com.fiskmods.heroes.client.particle.SHParticlesClient;

public class CTEffectRenderer extends ClassTransformerBase
{
    public CTEffectRenderer()
    {
        super("net.minecraft.client.particle.EffectRenderer");
    }

    public static int plus(int i)
    {
        return ++i == 3 ? ++i : i;
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        String[] names = {"<init>", SHTranslator.getMappedName("a", "updateEffects"), SHTranslator.getMappedName("a", "renderParticles"), SHTranslator.getMappedName("b", "renderLitParticles"), SHTranslator.getMappedName("a", "clearEffects")};
        String[] descs = {SHTranslator.getMappedName("(Lahb;Lbqf;)V", "(Lnet/minecraft/world/World;Lnet/minecraft/client/renderer/texture/TextureManager;)V"), "()V", SHTranslator.getMappedName("(Lsa;F)V", "(Lnet/minecraft/entity/Entity;F)V"), SHTranslator.getMappedName("(Lsa;F)V", "(Lnet/minecraft/entity/Entity;F)V"), SHTranslator.getMappedName("(Lahb;)V", "(Lnet/minecraft/world/World;)V")};
        boolean flag = false;

        for (MethodNode method : methods)
        {
            for (int i = 0; i < names.length; ++i)
            {
                String name = names[i];
                String desc = descs[i];

                if (method.name.equals(name) && method.desc.equals(desc))
                {
                    InsnList list = new InsnList();

                    for (int j = 0; j < method.instructions.size(); ++j)
                    {
                        AbstractInsnNode node = method.instructions.get(j);

                        if (node instanceof InsnNode)
                        {
                            InsnNode insnNode = (InsnNode) node;

                            if (insnNode.getOpcode() == ICONST_3)
                            {
                                if (method.name.equals(names[2]) && method.desc.equals(descs[2]))
                                {
                                    list.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(SHParticlesClient.class), "FX_LAYERS", "I"));
                                    continue;
                                }
                            }
                            else if (insnNode.getOpcode() == ICONST_4)
                            {
                                list.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(SHParticlesClient.class), "FX_LAYERS", "I"));
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

            if (method.name.equals(names[2]) && method.desc.equals(descs[2]))
            {
                InsnList list = new InsnList();
                int line = 0;

                for (int j = 0; j < method.instructions.size(); ++j)
                {
                    AbstractInsnNode node = method.instructions.get(j);

                    if (node instanceof LineNumberNode)
                    {
                        LineNumberNode lineNode = (LineNumberNode) node;
                        line = lineNode.line;
                    }

                    if (node instanceof MethodInsnNode)
                    {
                        MethodInsnNode methodNode = (MethodInsnNode) node;

                        if (node.getOpcode() == INVOKEVIRTUAL && methodNode.owner.equals(SHTranslator.getMappedName("bmh", "net/minecraft/client/renderer/Tessellator")) && methodNode.name.equals(SHTranslator.getMappedName("b", "startDrawingQuads")) && methodNode.desc.equals("()V"))
                        {
                            list.add(node);

                            LabelNode labelNode = new LabelNode();
                            list.add(labelNode);
                            list.add(new LineNumberNode(line + 1, labelNode));
                            list.add(new VarInsnNode(ILOAD, 9));
                            list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(SHParticlesClient.class), "bindParticleTextures", "(I)V", false));
                            continue;
                        }
                    }

                    if (node instanceof IincInsnNode)
                    {
                        IincInsnNode iincNode = (IincInsnNode) node;

                        if (iincNode.var == 8 && iincNode.incr == 1)
                        {
                            list.add(new VarInsnNode(ILOAD, 8));
                            list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(CTEffectRenderer.class), "plus", "(I)I", false));
                            list.add(new VarInsnNode(ISTORE, 8));
                            continue;
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                flag = true;
            }

            if (method.name.equals(SHTranslator.getMappedName("b", "getStatistics")) && method.desc.equals("()Ljava/lang/String;"))
            {
                InsnList list = new InsnList();
                boolean open = false;

                for (int i = 0; i < method.instructions.size(); ++i)
                {
                    AbstractInsnNode node = method.instructions.get(i);

                    if (node.getOpcode() == ARETURN)
                    {
                        open = false;
                    }

                    if (open)
                    {
                        continue;
                    }

                    if (node instanceof TypeInsnNode)
                    {
                        TypeInsnNode typeNode = (TypeInsnNode) node;

                        if (node.getOpcode() == NEW && typeNode.desc.equals("java/lang/StringBuilder"))
                        {
                            open = true;
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, SHTranslator.getMappedName("bkn", "net/minecraft/client/particle/EffectRenderer"), SHTranslator.getMappedName("c", "fxLayers"), "[Ljava/util/List;"));
                            list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(SHParticlesClient.class), "getParticlesInWorld", "([Ljava/util/List;)I", false));
                            list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(String.class), "valueOf", "(I)Ljava/lang/String;", false));
                            continue;
                        }
                    }

                    list.add(node);
                }

                method.instructions.clear();
                method.instructions.add(list);
                method.visitMaxs(1, 1);
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
