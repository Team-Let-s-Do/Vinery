package satisfyu.vinery.block.entity;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import satisfyu.vinery.registry.VineryBlockEntityTypes;
import satisfyu.vinery.util.GeneralUtil;
import satisfyu.vinery.util.networking.VineryMessages;

public class StorageBlockEntity extends BlockEntity {
	private int size;

	private NonNullList<ItemStack> inventory;

	public StorageBlockEntity(BlockPos pos, BlockState state) {
		super(VineryBlockEntityTypes.STORAGE_ENTITY.get(), pos, state);
	}

	public StorageBlockEntity(BlockPos pos, BlockState state, int size) {
		super(VineryBlockEntityTypes.STORAGE_ENTITY.get(), pos, state);
		this.size = size;
		this.inventory = NonNullList.withSize(this.size, ItemStack.EMPTY);
	}

	public ItemStack removeStack(int slot) {
		ItemStack stack = inventory.set(slot, ItemStack.EMPTY);
		setChanged();
		return stack;
	}

	public void setStack(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		setChanged();
	}

	@Override
	public void setChanged() {
		if (!level.isClientSide()) {
			FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
			data.writeInt(inventory.size());
			for (ItemStack itemStack : inventory) {
				data.writeItem(itemStack);
			}
			data.writeBlockPos(getBlockPos());
			for (ServerPlayer player : GeneralUtil.tracking((ServerLevel) level, getBlockPos())) {
				NetworkManager.sendToPlayer(player, VineryMessages.ITEM_SYNC, data);
			}
		}
		super.setChanged();
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.size = nbt.getInt("size");
		this.inventory = NonNullList.withSize(this.size, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.inventory);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		ContainerHelper.saveAllItems(nbt, this.inventory);
		nbt.putInt("size", this.size);
		super.saveAdditional(nbt);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	public NonNullList<ItemStack> getInventory() {
		return inventory;
	}

	public void setInventory(NonNullList<ItemStack> inventory) {
		for (int i = 0; i < inventory.size(); i++) {
			this.inventory.set(i, inventory.get(i));
		}
	}
}
