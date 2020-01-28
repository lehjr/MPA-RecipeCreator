package com.github.lehjr.mparecipecreator.basemod;

import com.github.lehjr.mparecipecreator.block.RecipeWorkbench;
import com.github.lehjr.mparecipecreator.network.MessageResponse;
import com.github.lehjr.mparecipecreator.network.MessageSend;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * @author lehjr
 */
@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION,
        dependencies = "required-after:mpalib@[@mpalib_version@,), required-after:modularpowerarmor@[1.0.0,)")
public enum MPA_RecipeCreator {
    INSTANCE;

    public static final CreativeTab creativeTab = new CreativeTab();

    @Nonnull
    @Mod.InstanceFactory
    public static MPA_RecipeCreator getInstance() {
        return INSTANCE;
    }

    private SimpleNetworkWrapper snw;
    public static SimpleNetworkWrapper getSnw() {
        return getInstance().snw;
    }


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiIhandler());

        int id = 0;
        snw = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MODID);
        snw.registerMessage(MessageSend.Handler.class, MessageSend.class, id++, Side.SERVER);
        snw.registerMessage(MessageResponse.Handler.class, MessageResponse.class, id++, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(new RegisterModel());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    public static class RegisterModel {
        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void registerRenderers(ModelRegistryEvent event) {
            Item item = Item.getItemFromBlock(ModObjects.recipeWorkBench);
            ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Constants.RECIPE_WORKBENCH__REGNAME, "inventory");
            ModelLoader.setCustomModelResourceLocation(item, 0, itemModelResourceLocation);
        }
    }

    @Mod.EventBusSubscriber
    public static class RegisterStuff {
        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent) {
            itemRegistryEvent.getRegistry().register(
                    new ItemBlock(ModObjects.INSTANCE.recipeWorkBench).setRegistryName(new ResourceLocation(Constants.RECIPE_WORKBENCH__REGNAME)));
        }

        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> blockRegistryEvent) {
            blockRegistryEvent.getRegistry().register(new RecipeWorkbench( new ResourceLocation(Constants.RECIPE_WORKBENCH__REGNAME)));
        }
    }
}