package com.fiskmods.heroes.common.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fiskmods.heroes.common.data.world.SHMapData;
import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public enum RuleHandler
{
    INSTANCE;

    private Map<String, RuleSet> ruleSets;
    private RuleSet ruleSet;

    public void tick()
    {
        if (ruleSet != null)
        {
            ruleSet.tick();
        }
    }

    public void load(File saveDir)
    {
        File dir = new File(saveDir, "rulesets");
        ruleSets = new HashMap<>();

        if (!dir.exists())
        {
            dir.mkdirs();
        }

        String suffix = ".dat";
        File[] files = dir.listFiles((f, s) -> s.endsWith(suffix));

        if (files != null)
        {
            for (File file : files)
            {
                RuleSet set = new RuleSet();
                String s = file.getName();

                set.load(file);
                ruleSets.put(s.substring(0, s.length() - suffix.length()), set);
            }
        }
    }

    public ImmutableRuleSet copy(String key)
    {
        return ruleSets.containsKey(key) ? new ImmutableRuleSet(ruleSets.get(key)) : null;
    }

    public boolean load(String key)
    {
        if (ruleSets.containsKey(key))
        {
            ruleSet = new RuleSet(ruleSets.get(key));
            ruleSet.markDirty();
            return true;
        }

        return false;
    }

    public void save(World world, File saveDir, String key) throws IOException
    {
        File file = new File(saveDir, "rulesets/" + key + ".dat");

        if (!file.exists())
        {
            file.createNewFile();
        }

        ruleSet.save(file); // FIXME: Notify rule handler blocks
        ruleSets.put(key, new RuleSet(ruleSet));
        SHMapData.get(world).notifyRuleSetChange(key);
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        ruleSet = new RuleSet();

        if (nbt.hasKey("RuleSet", NBT.TAG_COMPOUND))
        {
            ruleSet.readFromNBT(nbt.getCompoundTag("RuleSet"));
        }
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        if (ruleSet != null)
        {
            nbt.setTag("RuleSet", ruleSet.writeToNBT(new NBTTagCompound()));
        }
    }

    public static ImmutableSet<String> getKeys()
    {
        return ImmutableSet.copyOf(INSTANCE.ruleSets.keySet());
    }

    public static RuleSet getGlobal()
    {
        if (INSTANCE.ruleSet == null)
        {
            INSTANCE.ruleSet = new RuleSet();
        }

        return INSTANCE.ruleSet;
    }

    public static RuleSet getLocal(World world, int x, int z)
    {
        return SHMapData.get(world).getRuleSet(world, x, z);
    }

    public static RuleSet getLocal(Entity entity)
    {
        return entity != null ? getLocal(entity.worldObj, (int) entity.posX, (int) entity.posZ) : getGlobal();
    }
}
