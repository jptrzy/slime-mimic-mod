package net.jptrzy.slime.mimic.entity.mob;

import net.jptrzy.slime.mimic.Main;
import net.jptrzy.slime.mimic.block.entity.SlimeMimicBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
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
    public static final int INVENTORY_SIZE = 8;
    public static final Vec3i CELLS[] = new Vec3i[]{
            new Vec3i(0,0,0),
            new Vec3i(1,0,0),
            new Vec3i(-1,0,0),
            new Vec3i(0,1,0),
            new Vec3i(0,-1,0),
            new Vec3i(0,0,1),
            new Vec3i(0,0,-1)
    };

    public int inventory_usage = 0;
    private final SimpleInventory inventory = new SimpleInventory(INVENTORY_SIZE);


    public SlimeMimicEntity(EntityType<? extends SlimeMimicEntity> entityType, World world) {
        super((EntityType<? extends SlimeEntity>) entityType, world);
        this.setCanPickUpLoot(true);
    }

    public static SlimeMimicEntity create(World world, BlockPos pos, @Nullable NbtCompound tag, @Nullable Entity target) {
        SlimeMimicEntity entity = new SlimeMimicEntity(Main.SLIME_MIMIC, world);
        if(tag != null)
            entity.readNbt(tag);
        entity.setPosition(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
        if(target != null)
            entity.lookAtEntity(target, 360, 360);
        entity.setVelocity(0, 0, 0);
        entity.setForwardSpeed(0);
        entity.playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0f, (entity.random.nextFloat() - entity.random.nextFloat()) * 0.2f + 1.0f);
        return entity;
    }

    @Override
    @Nullable
    //TODO This is only walk around solution, it will break in the future.
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setSize(2, true);
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

            this.setPersistent();

            this.triggerItemPickedUpByEntityCriteria(item);
            this.sendPickup(item, itemStack.getCount());
            ItemStack itemStack2 = inventory.addStack(itemStack);
            if (itemStack2.isEmpty()) {
                item.discard();
            } else {
                itemStack.setCount(itemStack2.getCount());
            }

            if (!this.getWorld().isClient()) {
                for(int i = 0; i< INVENTORY_SIZE; i++){
                    if(this.inventory.getStack(i).isEmpty()){
                        if(i != this.inventory_usage){
                            this.inventory_usage = i;
                            i = (int) Math.ceil(inventory_usage / 2.0);
                            Main.LOGGER.warn(i);
                            if(i > this.getSize()){
                                Main.LOGGER.warn("GROW");
                                this.setSize(i, false, true);
                            }
                        }
                        break;
                    }
                }

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
//        return ParticleTypes.FALLING_HONEY;
        return ParticleTypes.WAX_ON;
    }

    @Override
    protected void setSize(int size, boolean heal) {
        super.setSize(size, heal);

        int i = MathHelper.clamp(size, MIN_SIZE, MAX_SIZE);

        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10 + i * 2);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3f + 0.1f * (float)i);
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(8 + i * 2);

        if (heal) { this.setHealth(this.getMaxHealth()); }

        this.experiencePoints = 10+i*2;
    }

    protected void setSize(int size, boolean heal, boolean growHeal) {
        this.setSize(size, heal);
        if(!heal && growHeal){
            this.heal(2);
        }
    }

    public float getScale(){
        return .6f + .1f * (float)this.getSize();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return this.getType().getDimensions().scaled(this.getScale());
    }

    @Override
    public boolean isSmall(){return false;}

    //TODO This is only walk around solution, it will break in the future.
    protected boolean canAttack() {
        return !this.getWorld().isClient();
    }

    @Override
    protected Identifier getLootTableId() {
        Main.LOGGER.warn(this.getType().getLootTableId());
        return this.getType().getLootTableId();
    }

    public void changeToBlock(BlockState state) {
        Main.LOGGER.warn("Change To Block");

        BlockPos pos = this.getBlockPos();

        //TODO -> CHECK IF EMPTY

        for (Vec3i v : CELLS) {
            if(this.getWorld().getBlockState(pos.add(v)).isAir()){
                Main.LOGGER.warn("Find place");
                this.remove(RemovalReason.DISCARDED);

                this.getWorld().setBlockState(pos.add(v), Main.SLIME_MIMIC_BLOCK.getDefaultState());
                SlimeMimicBlockEntity entity = (SlimeMimicBlockEntity) this.getWorld().getBlockEntity(pos.add(v));

                if(entity == null){
                    Main.LOGGER.warn("Can't get blockEntity");
                }

                entity.setBlockState(state);
                entity.setMimicNbt(this.writeNbt(new NbtCompound()));
                entity.sync();

                this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                return;
            }
        }

        Main.LOGGER.warn("Can't find place for mimicking");
    }
}