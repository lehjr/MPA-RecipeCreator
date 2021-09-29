package com.github.lehjr.mpsrecipecreator.basemod;

import com.github.lehjr.mpsrecipecreator.basemod.config.Config;
import com.github.lehjr.mpsrecipecreator.block.RecipeWorkbench;
import com.github.lehjr.mpsrecipecreator.client.gui.MPARCContainer;
import com.github.lehjr.mpsrecipecreator.client.gui.MPARCGui;
import com.github.lehjr.mpsrecipecreator.network.NetHandler;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author lehjr
 */
@Mod(Constants.MOD_ID)
public final class MPS_RecipeCreator {

    public static final CreativeTab creativeTab = new CreativeTab();

    public MPS_RecipeCreator() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the setupClient method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);
        ConditionsJsonLoader.setFile();
    }

    private void setupClient(final FMLClientSetupEvent event) {
        ScreenManager.register(ModObjects.RECIPE_WORKBENCH_CONTAINER_TYPE, MPARCGui::new);
    }

    private void setup(final FMLCommonSetupEvent event) {
        NetHandler.registerMPALibPackets();
    }

    @SubscribeEvent
    public void registerContainerTypes(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(                // recipe creator gui
                new ContainerType<>(MPARCContainer::new)
                        .setRegistryName(Constants.RECIPE_WORKBENCH_TYPE__REG_NAME));
    }

    @SubscribeEvent
    public void registerBlock(RegistryEvent.Register<Block> blockRegistryEvent) {
        blockRegistryEvent.getRegistry().register(new RecipeWorkbench(Constants.RECIPE_WORKBENCH__REGNAME));
    }

    @SubscribeEvent
    public void registerItemBlock(RegistryEvent.Register<Item> itemBlockRegistryEvent) {
        itemBlockRegistryEvent.getRegistry().register(
                new BlockItem(ModObjects.recipeWorkBench,
                        new Item.Properties().tab(creativeTab))
                        .setRegistryName(new ResourceLocation(Constants.RECIPE_WORKBENCH__REGNAME)));
    }
}