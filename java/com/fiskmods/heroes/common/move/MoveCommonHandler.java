//package com.fiskmods.heroes.common.move; // TODO: 1.4 Combat
//
//import com.fiskmods.heroes.SHConstants;
//import com.fiskmods.heroes.common.data.SHData;
//import com.fiskmods.heroes.common.hero.Hero;
//import com.fiskmods.heroes.common.move.Move.MouseAction;
//import com.fiskmods.heroes.common.move.Move.MoveSensitivity;
//import com.fiskmods.heroes.common.network.SHNetworkManager;
//import com.fiskmods.heroes.helper.SHHelper;
//import com.google.common.collect.ImmutableMap;
//
//import cpw.mods.fml.common.FMLCommonHandler;
//import cpw.mods.fml.common.eventhandler.Event.Result;
//import cpw.mods.fml.common.eventhandler.SubscribeEvent;
//import cpw.mods.fml.common.gameevent.TickEvent;
//import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
//import net.minecraft.crash.CrashReport;
//import net.minecraft.crash.CrashReportCategory;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.entity.player.InventoryPlayer;
//import net.minecraft.init.Blocks;
//import net.minecraft.init.Items;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.stats.AchievementList;
//import net.minecraft.util.ReportedException;
//import net.minecraftforge.event.entity.player.AttackEntityEvent;
//import net.minecraftforge.event.entity.player.EntityInteractEvent;
//import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
//import net.minecraftforge.event.entity.player.PlayerEvent;
//import net.minecraftforge.event.entity.player.PlayerInteractEvent;
//import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
//import net.minecraftforge.event.world.BlockEvent;
//
//public enum MoveCommonHandler
//{
//    INSTANCE;
//
//    @SubscribeEvent
//    public void onEntityItemPickup(EntityItemPickupEvent event)
//    {
//        EntityPlayer player = event.entityPlayer;
//        MoveEntry e = getMove(player);
//
//        if (e != null)
//        {
//            ItemStack stack = event.item.getEntityItem();
//            int i = stack.stackSize;
//
//            event.setCanceled(true);
//
//            if (event.item.delayBeforeCanPickup <= 0 && (event.item.func_145798_i() == null || event.item.lifespan - event.item.age <= 200 || event.item.func_145798_i().equals(player.getCommandSenderName())))
//            {
//                if (event.getResult() == Result.ALLOW || i <= 0 || pickupItem(player.inventory, stack, e))
//                {
//                    if (stack.getItem() == Item.getItemFromBlock(Blocks.log))
//                    {
//                        player.triggerAchievement(AchievementList.mineWood);
//                    }
//
//                    if (stack.getItem() == Item.getItemFromBlock(Blocks.log2))
//                    {
//                        player.triggerAchievement(AchievementList.mineWood);
//                    }
//
//                    if (stack.getItem() == Items.leather)
//                    {
//                        player.triggerAchievement(AchievementList.killCow);
//                    }
//
//                    if (stack.getItem() == Items.diamond)
//                    {
//                        player.triggerAchievement(AchievementList.diamonds);
//                    }
//
//                    if (stack.getItem() == Items.blaze_rod)
//                    {
//                        player.triggerAchievement(AchievementList.blazeRod);
//                    }
//
//                    if (stack.getItem() == Items.diamond && event.item.func_145800_j() != null)
//                    {
//                        EntityPlayer thrower = event.item.worldObj.getPlayerEntityByName(event.item.func_145800_j());
//
//                        if (thrower != null && thrower != player)
//                        {
//                            thrower.triggerAchievement(AchievementList.field_150966_x);
//                        }
//                    }
//
//                    FMLCommonHandler.instance().firePlayerItemPickupEvent(player, event.item);
//
//                    event.item.worldObj.playSoundAtEntity(player, "random.pop", 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
//                    player.onItemPickup(event.item, i);
//
//                    if (stack.stackSize <= 0)
//                    {
//                        event.item.setDead();
//                    }
//                }
//            }
//        }
//    }
//
//    private boolean pickupItem(InventoryPlayer inventory, ItemStack pickup, MoveEntry e)
//    {
//        if (pickup != null && pickup.stackSize != 0 && pickup.getItem() != null)
//        {
//            if (e.move.canPickupItem(inventory, pickup, e.getParent()))
//            {
//                if (inventory.getCurrentItem() == null)
//                {
//                    inventory.mainInventory[inventory.currentItem] = pickup.copy();
//                    inventory.mainInventory[inventory.currentItem].animationsToGo = 5;
//                    pickup.stackSize = 0;
//                    return true;
//                }
//
//                return inventory.addItemStackToInventory(pickup);
//            }
//
//            try
//            {
//                if (pickup.isItemDamaged())
//                {
//                    int slot = getFirstEmptyStack(inventory, null);
//
//                    if (slot >= 0)
//                    {
//                        inventory.mainInventory[slot] = ItemStack.copyItemStack(pickup);
//                        inventory.mainInventory[slot].animationsToGo = 5;
//                        pickup.stackSize = 0;
//                        return true;
//                    }
//                    else if (inventory.player.capabilities.isCreativeMode)
//                    {
//                        pickup.stackSize = 0;
//                        return true;
//                    }
//                    else
//                    {
//                        return false;
//                    }
//                }
//                else
//                {
//                    int count;
//
//                    do
//                    {
//                        count = pickup.stackSize;
//                        pickup.stackSize = storePartialItemStack(inventory, pickup);
//                    }
//                    while (pickup.stackSize > 0 && pickup.stackSize < count);
//
//                    if (pickup.stackSize == count && inventory.player.capabilities.isCreativeMode)
//                    {
//                        pickup.stackSize = 0;
//                        return true;
//                    }
//                    else
//                    {
//                        return pickup.stackSize < count;
//                    }
//                }
//            }
//            catch (Throwable t)
//            {
//                CrashReport report = CrashReport.makeCrashReport(t, "Adding item to inventory");
//                CrashReportCategory category = report.makeCategory("Item being added");
//
//                category.addCrashSection("Item ID", Item.getIdFromItem(pickup.getItem()));
//                category.addCrashSection("Item data", pickup.getItemDamage());
//                category.addCrashSectionCallable("Item name", () -> pickup.getDisplayName());
//
//                throw new ReportedException(report);
//            }
//        }
//        else
//        {
//            return false;
//        }
//    }
//
//    private int storePartialItemStack(InventoryPlayer inventory, ItemStack pickup)
//    {
//        Item item = pickup.getItem();
//        int count = pickup.stackSize;
//
//        if (pickup.getMaxStackSize() == 1)
//        {
//            int slot = getFirstEmptyStack(inventory, null);
//
//            if (slot < 0)
//            {
//                return count;
//            }
//            else
//            {
//                if (inventory.mainInventory[slot] == null)
//                {
//                    inventory.mainInventory[slot] = ItemStack.copyItemStack(pickup);
//                }
//
//                return 0;
//            }
//        }
//        else
//        {
//            int slot = getFirstEmptyStack(inventory, pickup);
//
//            if (slot < 0)
//            {
//                slot = getFirstEmptyStack(inventory, null);
//            }
//
//            if (slot < 0)
//            {
//                return count;
//            }
//            else
//            {
//                ItemStack stack = inventory.mainInventory[slot];
//
//                if (stack == null)
//                {
//                    stack = new ItemStack(item, 0, pickup.getItemDamage());
//
//                    if (pickup.hasTagCompound())
//                    {
//                        stack.setTagCompound((NBTTagCompound) pickup.getTagCompound().copy());
//                    }
//                }
//
//                int newCount = count;
//
//                if (count > stack.getMaxStackSize() - stack.stackSize)
//                {
//                    newCount = stack.getMaxStackSize() - stack.stackSize;
//                }
//
//                if (newCount > inventory.getInventoryStackLimit() - stack.stackSize)
//                {
//                    newCount = inventory.getInventoryStackLimit() - stack.stackSize;
//                }
//
//                inventory.mainInventory[slot] = stack;
//
//                if (newCount == 0)
//                {
//                    return count;
//                }
//                else
//                {
//                    count -= newCount;
//                    stack.stackSize += newCount;
//                    stack.animationsToGo = 5;
//                    return count;
//                }
//            }
//        }
//    }
//
//    private int getFirstEmptyStack(InventoryPlayer inventory, ItemStack pickup)
//    {
//        for (int i = 9; i < inventory.mainInventory.length; ++i)
//        {
//            ItemStack stack = inventory.mainInventory[i];
//
//            if (pickup == null ? stack == null : canMergeStacks(inventory, stack, pickup))
//            {
//                return i;
//            }
//        }
//
//        return -1;
//    }
//
//    private boolean canMergeStacks(InventoryPlayer inventory, ItemStack stack, ItemStack pickup)
//    {
//        return stack.getItem() == pickup.getItem() && stack.isStackable() && stack.stackSize < stack.getMaxStackSize() && stack.stackSize < inventory.getInventoryStackLimit() && (!stack.getHasSubtypes() || stack.getItemDamage() == pickup.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, pickup);
//    }
//
//    @SubscribeEvent
//    public void onPlayerTick(PlayerTickEvent event)
//    {
//        EntityPlayer player = event.player;
//
//        if (event.phase == TickEvent.Phase.END)
//        {
//            float focusTime = SHData.FOCUS_TIME.get(player);
//
//            if (isFocusing(player))
//            {
//                if (focusTime <= SHConstants.TICKS_FOCUS_START)
//                {
//                    SHData.FOCUS_TIME.setWithoutNotify(player, ++focusTime);
//                }
//                else
//                {
//                    if (focusTime < SHConstants.TICKS_FOCUS_HOLD)
//                    {
//                        SHData.FOCUS_TIME.setWithoutNotify(player, focusTime = SHConstants.TICKS_FOCUS_HOLD);
//                    }
//
//                    if (SHData.FOCUS.incrWithoutNotify(player, getFocusRate(player)))
//                    {
//                        SHData.FOCUS.clampWithoutNotify(player, 0F, getMaxFocus(player));
//                    }
//                }
//            }
//            else if (focusTime > 0)
//            {
//                SHData.FOCUS_TIME.setWithoutNotify(player, --focusTime);
//
//                if (focusTime <= 0)
//                {
//                    SHData.FOCUS.setWithoutNotify(player, 0F);
//                }
//                else if (SHData.FOCUS.incrWithoutNotify(player, -getFocusRate(player) / 10))
//                {
//                    SHData.FOCUS.clampWithoutNotify(player, 0F, getMaxFocus(player));
//                }
//            }
//        }
//    }
//
//    @SubscribeEvent
//    public void onPlayerInteract(PlayerInteractEvent event)
//    {
//        MoveEntry e = getMove(event.entityPlayer);
//
//        if (e != null && e.move.action.test(MouseAction.get(event.action), event.action == Action.RIGHT_CLICK_AIR ? MoveSensitivity.AIR : MoveSensitivity.BLOCK))
//        {
//            event.setCanceled(true);
//        }
//    }
//
//    @SubscribeEvent
//    public void onEntityInteract(EntityInteractEvent event)
//    {
//        MoveEntry e = getMove(event.entityPlayer);
//
//        if (e != null && e.move.action.test(MouseAction.RIGHT_CLICK, MoveSensitivity.ENTITY))
//        {
//            event.setCanceled(true);
//        }
//    }
//
//    @SubscribeEvent
//    public void onAttackEntity(AttackEntityEvent event)
//    {
//        MoveEntry e = getMove(event.entityPlayer);
//
//        if (e != null && e.move.action.test(MouseAction.LEFT_CLICK, MoveSensitivity.ENTITY))
//        {
//            event.setCanceled(true);
//        }
//    }
//
//    @SubscribeEvent
//    public void onPlayerBreakBlock(PlayerEvent.BreakSpeed event)
//    {
//        MoveEntry e = getMove(event.entityPlayer);
//
//        if (e != null && e.move.action.test(MouseAction.LEFT_CLICK, MoveSensitivity.BLOCK))
//        {
//            event.setCanceled(true);
//        }
//    }
//
//    @SubscribeEvent
//    public void onBreakBlock(BlockEvent.BreakEvent event)
//    {
//        MoveEntry e = getMove(event.getPlayer());
//
//        if (e != null && e.move.action.test(MouseAction.LEFT_CLICK, MoveSensitivity.BLOCK))
//        {
//            event.setCanceled(true);
//        }
//    }
//
////    @SubscribeEvent
////    public void onAttackEntity(AttackEntityEvent event)
////    {
////        EntityPlayer player = event.entityPlayer;
////        Hero hero = SHHelper.getHero(player);
////        MoveEntry e = getMove(player, hero);
////
////        if (e != null)
////        {
////            float focus = SHData.FOCUS.get(player);
////            ImmutableMap<String, Number> modifiers = e.getParent().getModifiers(e.move, focus);
////
////            if (modifiers != null && e.move.onActivated(player, hero, modifiers, focus) && (focus == 0 || SHData.FOCUS.setWithoutNotify(player, 0F)) && context.side.isServer())
////            {
////                SHNetworkManager.wrapper.sendToDimension(this, player.dimension);
////            }
////        }
////    }
//
//    public static boolean hasMoveSet(EntityLivingBase entity, Hero hero)
//    {
//        return hero != null && hero.getMoveSet() != null;
//    }
//
//    public static boolean hasMoveSet(EntityLivingBase entity)
//    {
//        return hasMoveSet(entity, SHHelper.getHero(entity));
//    }
//
//    public static MoveEntry getMove(EntityLivingBase entity, Hero hero)
//    {
//        if (hasMoveSet(entity, hero) && SHData.COMBAT_MODE.get(entity))
//        {
//            return hero.getMoveSet().getMove(SHData.COMBAT_INDEX.get(entity));
//        }
//
//        return null;
//    }
//
//    public static MoveEntry getMove(EntityLivingBase entity)
//    {
//        return getMove(entity, SHHelper.getHero(entity));
//    }
//
//    public static boolean isFocusing(Entity entity, Hero hero)
//    {
//        return entity.isSneaking() && entity instanceof EntityLivingBase && hasMoveSet((EntityLivingBase) entity, hero);
//    }
//
//    public static boolean isFocusing(Entity entity)
//    {
//        return entity.isSneaking() && SHData.COMBAT_MODE.get(entity) && entity instanceof EntityLivingBase && hasMoveSet((EntityLivingBase) entity);
//    }
//
//    public static float getMaxFocus(EntityPlayer player)
//    {
//        return 1.5F;
//    }
//
//    public static float getFocusRate(EntityPlayer player)
//    {
//        return 1F / 20;
//    }
//}
