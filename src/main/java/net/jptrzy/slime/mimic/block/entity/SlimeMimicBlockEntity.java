package net.jptrzy.slime.mimic.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.jptrzy.slime.mimic.Main;
import net.jptrzy.slime.mimic.entity.mob.SlimeMimicEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SlimeMimicBlockEntity extends BlockEntity {

    private BlockState blockState = Blocks.AIR.getDefaultState();
    private NbtCompound mimicNbt;

    public SlimeMimicBlockEntity(BlockPos pos, BlockState state) {
        super(Main.SLIME_MIMIC_BLOCK_ENTITY, pos, state);
    }

    public BlockState getBlockState(){ return blockState; }

    public void setBlockState(BlockState state){
        this.blockState = state;
    }
    public void setMimicNbt(NbtCompound tag){ this.mimicNbt = tag; }

    @Override
    public void writeNbt(NbtCompound tag) {
        tag.put("BlockItem", this.blockState.getBlock().asItem().getDefaultStack().writeNbt(new NbtCompound()));
        tag.put("Mimic", this.mimicNbt);
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        this.setBlockState(((BlockItem) ItemStack.fromNbt(tag.getCompound("BlockItem")).getItem()).getBlock().getDefaultState());
        this.mimicNbt = tag.getCompound("Mimic");
        super.readNbt(tag);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = super.toInitialChunkDataNbt();
        writeNbt(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void sync() {
        this.markDirty();

        if(world != null && !world.isClient())
            world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public void changeToEntity(){
        if(this.getWorld().isClient){ return; }

        Main.LOGGER.warn("Change to Entity");

        this.getWorld().removeBlock(this.getPos(), false);

        this.world.spawnEntity(SlimeMimicEntity.create(this.getWorld(), this.getPos(), this.mimicNbt));
    }
}
