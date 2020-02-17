package com.github.lehjr.mparecipecreator.jei;

import com.github.lehjr.mparecipecreator.client.gui.MPARCGui;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return null;
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(new TransferInfo());
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiScreenHandler(MPARCGui.class, new ScreenHandler());
	}

//	@Override
//	public void register(IModRegistry registry) {
//		System.out.println("registering plugin");
//		registry.addGuiScreenHandler(MPARCGui.class, MPARCGuiProperties::create);
//		registry.getRecipeTransferRegistry()
//				.addRecipeTransferHandler(
//						MTRMContainer.class, // containerClass: the class of the container that this recipe transfer handler is for
//						VanillaRecipeCategoryUid.CRAFTING, // recipeCategoryUid: the recipe categories that this container can use
//						1, // recipeSlotStart: the first slot for recipe inputs
//						9, // recipeSlotCount: the number of slots for recipe inputs
//						10, // inventorySlotStart: the first slot of the available inventory (usually player inventory)
//						36); // inventorySlotCount: the number of slots of the available inventory
//	}
}