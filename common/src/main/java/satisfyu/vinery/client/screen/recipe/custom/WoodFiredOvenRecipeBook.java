package satisfyu.vinery.client.screen.recipe.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import satisfyu.vinery.client.recipebook.PrivateRecipeBookWidget;
import satisfyu.vinery.registry.VineryRecipeTypes;

import java.util.List;
import java.util.Random;

public class WoodFiredOvenRecipeBook extends PrivateRecipeBookWidget {
	private static final Component TOGGLE_COOKABLE_TEXT;

	static {
		TOGGLE_COOKABLE_TEXT = new TranslatableComponent("gui.vinery.recipebook.toggleRecipes.cookable");
	}

	public WoodFiredOvenRecipeBook() {
	}

	@Override
	protected RecipeType<? extends Recipe<Container>> getRecipeType() {
		return VineryRecipeTypes.WOOD_FIRED_OVEN_RECIPE_TYPE.getOrNull();
	}

	@Override
	public void showGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
		this.ghostSlots.addSlot(recipe.getResultItem(), slots.get(4).x, slots.get(4).y);
		int j = 0;
		for (Ingredient ingredient : recipe.getIngredients()) {
			ItemStack[] inputStacks = ingredient.getItems();
			ItemStack inputStack = inputStacks[new Random().nextInt(0, inputStacks.length - 1)];
			this.ghostSlots.addSlot(inputStack, slots.get(j).x, slots.get(j++).y);
		}
	}

	@Override
	public void insertRecipe(Recipe<?> recipe) {
		int usedInputSlots = 0;
		for (Ingredient ingredient : recipe.getIngredients()) {
			int slotIndex = 0;
			for (Slot slot : screenHandler.slots) {
				ItemStack itemStack = slot.getItem();
				if (ingredient.test(itemStack) && usedInputSlots < 3) {
					Minecraft.getInstance().gameMode.handleInventoryMouseClick(screenHandler.containerId, slotIndex, 0,
							ClickType.PICKUP, Minecraft.getInstance().player);
					Minecraft.getInstance().gameMode.handleInventoryMouseClick(screenHandler.containerId,
							usedInputSlots, 0, ClickType.PICKUP, Minecraft.getInstance().player);
					++usedInputSlots;
					break;
				}
				++slotIndex;
			}
		}
	}

	@Override
	protected void setCraftableButtonTexture() {
		this.toggleCraftableButton.initTextureValues(152, 41, 28, 18, TEXTURE);
	}

	@Override
	public void slotClicked(@Nullable Slot slot) {
		super.slotClicked(slot);
		if (slot != null && slot.index < this.screenHandler.getCraftingSlotCount()) {
			this.ghostSlots.reset();
		}
	}

	@Override
	protected Component getToggleCraftableButtonText() {
		return TOGGLE_COOKABLE_TEXT;
	}
}
