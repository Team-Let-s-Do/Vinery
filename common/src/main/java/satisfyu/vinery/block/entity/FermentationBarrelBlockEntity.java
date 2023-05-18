package satisfyu.vinery.block.entity;

import satisfyu.vinery.client.gui.handler.FermentationBarrelGuiHandler;
import satisfyu.vinery.item.food.EffectFood;
import satisfyu.vinery.item.food.EffectFoodHelper;
import satisfyu.vinery.registry.ObjectRegistry;
import satisfyu.vinery.registry.VineryBlockEntityTypes;
import satisfyu.vinery.registry.VineryRecipeTypes;
import satisfyu.vinery.util.WineYears;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FermentationBarrelBlockEntity extends BlockEntity implements Container, BlockEntityTicker<FermentationBarrelBlockEntity>, MenuProvider {

    private NonNullList<ItemStack> inventory;
    public static final int CAPACITY = 6;
    public static final int COOKING_TIME_IN_TICKS = 1800; // 90s or 3 minutes
    private static final int BOTTLE_INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 5;
    private int fermentationTime = 0;
    private int totalFermentationTime;

    private final ContainerData propertyDelegate = new ContainerData() {

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> FermentationBarrelBlockEntity.this.fermentationTime;
                case 1 -> FermentationBarrelBlockEntity.this.totalFermentationTime;
                default -> 0;
            };
        }


        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> FermentationBarrelBlockEntity.this.fermentationTime = value;
                case 1 -> FermentationBarrelBlockEntity.this.totalFermentationTime = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public FermentationBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(VineryBlockEntityTypes.FERMENTATION_BARREL_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(CAPACITY, ItemStack.EMPTY);
    }


    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.inventory);
        this.fermentationTime = nbt.getShort("FermentationTime");
    }


    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        ContainerHelper.saveAllItems(nbt, this.inventory);
        nbt.putShort("FermentationTime", (short) this.fermentationTime);
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, FermentationBarrelBlockEntity blockEntity) {
        if (world.isClientSide) return;
        boolean dirty = false;
        Recipe<?> recipe = world.getRecipeManager().getRecipeFor(VineryRecipeTypes.FERMENTATION_BARREL_RECIPE_TYPE.get(), this, world).orElse(null);
        if (canCraft(recipe)) {
            this.fermentationTime++;

            if (this.fermentationTime >= this.totalFermentationTime) {
                this.fermentationTime = 0;
                craft(recipe);
                dirty = true;
            }
        } else {
            this.fermentationTime = 0;
        }
        if (dirty) {
            setChanged();
        }

    }

    private boolean canCraft(Recipe<?> recipe) {
        if (recipe == null || recipe.getResultItem().isEmpty()) {
            return false;
        } else if (areInputsEmpty()) {
            return false;
        } else if (this.getItem(BOTTLE_INPUT_SLOT).isEmpty()) {
            return false;
        } else {
            final Item item = this.getItem(BOTTLE_INPUT_SLOT).getItem();
            if (item != ObjectRegistry.WINE_BOTTLE.get().asItem()) {
                return false;
            }
            return this.getItem(OUTPUT_SLOT).isEmpty();
        }
    }

    private boolean areInputsEmpty() {
        int emptyStacks = 0;
        for (int i = 1; i < 5; i++) {
            if (this.getItem(i).isEmpty()) emptyStacks++;
        }
        return emptyStacks == 4;
    }
    private void craft(Recipe<?> recipe) {
        if (!canCraft(recipe)) {
            return;
        }
        final ItemStack recipeOutput = generateOutputItem(recipe);
        final ItemStack outputSlotStack = this.getItem(OUTPUT_SLOT);
        if (outputSlotStack.isEmpty()) {
            ItemStack output = recipeOutput.copy();
            WineYears.setWineYear(output, this.level);
            setItem(OUTPUT_SLOT, output);
        }
        // Decrement bottles
        final ItemStack bottle = this.getItem(BOTTLE_INPUT_SLOT);
        if (bottle.getCount() > 1) {
            removeItem(BOTTLE_INPUT_SLOT, 1);
        } else if (bottle.getCount() == 1) {
            setItem(BOTTLE_INPUT_SLOT, ItemStack.EMPTY);
        }

        // Decrement ingredient
        for (Ingredient entry : recipe.getIngredients()) {
            if (entry.test(this.getItem(1))) {
                removeItem(1, 1);
            }
            if (entry.test(this.getItem(2))) {
                removeItem(2, 1);
            }
            if (entry.test(this.getItem(3))) {
                removeItem(3, 1);
            }
            if (entry.test(this.getItem(4))) {
                removeItem(4, 1);
            }
        }
    }

    private ItemStack generateOutputItem(Recipe<?> recipe) {
        ItemStack outputStack = recipe.getResultItem();

        if (!(outputStack.getItem() instanceof EffectFood)) {
            return outputStack;
        }

        for (Ingredient ingredient : recipe.getIngredients()) {
            for (int j = 0; j < 5; j++) {
                ItemStack stack = this.getItem(j);
                if (ingredient.test(stack)) {
                    EffectFoodHelper.getEffects(stack).forEach(effect -> EffectFoodHelper.addEffect(outputStack, effect));
                    break;
                }
            }
        }
        return outputStack;
    }


    @Override
    public int getContainerSize() {
        return CAPACITY;
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        final ItemStack stackInSlot = this.inventory.get(slot);
        boolean dirty = !stack.isEmpty() && stack.sameItem(stackInSlot) && ItemStack.tagMatches(stack, stackInSlot);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        if (slot == BOTTLE_INPUT_SLOT || slot == 2 || slot == 3 || slot == 4|| slot == 5) {
            if (!dirty) {
                this.totalFermentationTime = 50;
                this.fermentationTime = 0;
                setChanged();
            }
        }
    }
    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) <= 64.0;
        }
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }


    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new FermentationBarrelGuiHandler(syncId, inv, this, this.propertyDelegate);
    }
}
