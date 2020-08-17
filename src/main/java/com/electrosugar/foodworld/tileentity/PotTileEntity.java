package com.electrosugar.foodworld.tileentity;

import com.electrosugar.foodworld.blocks.PotBlock;
import com.electrosugar.foodworld.container.PotContainer;
import com.electrosugar.foodworld.containerrecipe.abstractrecipe.AbstractBoilingRecipe;
import com.electrosugar.foodworld.init.ModTileEntityTypes;
import com.electrosugar.foodworld.tileentity.abstracttileentity.AbstractPotTileEntity;
import com.google.gson.internal.$Gson$Preconditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class PotTileEntity extends AbstractPotTileEntity implements IFluidHandler {

    //Fluid Tank inside
    public FluidTank fluidTank = new FluidTank(8000);//added
    //Container Size
    private NonNullList<ItemStack> potContainer = NonNullList.withSize(11,ItemStack.EMPTY);
    //
    protected int numPlayerUsing;
    private IItemHandlerModifiable items = createHandler();
    private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);

    public PotTileEntity(){
        super(ModTileEntityTypes.POT.get(),IRecipeType.register("smelting"));
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


    @Override
    public int getSizeInventory() {
        return 11;
    }

    @Override
    public NonNullList<ItemStack> getItems(){
        return this.potContainer;
    }

    @Override
    public void setItems(NonNullList<ItemStack> itemsIn){
        this.potContainer = itemsIn;
    }

    @Override
    public ITextComponent getDefaultName(){
        return new TranslationTextComponent("container.pot");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player){
        return new PotContainer(id,player,this);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound){
        super.write(compound);
        return compound;
    }

    @Override
    public void read(CompoundNBT compound){
        super.read(compound);
    }

    private void playSound(SoundEvent sound){
        double dx = (double)this.pos.getX() + 0.5D;
        double dy = (double)this.pos.getY() + 0.5D;
        double dz = (double)this.pos.getZ() + 0.5D;
        this.world.playSound((PlayerEntity)null,dx,dy,dz,sound, SoundCategory.BLOCKS, 0.5f,this.world.rand.nextFloat()*0.1f+0.9f);
    }

    @Override
    public boolean receiveClientEvent(int id, int type){
        if(id==1){
            this.numPlayerUsing=type;
            return true;
        }else{
            return super.receiveClientEvent(id,type);
        }
    }

    @Override
    public void openInventory(PlayerEntity player){
        if(!player.isSpectator()){
            if(this.numPlayerUsing<0){
                this.numPlayerUsing=0;
            }
            ++this.numPlayerUsing;
            this.onOpenOrClose();
        }
//        FoodWorld.LOGGER.info("HELLO FROM PREINIT");
    }

    @Override
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
            if(tileEntity instanceof PotTileEntity){
                return ((PotTileEntity)tileEntity).numPlayerUsing;
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction side) {
        if(cap== CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return itemHandler.cast();
        }
        return super.getCapability(cap,side);
    }

    private IItemHandlerModifiable createHandler(){
        return new InvWrapper(this);
    }

    @Override
    public void remove(){
        super.remove();
        if(itemHandler!=null){
            itemHandler.invalidate();
        }
    }
}
