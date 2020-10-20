package com.fiskmods.heroes.asm.transformers;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.fiskmods.heroes.FiskHeroes;
import com.fiskmods.heroes.asm.SHTranslator;

import net.minecraft.launchwrapper.IClassTransformer;

public abstract class ClassTransformerBase implements IClassTransformer, Opcodes
{
    public static final Logger LOGGER = LogManager.getLogger(FiskHeroes.NAME + "/ASM");

    protected static final String ASMHOOKS = "com/fiskmods/heroes/asm/ASMHooks";
    protected static final String ASMHOOKSCLIENT = "com/fiskmods/heroes/asm/ASMHooksClient";
    protected static final String MODELHELPER = "com/fiskmods/heroes/client/model/ModelHelper";
    protected static final String RENDERHOOKS = "com/fiskmods/heroes/client/SHRenderHooks";

    private boolean initVars = false;
    protected static String varPlayer;
    protected static String varEntity;
    protected static String varLivingBase;
    protected static String varAABB;
    protected static String varIBlockAccess;
    protected static String varBlock;
    protected static String varWorld;
    protected static String varResourceLocation;

    protected final String classPath;
    protected final String unobfClass;

    public ClassTransformerBase(String classPath)
    {
        this.classPath = classPath;
        unobfClass = classPath.substring(classPath.lastIndexOf('.') + 1);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        try
        {
            if (transformedName.equals(classPath))
            {
                LOGGER.debug(String.format("Patching Class %s (%s)", unobfClass, name));

                ClassReader reader = new ClassReader(bytes);
                ClassNode node = new ClassNode();
                reader.accept(node, 0);

                setupMappings();
                boolean success = processFields(node.fields) && processMethods(node.methods);
                addInterface(node.interfaces);

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                node.accept(cw);

                if (success)
                {
                    LOGGER.debug(String.format("Patching Class %s done", unobfClass));
                }
                else
                {
                    LOGGER.error(String.format("Patching Class %s FAILED!", unobfClass));
                }

                writeClassFile(cw, String.format("%s (%s)", unobfClass, name));
                return cw.toByteArray();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return bytes;
    }

    public void addInterface(List<String> interfaces)
    {
    }

    public abstract boolean processMethods(List<MethodNode> methods);

    public abstract boolean processFields(List<FieldNode> fields);

    public void setupMappings()
    {
        if (!initVars)
        {
            initVars = true;
            varPlayer = SHTranslator.getMappedName("yz", "net/minecraft/entity/player/EntityPlayer");
            varEntity = SHTranslator.getMappedName("sa", "net/minecraft/entity/Entity");
            varLivingBase = SHTranslator.getMappedName("sv", "net/minecraft/entity/EntityLivingBase");
            varAABB = SHTranslator.getMappedName("azt", "net/minecraft/util/AxisAlignedBB");
            varIBlockAccess = SHTranslator.getMappedName("ahl", "net/minecraft/world/IBlockAccess");
            varBlock = SHTranslator.getMappedName("aji", "net/minecraft/block/Block");
            varWorld = SHTranslator.getMappedName("ahb", "net/minecraft/world/World");
            varResourceLocation = SHTranslator.getMappedName("bqx", "net/minecraft/util/ResourceLocation");
        }
    }

    public void sendPatchLog(String method)
    {
        LOGGER.debug("\tPatching method " + method + " in " + unobfClass);
    }

    public static void writeClassFile(ClassWriter cw, String name)
    {
        try
        {
            File outDir = new File("debug/");
            outDir.mkdirs();
            DataOutputStream dout = new DataOutputStream(new FileOutputStream(new File(outDir, name + ".class")));
            dout.write(cw.toByteArray());
            dout.flush();
            dout.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static MethodNode generateSetter(String className, String methodName, String fieldName, String fieldType)
    {
        MethodNode mn = new MethodNode(ACC_PUBLIC, methodName, "(" + fieldType + ")V", null, null);
        mn.visitCode();
        mn.visitVarInsn(ALOAD, 0);
        int opCode;

        if (fieldType.equals("I") || fieldType.equals("Z"))
        {
            opCode = ILOAD;
        }
        else if (fieldType.equals("L"))
        {
            opCode = LLOAD;
        }
        else if (fieldType.equals("F"))
        {
            opCode = FLOAD;
        }
        else if (fieldType.equals("D"))
        {
            opCode = DLOAD;
        }
        else
        {
            opCode = ALOAD;
        }

        mn.visitVarInsn(opCode, 1);
        mn.visitFieldInsn(PUTFIELD, className, fieldName, fieldType);
        mn.visitInsn(RETURN);
        mn.visitMaxs(2, 2);
        mn.visitEnd();
        return mn;
    }

    public static MethodNode generateGetter(String className, String methodName, String fieldName, String fieldType)
    {
        MethodNode mn = new MethodNode(ACC_PUBLIC, methodName, "()" + fieldType, null, null);
        mn.visitCode();
        mn.visitVarInsn(ALOAD, 0);
        mn.visitFieldInsn(GETFIELD, className, fieldName, fieldType);
        int opCode;

        if (fieldType.equals("I") || fieldType.equals("Z"))
        {
            opCode = IRETURN;
        }
        else if (fieldType.equals("L"))
        {
            opCode = LRETURN;
        }
        else if (fieldType.equals("F"))
        {
            opCode = FRETURN;
        }
        else if (fieldType.equals("D"))
        {
            opCode = DRETURN;
        }
        else
        {
            opCode = ARETURN;
        }

        mn.visitInsn(opCode);
        mn.visitMaxs(1, 1);
        mn.visitEnd();
        return mn;
    }
}
