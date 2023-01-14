package satisfyu.vinery.block.entity;

import net.minecraft.text.TranslatableText;
import satisfyu.vinery.client.gui.handler.FermentationBarrelGuiHandler;
import satisfyu.vinery.recipe.FermentationBarrelRecipe;
import satisfyu.vinery.registry.ObjectRegistry;
import satisfyu.vinery.registry.VineryBlockEntityTypes;
import satisfyu.vinery.registry.VineryRecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import satisfyu.vinery.util.WineYears;

public class FermentationBarrelBlockEntity extends BlockEntity implements Inventory, BlockEntityTicker<FermentationBarrelBlockEntity>, NamedScreenHandlerFactory {

    private DefaultedList<ItemStack> inventory;
    public static final int CAPACITY = 6;
    public static final int COOKING_TIME_IN_TICKS = 1800; // 90s or 3 minutes
    private static final int BOTTLE_INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private int fermentationTime = 0;
    private int totalFermentationTime;

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {

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
        public int size() {
            return 2;
        }
    };

    public FermentationBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(VineryBlockEntityTypes.FERMENTATION_BARREL_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(CAPACITY, ItemStack.EMPTY);
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.fermentationTime = nbt.getShort("FermentationTime");
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putShort("FermentationTime", (short) this.fermentationTime);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, FermentationBarrelBlockEntity blockEntity) {
        if (world.isClient) return;
        boolean dirty = false;
        final var recipeType = world.getRecipeManager()
                .getFirstMatch(VineryRecipeTypes.FERMENTATION_BARREL_RECIPE_TYPE, blockEntity, world)
                .orElse(null);
        if (canCraft(recipeType)) {
            this.fermentationTime++;

            if (this.fermentationTime == this.totalFermentationTime) {
                this.fermentationTime = 0;
                craft(recipeType);
                dirty = true;
            }
        } else {
            this.fermentationTime = 0;
        }
        if (dirty) {
            markDirty();
        }

    }

    private boolean canCraft(FermentationBarrelRecipe recipe) {
        if (recipe == null || recipe.getOutput().isEmpty()) {
            return false;
        } else if (areInputsEmpty()) {
            return false;
        } else if (this.getStack(BOTTLE_INPUT_SLOT).isEmpty()) {
            return false;
        } else {
            final Block block = Block.getBlockFromItem(this.getStack(BOTTLE_INPUT_SLOT).getItem());
            if (block != ObjectRegistry.WINE_BOTTLE) {
                return false;
            }
            return this.getStack(OUTPUT_SLOT).isEmpty();
        }
    }

    private boolean areInputsEmpty() {
        int emptyStacks = 0;
        for (int i = 2; i < 6; i++) {
            if (this.getStack(i).isEmpty()) emptyStacks++;
        }
        return emptyStacks == 4;
    }
    private void craft(FermentationBarrelRecipe recipe) {
        if (!canCraft(recipe)) {
            return;
        }
        final ItemStack recipeOutput = recipe.getOutput();
        final ItemStack outputSlotStack = this.getStack(OUTPUT_SLOT);
        if (outputSlotStack.isEmpty()) {
            ItemStack output = recipeOutput.copy();
            WineYears.setWineYear(output, this.world);
            setStack(OUTPUT_SLOT, output);
        }
        // Decrement bottles
        final ItemStack bottle = this.getStack(BOTTLE_INPUT_SLOT);
        if (bottle.getCount() > 1) {
            removeStack(BOTTLE_INPUT_SLOT, 1);
        } else if (bottle.getCount() == 1) {
            setStack(BOTTLE_INPUT_SLOT, ItemStack.EMPTY);
        }

        // Decrement ingredient
        for (Ingredient entry : recipe.getIngredients()) {
            if (entry.test(this.getStack(2))) {
                removeStack(2, 1);
            }
            if (entry.test(this.getStack(3))) {
                removeStack(3, 1);
            }
            if (entry.test(this.getStack(4))) {
                removeStack(4, 1);
            }
            if (entry.test(this.getStack(5))) {
                removeStack(5, 1);
            }
        }
    }


    @Override
    public int size() {
        return CAPACITY;
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        final ItemStack stackInSlot = this.inventory.get(slot);
        boolean dirty = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(stackInSlot) && ItemStack.areNbtEqual(stack, stackInSlot);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        if (slot == BOTTLE_INPUT_SLOT || slot == 2 || slot == 3 || slot == 4|| slot == 5) {
            if (!dirty) {
                this.totalFermentationTime = 50;
                this.fermentationTime = 0;
                markDirty();
            }
        }
    }
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
        }
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }


    @Override
    public Text getDisplayName() {
        return new TranslatableText(this.getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FermentationBarrelGuiHandler(syncId, inv, this, this.propertyDelegate);
    }
}
