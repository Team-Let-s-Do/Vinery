package satisfyu.vinery.item;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ToastItem extends Item {

    public ToastItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("item.vinery.toast_line1.tooltip"));
            tooltip.add(Component.translatable("item.vinery.toast_line2.tooltip"));
            tooltip.add(Component.translatable("item.vinery.oven.tooltip"));
        } else {
            tooltip.add(Component.translatable("item.vinery.ingredient.tooltip").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }

        super.appendHoverText(stack, world, tooltip, context);
    }

}