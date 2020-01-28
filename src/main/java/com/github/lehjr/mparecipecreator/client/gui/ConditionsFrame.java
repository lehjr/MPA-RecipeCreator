package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.clickable.CheckBox;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.math.Colour;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lehjr
 */
public class ConditionsFrame extends ScrollableFrame {
    Map<String, CheckBox> checkBoxList = new HashMap<>();

    public ConditionsFrame(Point2D topleft, Point2D bottomright, Colour backgroundColour, Colour borderColour) {
        super(topleft, bottomright, backgroundColour, borderColour);
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
        loadConditions();
    }

    public void loadConditions() {
        Point2D starterPoint = this.getULFinal().copy().plus(4, 4);

        try {
            if (checkBoxList.isEmpty()) {
                IResource iresource = Minecraft.getMinecraft().getResourceManager()
                        .getResource(new ResourceLocation("modularpowerarmor", "recipes/_factories.json"));

                Reader reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
                JSONObject jsonObject = new JSONObject(IOUtils.toString(reader));
                JSONObject jobject = jsonObject.getJSONObject("conditions");

                for (String key : jobject.keySet()) {
                    CheckBox checkbox = new CheckBox(checkBoxList.size(), starterPoint.plus(0, checkBoxList.size() * 10), key, false);
                    checkbox.setOnPressed(press-> toggleCheckboxes(checkbox.id()));
                    checkBoxList.put(key, checkbox);
                }
                // moves the checkboxes without recreating them so their state is preserved
            } else {
                int i =0;
                for (CheckBox checkBox : checkBoxList.values()) {
                    checkBox.setPosition(starterPoint.plus(0, i * 10));
                    i++;
                }
            }

            this.totalsize = checkBoxList.size() * 15;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void toggleCheckboxes(int id) {
        for (CheckBox checkbox : checkBoxList.values()) {
            if(checkbox.id() == id) {
                continue;
            } else {
                checkbox.setChecked(false);
            }
        }
    }

    /**
     * Note that the conditions aren't setup for multiple conditions at this time
     */
    public JsonObject getJson() {
        JsonArray array = new JsonArray();

        for (String label : checkBoxList.keySet()) {
            CheckBox box = checkBoxList.get(label);

            if (box.isChecked()) {
                JsonObject condition = new JsonObject();
                condition.addProperty("type", label);
                array.add(condition);
            }
        }

        JsonObject retObject = new JsonObject();
        retObject.add("conditions", array);

        return retObject;
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
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (this.isEnabled() && this.isVisible()) {
            super.onMouseDown(mouseX, mouseY, button);

            for (CheckBox checkBox : checkBoxList.values()) {
                if (checkBox.mouseClicked(mouseX, mouseY + currentscrollpixels, button)) {
                    return true;
                }
            }
        }
        return false;
    }
}