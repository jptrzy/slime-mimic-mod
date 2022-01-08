package net.jptrzy.slime.mimic.mixin;

import net.jptrzy.slime.mimic.Main;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {

    public LootableContainerBlockEntityAccessor getThis(){
        return ((LootableContainerBlockEntityAccessor) (LootableContainerBlockEntity) (Object) this);
    }

    @Inject(at = @At("HEAD"), method = "checkLootInteraction")
    private void checkLootInteraction(@Nullable PlayerEntity player, CallbackInfo ci) {
        if (player != null && getThis().getLootTableId() != null && !getThis().getWorld().isClient()) {
            getThis().getWorld().removeBlock(getThis().getPos(), false);
        }
    }
}
