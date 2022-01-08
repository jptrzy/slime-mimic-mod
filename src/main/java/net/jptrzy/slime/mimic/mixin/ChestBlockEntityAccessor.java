package net.jptrzy.slime.mimic.mixin;

import net.minecraft.block.entity.ChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChestBlockEntity.class)
public interface ChestBlockEntityAccessor extends LootableContainerBlockEntityAccessor {

}
