package satisfyu.vinery.client.render.block.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import satisfyu.vinery.block.entity.StorageBlockEntity;
import satisfyu.vinery.client.render.block.storage.api.StorageTypeRenderer;
import satisfyu.vinery.client.ClientUtil;


public class FlowerBoxRenderer implements StorageTypeRenderer {

    @Override
    public void render(StorageBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, NonNullList<ItemStack> itemStacks) {
        matrices.translate(-0.25f, 0.25f, 0.75f);
        matrices.mulPose(Vector3f.YP.rotationDegrees(90));

        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack stack = itemStacks.get(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                matrices.pushPose();
                matrices.translate(0f, 0f, -0.5f * i);
                ClientUtil.renderBlockFromItem(blockItem, matrices, vertexConsumers, entity);
                matrices.popPose();
            }
        }
    }
}