package com.github.lehjr.mpsrecipecreator.client.gui;


import com.github.lehjr.numina.util.client.gui.frame.ScrollableFrame;
import com.github.lehjr.numina.util.client.gui.gemoetry.MusePoint2D;
import com.github.lehjr.numina.util.client.render.MuseRenderer;
import com.github.lehjr.numina.util.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;

public class StackTextDisplayFrame extends ScrollableFrame {
    String label = "";
    int slot = -1;

    public StackTextDisplayFrame(float zLevel) {
        super(new MusePoint2D(0, 0), new MusePoint2D(0, 0), zLevel, Colour.BLACK, Colour.LIGHT_BLUE.withAlpha(0.8F));
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setLabel(String labelIn) {
        label = labelIn;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        MuseRenderer.drawLeftAlignedStringString(matrixStack, slot != -1 ? "Slot " + slot + ": " +
                this.label : "No slot selected", this.getBorder().finalLeft() + 4, this.getBorder().center().getY() - 4.0D);
    }
}