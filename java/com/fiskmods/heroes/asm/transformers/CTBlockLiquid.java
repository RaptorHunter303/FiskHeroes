package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.fiskmods.heroes.asm.SHTranslator;

public class CTBlockLiquid extends ClassTransformerBase
{
    protected static String varLiquid;

    public CTBlockLiquid()
    {
        super("net.minecraft.block.BlockLiquid");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        methods.add(methods.size(), generateMethod());
        return true;
    }

    private MethodNode generateMethod()
    {
        MethodNode mn = new MethodNode(ACC_PUBLIC, SHTranslator.getMappedName("a", "addCollisionBoxesToList"), "(L" + varWorld + ";IIIL" + varAABB + ";Ljava/util/List;L" + varEntity + ";)V", null, null);
        mn.visitCode();
        Label l0 = new Label();
        mn.visitLabel(l0);
        mn.visitVarInsn(ALOAD, 0);
        mn.visitVarInsn(ALOAD, 1);
        mn.visitVarInsn(ILOAD, 2);
        mn.visitVarInsn(ILOAD, 3);
        mn.visitVarInsn(ILOAD, 4);
        mn.visitVarInsn(ALOAD, 5);
        mn.visitVarInsn(ALOAD, 6);
        mn.visitVarInsn(ALOAD, 7);
        mn.visitMethodInsn(INVOKESTATIC, ASMHOOKS, "addLiquidCollisionBoxesToList", "(L" + varLiquid + ";L" + varWorld + ";IIIL" + varAABB + ";Ljava/util/List;L" + varEntity + ";)V", false);
        Label l1 = new Label();
        mn.visitLabel(l1);
        mn.visitInsn(RETURN);
        Label l2 = new Label();
        mn.visitLabel(l2);
        mn.visitLocalVariable("this", "L" + varLiquid + ";", null, l0, l2, 0);
        mn.visitLocalVariable("world", "L" + varWorld + ";", null, l0, l2, 1);
        mn.visitLocalVariable("x", "I", null, l0, l2, 2);
        mn.visitLocalVariable("y", "I", null, l0, l2, 3);
        mn.visitLocalVariable("z", "I", null, l0, l2, 4);
        mn.visitLocalVariable("aabb", "L" + varAABB + ";", null, l0, l2, 5);
        mn.visitLocalVariable("list", "Ljava/util/List;", null, l0, l2, 6);
        mn.visitLocalVariable("entity", "L" + varEntity + ";", null, l0, l2, 7);
        mn.visitMaxs(8, 8);
        mn.visitEnd();
        return mn;
    }

    @Override
    public boolean processFields(List<FieldNode> fields)
    {
        return true;
    }

    @Override
    public void setupMappings()
    {
        varLiquid = SHTranslator.getMappedName("alw", "net/minecraft/block/BlockLiquid");
    }
}
