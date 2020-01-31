package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.*;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableArrow;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lehjr
 */
public class SlotOptionsFrame extends ScrollableFrame {
    private ClickableLabel title;

    final int slotID;
    MTRMContainer container;

    private int lastOreId = 0;
    protected DrawableRect extSlotOptionsRect;

    final int spacer = 4;

    // text box
    public GuiTextField tokenTxt;

    private ClickableArrow prevOredict, nextOredict;

    // Slot specific
    private CheckBox useOreDict;

    public SlotOptionsFrame(Point2D topleft,
                            Point2D bottomright,
                            int slotID,
                            MTRMContainer container,
                            Colour backgroundColour,
                            Colour borderColour,
                            Colour arrowNormalBackGound,
                            Colour arrowHighlightedBackground,
                            Colour arrowBorderColour) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.slotID = slotID;
        this.container = container;

        Point2D starterPoint = this.getULFinal().copy().plus(4, 4);

        this.title = new ClickableLabel("Slot " + slotID + " Options", starterPoint.copy());
        title.setMode(0);

        extSlotOptionsRect = new DrawableRect(
                topleft,
                bottomright,
                backgroundColour,
                borderColour);

        // display for stack string in slot
        tokenTxt = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer,
                0,
                0, 0, 20);
        tokenTxt.setMaxStringLength(Integer.MAX_VALUE);

        int id = 0;

        nextOredict = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        nextOredict.setDrawShaft(false);

        prevOredict = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        prevOredict.setDrawShaft(false);
        prevOredict.setDirection(DrawableArrow.ArrowDirection.LEFT);


        useOreDict = new CheckBox(id++, starterPoint, "Use ore dictionary", true); //ID_OPTION_OREDICT
        useOreDict.setOnPressed(pressed->{
//            nextOreDict.setEnabled(useOreDict.isChecked());
        });






        useOreDict.setVisible(true);

        Point2D nextSliderPoint = new Point2D(extSlotOptionsRect.centerx(), extSlotOptionsRect.finalTop());

    }


    /**
     TODO: move text field up and use a single instance, always visible. Move frame down, make scrollable and use for showing prettified recipe
     */









    /**
     * Call this instead of the normal frame init since this sets up the stuff in the extra frame on the bottom
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param left2
     * @param top2
     * @param right2
     * @param bottom2
     */
    public void extInit(double left, double top, double right, double bottom, double left2, double top2, double right2, double bottom2) {
        this.init(left, top, right, bottom);
        this.extSlotOptionsRect.setTargetDimensions(left2, top2, right2, bottom2);

        tokenTxt.x = (int) (left2 + spacer);
        tokenTxt.y = (int) (top2 + spacer);
        tokenTxt.width = (int) (right2 - left2 - spacer * 2);

        Point2D nextSliderPoint = new Point2D(extSlotOptionsRect.centerx(), extSlotOptionsRect.finalTop());
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);

        // Slot-specific controls
        Point2D slotSpecificCol = this.getULFinal().plus(spacer, spacer);
        double nextLineSC = 0;

        title.setPosition(slotSpecificCol.plus(0,spacer));

        useOreDict.setPosition(slotSpecificCol.plus(0, nextLineSC+=10));

        prevOredict.setTargetDimensions(new Point2D(left + 93, top + 137), new Point2D(12, 17));
        nextOredict.setTargetDimensions(new Point2D(left + 38, top + 137), new Point2D(12, 17));

    }


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (isVisible()) {
            super.render(mouseX, mouseY, partialTicks);
            title.render(mouseX, mouseY, partialTicks);

            extSlotOptionsRect.draw();

            useOreDict.render(mouseX, mouseY, partialTicks);

            tokenTxt.drawTextBox();
        }
    }

    // FIXME: is this needed?
    public void setOptionsVisible(boolean visible) {

        System.out.println("visible: " + visible);

//        String token = messageSend.data[slotID];
//
//
//        if (token == null) {
//            tokenTxt.setText("empty");
//
////            useOreDict.setEnabled(slotID != 0);
////            useOreDict.setChecked(slotID != 0);
////            nextOreDict.setEnabled(slotID != 0);
////
////            for (CheckBox[] group : allGroups) {
////                for (CheckBox checkBox : group) {
////                    checkBox.setChecked(false);
////                    if (sliders.containsKey(checkBox)) {
////                        for (RangedSliderExt slider : sliders.get(checkBox)) {
////                            slider.minValue = 0;
////                            slider.maxValue = 0;
////                            slider.setValue(0);
////                        }
////                    }
////                }
////            }
//            container.getSlot(container.getReturnSlotId(slotID)).putStack(ItemStack.EMPTY);
//        } else {
//            tokenTxt.setText(token);
//
////            useOreDict.setEnabled(slotID != 0);
////            useOreDict.setChecked(ORE_DICT.matcher(token).find() && slotID != 0);
////            nextOreDict.setEnabled(useOreDict.isChecked() && slotID != 0);
//        }

        //------------------------------------------------------
        useOreDict.setVisible(visible);

        tokenTxt.setVisible(visible);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        useOreDict.setVisible(visible);
        if (visible) {
            if (useOreDict.isChecked()) {


            }
        }
    }

    /**
     * This entire section just gets the formatted string for display/use in the MineTweaker recipe.
     *
     * @param nextOreDict
     * @param stack
     * @return
     */
    public String getStackToken(boolean nextOreDict, boolean prevOreDict, ItemStack stack) {
        boolean oreDict = this.useOreDict.isChecked();
        if (stack.isEmpty()){
            return "empty";
        }
        if (stack.getItem().getRegistryName() == null) {
            throw new IllegalStateException("PLEASE REPORT: Item not empty, but getRegistryName null? Debug info: " + stack);
        }
        String stackName = stack.getItem().getRegistryName().toString();
        StringBuilder builder = new StringBuilder("<");
        if (oreDict) {
            int[] ids = OreDictionary.getOreIDs(stack);
            if (ids.length != 0) {
                stackName = "ore:" + OreDictionary.getOreName(ids[lastOreId]);
                if (nextOreDict) {
                    lastOreId++;
                }
                if (lastOreId >= ids.length) {
                    lastOreId = 0;
                }
            } else oreDict = false;
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

            if (useOreDict.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }

            if (nextOredict.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }

            if (prevOredict.mouseClicked(mouseX, mouseY, button)) {
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