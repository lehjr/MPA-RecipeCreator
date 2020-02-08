package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.*;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableArrow;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import org.json.JSONObject;

/**
 * @author lehjr
 */
public class SlotOptionsFrame extends ScrollableFrame {
    private ClickableLabel title;

    int activeSlotID;
    MTRMContainer container;

    private int oreIndex[] = new int[10];
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
            this.tokenTxt.setText(getStackToken(true, false, activeSlotID));
        });

        prevOreDictArrow = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        prevOreDictArrow.setDrawShaft(false);
        prevOreDictArrow.setDirection(DrawableArrow.ArrowDirection.LEFT);
        prevOreDictArrow.setOnPressed(pressed-> {
            this.tokenTxt.setText(getStackToken(false, true, activeSlotID));
        });

        activeSlotID = -1;
        for (int id = 0; id < 10; id ++) {
            useOreDict[id] = new CheckBox(id, new Point2D(0, 0), "Use ore dictionary", true); //ID_OPTION_OREDICT
            useOreDict[id].setOnPressed(pressed -> {
                this.tokenTxt.setText(getStackToken(false, false, activeSlotID));
            });
        }

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

    public void selectSlot(int slot) {
        this.activeSlotID = slot;
        this.tokenTxt.setText(getStackToken(false, false, slot));
        for (int id = 0; id < 10; id ++) {
            useOreDict[id].setVisible(id==slot);
        }
        setLabel();
    }

    void setLabel() {
        this.title .setLabel("Slot " + (activeSlotID >=0 && activeSlotID <=10 ? activeSlotID + " " : "") + "Options");
    }

    public void reset() {
        for (int id = 0; id < 10; id ++) {
            useOreDict[id].setVisible(false);
            useOreDict[id].setChecked(false);
            oreIndex[id] = 0;
        }
        activeSlotID = -1;
        setLabel();
    }

    ItemStack getStack(int slot) {
        return container.getSlot(slot).getStack();
    }

    /**
     *
     * @param slot
     * @return JSon representing the item stack in the given slot
     */
    public JSONObject getStackJson(int slot) {
        JSONObject jsonObject = new JSONObject();

        ItemStack stack = getStack(slot);
        if (!stack.isEmpty()) {
            boolean usingOredict = useOreDict[slot].isChecked();

            if (usingOredict) {
                int[] ids = OreDictionary.getOreIDs(stack);
                jsonObject.put("type", "forge:ore_dict");
                jsonObject.put("ore", OreDictionary.getOreName(ids[oreIndex[slot]]));
            } else {
                // fail here, but not gracefully I guess
                if (stack.getItem().getRegistryName() == null) {
                    throw new IllegalStateException("PLEASE REPORT: Item not empty, but getRegistryName null? Debug info: " + stack);
                }

                if (stack.hasTagCompound()) {
                    if (!stack.getItem().getRegistryName().toString().equals("forge:bucketfilled")) {
                        jsonObject.put("type", "minecraft:item_nbt");
                    }

                    String nbtString = stack.getTagCompound().toString();
                    System.out.println("nbtString: " + nbtString);

                    jsonObject.put("nbt", new JSONObject(nbtString));

                    // <modularpowerarmor:powerarmor_feet>.withTag({MMModItem: {render: {"mps_boots.boots2": {part: "boots2", model: "mps_boots"}, texSpec: {part: "feet", model: "default_armorskin"}, "mps_boots.boots1": {part: "boots1", model: "mps_boots"}, colours: [-1, -15642881] as int[]}}})




                }

                jsonObject.put("item", stack.getItem().getRegistryName().toString());
                if (stack.getHasSubtypes()) {
                    jsonObject.put("data", stack.getItemDamage());
                }
            }

            // set the stack count
            if (stack.getCount() > 1) {
                jsonObject.put("count", stack.getCount());
            }
        }
        return jsonObject;
    }

    /**
     * @param nextOreDict
     * @param prevOreDict
     * @param slot
     * @return the string for display in the text bar
     */
    public String getStackToken(boolean nextOreDict, boolean prevOreDict, int slot) {
        System.out.println("slot selected: " + slot);

        if (slot < 0 || slot > 10) {
            return "No slot selected";
        }

        ItemStack stack = getStack(slot);

        // return empty slot string
        if (stack.isEmpty()) {
            useOreDict[slot].disableAndHide();

            if (slot == activeSlotID) {
                nextOreDictArrow.disableAndHide();
                prevOreDictArrow.disableAndHide();
            }
            return "empty";
        }

        // fail here, but not gracefully I guess
        if (stack.getItem().getRegistryName() == null) {
            throw new IllegalStateException("PLEASE REPORT: Item not empty, but getRegistryName null? Debug info: " + stack);
        }

        if (slot == activeSlotID) {
            useOreDict[slot].enableAndShow();
        }

        boolean oreDict = activeSlotID > -1 && activeSlotID < 11 && this.useOreDict[slot].isChecked();

        String stackName = stack.getItem().getRegistryName().toString();
        StringBuilder builder = new StringBuilder();
        if (oreDict) {
            int[] ids = OreDictionary.getOreIDs(stack);
            if (ids.length != 0) {
                stackName = "ore:" + OreDictionary.getOreName(ids[oreIndex[slot]]);
                if (nextOreDict) {
                    oreIndex[slot]++;
                } else if (prevOreDict) {
                    oreIndex[slot]--;
                }

                if (oreIndex[slot] >= ids.length || oreIndex[slot] < 0) {
                    oreIndex[slot] = 0;
                }

                if (activeSlotID == slot) {
                    nextOreDictArrow.setVisible(oreIndex[slot] > -1 && oreIndex[slot] + 1 < ids.length);
                    prevOreDictArrow.setVisible(oreIndex[slot] > 0);
                }
            } else {
                nextOreDictArrow.setVisible(false);
                prevOreDictArrow.setVisible(false);
            }
        }
        builder.append(stackName);

        if (stack.getCount() > 1) {
            builder.append(" * ").append(stack.getCount());
        }

        return builder.toString();
    }
}