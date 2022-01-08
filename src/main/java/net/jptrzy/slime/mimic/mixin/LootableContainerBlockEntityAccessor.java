package net.jptrzy.slime.mimic.mixin;

import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootableContainerBlockEntity.class)
public interface LootableContainerBlockEntityAccessor extends BlockEntityAccessor  {
    @Accessor
    Identifier getLootTableId();
}
