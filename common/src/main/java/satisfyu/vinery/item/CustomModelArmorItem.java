package satisfyu.vinery.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public abstract class CustomModelArmorItem extends ArmorItem {
	public CustomModelArmorItem(ArmorMaterial material, EquipmentSlot type, Properties settings) {
		super(material, type, settings);
	}

	public abstract ResourceLocation getTexture();

	public abstract Float getOffset();
}
