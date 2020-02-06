package com.github.lehjr.mparecipecreator.jei;

import com.github.lehjr.mparecipecreator.client.gui.MPARCGui;
import com.github.lehjr.mparecipecreator.client.gui.MTRMContainer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.gui.overlay.GuiProperties;
import net.minecraft.client.gui.inventory.GuiContainer;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		System.out.println("registering plugin");
		registry.addGuiScreenHandler(MPARCGui.class, MPARCGuiProperties::create);
		registry.getRecipeTransferRegistry()
				.addRecipeTransferHandler(
						MTRMContainer.class, // containerClass: the class of the container that this recipe transfer handler is for
						VanillaRecipeCategoryUid.CRAFTING, // recipeCategoryUid: the recipe categories that this container can use
						1, // recipeSlotStart: the first slot for recipe inputs
						9, // recipeSlotCount: the number of slots for recipe inputs
						10, // inventorySlotStart: the first slot of the available inventory (usually player inventory)
						36); // inventorySlotCount: the number of slots of the available inventory
	}
}