package com.fiskmods.heroes.asm;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class ASMHelper implements Opcodes
{
    public static TypeInsnNode cast(String to)
    {
        return new TypeInsnNode(CHECKCAST, to);
    }

    public static MethodInsnNode and()
    {
        return new MethodInsnNode(INVOKESTATIC, Type.getInternalName(ASMHelper.class), "and", "(ZZ)Z", false);
    }

    public static boolean and(boolean arg1, boolean arg2)
    {
        return arg1 && arg2;
    }

    public static MethodInsnNode or()
    {
        return new MethodInsnNode(INVOKESTATIC, Type.getInternalName(ASMHelper.class), "or", "(ZZ)Z", false);
    }

    public static void or(List<AbstractInsnNode> list, Runnable arg1, Runnable arg2)
    {
        LabelNode l0 = new LabelNode();
        list.add(l0);
        arg1.run();
        LabelNode l1 = new LabelNode();
        list.add(l1);
        list.add(new JumpInsnNode(IFNE, l1));
        arg2.run();
        list.add(new JumpInsnNode(IFNE, l1));
        list.add(new InsnNode(ICONST_0));
        list.add(new InsnNode(IRETURN));
        list.add(l1);
        list.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        list.add(new InsnNode(ICONST_1));
        list.add(new InsnNode(IRETURN));

//        Label l0 = new Label();
//        mv.visitLabel(l0);
//        mv.visitLineNumber(32, l0);
//        mv.visitVarInsn(ILOAD, 0);
//        Label l1 = new Label();
//        mv.visitJumpInsn(IFNE, l1);
//        mv.visitVarInsn(ILOAD, 1);
//        mv.visitJumpInsn(IFNE, l1);
//        mv.visitInsn(ICONST_0);
//        mv.visitInsn(IRETURN);
//        mv.visitLabel(l1);
//        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//        mv.visitInsn(ICONST_1);
//        mv.visitInsn(IRETURN);
    }

    public static boolean or(boolean arg1, boolean arg2)
    {
        return arg1 || arg2;
    }

    public static MethodInsnNode not()
    {
        return new MethodInsnNode(INVOKESTATIC, Type.getInternalName(ASMHelper.class), "not", "(Z)Z", false);
    }

    public static boolean not(boolean arg)
    {
        return !arg;
    }

    public static MethodInsnNode conditional()
    {
        return new MethodInsnNode(INVOKESTATIC, Type.getInternalName(ASMHelper.class), "conditional", "(ZLjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
    }

    public static Object conditional(boolean condition, Object arg1, Object arg2)
    {
        return condition ? arg1 : arg2;
    }
}
