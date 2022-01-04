package net.jptrzy.slime.mimic.client.render.entity;

import net.jptrzy.slime.mimic.Main;
import net.jptrzy.slime.mimic.entity.mob.SlimeMimicEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SlimeMimicEntityRenderer extends MobEntityRenderer<SlimeMimicEntity, SlimeEntityModel<SlimeMimicEntity>> {
    private static final Identifier TEXTURE = new Identifier(Main.MOD_ID+":textures/entity/slime/slime_mimic.png");

    public SlimeMimicEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SlimeEntityModel(context.getPart(EntityModelLayers.SLIME)), 0.25f);
        this.addFeature(new SlimeOverlayFeatureRenderer<SlimeMimicEntity>(this, context.getModelLoader()));
    }

    @Override
    public void render(SlimeMimicEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.shadowRadius = entity.getScale() / 2;
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    protected void scale(SlimeMimicEntity entity, MatrixStack matrixStack, float f) {
//        float g = 0.999f;
//        matrixStack.scale(0.999f, 0.999f, 0.999f);
//        matrixStack.translate(0.0, 0.001f, 0.0);
        float scale = 2f * entity.getScale();
        float i = MathHelper.lerp(f, entity.lastStretch, entity.stretch) / (scale * 0.5f + 1.0f);
        float j = 1.0f / (i + 1.0f);
//        matrixStack.scale(j * h, 1.0f / j * h, j * h);
        matrixStack.scale(j * scale, 1.0f / j * scale, j * scale);

    }

    @Override
    public Identifier getTexture(SlimeMimicEntity slimeEntity) {
        return TEXTURE;
    }
}
