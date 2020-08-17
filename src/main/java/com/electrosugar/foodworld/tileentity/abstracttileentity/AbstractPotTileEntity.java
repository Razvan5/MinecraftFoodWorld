package com.electrosugar.foodworld.tileentity.abstracttileentity;

import com.electrosugar.foodworld.blocks.PotBlock;
import com.electrosugar.foodworld.container.PotContainer;
import com.electrosugar.foodworld.containerrecipe.abstractrecipe.AbstractBoilingRecipe;
import com.electrosugar.foodworld.tileentity.PotTileEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class AbstractPotTileEntity extends LockableTileEntity implements ISidedInventory, IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity, IFluidHandler {
    private static final int[] SLOTS_UP = new int[]{0};
    private static final int[] SLOTS_DOWN = new int[]{10, 1};
    private static final int[] SLOTS_HORIZONTAL = new int[]{1, 2, 3, 4 ,5 ,6, 7, 8, 9};
    protected NonNullList<ItemStack> items = NonNullList.withSize(11, ItemStack.EMPTY);
    private int boilingLevel;
    private int recipesUsed;
    private int cookTime;
    private int cookTimeTotal;
    
    private FluidTank fluidTank = new FluidTank(8000);
    private int numPlayerUsing;
    private IItemHandlerModifiable itemHandlerModifiable = createHandler();//to add
    private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> itemHandlerModifiable);
    
    protected final IIntArray furnaceData = new IIntArray() {
        public int get(int index) {
            switch(index) {
                case 0:
                    return com.electrosugar.foodworld.tileentity.abstracttileentity.AbstractPotTileEntity.this.boilingLevel;
                case 1:
                    return com.electrosugar.foodworld.tileentity.abstracttileentity.AbstractPotTileEntity.this.recipesUsed;
                case 2:
                    return com.electrosugar.foodworld.tileentity.abstracttileentity.AbstractPotTileEntity.this.cookTime;
                case 3:
                    return com.electrosugar.foodworld.tileentity.abstracttileentity.AbstractPotTileEntity.this.cookTimeTotal;
                default:
                    return 0;
            }
        }

        public void set(int index, int value) {
            switch(index) {
                case 0:
                    com.electrosugar.foodworld.tileentity.abstracttileentity.AbstractPotTileEntity.this.boilingLevel = value;
                    break;
                case 1:
                    com.electrosugar.foodworld.tileentity.abstracttileentity.AbstractPotTileEntity.this.recipesUsed = value;
                    break;
                case 2:
                    com.electrosugar.foodworld.tileentity.abstracttileentity.AbstractPotTileEntity.this.cookTime = value;
                    break;
                case 3:
                    com.electrosugar.foodworld.tileentity.abstracttileentity.AbstractPotTileEntity.this.cookTimeTotal = value;
            }

        }

        public int size() {
            return 4;
        }
    };
    
    private final Map<ResourceLocation, Integer> field_214022_n = Maps.newHashMap();
    protected final IRecipeType<? extends AbstractBoilingRecipe> recipeType;

    public AbstractPotTileEntity(TileEntityType<PotTileEntity> tileTypeIn, IRecipeType<? extends AbstractBoilingRecipe> recipeTypeIn) {
        super(tileTypeIn);
        this.recipeType = recipeTypeIn;
    }

    @Deprecated //Forge - get burn times by calling ForgeHooks#getBurnTime(ItemStack)
    public static Map<Item, Integer> getBoilTimes() {
        Map<Item, Integer> map = Maps.newLinkedHashMap();
        addItemBurnTime(map, Items.LAVA_BUCKET, 20000);
        addItemBurnTime(map, Blocks.COAL_BLOCK, 16000);
        addItemBurnTime(map, Items.BLAZE_ROD, 2400);
        addItemBurnTime(map, Items.COAL, 1600);
        addItemBurnTime(map, Items.CHARCOAL, 1600);
        addItemTagBurnTime(map, ItemTags.LOGS, 300);
        addItemTagBurnTime(map, ItemTags.PLANKS, 300);
        addItemTagBurnTime(map, ItemTags.WOODEN_STAIRS, 300);
        addItemTagBurnTime(map, ItemTags.WOODEN_SLABS, 150);
        addItemTagBurnTime(map, ItemTags.WOODEN_TRAPDOORS, 300);
        addItemTagBurnTime(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        addItemTagBurnTime(map, net.minecraftforge.common.Tags.Items.FENCES_WOODEN, 300);
        addItemTagBurnTime(map, net.minecraftforge.common.Tags.Items.FENCE_GATES_WOODEN, 300);
        addItemBurnTime(map, Blocks.NOTE_BLOCK, 300);
        addItemBurnTime(map, Blocks.BOOKSHELF, 300);
        addItemBurnTime(map, Blocks.LECTERN, 300);
        addItemBurnTime(map, Blocks.JUKEBOX, 300);
        addItemBurnTime(map, Blocks.CHEST, 300);
        addItemBurnTime(map, Blocks.TRAPPED_CHEST, 300);
        addItemBurnTime(map, Blocks.CRAFTING_TABLE, 300);
        addItemBurnTime(map, Blocks.DAYLIGHT_DETECTOR, 300);
        addItemTagBurnTime(map, ItemTags.BANNERS, 300);
        addItemBurnTime(map, Items.BOW, 300);
        addItemBurnTime(map, Items.FISHING_ROD, 300);
        addItemBurnTime(map, Blocks.LADDER, 300);
        addItemTagBurnTime(map, ItemTags.SIGNS, 200);
        addItemBurnTime(map, Items.WOODEN_SHOVEL, 200);
        addItemBurnTime(map, Items.WOODEN_SWORD, 200);
        addItemBurnTime(map, Items.WOODEN_HOE, 200);
        addItemBurnTime(map, Items.WOODEN_AXE, 200);
        addItemBurnTime(map, Items.WOODEN_PICKAXE, 200);
        addItemTagBurnTime(map, ItemTags.WOODEN_DOORS, 200);
        addItemTagBurnTime(map, ItemTags.BOATS, 1200);
        addItemTagBurnTime(map, ItemTags.WOOL, 100);
        addItemTagBurnTime(map, ItemTags.WOODEN_BUTTONS, 100);
        addItemBurnTime(map, Items.STICK, 100);
        addItemTagBurnTime(map, ItemTags.SAPLINGS, 100);
        addItemBurnTime(map, Items.BOWL, 100);
        addItemTagBurnTime(map, ItemTags.CARPETS, 67);
        addItemBurnTime(map, Blocks.DRIED_KELP_BLOCK, 4001);
        addItemBurnTime(map, Items.CROSSBOW, 300);
        addItemBurnTime(map, Blocks.BAMBOO, 50);
        addItemBurnTime(map, Blocks.DEAD_BUSH, 100);
        addItemBurnTime(map, Blocks.SCAFFOLDING, 400);
        addItemBurnTime(map, Blocks.LOOM, 300);
        addItemBurnTime(map, Blocks.BARREL, 300);
        addItemBurnTime(map, Blocks.CARTOGRAPHY_TABLE, 300);
        addItemBurnTime(map, Blocks.FLETCHING_TABLE, 300);
        addItemBurnTime(map, Blocks.SMITHING_TABLE, 300);
        addItemBurnTime(map, Blocks.COMPOSTER, 300);
        return map;
    }

    private static void addItemTagBurnTime(Map<Item, Integer> map, Tag<Item> itemTag, int burnTimeIn) {
        for(Item item : itemTag.getAllElements()) {
            map.put(item, burnTimeIn);
        }

    }

    private static void addItemBurnTime(Map<Item, Integer> map, IItemProvider itemProvider, int burnTimeIn) {
        map.put(itemProvider.asItem(), burnTimeIn);
    }

    private boolean isBoiling() {
        return this.boilingLevel > 0;
    }

    public void read(CompoundNBT compound) {
        super.read(compound);
        this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.items);
        this.boilingLevel = compound.getInt("BoilLevel");
        this.cookTime = compound.getInt("CookTime");
        this.cookTimeTotal = compound.getInt("CookTimeTotal");
        this.recipesUsed = this.getBurnTime(this.items.get(1));
        int i = compound.getShort("RecipesUsedSize");

        for(int j = 0; j < i; ++j) {
            ResourceLocation resourcelocation = new ResourceLocation(compound.getString("RecipeLocation" + j));
            int k = compound.getInt("RecipeAmount" + j);
            this.field_214022_n.put(resourcelocation, k);
        }

    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("BoilLevel", this.boilingLevel);
        compound.putInt("CookTime", this.cookTime);
        compound.putInt("CookTimeTotal", this.cookTimeTotal);
        ItemStackHelper.saveAllItems(compound, this.items);
        compound.putShort("RecipesUsedSize", (short)this.field_214022_n.size());
        int i = 0;

        for(Entry<ResourceLocation, Integer> entry : this.field_214022_n.entrySet()) {
            compound.putString("RecipeLocation" + i, entry.getKey().toString());
            compound.putInt("RecipeAmount" + i, entry.getValue());
            ++i;
        }

        return compound;
    }

    public void tick() {
        boolean flag = this.isBoiling();
        boolean flag1 = false;
        if (this.isBoiling()) {
            --this.boilingLevel;
        }

        if (!this.world.isRemote) {
            ItemStack itemstack = this.items.get(1);
            if (this.isBoiling() || !itemstack.isEmpty() && !this.items.get(0).isEmpty()) {
                IRecipe<?> irecipe = this.world.getRecipeManager().getRecipe((IRecipeType<AbstractBoilingRecipe>)this.recipeType, this, this.world).orElse(null);
                if (!this.isBoiling() && this.canSmelt(irecipe)) {
                    this.boilingLevel = this.getBurnTime(itemstack);
                    this.recipesUsed = this.boilingLevel;
                    if (this.isBoiling()) {
                        flag1 = true;
                        if (itemstack.hasContainerItem())
                            this.items.set(1, itemstack.getContainerItem());
                        else
                        if (!itemstack.isEmpty()) {
                            Item item = itemstack.getItem();
                            itemstack.shrink(1);
                            if (itemstack.isEmpty()) {
                                this.items.set(1, itemstack.getContainerItem());
                            }
                        }
                    }
                }

                if (this.isBoiling() && this.canSmelt(irecipe)) {
                    ++this.cookTime;
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.getCookTime();
                        this.smelt(irecipe);
                        flag1 = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (!this.isBoiling() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }

            if (flag != this.isBoiling()) {
                flag1 = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, Boolean.valueOf(this.isBoiling())), 3);
            }
        }

        if (flag1) {
            this.markDirty();
        }

    }

    protected boolean canSmelt(@Nullable IRecipe<?> recipeIn) {
        if (!this.items.get(0).isEmpty() && recipeIn != null) {
            ItemStack itemstack = recipeIn.getRecipeOutput();
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = this.items.get(2);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!itemstack1.isItemEqual(itemstack)) {
                    return false;
                } else if (itemstack1.getCount() + itemstack.getCount() <= this.getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
                    return true;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
                }
            }
        } else {
            return false;
        }
    }

    private void smelt(@Nullable IRecipe<?> recipe) {
        if (recipe != null && this.canSmelt(recipe)) {
            ItemStack itemstack = this.items.get(0);
            ItemStack itemstack1 = recipe.getRecipeOutput();
            ItemStack itemstack2 = this.items.get(2);
            if (itemstack2.isEmpty()) {
                this.items.set(2, itemstack1.copy());
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(itemstack1.getCount());
            }

            if (!this.world.isRemote) {
                this.setRecipeUsed(recipe);
            }

            if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET) {
                this.items.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
        }
    }

    protected int getBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return net.minecraftforge.common.ForgeHooks.getBurnTime(fuel);
        }
    }

    protected int getCookTime() {
        return this.world.getRecipeManager().getRecipe((IRecipeType<AbstractBoilingRecipe>)this.recipeType, this, this.world).map(AbstractBoilingRecipe::getCookTime).orElse(200);
    }

    public static boolean isFluid(ItemStack stack) {
        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack) > 0;
    }

    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_DOWN;
        } else {
            return side == Direction.UP ? SLOTS_UP : SLOTS_HORIZONTAL;
        }
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side.
     */
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side.
     */
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        if (direction == Direction.DOWN && index == 10) {
            Item item = stack.getItem();
            if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return this.items.size();
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getStackInSlot(int index) {
        return this.items.get(index);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.items, index, count);
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.items, index);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = this.items.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        this.items.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (index == 0 && !flag) {
            this.cookTimeTotal = this.getCookTime();
            this.cookTime = 0;
            this.markDirty();
        }

    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    public boolean isUsableByPlayer(PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
     * guis use Slot.isItemValid
     */
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 2) {
            return false;
        } else if (index != 1) {
            return true;
        } else {
            ItemStack itemstack = this.items.get(1);
            return isFluid(stack) || stack.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET;
        }
    }

    public void clear() {
        this.items.clear();
    }

    public void setRecipeUsed(@Nullable IRecipe<?> recipe) {
        if (recipe != null) {
            this.field_214022_n.compute(recipe.getId(), (p_214004_0_, p_214004_1_) -> {
                return 1 + (p_214004_1_ == null ? 0 : p_214004_1_);
            });
        }

    }

    @Nullable
    public IRecipe<?> getRecipeUsed() {
        return null;
    }

    public void onCrafting(PlayerEntity player) {
    }

    public void func_213995_d(PlayerEntity player) {
        List<IRecipe<?>> list = Lists.newArrayList();

        for(Entry<ResourceLocation, Integer> entry : this.field_214022_n.entrySet()) {
            player.world.getRecipeManager().getRecipe(entry.getKey()).ifPresent((p_213993_3_) -> {
                list.add(p_213993_3_);
                spawnExpOrbs(player, entry.getValue(), ((AbstractCookingRecipe)p_213993_3_).getExperience());
            });
        }

        player.unlockRecipes(list);
        this.field_214022_n.clear();
    }

    private static void spawnExpOrbs(PlayerEntity player, int p_214003_1_, float experience) {
        if (experience == 0.0F) {
            p_214003_1_ = 0;
        } else if (experience < 1.0F) {
            int i = MathHelper.floor((float)p_214003_1_ * experience);
            if (i < MathHelper.ceil((float)p_214003_1_ * experience) && Math.random() < (double)((float)p_214003_1_ * experience - (float)i)) {
                ++i;
            }

            p_214003_1_ = i;
        }

        while(p_214003_1_ > 0) {
            int j = ExperienceOrbEntity.getXPSplit(p_214003_1_);
            p_214003_1_ -= j;
            player.world.addEntity(new ExperienceOrbEntity(player.world, player.getPosX(), player.getPosY() + 0.5D, player.getPosZ() + 0.5D, j));
        }

    }

    public void fillStackedContents(RecipeItemHelper helper) {
        for(ItemStack itemstack : this.items) {
            helper.accountStack(itemstack);
        }

    }

    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (!this.removed && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == Direction.UP)
                return handlers[0].cast();
            else if (facing == Direction.DOWN)
                return handlers[1].cast();
            else
                return handlers[2].cast();
        }
        return super.getCapability(capability, facing);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void remove() {
        super.remove();
        for (int x = 0; x < handlers.length; x++)
            handlers[x].invalidate();
    }

