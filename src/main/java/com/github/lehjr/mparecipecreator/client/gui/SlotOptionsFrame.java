package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.*;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableArrow;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author lehjr
 */
public class SlotOptionsFrame extends ScrollableFrame {
    private ClickableLabel title;

    int activeSlotID;
    MTRMContainer container;

    private int lastOreId = 0;
    final int spacer = 4;

    private ClickableArrow prevOreDictArrow, nextOreDictArrow;

    // Slot specific
    private CheckBox[] useOreDict = new CheckBox[10];
    GuiTextField tokenTxt;


    public SlotOptionsFrame(Point2D topleft,
                            Point2D bottomright,
                            GuiTextField tokenTxt,
                            MTRMContainer container,
                            Colour backgroundColour,
                            Colour borderColour,
                            Colour arrowNormalBackGound,
                            Colour arrowHighlightedBackground,
                            Colour arrowBorderColour) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.activeSlotID = activeSlotID;
        this.container = container;
        this.tokenTxt = tokenTxt;

        Point2D starterPoint = this.getULFinal().copy().plus(4, 4);

        this.title = new ClickableLabel("Slot Options", starterPoint.copy());
        title.setMode(0);

        nextOreDictArrow = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        nextOreDictArrow.setDrawShaft(false);
        nextOreDictArrow.setOnPressed(pressed-> {
            this.tokenTxt.setText(getStackToken(true, false, container.getSlot(activeSlotID).getStack()));
        });

        prevOreDictArrow = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        prevOreDictArrow.setDrawShaft(false);
        prevOreDictArrow.setDirection(DrawableArrow.ArrowDirection.LEFT);
        prevOreDictArrow.setOnPressed(pressed-> {
            this.tokenTxt.setText(getStackToken(false, true, container.getSlot(activeSlotID).getStack()));
        });

        activeSlotID = -1;
        reset();
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
        // Slot-specific controls
        Point2D slotSpecificCol = this.getULFinal().plus(spacer, spacer);
        double nextLineSC = 0;

        title.setPosition(slotSpecificCol.plus(0,spacer));

        nextLineSC+=10;
        for (int id = 0; id < 10; id ++) {
            useOreDict[id].setPosition(slotSpecificCol.plus(0, nextLineSC));
        }

        prevOreDictArrow.setTargetDimensions(new Point2D(left + 93, top + 137), new Point2D(12, 17));
        nextOreDictArrow.setTargetDimensions(new Point2D(left + 38, top + 137), new Point2D(12, 17));
    }


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (isVisible()) {
            super.render(mouseX, mouseY, partialTicks);
            title.render(mouseX, mouseY, partialTicks);
            for (int id = 0; id < 10; id ++) {
                useOreDict[id].render(mouseX, mouseY, partialTicks);
            }
        }
    }

    public void selectSlot(int slot) {
        this.activeSlotID = slot;
        this.tokenTxt.setText(getStackToken(false, false, container.getSlot(slot).getStack()));
        for (int id = 0; id < 10; id ++) {
            useOreDict[id].setVisible(id==slot);
        }
    }

    public void reset() {
        for (int id = 0; id < 10; id ++) {
            useOreDict[id] = new CheckBox(id, new Point2D(0,0), "Use ore dictionary", true); //ID_OPTION_OREDICT
            useOreDict[id].setOnPressed(pressed->{
                this.tokenTxt.setText(getStackToken(false, false, container.getSlot(activeSlotID).getStack()));
            });
            useOreDict[id].setVisible(false);
            useOreDict[id].setChecked(false);
        }
        activeSlotID = -1;
    }

    /**
     * This entire section just gets the formatted string for display/use in the MineTweaker recipe.
     *
     * @param nextOreDict
     * @param stack
     * @return
     */
    public String getStackToken(boolean nextOreDict, boolean prevOreDict, ItemStack stack) {
        if (stack.isEmpty()) {
            System.out.println("returning empty");

            return "empty";
        }

        System.out.println("made it here");


        if (stack.getItem().getRegistryName() == null) {
            throw new IllegalStateException("PLEASE REPORT: Item not empty, but getRegistryName null? Debug info: " + stack);
        }
        boolean oreDict = activeSlotID > -1 && activeSlotID < 11 && this.useOreDict[activeSlotID].isChecked();
        String stackName = stack.getItem().getRegistryName().toString();
        StringBuilder builder = new StringBuilder("<");
        if (oreDict) {
            int[] ids = OreDictionary.getOreIDs(stack);
            if (ids.length != 0) {
                stackName = "ore:" + OreDictionary.getOreName(ids[lastOreId]);
                if (nextOreDict) {
                    lastOreId++;
                } else if (prevOreDict) {
                    lastOreId--;
                }

                if (lastOreId >= ids.length || lastOreId < 0) {
                    lastOreId = 0;
                }

                nextOreDictArrow.setVisible(lastOreId > -1 && lastOreId + 1 < ids.length);
                prevOreDictArrow.setVisible(lastOreId > 0);

            } else {
                nextOreDictArrow.setVisible(false);
                prevOreDictArrow.setVisible(false);
            }
        }
        builder.append(stackName);
        builder.append('>');

        if (stack.getCount() > 1) {
            builder.append(" * ").append(stack.getCount());
        }

        return builder.toString();
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (isVisible()) {
            super.onMouseDown(mouseX, mouseY, button);

            for (int id = 0; id < 10; id ++) {
                if (useOreDict[id].mouseClicked(mouseX, mouseY, button)) {
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

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        if (isVisible()) {
            super.onMouseUp(mouseX, mouseY, button);
        }
        return false;
    }
}