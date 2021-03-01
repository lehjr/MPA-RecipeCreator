package com.github.lehjr.mpsrecipecreator.client.gui;

import com.github.lehjr.numina.util.client.gui.frame.ScrollableFrame;
import com.github.lehjr.numina.util.client.gui.gemoetry.MusePoint2D;
import com.github.lehjr.numina.util.client.render.MuseRenderer;
import com.github.lehjr.numina.util.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

public class RecipeDisplayFrame extends ScrollableFrame {
    String[] recipe = new String[0];
    String title;;

    public RecipeDisplayFrame(MusePoint2D topleft, MusePoint2D bottomright, float zLevel, Colour backgroundColour, Colour borderColour) {
        super(topleft, bottomright, zLevel, backgroundColour, borderColour);
        reset();
    }

    public void setFileName(String fileName) {
        title = fileName;
    }

    public void setRecipe(String recipeIn) {
        recipe = recipeIn.split("\n");
    }

    @Override
    public void update(double mouseX, double mouseY) {
        super.update(mouseX, mouseY);
        this.totalsize = 25 + recipe.length * 12;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.isEnabled() && this.isVisible()) {
            this.currentscrollpixels = Math.min(currentscrollpixels, getMaxScrollPixels());
            super.preRender(matrixStack, mouseX, mouseY, partialTicks);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0, -currentscrollpixels, 0);
            MuseRenderer.drawLeftAlignedStringString(matrixStack, "FileName: " + title,
                    this.getBorder().finalLeft() + 4,
                    this.getBorder().finalTop() + 4);

            if (recipe.length > 0) {
                for (int index = 0; index < recipe.length; index ++) {
                    MuseRenderer.drawLeftAlignedStringString(matrixStack, recipe[index],
                            this.getBorder().finalLeft() + 4,
                            this.getBorder().finalTop() + 15 + 12 * index);
                }
            }
            RenderSystem.popMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }

    public void reset() {
        recipe = new String[0];
        setFileName("");
    }
}