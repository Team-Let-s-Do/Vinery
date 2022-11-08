package daniking.vinery.client.render.block;

import daniking.vinery.block.DisplayRackBlock;
import daniking.vinery.block.WineBoxBlock;
import daniking.vinery.block.entity.GeckoStorageBlockEntity;
import daniking.vinery.item.DrinkBlockItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import software.bernie.geckolib3.util.RenderUtils;

public class WineRackRenderer extends GeoBlockRenderer<GeckoStorageBlockEntity> {
	GeckoStorageBlockEntity entity;
	VertexConsumerProvider renderTypeBuffer;
	
	public WineRackRenderer() {
		super(new WineRackGeckoModel());
	}
	
	@Override
	public RenderLayer getRenderType(GeckoStorageBlockEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
		entity = animatable;
		this.renderTypeBuffer = renderTypeBuffer;
		return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
	}
	
	@Override
	public void renderRecursively(GeoBone bone, MatrixStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("ref_points")) {
			matrixStack.push();
			matrixStack.translate(-0.63f, -0.65f, 0f);
			for (GeoBone b : bone.childBones) {
				matrixStack.push();
				RenderUtils.translate(b, matrixStack);
				RenderUtils.moveToPivot(b, matrixStack);
				matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
				matrixStack.scale(0.9f, 0.9f, 0.9f);
				if (entity.getCachedState().getBlock() instanceof WineBoxBlock) {
					matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
					matrixStack.translate(-0.6f, -1.1f, 0f);
					matrixStack.scale(0.9f, 0.9f, 0.9f);
				} else if (entity.getCachedState().getBlock() instanceof DisplayRackBlock) {
					matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
					matrixStack.translate(0.7f, 0.63f, 0f);
				}
				ItemStack itemStack = entity.getStack(Integer.parseInt(String.valueOf(b.getName().charAt(4))) - 1);
				if (!itemStack.isEmpty()) {
					if (itemStack.getItem() instanceof  DrinkBlockItem) {
						if (entity.getCachedState().getBlock() instanceof DisplayRackBlock) {
							matrixStack.translate(-0.67f, -0.05f, -0.6f);
						}
						BlockState bs = ((DrinkBlockItem) itemStack.getItem()).getBlock().getDefaultState();
						MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(bs, matrixStack, renderTypeBuffer, packedLightIn, packedOverlayIn);
					} else {
						if (entity.getCachedState().getBlock() instanceof DisplayRackBlock && itemStack.getItem() instanceof BlockItem) {
							matrixStack.translate(0f, -0.1f, 0f);
						}
						MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.GROUND, packedLightIn, packedOverlayIn, matrixStack, renderTypeBuffer, 0);
					}
				}
				// Restart render buffer to continue rendering the rest of the wine rack
				renderTypeBuffer.getBuffer(RenderLayer.getEntityTranslucent(getTextureLocation(entity)));
				matrixStack.pop();
			}
			matrixStack.pop();
			return;
		}
		super.renderRecursively(bone, matrixStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
