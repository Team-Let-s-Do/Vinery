package satisfyu.vinery.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import satisfyu.vinery.registry.ObjectRegistry;

import java.util.Map;

public class WanderingWinemakerEntity extends WanderingTrader {
	public static final Int2ObjectMap<VillagerTrades.ItemListing[]> TRADES = new Int2ObjectOpenHashMap<>(Map.of(1, new VillagerTrades.ItemListing[] {
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.RED_GRAPE_SEEDS.get(), 1, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.WHITE_GRAPE_SEEDS.get(), 1, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.TAIGA_RED_GRAPE_SEEDS.get(), 1, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.TAIGA_WHITE_GRAPE_SEEDS.get(), 1, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.SAVANNA_RED_GRAPE_SEEDS.get(), 1, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.SAVANNA_WHITE_GRAPE_SEEDS.get(), 1, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.JUNGLE_RED_GRAPE_SEEDS.get(), 1, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.JUNGLE_WHITE_GRAPE.get(), 1, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.CHERRY_SAPLING.get(), 3, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.OLD_CHERRY_SAPLING.get(), 5, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.RED_GRAPE.get(), 2, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.RED_GRAPEJUICE_WINE_BOTTLE.get(), 4, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.WHITE_GRAPEJUICE_WINE_BOTTLE.get(), 4, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.SAVANNA_RED_GRAPEJUICE_BOTTLE.get(), 4, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.TAIGA_WHITE_GRAPEJUICE_BOTTLE.get(), 4, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.JUNGLE_RED_GRAPEJUICE_BOTTLE.get(), 4, 1, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.COARSE_DIRT_SLAB.get(), 1, 3, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.GRASS_SLAB.get(), 1, 3, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.CHERRY_PLANKS.get(), 3, 4, 8, 1),
			new VillagerTrades.ItemsForEmeralds(ObjectRegistry.CHERRY_WINE.get(), 1, 1, 8, 1)
	}));
	
	public WanderingWinemakerEntity(EntityType<? extends WanderingWinemakerEntity> entityType, Level world) {
		super(entityType, world);
	}
	
	@Override
	protected void updateTrades() {
		if (this.offers == null) {
			this.offers = new MerchantOffers();
		}
		this.addOffersFromItemListings(this.offers, TRADES.get(1), 8);
	}
	
}