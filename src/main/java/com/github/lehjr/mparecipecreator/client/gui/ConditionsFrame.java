package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.CheckBox;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mparecipecreator.basemod.ConditionsJsonLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lehjr
 */
public class ConditionsFrame extends ScrollableFrame {
    Map<CheckBox, JsonObject> checkBoxList = new HashMap<>();

    public ConditionsFrame(Point2F topleft, Point2F bottomright, float zLevel, Colour backgroundColour, Colour borderColour) {
        super(topleft, bottomright, zLevel, backgroundColour, borderColour);
    }

    @Override
    public void init(float left, float top, float right, float bottom) {
        super.init(left, top, right, bottom);
        loadConditions();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        loadConditions();
    }

    public void loadConditions() {
        Point2F starterPoint = this.getULFinal().copy().plus(8, 14);
        if (checkBoxList.isEmpty()) {
            JsonArray jsonArray = ConditionsJsonLoader.getConditionsFromFile();
            if (jsonArray != null) {
                jsonArray.forEach(jsonElement -> {
                    if (jsonElement instanceof JsonObject) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        String condition = jsonObject.get("display_name").getAsString();
                        jsonObject.remove("display_name");

                        CheckBox checkbox = new CheckBox(
                                starterPoint.plus(0, checkBoxList.size() * 10),
                                condition, false);
                        checkBoxList.put(checkbox, jsonObject);
                    }
                });
            }
            // moves the checkboxes without recreating them so their state is preserved
        } else {
            int i =0;
            for (CheckBox checkBox : checkBoxList.keySet()) {
                checkBox.setPosition(starterPoint.plus(0, i * 10));
                i++;
            }
        }
        this.totalsize = checkBoxList.size() * 12;
    }

    /**
     * Note that the conditions aren't setup for multiple conditions at this time
     */
    public JsonArray getJsonArray() {
        JsonArray array = new JsonArray();
        for (CheckBox box : checkBoxList.keySet()) {
            if (box.isChecked()) {
                array.add(checkBoxList.get(box));
            }
        }
        return array;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.isEnabled() && this.isVisible()) {
            this.currentscrollpixels = Math.min(currentscrollpixels, getMaxScrollPixels());
            super.preRender(mouseX, mouseY, partialTicks);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0, -currentscrollpixels, 0);
            for (CheckBox checkBox : checkBoxList.keySet()) {
                checkBox.render(mouseX, mouseY, partialTicks, zLevel);
            }
            RenderSystem.popMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isEnabled() && this.isVisible()) {
            super.mouseClicked(mouseX, mouseY, button);

            for (CheckBox checkBox : checkBoxList.keySet()) {
                if (checkBox.mouseClicked(mouseX, mouseY + currentscrollpixels, button)) {
                    return true;
                }
            }
        }
        return false;
    }
}