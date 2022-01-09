package net.jptrzy.slime.mimic.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.jptrzy.slime.mimic.Main;
import net.jptrzy.slime.mimic.entity.mob.SlimeMimicEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SlimeMimicBlockEntity extends BlockEntity {

    private BlockState blockState = Blocks.AIR.getDefaultState();
    private NbtCompound mimicNbt;
    private int ticks = 0;

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

    public static void tick(World world, BlockPos pos, BlockState state, SlimeMimicBlockEntity entity) {
        if(entity.ticks > 20){
            PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 3.0, true);
            if(playerEntity != null){
                entity.changeToEntity(playerEntity);
            }
        }else{
            entity.ticks++;
        }
    }

    public void changeToEntity(Entity target){
        if(this.getWorld().isClient){ return; }

        Main.LOGGER.debug("Change to Entity");

        this.getWorld().removeBlock(this.getPos(), false);

        SlimeMimicEntity mimic = SlimeMimicEntity.create(this.getWorld(), this.getPos(), this.mimicNbt, target);
        this.world.spawnEntity(mimic);

        mimic.playSpawnEffects();
    }
}
