package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mparecipecreator.basemod.ModObjects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

/**
 * @author Dries007
 */
public class MTRMContainer extends Container {
    //    public class WorkbenchContainer extends RecipeBookContainer<CraftingInventory> {
    /**
     * The crafting matrix inventory (3x3).
     */
    public CraftingInventory craftMatrix = new CraftingInventory(this, 3, 3);
    public CraftResultInventory craftResult = new CraftResultInventory();
    /** Position of the workbench */
    private final PlayerEntity player;
    private final IWorldPosCallable posCallable;

    public MTRMContainer(int windowID, PlayerInventory playerInventory) {
        this(windowID, playerInventory, IWorldPosCallable.DUMMY);
    }

    public MTRMContainer(int windowID, PlayerInventory playerInventory, IWorldPosCallable posCallable) {
        super(ModObjects.RECIPE_WORKBENCH_CONTAINER_TYPE, windowID);
        this.posCallable = posCallable;
        this.player = playerInventory.player;

        // crafting result
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));

        // crafting grid
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new Slot(this.craftMatrix, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }

        // player inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // player hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public boolean canInteractWith(PlayerEntity p_75145_1_) {
        return true;
    }

    @Override
    public ItemStack slotClick(int i, int mousebtn, ClickType clickTypeIn, PlayerEntity player) {
        ItemStack stack = ItemStack.EMPTY;
        if ((i >= 0 && i <= 9)) {
            if (mousebtn == 2) {
                getSlot(i).putStack(ItemStack.EMPTY);
            } else if (mousebtn == 0) {
                PlayerInventory playerInv = player.inventory;
//                getSlot(i).onSlotChanged();
                ItemStack stackSlot = getSlot(i).getStack();
                ItemStack stackHeld = playerInv.getItemStack();

                if (!stackSlot.isEmpty()) {
                    stack = stackSlot.copy();
                }

                if (!stackHeld.isEmpty()) {
                    ItemStack newStack = stackHeld.copy();
                    if (!(i == 0)) {
                        newStack.setCount(1);
                    }
                    getSlot(i).putStack(newStack);
                } else {
                    getSlot(i).putStack(ItemStack.EMPTY);
                }
            } else if (mousebtn == 1) {
                PlayerInventory playerInv = player.inventory;
                getSlot(i).onSlotChanged();
                ItemStack stackSlot = getSlot(i).getStack();
                ItemStack stackHeld = playerInv.getItemStack();

                stack = stackSlot.copy();

                if (!stackHeld.isEmpty()) {
                    stackHeld = stackHeld.copy();
                    if (!stackSlot.isEmpty() && stackHeld.isItemEqual(stackSlot) && (i == 0)) {
                        if (stackSlot.getCount() < stackSlot.getMaxStackSize()) stackSlot.grow(1);
                    } else {
                        stackSlot.setCount(1);
                    }
                    getSlot(i).putStack(stackSlot);
                } else {
                    if (!stackSlot.isEmpty()) {
                        stackSlot.shrink(1);
                        if (stackSlot.isEmpty()) {
                            getSlot(i).putStack(ItemStack.EMPTY);
                        }
                    }
                }
            }
        } else {
            stack = super.slotClick(i, mousebtn, clickTypeIn, player);
        }
        return stack;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        if (index < 10) {
            inventorySlots.get(index).putStack(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }
}
