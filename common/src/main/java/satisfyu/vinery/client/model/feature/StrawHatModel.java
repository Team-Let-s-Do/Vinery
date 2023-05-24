package satisfyu.vinery.client.model.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import satisfyu.vinery.VineryIdentifier;

@Environment(EnvType.CLIENT)
public class StrawHatModel <T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new VineryIdentifier("straw_hat"),
			"main");

	private final ModelPart top_part;

	public StrawHatModel(ModelPart root) {
		this.top_part = root.getChild("top_part");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition top_part = modelPartData.addOrReplaceChild("top_part", CubeListBuilder.create().texOffs(0, 1)
						.addBox(-4.0F, -4.01F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition lower_part = top_part.addOrReplaceChild("lower_part", CubeListBuilder.create().texOffs(0, 13)
						.addBox(-8.0F, 0.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		top_part.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}