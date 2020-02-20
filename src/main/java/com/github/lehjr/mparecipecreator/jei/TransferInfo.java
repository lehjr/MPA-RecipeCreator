package com.github.lehjr.mparecipecreator.jei;

import com.github.lehjr.mparecipecreator.client.gui.MTRMContainer;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import static mezz.jei.api.constants.VanillaRecipeCategoryUid.CRAFTING;

public class TransferInfo implements IRecipeTransferInfo<MTRMContainer> {
    @Override
    public Class<MTRMContainer> getContainerClass() {
        return MTRMContainer.class;
    }

    @Override
    public ResourceLocation getRecipeCategoryUid() {
        return CRAFTING;
    }

    @Override
    public boolean canHandle(MTRMContainer mtrmContainer) {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(MTRMContainer mtrmContainer) {
        return mtrmContainer.inventorySlots.subList(1, 10);
    }

    @Override
    public List<Slot> getInventorySlots(MTRMContainer mtrmContainer) {
        return mtrmContainer.inventorySlots.subList(10, mtrmContainer.inventorySlots.size() -1);
    }
}
