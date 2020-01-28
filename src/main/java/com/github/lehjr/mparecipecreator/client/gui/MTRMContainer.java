package com.github.lehjr.mparecipecreator.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

/**
 * @author Dries007
 */
public class MTRMContainer extends Container {
    private static final int RETURN_SLOT_ID = 1 + (3 * 3) + (3 * 9) + 9;

    /**
     * The crafting matrix inventory (3x3).
     */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    public IInventory returnSlot = new InventoryCraftResult();

    public IInventory[] returnSlots = new InventoryCraftResult[10];

    public MTRMContainer(InventoryPlayer playerInventory) {
        playerInventory.player.openContainer = this;

        // crafting result
        this.addSlotToContainer(new Slot(this.craftResult, 0, 138, 48));

        // crafting grid
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlotToContainer(new Slot(this.craftMatrix, col + row * 3, 22 + col * 26, 21 + row * 26));
            }
        }

        // main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 99 + row * 18));
            }
        }

        // hotbar
        for (int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 157));
        }

        // return slot ??
        this.addSlotToContainer(new Slot(returnSlot, 0, -109, 143));

        for (int i = 0; i < 10; i++) {
            returnSlots[i] = new  InventoryCraftResult();
            this.addSlotToContainer(new Slot(returnSlots[i], 0, -109, 143));
        }
        this.onCraftMatrixChanged(this.craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory p_75130_1_) {
        super.onCraftMatrixChanged(p_75130_1_);
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }

    public int getReturnSlotId(int index) {
        return RETURN_SLOT_ID + index;
    }

    boolean isReturnSlot(int i) {
        return i > RETURN_SLOT_ID - 1 && i < RETURN_SLOT_ID - 9;
    }

    protected void retrySlotClick(int p_75133_1_, int p_75133_2_, boolean p_75133_3_, EntityPlayer player) {

    }

    @Override
    public ItemStack slotClick(int i, int mousebtn, ClickType clickTypeIn, EntityPlayer player) {
        ItemStack stack = ItemStack.EMPTY;
        if ((i >= 0 && i <= 9) || isReturnSlot(i))// Fake slots
        {
            if (mousebtn == 2) {
                getSlot(i).putStack(ItemStack.EMPTY);
            } else if (mousebtn == 0) {
                InventoryPlayer playerInv = player.inventory;
//                getSlot(i).onSlotChanged();
                ItemStack stackSlot = getSlot(i).getStack();
                ItemStack stackHeld = playerInv.getItemStack();

                if (!stackSlot.isEmpty()) {
                    stack = stackSlot.copy();
                }

                if (!stackHeld.isEmpty()) {
                    ItemStack newStack = stackHeld.copy();
                    if (!(i == 0 || (isReturnSlot(i)))) {
                        newStack.setCount(1);
                    }
                    getSlot(i).putStack(newStack);
                } else {
                    getSlot(i).putStack(ItemStack.EMPTY);
                }
            } else if (mousebtn == 1) {
                InventoryPlayer playerInv = player.inventory;
                getSlot(i).onSlotChanged();
                ItemStack stackSlot = getSlot(i).getStack();
                ItemStack stackHeld = playerInv.getItemStack();

                stack = stackSlot.copy();

                if (!stackHeld.isEmpty()) {
                    stackHeld = stackHeld.copy();
                    if (!stackSlot.isEmpty() && stackHeld.isItemEqual(stackSlot) && (i == 0 || (isReturnSlot(i)))) {
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
    public ItemStack transferStackInSlot(EntityPlayer player, int slots) {
        if (slots < 10 || (isReturnSlot(slots))) {
            inventorySlots.get(slots).putStack(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }
}
