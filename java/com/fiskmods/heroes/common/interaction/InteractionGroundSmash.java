package com.fiskmods.heroes.common.interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fiskmods.heroes.common.config.Rule;
import com.fiskmods.heroes.common.data.Cooldowns.Cooldown;
import com.fiskmods.heroes.common.hero.Hero;
import com.fiskmods.heroes.common.hero.modifier.Ability;
import com.fiskmods.heroes.util.FiskMath;
import com.fiskmods.heroes.util.SHHelper;
import com.fiskmods.heroes.util.VectorHelper;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Vec3;

public class InteractionGroundSmash extends InteractionBase
{
    public static final String KEY_GROUND_SMASH = "GROUND_SMASH";

    public InteractionGroundSmash()
    {
        super(InteractionType.RIGHT_CLICK_BLOCK);
        requireModifier(Ability.GEOKINESIS);
    }

    @Override
    public boolean serverRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return true;
    }

    @Override
    public boolean clientRequirements(EntityPlayer player, InteractionType type, int x, int y, int z)
    {
        return Cooldown.GROUND_SMASH.available(player) && SHHelper.getHero(player).isKeyPressed(player, KEY_GROUND_SMASH);
    }

    @Override
    public void receive(EntityPlayer sender, EntityPlayer clientPlayer, InteractionType type, Side side, int x, int y, int z)
    {
        if (side.isServer())
        {
            List<EntityFallingBlock> list = new ArrayList<>();
            Hero hero = SHHelper.getHero(sender);
            Random rand = new Random();

            int radius = Rule.RADIUS_GROUNDSMASH.get(sender, hero);

            for (int x1 = -radius; x1 <= radius; ++x1)
            {
                for (int z1 = -radius; z1 <= radius; ++z1)
                {
                    if (Rule.GRIEF_GEOKINESIS.get(sender.worldObj, x + x1, z + z1))
                    {
                        for (int y1 = -radius / 2; y1 <= radius; ++y1)
                        {
                            Block block = sender.worldObj.getBlock(x + x1, y + y1, z + z1);
                            int metadata = sender.worldObj.getBlockMetadata(x + x1, y + y1, z + z1);

                            if ((x1 <= radius - 2 && y1 <= radius - 2 && z1 <= radius - 2 || x1 >= -radius + 2 && y1 >= -radius + 2 && z1 >= -radius + 2 || rand.nextInt(3) != 0) && block != Blocks.air && block.isBlockSolid(sender.worldObj, x + x1, y + y1, z + z1, 0) && block.getBlockHardness(sender.worldObj, x + x1, y + y1, z + z1) >= 0)
                            {
                                EntityFallingBlock entity = new EntityFallingBlock(sender.worldObj, x + x1 + 0.5F, y + y1 + 0.5F, z + z1 + 0.5F, block, metadata);
                                entity.motionY += 0.5F;
                                entity.field_145812_b = -100;
                                entity.field_145813_c = false;

                                NBTTagCompound nbt = new NBTTagCompound();
                                entity.writeToNBT(nbt);
                                nbt.setFloat("FallHurtAmount", 100);
                                nbt.setInteger("FallHurtMax", 100);
                                entity.readFromNBT(nbt);

                                list.add(entity);
                                sender.worldObj.spawnEntityInWorld(entity);
                                sender.worldObj.setBlock(x + x1, y + y1, z + z1, Blocks.air);
                            }
                        }
                    }
                }
            }

            List<EntityLivingBase> list1 = VectorHelper.getEntitiesNear(EntityLivingBase.class, sender.worldObj, x + 0.5D, y + 0.5D, z + 0.5D, radius);

            if (!list1.isEmpty())
            {
                float dmg = Rule.DMG_GROUNDSMASH.get(sender, hero);
                float knockback = Rule.KNOCKBACK_GROUNDSMASH.get(sender, hero);
                
                for (EntityLivingBase entity : list1)
                {
                    float amount = FiskMath.getScaledDistance(Vec3.createVectorHelper(x + 0.5D, y + 0.5D, z + 0.5D), VectorHelper.centerOf(entity), radius);
                    entity.attackEntityFrom(new EntityDamageSource("explosion.player", sender).setExplosion(), dmg * amount);
                    SHHelper.knockbackWithoutNotify(entity, sender, knockback * amount);
                }
            }

            sender.worldObj.createExplosion(sender, x, y, z, Rule.RADIUS_GROUNDSMASH_EXPL.get(sender, hero), false);
        }
        else if (sender == clientPlayer)
        {
            sender.swingItem();
            Cooldown.GROUND_SMASH.set(sender);
        }
    }

    @Override
    public TargetPoint getTargetPoint(EntityPlayer player, int x, int y, int z)
    {
        return TARGET_NONE;
    }
}
