package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.CheckBox;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableArrow;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableArrow;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lehjr
 */
public class SlotOptionsFrame extends ScrollableFrame {
    private ClickableLabel title;
    int activeSlotID;
    MTRMContainer container;
    CheckBox[] useOreDictCheckbox = new CheckBox[9];
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

        for(int i=0; i < 9; i++) {
            useOreDictCheckbox[i] = new CheckBox(i+1, new Point2D(0, 0), "Use ore dictionary", false);
            useOreDictCheckbox[i].disableAndHide();
            useOreDictCheckbox[i].setOnPressed(pressed -> {
                if (getActiveSlotID() > 0) {
                    recipeGen.useOredict.put(getActiveSlotID(), useOreDictCheckbox[getActiveSlotID()-1].isChecked());
                }
            });
        }
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

        for(int i=0; i<9; i++) {
            useOreDictCheckbox[i].setPosition(slotSpecificCol.plus(4, nextLineSC + 18));
        }

        prevOreDictArrow.setTargetDimensions(new Point2D(right - 40, top + 8), new Point2D(12, 17));
        nextOreDictArrow.setTargetDimensions(new Point2D(right - 20, top + 8), new Point2D(12, 17));
    }

    @Override
    public void update(double mouseX, double mouseY) {
        super.update(mouseX, mouseY);

        for(int i=0; i< 9; i++) {
            if (activeSlotID >= 1 && i + 1 == activeSlotID) {
                continue;
            }
            useOreDictCheckbox[i].disableAndHide();

            if (!container.getSlot(i+1).getHasStack()) {
                useOreDictCheckbox[i].setChecked(false);
            }
        }

        if (activeSlotID >= 1) {
            ItemStack stack = container.getSlot(activeSlotID).getStack();
            if (stack.isEmpty()) {
                useOreDictCheckbox[activeSlotID -1].setChecked(false);
                useOreDictCheckbox[activeSlotID -1].disableAndHide();
                nextOreDictArrow.disableAndHide();
                prevOreDictArrow.disableAndHide();
            } else {
                Item item = stack.getItem();
                final ArrayList<ResourceLocation> ids = new ArrayList<>(ItemTags.getCollection().getOwningTags(item));
                if (!ids.isEmpty()) {
                    useOreDictCheckbox[activeSlotID -1].enableAndShow();
                    int oreIndex = recipeGen.getOreIndex(activeSlotID);

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
                } else {
                    useOreDictCheckbox[activeSlotID -1].disableAndHide();
                    nextOreDictArrow.disableAndHide();
                    prevOreDictArrow.disableAndHide();
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
            for (int i =0; i < 9; i++) {
                useOreDictCheckbox[i].render(mouseX, mouseY, partialTicks);
            }
            nextOreDictArrow.render(mouseX, mouseY, partialTicks);
            prevOreDictArrow.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isVisible()) {
            super.mouseClicked(mouseX, mouseY, button);
            for (int i =0; i < 9; i++) {
                if (useOreDictCheckbox[i].mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
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
        for(int i=0; i<9; i++) {
            useOreDictCheckbox[i].disableAndHide();
        }

        activeSlotID = -1;
        setLabel();
    }
}