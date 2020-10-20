package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.fiskmods.heroes.asm.SHTranslator;

public abstract class ClassTransformerMethodProcess extends ClassTransformerBase
{
    private final String methodName;
    private final String methodNameDev;
    private final String methodDesc;
    private final String methodDescDev;

    private String methName;
    private String methDesc;

    public ClassTransformerMethodProcess(String classPath, String methodName, String methodNameDev, String methodDesc, String methodDescDev)
    {
        super(classPath);
        this.methodName = methodName;
        this.methodNameDev = methodNameDev;
        this.methodDesc = methodDesc;
        this.methodDescDev = methodDescDev;
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        for (MethodNode method : methods)
        {
            if (method.name.equals(methName) && method.desc.equals(methDesc))
            {
                sendPatchLog(methodNameDev);
                return processMethod(method);
            }
        }

        return false;
    }

    public abstract boolean processMethod(MethodNode method);

    @Override
    public boolean processFields(List<FieldNode> fields)
    {
        return true;
    }

    @Override
    public void setupMappings()
    {
        super.setupMappings();
        methName = SHTranslator.getMappedName(methodName, methodNameDev);
        methDesc = SHTranslator.getMappedName(methodDesc, methodDescDev);
    }
}
