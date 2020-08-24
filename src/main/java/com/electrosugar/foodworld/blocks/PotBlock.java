package com.electrosugar.foodworld.blocks;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.tileentity.PotTileEntity;
import com.electrosugar.foodworld.util.RegistryHandler;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.stream.Stream;


public class PotBlock extends ContainerBlock {

    private static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.makeCuboidShape(1, 8, 6, 2, 9, 7),
            Block.makeCuboidShape(0, 8, 6, 1, 9, 10),
            Block.makeCuboidShape(1, 8, 9, 2, 9, 10),
            Block.makeCuboidShape(14, 8, 9, 15, 9, 10),
            Block.makeCuboidShape(15, 8, 6, 16, 9, 10),
            Block.makeCuboidShape(14, 8, 6, 15, 9, 7),
            Block.makeCuboidShape(10, 0, 6, 11, 1.5, 7),
            Block.makeCuboidShape(10, 0, 9, 11, 1, 10),
            Block.makeCuboidShape(10, 0, 8, 11, 1.5, 9),
            Block.makeCuboidShape(10, 0, 7, 11, 1, 8),
            Block.makeCuboidShape(5, 0, 9, 6, 1.5, 10),
            Block.makeCuboidShape(5, 0, 6, 6, 1, 7),
            Block.makeCuboidShape(5, 0, 7, 6, 1.5, 8),
            Block.makeCuboidShape(5, 0, 8, 6, 1, 9),
            Block.makeCuboidShape(7, 0, 5, 8, 1, 6),
            Block.makeCuboidShape(6, 0, 5, 7, 1.5, 6),
            Block.makeCuboidShape(9, 0, 5, 10, 1, 6),
            Block.makeCuboidShape(8, 0, 5, 9, 1.5, 6),
            Block.makeCuboidShape(8, 0, 10, 9, 1, 11),
            Block.makeCuboidShape(9, 0, 10, 10, 1.5, 11),
            Block.makeCuboidShape(6, 0, 10, 7, 1, 11),
            Block.makeCuboidShape(7, 0, 10, 8, 1.5, 11),
            Block.makeCuboidShape(2, 3, 3.0000000000000036, 3, 13, 13),
            Block.makeCuboidShape(3, 2, 3, 13, 3, 13),
            Block.makeCuboidShape(3, 3, 13, 13, 13, 14),
            Block.makeCuboidShape(3, 3, 2.0000000000000036, 13, 13, 3.0000000000000036),
            Block.makeCuboidShape(13, 3, 3.0000000000000036, 14, 13, 13),
            Block.makeCuboidShape(6, 0, 6, 10, 0.5, 10),
            Block.makeCuboidShape(4, 0, 11, 5, 2, 12),
            Block.makeCuboidShape(4, 0, 4, 5, 2, 5),
            Block.makeCuboidShape(11, 0, 11, 12, 2, 12),
            Block.makeCuboidShape(11, 0, 4, 12, 2, 5)
    ).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.makeCuboidShape(9, 8, 1, 10, 9, 2),
            Block.makeCuboidShape(6, 8, 0, 10, 9, 1),
            Block.makeCuboidShape(6, 8, 1, 7, 9, 2),
            Block.makeCuboidShape(6, 8, 14, 7, 9, 15),
            Block.makeCuboidShape(6, 8, 15, 10, 9, 16),
            Block.makeCuboidShape(9, 8, 14, 10, 9, 15),
            Block.makeCuboidShape(9, 0, 10, 10, 1.5, 11),
            Block.makeCuboidShape(6, 0, 10, 7, 1, 11),
            Block.makeCuboidShape(7, 0, 10, 8, 1.5, 11),
            Block.makeCuboidShape(8, 0, 10, 9, 1, 11),
            Block.makeCuboidShape(6, 0, 5, 7, 1.5, 6),
            Block.makeCuboidShape(9, 0, 5, 10, 1, 6),
            Block.makeCuboidShape(8, 0, 5, 9, 1.5, 6),
            Block.makeCuboidShape(7, 0, 5, 8, 1, 6),
            Block.makeCuboidShape(10, 0, 7, 11, 1, 8),
            Block.makeCuboidShape(10, 0, 6, 11, 1.5, 7),
            Block.makeCuboidShape(10, 0, 9, 11, 1, 10),
            Block.makeCuboidShape(10, 0, 8, 11, 1.5, 9),
            Block.makeCuboidShape(5, 0, 8, 6, 1, 9),
            Block.makeCuboidShape(5, 0, 9, 6, 1.5, 10),
            Block.makeCuboidShape(5, 0, 6, 6, 1, 7),
            Block.makeCuboidShape(5, 0, 7, 6, 1.5, 8),
            Block.makeCuboidShape(3, 3, 2, 12.999999999999996, 13, 3),
            Block.makeCuboidShape(3, 2, 3, 13, 3, 13),
            Block.makeCuboidShape(1.9999999999999991, 3, 3, 2.999999999999999, 13, 13),
            Block.makeCuboidShape(12.999999999999996, 3, 3, 13.999999999999996, 13, 13),
            Block.makeCuboidShape(3, 3, 13, 12.999999999999996, 13, 14),
            Block.makeCuboidShape(6, 0, 6, 10, 0.5, 10),
            Block.makeCuboidShape(4, 0, 4, 5, 2, 5),
            Block.makeCuboidShape(11, 0, 4, 12, 2, 5),
            Block.makeCuboidShape(4, 0, 11, 5, 2, 12),
            Block.makeCuboidShape(11, 0, 11, 12, 2, 12)
    ).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.makeCuboidShape(1, 8, 6, 2, 9, 7),
            Block.makeCuboidShape(0, 8, 6, 1, 9, 10),
            Block.makeCuboidShape(1, 8, 9, 2, 9, 10),
            Block.makeCuboidShape(14, 8, 9, 15, 9, 10),
            Block.makeCuboidShape(15, 8, 6, 16, 9, 10),
            Block.makeCuboidShape(14, 8, 6, 15, 9, 7),
            Block.makeCuboidShape(10, 0, 6, 11, 1.5, 7),
            Block.makeCuboidShape(10, 0, 9, 11, 1, 10),
            Block.makeCuboidShape(10, 0, 8, 11, 1.5, 9),
            Block.makeCuboidShape(10, 0, 7, 11, 1, 8),
            Block.makeCuboidShape(5, 0, 9, 6, 1.5, 10),
            Block.makeCuboidShape(5, 0, 6, 6, 1, 7),
            Block.makeCuboidShape(5, 0, 7, 6, 1.5, 8),
            Block.makeCuboidShape(5, 0, 8, 6, 1, 9),
            Block.makeCuboidShape(7, 0, 5, 8, 1, 6),
            Block.makeCuboidShape(6, 0, 5, 7, 1.5, 6),
            Block.makeCuboidShape(9, 0, 5, 10, 1, 6),
            Block.makeCuboidShape(8, 0, 5, 9, 1.5, 6),
            Block.makeCuboidShape(8, 0, 10, 9, 1, 11),
            Block.makeCuboidShape(9, 0, 10, 10, 1.5, 11),
            Block.makeCuboidShape(6, 0, 10, 7, 1, 11),
            Block.makeCuboidShape(7, 0, 10, 8, 1.5, 11),
            Block.makeCuboidShape(2, 3, 3.0000000000000036, 3, 13, 13),
            Block.makeCuboidShape(3, 2, 3, 13, 3, 13),
            Block.makeCuboidShape(3, 3, 13, 13, 13, 14),
            Block.makeCuboidShape(3, 3, 2.0000000000000036, 13, 13, 3.0000000000000036),
            Block.makeCuboidShape(13, 3, 3.0000000000000036, 14, 13, 13),
            Block.makeCuboidShape(6, 0, 6, 10, 0.5, 10),
            Block.makeCuboidShape(4, 0, 11, 5, 2, 12),
            Block.makeCuboidShape(4, 0, 4, 5, 2, 5),
            Block.makeCuboidShape(11, 0, 11, 12, 2, 12),
            Block.makeCuboidShape(11, 0, 4, 12, 2, 5)
    ).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.makeCuboidShape(9, 8, 1, 10, 9, 2),
            Block.makeCuboidShape(6, 8, 0, 10, 9, 1),
            Block.makeCuboidShape(6, 8, 1, 7, 9, 2),
            Block.makeCuboidShape(6, 8, 14, 7, 9, 15),
            Block.makeCuboidShape(6, 8, 15, 10, 9, 16),
            Block.makeCuboidShape(9, 8, 14, 10, 9, 15),
            Block.makeCuboidShape(9, 0, 10, 10, 1.5, 11),
            Block.makeCuboidShape(6, 0, 10, 7, 1, 11),
            Block.makeCuboidShape(7, 0, 10, 8, 1.5, 11),
            Block.makeCuboidShape(8, 0, 10, 9, 1, 11),
            Block.makeCuboidShape(6, 0, 5, 7, 1.5, 6),
            Block.makeCuboidShape(9, 0, 5, 10, 1, 6),
            Block.makeCuboidShape(8, 0, 5, 9, 1.5, 6),
            Block.makeCuboidShape(7, 0, 5, 8, 1, 6),
            Block.makeCuboidShape(10, 0, 7, 11, 1, 8),
            Block.makeCuboidShape(10, 0, 6, 11, 1.5, 7),
            Block.makeCuboidShape(10, 0, 9, 11, 1, 10),
            Block.makeCuboidShape(10, 0, 8, 11, 1.5, 9),
            Block.makeCuboidShape(5, 0, 8, 6, 1, 9),
            Block.makeCuboidShape(5, 0, 9, 6, 1.5, 10),
            Block.makeCuboidShape(5, 0, 6, 6, 1, 7),
            Block.makeCuboidShape(5, 0, 7, 6, 1.5, 8),
            Block.makeCuboidShape(3, 3, 2, 12.999999999999996, 13, 3),
            Block.makeCuboidShape(3, 2, 3, 13, 3, 13),
            Block.makeCuboidShape(1.9999999999999991, 3, 3, 2.999999999999999, 13, 13),
            Block.makeCuboidShape(12.999999999999996, 3, 3, 13.999999999999996, 13, 13),
            Block.makeCuboidShape(3, 3, 13, 12.999999999999996, 13, 14),
            Block.makeCuboidShape(6, 0, 6, 10, 0.5, 10),
            Block.makeCuboidShape(4, 0, 4, 5, 2, 5),
            Block.makeCuboidShape(11, 0, 4, 12, 2, 5),
            Block.makeCuboidShape(4, 0, 11, 5, 2, 12),
            Block.makeCuboidShape(11, 0, 11, 12, 2, 12)
    ).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch(state.get(FACING)){
            case NORTH:
                return SHAPE_N;
            case EAST:
                return SHAPE_E;
            case SOUTH:
                return SHAPE_S;
            case WEST:
                return SHAPE_W;
            default:
                return SHAPE_N;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING,rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 0.6f;
    }

    public PotBlock(){
        super(Block.Properties.create(Material.IRON)
                .hardnessAndResistance(3.5f,4.5f)
                .sound(SoundType.METAL)
                .harvestLevel(0)
                .harvestTool(ToolType.PICKAXE)
        );
    }

    @Override
    public boolean hasTileEntity(BlockState state){
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world){
        return createNewTileEntity(world);
    }
    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new PotTileEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (worldIn.isRemote) return ActionResultType.SUCCESS; // on client side, don't do anything

        INamedContainerProvider namedContainerProvider = this.getContainer(state, worldIn, pos);
        if (namedContainerProvider != null) {
            if (!(player instanceof ServerPlayerEntity)) return ActionResultType.FAIL;  // should always be true, but just in case...
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, (packetBuffer)->{});
            // (packetBuffer)->{} is just a do-nothing because we have no extra data to send
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getTileEntity(blockPos);
            if (tileentity instanceof PotTileEntity) {
                PotTileEntity potTileEntity = (PotTileEntity)tileentity;
                potTileEntity.dropAllContents(world, blockPos);
            }
//      worldIn.updateComparatorOutputLevel(pos, this);  if the inventory is used to set redstone power for comparators
            super.onReplaced(state, world, blockPos, newState, isMoving);  // call it last, because it removes the TileEntity
        }
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return false;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState iBlockState) {
        return BlockRenderType.MODEL;
    }

}
