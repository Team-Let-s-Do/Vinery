package satisfyu.vinery.client;

import de.cristelknight.doapi.DoApi;
import de.cristelknight.doapi.terraform.sign.TerraformSignHelper;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import satisfyu.vinery.client.gui.ApplePressGui;
import satisfyu.vinery.client.gui.BasketGui;
import satisfyu.vinery.client.gui.FermentationBarrelGui;
import satisfyu.vinery.client.model.MuleModel;
import satisfyu.vinery.client.render.block.FlowerPotBlockEntityRenderer;
import satisfyu.vinery.client.render.block.storage.BasketRenderer;
import satisfyu.vinery.client.render.entity.MuleRenderer;
import satisfyu.vinery.client.render.entity.WanderingWinemakerRenderer;
import satisfyu.vinery.network.VineryNetwork;
import satisfyu.vinery.registry.*;

import static satisfyu.vinery.Vinery.LOGGER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static satisfyu.vinery.registry.ObjectRegistry.*;

@Environment(EnvType.CLIENT)
public class VineryClient {

    public static final Collection<Block> LATTICE_BLOCKS = new ArrayList<>();

    public static void onInitializeClient() {

        VineryNetwork.registerS2CPackets();

        RenderTypeRegistry.register(RenderType.cutout(),
                RED_GRAPE_BUSH.get(), WHITE_GRAPE_BUSH.get(), CHERRY_DOOR.get(), FERMENTATION_BARREL.get(),
                MELLOHI_WINE.get(), CLARK_WINE.get(), BOLVAR_WINE.get(), CHERRY_WINE.get(),
                LILITU_WINE.get(), CHENET_WINE.get(), NOIR_WINE.get(), TABLE.get(), APPLE_CIDER.get(),
                APPLE_WINE.get(), SOLARIS_WINE.get(), JELLIE_WINE.get(), AEGIS_WINE.get(), KELP_CIDER.get(),
                SAVANNA_RED_GRAPE_BUSH.get(), SAVANNA_WHITE_GRAPE_BUSH.get(),
                CHORUS_WINE.get(), STAL_WINE.get(), MAGNETIC_WINE.get(), STRAD_WINE.get(), JUNGLE_WHITE_GRAPE_BUSH.get(),
                JUNGLE_RED_GRAPE_BUSH.get(), TAIGA_RED_GRAPE_BUSH.get(), TAIGA_WHITE_GRAPE_BUSH.get(),
                GRAPEVINE_STEM.get(), WINE_BOX.get(), FLOWER_POT.get(), CHAIR.get(),
                APPLE_PRESS.get(), GRASS_SLAB.get(), CHERRY_SAPLING.get(), APPLE_TREE_SAPLING.get(),
                KITCHEN_SINK.get(), STACKABLE_LOG.get(), APPLE_LEAVES.get(), POTTED_APPLE_TREE_SAPLING.get(),
                POTTED_CHERRY_TREE_SAPLING.get(), RED_WINE.get(), KNULP_WINE.get(),
                CHAIR.get(), CRISTEL_WINE.get(), VILLAGERS_FRIGHT.get(), EISWEIN.get(), CREEPERS_CRUSH.get(),
                GLOWING_WINE.get(), JO_SPECIAL_MIXTURE.get(), MEAD.get(), BOTTLE_MOJANG_NOIR.get(),
                TABLE.get(), OAK_WINE_RACK_MID.get(), DARK_OAK_WINE_RACK_MID.get(), BIRCH_WINE_RACK_MID.get(),
                SPRUCE_WINE_RACK_MID.get(), JUNGLE_WINE_RACK_MID.get(), MANGROVE_WINE_RACK_MID.get(), CHERRY_WINE_RACK_MID.get(),
                ACACIA_WINE_RACK_MID.get(), MCCHERRY_WINE_RACK_MID.get(), BAMBOO_WINE_RACK_MID.get(), OAK_LATTICE.get(), SPRUCE_LATTICE.get(),
                BIRCH_LATTICE.get(), DARK_OAK_LATTICE.get(), MCCHERRY_LATTICE.get(), BAMBOO_LATTICE.get(), ACACIA_LATTICE.get(), JUNGLE_LATTICE.get(),
                MANGROVE_LATTICE.get()
                );


        ClientStorageTypes.init();
        RenderTypeRegistry.register(RenderType.translucent(), WINDOW.get());

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> GrassColor.get(0.5, 1.0), GRASS_SLAB);

        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
                    if (world == null || pos == null) {
                        return -1;
                    }
                    return BiomeColors.getAverageWaterColor(world, pos);
                }, KITCHEN_SINK.get()
        );

        ColorHandlerRegistry.registerBlockColors((state,world,pos,tintIndex)->{
                    if(world== null || pos == null){
                        return -1;
                    }
                    return BiomeColors.getAverageGrassColor(world,pos);
                },  TAIGA_WHITE_GRAPE_BUSH.get(), TAIGA_RED_GRAPE_BUSH.get(), GRASS_SLAB.get()
        );

        ColorHandlerRegistry.registerBlockColors((state,world,pos,tintIndex)->{
                    if(world== null || pos == null){
                        return -1;
                    }
                    return BiomeColors.getAverageFoliageColor(world,pos);
                }, SAVANNA_RED_GRAPE_BUSH.get(), SAVANNA_WHITE_GRAPE_BUSH.get(), JUNGLE_RED_GRAPE_BUSH.get(), JUNGLE_WHITE_GRAPE_BUSH.get(),
                GRAPEVINE_STEM.get()
        );

        MenuRegistry.registerScreenFactory(ScreenhandlerTypeRegistry.FERMENTATION_BARREL_GUI_HANDLER.get(), FermentationBarrelGui::new);
        MenuRegistry.registerScreenFactory(ScreenhandlerTypeRegistry.APPLE_PRESS_GUI_HANDLER.get(), ApplePressGui::new);
        MenuRegistry.registerScreenFactory(ScreenhandlerTypeRegistry.BASKET_GUI_HANDLER.get(), BasketGui::new);

        BlockEntityRendererRegistry.register(BlockEntityTypeRegistry.FLOWER_POT_ENTITY.get(), FlowerPotBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntityTypeRegistry.BASKET_ENTITY.get(), BasketRenderer::new);

    }


    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(EntityRegistry.MULE, MuleRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.WANDERING_WINEMAKER, WanderingWinemakerRenderer::new);

    }


    public static void preInitClient(){
        registerEntityRenderers();
        TerraformSignHelper.regsterSignSprite(BoatAndSignRegistry.CHERRY_SIGN_TEXTURE);
        EntityModelLayerRegistry.register(MuleModel.LAYER_LOCATION, MuleModel::getTexturedModelData);
        EntityModelLayerRegistry.register(BasketRenderer.LAYER_LOCATION, BasketRenderer::getTexturedModelData);

        ArmorRegistry.registerArmorModelLayers();

        LOGGER.info("Resource provider initialized, side is {}", Platform.getEnvironment().toPlatform().toString());
    }
    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}