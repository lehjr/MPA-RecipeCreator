package com.github.lehjr.mparecipecreator.client.gui;

import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.mojang.blaze3d.systems.RenderSystem;

public class RecipeDisplayFrame extends ScrollableFrame {
    String[] recipe = new String[0];
    String title;;

    public RecipeDisplayFrame(Point2F topleft, Point2F bottomright, float zLevel, Colour backgroundColour, Colour borderColour) {
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
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.isEnabled() && this.isVisible()) {
            this.currentscrollpixels = Math.min(currentscrollpixels, getMaxScrollPixels());
            super.preRender(mouseX, mouseY, partialTicks);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0, -currentscrollpixels, 0);
            Renderer.drawLeftAlignedStringString("FileName: " + title,
                    this.getBorder().finalLeft() + 4,
                    this.getBorder().finalTop() + 4);

            if (recipe.length > 0) {
                for (int index = 0; index < recipe.length; index ++) {
                    Renderer.drawLeftAlignedStringString(recipe[index],
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