package com.github.lehjr.mpsrecipecreator.container;

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


    /**
     * TODO: fix so that the held itemstack isn't used up
     * @param slotIndex
     * @param mouseButton
     * @param clickType
     * @param player
     * @return
     */



    @Override
    public ItemStack doClick(int slotIndex, int mouseButton, ClickType clickType, PlayerEntity player) {
        System.out.println("slot: " + slotIndex + " clicktype: " + clickType);

        ItemStack itemstack = ItemStack.EMPTY;
        PlayerInventory playerinventory = player.inventory;
        if (clickType == ClickType.QUICK_CRAFT) {
            System.out.println("doing something here");

            int i1 = this.quickcraftStatus;
            this.quickcraftStatus = getQuickcraftHeader(mouseButton);
            if ((i1 != 1 || this.quickcraftStatus != 2) && i1 != this.quickcraftStatus) {
                System.out.println("doing something here");
                this.resetQuickCraft();
            } else if (playerinventory.getCarried().isEmpty()) {
                System.out.println("doing something here");
                this.resetQuickCraft();
            } else if (this.quickcraftStatus == 0) {
                System.out.println("doing something here");
                this.quickcraftType = getQuickcraftType(mouseButton);
                if (isValidQuickcraftType(this.quickcraftType, player)) {
                    System.out.println("doing something here");
                    this.quickcraftStatus = 1;
                    this.quickcraftSlots.clear();
                } else {
                    System.out.println("doing something here");
                    this.resetQuickCraft();
                }
            } else if (this.quickcraftStatus == 1) {
                System.out.println("doing something here");
                Slot slot7 = this.slots.get(slotIndex);
                ItemStack itemstack12 = playerinventory.getCarried();
                if (slot7 != null && canItemQuickReplace(slot7, itemstack12, true) && slot7.mayPlace(itemstack12) && (this.quickcraftType == 2 || itemstack12.getCount() > this.quickcraftSlots.size()) && this.canDragTo(slot7)) {
                    System.out.println("doing something here");
                    this.quickcraftSlots.add(slot7);
                }
            } else if (this.quickcraftStatus == 2) {
                System.out.println("doing something here");
                if (!this.quickcraftSlots.isEmpty()) {
                    System.out.println("doing something here");
                    ItemStack itemstack10 = playerinventory.getCarried().copy();
                    int k1 = playerinventory.getCarried().getCount();

                    for(Slot slot8 : this.quickcraftSlots) {
                        ItemStack itemstack13 = playerinventory.getCarried();
                        if (slot8 != null && canItemQuickReplace(slot8, itemstack13, true) && slot8.mayPlace(itemstack13) && (this.quickcraftType == 2 || itemstack13.getCount() >= this.quickcraftSlots.size()) && this.canDragTo(slot8)) {
                            System.out.println("doing something here");
                            ItemStack itemstack14 = itemstack10.copy();
                            int j3 = slot8.hasItem() ? slot8.getItem().getCount() : 0;
                            getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, itemstack14, j3);
                            int k3 = Math.min(itemstack14.getMaxStackSize(), slot8.getMaxStackSize(itemstack14));
                            if (itemstack14.getCount() > k3) {
                                System.out.println("doing something here");
                                itemstack14.setCount(k3);
                            }

                            k1 -= itemstack14.getCount() - j3;
                            slot8.set(itemstack14);
                        }
                    }

                    itemstack10.setCount(k1);
                    playerinventory.setCarried(itemstack10);
                }

                this.resetQuickCraft();
            } else {
                System.out.println("doing something here");
                this.resetQuickCraft();
            }
        } else if (this.quickcraftStatus != 0) {
            System.out.println("doing something here");

            this.resetQuickCraft();
        } else if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && (mouseButton == 0 || mouseButton == 1)) {
            System.out.println("doing something here");
            if (slotIndex == -999) {
                if (!playerinventory.getCarried().isEmpty()) {
                    if (mouseButton == 0) {
                        System.out.println("doing something here");
                        player.drop(playerinventory.getCarried(), true);
                        playerinventory.setCarried(ItemStack.EMPTY);
                    }

                    if (mouseButton == 1) {
                        System.out.println("doing something here");
                        player.drop(playerinventory.getCarried().split(1), true);
                    }
                }
            } else if (clickType == ClickType.QUICK_MOVE) {
                System.out.println("doing something here");

                if (slotIndex < 0) {
                    System.out.println("doing something here");
                    return ItemStack.EMPTY;
                }

                Slot slot5 = this.slots.get(slotIndex);
                if (slot5 == null || !slot5.mayPickup(player)) {
                    System.out.println("doing something here");
                    return ItemStack.EMPTY;
                }

                for(ItemStack itemstack8 = this.quickMoveStack(player, slotIndex); !itemstack8.isEmpty() && ItemStack.isSame(slot5.getItem(), itemstack8); itemstack8 = this.quickMoveStack(player, slotIndex)) {
                    itemstack = itemstack8.copy();
                }
            } else {
                System.out.println("doing something here");

                if (slotIndex < 0) {
                    System.out.println("doing something here");
                    return ItemStack.EMPTY;
                }

                Slot slot6 = this.slots.get(slotIndex);
                if (slot6 != null) {
                    System.out.println("doing something here");
                    ItemStack itemstack9 = slot6.getItem();
                    ItemStack itemstack11 = playerinventory.getCarried();
                    if (!itemstack9.isEmpty()) {
                        itemstack = itemstack9.copy();
                        System.out.println("doing something here");
                    }

                    if (itemstack9.isEmpty()) {
                        System.out.println("doing something here");
                        if (!itemstack11.isEmpty() && slot6.mayPlace(itemstack11)) {
                            System.out.println("doing something here");
                            int j2 = mouseButton == 0 ? itemstack11.getCount() : 1;
                            if (j2 > slot6.getMaxStackSize(itemstack11)) {
                                System.out.println("doing something here");
                                j2 = slot6.getMaxStackSize(itemstack11);
                            }

                            slot6.set(itemstack11.split(j2));
                        }
                    } else if (slot6.mayPickup(player)) {
                        if (itemstack11.isEmpty()) {
                            if (itemstack9.isEmpty()) {
                                System.out.println("doing something here");
                                slot6.set(ItemStack.EMPTY);
                                playerinventory.setCarried(ItemStack.EMPTY);
                            } else {
                                System.out.println("doing something here");
                                int k2 = mouseButton == 0 ? itemstack9.getCount() : (itemstack9.getCount() + 1) / 2;
                                playerinventory.setCarried(slot6.remove(k2));
                                if (itemstack9.isEmpty()) {
                                    System.out.println("doing something here");
                                    slot6.set(ItemStack.EMPTY);
                                }

                                slot6.onTake(player, playerinventory.getCarried());
                            }
                        } else if (slot6.mayPlace(itemstack11)) {
                            System.out.println("doing something here");
                            if (consideredTheSameItem(itemstack9, itemstack11)) {
                                System.out.println("doing something here");
                                int l2 = mouseButton == 0 ? itemstack11.getCount() : 1;
                                if (l2 > slot6.getMaxStackSize(itemstack11) - itemstack9.getCount()) {
                                    System.out.println("doing something here");
                                    l2 = slot6.getMaxStackSize(itemstack11) - itemstack9.getCount();
                                }

                                if (l2 > itemstack11.getMaxStackSize() - itemstack9.getCount()) {
                                    System.out.println("doing something here");
                                    l2 = itemstack11.getMaxStackSize() - itemstack9.getCount();
                                }

                                itemstack11.shrink(l2);
                                itemstack9.grow(l2);
                            } else if (itemstack11.getCount() <= slot6.getMaxStackSize(itemstack11)) {
                                System.out.println("doing something here");
                                slot6.set(itemstack11);
                                playerinventory.setCarried(itemstack9);
                            }
                        } else if (itemstack11.getMaxStackSize() > 1 && consideredTheSameItem(itemstack9, itemstack11) && !itemstack9.isEmpty()) {
                            int i3 = itemstack9.getCount();
                            if (i3 + itemstack11.getCount() <= itemstack11.getMaxStackSize()) {
                                System.out.println("doing something here");
                                itemstack11.grow(i3);
                                itemstack9 = slot6.remove(i3);
                                if (itemstack9.isEmpty()) {
                                    System.out.println("doing something here");
                                    slot6.set(ItemStack.EMPTY);
                                }

                                slot6.onTake(player, playerinventory.getCarried());
                            }
                        }
                    }

                    slot6.setChanged();
                }
            }
        } else if (clickType == ClickType.SWAP) {
            System.out.println("doing something here");
            Slot slot = this.slots.get(slotIndex);
            ItemStack itemstack1 = playerinventory.getItem(mouseButton);
            ItemStack itemstack2 = slot.getItem();
            if (!itemstack1.isEmpty() || !itemstack2.isEmpty()) {
                System.out.println("doing something here");
                if (itemstack1.isEmpty()) {
                    System.out.println("doing something here");
                    if (slot.mayPickup(player)) {
                        System.out.println("doing something here");
                        playerinventory.setItem(mouseButton, itemstack2);
//                        slot.onSwapCraft(itemstack2.getCount());
                        SlotSwapCraft.slotSwapCraft(slot, itemstack2.getCount());
                        slot.set(ItemStack.EMPTY);
                        slot.onTake(player, itemstack2);
                    }
                } else if (itemstack2.isEmpty()) {
                    System.out.println("doing something here");
                    if (slot.mayPlace(itemstack1)) {
                        System.out.println("doing something here");
                        int i = slot.getMaxStackSize(itemstack1);
                        if (itemstack1.getCount() > i) {
                            System.out.println("doing something here");
                            slot.set(itemstack1.split(i));
                        } else {
                            System.out.println("doing something here");
                            slot.set(itemstack1);
                            playerinventory.setItem(mouseButton, ItemStack.EMPTY);
                        }
                    }
                } else if (slot.mayPickup(player) && slot.mayPlace(itemstack1)) {
                    System.out.println("doing something here");
                    int l1 = slot.getMaxStackSize(itemstack1);
                    if (itemstack1.getCount() > l1) {
                        System.out.println("doing something here");
                        slot.set(itemstack1.split(l1));
                        slot.onTake(player, itemstack2);
                        if (!playerinventory.add(itemstack2)) {
                            System.out.println("doing something here");
                            player.drop(itemstack2, true);
                        }
                    } else {
                        System.out.println("doing something here");
                        slot.set(itemstack1);
                        playerinventory.setItem(mouseButton, itemstack2);
                        slot.onTake(player, itemstack2);
                    }
                }
            }
        } else if (clickType == ClickType.CLONE && player.abilities.instabuild && playerinventory.getCarried().isEmpty() && slotIndex >= 0) {
            System.out.println("doing something here");
            Slot slot4 = this.slots.get(slotIndex);
            if (slot4 != null && slot4.hasItem()) {
                System.out.println("doing something here");
                ItemStack itemstack7 = slot4.getItem().copy();
                itemstack7.setCount(itemstack7.getMaxStackSize());
                playerinventory.setCarried(itemstack7);
            }
        } else if (clickType == ClickType.THROW && playerinventory.getCarried().isEmpty() && slotIndex >= 0) {
            System.out.println("doing something here");
            Slot slot3 = this.slots.get(slotIndex);
            if (slot3 != null && slot3.hasItem() && slot3.mayPickup(player)) {
                System.out.println("doing something here");
                ItemStack itemstack6 = slot3.remove(mouseButton == 0 ? 1 : slot3.getItem().getCount());
                slot3.onTake(player, itemstack6);
                player.drop(itemstack6, true);
            }
        } else if (clickType == ClickType.PICKUP_ALL && slotIndex >= 0) {
            System.out.println("doing something here");
            Slot slot2 = this.slots.get(slotIndex);
            ItemStack itemstack5 = playerinventory.getCarried();
            if (!itemstack5.isEmpty() && (slot2 == null || !slot2.hasItem() || !slot2.mayPickup(player))) {
                System.out.println("doing something here");
                int j1 = mouseButton == 0 ? 0 : this.slots.size() - 1;
                int i2 = mouseButton == 0 ? 1 : -1;

                for(int j = 0; j < 2; ++j) {
                    for(int k = j1; k >= 0 && k < this.slots.size() && itemstack5.getCount() < itemstack5.getMaxStackSize(); k += i2) {
                        Slot slot1 = this.slots.get(k);
                        if (slot1.hasItem() && canItemQuickReplace(slot1, itemstack5, true) && slot1.mayPickup(player) && this.canTakeItemForPickAll(itemstack5, slot1)) {
                            System.out.println("doing something here");
                            ItemStack itemstack3 = slot1.getItem();
                            if (j != 0 || itemstack3.getCount() != itemstack3.getMaxStackSize()) {
                                System.out.println("doing something here");
                                int l = Math.min(itemstack5.getMaxStackSize() - itemstack5.getCount(), itemstack3.getCount());
                                ItemStack itemstack4 = slot1.remove(l);
                                itemstack5.grow(l);
                                if (itemstack4.isEmpty()) {
                                    System.out.println("doing something here");
                                    slot1.set(ItemStack.EMPTY);
                                }

                                slot1.onTake(player, itemstack4);
                            }
                        }
                    }
                }
            }

            this.broadcastChanges();
        }

        return itemstack;
    }

