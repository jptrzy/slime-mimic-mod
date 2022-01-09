package net.jptrzy.slime.mimic.client.render.block.entity;


import ca.weblite.objc.Client;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.jptrzy.slime.mimic.Main;
import net.jptrzy.slime.mimic.block.entity.SlimeMimicBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SlimeMimicBlockEntityRenderer implements BlockEntityRenderer<SlimeMimicBlockEntity> {

    private  MinecraftClient minecraft;

    public SlimeMimicBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        minecraft = MinecraftClient.getInstance();
    }

    @Override
    public void render(SlimeMimicBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

//        int color = minecraft.getBlockColors().getColor(blockEntity.getBlockState(), blockEntity.getWorld(), blockEntity.getPos(), 0);
//        Main.LOGGER.debug(color);

        BlockRenderManager manager = minecraft.getBlockRenderManager();
        manager.getModelRenderer().render(
                matrices.peek(), vertexConsumers.getBuffer(RenderLayer.getCutout()), blockEntity.getBlockState(), manager.getModel(blockEntity.getBlockState()), 0, 0,0, light, overlay
        );
//        public void render(MatrixStack.Entry entry, VertexConsumer vertexConsumer, @Nullable BlockState blockState, BakedModel bakedModel, float f, float g, float h, int i, int j) {

        matrices.pop();
    }
}
