package com.fiskmods.heroes.asm.transformers;

import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.fiskmods.heroes.asm.SHTranslator;
import com.fiskmods.heroes.common.IOffhandRender;

public class CTItemRenderer extends ClassTransformerBase
{
    public CTItemRenderer()
    {
        super("net.minecraft.client.renderer.ItemRenderer");
    }

    private String itemRendererClass;

    @Override
    public void addInterface(List<String> interfaces)
    {
        interfaces.add(Type.getInternalName(IOffhandRender.class));
    }

    private void processRenderItemMethod(MethodNode method)
    {
        InsnList list = new InsnList();

        for (AbstractInsnNode node : method.instructions.toArray())
        {
            if (node.getOpcode() == RETURN)
            {
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(FLOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, RENDERHOOKS, "renderItemInFirstPerson", SHTranslator.getMappedName("(Lbly;F)V", "(Lnet/minecraft/client/renderer/ItemRenderer;F)V"), false));
            }

            list.add(node);
        }

        method.instructions.clear();
        method.instructions.add(list);
    }

    @Override
    public boolean processMethods(List<MethodNode> methods)
    {
        int found = 0;

        for (MethodNode mn : methods)
        {
            if (mn.name.equals(SHTranslator.getMappedName("a", "renderItemInFirstPerson")) && mn.desc.equals("(F)V"))
            {
                processRenderItemMethod(mn);
                found++;
            }
        }

        methods.add(methods.size(), generateSetter(itemRendererClass, "setItemToRenderSH", SHTranslator.getMappedName("e", "itemToRender"), SHTranslator.getMappedName("Ladd;", "Lnet/minecraft/item/ItemStack;")));
        methods.add(methods.size(), generateSetter(itemRendererClass, "setEquippedProgressSH", SHTranslator.getMappedName("field_78454_c", "equippedProgress"), "F"));
        methods.add(methods.size(), generateSetter(itemRendererClass, "setPrevEquippedProgressSH", SHTranslator.getMappedName("field_78451_d", "prevEquippedProgress"), "F"));
        methods.add(methods.size(), generateGetter(itemRendererClass, "getItemToRenderSH", SHTranslator.getMappedName("e", "itemToRender"), SHTranslator.getMappedName("Ladd;", "Lnet/minecraft/item/ItemStack;")));
        methods.add(methods.size(), generateGetter(itemRendererClass, "getEquippedProgressSH", SHTranslator.getMappedName("field_78454_c", "equippedProgress"), "F"));
        methods.add(methods.size(), generateGetter(itemRendererClass, "getPrevEquippedProgressSH", SHTranslator.getMappedName("field_78451_d", "prevEquippedProgress"), "F"));
        return found == 1;
    }

    @Override
    public boolean processFields(List<FieldNode> fields)
    {
        return true;
    }

    @Override
    public void setupMappings()
    {
        itemRendererClass = SHTranslator.getMappedClassName("client.renderer.ItemRenderer");
    }
}
