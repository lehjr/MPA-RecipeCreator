package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.CheckBox;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mparecipecreator.basemod.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lehjr
 */
public class ConditionsFrame extends ScrollableFrame {
    Map<String, CheckBox> checkBoxList = new HashMap<>();
    //    JsonObject conditions = null;
    public ConditionsFrame(Point2D topleft, Point2D bottomright, Colour backgroundColour, Colour borderColour) {
        super(topleft, bottomright, backgroundColour, borderColour);
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
        loadConditions();
    }

//    public void setConditions(JsonObject conditionsIn) {
//        this.conditions = conditionsIn;
//    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        loadConditions();
    }

    public void loadConditions() {
        Point2D starterPoint = this.getULFinal().copy().plus(4, 4);
//        try {
//            if (this.conditions != null && this.conditions.size() > 0) {
//                if (checkBoxList.isEmpty()) {
//                    JsonObject jobject = conditions.getAsJsonObject("conditions");
//
//                    for (Map.Entry<String, JsonElement> entry : jobject.entrySet()) {
//                        String key = entry.getKey();
//                        CheckBox checkbox = new CheckBox(checkBoxList.size(), starterPoint.plus(0, checkBoxList.size() * 10), key, false);
//                        checkbox.setOnPressed(press-> toggleCheckboxes(checkbox.getId()));
//                        checkBoxList.put(key, checkbox);
//                    }
//                    // moves the checkboxes without recreating them so their state is preserved
//                } else {
//                    int i =0;
//                    for (CheckBox checkBox : checkBoxList.values()) {
//                        checkBox.setPosition(starterPoint.plus(0, i * 10));
//                        i++;
//                    }
//                }
//            }
//            this.totalsize = checkBoxList.size() * 15;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (checkBoxList.isEmpty()) {
            for (String condition: Config.getConditions()) {
                CheckBox checkbox = new CheckBox(checkBoxList.size(), starterPoint.plus(0, checkBoxList.size() * 10), condition, false);
                checkbox.setOnPressed(press-> toggleCheckboxes(checkbox.getId()));
                checkBoxList.put(condition, checkbox);
            }
            // moves the checkboxes without recreating them so their state is preserved
        } else {
            int i =0;
            for (CheckBox checkBox : checkBoxList.values()) {
                checkBox.setPosition(starterPoint.plus(0, i * 10));
                i++;
            }
        }
    }

    void toggleCheckboxes(int id) {
        for (CheckBox checkbox : checkBoxList.values()) {
            if(checkbox.getId() == id) {
                continue;
            } else {
                checkbox.setChecked(false);
            }
        }
    }

    /**
     * Note that the conditions aren't setup for multiple conditions at this time
     */
    public JsonArray getJson() {
        JsonArray array = new JsonArray();
        for (String label : checkBoxList.keySet()) {
            CheckBox box = checkBoxList.get(label);
            if (box.isChecked()) {
                JsonObject condition = new JsonObject();
                condition.addProperty("type", "modularpowerarmor:conditional");
                condition.addProperty("condition", label);
                array.add(condition);
            }
        }
        return array;
    }

    @Override
    protected double getScrollAmount() {
        return 1;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.isEnabled() && this.isVisible()) {
            this.currentscrollpixels = Math.min(currentscrollpixels, getMaxScrollPixels());
            super.preRender(mouseX, mouseY, partialTicks);
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -currentscrollpixels, 0);
            for (CheckBox checkBox : checkBoxList.values()) {
                checkBox.render(mouseX, mouseY, partialTicks);
            }
            GL11.glPopMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isEnabled() && this.isVisible()) {
            super.mouseClicked(mouseX, mouseY, button);

            for (CheckBox checkBox : checkBoxList.values()) {
                if (checkBox.mouseClicked(mouseX, mouseY + currentscrollpixels, button)) {
                    return true;
                }
            }
        }
        return false;
    }
}