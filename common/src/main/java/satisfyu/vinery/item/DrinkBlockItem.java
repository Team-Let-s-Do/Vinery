package satisfyu.vinery.item;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import satisfyu.vinery.registry.ItemRegistry;
import satisfyu.vinery.util.WineYears;

import java.util.List;
import java.util.Map;

public class DrinkBlockItem extends BlockItem {
	public DrinkBlockItem(Block block, Properties settings) {
		super(block, settings);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@Override
	protected BlockState getPlacementState(BlockPlaceContext context) {
		if (!context.getPlayer().isCrouching()) {
			return null;
		}
		BlockState blockState = this.getBlock().getStateForPlacement(context);
		return blockState != null && this.canPlace(context, blockState) ? blockState : null;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		List<Pair<MobEffectInstance, Float>> list2 =
				getFoodProperties() != null ? getFoodProperties().getEffects() : Lists.newArrayList();
		List<Pair<Attribute, AttributeModifier>> list3 = Lists.newArrayList();
		if (list2.isEmpty()) {
			tooltip.add(new TranslatableComponent("effect.none").withStyle(ChatFormatting.GRAY));
		}
		else {
			for (Pair<MobEffectInstance, Float> statusEffectInstance : list2) {
				MutableComponent mutableText = new TranslatableComponent(
						statusEffectInstance.getFirst().getDescriptionId());
				MobEffect statusEffect = statusEffectInstance.getFirst().getEffect();
				Map<Attribute, AttributeModifier> map = statusEffect.getAttributeModifiers();
				if (!map.isEmpty()) {
					for (Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier entityAttributeModifier = entry.getValue();
						AttributeModifier entityAttributeModifier2 = new AttributeModifier(
								entityAttributeModifier.getName(),
								statusEffect.getAttributeModifierValue(statusEffectInstance.getFirst().getAmplifier(),
										entityAttributeModifier), entityAttributeModifier.getOperation());
						list3.add(new Pair<>(entry.getKey(), entityAttributeModifier2));
					}
				}
				if (world != null) {
					mutableText = new TranslatableComponent("potion.withAmplifier", mutableText,
							new TranslatableComponent("potion.potency." + WineYears.getEffectLevel(stack, world)));
				}
				if (statusEffectInstance.getFirst().getDuration() > 20) {
					mutableText = new TranslatableComponent("potion.withDuration", mutableText,
							MobEffectUtil.formatDuration(statusEffectInstance.getFirst(),
									statusEffectInstance.getSecond()));
				}
				tooltip.add(mutableText.withStyle(statusEffect.getCategory().getTooltipFormatting()));
			}
		}
		if (!list3.isEmpty()) {
			tooltip.add(Component.nullToEmpty(""));
			tooltip.add(new TranslatableComponent("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
			for (Pair<Attribute, AttributeModifier> pair : list3) {
				AttributeModifier entityAttributeModifier3 = pair.getSecond();
				double d = entityAttributeModifier3.getAmount();
				double e;
				if (entityAttributeModifier3.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE
						&& entityAttributeModifier3.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
					e = entityAttributeModifier3.getAmount();
				}
				else {
					e = entityAttributeModifier3.getAmount() * 100.0;
				}
				if (d > 0.0) {
					tooltip.add(new TranslatableComponent(
							"attribute.modifier.plus." + entityAttributeModifier3.getOperation().toValue(),
							ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(e),
							new TranslatableComponent(pair.getFirst().getDescriptionId())).withStyle(
							ChatFormatting.BLUE));
				}
				else if (d < 0.0) {
					e *= -1.0;
					tooltip.add(new TranslatableComponent(
							"attribute.modifier.take." + entityAttributeModifier3.getOperation().toValue(),
							ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(e),
							new TranslatableComponent(pair.getFirst().getDescriptionId())).withStyle(
							ChatFormatting.RED));
				}
			}
		}
		tooltip.add(Component.nullToEmpty(""));
		tooltip.add(new TranslatableComponent("block.vinery.canbeplaced.tooltip").withStyle(ChatFormatting.GRAY));
		tooltip.add(new TranslatableComponent("tooltip.vinery.year").withStyle(ChatFormatting.GRAY)
				.append(Component.nullToEmpty(" " + WineYears.getWineYear(stack, world))));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
		super.finishUsingItem(itemStack, level, livingEntity);
		if (livingEntity instanceof ServerPlayer serverPlayer) {
			CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
			serverPlayer.awardStat(Stats.ITEM_USED.get(this));
		}
		if (itemStack.isEmpty()) {
			return new ItemStack(ItemRegistry.WINE_BOTTLE.get());
		}
		if (livingEntity instanceof Player player && !((Player) livingEntity).getAbilities().instabuild) {
			ItemStack itemStack2 = new ItemStack(ItemRegistry.WINE_BOTTLE.get());
			if (!player.getInventory().add(itemStack2)) {
				player.drop(itemStack2, false);
			}
		}
		return itemStack;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		return ItemUtils.startUsingInstantly(level, player, interactionHand);
	}
}
