package satisfyu.vinery.item;

import org.jetbrains.annotations.Nullable;
import satisfyu.vinery.registry.ObjectRegistry;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class AppleSauceItem extends Item {

    public AppleSauceItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("item.vinery.applesauce_line1.tooltip"));
            tooltip.add(Component.translatable("item.vinery.applesauce_line2.tooltip"));
            tooltip.add(Component.translatable("item.vinery.applesauce_line3.tooltip"));

            tooltip.add(Component.translatable("item.vinery.cookingpot.tooltip"));
        } else {
            tooltip.add(Component.translatable("item.vinery.ingredient.tooltip").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }

        super.appendHoverText(stack, world, tooltip, context);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        ItemStack itemStack = super.finishUsingItem(stack, world, user);
        return user instanceof Player && ((Player)user).getAbilities().instabuild ? itemStack : new ItemStack(Items.BOWL);
    }

}