package com.github.lehjr.mparecipecreator.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Dries007
 */
public class MTRMContainer extends Container {
    /**
     * The crafting matrix inventory (3x3).
     */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public InventoryCraftResult craftResult = new InventoryCraftResult();
    private final World world;
    /** Position of the workbench */
    private final BlockPos pos;
    private final EntityPlayer player;


    public MTRMContainer(InventoryPlayer playerInventory) {
        playerInventory.player.openContainer = this;
        this.player = playerInventory.player;
        this.world = player.world;
        this.pos = player.getPosition();

        // crafting result
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));

        // crafting grid
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlotToContainer(new Slot(this.craftMatrix, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }

        // player inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // player hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
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


    protected void retrySlotClick(int p_75133_1_, int p_75133_2_, boolean p_75133_3_, EntityPlayer player) {

    }

    @Override
    public ItemStack slotClick(int i, int mousebtn, ClickType clickTypeIn, EntityPlayer player) {
        ItemStack stack = ItemStack.EMPTY;
        if ((i >= 0 && i <= 9)) {
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
                    if (!(i == 0)) {
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
    public ItemStack transferStackInSlot(EntityPlayer player, int slots) {
        if (slots < 10) {
            inventorySlots.get(slots).putStack(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }
}
