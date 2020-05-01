package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;

public class StackTextDisplayFrame extends ScrollableFrame {
    String label = "";
    int slot = -1;

    public StackTextDisplayFrame(float zLevel) {
        super(new Point2F(0, 0), new Point2F(0, 0), zLevel, Colour.BLACK, Colour.LIGHTBLUE.withAlpha(0.8F));
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setLabel(String labelIn) {
        label = labelIn;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        Renderer.drawLeftAlignedStringString(slot != -1 ? "Slot " + slot + ": " +
                this.label : "No slot selected", this.getBorder().finalLeft() + 4, this.getBorder().center().getY() - 4.0D);
    }
}