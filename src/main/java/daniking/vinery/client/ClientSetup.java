package daniking.vinery.client;

import com.terraformersmc.terraform.boat.api.client.TerraformBoatClientHelper;
import com.terraformersmc.terraform.sign.SpriteIdentifierRegistry;
import daniking.vinery.Vinery;
import daniking.vinery.VineryIdentifier;
import daniking.vinery.client.gui.CookingPotGui;
import daniking.vinery.client.gui.FermentationBarrelGui;
import daniking.vinery.client.gui.StoveGui;
import daniking.vinery.client.render.block.WineRackRenderer;
import daniking.vinery.client.render.entity.SimpleGeoRenderer;
import daniking.vinery.client.render.entity.WanderingWinemakerRenderer;
import daniking.vinery.client.render.feature.StrawHatRenderer;
import daniking.vinery.registry.ObjectRegistry;
import daniking.vinery.registry.VineryBlockEntityTypes;
import daniking.vinery.registry.VineryEntites;
import daniking.vinery.registry.VineryScreenHandlerTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.SpriteIdentifier;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Environment(EnvType.CLIENT)
public class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SpriteIdentifierRegistry.INSTANCE.addIdentifier(new SpriteIdentifier(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE, ObjectRegistry.CHERRY_SIGN.getTexture()));
        //ObjectRegistry.EMPTY_RED_VINE, ObjectRegistry.RED_VINE, ObjectRegistry.RED_VINE_VARIANT_B, ObjectRegistry.RED_VINE_VARIANT_C
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ObjectRegistry.RED_GRAPE_BUSH, ObjectRegistry.WHITE_GRAPE_BUSH,
                                               ObjectRegistry.CHERRY_DOOR, ObjectRegistry.CHERRY_DOOR_WITH_IRON_BARS, ObjectRegistry.CHERRY_DOOR_WITH_WINDOWS, ObjectRegistry.STACKABLE_LOG, ObjectRegistry.COOKING_POT,
                                               ObjectRegistry.CHERRY_JAM, ObjectRegistry.CHERRY_JAR, ObjectRegistry.FERMENTATION_BARREL,
                                                ObjectRegistry.MELLOHI_WINE, ObjectRegistry.CLARK_WINE, ObjectRegistry.BOLVAR_WINE, ObjectRegistry.CHERRY_WINE


        );
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ObjectRegistry.ROCKS, ObjectRegistry.ROCKS_VARIANT_B, ObjectRegistry.ROCKS_VARIANT_C,
                                               ObjectRegistry.RED_GRASS_FLOWER, ObjectRegistry.RED_GRASS_FLOWER_VARIANT_B,
                                               ObjectRegistry.PINK_GRASS_FLOWER, ObjectRegistry.PINK_GRASS_FLOWER_VARIANT_B,
                                               ObjectRegistry.WHITE_GRASS_FLOWER, ObjectRegistry.GRAPEVINE_STEM, ObjectRegistry.WINE_BOTTLE,
                                               ObjectRegistry.RED_GRAPEJUICE_WINE_BOTTLE, ObjectRegistry.WHITE_GRAPEJUICE_WINE_BOTTLE,
                                               ObjectRegistry.WINE_BOX, ObjectRegistry.FLOWER_BOX_ALLIUM, ObjectRegistry.FLOWER_BOX_AZURE_BLUET,
                                               ObjectRegistry.FLOWER_BOX, ObjectRegistry.FLOWER_BOX_BLUE_ORCHID, ObjectRegistry.FLOWER_BOX_BLUE_DANDELION,
                                               ObjectRegistry.FLOWER_BOX_BLUE_CORNFLOWER, ObjectRegistry.FLOWER_BOX_BLUE_LILY_OF_THE_VALLEY,
                                               ObjectRegistry.FLOWER_BOX_BLUE_ORANGE_TULIP, ObjectRegistry.FLOWER_BOX_BLUE_OXEYE_DAISY,
                                               ObjectRegistry.FLOWER_BOX_BLUE_PINK_TULIP, ObjectRegistry.FLOWER_BOX_BLUE_POPPY,
                                               ObjectRegistry.FLOWER_BOX_BLUE_RED_TULIP, ObjectRegistry.FLOWER_BOX_BLUE_WHITE_TULIP,
                                               ObjectRegistry.FLOWER_BOX_BLUE_WHITER_ROSE, ObjectRegistry.FLOWER_POT,
                                               ObjectRegistry.WINE_PRESS, ObjectRegistry.GRASS_SLAB,
                                               ObjectRegistry.CHERRY_SAPLING, ObjectRegistry.OLD_CHERRY_SAPLING, ObjectRegistry.KITCHEN_SINK
                                              );

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), ObjectRegistry.WINDOW_1, ObjectRegistry.WINDOW_2);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> BiomeColors.getGrassColor(world, pos), ObjectRegistry.PINK_GRASS_FLOWER, ObjectRegistry.PINK_GRASS_FLOWER_VARIANT_B, ObjectRegistry.RED_GRASS_FLOWER, ObjectRegistry.RED_GRASS_FLOWER_VARIANT_B, ObjectRegistry.WHITE_GRASS_FLOWER, ObjectRegistry.GRASS_SLAB);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getColor(1.0, 0.5), ObjectRegistry.PINK_GRASS_FLOWER, ObjectRegistry.PINK_GRASS_FLOWER_VARIANT_B, ObjectRegistry.RED_GRASS_FLOWER, ObjectRegistry.RED_GRASS_FLOWER_VARIANT_B, ObjectRegistry.WHITE_GRASS_FLOWER, ObjectRegistry.GRASS_SLAB);
//        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
//            if (world == null || pos == null) {
//                return -1;
//            }
//            return BiomeColors.getWaterColor(world, pos);
//        }, ObjectRegistry.KITCHEN_SINK);
        HandledScreens.register(VineryScreenHandlerTypes.STOVE_GUI_HANDLER, StoveGui::new);
        HandledScreens.register(VineryScreenHandlerTypes.FERMENTATION_BARREL_GUI_HANDLER, FermentationBarrelGui::new);
        HandledScreens.register(VineryScreenHandlerTypes.COOKING_POT_SCREEN_HANDLER, CookingPotGui::new);
        TerraformBoatClientHelper.registerModelLayer(new VineryIdentifier("cherry"));

        GeoArmorRenderer.registerArmorRenderer(new StrawHatRenderer(), ObjectRegistry.STRAW_HAT);
        
        EntityRendererRegistry.register(VineryEntites.MULE, mgr -> new SimpleGeoRenderer<>(mgr, Vinery.MODID, "wandering_mule"));
        EntityRendererRegistry.register(VineryEntites.WANDERING_WINEMAKER, WanderingWinemakerRenderer::new);
        
        BlockEntityRendererRegistry.register(VineryBlockEntityTypes.WINE_RACK_GECKO_ENTITY,
                (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new WineRackRenderer());
    }
}
