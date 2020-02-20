package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.CheckBox;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableArrow;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableArrow;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.nbt.NBT2Json;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lehjr
 */
public class SlotOptionsFrame extends ScrollableFrame {
    private ClickableLabel title;
    int activeSlotID;
    MTRMContainer container;
    CheckBox useOreDictCheckbox;
    RecipeGen recipeGen;

    private Map<Integer, ResourceLocation> oreTags = new HashMap<>();
    final int spacer = 4;

    private ClickableArrow prevOreDictArrow, nextOreDictArrow;

    public SlotOptionsFrame(Point2D topleft,
                            Point2D bottomright,
                            RecipeGen recipeGenIn,
                            MTRMContainer container,
                            Colour backgroundColour,
                            Colour borderColour,
                            Colour arrowNormalBackGound,
                            Colour arrowHighlightedBackground,
                            Colour arrowBorderColour) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.container = container;
        this.recipeGen = recipeGenIn;

        Point2D starterPoint = this.getULFinal().copy().plus(4, 4);

        this.title = new ClickableLabel("Slot Options", starterPoint.copy());
        title.setMode(0);

        nextOreDictArrow = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        nextOreDictArrow.setDrawShaft(false);
        nextOreDictArrow.setOnPressed(pressed-> {
            this.recipeGen.setOreDictIndexForward(activeSlotID);
        });

        prevOreDictArrow = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        prevOreDictArrow.setDrawShaft(false);
        prevOreDictArrow.setDirection(DrawableArrow.ArrowDirection.LEFT);
        prevOreDictArrow.setOnPressed(pressed-> {
            this.recipeGen.setOreDictIndexReverse(activeSlotID);
        });

        activeSlotID = -1;

        useOreDictCheckbox = new CheckBox(1, new Point2D(0, 0), "Use ore dictionary", false);
        useOreDictCheckbox.disableAndHide();
        useOreDictCheckbox.setOnPressed(pressed -> {
            if (this.activeSlotID < 0) {
                recipeGen.useOredict.put(activeSlotID, useOreDictCheckbox.isChecked());
            }
        });

        reset();
    }

    public int getActiveSlotID() {
        return activeSlotID;
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
        // Slot-specific controls
        Point2D slotSpecificCol = this.getULFinal().plus(spacer, spacer);
        double nextLineSC = 0;

        title.setPosition(slotSpecificCol.plus(0,spacer));
        useOreDictCheckbox.setPosition(slotSpecificCol.plus(0, nextLineSC + 10));

        prevOreDictArrow.setTargetDimensions(new Point2D(left + 93, top + 137), new Point2D(12, 17));
        nextOreDictArrow.setTargetDimensions(new Point2D(left + 38, top + 137), new Point2D(12, 17));
    }

    @Override
    public void update(double mouseX, double mouseY) {
        super.update(mouseX, mouseY);
        if (activeSlotID < 1) {
            useOreDictCheckbox.disableAndHide();
        } else {
            ItemStack stack = container.getSlot(activeSlotID).getStack();
            if (stack.isEmpty()) {
                useOreDictCheckbox.disableAndHide();
                nextOreDictArrow.disableAndHide();
                prevOreDictArrow.disableAndHide();
            } else {
                Item item = stack.getItem();
                final ArrayList<ResourceLocation> ids = new ArrayList<>(ItemTags.getCollection().getOwningTags(item));
                if (!ids.isEmpty()) {
                    useOreDictCheckbox.enableAndShow();
                    useOreDictCheckbox.setChecked(recipeGen.useOredict.getOrDefault(activeSlotID, false));
                    if (recipeGen.useOredict.get(activeSlotID)) {
                        int oreIndex = recipeGen.getOreIndex(activeSlotID);
                        if (oreIndex < -1) {
                            if (oreIndex -1 < ids.size()) {
                                nextOreDictArrow.enableAndShow();
                            } else {
                                nextOreDictArrow.disableAndHide();
                            }
                            if (oreIndex > 0) {
                                prevOreDictArrow.enableAndShow();
                            } else {
                                prevOreDictArrow.disableAndHide();
                            }
                        }
                    } else {
                        useOreDictCheckbox.disableAndHide();
                        nextOreDictArrow.disableAndHide();
                        prevOreDictArrow.disableAndHide();
                    }
                }
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        if (isVisible()) {
            super.render(mouseX, mouseY, partialTicks);
            title.render(mouseX, mouseY, partialTicks);
            useOreDictCheckbox.render(mouseX, mouseY, partialTicks);
            nextOreDictArrow.render(mouseX, mouseY, partialTicks);
            prevOreDictArrow.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isVisible()) {
            super.mouseClicked(mouseX, mouseY, button);

            if (useOreDictCheckbox.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }

            if (nextOreDictArrow.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }

            if (prevOreDictArrow.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    public void selectSlot(int slot) {
        this.activeSlotID = slot;
        setLabel();
    }

    void setLabel() {
        this.title .setLabel("Slot " + (activeSlotID >=0 && activeSlotID <=10 ? activeSlotID + " " : "") + "Options");
    }

    public void reset() {
        useOreDictCheckbox.disableAndHide();
        activeSlotID = -1;
        setLabel();
    }
}