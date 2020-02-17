package com.github.lehjr.mparecipecreator.jei;

import com.github.lehjr.mparecipecreator.client.gui.MTRMContainer;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class TransferInfo implements IRecipeTransferInfo<MTRMContainer> {
    final ResourceLocation location = new ResourceLocation(ModIds.MINECRAFT_ID, "crafting");

    @Override
    public Class<MTRMContainer> getContainerClass() {
        return MTRMContainer.class;
    }

    @Override
    public ResourceLocation getRecipeCategoryUid() {
        return location;
    }

    @Override
    public boolean canHandle(MTRMContainer mtrmContainer) {
        return false;
    }

    @Override
    public List<Slot> getRecipeSlots(MTRMContainer mtrmContainer) {
        List<Slot> slots = new ArrayList<>();
        for (int i=1; i < 10; i++) {
            slots.add(mtrmContainer.getSlot(i));
        }
        return slots;
    }

    @Override
    public List<Slot> getInventorySlots(MTRMContainer mtrmContainer) {
        List<Slot> slots = new ArrayList<>();
        for (int i=10; i < mtrmContainer.inventorySlots.size(); i++) {
            slots.add(mtrmContainer.getSlot(i));
        }
        return slots;
    }
}
