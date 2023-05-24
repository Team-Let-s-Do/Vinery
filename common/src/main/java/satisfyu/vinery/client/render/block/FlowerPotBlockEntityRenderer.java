package satisfyu.vinery.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import satisfyu.vinery.block.FlowerPotBlock;
import satisfyu.vinery.block.entity.FlowerPotBlockEntity;

import static satisfyu.vinery.client.ClientUtil.renderBlock;

public class FlowerPotBlockEntityRenderer implements BlockEntityRenderer<FlowerPotBlockEntity> {
	public FlowerPotBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
	}

	@Override
	public void render(FlowerPotBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		if (!entity.hasLevel()) {
			return;
		}
		BlockState selfState = entity.getBlockState();
		if (selfState.getBlock() instanceof FlowerPotBlock) {
			Item item = entity.getFlower();
			matrices.pushPose();
			if (item instanceof BlockItem) {
				BlockState state = ((BlockItem) item).getBlock().defaultBlockState();
				matrices.translate(0f, 0.4f, 0f);
				renderBlock(state, matrices, vertexConsumers, entity);
				state = ((BlockItem) item).getBlock().defaultBlockState().setValue(DoublePlantBlock.HALF,
						DoubleBlockHalf.UPPER);
				matrices.translate(0f, 1f, 0f);
				renderBlock(state, matrices, vertexConsumers, entity);
			}
		}
		matrices.popPose();
	}
}