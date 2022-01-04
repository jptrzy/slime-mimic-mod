package net.jptrzy.slime.mimic.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.jptrzy.slime.mimic.Main;
import net.jptrzy.slime.mimic.client.render.block.entity.SlimeMimicBlockEntityRenderer;
import net.jptrzy.slime.mimic.client.render.entity.SlimeMimicEntityRenderer;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class Client implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Main.SLIME_MIMIC, SlimeMimicEntityRenderer::new);

        BlockEntityRendererRegistry.INSTANCE.register(Main.SLIME_MIMIC_BLOCK_ENTITY,  SlimeMimicBlockEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(Main.SLIME_MIMIC_BLOCK, RenderLayer.getCutout());
    }
}