//added before

    public NonNullList<ItemStack> getItems(){
        return this.items;
    }

    public void setItems(NonNullList<ItemStack> itemsIn){
        this.items = itemsIn;
    }

    public ITextComponent getDefaultName(){
        return new TranslationTextComponent("container.pot");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player){
        return new PotContainer(id,player, (PotTileEntity) this);
    }

    //important dont delete!
//    @Override
//    public CompoundNBT write(CompoundNBT compound){
//        super.write(compound);
//        if(!this.checkLootAndWrite(compound)){
//            ItemStackHelper.saveAllItems(compound,this.potContainer);
//        }
//        return compound;
//    }
//
//    @Override
//    public void read(CompoundNBT compound){
//        super.read(compound);
//        this.items = NonNullList.withSize(this.getSizeInventory(),ItemStack.EMPTY);
//        if(!this.checkLootAndRead(compound)){
//            ItemStackHelper.loadAllItems(compound,this.items);
//        }
//
//    }

    private void playSound(SoundEvent sound){
        double dx = (double)this.pos.getX() + 0.5D;
        double dy = (double)this.pos.getY() + 0.5D;
        double dz = (double)this.pos.getZ() + 0.5D;
        this.world.playSound((PlayerEntity)null,dx,dy,dz,sound, SoundCategory.BLOCKS, 0.5f,this.world.rand.nextFloat()*0.1f+0.9f);
    }

    public boolean receiveClientEvent(int id, int type){
        if(id==1){
            this.numPlayerUsing=type;
            return true;
        }else{
            return super.receiveClientEvent(id,type);
        }
    }

    public void openInventory(PlayerEntity player){
        if(!player.isSpectator()){
            if(this.numPlayerUsing<0){
                this.numPlayerUsing=0;
            }
            ++this.numPlayerUsing;
            this.onOpenOrClose();
        }
    }

    public void closeInventory(PlayerEntity player){
        if(!player.isSpectator()){
            --this.numPlayerUsing;
            this.onOpenOrClose();
        }
    }

    protected void onOpenOrClose(){
        Block block = this.getBlockState().getBlock();
        if(block instanceof PotBlock){
            assert this.world != null;
            this.world.addBlockEvent(this.pos,block,1,this.numPlayerUsing);
            this.world.notifyNeighborsOfStateChange(this.pos,block);
        }
    }

    public static int getPlayerUsing(IBlockReader reader, BlockPos pos){
        BlockState blockState = reader.getBlockState(pos);
        if(blockState.hasTileEntity()){
            TileEntity tileEntity = reader.getTileEntity(pos);
            if(tileEntity instanceof AbstractPotTileEntity){
                return ((AbstractPotTileEntity)tileEntity).numPlayerUsing;
            }
        }
        return 0;
    }

    public static void swapContents(PotTileEntity tileEntity, PotTileEntity otherTileEntity){
        NonNullList<ItemStack> list =tileEntity.getItems();
        tileEntity.setItems(otherTileEntity.getItems());
        otherTileEntity.setItems(list);
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if(this.itemHandler !=null){
            this.itemHandler.invalidate();
            this.itemHandler=null;
        }
    }


    private IItemHandlerModifiable createHandler(){
        return new InvWrapper(this);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.fluidTank.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.fluidTank.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return this.fluidTank.fill(resource,action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return this.fluidTank.drain(resource,action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return this.fluidTank.drain(maxDrain,action);
    }

}
