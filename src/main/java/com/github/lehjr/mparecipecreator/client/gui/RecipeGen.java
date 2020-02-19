package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.nbt.NBT2Json;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lehjr
 */
public class RecipeGen {
    // <Slot, oredict index>
    private Map<Integer, Integer> oreTagIndeces = new HashMap<>();
    MTRMContainer container;

    public RecipeGen(MTRMContainer containerIn) {
        this.container=containerIn;
    }

    public void reset() {
        oreTagIndeces = new HashMap<>();
    }

    /**
     * @param slot index
     * @return ItemStack from container slot
     */
    @Nonnull
    ItemStack getStack(int slot) {
        return container.getSlot(slot).getStack();
    }

    /**
     * Set the index of the tag list for the stack in the slot
     * @param slot
     * @param index
     * @return
     */
    public int setOreTagIndex(int slot, int index) {
        ItemStack stack = getStack(slot);
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            final ArrayList<ResourceLocation> ids = new ArrayList<>(ItemTags.getCollection().getOwningTags(item));
            if (!ids.isEmpty()) {
                if (!(index < ids.size())) {
                    index = 0;
                }
                oreTagIndeces.put(slot, index);
            }
        } else {
            index = -1;
        }
        return index;
    }

    /**
     * Gets the JsonObject representing the ItemStack.
     * Does not fetch oredict (now tags) data
     *
     * @param stack
     * @return
     */
    JsonObject getStackJson(@Nonnull ItemStack stack) {
        JsonObject stackJson = new JsonObject();
        if (!stack.isEmpty()) {
            // fail here, but not gracefully I guess
            if (stack.getItem().getRegistryName() == null) {
                throw new IllegalStateException("PLEASE REPORT: Item not empty, but getRegistryName null? Debug info: " + stack);
            }

            if (stack.hasTag()) {
                if (!stack.getItem().getRegistryName().toString().equals("forge:bucketfilled")) {
                    stackJson.addProperty("type", "minecraft:item_nbt");
                }
                stackJson.add("nbt", NBT2Json.CompoundNBT2Json(stack.getTag(), new JsonObject()));
            }
            stackJson.addProperty("item", stack.getItem().getRegistryName().toString());

            // set the stack count
            if (stack.getCount() > 1) {
                stackJson.addProperty("count", stack.getCount());
            }
        }
        return stackJson;
    }

    /**
     * @param slot
     * @return
     */
    public JsonObject getOreJson(int slot) {
        ItemStack stack = getStack(slot);
        if (slot == 0) {
            return getStackJson(stack);
        }
        JsonObject stackJson = new JsonObject();

        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            final ArrayList<ResourceLocation> ids = new ArrayList<>(ItemTags.getCollection().getOwningTags(item));
            if (!ids.isEmpty()) {
                int index = 0;
                if (oreTagIndeces.containsKey(slot)) {
                    index = oreTagIndeces.get(slot);
                }
                stackJson.addProperty("tag", ids.get(index).toString());
            }

            if (stackJson.size() == 0) {
                stackJson = getStackJson(stack);
            }

            // set the stack count
            if (stack.getCount() > 1) {
                stackJson.addProperty("count", stack.getCount());
            }
        }
        return stackJson;
    }
}
