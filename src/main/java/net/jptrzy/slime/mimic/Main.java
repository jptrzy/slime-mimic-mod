package net.jptrzy.slime.mimic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.jptrzy.slime.mimic.block.SlimeMimicBlock;
import net.jptrzy.slime.mimic.block.entity.SlimeMimicBlockEntity;
import net.jptrzy.slime.mimic.entity.mob.SlimeMimicEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ModInitializer {
	public static final String MOD_ID = "slime_mimic";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final EntityType<SlimeMimicEntity> SLIME_MIMIC = Registry.register(
			Registry.ENTITY_TYPE,
			id("slime_mimic"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SlimeMimicEntity::new).dimensions(EntityDimensions.changing(1, 1)).build()
	);
	public static final Item SLIME_MIMIC_SPAWN_EGG = new SpawnEggItem(SLIME_MIMIC, 16759552, 13736192, new Item.Settings().group(ItemGroup.MISC));


	public static final Block SLIME_MIMIC_BLOCK = new SlimeMimicBlock(FabricBlockSettings.of(Material.BARRIER).strength(999999999).nonOpaque());
	public static BlockEntityType<SlimeMimicBlockEntity> SLIME_MIMIC_BLOCK_ENTITY;


	@Override
	public void onInitialize() {

		SLIME_MIMIC_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID+":slime_mimic_block_entity", FabricBlockEntityTypeBuilder.create(SlimeMimicBlockEntity::new, SLIME_MIMIC_BLOCK).build(null));

		Registry.register(Registry.BLOCK, id("slime_mimic_block"), SLIME_MIMIC_BLOCK);
		Registry.register(Registry.ITEM, id("slime_mimic_block"), new BlockItem(SLIME_MIMIC_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));

		Registry.register(Registry.ITEM, id("slime_mimic_spawn_egg"), SLIME_MIMIC_SPAWN_EGG);

		FabricDefaultAttributeRegistry.register(SLIME_MIMIC, SlimeMimicEntity.createMobAttributes());

	}

	public static Identifier id(String key){
		return new Identifier(Main.MOD_ID, key);
	}
}