//    @Override
//    public ItemStack clicked(int slotIndex, int mousebtn, ClickType clickTypeIn, PlayerEntity player) {
//
//        System.out.println("slot: " + slotIndex + " clicktype: " + clickTypeIn);
//        ItemStack stack = ItemStack.EMPTY;
//
//        // handle crafting grid or result
//        if ((slotIndex >= 0 && slotIndex <= 9)) {
//            if (mousebtn == 1) {
//                getSlot(slotIndex).set(ItemStack.EMPTY);
//            } else if (mousebtn == 0) {
//                PlayerInventory playerInv = player.inventory;
////                getSlot(i).onSlotChanged();
//                ItemStack stackSlot = getSlot(slotIndex).getItem();
//                ItemStack stackHeld = playerInv.getSelected();
//
//                System.out.println("stackSlot:  " + stackSlot);
//                System.out.println("stackHeld: " + stackHeld);
//
//                if (!stackSlot.isEmpty()) {
//                    stack = stackSlot.copy();
//                }
//
//                if (!stackHeld.isEmpty()) {
//                    ItemStack newStack = stackHeld.copy();
//                    if (!(slotIndex == 0)) {
//                        newStack.setCount(1);
//                    }
//                    getSlot(slotIndex).set(newStack);
//                } else {
//                    getSlot(slotIndex).set(ItemStack.EMPTY);
//                }
//            } else if (mousebtn == 1) {
//                PlayerInventory playerInv = player.inventory;
//                getSlot(slotIndex).setChanged();
//                ItemStack stackSlot = getSlot(slotIndex).getItem();
//                ItemStack stackHeld = playerInv.getSelected();
//
//                System.out.println("stackSlot:  " + stackSlot);
//                System.out.println("stackHeld: " + stackHeld);
//
//                stack = stackSlot.copy();
//
//                if (!stackHeld.isEmpty()) {
//                    stackHeld = stackHeld.copy();
//                    if (!stackSlot.isEmpty() && stackHeld.sameItem(stackSlot) && (slotIndex == 0)) {
//                        if (stackSlot.getCount() < stackSlot.getMaxStackSize()) stackSlot.grow(1);
//                    } else {
//                        stackSlot.setCount(1);
//                    }
//                    getSlot(slotIndex).set(stackSlot);
//                } else {
//                    if (!stackSlot.isEmpty()) {
//                        stackSlot.shrink(1);
//                        if (stackSlot.isEmpty()) {
//                            getSlot(slotIndex).set(ItemStack.EMPTY);
//                        }
//                    }
//                }
//            }
//        } else {
//            stack = super.clicked(slotIndex, mousebtn, clickTypeIn, player);
//        }
//
//        System.out.println("stack: " + stack);
//
//        return stack;
//    }

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
