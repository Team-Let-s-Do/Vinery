package satisfyu.vinery.block;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class StackableLogBlock extends SlabBlock {
	public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;

	public static final BooleanProperty FIRED = BooleanProperty.create("fired");

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public StackableLogBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(FIRED, false)
				.setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH));
	}

	public static void displayTickLikeCampfire(BlockState state, Level world, BlockPos pos, Random random, boolean isSignal) {
		if (random.nextFloat() < 0.80f) {
			for (int i = 0; i < random.nextInt(5) + 3; ++i) {
				CampfireBlock.makeParticles(world, pos, isSignal, true);
			}
		}
		if (random.nextInt(10) == 0) {
			world.playLocalSound((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5,
					SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5f + random.nextFloat(),
					random.nextFloat() * 0.7f + 0.6f, false);
		}
		if (random.nextInt(5) == 0) {
			for (int i = 0; i < random.nextInt(4) + 3; ++i) {
				world.addParticle(ParticleTypes.LAVA, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5,
						(double) pos.getZ() + 0.5, random.nextFloat() / 2.0f, 5.0E-5, random.nextFloat() / 2.0f);
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		final ItemStack stack = player.getItemInHand(hand);
		final SlabType stackSize = state.getValue(TYPE);
		if (stack.is(Items.FLINT_AND_STEEL) && stackSize == SlabType.DOUBLE) {
			world.setBlock(pos, state.setValue(FIRED, true), Block.UPDATE_ALL);
			world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
			return InteractionResult.SUCCESS;
		}
		else if (stack.getItem() instanceof ShovelItem && stackSize == SlabType.DOUBLE && state.getValue(FIRED)) {
			world.setBlockAndUpdate(pos, state.setValue(FIRED, false));
			world.playSound(player, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
			final boolean clientSide = world.isClientSide();
			if (clientSide) {
				for (int i = 0; i < 20; ++i) {
					CampfireBlock.makeParticles(world, pos, false, false);
				}
			}
			return InteractionResult.sidedSuccess(clientSide);
		}
		else {
			return InteractionResult.PASS;
		}
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockPos blockPos = ctx.getClickedPos();
		BlockState blockState = ctx.getLevel().getBlockState(blockPos);
		if (blockState.is(this)) {
			return blockState.setValue(TYPE, SlabType.DOUBLE).setValue(FIRED, false).setValue(WATERLOGGED, false)
					.setValue(FACING, ctx.getHorizontalDirection().getOpposite());
		}
		else {
			FluidState fluidState = ctx.getLevel().getFluidState(blockPos);
			BlockState blockState2 = this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(FACING,
					ctx.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED,
					fluidState.getType() == Fluids.WATER);
			Direction direction = ctx.getClickedFace();
			return direction != Direction.DOWN && (direction == Direction.UP || !(
					ctx.getClickLocation().y() - (double) blockPos.getY() > 0.5)) ? blockState2 : blockState2.setValue(
					TYPE, SlabType.TOP);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FIRED, FACING);
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
		if ((state.getValue(FIRED))) {
			displayTickLikeCampfire(state, world, pos, random, world.getBlockState(pos.below()).is(Blocks.HAY_BLOCK));
		}
	}

	@Override
	public void appendHoverText(ItemStack itemStack, BlockGetter world, List<Component> tooltip, TooltipFlag tooltipContext) {
		tooltip.add(new TranslatableComponent("block.vinery.log.tooltip").withStyle(ChatFormatting.ITALIC,
				ChatFormatting.GRAY));
		if (Screen.hasShiftDown()) {
			tooltip.add(new TranslatableComponent("block.vinery.log.tooltip.shift_1"));
			tooltip.add(new TranslatableComponent("block.vinery.log.tooltip.shift_2"));
		}
		else {
			tooltip.add(new TranslatableComponent("block.vinery.log.tooltip.tooltip_shift"));
		}
	}

	@Override
	public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
		boolean isLit = world.getBlockState(pos).getValue(FIRED);
		if (isLit && !entity.fireImmune() && entity instanceof LivingEntity livingEntity
				&& !EnchantmentHelper.hasFrostWalker(livingEntity)) {
			entity.hurt(DamageSource.IN_FIRE, 1.f);
		}
		super.stepOn(world, pos, state, entity);
	}
}
