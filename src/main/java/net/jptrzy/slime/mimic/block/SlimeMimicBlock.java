package net.jptrzy.slime.mimic.block;

import net.jptrzy.slime.mimic.block.entity.SlimeMimicBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimeMimicBlock extends Block implements BlockEntityProvider {

    public SlimeMimicBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SlimeMimicBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        SlimeMimicBlockEntity blockEntity = (SlimeMimicBlockEntity) world.getBlockEntity(pos);
        if(blockEntity != null){
            blockEntity.changeToEntity();
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        SlimeMimicBlockEntity blockEntity = (SlimeMimicBlockEntity) world.getBlockEntity(pos);
        if(blockEntity != null){
            blockEntity.changeToEntity();
        }
    }

//    @Override
//    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
//        SlimeMimicBlockEntity blockEntity = (SlimeMimicBlockEntity) world.getBlockEntity(state.getBlock().getPo);
//        if(blockEntity != null){
//            blockEntity.changeToEntity();
//        }
//    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.INVISIBLE;
    }

//    @Override
//    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//        if (!world.isClient) {
//            player.sendMessage(new LiteralText("Hello, world!"), false);
//        }
//
//        return ActionResult.SUCCESS;
//    }
}
