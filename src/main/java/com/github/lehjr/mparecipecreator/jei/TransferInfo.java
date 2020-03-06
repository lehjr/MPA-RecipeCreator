package com.github.lehjr.mparecipecreator.jei;

import com.github.lehjr.mparecipecreator.client.gui.MPARCContainer;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import static mezz.jei.api.constants.VanillaRecipeCategoryUid.CRAFTING;

public class TransferInfo implements IRecipeTransferInfo<MPARCContainer> {
    @Override
    public Class<MPARCContainer> getContainerClass() {
        return MPARCContainer.class;
    }

    @Override
    public ResourceLocation getRecipeCategoryUid() {
        return CRAFTING;
    }

    @Override
    public boolean canHandle(MPARCContainer mparcContainer) {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(MPARCContainer mparcContainer) {
        return mparcContainer.inventorySlots.subList(1, 10);
    }

    @Override
    public List<Slot> getInventorySlots(MPARCContainer mparcContainer) {
        return mparcContainer.inventorySlots.subList(10, mparcContainer.inventorySlots.size() -1);
    }
}
