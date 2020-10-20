package com.fiskmods.heroes.common.entity.arrow;

import java.util.List;

import com.fiskmods.heroes.common.arrowtype.ArrowType;
import com.fiskmods.heroes.common.arrowtype.ArrowTypeManager;
import com.fiskmods.heroes.common.data.DataManager;
import com.fiskmods.heroes.util.SHHelper;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityTrickArrow extends EntityArrow implements IEntityAdditionalSpawnData
{
    protected String shooterName = null;
    private ItemStack arrowItem;
    private int arrowId;
    private boolean noEntity;

    protected Block inTile;
    protected int inData, xTile = -1, yTile = -1, zTile = -1;
    protected int ticksInGround = 0, ticksInAir = 0;
    public boolean inGround = false, horizontal = false;

    private int knockbackStrength;
    public float spinTime, prevSpinTime;

    public EntityTrickArrow(World world)
    {
        super(world);
        yOffset = 0;
        init(null, false);
    }

    public EntityTrickArrow(World world, double x, double y, double z)
    {
        super(world, x, y, z);
        init(null, false);
    }

    public EntityTrickArrow(World world, EntityLivingBase shooter, float velocity)
    {
        this(world, shooter, velocity, false);
    }

    public EntityTrickArrow(World world, EntityLivingBase shooter, float velocity, boolean horizontalBow)
    {
        super(world);
        init(shooter, horizontalBow);
        shootFrom(shooter, velocity);
    }

    protected void init(EntityLivingBase shooter, boolean horizontalBow)
    {
        renderDistanceWeight = 10;
        shootingEntity = shooter;
        horizontal = horizontalBow;

        if (shooter instanceof EntityPlayer)
        {
            canBePickedUp = 1;
        }

        setSize(0.2F, 0.2F);
    }

    protected void shootFrom(Entity shooter, float velocity)
    {
        setLocationAndAngles(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        posX -= MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        posY -= 0.1;
        posZ -= MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        setPosition(posX, posY, posZ);

        yOffset = 0.0F;
        motionX = -MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI);
        motionZ = MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI);
        motionY = -MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI);
        setThrowableHeading(motionX, motionY, motionZ, velocity * getVelocityFactor(), getStrayFactor() * (horizontal ? 0.5F : 1));
    }

    @Override
    public void setThrowableHeading(double x, double y, double z, float speed, float stray)
    {
        float f = MathHelper.sqrt_double(x * x + y * y + z * z);
        x /= f;
        y /= f;
        z /= f;
        x += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * stray;
        y += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * stray;
        z += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * stray;
        x *= speed;
        y *= speed;
        z *= speed;
        motionX = x;
        motionY = y;
        motionZ = z;
        float f1 = MathHelper.sqrt_double(x * x + z * z);
        prevRotationYaw = rotationYaw = (float) (Math.atan2(x, z) * 180.0D / Math.PI);
        prevRotationPitch = rotationPitch = (float) (Math.atan2(y, f1) * 180.0D / Math.PI);
        ticksInGround = 0;
    }

    public Entity getShooter()
    {
        if (shootingEntity == null && shooterName != null)
        {
            shootingEntity = worldObj.getPlayerEntityByName(shooterName);
        }

        return shootingEntity;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(17, "");
    }

    public EntityTrickArrow setArrowItem(ItemStack itemstack)
    {
        arrowItem = itemstack;
        return this;
    }

    public ItemStack getArrowItem()
    {
        if (arrowItem == null)
        {
            arrowItem = ArrowTypeManager.NORMAL.makeItem();
        }

        return arrowItem;
    }

    public EntityTrickArrow setArrowId(int id)
    {
        if (arrowItem != null && arrowItem.getItemDamage() != id)
        {
            arrowItem.setItemDamage(id);
            setArrowItem(arrowItem);
        }

        arrowId = id;
        return this;
    }

    public int getArrowId()
    {
        return arrowId;
    }

    public String getHero()
    {
        return dataWatcher.getWatchableObjectString(17);
    }

    public EntityTrickArrow setHero(String s)
    {
        dataWatcher.updateObject(17, s == null ? "" : s);
        return this;
    }

    public EntityTrickArrow setNoEntity()
    {
        noEntity = true;
        return this;
    }

    public boolean getNoEntity()
    {
        return noEntity;
    }

    @Override
    public void onUpdate()
    {
        super.onEntityUpdate();
        updateAngles();
        checkInGround();

        if (arrowShake > 0)
        {
            --arrowShake;
        }

        if (inGround)
        {
            updateInGround();
        }
        else
        {
            updateInAir();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z)
    {
        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
        {
            ticksInGround = 0;
        }
        super.setVelocity(x, y, z);
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player)
    {
        if (!worldObj.isRemote && inGround && arrowShake <= 0)
        {
            boolean flag = canBePickedUp == 1 || canBePickedUp == 2 && player.capabilities.isCreativeMode;

            if (canBePickedUp == 1 && !player.inventory.addItemStackToInventory(arrowItem))
            {
                flag = false;
            }

            if (flag)
            {
                playSound("random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.onItemPickup(this, 1);
                setDead();
            }
        }
    }

    protected DamageSource getDamageSource(Entity entity)
    {
        return DamageSource.causeArrowDamage(this, shootingEntity);
    }

    protected boolean canTargetEntity(Entity entity)
    {
        return !(entity instanceof EntityEnderman);
    }

    public int getKnockbackStrength()
    {
        return knockbackStrength;
    }

    @Override
    public void setKnockbackStrength(int value)
    {
        knockbackStrength = value;
    }

    protected void updateAngles()
    {
        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            prevRotationYaw = rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
            prevRotationPitch = rotationPitch = (float) (Math.atan2(motionY, f) * 180.0D / Math.PI);
        }

        prevSpinTime = spinTime;

        if (spinTime == 0)
        {
            spinTime += rand.nextFloat() * 100;
            prevSpinTime = spinTime;
        }

        if (!inGround)
        {
            spinTime += MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        }
    }

    protected void updatePosition()
    {
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

        for (rotationPitch = (float) (Math.atan2(motionY, f) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
        {
        }

        while (rotationPitch - prevRotationPitch >= 180.0F)
        {
            prevRotationPitch += 360.0F;
        }

        while (rotationYaw - prevRotationYaw < -180.0F)
        {
            prevRotationYaw -= 360.0F;
        }

        while (rotationYaw - prevRotationYaw >= 180.0F)
        {
            prevRotationYaw += 360.0F;
        }

        rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
        rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
        float motionFactor = 0.99F;

        if (isInWater())
        {
            for (int i = 0; i < 4; ++i)
            {
                float f3 = 0.25F;
                worldObj.spawnParticle("bubble", posX - motionX * f3, posY - motionY * f3, posZ - motionZ * f3, motionX, motionY, motionZ);
            }

            motionFactor = 0.8F;
        }

        updateMotion(motionFactor, getGravityVelocity() * (horizontal ? 1.5F : 1));
        setPosition(posX, posY, posZ);
    }

    protected void updateMotion(float factor, float adjustY)
    {
        motionX *= factor;
        motionY *= factor;
        motionZ *= factor;
        motionY -= adjustY;
    }

    protected void checkInGround()
    {
        Block block = worldObj.getBlock(xTile, yTile, zTile);

        if (block.getMaterial() != Material.air)
        {
            block.setBlockBoundsBasedOnState(worldObj, xTile, yTile, zTile);
            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(worldObj, xTile, yTile, zTile);

            if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(posX, posY, posZ)))
            {
                inGround = true;
            }
        }
    }

    protected void updateInGround()
    {
        Block block = worldObj.getBlock(xTile, yTile, zTile);
        int k = worldObj.getBlockMetadata(xTile, yTile, zTile);

        if (block == inTile && k == inData)
        {
            ++ticksInGround;

            if (ticksInGround >= getLifespan())
            {
                setDead();
            }
        }
        else
        {
            inGround = false;
            motionX *= rand.nextFloat() * 0.2F;
            motionY *= rand.nextFloat() * 0.2F;
            motionZ *= rand.nextFloat() * 0.2F;
            ticksInGround = 0;
            ticksInAir = 0;
        }
    }

    protected int getLifespan()
    {
        return 1200;
    }

    protected void updateInAir()
    {
        ++ticksInAir;
        MovingObjectPosition mop = checkForImpact(worldObj, this, getShooter(), 0.3D, ticksInAir >= 5);

        if (mop != null)
        {
            onImpact(mop);
        }

        if (worldObj.isRemote)
        {
            spawnTrailingParticles();
        }

        updatePosition();
        func_145775_I();
    }

    /**
     * Returns MovingObjectPosition of Entity or Block impacted, or null if nothing was struck
     *
     * @param entity The entity checking for impact, e.g. an arrow
     * @param shooter An entity not to be collided with, generally the shooter
     * @param hitBox The amount by which to expand the collided entities' bounding boxes when checking for impact (may be negative)
     * @param flag Optional flag to allow collision with shooter, e.g. (ticksInAir >= 5)
     */
    private MovingObjectPosition checkForImpact(World world, Entity entity, Entity shooter, double hitBox, boolean flag)
    {
        Vec3 vec3 = Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
        Vec3 vec31 = Vec3.createVectorHelper(entity.posX + entity.motionX, entity.posY + entity.motionY, entity.posZ + entity.motionZ);

        MovingObjectPosition mop = world.func_147447_a(vec3, vec31, false, true, false);
        vec3 = Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
        vec31 = Vec3.createVectorHelper(entity.posX + entity.motionX, entity.posY + entity.motionY, entity.posZ + entity.motionZ);

        if (mop != null)
        {
            vec31 = Vec3.createVectorHelper(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
        }

        Entity target = null;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.addCoord(entity.motionX, entity.motionY, entity.motionZ).expand(1.0D, 1.0D, 1.0D));
        double d0 = 0.0D;

        for (Entity aList : list)
        {
            Entity entity1 = aList;

            if (entity1.canBeCollidedWith() && (entity1 != shooter || flag))
            {
                AxisAlignedBB axisalignedbb = entity1.boundingBox.expand(hitBox, hitBox, hitBox);
                MovingObjectPosition mop1 = axisalignedbb.calculateIntercept(vec3, vec31);

                if (mop1 != null)
                {
                    double d1 = vec3.distanceTo(mop1.hitVec);

                    if (d1 < d0 || d0 == 0.0D)
                    {
                        target = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        if (target != null)
        {
            mop = new MovingObjectPosition(target);
        }

        if (mop != null && mop.entityHit instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) mop.entityHit;

            if (player.capabilities.disableDamage || shooter instanceof EntityPlayer && !((EntityPlayer) shooter).canAttackPlayer(player))
            {
                mop = null;
            }
        }

        return mop;
    }

    public float getVelocityFactor()
    {
        ArrowType type = ArrowType.getArrowById(getArrowId());

        if (type != null)
        {
            return type.getVelocityFactor();
        }

        return 1.5F;
    }

    public float getStrayFactor()
    {
        return 1.0F;
    }

    protected float getGravityVelocity()
    {
        ArrowType type = ArrowType.getArrowById(getArrowId());

        if (type != null)
        {
            return type.getGravityFactor();
        }

        return 0.05F;
    }

    protected String getParticleName()
    {
        return "crit";
    }

    protected boolean shouldSpawnParticles()
    {
        return getIsCritical() && getParticleName().length() > 0;
    }

    protected void spawnTrailingParticles()
    {
        if (shouldSpawnParticles())
        {
            for (int i = 0; i < 4; ++i)
            {
                worldObj.spawnParticle(getParticleName(), posX + motionX * i / 4.0D, posY + motionY * i / 4.0D, posZ + motionZ * i / 4.0D, -motionX, -motionY + 0.2D, -motionZ);
            }
        }
    }

    protected void onImpact(MovingObjectPosition mop)
    {
        if (mop.typeOfHit != MovingObjectType.BLOCK)
        {
            onImpactEntity(mop);
        }
        else
        {
            onImpactBlock(mop);
        }
    }

    protected void onImpactEntity(MovingObjectPosition mop)
    {
        if (mop.entityHit != null)
        {
            shootingEntity = getShooter();
            float dmg = calculateDamage(mop.entityHit);

            if (dmg > 0)
            {
                if (isBurningInternal() && canTargetEntity(mop.entityHit))
                {
                    SHHelper.ignite(mop.entityHit, 5);
                }

                if (mop.entityHit.attackEntityFrom(getDamageSource(mop.entityHit), dmg))
                {
                    if (mop.entityHit instanceof EntityLivingBase)
                    {
                        handlePostDamageEffects((EntityLivingBase) mop.entityHit);

                        if (shootingEntity instanceof EntityPlayerMP && mop.entityHit != shootingEntity && mop.entityHit instanceof EntityPlayer)
                        {
                            ((EntityPlayerMP) shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                        }
                    }

                    playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));

                    if (canTargetEntity(mop.entityHit))
                    {
                        setDead();
                    }
                }
                else
                {
                    motionX *= -0.10000000149011612D;
                    motionY *= -0.10000000149011612D;
                    motionZ *= -0.10000000149011612D;
                    rotationYaw += 180.0F;
                    prevRotationYaw += 180.0F;
                    ticksInAir = 0;
                }
            }
        }
    }

    /**
     * Used for special cases where despite burning, the arrow should not set fire to the target
     */
    protected boolean isBurningInternal()
    {
        return isBurning();
    }

    protected void onImpactBlock(MovingObjectPosition mop)
    {
        xTile = mop.blockX;
        yTile = mop.blockY;
        zTile = mop.blockZ;
        inTile = worldObj.getBlock(xTile, yTile, zTile);
        inData = worldObj.getBlockMetadata(xTile, yTile, zTile);
        motionX = (float) (mop.hitVec.xCoord - posX);
        motionY = (float) (mop.hitVec.yCoord - posY);
        motionZ = (float) (mop.hitVec.zCoord - posZ);
        float velocity = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        posX -= motionX / velocity * 0.05000000074505806D;
        posY -= motionY / velocity * 0.05000000074505806D;
        posZ -= motionZ / velocity * 0.05000000074505806D;
        playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
        inGround = true;
        arrowShake = 7;
        setIsCritical(false);

        if (inTile.getMaterial() != Material.air)
        {
            inTile.onEntityCollidedWithBlock(worldObj, xTile, yTile, zTile, this);
        }

        if (noEntity)
        {
            setDead();
        }
    }

    @Override
    protected void func_145775_I()
    {
        int i = MathHelper.floor_double(boundingBox.minX + 0.001D);
        int j = MathHelper.floor_double(boundingBox.minY + 0.001D);
        int k = MathHelper.floor_double(boundingBox.minZ + 0.001D);
        int l = MathHelper.floor_double(boundingBox.maxX - 0.001D);
        int i1 = MathHelper.floor_double(boundingBox.maxY - 0.001D);
        int j1 = MathHelper.floor_double(boundingBox.maxZ - 0.001D);

        if (worldObj.checkChunksExist(i, j, k, l, i1, j1))
        {
            for (int k1 = i; k1 <= l; ++k1)
            {
                for (int l1 = j; l1 <= i1; ++l1)
                {
                    for (int i2 = k; i2 <= j1; ++i2)
                    {
                        Block block = worldObj.getBlock(k1, l1, i2);

                        try
                        {
                            block.onEntityCollidedWithBlock(worldObj, k1, l1, i2, this);
                        }
                        catch (Throwable throwable)
                        {
                            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being collided with");
                            CrashReportCategory.func_147153_a(crashreportcategory, k1, l1, i2, block, worldObj.getBlockMetadata(k1, l1, i2));
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }
    }

    protected float calculateDamage(Entity entityHit)
    {
        float velocity = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        float dmg = (float) (velocity * getDamage());

        if (getIsCritical())
        {
            dmg += rand.nextInt(MathHelper.ceiling_double_int(dmg) / 2 + 2);
        }

        ArrowType type = ArrowType.getArrowById(getArrowId());

        if (type != null)
        {
            dmg *= type.getDamageMultiplier();
        }

        return dmg;
    }

    protected void handlePostDamageEffects(EntityLivingBase entityHit)
    {
        if (!worldObj.isRemote)
        {
            ArrowType<EntityTrickArrow> type = ArrowType.getArrowById(getArrowId());

            if (type != null)
            {
                DataManager.addArrowToEntity(entityHit, type, this);
            }
        }

        int k = getKnockbackStrength();

        if (k > 0)
        {
            float f3 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

            if (f3 > 0.0F)
            {
                double knockback = k * 0.6000000238418579D / f3;
                entityHit.addVelocity(motionX * knockback, 0.1D, motionZ * knockback);
            }
        }

        if (shootingEntity instanceof EntityLivingBase)
        {
            EnchantmentHelper.func_151384_a(entityHit, shootingEntity);
            EnchantmentHelper.func_151385_b((EntityLivingBase) shootingEntity, entityHit);
        }
    }

    public boolean onCaught(EntityLivingBase entity)
    {
        ItemStack arrowItem = ItemStack.copyItemStack(getArrowItem());

        if (arrowItem != null)
        {
            arrowItem.stackSize = 1;
            entity.setCurrentItemOrArmor(0, arrowItem);
        }

        setDead();

        return true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setShort("xTile", (short) xTile);
        compound.setShort("yTile", (short) yTile);
        compound.setShort("zTile", (short) zTile);
        compound.setShort("life", (short) ticksInGround);
        compound.setInteger("inTile", Block.getIdFromBlock(inTile));
        compound.setByte("inData", (byte) inData);
        compound.setByte("shake", (byte) arrowShake);
        compound.setByte("inGround", (byte) (inGround ? 1 : 0));
        compound.setByte("pickup", (byte) canBePickedUp);
        compound.setBoolean("horizontal", horizontal);
        compound.setBoolean("noEntity", noEntity);
        compound.setDouble("damage", getDamage());
        compound.setString("hero", getHero());

        if ((shooterName == null || shooterName.length() == 0) && shootingEntity instanceof EntityPlayer)
        {
            shooterName = shootingEntity.getCommandSenderName();
        }

        compound.setString("shooter", shooterName == null ? "" : shooterName);
        compound.setInteger("arrowId", arrowId);

        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (arrowItem != null)
        {
            arrowItem.writeToNBT(nbttagcompound);
        }

        compound.setTag("arrowItem", nbttagcompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        xTile = compound.getShort("xTile");
        yTile = compound.getShort("yTile");
        zTile = compound.getShort("zTile");
        ticksInGround = compound.getShort("life");
        inTile = Block.getBlockById(compound.getInteger("inTile"));
        inData = compound.getByte("inData") & 255;
        arrowShake = compound.getByte("shake") & 255;
        inGround = compound.getByte("inGround") == 1;
        canBePickedUp = compound.getByte("pickup");
        shooterName = compound.getString("shooter");
        arrowId = compound.getInteger("arrowId");
        horizontal = compound.getBoolean("horizontal");
        noEntity = compound.getBoolean("noEntity");
        setDamage(compound.getDouble("damage"));
        setHero(compound.getString("hero"));

        if (shooterName != null && shooterName.length() == 0)
        {
            shooterName = null;
        }

        ItemStack itemstack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("arrowItem"));

        if (itemstack != null)
        {
            arrowItem = itemstack;
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeInt(shootingEntity != null ? shootingEntity.getEntityId() : -1);
        ByteBufUtils.writeItemStack(buffer, getArrowItem());
        buffer.writeInt(getArrowId());
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        Entity shooter = worldObj.getEntityByID(buffer.readInt());

        if (shooter instanceof EntityLivingBase)
        {
            shootingEntity = shooter;
        }

        setArrowItem(ByteBufUtils.readItemStack(buffer));
        setArrowId(buffer.readInt());
    }

    public void inEntityUpdate(EntityLivingBase living)
    {
        ++ticksExisted;

        if (++ticksInGround == 1200)
        {
            setDead();
        }
    }

    @Override
    public String getCommandSenderName()
    {
        if (getArrowItem() != null)
        {
            if (getArrowItem().hasDisplayName())
            {
                return String.format("%s[%s]%s", EnumChatFormatting.AQUA, getArrowItem().getDisplayName(), EnumChatFormatting.RESET);
            }

            return getArrowItem().getItem().getItemStackDisplayName(getArrowItem());
        }

        return super.getCommandSenderName();
    }
}
