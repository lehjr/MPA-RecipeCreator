package com.github.lehjr.mpsrecipecreator.client.gui;

import com.github.lehjr.mpsrecipecreator.basemod.ModObjects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MPARCContainer extends Container {
    /**
     * The crafting matrix inventory (3x3).
     */
    public CraftingInventory craftMatrix = new CraftingInventory(this, 3, 3);
    public CraftResultInventory craftResult = new CraftResultInventory();
    /** Position of the workbench */
    private final PlayerEntity player;
    private final IWorldPosCallable posCallable;

    public MPARCContainer(int windowID, PlayerInventory playerInventory) {
        this(windowID, playerInventory, IWorldPosCallable.NULL);
    }

    public MPARCContainer(int windowID, PlayerInventory playerInventory, IWorldPosCallable posCallable) {
        super(ModObjects.RECIPE_WORKBENCH_CONTAINER_TYPE, windowID);
        this.posCallable = posCallable;
        this.player = playerInventory.player;

        // crafting result
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true;
            }
        });

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

//        this.onCraftMatrixChanged(this.craftMatrix);
    }

    /**
     * Note only called if player is moving an itemstack through the dragging mechanics
     * @param slotIndex
     * @param mousebtn
     * @param clickTypeIn
     * @param player
     * @return
     */

    int slotChanged = -1;

    public int getSlotChanged() {
        int ret = slotChanged;
        slotChanged = -1;
        return ret;
    }

    @Override
    public ItemStack clicked(int slotIndex, int mousebtn, ClickType clickTypeIn, PlayerEntity player) {
        ItemStack stack = ItemStack.EMPTY;

        // handle crafting grid or result
        if ((slotIndex >= 0 && slotIndex <= 9)) {
            if (mousebtn == 1) {
                getSlot(slotIndex).set(ItemStack.EMPTY);
            } else if (mousebtn == 0) {
                PlayerInventory playerInv = player.inventory;
//                getSlot(i).onSlotChanged();
                ItemStack stackSlot = getSlot(slotIndex).getItem();
                ItemStack stackHeld = playerInv.getSelected();

                if (!stackSlot.isEmpty()) {
                    stack = stackSlot.copy();
                }

                if (!stackHeld.isEmpty()) {
                    ItemStack newStack = stackHeld.copy();
                    if (!(slotIndex == 0)) {
                        newStack.setCount(1);
                    }
                    getSlot(slotIndex).set(newStack);
                } else {
                    getSlot(slotIndex).set(ItemStack.EMPTY);
                }
            } else if (mousebtn == 1) {
                PlayerInventory playerInv = player.inventory;
                getSlot(slotIndex).setChanged();
                ItemStack stackSlot = getSlot(slotIndex).getItem();
                ItemStack stackHeld = playerInv.getSelected();

                stack = stackSlot.copy();

                if (!stackHeld.isEmpty()) {
                    stackHeld = stackHeld.copy();
                    if (!stackSlot.isEmpty() && stackHeld.sameItem(stackSlot) && (slotIndex == 0)) {
                        if (stackSlot.getCount() < stackSlot.getMaxStackSize()) stackSlot.grow(1);
                    } else {
                        stackSlot.setCount(1);
                    }
                    getSlot(slotIndex).set(stackSlot);
                } else {
                    if (!stackSlot.isEmpty()) {
                        stackSlot.shrink(1);
                        if (stackSlot.isEmpty()) {
                            getSlot(slotIndex).set(ItemStack.EMPTY);
                        }
                    }
                }
            }
        } else {
            stack = super.clicked(slotIndex, mousebtn, clickTypeIn, player);
        }
        return stack;
    }

    @Override
    public boolean stillValid(PlayerEntity playerEntity) {
        return true;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        if (index < 10) {
            slots.get(index).set(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }
}
