package com.github.lehjr.mparecipecreator.basemod;

import com.github.lehjr.mparecipecreator.block.RecipeWorkbench;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author lehjr
 */
@Mod(Constants.MOD_ID)
public final class MPA_RecipeCreator {

    public static final CreativeTab creativeTab = new CreativeTab();

    public MPA_RecipeCreator() {



        // Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }







//    @Mod.EventBusSubscriber
//    public static class RegisterStuff {
        @SubscribeEvent
        public void registerBlock(final RegistryEvent.Register<Block> blockRegistryEvent) {
            blockRegistryEvent.getRegistry().register(new RecipeWorkbench(Constants.RECIPE_WORKBENCH__REGNAME));
        }

        @SubscribeEvent
        public void registerItemBlock(final RegistryEvent.Register<Item> itemBlockRegistryEvent) {
            itemBlockRegistryEvent.getRegistry().register(
                    new BlockItem(ModObjects.recipeWorkBench,
                            new Item.Properties().group(creativeTab))
                            .setRegistryName(new ResourceLocation(Constants.RECIPE_WORKBENCH__REGNAME)));
        }
//    }
}