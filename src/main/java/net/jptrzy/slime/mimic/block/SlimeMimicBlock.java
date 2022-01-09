package net.jptrzy.slime.mimic.block;

import net.jptrzy.slime.mimic.Main;
import net.jptrzy.slime.mimic.block.entity.SlimeMimicBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SlimeMimicBlock extends BlockWithEntity {

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
            blockEntity.changeToEntity(player);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        SlimeMimicBlockEntity blockEntity = (SlimeMimicBlockEntity) world.getBlockEntity(pos);
        if(blockEntity != null){
            blockEntity.changeToEntity(player);
        }
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : checkType(type, Main.SLIME_MIMIC_BLOCK_ENTITY, SlimeMimicBlockEntity::tick);

    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (random.nextInt(32) != 0) return;

        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_SILVERFISH_STEP, SoundCategory.BLOCKS, 1, 1, true);
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
}
