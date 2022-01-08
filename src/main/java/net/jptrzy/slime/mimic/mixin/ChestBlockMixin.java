package net.jptrzy.slime.mimic.mixin;

import net.jptrzy.slime.mimic.Main;
import net.jptrzy.slime.mimic.entity.mob.SlimeMimicEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ChestBlock.class)
public class ChestBlockMixin {

    @Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if(world.isClient){ return; }

        ChestBlockEntityAccessor entity = ((ChestBlockEntityAccessor) world.getBlockEntity(pos));
        if (entity != null && entity.getLootTableId() != null && new Random().nextInt(10) == 1) {
            world.removeBlockEntity(pos);
            world.removeBlock(pos, false);

            SlimeMimicEntity mimic = SlimeMimicEntity.create(world, pos, null, player);
            mimic.initialize((ServerWorldAccess) world, world.getLocalDifficulty(pos), SpawnReason.CONVERSION, null, null);
            world.spawnEntity(mimic);
            mimic.playSpawnEffects();

            cir.setReturnValue(ActionResult.SUCCESS);
        }

    }
}
