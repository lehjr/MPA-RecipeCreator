package com.github.lehjr.mparecipecreator.basemod;

import com.github.lehjr.mparecipecreator.block.RecipeWorkbench;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author lehjr
 */
@Mod(Constants.MOD_ID)
public final class MPA_RecipeCreator {

    public static final CreativeTab creativeTab = new CreativeTab();

    public MPA_RecipeCreator() {

        System.out.println("doing something here");

//        // Register ourselves for server, registry and other game events we are interested in
//        MinecraftForge.EVENT_BUS.register(this);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);

    }







//    @Mod.EventBusSubscriber
//    public static class RegisterStuff {
        @SubscribeEvent
        public void registerBlock(RegistryEvent.Register<Block> blockRegistryEvent) {
            System.out.println("doing something here");

            blockRegistryEvent.getRegistry().register(new RecipeWorkbench(Constants.RECIPE_WORKBENCH__REGNAME));
        }

        @SubscribeEvent
        public void registerItemBlock(RegistryEvent.Register<Item> itemBlockRegistryEvent) {
            System.out.println("doing something here");
            itemBlockRegistryEvent.getRegistry().register(
                    new BlockItem(ModObjects.recipeWorkBench,
                            new Item.Properties().group(creativeTab))
                            .setRegistryName(new ResourceLocation(Constants.RECIPE_WORKBENCH__REGNAME)));
        }
//    }
}