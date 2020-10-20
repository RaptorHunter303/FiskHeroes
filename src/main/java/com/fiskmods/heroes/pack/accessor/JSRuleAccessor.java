package com.fiskmods.heroes.pack.accessor;

import com.fiskmods.heroes.common.config.Rule;

public class JSRuleAccessor<T>
{
    private final Rule<T> rule;

    private JSRuleAccessor(Rule<T> rule)
    {
        this.rule = rule;
    }

    public static <T> JSRuleAccessor wrap(Rule<T> rule)
    {
        return new JSRuleAccessor(rule);
    }
    
    public String name()
    {
        return rule.delegate.name();
    }
    
    public T value()
    {
        return rule.defaultValue;
    }
    
    public boolean isBoolean()
    {
        return rule.ofType(Boolean.class);
    }
    
    public boolean isInteger()
    {
        return rule.ofType(Integer.class);
    }
    
    public boolean isFloat()
    {
        return rule.ofType(Float.class);
    }
    
    public boolean isDouble()
    {
        return rule.ofType(Double.class);
    }
}
