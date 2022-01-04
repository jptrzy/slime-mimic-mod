package net.jptrzy.slime.mimic.entity.mob;

import net.jptrzy.slime.mimic.Main;
import net.jptrzy.slime.mimic.block.entity.SlimeMimicBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.apache.http.annotation.Obsolete;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Overwrite;

public class SlimeMimicEntity extends SlimeEntity implements InventoryOwner {

    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 4;

    public SlimeMimicEntity(EntityType<? extends SlimeMimicEntity> entityType, World world) {
        super((EntityType<? extends SlimeEntity>) entityType, world);
        this.setCanPickUpLoot(true);
    }

    public static SlimeMimicEntity create(World world, BlockPos pos, SimpleInventory inventory, int size) {
        SlimeMimicEntity entity = new SlimeMimicEntity(Main.SLIME_MIMIC, world);
        entity.setPosition(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
        entity.inventory.readNbtList(inventory.toNbtList());
        entity.setSize(size, false);
        return entity;
    }

    public static final int INVENTORY_SIZE = 8;
    private final SimpleInventory inventory = new SimpleInventory(INVENTORY_SIZE);

    @Override
    @Nullable
    //TODO This is only walk around solution, it will break in the future.
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setSize(1, true);

        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addPersistentModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        return entityData;
    }

    //TODO This is only walk around solution, it will break in the future.
    @Override
    public void remove(Entity.RemovalReason reason) {
        this.setRemoved(reason);
        if (reason == RemovalReason.KILLED) {
            this.emitGameEvent(GameEvent.ENTITY_KILLED);
        }
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1D);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.DEFAULT;
    }

    @Override
    public boolean canEquip(ItemStack stack) {
        return false;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (this.canGather(itemStack)) {

            if (!inventory.canInsert(itemStack)) {
                return;
            }

            this.triggerItemPickedUpByEntityCriteria(item);
            this.sendPickup(item, itemStack.getCount());
            ItemStack itemStack2 = inventory.addStack(itemStack);
            if (itemStack2.isEmpty()) {
                item.discard();
            } else {
                itemStack.setCount(itemStack2.getCount());
            }

            if (!this.getWorld().isClient()) {
                if (itemStack.getItem() instanceof BlockItem) {
                    Block block = ((BlockItem) itemStack.getItem()).getBlock();
                    BlockState state = block.getDefaultState();
                    //Check if fullblock.
                    if (state != null && Block.isShapeFullCube(state.getCollisionShape(this.getWorld(), this.getBlockPos()))) {
                        this.changeToBlock(state);
                    }
                }
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("Inventory", this.inventory.toNbtList());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.inventory.readNbtList(nbt.getList("Inventory", 10));
    }

    @Override
    public void onDeath(DamageSource source) {
        Main.LOGGER.warn("Death");
        super.onDeath(source);

        for (int i = 0; i < INVENTORY_SIZE; i++) {
            this.dropStack(inventory.getStack(i));
        }
    }

    @Override
    protected ParticleEffect getParticles() {
        return ParticleTypes.FALLING_HONEY;
    }

    @Override
    protected void setSize(int size, boolean heal) {
        super.setSize(size, heal);

        int i = MathHelper.clamp(size, MIN_SIZE, MAX_SIZE);

        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(i * i * 2);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2f + 0.2f * (float)i);
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(i*2);

        if (heal) { this.setHealth(this.getMaxHealth()); }

        this.experiencePoints = i;
    }

    public float getScale(){
        return .6f + .1f * (float)this.getSize();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return this.getType().getDimensions().scaled(this.getScale());
    }


    public void changeToBlock(BlockState state) {
        Main.LOGGER.warn("Change To Block");

        BlockPos pos = this.getBlockPos();

        //TODO -> CHECK IF EMPTY

        this.remove(RemovalReason.DISCARDED);

        this.getWorld().setBlockState(pos, Main.SLIME_MIMIC_BLOCK.getDefaultState());
        SlimeMimicBlockEntity entity = (SlimeMimicBlockEntity) this.getWorld().getBlockEntity(pos);

        if(entity == null){
            Main.LOGGER.warn("Can't get blockEntity");
        }

        entity.setBlockState(state);
        entity.setInventory(inventory);
        entity.setSize(this.getSize());
        entity.sync();
    }
}